package org.example.engine.core.graphics;

public abstract class WindowScreen {

    protected final Window window;

    protected WindowScreen(Window window) {
        this.window = window;
    }

    public abstract void show();

    public abstract void update(float delta);

    public abstract void resize(int width, int height);

    public abstract void pause();

    public abstract void resume();

    public abstract void hide();
}
