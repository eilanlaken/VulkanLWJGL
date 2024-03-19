package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

// REFERENCE TODO: DefaultTextureBinder (libGDX)
public class TextureBinder {

    private static final int OFFSET = 0; // we will begin binding from slots OFFSET, OFFSET + 1,... leaving slots 0... OFFSET - 1 for texture loading and manipulation?
    private static final int availableTextureSlots = GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) - OFFSET;
    private static Texture[] boundTextures = new Texture[availableTextureSlots];
    private static int roundRobinIndex = 0;

    public static int bindTexture(final Texture texture) {
        if (texture.handle == 0) throw new IllegalStateException("Trying to bind " + Texture.class.getSimpleName() + " that was already freed.");
        int index = TextureBinder.lookForTexture(texture);
        final int slot;
        if (index == -1) {  // texture not found in boundTextures cache
            slot = roundRobinIndex + OFFSET;
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
            GL11.glBindTexture(GL20.GL_TEXTURE_2D, texture.handle);
            updateTextureParameters(texture);
            boundTextures[roundRobinIndex] = texture;
            roundRobinIndex = (roundRobinIndex + 1) % availableTextureSlots;
        } else { // texture was found in boundTextures cache
            // activate unit but no need to rebind
            slot = index + OFFSET;
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
            updateTextureParameters(texture);
        }
        return slot;
    }

    private static int lookForTexture(final Texture texture) {
        for (int i = 0; i < availableTextureSlots; i++) {
            if (boundTextures[i] == texture) return i;
        }
        return -1;
    }


    // TODO: see all texture parameters (formats: RGB, RGBA, ...; packing, etc.)
    private static void updateTextureParameters(final Texture texture) {
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, texture.minFilter.glValue);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, texture.magFilter.glValue);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, texture.uWrap.glValue);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, texture.vWrap.glValue);
    }
    /**
     * In OpenGL, texture parameters are set using the glTexParameteri or glTexParameterf functions, which define how textures are applied and rendered. These parameters can control various aspects of texture mapping, such as wrapping, filtering, and mipmapping. Below is a list of common texture parameters and their purposes:
     *
     *     Wrapping Parameters:
     *         GL_TEXTURE_WRAP_S: Sets the wrap parameter for texture coordinate s (equivalent to the x direction).
     *         GL_TEXTURE_WRAP_T: Sets the wrap parameter for texture coordinate t (equivalent to the y direction).
     *         GL_TEXTURE_WRAP_R: Sets the wrap parameter for texture coordinate r (equivalent to the z direction for 3D textures).
     *
     *     Wrap parameters can be set to:
     *         GL_REPEAT: Repeats the texture image.
     *         GL_MIRRORED_REPEAT: Repeats the texture image but mirrors it with each repeat.
     *         GL_CLAMP_TO_EDGE: Clamps the coordinates between 0 and 1, effectively stretching the edge pixels of the texture.
     *         GL_CLAMP_TO_BORDER: Coordinates outside the range are given a specific border color.
     *
     *     Filtering Parameters:
     *         GL_TEXTURE_MIN_FILTER: Filtering method used when a pixel maps to an area greater than one texture element. Options include GL_NEAREST, GL_LINEAR, GL_NEAREST_MIPMAP_NEAREST, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST_MIPMAP_LINEAR, and GL_LINEAR_MIPMAP_LINEAR.
     *         GL_TEXTURE_MAG_FILTER: Filtering method used when a pixel maps to an area less than or equal to one texture element. Options include GL_NEAREST and GL_LINEAR.
     *
     *     Mipmap Parameters:
     *         GL_TEXTURE_BASE_LEVEL: Specifies the base level mipmap.
     *         GL_TEXTURE_MAX_LEVEL: Specifies the highest level mipmap.
     *         GL_TEXTURE_LOD_BIAS: Specifies the level-of-detail bias.
     *
     *     Texture Comparison Parameters (used in depth textures):
     *         GL_TEXTURE_COMPARE_MODE: Sets the texture comparison mode. Options include GL_COMPARE_REF_TO_TEXTURE and others.
     *         GL_TEXTURE_COMPARE_FUNC: Specifies the comparison function. Options include GL_LEQUAL, GL_GEQUAL, etc.
     *
     *     Border Color:
     *         GL_TEXTURE_BORDER_COLOR: Specifies the border color of the texture (used when GL_CLAMP_TO_BORDER is set).
     *
     *     Anisotropic Filtering Extension (not part of the core OpenGL specification but commonly supported):
     *         GL_TEXTURE_MAX_ANISOTROPY_EXT: Specifies the maximum level of anisotropic filtering.
     */


}
