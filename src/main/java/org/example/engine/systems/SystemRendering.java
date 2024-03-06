package org.example.engine.systems;

import org.example.engine.entities.Entity;
import org.example.engine.entities.EntityContainer;

public class SystemRendering extends System {

    public SystemRendering(final EntityContainer container) {
        super(container);
    }

    @Override
    protected boolean shouldProcess(Entity entity) {
        return false;
    }

    @Override
    protected void add(Entity entity) {

    }

    @Override
    protected void remove(Entity entity) {

    }

    @Override
    protected void frameUpdate(float delta) {

    }

    @Override
    protected void fixedUpdate(float delta) {

    }
}
