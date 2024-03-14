package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// TODO: pretty good solution here:
//  TODO: http://forum.lwjgl.org/index.php?topic=5789.0
// TODO: orphaning - multi buffering:
// TODO: https://www.cppstories.com/2015/01/persistent-mapped-buffers-in-opengl/#persistence
// Note: glBufferData invalidates and reallocates the whole buffer. Use glBufferSubData to only update the data inside.
public class SceneRendering2D_5 extends WindowScreen {

    private ShaderProgram shader;

    // create and modify quad dynamically
    int vao;
    FloatBuffer floatBuffer = MemoryUtils.createFloatBuffer(8000);
    float[] vertices = new float[8000];
    float size = 0;
    int BATCH_VERTICES = 8000;
    int VERTEX_SIZE = 5;

    int vbo;

    public SceneRendering2D_5() {

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

    }

    @Override
    public void show() {


        int[] indices = {
                0,1,3,	//Top left triangle (V0,V1,V3)
                3,1,2	//Bottom right triangle (V3,V1,V2)
        };

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 1024, GL15.GL_DYNAMIC_DRAW);

        int vertexSize = 3 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSize, 0);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSize, Float.BYTES * 2L);

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
        // change the fucking buffer
        float x = 0;
        float c0 = new Color(0f,1,0,1).toFloatBits();
        float c1 = new Color(1f,0,0,1).toFloatBits();
        float c2 = new Color(0f,0,1,0.1f).toFloatBits();

        float[] vertices = {
                -0.5f,0.5f, c0,
                -0.5f,-0.5f, c1,
                0.5f,-0.5f, c2,
                0.5f,0.5f, c1,
        };

        float change = Keyboard.isKeyPressed(Keyboard.Key.Q) ? 0.01f : 0;

        floatBuffer.put(-0.5f + change).put(0.5f).put(c0)
                        .put(-0.5f).put(-0.5f).put(c1)
                        .put(0.5f).put(-0.5f).put(c2)
                        .put(0.5f).put(0.5f).put(c1);
        floatBuffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, floatBuffer);


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,1,1,0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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
