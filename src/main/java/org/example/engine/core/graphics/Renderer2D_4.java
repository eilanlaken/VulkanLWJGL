package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.memory.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

// The Cherno:
// https://github.com/TheCherno/Hazel/blob/master/Hazelnut/assets/shaders/Renderer2D_Quad.glsl
public class Renderer2D_4 implements Resource {

    public static final float DEFAULT_COLOR = new Color(1,1,1,1).toFloatBits();

    private static final int BATCH_SIZE = 2000;
    private static final int VERTEX_SIZE = 5;
    private static final int BATCH_TRIANGLES_CAPACITY = BATCH_SIZE * 2;
    private static final int TRIANGLE_INDICES = 3;

    // state management
    private CameraLens_dep lens;
    private final ShaderProgram defaultShader;
    private ShaderProgram currentShader;
    private Texture lastTexture;
    private HashMap<String, Object> currentCustomAttributes;

    private boolean drawing = false;
    private int vertexIndex = 0;
    private int triangleIndex = 0;
    private final int vao;
    private int vbo, ebo;
    private FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(BATCH_SIZE * VERTEX_SIZE);
    private IntBuffer indicesBuffer = BufferUtils.createIntBuffer(BATCH_TRIANGLES_CAPACITY * TRIANGLE_INDICES);

    // profiling
    private int drawCalls = 0;

    public Renderer2D_4() {
        // TODO: for debugging only; later, inline the shader source code here.
        this.defaultShader = new ShaderProgram(AssetUtils.getFileContent("assets/shaders/default-2d-new-4.vert"), AssetUtils.getFileContent("assets/shaders/default-2d-new-4.frag"));
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

    public void begin(CameraLens_dep lens) {
        if (drawing) throw new IllegalStateException("Already in a drawing state; Must call " + Renderer2D_4.class.getSimpleName() + ".end() before calling begin().");
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND); // TODO: make camera attributes, get as additional parameter to begin()
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // TODO: make camera attributes, get as additional parameter to begin()
        this.drawCalls = 0;
        this.lens = lens;
        this.currentShader = null;
        drawing = true;
    }

    /** Push primitives: TextureRegion, Shape, Light **/
    // TODO: libGDX PolygonSpriteBatch.java: 772
    @Deprecated public void pushTexture(Texture texture, Color tint, float ui, float vi, float uf, float vf, float offsetX, float offsetY, float pw, float ph, float ow, float oh, float x, float y, float angle, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (indicesBuffer.position() + triangleIndex + 6 > indicesBuffer.capacity() || verticesBuffer.position() + vertexIndex + 24 > verticesBuffer.capacity()) flush();
        useShader(shader);
        useTexture(texture);
        useCustomAttributes(customAttributes);

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
        triangleIndex += 6;

        // put vertices
        float x1, y1;
        float x2, y2;
        float x3, y3;
        float x4, y4;

        x1 = x2 = scaleX * (offsetX - ow / 2);
        x3 = x4 = x1 + scaleX * pw;
        //y1 = y2 =

        float t = tint == null ? DEFAULT_COLOR : tint.toFloatBits();
        verticesBuffer
                .put(-0.5f).put(0.5f).put(t).put(ui).put(vi)
                .put(-0.5f).put(-0.5f).put(t).put(ui).put(vf)
                .put(0.5f).put(-0.5f).put(t).put(uf).put(vf)
                .put(0.5f).put(0.5f).put(t).put(uf).put(vi)
        ;
        vertexIndex += 20;
    }

    // TODO: also consider angleX and angleY
    public void pushTextureRegion(TextureRegion region, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (indicesBuffer.position() + triangleIndex + 6 > indicesBuffer.capacity() || verticesBuffer.position() + vertexIndex + 24 > verticesBuffer.capacity()) flush();

        final Texture texture = region.texture;
        final float ui = region.u;
        final float vi = region.v;
        final float uf = region.u2;
        final float vf = region.v2;
        final float offsetX = region.offsetX;
        final float offsetY = region.offsetY;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidth = region.originalWidth;
        final float originalHeight = region.originalHeight;
        final float packedWidthHalf = region.packedWidthHalf;
        final float packedHeightHalf = region.packedHeightHalf;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        useShader(shader);
        useTexture(texture);
        useCustomAttributes(customAttributes);

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
        triangleIndex += 6;

        // put vertices
        float t = tint == null ? DEFAULT_COLOR : tint.toFloatBits();
        verticesBuffer
                .put(-0.5f).put(0.5f).put(t).put(ui).put(vi)
                .put(-0.5f).put(-0.5f).put(t).put(ui).put(vf)
                .put(0.5f).put(-0.5f).put(t).put(uf).put(vf)
                .put(0.5f).put(0.5f).put(t).put(uf).put(vi)
        ;
        vertexIndex += 20;
    }

    public void pushShape() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void pushLight() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /** Swap Operations **/
    private void useShader(ShaderProgram shader) {
        if (shader == null) shader = defaultShader;
        if (currentShader != shader) {
            flush();
            ShaderProgramBinder.bind(shader);
            // TODO: bind camera
            //shader.bindUniform("u_camera_combined", lens.combined);
        }
        currentShader = shader;
    }

    private void useTexture(Texture texture) {
        if (lastTexture != texture) {
            flush();
        }
        lastTexture = texture;
        TextureBinder.bind(lastTexture);
    }

    private void useCustomAttributes(HashMap<String, Object> customAttributes) {
        //flush();

    }

    // contains the logic that sends everything to the GPU for rendering
    private void flush() {
        if (verticesBuffer.position() == 0) return;
        ShaderProgramBinder.bind(currentShader);
        currentShader.bindUniform("u_texture", lastTexture);

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
            GL11.glDrawElements(GL11.GL_TRIANGLES, indicesBuffer.limit(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
        }
        GL30.glBindVertexArray(0);
        indicesBuffer.position(0);
        verticesBuffer.position(0);
        vertexIndex = 0;
        triangleIndex = 0;
        drawCalls++;
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Called " + Renderer2D_4.class.getSimpleName() + ".end() without calling " + Renderer2D_4.class.getSimpleName() + ".begin() first.");
        flush();
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        drawing = false;
    }

    public int getDrawCalls() {
        return drawCalls;
    }

    @Override
    public void free() {
        // free shader
        // free dynamic mesh
    }
}
