package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.Array;
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
public class Renderer2D implements Resource {

    private static final int BATCH_SIZE = 2000;
    private static final int VERTEX_SIZE = 6;
    private static final int BATCH_TRIANGLES_CAPACITY = BATCH_SIZE * 2;
    private static final int TRIANGLE_INDICES = 3;
    private static final int TEXTURES_CAPACITY = 4;

    // state management
    private CameraLens lens;
    private final ShaderProgram defaultShader;
    private ShaderProgram currentShader;
    private Array<Texture> usedTextures = new Array<>(TEXTURES_CAPACITY);
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

    public Renderer2D() {
        // TODO: for debugging only; later, inline the shader source code here.
        this.defaultShader = new ShaderProgram(AssetUtils.getFileContent("assets/shaders/default-2d-new-3.vert"), AssetUtils.getFileContent("assets/shaders/default-2d-new-3.frag"));
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        {
            vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
            int vertexSizeBytes = VERTEX_SIZE * Float.BYTES;
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSizeBytes, Float.BYTES * 2L);
            GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, true, vertexSizeBytes, Float.BYTES * 3L);
            GL20.glVertexAttribPointer(3, 1, GL11.GL_FLOAT, true, vertexSizeBytes, Float.BYTES * 5L);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL20.glEnableVertexAttribArray(3);
            ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
        }
        GL30.glBindVertexArray(0);
    }

    public void begin(CameraLens lens) {
        if (drawing) throw new IllegalStateException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.drawCalls = 0;
        this.lens = lens;
        this.currentShader = null;
        drawing = true;
    }

    /** Push primitives: TextureRegion, Shape, Light **/
    // TODO: libGDX PolygonSpriteBatch.java: 772
    public void pushTexture(Texture texture, float u1, float v1, float u2, float v2, float offsetX, float offsetY, float x, float y, float angle, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (indicesBuffer.position() + triangleIndex + 6 > indicesBuffer.capacity() || verticesBuffer.position() + vertexIndex + 24 > verticesBuffer.capacity()) flush();
        useShader(shader);
        useTexture(texture);
        useCustomAttributes(customAttributes);
    }

    public void pushShape() {

    }

    public void pushLight() {

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
        if (usedTextures.contains(texture, true)) return;
        if (usedTextures.size >= TEXTURES_CAPACITY) {
            flush();
            usedTextures.clear();
        }
        usedTextures.add(texture);
        TextureBinder.bind(texture);
    }

    private void useCustomAttributes(HashMap<String, Object> customAttributes) {
        //flush();

    }

    // contains the logic that sends everything to the GPU for rendering
    private void flush() {
        if (this.vertexIndex == 0) return;
        ShaderProgramBinder.bind(currentShader);
        currentShader.bindUniform("u_textures[0]", usedTextures.get(0, null));
        currentShader.bindUniform("u_textures[1]", usedTextures.get(1, null));
        currentShader.bindUniform("u_textures[2]", usedTextures.get(2, null));
        currentShader.bindUniform("u_textures[3]", usedTextures.get(3, null));

        GL30.glBindVertexArray(vao);
        {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indicesBuffer);

            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL20.glEnableVertexAttribArray(3);
            GL11.glDrawElements(GL11.GL_TRIANGLES, indicesBuffer.position(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(3);
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
        if (!drawing) throw new IllegalStateException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
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
