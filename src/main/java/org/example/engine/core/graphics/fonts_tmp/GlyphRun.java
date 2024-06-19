package org.example.engine.core.graphics.fonts_tmp;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayFloat;
import org.example.engine.core.memory.MemoryPool;

/** Stores glyphs and positions for a line of text.
 * @author Nathan Sweet */
public class GlyphRun implements MemoryPool.Reset {
    public Array<Glyph> glyphs = new Array();

    /** Contains glyphs.size+1 entries:<br>
     * The first entry is the X offset relative to the drawing position.<br>
     * Subsequent entries are the X advance relative to previous glyph position.<br>
     * The last entry is the width of the last glyph. */
    public ArrayFloat xAdvances = new ArrayFloat();

    public float x, y, width;

    void appendRun (GlyphRun run) {
        glyphs.addAll(run.glyphs);
        // Remove the width of the last glyph. The first xadvance of the appended run has kerning for the last glyph of this run.
        if (xAdvances.notEmpty()) xAdvances.size--;
        xAdvances.addAll(run.xAdvances);
    }

    @Override
    public void reset () {
        glyphs.clear();
        xAdvances.clear();
    }

    public String toString () {
        StringBuilder buffer = new StringBuilder(glyphs.size + 32);
        Array<Glyph> glyphs = this.glyphs;
        for (int i = 0, n = glyphs.size; i < n; i++) {
            Glyph g = glyphs.get(i);
            buffer.append((char)g.id);
        }
        buffer.append(", ");
        buffer.append(x);
        buffer.append(", ");
        buffer.append(y);
        buffer.append(", ");
        buffer.append(width);
        return buffer.toString();
    }
}
