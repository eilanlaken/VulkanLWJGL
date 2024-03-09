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
        SIGNALS,
        ;

        public final int bitMask;

        Category() {
            this.bitMask = 0b000001 << ordinal();
        }

    }

    public static final class Factory {

        // Transform
        public static ComponentTransform createTransform() {
            return new ComponentTransform(false,0,0,0,0,0,0,1,1,1);
        }

    }
}
