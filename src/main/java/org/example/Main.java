package org.example;

import org.example.engine.core.graphics.Vulkan_tmp;
import org.example.engine.core.graphics.Window;

public class Main {

    public static void main(String[] args) throws Exception {
        Window window = new Window("he", 1600, 900, false, false);
        window.init();

        while (!window.windowShouldClose()) {
            window.update();
        }

        window.cleanup();
    }
}