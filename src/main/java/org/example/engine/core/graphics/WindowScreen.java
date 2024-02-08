package org.example.engine.core.graphics;

public interface WindowScreen {

    public void show();

    public void update(float delta);

    public void resize(int width, int height);

    public void pause();

    public void resume();

    public void hide();

}
