package org.example.engine.core.physics2d_new;

import org.example.engine.core.graphics.CameraLens;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.shape.Shape2DPolygon;
import org.example.engine.core.shape.ShapeUtils;

public class Physics2DRenderer {

    private Shape2DPolygon contactIndicator;
    private final Renderer2D renderer2D;

    protected Physics2DRenderer(Renderer2D renderer2D) {
        this.renderer2D = renderer2D;
        contactIndicator = ShapeUtils.createPolygonCircleFilled(1, 10);
    }

    private void render(Physics2DWorld world, CameraLens lens) {

    }

}
