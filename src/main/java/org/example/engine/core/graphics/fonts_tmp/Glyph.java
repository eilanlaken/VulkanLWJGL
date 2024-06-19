package org.example.engine.core.graphics.fonts_tmp;

/** Represents a single character in a font page. */
public class Glyph {
    public int id;
    public int srcX;
    public int srcY;
    public int width, height;
    public float u, v, u2, v2;
    public int xoffset, yoffset;
    public int xadvance;
    public byte[][] kerning;
    public boolean fixedWidth;

    /** The index to the texture page that holds this glyph. */
    public int page = 0;

    public int getKerning (char ch) {
        if (kerning != null) {
            byte[] page = kerning[ch >>> BitmapFont.LOG2_PAGE_SIZE];
            if (page != null) return page[ch & BitmapFont.PAGE_SIZE - 1];
        }
        return 0;
    }

    public void setKerning (int ch, int value) {
        if (kerning == null) kerning = new byte[BitmapFont.PAGES][];
        byte[] page = kerning[ch >>> BitmapFont.LOG2_PAGE_SIZE];
        if (page == null) kerning[ch >>> BitmapFont.LOG2_PAGE_SIZE] = page = new byte[BitmapFont.PAGE_SIZE];
        page[ch & BitmapFont.PAGE_SIZE - 1] = (byte)value;
    }

    public String toString () {
        return Character.toString((char)id);
    }
}
