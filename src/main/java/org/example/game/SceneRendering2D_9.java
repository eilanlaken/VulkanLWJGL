package org.example.game;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// TODO: pretty good solution here:
//  TODO: http://forum.lwjgl.org/index.php?topic=5789.0
// TODO: orphaning - multi buffering:
// TODO: https://www.cppstories.com/2015/01/persistent-mapped-buffers-in-opengl/#persistence
// Note: glBufferData invalidates and reallocates the whole buffer. Use glBufferSubData to only update the data inside.
// https://stackoverflow.com/questions/72648980/opengl-sampler2d-array
public class SceneRendering2D_9 extends WindowScreen {

    private ShaderProgram shader;

    // create and modify quad dynamically
    int vao;
    FloatBuffer floatBuffer = MemoryUtils.createFloatBuffer(8000);
    Texture texture = AssetStore.get("assets/textures/yellowSquare.png");
    Texture texture2 = AssetStore.get("assets/textures/pattern2.png");
    Texture texture3 = AssetStore.get("assets/textures/redGreenHalf.png");

    int vbo;

    public SceneRendering2D_9() {

        System.out.println("max fragment textures: " + GraphicsUtils.getMaxFragmentShaderTextureUnits());
        System.out.println("max textures: " + GraphicsUtils.getMaxBoundTextureUnits());

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-new-3.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-new-3.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

    }

    @Override
    public void show() {

        float c0 = new Color(0f,1,1,1).toFloatBits();
        float c1 = new Color(1f,1,0,1).toFloatBits();
        float c2 = new Color(1f,1,1,0.1f).toFloatBits();

        float[] vertices = {
                -0.5f,0.5f, c0, 0, 0, 1,
                -0.5f,-0.5f, c0, 0, 1, 1,
                0.5f,-0.5f, c0, 1, 1, 1,
                0.5f,0.5f, c0, 1, 0, 1,

                -1.5f,-0.5f, c0, 0, 0, 0,
                -1.5f,-1.5f, c0, 0, 1, 0,
                -0.5f,-1.5f, c0, 1, 1, 0,
                -0.5f,-0.5f, c0, 1, 0, 0,

                0.5f,1.5f, c0, 0, 0, 2,
                0.5f,0.5f, c0, 0, 1, 2,
                1.5f,0.5f, c0, 1, 1, 2,
                1.5f,1.5f, c0, 1, 0, 2,
        };

        int[] indices = {
                0,1,3,	//Top left triangle (V0,V1,V3)
                3,1,2,	//Bottom right triangle (V3,V1,V2)

                4,5,7,
                7,5,6,

                8,9,11,
                11,9,10
        };

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        //int vertexSize = 9 * Float.BYTES;
        int vertexSize = 6 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSize, 0);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSize, Float.BYTES * 2L);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, true, vertexSize, Float.BYTES * 3L);
        GL20.glVertexAttribPointer(3, 1, GL11.GL_FLOAT, true, vertexSize, Float.BYTES * 5L);


        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);


        int ebo = GL15.glGenBuffers();
        IntBuffer indicesBuffer = MemoryUtils.store(indices);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);



    }


    @Override
    protected void refresh() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, floatBuffer);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,1,0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderProgramBinder.bind(shader);
        shader.bindUniform("u_textures[0]", texture);
        shader.bindUniform("u_textures[1]", texture2);
        shader.bindUniform("u_textures[2]", texture3);
        shader.bindUniform("x", 0.3f);



        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        GL11.glDrawElements(GL11.GL_TRIANGLES, 18, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);

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
