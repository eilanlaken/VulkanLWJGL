package org.example.engine.components;

import org.example.engine.core.graphics.GraphicsUtils;

public class Component {

    public final Category category;

    public Component(final Category category) {
        this.category = category;
    }

    public enum Category {

        TRANSFORM,
        PHYSICS,
        GRAPHICS,
        AUDIO,
        LOGIC,
        SIGNALS,
        ;

        public final int bitMask;

        Category() {
            this.bitMask = 0b000001 << ordinal();
        }

    }

}
