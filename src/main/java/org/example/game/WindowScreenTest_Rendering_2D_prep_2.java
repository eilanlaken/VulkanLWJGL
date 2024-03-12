package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class WindowScreenTest_Rendering_2D_prep extends WindowScreen {

    private Renderer2D renderer2D;
    private ShaderProgram shader;
    private ComponentTransform transform;
    private ComponentGraphicsCamera camera;

    // create and modify quad dynamically
    private float[] vertices = new float[4 * 5];
    private short[] indices = new short[6];

    int vao;
    int vboVertices;
    int vboIndices;

    public WindowScreenTest_Rendering_2D_prep() {
        this.renderer2D = new Renderer2D();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d-prep.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

        this.camera = Component.Factory.createCamera2D(GraphicsUtils.getWindowWidth(),GraphicsUtils.getWindowHeight());

        float c = Color.asSingleFloat(new Color(1,0,0,1));
        System.out.println(c);
    }

    @Override
    public void show() {
        vertices = new float[] {
                -0.5f, 0.5f, Color.asSingleFloat(new Color(1,0,0,1)),
                -0.5f, -0.5f, Color.asSingleFloat(new Color(1,0,0,1)),
                0.5f, -0.5f, Color.asSingleFloat(new Color(1,0,0,1)),
                0.5f, 0.5f, Color.asSingleFloat(new Color(1,0,0,1))
        };

        indices = new short[] {
                0,1,3,//top left triangle (v0, v1, v3)
                3,1,2//bottom right triangle (v3, v1, v2)
        };

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vboVertices = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertices);
        //GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 1024, GL15.GL_DYNAMIC_DRAW);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);


        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL15.GL_FLOAT, false, 3 * Float.BYTES, 0);

        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 1, GL15.GL_UNSIGNED_BYTE , true, 3 * Float.BYTES, 2 * Float.BYTES);

        vboIndices = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndices);
        //GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, 1024, GL15.GL_DYNAMIC_DRAW);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_DYNAMIC_DRAW);


        GL30.glBindVertexArray(0);
    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        // rebuild the mesh
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertices);
//        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, MemoryUtils.store(vertices));
//
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndices);
//        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, MemoryUtils.store(indices));


        ShaderProgramBinder.bind(shader);
        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
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
