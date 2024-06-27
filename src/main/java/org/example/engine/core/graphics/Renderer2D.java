package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.memory.MemoryResourceHolder;
import org.example.engine.core.shape.Shape2DPolygon;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: to make it a standalone, make it initialize the opengl context itself, in case it is not initialized. Maybe.
// TODO: fix rendering bug.
// TODO: overhaul, rename some methods, give option to render functions using lines.
// TODO: instead of taking shaders as arguments, deploy useShader()
// TODO:
/*
instead of:
int startVertex = this.vertexIndex / VERTEX_SIZE;
vertexIndex += vertices * VERTEX_SIZE;

do:
int startVertex = this.vertexIndex;
vertexIndex += vertices;
 */
public class Renderer2D implements MemoryResourceHolder {

    // constants
    private static final int VERTEX_SIZE       = 5;    // A vertex is composed of 5 floats: x,y: position, t: color (as float bits) and u,v: texture coordinates.
    private static final int VERTICES_CAPACITY = 6000; // The batch can render VERTICES_CAPACITY vertices (so wee need a float buffer of size: VERTICES_CAPACITY * VERTEX_SIZE)
    private static final int INDICES_CAPACITY  = VERTICES_CAPACITY * 2; // TODO

    // buffers
    private final int         vao;
    private final int         vbo;
    private final int         ebo;
    private final IntBuffer   indicesBuffer  = BufferUtils.createIntBuffer(INDICES_CAPACITY * 3);
    private final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * VERTEX_SIZE);

    // defaults
    private final ShaderProgram defaultShader = createDefaultShaderProgram();
    private final Texture       whitePixel    = createWhiteSinglePixelTexture();
    private final Camera        defaultCamera = createDefaultCamera();

    // memory pools
    private final MemoryPool<Vector2> vector2MemoryPool = new MemoryPool<>(Vector2.class, 10);

    // caches
    private final float WHITE_TINT = Color.WHITE.toFloatBits();

    // state
    private Camera        currentCamera  = null;
    private Texture       currentTexture = null;
    private ShaderProgram currentShader  = null;
    private float         currentTint    = WHITE_TINT;
    private boolean       drawing        = false;
    private int           vertexIndex    = 0;
    private int           currentMode    = GL11.GL_TRIANGLES;
    private int           drawCalls      = 0;


    public Renderer2D() {
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        {
            this.vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
            int vertexSizeBytes = VERTEX_SIZE * Float.BYTES;
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSizeBytes, Float.BYTES * 2L);
            GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, true, vertexSizeBytes, Float.BYTES * 3L);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            this.ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
        }
        GL30.glBindVertexArray(0);
    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void begin() {
        begin(null);
    }

    public void begin(Camera camera) {
        if (drawing) throw new GraphicsException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND); // TODO: make camera attributes, get as additional parameter to begin()
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // TODO: make camera attributes, get as additional parameter to begin()
        this.drawCalls = 0;
        this.currentCamera = camera != null ? camera : defaultCamera.update(GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight());
        setShader(defaultShader);
        setShaderAttributes(null);
        setTexture(whitePixel);
        setMode(GL11.GL_TRIANGLES);
        setTint(WHITE_TINT);
        this.drawing = true;
    }

    /** Push primitives: TextureRegion, Shape, Light **/

    public void pushTextureRegion(TextureRegion region, float x, float y, float angleZ, float scaleX, float scaleY) {
        pushTextureRegion(region, null, x, y, 0, 0, angleZ, scaleX, scaleY, null, null);
    }

    public void pushTextureRegion(TextureRegion region, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        pushTextureRegion(region, null, x, y, angleX, angleY, angleZ, scaleX, scaleY, null, null);
    }

    // TODO: fix.
    // TODO: make sure we apply scaling first, then rotation, then translation.
    public void pushTextureRegion(TextureRegion region, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 20 > VERTICES_CAPACITY * 4) renderCurrentBatch();

        final Texture texture = region.texture;
        final float ui = region.u;
        final float vi = region.v;
        final float uf = region.u2;
        final float vf = region.v2;
        final float offsetX = region.offsetX;
        final float offsetY = region.offsetY;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        // TODO: make sure we apply scaling first, then rotation, then translation.
        if (angleX != 0.0f) scaleX *= MathUtils.cosDeg(angleX);
        if (angleY != 0.0f) scaleY *= MathUtils.cosDeg(angleY);

        setShader(shader);
        useTexture_old(texture);
        useCustomAttributes_old(customAttributes);
        useMode_old(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex + 1)
                .put(startVertex + 2)
        ;

        // put vertices
        float localX1, localY1;
        float localX2, localY2;
        float localX3, localY3;
        float localX4, localY4;

        localX1 = localX2 = offsetX - originalWidthHalf;
        localX3 = localX4 = offsetX - originalWidthHalf + packedWidth;
        localY1 = localY4 = offsetY - originalHeightHalf + packedHeight;
        localY2 = localY3 = offsetY - originalHeightHalf;

        if (scaleX != 1.0f) {
            localX1 *= scaleX;
            localX2 *= scaleX;
            localX3 *= scaleX;
            localX4 *= scaleX;
        }
        if (scaleY != 1.0f) {
            localY1 *= scaleY;
            localY2 *= scaleY;
            localY3 *= scaleY;
            localY4 *= scaleY;
        }

        float x1, y1;
        float x2, y2;
        float x3, y3;
        float x4, y4;

        if (angleZ != 0.0f) {
            final float sin = MathUtils.sinDeg(angleZ);
            final float cos = MathUtils.cosDeg(angleZ);
            x1 = localX1 * cos - localY1 * sin;
            y1 = localX1 * sin + localY1 * cos;

            x2 = localX2 * cos - localY2 * sin;
            y2 = localX2 * sin + localY2 * cos;

            x3 = localX3 * cos - localY3 * sin;
            y3 = localX3 * sin + localY3 * cos;

            x4 = localX4 * cos - localY4 * sin;
            y4 = localX4 * sin + localY4 * cos;
        } else {
            x1 = localX1;
            y1 = localY1;

            x2 = localX2;
            y2 = localY2;

            x3 = localX3;
            y3 = localY3;

            x4 = localX4;
            y4 = localY4;
        }

        x1 += x;
        y1 += y;

        x2 += x;
        y2 += y;

        x3 += x;
        y3 += y;

        x4 += x;
        y4 += y;

        float t = tint == null ? WHITE_TINT : tint.toFloatBits();
        verticesBuffer
                .put(x1).put(y1).put(t).put(ui).put(vi) // V1
                .put(x2).put(y2).put(t).put(ui).put(vf) // V2
                .put(x3).put(y3).put(t).put(uf).put(vf) // V3
                .put(x4).put(y4).put(t).put(uf).put(vi) // V4
        ;
        vertexIndex += 20;
    }

    /* State */

    public void setShader(ShaderProgram shader) {
        if (shader == null) shader = defaultShader;
        if (currentShader != shader) {
            renderCurrentBatch();
            ShaderProgramBinder.bind(shader);
            shader.bindUniform("u_camera_combined", currentCamera.lens.combined);
            shader.bindUniform("u_texture", currentTexture);
        }
        currentShader = shader;
    }

    public void setTexture(Texture texture) {
        if (texture == null) texture = whitePixel;
        if (currentTexture != texture) renderCurrentBatch();
        currentTexture = texture;
        currentShader.bindUniform("u_texture", currentTexture);
    }

    public void setShaderAttributes(HashMap<String, Object> customAttributes) {
        if (customAttributes != null) {
            renderCurrentBatch();
            currentShader.bindUniforms(customAttributes);
        }
    }

    private void setMode(final int mode) {
        if (mode != this.currentMode) renderCurrentBatch();
        this.currentMode = mode;
    }

    public void setTint(final Color color) {
        setTint(color.toFloatBits());
    }

    public void setTint(float tintFloatBits) {
        this.currentTint = tintFloatBits;
    }

    /* Rendering API */

    /* Rendering 2D primitives - Circles */

    public void drawCircleThin(float r, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex * VERTEX_SIZE + refinement * 5 > VERTICES_CAPACITY * 4) renderCurrentBatch(); // TODO: use floatBuffer.capacity()
        setMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 1; i < refinement; i++) {
            indicesBuffer.put(startVertex + i - 1);
            indicesBuffer.put(startVertex + i);
        }
        indicesBuffer.put(startVertex + refinement - 1);
        indicesBuffer.put(startVertex);

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);

        Vector2 arm = vector2MemoryPool.allocate();
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            arm.x = r * scaleX * MathUtils.cosDeg(da * i);
            arm.y = r * scaleY * MathUtils.sinDeg(da * i);
            arm.rotateDeg(angleZ);
            verticesBuffer
                    .put(x + arm.x)
                    .put(y + arm.y)
                    .put(currentTint)
                    .put(0.5f)
                    .put(0.5f)
            ;
        }
        vector2MemoryPool.free(arm);
        //vertexIndex += refinement + 1;
        vertexIndex += refinement;
    }

    public void drawCircleFilled(float r, float x, float y, float angle, int refinement, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement + 2) * VERTEX_SIZE > VERTICES_CAPACITY * 4) renderCurrentBatch(); // TODO: use floatBuffer.capacity()

        refinement = Math.max(3, refinement);
        setMode(GL11.GL_TRIANGLES);

        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement; i++) {
            indicesBuffer.put(startVertex);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
        }

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);
        Vector2 arm = vector2MemoryPool.allocate();
        float da = angle / refinement;

        // put vertices
        verticesBuffer.put(x).put(y).put(currentTint).put(0.5f).put(0.5f);
        int i = 0;
        while (i < refinement + 1) {
            arm.x = r * scaleX * MathUtils.cosDeg(da * i);
            arm.y = r * scaleY * MathUtils.sinDeg(da * i);
            arm.rotateDeg(angleZ);
            float pointX = x + arm.x;
            float pointY = y + arm.y;
            verticesBuffer.put(pointX).put(pointY).put(currentTint).put(0.5f).put(0.5f);
            i++;
        }

        vector2MemoryPool.free(arm);
        vertexIndex += refinement + 2;
    }

    public void drawCircleFilled(float r, float x, float y, int refinement, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement + 2) * VERTEX_SIZE > VERTICES_CAPACITY * 4) renderCurrentBatch(); // TODO: use floatBuffer.capacity()

        refinement = Math.max(3, refinement);
        setMode(GL11.GL_TRIANGLES);

        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement; i++) {
            indicesBuffer.put(startVertex);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
        }
        indicesBuffer.put(startVertex);
        indicesBuffer.put(startVertex + refinement + 1);
        indicesBuffer.put(startVertex + 1);

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);
        Vector2 arm = vector2MemoryPool.allocate();
        float da = 360f / refinement;

        // put vertices
        verticesBuffer.put(x).put(y).put(currentTint).put(0.5f).put(0.5f);
        int i = 0;
        while (i < refinement + 1) {
            arm.x = r * scaleX * MathUtils.cosDeg(da * i);
            arm.y = r * scaleY * MathUtils.sinDeg(da * i);
            arm.rotateDeg(angleZ);
            float pointX = x + arm.x;
            float pointY = y + arm.y;
            verticesBuffer.put(pointX).put(pointY).put(currentTint).put(0.5f).put(0.5f);
            i++;
        }

        vector2MemoryPool.free(arm);
        vertexIndex += refinement + 2;
    }

    public void drawCircleBorder(float r, float thickness, float angle, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();

        refinement = Math.max(3, refinement);
        setMode(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < (refinement - 1) * 2; i += 2) { // 012 213
            indicesBuffer.put(startVertex + i + 0);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);

        Vector2 arm0 = vector2MemoryPool.allocate();
        Vector2 arm1 = vector2MemoryPool.allocate();

        float da = angle / refinement;
        float halfBorder = thickness * 0.5f;
        // render arc segments.
        for (int i = 0; i < refinement; i++) {
            float currentAngle = da * i;

            arm0.x = scaleX * (r - halfBorder) * MathUtils.cosDeg(currentAngle);
            arm0.y = scaleY * (r - halfBorder) * MathUtils.sinDeg(currentAngle);
            arm0.rotateDeg(angleZ);

            arm1.x = scaleX * (r + halfBorder) * (MathUtils.cosDeg(currentAngle));
            arm1.y = scaleY * (r + halfBorder) * (MathUtils.sinDeg(currentAngle));
            arm1.rotateDeg(angleZ);

            verticesBuffer.put(arm0.x + x).put(arm0.y + y).put(currentTint).put(0.5f).put(0.5f);
            verticesBuffer.put(arm1.x + x).put(arm1.y + y).put(currentTint).put(0.5f).put(0.5f);
        }

        vector2MemoryPool.free(arm0);
        vector2MemoryPool.free(arm1);
        vertexIndex += refinement * 2;
    }

    public void drawCircleBorder(float r, float thickness, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();

        refinement = Math.max(3, refinement);
        setMode(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < (refinement - 1) * 2; i += 2) { // 012 213
            indicesBuffer.put(startVertex + i + 0);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }
        indicesBuffer.put(startVertex + refinement * 2 - 2);
        indicesBuffer.put(startVertex + refinement * 2 - 1);
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + refinement * 2 - 1);
        indicesBuffer.put(startVertex + 1);

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);

        Vector2 arm0 = vector2MemoryPool.allocate();
        Vector2 arm1 = vector2MemoryPool.allocate();

        float da = 360f / refinement;
        float halfBorder = thickness * 0.5f;
        // render arc segments.
        for (int i = 0; i < refinement; i++) {
            float currentAngle = da * i;

            arm0.x = scaleX * (r - halfBorder) * MathUtils.cosDeg(currentAngle);
            arm0.y = scaleY * (r - halfBorder) * MathUtils.sinDeg(currentAngle);
            arm0.rotateDeg(angleZ);

            arm1.x = scaleX * (r + halfBorder) * MathUtils.cosDeg(currentAngle);
            arm1.y = scaleY * (r + halfBorder) * MathUtils.sinDeg(currentAngle);
            arm1.rotateDeg(angleZ);

            verticesBuffer.put(arm0.x + x).put(arm0.y + y).put(currentTint).put(0.5f).put(0.5f);
            verticesBuffer.put(arm1.x + x).put(arm1.y + y).put(currentTint).put(0.5f).put(0.5f);
        }

        vector2MemoryPool.free(arm0);
        vector2MemoryPool.free(arm1);
        vertexIndex += refinement * 2;
    }

    /* Rendering 2D primitives - Rectangles */

    public void drawRectangleThin(
            float x0, float y0,
            float x1, float y1,
            float x2, float y2,
            float x3, float y3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();

        setMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer
                .put(startVertex + 0)
                .put(startVertex + 1)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex + 0)
        ;

        verticesBuffer
                .put(x0).put(y0).put(currentTint).put(0.5f).put(0.5f) // V0
                .put(x1).put(y1).put(currentTint).put(0.5f).put(0.5f) // V1
                .put(x2).put(y2).put(currentTint).put(0.5f).put(0.5f) // V2
                .put(x3).put(y3).put(currentTint).put(0.5f).put(0.5f) // V3
        ;

        vertexIndex += 4;
    }

    public void drawRectangleThin(float width, float height,
            float x, float y,
            float angleX, float angleY, float angleZ,
            float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();

        setMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer
                .put(startVertex + 0)
                .put(startVertex + 1)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex + 0)
        ;

        float widthHalf  = width  * scaleX * MathUtils.cosDeg(angleY) * 0.5f;
        float heightHalf = height * scaleY * MathUtils.cosDeg(angleX) * 0.5f;

        Vector2 arm0 = vector2MemoryPool.allocate();
        Vector2 arm1 = vector2MemoryPool.allocate();
        Vector2 arm2 = vector2MemoryPool.allocate();
        Vector2 arm3 = vector2MemoryPool.allocate();

        arm0.x = -widthHalf;
        arm0.y = heightHalf;
        arm0.rotateDeg(angleZ);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(angleZ);

        arm2.x = widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(angleZ);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(angleZ);

        verticesBuffer
                .put(arm0.x + x).put(arm0.y + y).put(currentTint).put(0.5f).put(0.5f) // V0
                .put(arm1.x + x).put(arm1.y + y).put(currentTint).put(0.5f).put(0.5f) // V1
                .put(arm2.x + x).put(arm2.y + y).put(currentTint).put(0.5f).put(0.5f) // V2
                .put(arm3.x + x).put(arm3.y + y).put(currentTint).put(0.5f).put(0.5f) // V3
        ;

        vector2MemoryPool.free(arm0);
        vector2MemoryPool.free(arm1);
        vector2MemoryPool.free(arm2);
        vector2MemoryPool.free(arm3);

        vertexIndex += 4;
    }

    public void drawRectangleFilled(
            float x0, float y0,
            float x1, float y1,
            float x2, float y2,
            float x3, float y3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();

        setMode(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer
                .put(startVertex + 0)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 0)
        ;

        verticesBuffer
                .put(x0).put(y0).put(currentTint).put(0.5f).put(0.5f) // V0
                .put(x1).put(y1).put(currentTint).put(0.5f).put(0.5f) // V1
                .put(x2).put(y2).put(currentTint).put(0.5f).put(0.5f) // V2
                .put(x3).put(y3).put(currentTint).put(0.5f).put(0.5f) // V3
        ;

        vertexIndex += 4;
    }

    public void drawRectangleFilled(float width, float height,
                                  float x, float y,
                                  float angleX, float angleY, float angleZ,
                                  float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();

        setMode(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer
                .put(startVertex + 0)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 0)
        ;

        float widthHalf  = width  * scaleX * MathUtils.cosDeg(angleY) * 0.5f;
        float heightHalf = height * scaleY * MathUtils.cosDeg(angleX) * 0.5f;

        Vector2 arm0 = vector2MemoryPool.allocate();
        Vector2 arm1 = vector2MemoryPool.allocate();
        Vector2 arm2 = vector2MemoryPool.allocate();
        Vector2 arm3 = vector2MemoryPool.allocate();

        arm0.x = -widthHalf;
        arm0.y =  heightHalf;
        arm0.rotateDeg(angleZ);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(angleZ);

        arm2.x =  widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(angleZ);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(angleZ);

        verticesBuffer
                .put(arm0.x + x).put(arm0.y + y).put(currentTint).put(0.5f).put(0.5f) // V0
                .put(arm1.x + x).put(arm1.y + y).put(currentTint).put(0.5f).put(0.5f) // V1
                .put(arm2.x + x).put(arm2.y + y).put(currentTint).put(0.5f).put(0.5f) // V2
                .put(arm3.x + x).put(arm3.y + y).put(currentTint).put(0.5f).put(0.5f) // V3
        ;

        vector2MemoryPool.free(arm0);
        vector2MemoryPool.free(arm1);
        vector2MemoryPool.free(arm2);
        vector2MemoryPool.free(arm3);

        vertexIndex += 4;
    }

    /**
     * Renders a rectangle with rounded corners.
     *
     * <p> This adds a single vertex at the center of the rectangle,
     * Then traces the rest of the vertices using the radius and the
     * refinement of the corners. The rendered triangles are casted
     * from the center x,y into the edge vertices.
     * </p>
     *
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param r the corner radius, given in world units, not pixels
     * @param refinement how smooth the corners are, minimum value is 3
     * @param x the x value of the center of the rectangle
     * @param y the y value of the center of the rectangle
     * @param angleX the angle around the x-axis
     * @param angleY the angle around the y-axis
     * @param angleZ the angle around the z-axis
     * @param scaleX the scale around the x-axis (before transform is applied)
     * @param scaleY the scale around the y-axis (before transform is applied)
     */
    public void drawRectangleFilled(float width, float height, float r, int refinement,
                                    float x, float y,
                                    float angleX, float angleY, float angleZ,
                                    float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) renderCurrentBatch();
        refinement = Math.max(3, refinement);

        setMode(GL11.GL_TRIANGLES);

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / refinement;

        // we store the vertices in this array and apply the transform after, then put them in the buffer
        Array<Vector2> vertices = new Array<>(true, 1 + refinement * 4);

        // add center vertex
        Vector2 center = vector2MemoryPool.allocate().set(0, 0);
        vertices.add(center); // center vertex

        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vector2MemoryPool.allocate();
            corner.set(-r, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + r, heightHalf - r);
            vertices.add(corner);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vector2MemoryPool.allocate();
            corner.set(0, r);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - r, heightHalf - r);
            vertices.add(corner);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vector2MemoryPool.allocate();
            corner.set(r, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - r, -heightHalf + r);
            vertices.add(corner);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vector2MemoryPool.allocate();
            corner.set(0, -r);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + r, -heightHalf + r);
            vertices.add(corner);
        }

        // transform each vertex, then put it in the buffer + tint + uv
        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);
        for (int i = 0; i < vertices.size; i++) {
            Vector2 vertex = vertices.get(i).scl(scaleX, scaleY).rotateDeg(angleZ).add(x, y);
            // TODO: use proper UV.
            verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = this.vertexIndex;
        // upper left corner
        for (int i = 0; i < refinement - 1; i++) {
            indicesBuffer.put(startVertex + 0);
            indicesBuffer.put(startVertex + refinement * 0 + i + 1);
            indicesBuffer.put(startVertex + refinement * 0 + i + 2);
        }
        // upper triangle
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + refinement * 1 + 0);
        indicesBuffer.put(startVertex + refinement * 1 + 1);
        // upper right corner
        for (int i = 0; i < refinement - 1; i++) {
            indicesBuffer.put(startVertex + 0);
            indicesBuffer.put(startVertex + refinement * 1 + i + 1);
            indicesBuffer.put(startVertex + refinement * 1 + i + 2);
        }
        // right triangle
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + refinement * 2 + 0);
        indicesBuffer.put(startVertex + refinement * 2 + 1);
        // lower right corner
        for (int i = 0; i < refinement - 1; i++) {
            indicesBuffer.put(startVertex + 0);
            indicesBuffer.put(startVertex + refinement * 2 + i + 1);
            indicesBuffer.put(startVertex + refinement * 2 + i + 2);
        }
        // bottom triangle
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + refinement * 3 + 0);
        indicesBuffer.put(startVertex + refinement * 3 + 1);
        // lower left corner
        for (int i = 0; i < refinement - 1; i++) {
            indicesBuffer.put(startVertex + 0);
            indicesBuffer.put(startVertex + refinement * 3 + i + 1);
            indicesBuffer.put(startVertex + refinement * 3 + i + 2);
        }
        // right triangle
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + refinement * 4 + 0);
        indicesBuffer.put(startVertex + refinement * 0 + 1);

        vector2MemoryPool.freeAll(vertices);
        vertexIndex += 1 + refinement * 4;
    }

    // TODO: delete. Break down the drawing operations to explicit pushCircleFilled, pushCircleHollow, pushRectangleFilled, pushRectangleHollow, pushCurve
    @Deprecated public void pushPolygon(final Shape2DPolygon polygon, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + polygon.vertices.length > VERTICES_CAPACITY * 4) {
            renderCurrentBatch();
        }
        setShader(shader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(customAttributes);
        useMode_old(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < polygon.indices.length; i++) {
            indicesBuffer.put(startVertex + polygon.indices[i]);
        }

        if (angleX != 0.0f) scaleX *= MathUtils.cosDeg(angleX);
        if (angleY != 0.0f) scaleY *= MathUtils.cosDeg(angleY);

        polygon.setTransform(x, y, angleZ, scaleX, scaleY);
        polygon.update();

        float t = tint == null ? WHITE_TINT : tint.toFloatBits();

        final Array<Vector2> worldVertices = polygon.worldVertices();

        for (Vector2 vertex : worldVertices) {
            verticesBuffer.put(vertex.x).put(vertex.y).put(t).put(0.5f).put(0.5f);
        }

        vertexIndex += polygon.vertexCount * 5;
    }


    // TODO: add refinement argument
    @Deprecated public void pushThinCircle(final float r, final float centerX, final float centerY, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 15 * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand sides are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        setShader(defaultShader);
        useMode_old(GL11.GL_LINES);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 1; i < 15; i++) {
            indicesBuffer.put(startVertex + i - 1);
            indicesBuffer.put(startVertex + i);
        }
        indicesBuffer.put(startVertex + 14);
        indicesBuffer.put(startVertex);
        indicesBuffer.put(startVertex + 15);
        indicesBuffer.put(startVertex + 16);

        float da = 360f / 15;
        for (int i = 0; i < 15; i++) {
            verticesBuffer
                    .put(centerX + r * MathUtils.cosDeg(da * i))
                    .put(centerY + r * MathUtils.sinDeg(da * i))
                    .put(tintFloatBits)
                    .put(0.5f)
                    .put(0.5f)
            ;
        }
        vertexIndex += 15 * 5;
    }


    @Deprecated public void pushThinRectangle_old(
            float x0, float y0,
            float x1, float y1,
            float x2, float y2,
            float x3, float y3, final Color color) {
        pushThinRectangle_old(x0, y0, x1, y1, x2, y2, x3, y3, color.toFloatBits());
    }

    @Deprecated public void pushThinRectangle_old(
            float x0, float y0,
            float x1, float y1,
            float x2, float y2,
            float x3, float y3, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 6 * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        // TODO: why the fuck do I put the same vertex twice? Bug?
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex)
        ;
        indicesBuffer.put(startVertex + 4);
        indicesBuffer.put(startVertex + 5);

        verticesBuffer
                .put(x0).put(y0).put(tintFloatBits).put(0.5f).put(0.5f) // V1
                .put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f) // V2
                .put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f) // V3
                .put(x3).put(y3).put(tintFloatBits).put(0.5f).put(0.5f) // V4
        ;

        vertexIndex += 4 * 5;
    }

    public void pushFilledRectangle(float width, float height, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 4 * VERTEX_SIZE > VERTICES_CAPACITY * 4) renderCurrentBatch();

        // TODO: make sure we apply scaling first, then rotation, then translation.
        if (angleX != 0.0f) scaleX *= MathUtils.cosDeg(angleX);
        if (angleY != 0.0f) scaleY *= MathUtils.cosDeg(angleY);

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex + 1)
                .put(startVertex + 2)
        ;

        // put vertices
        float localX1, localY1;
        float localX2, localY2;
        float localX3, localY3;
        float localX4, localY4;

        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        localX1 = localX2 =  -halfWidth;
        localX3 = localX4 =  +halfWidth;
        localY1 = localY4 =  -halfHeight;
        localY2 = localY3 =  +halfHeight;

        if (scaleX != 1.0f) {
            localX1 *= scaleX;
            localX2 *= scaleX;
            localX3 *= scaleX;
            localX4 *= scaleX;
        }
        if (scaleY != 1.0f) {
            localY1 *= scaleY;
            localY2 *= scaleY;
            localY3 *= scaleY;
            localY4 *= scaleY;
        }

        float x1, y1;
        float x2, y2;
        float x3, y3;
        float x4, y4;

        if (angleZ != 0.0f) {
            final float sin = MathUtils.sinDeg(angleZ);
            final float cos = MathUtils.cosDeg(angleZ);
            x1 = localX1 * cos - localY1 * sin;
            y1 = localX1 * sin + localY1 * cos;

            x2 = localX2 * cos - localY2 * sin;
            y2 = localX2 * sin + localY2 * cos;

            x3 = localX3 * cos - localY3 * sin;
            y3 = localX3 * sin + localY3 * cos;

            x4 = localX4 * cos - localY4 * sin;
            y4 = localX4 * sin + localY4 * cos;
        } else {
            x1 = localX1;
            y1 = localY1;

            x2 = localX2;
            y2 = localY2;

            x3 = localX3;
            y3 = localY3;

            x4 = localX4;
            y4 = localY4;
        }

        x1 += x;
        y1 += y;

        x2 += x;
        y2 += y;

        x3 += x;
        y3 += y;

        x4 += x;
        y4 += y;

        verticesBuffer
                .put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f) // V1
                .put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f) // V2
                .put(x3).put(y3).put(tintFloatBits).put(0.5f).put(0.5f) // V3
                .put(x4).put(y4).put(tintFloatBits).put(0.5f).put(0.5f) // V4
        ;
        vertexIndex += 4 * VERTEX_SIZE; // TODO: update vertex index correctly.
    }

    public void pushThinLineSegment(float x1, float y1, float x2, float y2, final Color color) {
        pushThinLineSegment(x1, y1, x2, y2, color.toFloatBits());
    }

    public void pushThinLineSegment(float x1, float y1, float x2, float y2, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 2 * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1);

        verticesBuffer
                .put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f) // a
                .put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f) // b
        ;
        vertexIndex += 2 * 5;
    }

    public void pushFilledLineSegment(float x1, float y1, float x2, float y2, float stroke, final float tintFloatBits) {
        // we simply draw a rectangle with center: ((x1 + x2) / 2, (y1 + y2) / 2), width: segment length and height: stroke
        // and angle: slope
        float centerX = (x1 + x2) * 0.5f;
        float centerY = (y1 + y2) * 0.5f;
        float angleZ = MathUtils.atanDeg((y2 - y1) / (x2 - x1));
        float width = Vector2.dst(x1, y1, x2, y2);
        float height = stroke;
        pushFilledRectangle(width, height, centerX, centerY, 0,0, angleZ, 1, 1, tintFloatBits);
    }

    public void pushThinCurve(Function<Float, Float> f, float minX, float maxX, int refinement, float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (refinement < 2) throw new GraphicsException("refinement must be at least 2 for curve rendering. Got: " + refinement);
        if (vertexIndex + refinement * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_LINES);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < refinement - 1; i++) {
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
        }

        for (int i = 0; i < refinement; i++) {
            float currentX = minX + step * i;
            float currentY = f.apply(currentX);
            verticesBuffer.put(currentX).put(currentY).put(tintFloatBits).put(0.5f).put(0.5f);
        }

        vertexIndex += refinement * 5;
    }

    public void pushThinCurve(final Vector2[] values, float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values.length < 2) throw new GraphicsException("values must contain at least 2 points. Got: " + values.length);
        if (vertexIndex + values.length * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_LINES);

        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < values.length - 1; i++) {
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
        }

        for (Vector2 value : values) {
            verticesBuffer.put(value.x).put(value.y).put(tintFloatBits).put(0.5f).put(0.5f);
        }

        vertexIndex += values.length * 5;
    }

    public void pushCurve(Function<Float, Float> f, float minX, float maxX, int refinement, float stroke, float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (refinement < 2) throw new GraphicsException("refinement must be at least 2 for curve rendering. Got: " + refinement);
        if (vertexIndex + refinement * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        stroke = Math.abs(stroke);

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_TRIANGLES);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < refinement * 2 - 2; i += 2) {
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        for (int i = 0; i < refinement; i++) {
            float currentX = minX + step * i;
            float currentY = f.apply(currentX);

            float nextX = minX + step * (i + 1);
            float nextY = f.apply(nextX);

            Vector2 strokeVec = vector2MemoryPool.allocate();
            strokeVec.set(nextX - currentX, nextY - currentY);
            strokeVec.nor();
            strokeVec.rotate90(1);
            strokeVec.scl(stroke * 0.5f);

            float x1 = currentX + strokeVec.x;
            float y1 = currentY + strokeVec.y;

            float x2 = currentX - strokeVec.x;
            float y2 = currentY - strokeVec.y;

            verticesBuffer.put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f);
            verticesBuffer.put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f);

            vector2MemoryPool.free(strokeVec);
        }

        vertexIndex += 6 * refinement * 5;
    }

    // TODO: with and w/o triangulation + local vertices + transform.
    // TODO: use Vector2[] or float[] for the vertices.
    public void pushThinPolygon(final Array<Vector2> worldVertices, final int[] indices, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + worldVertices.size * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            renderCurrentBatch();
        }

        setShader(defaultShader);
        useTexture_old(whitePixel);
        useCustomAttributes_old(null);
        useMode_old(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < indices.length - 2; i += 3) {
            indicesBuffer.put(startVertex + indices[i]);
            indicesBuffer.put(startVertex + indices[i + 1]);

            indicesBuffer.put(startVertex + indices[i + 1]);
            indicesBuffer.put(startVertex + indices[i + 2]);

            indicesBuffer.put(startVertex + indices[i + 2]);
            indicesBuffer.put(startVertex + indices[i]);
        }

        for (Vector2 vertex : worldVertices) {
            verticesBuffer.put(vertex.x).put(vertex.y).put(tintFloatBits).put(0.5f).put(0.5f);
        }

        vertexIndex += worldVertices.size * 5;
    }

    /* Font Rendering API */
    // LWJGL example:
    // https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Truetype.java
    public void pushText(final String text, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float tintFloatBits) {

    }

    /** Swap Operations **/
    // TODO: refactor into public.
    private void useShader_old(ShaderProgram shader) {
        if (shader == null) shader = defaultShader;
        if (currentShader != shader) {
            renderCurrentBatch();
            ShaderProgramBinder.bind(shader);
            shader.bindUniform("u_camera_combined", currentCamera.lens.combined);
        }
        currentShader = shader;
    }

    private void useTexture_old(Texture texture) {
        if (currentTexture != texture) {
            renderCurrentBatch();
        }
        currentTexture = texture;
        currentShader.bindUniform("u_texture", currentTexture);
    }

    // TODO: unify with shader switching?
    @Deprecated private void useCustomAttributes_old(HashMap<String, Object> customAttributes) {
        if (customAttributes != null) {
            renderCurrentBatch();
            currentShader.bindUniforms(customAttributes);
        }
    }

    private void useMode_old(final int mode) {
        if (mode != this.currentMode) {
            renderCurrentBatch();
        }
        this.currentMode = mode;
    }

    // contains the logic that sends everything to the GPU for rendering
    private void renderCurrentBatch() {
        if (verticesBuffer.position() == 0) return;

        verticesBuffer.flip();
        indicesBuffer.flip();
        GL30.glBindVertexArray(vao);
        {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indicesBuffer);

            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL11.glDrawElements(currentMode, indicesBuffer.limit(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
        }
        GL30.glBindVertexArray(0);

        verticesBuffer.clear();
        indicesBuffer.clear();
        vertexIndex = 0;
        drawCalls++;
    }

    public void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
        renderCurrentBatch();
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        currentCamera = null;
        currentShader = null;
        drawing = false;
    }

    public int getDrawCalls() {
        return drawCalls;
    }

    @Override
    public void deleteAll() {
        defaultShader.delete();
        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(vbo);
        GL30.glDeleteBuffers(ebo);
        whitePixel.delete();
    }

    private static ShaderProgram createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new ShaderProgram(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources: ");
            e.printStackTrace();
            String vertexShader = "#version 450\n" +
                    "\n" +
                    "// attributes\n" +
                    "layout(location = 0) in vec2 a_position;\n" +
                    "layout(location = 1) in vec4 a_color;\n" +
                    "layout(location = 2) in vec2 a_texCoord0;\n" +
                    "\n" +
                    "// uniforms\n" +
                    "uniform mat4 u_camera_combined;\n" +
                    "\n" +
                    "// outputs\n" +
                    "out vec4 color;\n" +
                    "out vec2 uv;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    color = a_color;\n" +
                    "    uv = a_texCoord0;\n" +
                    "    gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);\n" +
                    "};";

            String fragmentShader = "#version 450\n" +
                    "\n" +
                    "// inputs\n" +
                    "in vec4 color;\n" +
                    "in vec2 uv;\n" +
                    "\n" +
                    "// uniforms\n" +
                    "uniform sampler2D u_texture;\n" +
                    "\n" +
                    "// outputs\n" +
                    "layout (location = 0) out vec4 out_color;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    out_color = color * texture(u_texture, uv);\n" +
                    "}";

            return new ShaderProgram(vertexShader, fragmentShader);
        }
    }

    private static Texture createWhiteSinglePixelTexture() {
        try {
            return TextureBuilder.buildFromClassPath("graphics-2d-single-white-pixel.png");
        } catch (Exception e) {
            System.err.println("Could not create single white pixel texture from resource. Creating manually.");
            e.printStackTrace();

            ByteBuffer buffer = ByteBuffer.allocateDirect(4);
            buffer.put((byte) ((0xFFFFFFFF >> 16) & 0xFF));   // Red component
            buffer.put((byte) ((0xFFFFFFFF >> 8) & 0xFF));    // Green component
            buffer.put((byte) (0xFF));           // Blue component
            buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF));   // Alpha component
            buffer.flip();
            int glHandle = GL11.glGenTextures();
            Texture texture = new Texture(glHandle,
                    1, 1,
                    Texture.Filter.NEAREST, Texture.Filter.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE
            );
            TextureBinder.bind(texture);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            return texture;
        }
    }

    private static Camera createDefaultCamera() {
        return new Camera(GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight(), 1);
    }

}
