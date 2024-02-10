package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

// REFERENCE TODO: DefaultTextureBinder (libgdx)
public class RendererTextureBinder {

    // texture binding
    private final int availableTextureSlots;
    private Texture[] boundTextures;
    private int currentTextureSlot;

    protected RendererTextureBinder() {
        availableTextureSlots = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
        boundTextures = new Texture[availableTextureSlots];
        currentTextureSlot = 0;
    }

    // returns the slot of which the texture was bound to.
    public final int bindTexture(final Texture texture) {
        int index = lookForTexture(texture);
        if (index == -1) {  // texture not found in boundTextures cache
            currentTextureSlot = (currentTextureSlot + 1) % availableTextureSlots;
            boundTextures[currentTextureSlot] = texture;
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + currentTextureSlot);
            GL11.glBindTexture(GL20.GL_TEXTURE_2D, texture.glHandle);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, texture.minFilter.glValue);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, texture.magFilter.glValue);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, texture.uWrap.glValue);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, texture.vWrap.glValue);
            return currentTextureSlot;
        } else { // texture was found in boundTextures cache
            // activate unit but no need to bind
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + index);
            return index;
        }
    }
    private int lookForTexture(final Texture texture) {
        for (int i = 0; i < availableTextureSlots; i++) {
            final int slot = (currentTextureSlot + i) % availableTextureSlots;
            if (boundTextures[slot] == texture) return slot;
        }
        return -1;
    }

}
