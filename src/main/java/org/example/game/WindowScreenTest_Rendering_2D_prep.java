package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class WindowScreenTest_Rendering_2D_prep extends WindowScreen {

    private ShaderProgram shader;
    private ComponentTransform transform;
    private ComponentGraphicsCamera camera;

    // create and modify quad dynamically
    int vao;

    public WindowScreenTest_Rendering_2D_prep() {

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

        this.camera = Component.Factory.createCamera2D(GraphicsUtils.getWindowWidth(),GraphicsUtils.getWindowHeight());

    }

    @Override
    public void show() {
        float[] vertices = {
                -0.5f,0.5f,	//V0
                -0.5f,-0.5f,	//V1
                0.5f,-0.5f,	//V2
                0.5f,0.5f		//V3
        };

        int[] indices = {
                0,1,3,	//Top left triangle (V0,V1,V3)
                3,1,2	//Bottom right triangle (V3,V1,V2)
        };

        float[] colors = {
                Color.asSingleFloat(new Color(1,0,0,1)),
                Color.asSingleFloat(new Color(0,1,0,1)),
                Color.asSingleFloat(new Color(0,0,0,1)),
                Color.asSingleFloat(new Color(0,0,0,1)),
        };

        vao = ModelBuilder.loadToVAO(vertices, colors, indices);
    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,1,0,0);

        shader.bind();
        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(ModelVertexAttribute.POSITION_2D.slot);
        GL20.glEnableVertexAttribArray(ModelVertexAttribute.COLOR_PACKED.slot);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(ModelVertexAttribute.POSITION_2D.slot);
        GL20.glDisableVertexAttribArray(ModelVertexAttribute.COLOR_PACKED.slot);

        GL30.glBindVertexArray(0);
        shader.unbind();
    }

    @Override
    public void resize(int width, int height) { }


    @Override
    public void hide() {
        shader.free();
    }

    @Override
    public void free() {

    }

}
