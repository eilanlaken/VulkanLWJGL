package org.example.engine.core.physics2d;

import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Shape2DAABB;
import org.example.engine.core.math.Shape2DCircle;

public final class CollisionDetection2D {

    public static boolean checkCollision(final Shape2D a, final Shape2D b) {
        if (a instanceof Shape2DAABB && b instanceof Shape2DAABB) return checkCollision((Shape2DAABB) a, (Shape2DAABB) b);
        if (a instanceof Shape2DCircle && b instanceof Shape2DCircle) return checkCollision((Shape2DCircle) a, (Shape2DCircle) b);
        return false;
    }

    private static boolean checkCollision(final Shape2DAABB a, final Shape2DAABB b) {
        if (a.worldMax.x < b.worldMin.x || a.worldMin.x > b.worldMax.x) return false;
        return !(a.worldMax.y < b.worldMin.y) && !(a.worldMin.y > b.worldMax.y);
    }

    private static boolean checkCollision(final Shape2DCircle a, final Shape2DCircle b) {
        float r = a.originalRadius + b.originalRadius;
        r *= r;
        return r < (a.localCenter.x + b.localCenter.x) * (a.localCenter.x + b.localCenter.x) + (a.localCenter.y + b.localCenter.y) * (a.localCenter.y + b.localCenter.y);
    }

}
