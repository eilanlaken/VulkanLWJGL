package org.example.engine.core.graphics;

import java.awt.image.BufferedImage;

// Equivalent to libGDXs' Pixmap
// TODO: later.
public class TexturePixelsData extends BufferedImage {

    public TexturePixelsData(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
    }


}
