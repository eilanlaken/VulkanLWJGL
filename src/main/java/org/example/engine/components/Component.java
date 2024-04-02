package org.example.engine.components;

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
        CHEMISTRY,
        ;

        public final int bitMask;

        Category() {
            this.bitMask = 0b000001 << ordinal();
        }

    }

}
