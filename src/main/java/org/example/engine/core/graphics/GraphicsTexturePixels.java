package org.example.engine.core.graphics;

import java.awt.image.BufferedImage;

// Equivalent to libGDXs' Pixmap
// TODO: later.
public class GraphicsTexturePixels extends BufferedImage {

    public GraphicsTexturePixels(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
    }


}
