package org.example.engine.systems;

import org.example.engine.entities.Entity;
import org.example.engine.entities.EntityContainer;

public abstract class System {

    public final EntityContainer container;
    protected boolean enabled;

    public System(final EntityContainer container) {
        this.container = container;
    }

    protected abstract boolean shouldProcess(final Entity entity);
    protected abstract void add(Entity entity);
    protected abstract void remove(Entity entity);
    protected abstract void frameUpdate(float delta);
    protected abstract void fixedUpdate(float delta);

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
