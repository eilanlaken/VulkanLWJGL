package org.example.engine.core.graphics;

import java.awt.image.BufferedImage;

// Equivalent to libGDXs' Pixmap
// TODO: later.
public class TexturePixelMap extends BufferedImage {

    public TexturePixelMap(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
    }


}
