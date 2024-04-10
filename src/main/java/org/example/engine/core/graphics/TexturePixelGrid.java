package org.example.engine.core.graphics;

import java.awt.image.BufferedImage;

// Equivalent to libGDXs' Pixmap
// TODO: later.
public class TexturePixelGrid extends BufferedImage {

    public TexturePixelGrid(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
    }


}
