package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Physics2DUtilsTest {

    @Test
    void calculateMomentOfInertia() {
        BodyColliderPolygon poly = new BodyColliderPolygon(1,0,0,0,false,0, new float[] {0,1, 1,1, 1,0, 0,0});
        BodyColliderRectangle rect = new BodyColliderRectangle(1,0,0,0,false,0,1,1,0,0,0);

        float polyInertia = Physics2DUtils.calculateMomentOfInertia(poly);
        float rectInertia = Physics2DUtils.calculateMomentOfInertia(rect);
        Assertions.assertEquals(polyInertia, rectInertia, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}