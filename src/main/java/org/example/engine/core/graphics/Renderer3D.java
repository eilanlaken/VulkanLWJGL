package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer3D {

    public void begin() {

    }

    public void end() {

    }

    public void render(final ModelInstance modelInstance) {
        GL30.glBindVertexArray(modelInstance.model.vaoId);
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(modelInstance.renderPrimitive, 0, modelInstance.model.vertexCount);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

}
