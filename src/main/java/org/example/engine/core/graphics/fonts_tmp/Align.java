package org.example.engine.core.graphics.fonts_tmp;

@Deprecated public class Align {

    public static final int CENTER = 1 << 0;
    public static final int TOP    = 1 << 1;
    public static final int BOTTOM = 1 << 2;
    public static final int LEFT   = 1 << 3;
    public static final int RIGHT  = 1 << 4;

    public static final int TOP_LEFT     = TOP | LEFT;
    public static final int TOP_RIGHT    = TOP | RIGHT;
    public static final int BOTTOM_LEFT  = BOTTOM | LEFT;
    public static final int BOTTOM_RIGHT = BOTTOM | RIGHT;

    static public final boolean isLeft (int align) {
        return (align & LEFT) != 0;
    }

    static public final boolean isRight (int align) {
        return (align & RIGHT) != 0;
    }

    static public final boolean isTop (int align) {
        return (align & TOP) != 0;
    }

    static public final boolean isBottom (int align) {
        return (align & BOTTOM) != 0;
    }

    static public final boolean isCenterVertical (int align) {
        return (align & TOP) == 0 && (align & BOTTOM) == 0;
    }

    static public final boolean isCenterHorizontal (int align) {
        return (align & LEFT) == 0 && (align & RIGHT) == 0;
    }

    static public String toString (int align) {
        StringBuilder buffer = new StringBuilder(13);
        if ((align & TOP) != 0)
            buffer.append("top,");
        else if ((align & BOTTOM) != 0)
            buffer.append("bottom,");
        else
            buffer.append("center,");
        if ((align & LEFT) != 0)
            buffer.append("left");
        else if ((align & RIGHT) != 0)
            buffer.append("right");
        else
            buffer.append("center");
        return buffer.toString();
    }

}
