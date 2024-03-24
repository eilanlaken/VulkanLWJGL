package org.example.engine.components;

import org.example.engine.core.graphics.CameraLens_dep;
import org.example.engine.core.graphics.CameraLensProjectionType;
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

    public static final class Factory {

        // Transform
        public static ComponentTransform createTransform() {
            return new ComponentTransform(false,0,0,0,0,0,0,1,1,1);
        }

        // camera
        public static ComponentGraphicsCamera createCamera2D() {
            CameraLens_dep lens = new CameraLens_dep(CameraLensProjectionType.ORTHOGRAPHIC_PROJECTION, GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight(), 1, 0, 100, 67);

            return new ComponentGraphicsCamera(lens);
        }

        public static ComponentGraphicsCamera createCamera2D(float viewportWidth, float viewportHeight) {
            CameraLens_dep lens = new CameraLens_dep(CameraLensProjectionType.ORTHOGRAPHIC_PROJECTION, viewportWidth, viewportHeight, 1, 0f, 1, 67);

            return new ComponentGraphicsCamera(lens);
        }

    }
}
