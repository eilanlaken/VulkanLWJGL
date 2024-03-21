package org.example.game;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
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
// libGDX PolygonSpriteBatch.java line 772 draw()
public class SceneRendering2D_11 extends WindowScreen {

    Texture[] texturesToBind = new Texture[16];
    private ShaderProgram shader;

    int vao;
    int vbo;
    int ebo;
    FloatBuffer verticesBuffer = MemoryUtils.createFloatBuffer(2000 * 6);
    IntBuffer triangleIndicesBuffer = BufferUtils.createIntBuffer(2000 * 2 * 3);

    Texture texture = AssetStore.get("assets/textures/yellowSquare.png");
    Texture texture2 = AssetStore.get("assets/textures/pattern2.png");
    Texture texture3 = AssetStore.get("assets/textures/redGreenHalf.png");
    float c0 = new Color(1f,0.2f,1,0.8f).toFloatBits();

    public SceneRendering2D_11() {

        System.out.println("max fragment textures: " + GraphicsUtils.getMaxFragmentShaderTextureUnits());
        System.out.println("max textures: " + GraphicsUtils.getMaxBoundTextureUnits());

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-new-3.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-new-3.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

    }

    @Override
    public void show() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        {
            vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 1024 * 6, GL15.GL_DYNAMIC_DRAW);
            int vertexSize = 6 * Float.BYTES;
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSize, 0);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSize, Float.BYTES * 2L);
            GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, true, vertexSize, Float.BYTES * 3L);
            GL20.glVertexAttribPointer(3, 1, GL11.GL_FLOAT, true, vertexSize, Float.BYTES * 5L);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL20.glEnableVertexAttribArray(3);

            ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, 1024 * 2 * 3, GL15.GL_DYNAMIC_DRAW);
        }
        GL30.glBindVertexArray(0);
    }


    @Override
    protected void refresh() {

        // update vbos
        float change = Keyboard.isKeyPressed(Keyboard.Key.Q) ? 0.01f : 0;

        verticesBuffer
                .put(-0.5f + change).put(0.5f).put(c0).put(0).put(0).put(1)
                .put(-0.5f).put(-0.5f).put(c0).put(0).put(1).put(1)
                .put(0.5f).put(-0.5f).put(c0).put(1).put(1).put(1)
                .put(0.5f).put(0.5f).put(c0).put(1).put(0).put(1)

                .put(-1.5f + change).put(-0.5f).put(c0).put(0).put(0).put(0)
                .put(-1.5f).put(-1.5f).put(c0).put(0).put(1).put(0)
                .put(-0.5f).put(-1.5f).put(c0).put(1).put(1).put(0)
                .put(-0.5f).put(-0.5f).put(c0).put(1).put(0).put(0)

                .put(0.5f + change).put(1.5f).put(c0).put(0).put(0).put(2)
                .put(0.5f).put(0.5f).put(c0).put(0).put(1).put(2)
                .put(1.5f).put(0.5f).put(c0).put(1).put(1).put(2)
                .put(1.5f).put(1.5f).put(c0).put(1).put(0).put(2)
                ;
        verticesBuffer.flip();

        triangleIndicesBuffer
                .put(0).put(1).put(3)
                .put(3).put(1).put(2)

                .put(4).put(5).put(7)
                .put(7).put(5).put(6)

                .put(8).put(9).put(11)
                .put(11).put(9).put(10)
                ;
        triangleIndicesBuffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, triangleIndicesBuffer);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,1,0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderProgramBinder.bind(shader);
        shader.bindUniform("u_textures[0]", texture);
        shader.bindUniform("u_textures[1]", texture2);
        shader.bindUniform("u_textures[2]", texture3);


        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        GL11.glDrawElements(GL11.GL_TRIANGLES, triangleIndicesBuffer.limit(), GL11.GL_UNSIGNED_INT, 0);

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
