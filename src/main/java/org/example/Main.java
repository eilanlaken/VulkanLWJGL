package org.example;

import org.example.engine.core.graphics.Window;
import org.example.game.WindowScreenTest_1;

public class Main {

    public static void main(String[] args) throws Exception {
        Window window = new Window("he", 1600, 900, false, true);
        window.init();
        window.setScreen(new WindowScreenTest_1(window));
        while (!window.windowShouldClose()) {
            window.update();
        }

        window.free();
    }
}