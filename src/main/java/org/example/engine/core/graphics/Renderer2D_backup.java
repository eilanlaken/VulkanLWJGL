package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.input.Keyboard;
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
public class Renderer2D_backup implements MemoryResourceHolder {

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
    private final MemoryPool<Vector2> vectorsPool = new MemoryPool<>(Vector2.class, 10);

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


    public Renderer2D_backup() {
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
        if (drawing) throw new GraphicsException("Already in a drawing state; Must call " + Renderer2D_backup.class.getSimpleName() + ".end() before calling begin().");
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

    // TODO: re-implement
    public void pushTextureRegion(TextureRegion region, float x, float y, float angleZ, float scaleX, float scaleY) {
        pushTextureRegion(region, null, x, y, 0, 0, angleZ, scaleX, scaleY, null, null);
    }

    // TODO: re-implement
    public void pushTextureRegion(TextureRegion region, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        pushTextureRegion(region, null, x, y, angleX, angleY, angleZ, scaleX, scaleY, null, null);
    }

    // TODO: re-implement
    public void pushTextureRegion(TextureRegion region, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 20 > VERTICES_CAPACITY * 4) flush();

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
            flush();
            ShaderProgramBinder.bind(shader);
            shader.bindUniform("u_camera_combined", currentCamera.lens.combined);
            shader.bindUniform("u_texture", currentTexture);
        }
        currentShader = shader;
    }

    public void setTexture(Texture texture) {
        if (texture == null) texture = whitePixel;
        if (currentTexture != texture) flush();
        currentTexture = texture;
        currentShader.bindUniform("u_texture", currentTexture);
    }

    public void setShaderAttributes(HashMap<String, Object> customAttributes) {
        if (customAttributes != null) {
            flush();
            currentShader.bindUniforms(customAttributes);
        }
    }

    private void setMode(final int mode) {
        if (mode != this.currentMode) flush();
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
        if ((vertexIndex + refinement) * VERTEX_SIZE > verticesBuffer.capacity()) flush(); // TODO: use floatBuffer.capacity()
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

        Vector2 arm = vectorsPool.allocate();
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
        vectorsPool.free(arm);

        vertexIndex += refinement;
    }

    public void drawCircleFilled(float r, float x, float y, float angle, int refinement, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement + 2) * VERTEX_SIZE > VERTICES_CAPACITY * 4) flush(); // TODO: use floatBuffer.capacity()

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
        Vector2 arm = vectorsPool.allocate();
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

        vectorsPool.free(arm);
        vertexIndex += refinement + 2;
    }

    public void drawCircleFilled(float r, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement + 2) * VERTEX_SIZE > VERTICES_CAPACITY * 4) flush(); // TODO: use floatBuffer.capacity()

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
        Vector2 arm = vectorsPool.allocate();
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

        vectorsPool.free(arm);
        vertexIndex += refinement + 2;
    }

    public void drawCircleBorder(float r, float thickness, float angle, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

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

        Vector2 arm0 = vectorsPool.allocate();
        Vector2 arm1 = vectorsPool.allocate();

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

        vectorsPool.free(arm0);
        vectorsPool.free(arm1);
        vertexIndex += refinement * 2;
    }

    public void drawCircleBorder(float r, float thickness, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

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

        Vector2 arm0 = vectorsPool.allocate();
        Vector2 arm1 = vectorsPool.allocate();

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

        vectorsPool.free(arm0);
        vectorsPool.free(arm1);
        vertexIndex += refinement * 2;
    }

    /* Rendering 2D primitives - Rectangles */

    public void drawRectangleThin(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

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

    public void drawRectangleThin(float width, float height, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

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

        Vector2 arm0 = vectorsPool.allocate();
        Vector2 arm1 = vectorsPool.allocate();
        Vector2 arm2 = vectorsPool.allocate();
        Vector2 arm3 = vectorsPool.allocate();

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

        vectorsPool.free(arm0);
        vectorsPool.free(arm1);
        vectorsPool.free(arm2);
        vectorsPool.free(arm3);

        vertexIndex += 4;
    }

    public void drawRectangleThin(float width, float height, float r, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + refinement * 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();
        refinement = Math.max(3, refinement);

        setMode(GL11.GL_LINES);

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / refinement;

        // we store the vertices in this array and apply the transform after, then put them in the buffer
        Array<Vector2> vertices = new Array<>(true, 1 + refinement * 4);

        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
            corner.set(-r, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + r, heightHalf - r);
            vertices.add(corner);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
            corner.set(0, r);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - r, heightHalf - r);
            vertices.add(corner);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
            corner.set(r, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - r, -heightHalf + r);
            vertices.add(corner);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
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
            verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = this.vertexIndex;
        // upper left corner
        indicesBuffer.put(startVertex + 0);
        for (int i = 1; i < refinement * 4; i++) {
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i);
        }
        indicesBuffer.put(startVertex + 0);

        vectorsPool.freeAll(vertices);
        vertexIndex += refinement * 4;
    }

    public void drawRectangleFilled(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

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

    public void drawRectangleFilled(float width, float height, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

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

        Vector2 arm0 = vectorsPool.allocate();
        Vector2 arm1 = vectorsPool.allocate();
        Vector2 arm2 = vectorsPool.allocate();
        Vector2 arm3 = vectorsPool.allocate();

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

        vectorsPool.free(arm0);
        vectorsPool.free(arm1);
        vectorsPool.free(arm2);
        vectorsPool.free(arm3);

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
    public void drawRectangleFilled(float width, float height, float r, int refinement, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 1 + refinement * 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();
        refinement = Math.max(3, refinement);

        setMode(GL11.GL_TRIANGLES);

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / refinement;

        // we store the vertices in this array and apply the transform after, then put them in the buffer
        Array<Vector2> vertices = new Array<>(true, 1 + refinement * 4);

        // add center vertex
        Vector2 center = vectorsPool.allocate().set(0, 0);
        vertices.add(center); // center vertex

        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
            corner.set(-r, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + r, heightHalf - r);
            vertices.add(corner);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
            corner.set(0, r);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - r, heightHalf - r);
            vertices.add(corner);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
            corner.set(r, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - r, -heightHalf + r);
            vertices.add(corner);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            Vector2 corner = vectorsPool.allocate();
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

        vectorsPool.freeAll(vertices);
        vertexIndex += 1 + refinement * 4;
    }

    public void drawRectangleBorder(float width, float height, float thickness, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 8) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        float widthHalf     = width     * 0.5f;
        float heightHalf    = height    * 0.5f;
        float thicknessHalf = thickness * 0.5f;

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);

        Array<Vector2> vertices = new Array<>(true, 8);
        // inner vertices
        Vector2 inner_vertex_0 = vectorsPool.allocate().set(-widthHalf + thicknessHalf, heightHalf - thicknessHalf);
        Vector2 inner_vertex_1 = vectorsPool.allocate().set(-widthHalf + thicknessHalf, -heightHalf + thicknessHalf);
        Vector2 inner_vertex_2 = vectorsPool.allocate().set(widthHalf - thicknessHalf, -heightHalf + thicknessHalf);
        Vector2 inner_vertex_3 = vectorsPool.allocate().set(widthHalf - thicknessHalf, heightHalf - thicknessHalf);
        // outer vertices
        Vector2 outer_vertex_0 = vectorsPool.allocate().set(-widthHalf - thicknessHalf, heightHalf + thicknessHalf);
        Vector2 outer_vertex_1 = vectorsPool.allocate().set(-widthHalf - thicknessHalf, -heightHalf - thicknessHalf);
        Vector2 outer_vertex_2 = vectorsPool.allocate().set(widthHalf + thicknessHalf, -heightHalf - thicknessHalf);
        Vector2 outer_vertex_3 = vectorsPool.allocate().set(widthHalf + thicknessHalf, heightHalf + thicknessHalf);

        vertices.add(inner_vertex_0, inner_vertex_1, inner_vertex_2, inner_vertex_3);
        vertices.add(outer_vertex_0, outer_vertex_1, outer_vertex_2, outer_vertex_3);

        // transform each vertex, then put it in the buffer + tint + uv
        for (int i = 0; i < vertices.size; i++) {
            Vector2 vertex = vertices.get(i);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(angleZ);
            vertex.add(x, y);
            verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
        }

        // put indices (use sketch).
        int startVertex = this.vertexIndex;

        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 4);
        indicesBuffer.put(startVertex + 5);

        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 5);
        indicesBuffer.put(startVertex + 1);

        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 5);
        indicesBuffer.put(startVertex + 6);

        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 6);
        indicesBuffer.put(startVertex + 2);

        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 6);
        indicesBuffer.put(startVertex + 7);

        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 7);
        indicesBuffer.put(startVertex + 3);

        indicesBuffer.put(startVertex + 3);
        indicesBuffer.put(startVertex + 7);
        indicesBuffer.put(startVertex + 4);

        indicesBuffer.put(startVertex + 3);
        indicesBuffer.put(startVertex + 4);
        indicesBuffer.put(startVertex + 0);

        vectorsPool.freeAll(vertices);
        vertexIndex += 8;
    }

    /* Rendering 2D primitives - Lines */

    public void drawLineThin(float x1, float y1, float x2, float y2) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_LINES);

        verticesBuffer.put(x1).put(y1).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x2).put(y2).put(currentTint).put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer.put(startVertex);
        indicesBuffer.put(startVertex + 1);

        vertexIndex += 2;
    }

    public void drawLineFilled(float x1, float y1, float x2, float y2, float thickness) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        Vector2 dir = vectorsPool.allocate();
        dir.x = x2 - x1;
        dir.y = y2 - y1;
        dir.nor();
        dir.scl(thickness * 0.5f);
        dir.rotate90(1);

        // put vertices for line segment
        verticesBuffer.put(x1 + dir.x).put(y1 + dir.y).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x1 - dir.x).put(y1 - dir.y).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x2 - dir.x).put(y2 - dir.y).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x2 + dir.x).put(y2 + dir.y).put(currentTint).put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 3);

        vectorsPool.free(dir);
        vertexIndex += 4;
    }

    public void drawLineFilled(float x1, float y1, float x2, float y2, float thickness, int edgeRefinement) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4 + (1 + edgeRefinement) + (1 + edgeRefinement)) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        final float r = thickness * 0.5f;
        edgeRefinement = Math.max(3, edgeRefinement);
        Vector2 p = vectorsPool.allocate();
        p.x = x2 - x1;
        p.y = y2 - y1;
        p.nor();
        p.scl(r);
        p.rotate90(1);

        // put vertices for line segment
        verticesBuffer.put(x1 + p.x).put(y1 + p.y).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x1 - p.x).put(y1 - p.y).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x2 - p.x).put(y2 - p.y).put(currentTint).put(0.5f).put(0.5f);
        verticesBuffer.put(x2 + p.x).put(y2 + p.y).put(currentTint).put(0.5f).put(0.5f);

        /* put edge circles */
        final float da = 180.0f / (edgeRefinement - 1);
        Vector2 vertex = vectorsPool.allocate();
        /* circle 1: */
        verticesBuffer.put(x1).put(y1).put(currentTint).put(0.5f).put(0.5f); // center point
        /* put arc vertices */
        for (int i = 0; i < edgeRefinement; i++) {
            vertex.set(p);
            vertex.rotateDeg(da * i);
            verticesBuffer.put(x1 + vertex.x).put(y1 + vertex.y).put(currentTint).put(0.5f).put(0.5f);
        }
        /* circle 2: */
        verticesBuffer.put(x2).put(y2).put(currentTint).put(0.5f).put(0.5f); // center point
        /* put arc vertices */
        for (int i = 0; i < edgeRefinement; i++) {
            vertex.set(p);
            vertex.rotateDeg(-da * i);
            verticesBuffer.put(x2 + vertex.x).put(y2 + vertex.y).put(currentTint).put(0.5f).put(0.5f);
        }
        vectorsPool.free(vertex);

        int startVertex = this.vertexIndex;

        // put indices for line segment
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 3);

        // put indices for circle 1
        for (int i = 0; i < edgeRefinement - 1; i++) {
            indicesBuffer.put(startVertex + 4);
            indicesBuffer.put(startVertex + 4 + i + 1);
            indicesBuffer.put(startVertex + 4 + i + 2);
        }

        // put indices for circle 2
        for (int i = 0; i < edgeRefinement - 1; i++) {
            indicesBuffer.put(startVertex + 4 + edgeRefinement + 1);
            indicesBuffer.put(startVertex + 4 + edgeRefinement + 1 + i + 1);
            indicesBuffer.put(startVertex + 4 + edgeRefinement + 1 + i + 2);
        }

        vectorsPool.free(p);
        vertexIndex += 4 + (1 + edgeRefinement) + (1 + edgeRefinement); // 4 vertices for the line segment, (1 + edgeRefinement) for each half-circle.
    }

    /* Rendering 2D primitives - Curves */

    public void drawCurveThin(final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if ((vertexIndex + values.length) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_LINES);

        /* put vertices */
        for (Vector2 value : values) {
            verticesBuffer.put(value.x).put(value.y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < values.length - 1; i++) {
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
        }

        vertexIndex += values.length;
    }

    public void drawCurveThin(float minX, float maxX, int refinement, Function<Float, Float> f) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if ((vertexIndex + refinement) * VERTEX_SIZE  > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_LINES);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        Array<Vector2> vertices = new Array<>(true, refinement);
        for (int i = 0; i < refinement; i++) {
            Vector2 vertex = vectorsPool.allocate();
            vertex.x = minX + i * step;
            vertex.y = f.apply(vertex.x);
            vertices.add(vertex);
        }

        /* put vertices */
        for (Vector2 value : vertices) {
            verticesBuffer.put(value.x).put(value.y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < vertices.size - 1; i++) {
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
        }

        vectorsPool.freeAll(vertices);
        vertexIndex += refinement;
    }

    public void drawCurveFilled(float thickness, final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if ((vertexIndex + values.length * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        thickness *= 0.5f;

        Array<Vector2> vertices = new Array<>(true, values.length * 2);
        /* put vertices */

        /* allocate memory */
        Vector2 norm = vectorsPool.allocate();
        Vector2 dir_prev = vectorsPool.allocate();
        Vector2 dir_next = vectorsPool.allocate();
        Vector2 v = vectorsPool.allocate();

        /* first 2 vertices */
        norm.x = values[1].x - values[0].x;
        norm.y = values[1].y - values[0].y;
        norm.nor();
        norm.rotate90(1);
        Vector2 up_first = vectorsPool.allocate();
        up_first.x = values[0].x + thickness * norm.x;
        up_first.y = values[0].y + thickness * norm.y;
        Vector2 down_first = vectorsPool.allocate();
        down_first.x = values[0].x - thickness * norm.x;
        down_first.y = values[0].y - thickness * norm.y;
        vertices.add(down_first);
        vertices.add(up_first);

        /* in-between vertices */
        int skipped = 0;
        for (int i = 1; i < values.length - 1; i++) {
            dir_prev.x = values[i].x - values[i-1].x;
            dir_prev.y = values[i].y - values[i-1].y;
            dir_prev.flip();
            dir_next.x = values[i+1].x - values[i].x;
            dir_next.y = values[i+1].y - values[i].y;
            float cross = Vector2.crs(dir_prev, dir_next);
            if (cross == 0) {
                skipped++;
                continue;
            }
            dir_prev.nor();
            dir_next.nor();
            float angle_between = Vector2.angleBetweenDeg(dir_prev, dir_next);
            float length = thickness / MathUtils.sinDeg(angle_between * 0.5f);
            v.x = dir_prev.x + dir_next.x;
            v.y = dir_prev.y + dir_next.y;
            v.nor().scl(length);
            Vector2 vertex_down = vectorsPool.allocate();
            vertex_down.x = values[i].x + v.x;
            vertex_down.y = values[i].y + v.y;
            Vector2 vertex_up = vectorsPool.allocate();
            vertex_up.x = values[i].x - v.x;
            vertex_up.y = values[i].y - v.y;
            if (cross > 0) {
                vertices.add(vertex_down);
                vertices.add(vertex_up);
            } else {
                vertices.add(vertex_up);
                vertices.add(vertex_down);
            }
        }

        /* last 2 vertices */
        norm.x = values[values.length - 1].x - values[values.length - 2].x;
        norm.y = values[values.length - 1].y - values[values.length - 2].y;
        norm.nor();
        norm.rotate90(1);
        Vector2 up_last   = vectorsPool.allocate();
        up_last.x = values[values.length - 1].x + thickness * norm.x;
        up_last.y = values[values.length - 1].y + thickness * norm.y;
        Vector2 down_last = vectorsPool.allocate();
        down_last.x = values[values.length - 1].x - thickness * norm.x;
        down_last.y = values[values.length - 1].y - thickness * norm.y;
        vertices.add(down_last);
        vertices.add(up_last);

        for (int i = 0; i < vertices.size; i++) {
            verticesBuffer.put(vertices.get(i).x).put(vertices.get(i).y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < values.length * 2 - 2 - skipped * 2; i += 2) {
            indicesBuffer.put(startVertex + i + 0);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        vertexIndex += vertices.size;

        /* free resource */
        vectorsPool.free(norm);
        vectorsPool.free(dir_prev);
        vectorsPool.free(dir_next);
        vectorsPool.free(v);
        vectorsPool.freeAll(vertices);
    }

    public void drawCurveFilled(float thickness, int smoothness, final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if ((vertexIndex + 2 + (values.length - 2) * (smoothness + 1) * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        smoothness = Math.max(1, smoothness);
        final float t = 0.5f * thickness;

        Array<Vector2> vertices = new Array<>(true, values.length);
        /* put vertices */

        /* allocate required memory */
        Vector2 edgeNormal = vectorsPool.allocate();
        Vector2 norm = vectorsPool.allocate();
        Vector2 dir_prev = vectorsPool.allocate();
        Vector2 dir_next = vectorsPool.allocate();
        Vector2 normal_prev = vectorsPool.allocate();
        Vector2 normal_next = vectorsPool.allocate();

        /* first 2 vertices */
        edgeNormal.x = values[1].x - values[0].x;
        edgeNormal.y = values[1].y - values[0].y;
        edgeNormal.nor();
        edgeNormal.rotate90(1);
        Vector2 up_first = vectorsPool.allocate();
        up_first.x = values[0].x + t * edgeNormal.x;
        up_first.y = values[0].y + t * edgeNormal.y;
        Vector2 down_first = vectorsPool.allocate();
        down_first.x = values[0].x - t * edgeNormal.x;
        down_first.y = values[0].y - t * edgeNormal.y;
        vertices.add(down_first);
        vertices.add(up_first);

        /* compute circular corner vertices */
        /* iterate over all internal corners */
        for (int i = 1; i < values.length - 1; i++) {
            Vector2 corner = values[i];
            dir_prev.x = values[i].x - values[i-1].x;
            dir_prev.y = values[i].y - values[i-1].y;
            dir_prev.flip();
            dir_next.x = values[i+1].x - values[i].x;
            dir_next.y = values[i+1].y - values[i].y;

            float cross = Vector2.crs(dir_prev, dir_next);
            if (MathUtils.isZero(cross)) continue; // Skip co-linear corners.

            dir_prev.nor();
            dir_next.nor();

            normal_prev.set(dir_prev);
            normal_prev.rotate90(-1);

            normal_next.set(dir_next);
            normal_next.rotate90(1);

            float av = Vector2.angleBetweenDeg(dir_prev, dir_next); // angle between normals
            float an = Vector2.angleBetweenDeg(normal_prev, normal_next); // angle between normals
            float da = an / smoothness;

            //norm.set(normal_prev).add(normal_next).nor().scl(2*t / MathUtils.sinDeg(av / 2));
            norm.set(normal_prev).add(normal_next).nor().scl(t / MathUtils.sinDeg(av / 2)); // <- looks better
            float sign = Math.signum(cross);

            for (int j = 0; j < smoothness + 1; j++) {
                Vector2 v1 = vectorsPool.allocate();
                Vector2 v2 = vectorsPool.allocate();
                if (sign < 0) {
                    v1.set(sign * normal_prev.x * t, sign * normal_prev.y * t).rotateDeg(da * j).add(corner);
                    //v2.set(v1).add(norm.x, norm.y);
                    v2.set(corner).add(norm.x, norm.y); // <- looks better
                    vertices.add(v1);
                    vertices.add(v2);
                } else {
                    v1.set(sign * normal_prev.x * t, sign * normal_prev.y * t).rotateDeg(-da * j).add(corner);
                    //v2.set(v1).add(-norm.x, -norm.y);
                    v2.set(corner).add(-norm.x, -norm.y); // <- looks better
                    vertices.add(v2);
                    vertices.add(v1);
                }
            }
        }

        /* last 2 vertices */
        edgeNormal.x = values[values.length - 1].x - values[values.length - 2].x;
        edgeNormal.y = values[values.length - 1].y - values[values.length - 2].y;
        edgeNormal.nor();
        edgeNormal.rotate90(1);
        Vector2 up_last = vectorsPool.allocate();
        up_last.x = values[values.length - 1].x + t * edgeNormal.x;
        up_last.y = values[values.length - 1].y + t * edgeNormal.y;
        Vector2 down_last = vectorsPool.allocate();
        down_last.x = values[values.length - 1].x - t * edgeNormal.x;
        down_last.y = values[values.length - 1].y - t * edgeNormal.y;
        vertices.add(down_last);
        vertices.add(up_last);

        final int curveEndIndex = vertices.size;

        /* add far edges half circles */
        final float da = 180.0f / (smoothness);

        Vector2 p_0 = vectorsPool.allocate();
        p_0.x = vertices.get(1).x - vertices.get(0).x;
        p_0.y = vertices.get(1).y - vertices.get(0).y;
        p_0.nor();
        p_0.scl(t);
        Vector2 p_1 = vectorsPool.allocate();
        p_1.x = vertices.get(vertices.size - 1).x - vertices.get(vertices.size - 2).x;
        p_1.y = vertices.get(vertices.size - 1).y - vertices.get(vertices.size - 2).y;
        p_1.nor();
        p_1.scl(t);

        /* add center 0 */
        Vector2 center_0 = vectorsPool.allocate();
        center_0.x = values[0].x;
        center_0.y = values[0].y;
        vertices.add(center_0);
        /* add half circle 0: */
        for (int i = 0; i < smoothness + 1; i++) {
            Vector2 vertex = vectorsPool.allocate();
            vertex.set(p_0);
            vertex.rotateDeg(da * i).add(center_0);
            vertices.add(vertex);
        }

        /* add center 1 */
        Vector2 center_1 = vectorsPool.allocate();
        center_1.x = values[values.length - 1].x;
        center_1.y = values[values.length - 1].y;
        vertices.add(center_1);
        /* add half circle 1: */
        for (int i = 0; i < smoothness + 2; i++) {
            Vector2 vertex = vectorsPool.allocate();
            vertex.set(p_1);
            vertex.rotateDeg(-da * i).add(center_1);
            vertices.add(vertex);
        }


        for (int i = 0; i < vertices.size; i++) {
            verticesBuffer.put(vertices.get(i).x).put(vertices.get(i).y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices ("connect the dots") */
        final int startVertex = this.vertexIndex;
        for (int i = 0; i < curveEndIndex - 2; i += 2) { // curve +  rounded corners
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        // put indices for half-circle 0
        for (int i = 0; i < smoothness - 1; i++) {
            indicesBuffer.put(startVertex + curveEndIndex + 1 + 0);
            indicesBuffer.put(startVertex + curveEndIndex + 1 + i + 1);
            indicesBuffer.put(startVertex + curveEndIndex + 1 + i + 2);
        }

        // put indices for circle 1
        for (int i = 0; i < smoothness; i++) {
            indicesBuffer.put(startVertex + curveEndIndex + smoothness + 2 + 0);
            indicesBuffer.put(startVertex + curveEndIndex + smoothness + 2 + i + 1);
            indicesBuffer.put(startVertex + curveEndIndex + smoothness + 2 + i + 2);
        }

        vertexIndex += vertices.size;

        /* free memory */
        vectorsPool.free(up_first);
        vectorsPool.free(down_first);
        vectorsPool.free(up_last);
        vectorsPool.free(down_last);
        vectorsPool.free(edgeNormal);
        vectorsPool.free(norm);
        vectorsPool.free(dir_prev);
        vectorsPool.free(dir_next);
        vectorsPool.free(normal_prev);
        vectorsPool.free(normal_next);
        vectorsPool.free(p_0);
        vectorsPool.free(p_1);
    }

    // TODO: implement.
    public void drawCurveFilled(float thickness, float minX, float maxX, int refinement, Function<Float, Float> f, boolean roundEnds) {

    }

    public void drawCurveFilled_broken2(Function<Float, Float> f, float thickness, float minX, float maxX, int refinement) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if ((vertexIndex + refinement * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        thickness *= 0.5f;

        Vector2[] values = new Vector2[refinement];
        for (int i = 0; i < refinement; i++) {
            Vector2 point = new Vector2();
            point.x = minX + i * step;
            point.y = f.apply(point.x);
            values[i] = point;
        }

        Array<Vector2> vertices = new Array<>(true, refinement * 2);
        /* put vertices */

        /* allocate memory */
        Vector2 norm = vectorsPool.allocate();
        Vector2 dir_prev = vectorsPool.allocate();
        Vector2 dir_next = vectorsPool.allocate();
        Vector2 v = vectorsPool.allocate();

        /* first 2 vertices */
        norm.x = values[1].x - values[0].x;
        norm.y = values[1].y - values[0].y;
        norm.nor();
        norm.rotate90(1);
        Vector2 up_first = vectorsPool.allocate();
        up_first.x = values[0].x + thickness * norm.x;
        up_first.y = values[0].y + thickness * norm.y;
        Vector2 down_first = vectorsPool.allocate();
        down_first.x = values[0].x - thickness * norm.x;
        down_first.y = values[0].y - thickness * norm.y;
        vertices.add(down_first);
        vertices.add(up_first);

        /* in-between vertices */
        int skipped = 0;
        for (int i = 1; i < values.length - 1; i++) {
            dir_prev.x = values[i].x - values[i-1].x;
            dir_prev.y = values[i].y - values[i-1].y;
            dir_prev.flip();
            dir_next.x = values[i+1].x - values[i].x;
            dir_next.y = values[i+1].y - values[i].y;
            float cross = Vector2.crs(dir_prev, dir_next);
            if (cross == 0) {
                skipped++;
                continue;
            }
            dir_prev.nor();
            dir_next.nor();
            float angle_between = Vector2.angleBetweenDeg(dir_prev, dir_next);
            float length = thickness / MathUtils.sinDeg(angle_between * 0.5f);
            v.x = dir_prev.x + dir_next.x;
            v.y = dir_prev.y + dir_next.y;
            v.nor().scl(length);
            Vector2 vertex_down = vectorsPool.allocate();
            vertex_down.x = values[i].x + v.x;
            vertex_down.y = values[i].y + v.y;
            Vector2 vertex_up = vectorsPool.allocate();
            vertex_up.x = values[i].x - v.x;
            vertex_up.y = values[i].y - v.y;
            if (cross > 0) {
                vertices.add(vertex_down);
                vertices.add(vertex_up);
            } else {
                vertices.add(vertex_up);
                vertices.add(vertex_down);
            }
        }

        /* last 2 vertices */
        norm.x = values[values.length - 1].x - values[values.length - 2].x;
        norm.y = values[values.length - 1].y - values[values.length - 2].y;
        norm.nor();
        norm.rotate90(1);
        Vector2 up_last   = vectorsPool.allocate();
        up_last.x = values[values.length - 1].x + thickness * norm.x;
        up_last.y = values[values.length - 1].y + thickness * norm.y;
        Vector2 down_last = vectorsPool.allocate();
        down_last.x = values[values.length - 1].x - thickness * norm.x;
        down_last.y = values[values.length - 1].y - thickness * norm.y;
        vertices.add(down_last);
        vertices.add(up_last);

        for (int i = 0; i < vertices.size; i++) {
            verticesBuffer.put(vertices.get(i).x).put(vertices.get(i).y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < values.length * 2 - 2 - skipped * 2; i += 2) {
            indicesBuffer.put(startVertex + i + 0);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        vertexIndex += vertices.size;

        /* free resource */
        vectorsPool.free(norm);
        vectorsPool.free(dir_prev);
        vectorsPool.free(dir_next);
        vectorsPool.free(v);
        vectorsPool.freeAll(vertices);
    }

    // TODO: FIXME
    public void drawCurveFilled_TODO(Function<Float, Float> f, float thickness, float minX, float maxX, int refinement, boolean roundEdges) {

        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if ((vertexIndex + refinement * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        final float t = 0.5f * thickness;

        Vector2[] values = new Vector2[refinement];
        for (int i = 0; i < refinement; i++) {
            Vector2 point = new Vector2();
            point.x = minX + i * step;
            point.y = f.apply(point.x);
            values[i] = point;
        }
        Array<Vector2> vertices = new Array<>(true, refinement * 2);
        /* put vertices */

        /* allocate required memory */
        Vector2 edgeNormal = vectorsPool.allocate();
        Vector2 norm = vectorsPool.allocate();
        Vector2 dir_prev = vectorsPool.allocate();
        Vector2 dir_next = vectorsPool.allocate();
        Vector2 normal_prev = vectorsPool.allocate();
        Vector2 normal_next = vectorsPool.allocate();

        /* first 2 vertices */
        edgeNormal.x = values[1].x - values[0].x;
        edgeNormal.y = values[1].y - values[0].y;
        edgeNormal.nor();
        edgeNormal.rotate90(1);
        Vector2 up_first = vectorsPool.allocate();
        up_first.x = values[0].x + t * edgeNormal.x;
        up_first.y = values[0].y + t * edgeNormal.y;
        Vector2 down_first = vectorsPool.allocate();
        down_first.x = values[0].x - t * edgeNormal.x;
        down_first.y = values[0].y - t * edgeNormal.y;
        vertices.add(down_first);
        vertices.add(up_first);

        /* compute circular corner vertices */
        /* iterate over all internal corners */
        for (int i = 1; i < values.length - 1; i++) {
            Vector2 corner = values[i];
            dir_prev.x = values[i].x - values[i-1].x;
            dir_prev.y = values[i].y - values[i-1].y;
            dir_prev.flip();
            dir_next.x = values[i+1].x - values[i].x;
            dir_next.y = values[i+1].y - values[i].y;

            float cross = Vector2.crs(dir_prev, dir_next);
            //if (MathUtils.isZero(cross)) continue; // Skip co-linear corners.

            dir_prev.nor();
            dir_next.nor();

            normal_prev.set(dir_prev);
            normal_prev.rotate90(-1);

            normal_next.set(dir_next);
            normal_next.rotate90(1);

            float av = Vector2.angleBetweenDeg(dir_prev, dir_next); // angle between normals
            float an = Vector2.angleBetweenDeg(normal_prev, normal_next); // angle between normals
            float da = an / refinement;

            //norm.set(normal_prev).add(normal_next).nor().scl(t / MathUtils.sinDeg(av / 2)); // causes skipped vertex
            norm.set(normal_prev).add(normal_next).nor().scl(t); // <- isn't as broken
            float sign = Math.signum(cross);

            for (int j = 0; j < refinement; j++) {
                Vector2 v1 = vectorsPool.allocate();
                Vector2 v2 = vectorsPool.allocate();
                if (sign < 0) {
                    v1.set(sign * normal_prev.x * t, sign * normal_prev.y * t).rotateDeg(da * j).add(corner);
                    //v2.set(v1).add(norm.x, norm.y);
                    v2.set(corner).add(norm.x, norm.y); // <- looks better
                    vertices.add(v1);
                    vertices.add(v2);
                } else {
                    v1.set(sign * normal_prev.x * t, sign * normal_prev.y * t).rotateDeg(-da * j).add(corner);
                    //v2.set(v1).add(-norm.x, -norm.y);
                    v2.set(corner).add(-norm.x, -norm.y); // <- looks better
                    vertices.add(v2);
                    vertices.add(v1);
                }
            }
        }

        /* last 2 vertices */
        edgeNormal.x = values[values.length - 1].x - values[values.length - 2].x;
        edgeNormal.y = values[values.length - 1].y - values[values.length - 2].y;
        edgeNormal.nor();
        edgeNormal.rotate90(1);
        Vector2 up_last = vectorsPool.allocate();
        up_last.x = values[values.length - 1].x + t * edgeNormal.x;
        up_last.y = values[values.length - 1].y + t * edgeNormal.y;
        Vector2 down_last = vectorsPool.allocate();
        down_last.x = values[values.length - 1].x - t * edgeNormal.x;
        down_last.y = values[values.length - 1].y - t * edgeNormal.y;
        vertices.add(down_last);
        vertices.add(up_last);

        final int curveEndIndex = vertices.size;

        /* add far edges half circles */
        if (false)
        {
            final float da = 180.0f / (refinement);

            Vector2 p_0 = vectorsPool.allocate();
            p_0.x = vertices.get(1).x - vertices.get(0).x;
            p_0.y = vertices.get(1).y - vertices.get(0).y;
            p_0.nor();
            p_0.scl(t);
            Vector2 p_1 = vectorsPool.allocate();
            p_1.x = vertices.get(vertices.size - 1).x - vertices.get(vertices.size - 2).x;
            p_1.y = vertices.get(vertices.size - 1).y - vertices.get(vertices.size - 2).y;
            p_1.nor();
            p_1.scl(t);

            /* add center 0 */
            Vector2 center_0 = vectorsPool.allocate();
            center_0.x = values[0].x;
            center_0.y = values[0].y;
            vertices.add(center_0);
            /* add half circle 0: */
            for (int i = 0; i < refinement + 1; i++) {
                Vector2 vertex = vectorsPool.allocate();
                vertex.set(p_0);
                vertex.rotateDeg(da * i).add(center_0);
                vertices.add(vertex);
            }

            /* add center 1 */
            Vector2 center_1 = vectorsPool.allocate();
            center_1.x = values[values.length - 1].x;
            center_1.y = values[values.length - 1].y;
            vertices.add(center_1);
            /* add half circle 1: */
            for (int i = 0; i < refinement + 2; i++) {
                Vector2 vertex = vectorsPool.allocate();
                vertex.set(p_1);
                vertex.rotateDeg(-da * i).add(center_1);
                vertices.add(vertex);
            }
        }


        for (int i = 0; i < vertices.size; i++) {
            verticesBuffer.put(vertices.get(i).x).put(vertices.get(i).y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices ("connect the dots") */
        final int startVertex = this.vertexIndex;
        for (int i = 0; i < curveEndIndex - 2; i += 2) { // curve +  rounded corners
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        // put indices for half-circle 0
        if (false)
        for (int i = 0; i < refinement - 1; i++) {
            indicesBuffer.put(startVertex + curveEndIndex + 1 + 0);
            indicesBuffer.put(startVertex + curveEndIndex + 1 + i + 1);
            indicesBuffer.put(startVertex + curveEndIndex + 1 + i + 2);
        }

        // put indices for circle 1
        if (false)
        for (int i = 0; i < refinement; i++) {
            indicesBuffer.put(startVertex + curveEndIndex + refinement + 2 + 0);
            indicesBuffer.put(startVertex + curveEndIndex + refinement + 2 + i + 1);
            indicesBuffer.put(startVertex + curveEndIndex + refinement + 2 + i + 2);
        }

        vertexIndex += vertices.size;

        /* free memory */
        vectorsPool.free(up_first);
        vectorsPool.free(down_first);
        vectorsPool.free(up_last);
        vectorsPool.free(down_last);
        vectorsPool.free(edgeNormal);
        vectorsPool.free(norm);
        vectorsPool.free(dir_prev);
        vectorsPool.free(dir_next);
        vectorsPool.free(normal_prev);
        vectorsPool.free(normal_next);
        //vectorsPool.free(p_0);
        //vectorsPool.free(p_1);
    }

    public void drawCurveFilled_broken(Function<Float, Float> f, float thickness, float minX, float maxX, int smoothness, int points) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        points = Math.max(2, points);
        if ((vertexIndex + 2 + (points - 2) * (smoothness + 1) * 2) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / points;

        smoothness = Math.max(1, smoothness);
        final float t = 0.5f * thickness;

        Vector2[] values = new Vector2[points];
        for (int i = 0; i < points; i++) {
            Vector2 point = new Vector2();
            point.x = minX + i * step;
            point.y = f.apply(point.x);
            values[i] = point;
        }
        Array<Vector2> vertices = new Array<>(true, 2 + (points - 2) * (smoothness + 1) * 2);
        /* put vertices */

        /* allocate required memory */
        Vector2 edgeNormal = vectorsPool.allocate();
        Vector2 norm = vectorsPool.allocate();
        Vector2 dir_prev = vectorsPool.allocate();
        Vector2 dir_next = vectorsPool.allocate();
        Vector2 normal_prev = vectorsPool.allocate();
        Vector2 normal_next = vectorsPool.allocate();

        /* first 2 vertices */
        edgeNormal.x = values[1].x - values[0].x;
        edgeNormal.y = values[1].y - values[0].y;
        edgeNormal.nor();
        edgeNormal.rotate90(1);
        Vector2 up_first = vectorsPool.allocate();
        up_first.x = values[0].x + t * edgeNormal.x;
        up_first.y = values[0].y + t * edgeNormal.y;
        Vector2 down_first = vectorsPool.allocate();
        down_first.x = values[0].x - t * edgeNormal.x;
        down_first.y = values[0].y - t * edgeNormal.y;
        vertices.add(down_first);
        vertices.add(up_first);

        /* compute circular corner vertices */
        /* iterate over all internal corners */
        for (int i = 1; i < values.length - 1; i++) {
            Vector2 corner = values[i];
            dir_prev.x = values[i].x - values[i-1].x;
            dir_prev.y = values[i].y - values[i-1].y;
            dir_prev.flip();
            dir_next.x = values[i+1].x - values[i].x;
            dir_next.y = values[i+1].y - values[i].y;

            float cross = Vector2.crs(dir_prev, dir_next);
            //if (MathUtils.isZero(cross)) continue; // Skip co-linear corners.

            dir_prev.nor();
            dir_next.nor();

            normal_prev.set(dir_prev);
            normal_prev.rotate90(-1);

            normal_next.set(dir_next);
            normal_next.rotate90(1);

            float av = Vector2.angleBetweenDeg(dir_prev, dir_next); // angle between normals
            float an = Vector2.angleBetweenDeg(normal_prev, normal_next); // angle between normals
            float da = an / smoothness;

            //norm.set(normal_prev).add(normal_next).nor().scl(t / MathUtils.sinDeg(av / 2)); // causes skipped vertex
            norm.set(normal_prev).add(normal_next).nor().scl(t); // <- isn't as broken
            float sign = Math.signum(cross);

            for (int j = 0; j < smoothness + 1; j++) {
                Vector2 v1 = vectorsPool.allocate();
                Vector2 v2 = vectorsPool.allocate();
                if (sign < 0) {
                    v1.set(sign * normal_prev.x * t, sign * normal_prev.y * t).rotateDeg(da * j).add(corner);
                    //v2.set(v1).add(norm.x, norm.y);
                    v2.set(corner).add(norm.x, norm.y); // <- looks better
                    vertices.add(v1);
                    vertices.add(v2);
                } else {
                    v1.set(sign * normal_prev.x * t, sign * normal_prev.y * t).rotateDeg(-da * j).add(corner);
                    //v2.set(v1).add(-norm.x, -norm.y);
                    v2.set(corner).add(-norm.x, -norm.y); // <- looks better
                    vertices.add(v2);
                    vertices.add(v1);
                }
            }
        }

        /* last 2 vertices */
        edgeNormal.x = values[values.length - 1].x - values[values.length - 2].x;
        edgeNormal.y = values[values.length - 1].y - values[values.length - 2].y;
        edgeNormal.nor();
        edgeNormal.rotate90(1);
        Vector2 up_last = vectorsPool.allocate();
        up_last.x = values[values.length - 1].x + t * edgeNormal.x;
        up_last.y = values[values.length - 1].y + t * edgeNormal.y;
        Vector2 down_last = vectorsPool.allocate();
        down_last.x = values[values.length - 1].x - t * edgeNormal.x;
        down_last.y = values[values.length - 1].y - t * edgeNormal.y;
        vertices.add(down_last);
        vertices.add(up_last);

        final int curveEndIndex = vertices.size;

        /* add far edges half circles */
        final float da = 180.0f / (smoothness);

        Vector2 p_0 = vectorsPool.allocate();
        p_0.x = vertices.get(1).x - vertices.get(0).x;
        p_0.y = vertices.get(1).y - vertices.get(0).y;
        p_0.nor();
        p_0.scl(t);
        Vector2 p_1 = vectorsPool.allocate();
        p_1.x = vertices.get(vertices.size - 1).x - vertices.get(vertices.size - 2).x;
        p_1.y = vertices.get(vertices.size - 1).y - vertices.get(vertices.size - 2).y;
        p_1.nor();
        p_1.scl(t);

        /* add center 0 */
        Vector2 center_0 = vectorsPool.allocate();
        center_0.x = values[0].x;
        center_0.y = values[0].y;
        vertices.add(center_0);
        /* add half circle 0: */
        for (int i = 0; i < smoothness + 1; i++) {
            Vector2 vertex = vectorsPool.allocate();
            vertex.set(p_0);
            vertex.rotateDeg(da * i).add(center_0);
            vertices.add(vertex);
        }

        /* add center 1 */
        Vector2 center_1 = vectorsPool.allocate();
        center_1.x = values[values.length - 1].x;
        center_1.y = values[values.length - 1].y;
        vertices.add(center_1);
        /* add half circle 1: */
        for (int i = 0; i < smoothness + 2; i++) {
            Vector2 vertex = vectorsPool.allocate();
            vertex.set(p_1);
            vertex.rotateDeg(-da * i).add(center_1);
            vertices.add(vertex);
        }


        for (int i = 0; i < vertices.size; i++) {
            verticesBuffer.put(vertices.get(i).x).put(vertices.get(i).y).put(currentTint).put(0.5f).put(0.5f);
        }

        /* put indices ("connect the dots") */
        final int startVertex = this.vertexIndex;
        for (int i = 0; i < curveEndIndex - 2; i += 2) { // curve +  rounded corners
            indicesBuffer.put(startVertex + i);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 2);
            indicesBuffer.put(startVertex + i + 1);
            indicesBuffer.put(startVertex + i + 3);
        }

        // put indices for half-circle 0
        for (int i = 0; i < smoothness - 1; i++) {
            indicesBuffer.put(startVertex + curveEndIndex + 1 + 0);
            indicesBuffer.put(startVertex + curveEndIndex + 1 + i + 1);
            indicesBuffer.put(startVertex + curveEndIndex + 1 + i + 2);
        }

        // put indices for circle 1
        for (int i = 0; i < smoothness; i++) {
            indicesBuffer.put(startVertex + curveEndIndex + smoothness + 2 + 0);
            indicesBuffer.put(startVertex + curveEndIndex + smoothness + 2 + i + 1);
            indicesBuffer.put(startVertex + curveEndIndex + smoothness + 2 + i + 2);
        }
        if (Keyboard.isKeyJustPressed(Keyboard.Key.J)) {
            System.out.println("indices buffer: " + indicesBuffer);
            System.out.println("verts   buffer: " + verticesBuffer);
        }


        vertexIndex += vertices.size;

        /* free memory */
        vectorsPool.free(up_first);
        vectorsPool.free(down_first);
        vectorsPool.free(up_last);
        vectorsPool.free(down_last);
        vectorsPool.free(edgeNormal);
        vectorsPool.free(norm);
        vectorsPool.free(dir_prev);
        vectorsPool.free(dir_next);
        vectorsPool.free(normal_prev);
        vectorsPool.free(normal_next);
        vectorsPool.free(p_0);
        vectorsPool.free(p_1);
    }

    /**
     * Renders the current batch, if not empty.
     */
    private void flush() {
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

    /**
     * Must be called after every call to begin().
     */
    public void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer2D_backup.class.getSimpleName() + ".end() without calling " + Renderer2D_backup.class.getSimpleName() + ".begin() first.");
        flush();
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        currentCamera = null;
        currentShader = null;
        drawing = false;
    }

    @Override
    public void deleteAll() {
        defaultShader.delete();
        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(vbo);
        GL30.glDeleteBuffers(ebo);
        whitePixel.delete();
    }

    /* Create defaults: shader, texture (single white pixel), camera */

    private static ShaderProgram createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D_backup.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D_backup.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
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

    /* TODO: DELETE after replacing in the physics renderer. */

    @Deprecated public void pushPolygon(final Shape2DPolygon polygon, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + polygon.vertices.length > VERTICES_CAPACITY * 4) {
            flush();
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

    @Deprecated public void pushThinCircle(final float r, final float centerX, final float centerY, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 15 * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand sides are multiplied by 2 to make sure buffer overflow is prevented
            flush();
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

    @Deprecated public void pushThinRectangle_old(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 6 * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            flush();
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

    @Deprecated public void pushThinLineSegment(float x1, float y1, float x2, float y2, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + 2 * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            flush();
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

    @Deprecated public void pushThinPolygon(final Array<Vector2> worldVertices, final int[] indices, final float tintFloatBits) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (vertexIndex + worldVertices.size * 5 * 2 > VERTICES_CAPACITY * 4) { // left hand side are multiplied by 2 to make sure buffer overflow is prevented
            flush();
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

    @Deprecated private void useTexture_old(Texture texture) {
        if (currentTexture != texture) {
            flush();
        }
        currentTexture = texture;
        currentShader.bindUniform("u_texture", currentTexture);
    }

    @Deprecated private void useCustomAttributes_old(HashMap<String, Object> customAttributes) {
        if (customAttributes != null) {
            flush();
            currentShader.bindUniforms(customAttributes);
        }
    }

    @Deprecated private void useMode_old(final int mode) {
        if (mode != this.currentMode) {
            flush();
        }
        this.currentMode = mode;
    }

}