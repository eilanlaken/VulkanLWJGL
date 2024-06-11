package org.example.engine.core.physics2d;

public class ConstraintPin extends Constraint {

    public ConstraintPin(final Body body) {
        super(body);
    }

    @Override
    void prepare(float delta) {

    }

    @Override
    void solveVelocity(float delta) {

    }

    @Override
    boolean solvePosition(float delta) {
        return true;
    }

}
