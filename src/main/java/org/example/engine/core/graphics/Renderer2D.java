package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;

// The Cherno:
// https://github.com/TheCherno/Hazel/blob/master/Hazelnut/assets/shaders/Renderer2D_Quad.glsl
public class Renderer2D implements Resource {

    private static final int BATCH_VERTICES_CAPACITY = 4000;
    private static final int VERTEX_SIZE = 5;
    private static final int BATCH_TRIANGLES_CAPACITY = BATCH_VERTICES_CAPACITY * 2;

    private final ShaderProgram defaultShader;
    private ShaderProgram currentShader;
    private ShaderProgram lastShader;
    private HashMap<String, Object> currentCustomAttributes;
    private CameraLens lens;
    private Texture lastTexture = null;
    private float invTexWidth = 0;
    private float invTexHeight = 0;
    private boolean drawing = false;
    private int drawCalls = 0;
    private int shaderSwaps = 0;
    private int textureSwaps = 0;

    private int vertexIndex = 0;
    private int triangleIndex = 0;
    private final int vaoId;
    private final ArrayInt vbos = new ArrayInt();

    //private float[] positions;
    //private float[] colors;
    //private float[] textureCoords0s;
    // try with interleaving first
    private float[] vertices;
    private short[] triangleIndices;
    // VertexBufferObjectWithVAO

    public Renderer2D() {
        this(null,null);
    }

    public Renderer2D(final String defaultVertexShader, final String defaultFragmentShader) {
        // TODO: for debugging only; later, inline the shader source code here.
        if (defaultVertexShader == null || defaultFragmentShader == null)
            this.defaultShader = new ShaderProgram(AssetUtils.getFileContent("assets/shaders/default-2d.vert"),
                    AssetUtils.getFileContent("assets/shaders/default-2d.frag"));
        else this.defaultShader = new ShaderProgram(defaultVertexShader, defaultFragmentShader);
        this.vaoId = GL30.glGenVertexArrays();

        //this.positions = new float[BATCH_SIZE * 2];
        //this.colors = new float[BATCH_SIZE * 4];
        //this.textureCoords0s = new float[BATCH_SIZE * 2];
        this.vertices = new float[BATCH_VERTICES_CAPACITY * 5]; // x,y,color (as single float, auto unpacked by opengl),u,v
        this.triangleIndices = new short[BATCH_TRIANGLES_CAPACITY * 3];
    }

    public void begin(CameraLens lens) {
        if (drawing) throw new IllegalStateException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        this.drawCalls = 0;
        this.shaderSwaps = 0;
        this.lens = lens;
        drawing = true;
    }

    // PolygonSpriteBatch.java --> line 360
    // public void draw (Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
    public void pushTexture(Texture texture, Color color,
                            float u1, float v1,
                            float u2, float v2,
                            ShaderProgram customShader, HashMap<String, Object> customAttributes,
                            float pivotX, float pivotY,
                            float x, float y, float z,
                            float angleX, float angleY, float angleZ,
                            float scaleX, float scaleY) {

    }

    public void pushTextureRegion() {

    }

    public void pushShape() {

    }

    public void pushLight() {

    }

    private void swapTextures() {

    }

    private void swapShaders() {

    }

    private void swapCustomAttributes() {

    }

    // contains the logic that sends everything to the GPU for rendering
    private void flush() {
        if (this.vertexIndex == 0) return;
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        // perform shader switch
        if (currentShader != lastShader) {
            ShaderProgramBinder.bind(currentShader);
            lastShader = currentShader;
            currentShader.bindUniform("u_camera_combined", lens.combined);
            currentShader.bindUniform("u_texture", lastTexture);
            if (currentShader != defaultShader) currentShader.bindUniforms(currentCustomAttributes);
            shaderSwaps++;
        }

        GL30.glBindVertexArray(vaoId);
        {
            GL20.glEnableVertexAttribArray(ModelVertexAttribute.POSITION_3D.slot);
            GL20.glEnableVertexAttribArray(ModelVertexAttribute.COLOR.slot);
            GL20.glEnableVertexAttribArray(ModelVertexAttribute.TEXTURE_COORDINATES0.slot);
            GL11.glDrawElements(GL11.GL_TRIANGLES, triangleIndex, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(ModelVertexAttribute.TEXTURE_COORDINATES0.slot);
            GL20.glDisableVertexAttribArray(ModelVertexAttribute.COLOR.slot);
            GL20.glDisableVertexAttribArray(ModelVertexAttribute.POSITION_3D.slot);
        }
        GL30.glBindVertexArray(0);
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

    public int getShaderSwaps() {
        return shaderSwaps;
    }

    @Override
    public void free() {
        // free shader
        // free dynamic mesh
    }
}
