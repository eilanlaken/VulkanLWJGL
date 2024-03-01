package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.WindowScreenTest_AssetStore_1;
import org.example.game.WindowScreenTest_Lights_1;
import org.example.game.WindowScreenTest_Lights_2;

public class Main {

    public static void main(String[] args) {
        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new WindowScreenTest_AssetStore_1());
    }
}