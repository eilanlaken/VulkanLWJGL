package org.example.engine.core.shape;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Shape2DUnionTest {

    private static Shape2DUnion shape_1;
    private static Shape2DUnion shape_2;
    private static Shape2DUnion shape_3;
    private static Shape2DUnion shape_invalid;

    @BeforeEach
    public void setup() {
        shape_1 = new Shape2DUnion(new Shape2DRectangle(2,1), new Shape2DCircle(1,2,2));
        shape_2 = new Shape2DUnion(new Shape2DRectangle(2,1), new Shape2DCircle(1,2,2), null, null);
        shape_3 = new Shape2DUnion(new Shape2DRectangle(2,1), new Shape2DUnion(new Shape2DCircle(1), new Shape2DCircle(1), new Shape2DUnion(new Shape2DCircle(1))));
    }

    @Test
    void constructor() {
        Assertions.assertEquals(2, shape_1.shapes.size);
        Assertions.assertEquals(2, shape_2.shapes.size);
        Assertions.assertEquals(4, shape_3.shapes.size);
        Assertions.assertThrows(IllegalArgumentException.class, Shape2DUnion::new);
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