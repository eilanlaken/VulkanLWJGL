package org.example.engine.core.graphics;

import org.example.engine.core.files.FileUtils;
import org.example.engine.core.math.Matrix4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer3D {

    public void begin() {

    }

    public void end() {

    }

    // TODO: use this
    public void render(final Model model, final Matrix4 transform, ShaderProgram shader) {
        shader.bind();
        GL30.glBindVertexArray(model.vaoId);
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.vertexCount);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.unbind(); // TODO: rewrite
    }

}
