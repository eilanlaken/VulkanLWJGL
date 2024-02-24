package org.example.engine.components;

public enum ComponentCategory {

    TRANSFORM,
    PHYSICS,
    GRAPHICS,
    AUDIO,
    LOGIC,
    SIGNALS,
    ;

    public final int bitMask;

    ComponentCategory() {
        this.bitMask = 0b000001 << ordinal();
    }

}
