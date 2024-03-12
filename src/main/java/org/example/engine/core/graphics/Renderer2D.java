package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Renderer2D implements Resource {

    private final ShaderProgram defaultShader;
    private ShaderProgram currentShader;
    private CameraLens lens;
    private Texture lastTexture = null;
    private float invTexWidth = 0;
    private float invTexHeight = 0;
    private boolean drawing = false;
    private int drawCalls = 0;

    private int vertexIndex = 0;
    private int triangleIndex = 0;
    private float[] positions;
    private float[] colors;
    private float[] textureCoords0s;
    private short[] triangles;
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
    }

    public void begin(CameraLens lens) {
        if (drawing) throw new IllegalStateException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        this.lens = lens;
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        drawing = true;
    }

    // PolygonSpriteBatch.java --> line 360
    // public void draw (Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
    public void pushTexture(Texture texture, Color color,
                            float u1, float v1,
                            float u2, float v2,
                            ShaderProgram specialShader,
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

    private void drawCurrentBatch() {
        // contains the logic that send everything to the GPU for rendering

        // render

    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
        drawCurrentBatch();
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
