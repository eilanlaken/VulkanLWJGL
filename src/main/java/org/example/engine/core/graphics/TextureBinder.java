package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

// REFERENCE TODO: DefaultTextureBinder (libgdx)
public class TextureBinder {

    private static final int OFFSET = 0; // we will begin binding from slots 2,3,4... leaving slot 1 for texture loading and manipulation.
    private static final int availableTextureSlots = GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) - OFFSET;
    private static Texture[] boundTextures = new Texture[availableTextureSlots];
    private static int roundRobinIndex = 0;

    public static int bindTexture(final Texture texture) {
        int index = lookForTexture(texture);
        if (index == -1) {  // texture not found in boundTextures cache
            roundRobinIndex = (roundRobinIndex + 1) % availableTextureSlots;
            boundTextures[roundRobinIndex] = texture;
            final int slot = roundRobinIndex + OFFSET;
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
            GL11.glBindTexture(GL20.GL_TEXTURE_2D, texture.glHandle);
            updateTextureParameters(texture);
            return roundRobinIndex;
        } else { // texture was found in boundTextures cache
            // activate unit but no need to bind
            final int slot = index + OFFSET;
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
            updateTextureParameters(texture);
            return index;
        }
    }

    private static void updateTextureParameters(final Texture texture) {
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, texture.minFilter.glValue);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, texture.magFilter.glValue);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, texture.uWrap.glValue);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, texture.vWrap.glValue);
    }

    private static int lookForTexture(final Texture texture) {
        for (int i = 0; i < availableTextureSlots; i++) {
            final int slot = (roundRobinIndex + i) % availableTextureSlots;
            if (boundTextures[slot] == texture) return slot;
        }
        return -1;
    }

}
