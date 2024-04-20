package org.example.engine.core.shape;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Shape2DCompositeTest {

    private static Shape2DComposite shape_1;
    private static Shape2DComposite shape_2;
    private static Shape2DComposite shape_3;
    private static Shape2DComposite shape_4;

    @BeforeEach
    public void setup() {
        shape_1 = new Shape2DComposite(new Shape2DRectangle(2,1), new Shape2DCircle(1,2,2));
        shape_2 = new Shape2DComposite();
        shape_3 = new Shape2DComposite(new Shape2DRectangle(2,1), new Shape2DCircle(1,2,2), shape_2);
        shape_4 = new Shape2DComposite(new Shape2DRectangle(2,1), new Shape2DComposite(new Shape2DCircle(1), new Shape2DCircle(1), new Shape2DComposite(new Shape2DCircle(1))));
    }

    @Test
    void constructor() {
        Assertions.assertEquals(2, shape_1.shapes.size);
        Assertions.assertEquals(0, shape_2.shapes.size);
        Assertions.assertEquals(2, shape_3.shapes.size);
        Assertions.assertEquals(4, shape_4.shapes.size);
    }

    @Test
    void contains() {
    }

    @Test
    void calculateOriginalBoundingRadius() {
    }

    @Test
    void bakeCurrentTransformToLocalCoordinates() {
    }

    @Test
    void calculateOriginalArea() {
    }

    @Test
    void updateWorldCoordinates() {
    }
}