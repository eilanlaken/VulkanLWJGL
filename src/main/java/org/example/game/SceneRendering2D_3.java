package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SceneRendering2D_3 extends WindowScreen {

    private ShaderProgram shader;
    private ComponentTransform transform;
    private ComponentGraphicsCamera camera;

    // create and modify quad dynamically
    int vao;

    public SceneRendering2D_3() {

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

        this.camera = Component.Factory.createCamera2D(GraphicsUtils.getWindowWidth(),GraphicsUtils.getWindowHeight());

    }

    @Override
    public void show() {

//        float[] vertices = {
//                -0.5f,0.5f, 1,0,0,1,
//                -0.5f,-0.5f, 0,1,0,1,
//                0.5f,-0.5f, 0,0,0,1,
//                0.5f,0.5f, 0,0,0,1
//        };

        float[] vertices = {
                -0.5f,0.5f, Color.asSingleFloat(new Color(1,0,0,1)),
                -0.5f,-0.5f, Color.asSingleFloat(new Color(1,1,0,1)),
                0.5f,-0.5f, Color.asSingleFloat(new Color(1,0,0,1)),
                0.5f,0.5f, Color.asSingleFloat(new Color(1,0,0,1)),
        };

        int[] indices = {
                0,1,3,	//Top left triangle (V0,V1,V3)
                3,1,2	//Bottom right triangle (V3,V1,V2)
        };



        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer interleavedBuffer = BufferUtils.createFloatBuffer(vertices.length);
        interleavedBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, interleavedBuffer, GL15.GL_STATIC_DRAW);

        int vertexSize = 2 * Float.BYTES + Integer.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSize, 0);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_INT, true, vertexSize, Float.BYTES * 2);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);


        int ebo = GL15.glGenBuffers();
        IntBuffer indicesBuffer = MemoryUtils.store(indices);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,1,1,0);

        ShaderProgramBinder.bind(shader);
        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(ModelVertexAttribute.POSITION_2D.slot);
        GL20.glDisableVertexAttribArray(ModelVertexAttribute.COLOR_PACKED.slot);
        GL30.glBindVertexArray(0);
        ShaderProgramBinder.unbind();
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
