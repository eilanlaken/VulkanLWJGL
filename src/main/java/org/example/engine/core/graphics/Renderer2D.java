package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayFloat;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.memory.MemoryResourceHolder;
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

// TODO: drawing filled curves.
// https://www.codeproject.com/Articles/226569/Drawing-polylines-by-tessellation
// https://math.stackexchange.com/questions/15815/how-to-union-many-polygons-efficiently
public class Renderer2D implements MemoryResourceHolder {

    /* constants */
    private static final int   VERTEX_SIZE       = 5;    // A vertex is composed of 5 floats: x,y: position, t: color (as float bits) and u,v: texture coordinates.
    private static final int   VERTICES_CAPACITY = 6000; // The batch can render VERTICES_CAPACITY vertices (so wee need a float buffer of size: VERTICES_CAPACITY * VERTEX_SIZE)
    private static final int   INDICES_CAPACITY  = VERTICES_CAPACITY * 2; // TODO
    private static final float WHITE_TINT        = Color.WHITE.toFloatBits();

    /* buffers */
    private final int         vao;
    private final int         vbo;
    private final int         ebo;
    private final IntBuffer   indicesBuffer  = BufferUtils.createIntBuffer(INDICES_CAPACITY * 3);
    private final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * VERTEX_SIZE);

    /* defaults */
    private final ShaderProgram defaultShader = createDefaultShaderProgram();
    private final Texture       whitePixel    = createWhiteSinglePixelTexture();
    private final Camera        defaultCamera = createDefaultCamera();

    /* memory pools */
    private final MemoryPool<Vector2>    vectorsPool    = new MemoryPool<>(Vector2.class, 10);
    private final MemoryPool<ArrayFloat> arrayFloatPool = new MemoryPool<>(ArrayFloat.class, 20);
    private final MemoryPool<ArrayInt>   arrayIntPool   = new MemoryPool<>(ArrayInt.class, 20);

    /* state */
    private Camera        currentCamera  = null;
    private Texture       currentTexture = null;
    private ShaderProgram currentShader  = null;
    private float         currentTint    = WHITE_TINT;
    private boolean       drawing        = false;
    private int           vertexIndex    = 0;
    private int           currentMode    = GL11.GL_TRIANGLES;
    private int           currentSFactor = GL11.GL_SRC_ALPHA;
    private int           currentDFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;
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
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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

    public void setBlending(int sFactor, int dFactor) {
        if (sFactor != currentSFactor || dFactor != currentDFactor) flush();
        this.currentSFactor = sFactor;
        this.currentDFactor = dFactor;
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
        Array<Vector2> vertices = new Array<>(true, 1 + refinement * 4); // TODO: allocate a float array instead of this.

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

        /* put vertices */
        verticesBuffer.put(x0).put(y0).put(currentTint).put(0.5f).put(0.5f); // V0
        verticesBuffer.put(x1).put(y1).put(currentTint).put(0.5f).put(0.5f); // V1
        verticesBuffer.put(x2).put(y2).put(currentTint).put(0.5f).put(0.5f); // V2
        verticesBuffer.put(x3).put(y3).put(currentTint).put(0.5f).put(0.5f); // V3

        // put indices
        int startVertex = this.vertexIndex;
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 2);
        indicesBuffer.put(startVertex + 3);
        indicesBuffer.put(startVertex + 0);

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

        vertexIndex += 8;

        vectorsPool.freeAll(vertices);
    }

    /* Rendering 2D primitives - Polygons */

    public void drawPolygonThin(float[] polygon, boolean triangulated, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if ((vertexIndex + count) * VERTEX_SIZE > verticesBuffer.capacity()) flush();
        int startVertex = this.vertexIndex;

        setMode(GL11.GL_LINES);

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);

        if (MathUtils.isZero(scaleX) || MathUtils.isZero(scaleY)) return;

        if (!triangulated) {
            Vector2 vertex = vectorsPool.allocate();
            for (int i = 0; i < polygon.length; i += 2) {
                float poly_x = polygon[i];
                float poly_y = polygon[i + 1];

                vertex.set(poly_x, poly_y);
                vertex.scl(scaleX, scaleY);
                vertex.rotateDeg(angleZ);
                vertex.add(x, y);

                verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
            }
            vectorsPool.free(vertex);

            for (int i = 0; i < count - 1; i++) {
                indicesBuffer.put(startVertex + i);
                indicesBuffer.put(startVertex + i + 1);
            }
            indicesBuffer.put(startVertex + count - 1);
            indicesBuffer.put(startVertex + 0);
            vertexIndex += count;
        } else {
            ArrayFloat vertices = arrayFloatPool.allocate();
            ArrayInt indices    = arrayIntPool.allocate();
            /* try to triangulate the polygon. We might have a polygon that is degenerate and the triangulation fails. In that case, it is okay to not render anything.*/
            try {
                MathUtils.polygonTriangulate(polygon, vertices, indices);
            } catch (Exception e) {
                /* Probably the polygon has collapsed into a single point. */
                return;
            }

            Vector2 vertex = vectorsPool.allocate();
            for (int i = 0; i < vertices.size; i += 2) {
                float poly_x = vertices.get(i);
                float poly_y = vertices.get(i + 1);

                vertex.set(poly_x, poly_y);
                vertex.scl(scaleX, scaleY);
                vertex.rotateDeg(angleZ);
                vertex.add(x, y);

                verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
            }
            vectorsPool.free(vertex);

            for (int i = 0; i < indices.size - 2; i += 3) {
                indicesBuffer.put(startVertex + indices.get(i));
                indicesBuffer.put(startVertex + indices.get(i + 1));

                indicesBuffer.put(startVertex + indices.get(i + 1));
                indicesBuffer.put(startVertex + indices.get(i + 2));

                indicesBuffer.put(startVertex + indices.get(i + 2));
                indicesBuffer.put(startVertex + indices.get(i));
            }

            vertexIndex += vertices.size / 2;

            arrayFloatPool.free(vertices);
            arrayIntPool.free(indices);
        }
    }

    public void drawPolygonFilled(float[] polygon, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if ((vertexIndex + count) * VERTEX_SIZE > verticesBuffer.capacity()) flush();

        setMode(GL11.GL_TRIANGLES);

        scaleX *= MathUtils.cosDeg(angleY);
        scaleY *= MathUtils.cosDeg(angleX);

        ArrayFloat vertices = arrayFloatPool.allocate();
        ArrayInt indices = arrayIntPool.allocate();
        MathUtils.polygonTriangulate(polygon, vertices, indices);
        Vector2 vertex = vectorsPool.allocate();
        for (int i = 0; i < vertices.size; i += 2) {
            float poly_x = vertices.get(i);
            float poly_y = vertices.get(i + 1);

            vertex.set(poly_x, poly_y);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(angleZ);
            vertex.add(x, y);

            verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
        }
        vectorsPool.free(vertex);

        int startVertex = this.vertexIndex;
        for (int i = 0; i < indices.size; i ++) {
            indicesBuffer.put(startVertex + indices.get(i));
        }

        vertexIndex += count;
        arrayFloatPool.free(vertices);
        arrayIntPool.free(indices);
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
    /* TODO: implement a version of these methods with a transform. */

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

    public void drawCurveFilled_3(float stroke, int smoothness, final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values.length < 2) return;

        setMode(GL11.GL_TRIANGLES);

        final float s2 = 0.5f * stroke;

        Vector2 p0 = vectorsPool.allocate();
        Vector2 p1 = vectorsPool.allocate();
        Vector2 p2 = vectorsPool.allocate();

        Vector2 t0  = vectorsPool.allocate();
        Vector2 t1a = vectorsPool.allocate();
        Vector2 t1b = vectorsPool.allocate();
        Vector2 t2  = vectorsPool.allocate();

        Vector2 intersection = vectorsPool.allocate();
        Vector2 arm          = vectorsPool.allocate();

        /* iterate over all the anchors */
        for (int i = 1; i < values.length - 1; i++) {
            /* the tuple (pi_0, pi_1, pi_2) constitutes the anchor that we will extrude, triangulate and render */

            if (i == 1) p0.set(values[0]);
            else p0.set(values[i - 1]).add(values[i]).scl(0.5f);
            p1.set(values[i]);
            if (i == values.length - 2) p2.set(values[values.length - 1]);
            else p2.set(values[i]).add(values[i + 1]).scl(0.5f);

            float sign = MathUtils.areaTriangleSigned(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y) >= 0 ? 1 : -1;

            t0.set(p1).sub(p0).rotate90(1).nor().scl(sign * s2);
            t1a.set(p1).sub(p0).rotate90(1).nor().scl(sign * s2);
            t1b.set(p2).sub(p1).rotate90(1).nor().scl(sign * s2);
            t2.set(p2).sub(p1).rotate90(1).nor().scl(sign * s2);

            int result = MathUtils.segmentsIntersection(p0.x + t0.x, p0.y + t0.y, p1.x + t1a.x, p1.y + t1a.y,p1.x + t1b.x, p1.y + t1b.y, p2.x + t2.x, p2.y + t2.y, intersection);

            float angle = Vector2.angleBetweenDeg(t1a, t1b);
            float da = angle / smoothness;

            System.out.println(result);

            ArrayFloat vertices = arrayFloatPool.allocate();
            switch (result) {
                case 1:
                    vertices.add(p0.x + t0.x);
                    vertices.add(p0.y + t0.y);
                    vertices.add(p0.x - t0.x);
                    vertices.add(p0.y - t0.y);
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices.add(intersection.x);
                        vertices.add(intersection.y);
                        arm.set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1);
                        vertices.add(arm.x);
                        vertices.add(arm.y);
                    }
                    vertices.add(p2.x + t2.x);
                    vertices.add(p2.x + t2.y);
                    vertices.add(p2.x - t2.x);
                    vertices.add(p2.x - t2.y);
                    break;
                case 3:
                case 5:
                    MathUtils.segmentsIntersection(new Vector2(p0).add(t0), new Vector2(p1).add(t1a), new Vector2(p2).add(t2), new Vector2(p2).sub(t1b), intersection);
                    vertices.add(p0.x + t0.x);
                    vertices.add(p0.y + t0.y);
                    vertices.add(p0.x - t0.x);
                    vertices.add(p0.y - t0.y);
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices.add(intersection.x);
                        vertices.add(intersection.y);
                        arm.set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1);
                        vertices.add(arm.x);
                        vertices.add(arm.y);
                    }
                    vertices.add(intersection.x);
                    vertices.add(intersection.y);
                    vertices.add(p2.x - t2.x);
                    vertices.add(p2.x - t2.y);
                    break;
            }

            Array<Vector2> vertices2 = new Array<>();
            switch (result) {
                case 1:
                    vertices2.add(vectorsPool.allocate().set(p0).add(t0));
                    vertices2.add(vectorsPool.allocate().set(p0).sub(t0));
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices2.add(vectorsPool.allocate().set(intersection));
                        vertices2.add(vectorsPool.allocate().set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1));
                    }
                    vertices2.add(vectorsPool.allocate().set(p2).add(t2));
                    vertices2.add(vectorsPool.allocate().set(p2).sub(t2));
                    break;
                case 3:
                case 4:
                case 5:
                    MathUtils.segmentsIntersection(new Vector2(p0).add(t0), new Vector2(p1).add(t1a), new Vector2(p2).add(t2), new Vector2(p2).sub(t1b), intersection);
                    vertices2.add(vectorsPool.allocate().set(p0).add(t0));
                    vertices2.add(vectorsPool.allocate().set(p0).sub(t0));
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices2.add(vectorsPool.allocate().set(intersection));
                        vertices2.add(vectorsPool.allocate().set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1));
                    }
                    vertices2.add(vectorsPool.allocate().set(intersection));
                    vertices2.add(vectorsPool.allocate().set(p2).sub(t2));
                    break;
            }

            /* put vertices */
            for (int j = 0; j < vertices.size - 1; j += 2) {
                float vx = vertices.get(j);
                float vy = vertices.get(j + 1);
                //verticesBuffer.put(vx).put(vy).put(currentTint).put(0.5f).put(0.5f);
            }
            vertices.clear();

            for (Vector2 vertex : vertices2) {
                verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
            }

            final int startVertex = this.vertexIndex;

            for (int index = 0; index < vertices2.size - 2; index += 2) { // curve +  rounded corners
                indicesBuffer.put(startVertex + index + 0);
                indicesBuffer.put(startVertex + index + 1);
                indicesBuffer.put(startVertex + index + 2);
                indicesBuffer.put(startVertex + index + 2);
                indicesBuffer.put(startVertex + index + 1);
                indicesBuffer.put(startVertex + index + 3);
            }
            this.vertexIndex += vertices2.size;

            arrayFloatPool.free(vertices);

        }

        vectorsPool.free(p0);
        vectorsPool.free(p1);
        vectorsPool.free(p2);
        vectorsPool.free(t0);
        vectorsPool.free(t1a);
        vectorsPool.free(t1b);
        vectorsPool.free(t2);
        vectorsPool.free(arm);
    }

    public void drawCurveFilled_2(float stroke, int smoothness, final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values.length < 2) return;

        setMode(GL11.GL_TRIANGLES);

        final float s2 = 0.5f * stroke;

        Vector2 p0 = vectorsPool.allocate();
        Vector2 p1 = vectorsPool.allocate();
        Vector2 p2 = vectorsPool.allocate();

        Vector2 t0  = vectorsPool.allocate();
        Vector2 t1a = vectorsPool.allocate();
        Vector2 t1b = vectorsPool.allocate();
        Vector2 t2  = vectorsPool.allocate();

        Vector2 intersection = vectorsPool.allocate();
        Vector2 arm          = vectorsPool.allocate();

        /* iterate over all the anchors */
        for (int i = 1; i < values.length - 1; i++) {
            /* the tuple (pi_0, pi_1, pi_2) constitutes the anchor that we will extrude, triangulate and render */

            if (i == 1) p0.set(values[0]);
            else p0.set(values[i - 1]).add(values[i]).scl(0.5f);
            p1.set(values[i]);
            if (i == values.length - 2) p2.set(values[values.length - 1]);
            else p2.set(values[i]).add(values[i + 1]).scl(0.5f);

            float sign = MathUtils.areaTriangleSigned(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y) >= 0 ? 1 : -1;

            t0.set(p1).sub(p0).rotate90(1).nor().scl(sign * s2);
            t1a.set(p1).sub(p0).rotate90(1).nor().scl(sign * s2);
            t1b.set(p2).sub(p1).rotate90(1).nor().scl(sign * s2);
            t2.set(p2).sub(p1).rotate90(1).nor().scl(sign * s2);

            int result = MathUtils.segmentsIntersection(p0.x + t0.x, p0.y + t0.y, p1.x + t1a.x, p1.y + t1a.y,p1.x + t1b.x, p1.y + t1b.y, p2.x + t2.x, p2.y + t2.y, intersection);

            float angle = Vector2.angleBetweenDeg(t1a, t1b);
            float da = angle / smoothness;

            System.out.println(result);

            ArrayFloat vertices = arrayFloatPool.allocate();
            switch (result) {
                case 1:
                    vertices.add(p0.x + t0.x);
                    vertices.add(p0.y + t0.y);
                    vertices.add(p0.x - t0.x);
                    vertices.add(p0.y - t0.y);
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices.add(intersection.x);
                        vertices.add(intersection.y);
                        arm.set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1);
                        vertices.add(arm.x);
                        vertices.add(arm.y);
                    }
                    vertices.add(p2.x + t2.x);
                    vertices.add(p2.x + t2.y);
                    vertices.add(p2.x - t2.x);
                    vertices.add(p2.x - t2.y);
                    break;
                case 3:
                case 5:
                    MathUtils.segmentsIntersection(new Vector2(p0).add(t0), new Vector2(p1).add(t1a), new Vector2(p2).add(t2), new Vector2(p2).sub(t1b), intersection);
                    vertices.add(p0.x + t0.x);
                    vertices.add(p0.y + t0.y);
                    vertices.add(p0.x - t0.x);
                    vertices.add(p0.y - t0.y);
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices.add(intersection.x);
                        vertices.add(intersection.y);
                        arm.set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1);
                        vertices.add(arm.x);
                        vertices.add(arm.y);
                    }
                    vertices.add(intersection.x);
                    vertices.add(intersection.y);
                    vertices.add(p2.x - t2.x);
                    vertices.add(p2.x - t2.y);
                    break;
            }

            Array<Vector2> vertices2 = new Array<>();
            switch (result) {
                case 1:
                    vertices2.add(vectorsPool.allocate().set(p0).add(t0));
                    vertices2.add(vectorsPool.allocate().set(p0).sub(t0));
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices2.add(vectorsPool.allocate().set(intersection));
                        vertices2.add(vectorsPool.allocate().set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1));
                    }
                    vertices2.add(vectorsPool.allocate().set(p2).add(t2));
                    vertices2.add(vectorsPool.allocate().set(p2).sub(t2));
                    break;
                case 3:
                case 4:
                case 5:
                    MathUtils.segmentsIntersection(new Vector2(p0).add(t0), new Vector2(p1).add(t1a), new Vector2(p2).add(t2), new Vector2(p2).sub(t1b), intersection);
                    vertices2.add(vectorsPool.allocate().set(p0).add(t0));
                    vertices2.add(vectorsPool.allocate().set(p0).sub(t0));
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices2.add(vectorsPool.allocate().set(intersection));
                        vertices2.add(vectorsPool.allocate().set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1));
                    }
                    vertices2.add(vectorsPool.allocate().set(intersection));
                    vertices2.add(vectorsPool.allocate().set(p2).sub(t2));
                    break;
            }

            /* put vertices */
            for (int j = 0; j < vertices.size - 1; j += 2) {
                float vx = vertices.get(j);
                float vy = vertices.get(j + 1);
                //verticesBuffer.put(vx).put(vy).put(currentTint).put(0.5f).put(0.5f);
            }
            vertices.clear();

            for (Vector2 vertex : vertices2) {
                verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
            }

            final int startVertex = this.vertexIndex;

            for (int index = 0; index < vertices2.size - 2; index += 2) { // curve +  rounded corners
                indicesBuffer.put(startVertex + index + 0);
                indicesBuffer.put(startVertex + index + 1);
                indicesBuffer.put(startVertex + index + 2);
                indicesBuffer.put(startVertex + index + 2);
                indicesBuffer.put(startVertex + index + 1);
                indicesBuffer.put(startVertex + index + 3);
            }
            this.vertexIndex += vertices2.size;

            arrayFloatPool.free(vertices);

        }

        vectorsPool.free(p0);
        vectorsPool.free(p1);
        vectorsPool.free(p2);
        vectorsPool.free(t0);
        vectorsPool.free(t1a);
        vectorsPool.free(t1b);
        vectorsPool.free(t2);
        vectorsPool.free(arm);
    }

    public void drawCurveFilled(float stroke, int smoothness, final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values.length < 2) return;

        setMode(GL11.GL_TRIANGLES);

        final float s2 = 0.5f * stroke;

        Vector2 p0 = vectorsPool.allocate();
        Vector2 p1 = vectorsPool.allocate();
        Vector2 p2 = vectorsPool.allocate();

        Vector2 t0  = vectorsPool.allocate();
        Vector2 t1a = vectorsPool.allocate();
        Vector2 t1b = vectorsPool.allocate();
        Vector2 t2  = vectorsPool.allocate();

        Vector2 intersection = vectorsPool.allocate();
        Vector2 arm          = vectorsPool.allocate();

        /* iterate over all the anchors */
        for (int i = 1; i < values.length - 1; i++) {
            /* the tuple (pi_0, pi_1, pi_2) constitutes the anchor that we will extrude, triangulate and render */

            if (i == 1) p0.set(values[0]);
            else p0.set(values[i - 1]).add(values[i]).scl(0.5f);
            p1.set(values[i]);
            if (i == values.length - 2) p2.set(values[values.length - 1]);
            else p2.set(values[i]).add(values[i + 1]).scl(0.5f);

            float sign = MathUtils.areaTriangleSigned(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y) >= 0 ? 1 : -1;

            t0.set(p1).sub(p0).rotate90(1).nor().scl(sign * s2);
            t1a.set(p1).sub(p0).rotate90(1).nor().scl(sign * s2);
            t1b.set(p2).sub(p1).rotate90(1).nor().scl(sign * s2);
            t2.set(p2).sub(p1).rotate90(1).nor().scl(sign * s2);

            int result = MathUtils.segmentsIntersection(p0.x + t0.x, p0.y + t0.y, p1.x + t1a.x, p1.y + t1a.y,p1.x + t1b.x, p1.y + t1b.y, p2.x + t2.x, p2.y + t2.y, intersection);

            float angle = Vector2.angleBetweenDeg(t1a, t1b);
            float da = angle / smoothness;

            System.out.println(result);

            ArrayFloat vertices = arrayFloatPool.allocate();
            Array<Vector2> vertices2 = new Array<>();


            switch (result) {
                case 1:
                    vertices.add(p0.x + t0.x);
                    vertices.add(p0.y + t0.y);
                    vertices.add(p0.x - t0.x);
                    vertices.add(p0.y - t0.y);
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices.add(intersection.x);
                        vertices.add(intersection.y);
                        arm.set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1);
                        vertices.add(arm.x);
                        vertices.add(arm.y);
                    }
                    vertices.add(p2.x + t2.x);
                    vertices.add(p2.x + t2.y);
                    vertices.add(p2.x - t2.x);
                    vertices.add(p2.x - t2.y);
                    break;
                case 3:
                case 5:
                    MathUtils.segmentsIntersection(new Vector2(p0).add(t0), new Vector2(p1).add(t1a), new Vector2(p2).add(t2), new Vector2(p2).sub(t1b), intersection);
                    vertices.add(p0.x + t0.x);
                    vertices.add(p0.y + t0.y);
                    vertices.add(p0.x - t0.x);
                    vertices.add(p0.y - t0.y);
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices.add(intersection.x);
                        vertices.add(intersection.y);
                        arm.set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1);
                        vertices.add(arm.x);
                        vertices.add(arm.y);
                    }
                    vertices.add(intersection.x);
                    vertices.add(intersection.y);
                    vertices.add(p2.x - t2.x);
                    vertices.add(p2.x - t2.y);
                    break;
            }

            switch (result) {
                case 1:
                    vertices2.add(vectorsPool.allocate().set(p0).add(t0));
                    vertices2.add(vectorsPool.allocate().set(p0).sub(t0));
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices2.add(vectorsPool.allocate().set(intersection));
                        vertices2.add(vectorsPool.allocate().set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1));
                    }
                    vertices2.add(vectorsPool.allocate().set(p2).add(t2));
                    vertices2.add(vectorsPool.allocate().set(p2).sub(t2));
                    break;
                case 3:
                case 5:
                    MathUtils.segmentsIntersection(new Vector2(p0).add(t0), new Vector2(p1).add(t1a), new Vector2(p2).add(t2), new Vector2(p2).sub(t1b), intersection);
                    vertices2.add(vectorsPool.allocate().set(p0).add(t0));
                    vertices2.add(vectorsPool.allocate().set(p0).sub(t0));
                    for (int j = 0; j < smoothness + 1; j++) {
                        vertices2.add(vectorsPool.allocate().set(intersection));
                        vertices2.add(vectorsPool.allocate().set(-t1a.x, -t1a.y).rotateDeg(sign * da * j).add(p1));
                    }
                    vertices2.add(vectorsPool.allocate().set(intersection));
                    vertices2.add(vectorsPool.allocate().set(p2).sub(t2));
                    break;
            }


            /* put vertices */
            for (int j = 0; j < vertices.size - 1; j += 2) {
                float vx = vertices.get(j);
                float vy = vertices.get(j + 1);
                //verticesBuffer.put(vx).put(vy).put(currentTint).put(0.5f).put(0.5f);
            }
            vertices.clear();

            for (Vector2 vertex : vertices2) {
                verticesBuffer.put(vertex.x).put(vertex.y).put(currentTint).put(0.5f).put(0.5f);
            }

            final int startVertex = this.vertexIndex;
            for (int index = 0; index < vertices2.size - 2; index += 2) { // curve +  rounded corners
                indicesBuffer.put(startVertex + index + 0);
                indicesBuffer.put(startVertex + index + 1);
                indicesBuffer.put(startVertex + index + 2);
                indicesBuffer.put(startVertex + index + 2);
                indicesBuffer.put(startVertex + index + 1);
                indicesBuffer.put(startVertex + index + 3);
            }
            this.vertexIndex += vertices2.size;

            arrayFloatPool.free(vertices);

        }

        vectorsPool.free(p0);
        vectorsPool.free(p1);
        vectorsPool.free(p2);
        vectorsPool.free(t0);
        vectorsPool.free(t1a);
        vectorsPool.free(t1b);
        vectorsPool.free(t2);
        vectorsPool.free(arm);
    }

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

    public void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
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

    // TODO: delete this after taking care of textures.

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