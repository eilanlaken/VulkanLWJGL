package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.ScreenLoading;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Main {

    public static void main(String[] args) {
        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());
    }

}