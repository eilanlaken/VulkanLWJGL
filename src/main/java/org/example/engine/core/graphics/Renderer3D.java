package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer3D {

    public final RendererFixedPipelineParamSetter paramSetter;
    //public final TextureBinder textureBinder;

    public Renderer3D() {
        //textureBinder = new TextureBinder();
        paramSetter = new RendererFixedPipelineParamSetter();
    }

    public void begin() {

    }

    public void end() {

    }

    // TODO: use this
    public void render(final Model model, final Matrix4 transform, ShaderProgram shader) {
        // TODO: move to render context.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);

        shader.bind();

        shader.bindUniforms(model.get_material_debug());
        //GL20.glUniform1i(0, 1);

        GL30.glBindVertexArray(model.vaoId);
        GL20.glEnableVertexAttribArray(0); // positions
        GL20.glEnableVertexAttribArray(1); // texture coordinates

        //GL13.glActiveTexture(GL13.GL_TEXTURE0);
        //GL13.glBindTexture(GL11.GL_TEXTURE_2D, model.texture.glHandle);

        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture coordinates
        GL30.glBindVertexArray(0);
        shader.unbind();
    }

}
