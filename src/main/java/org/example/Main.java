package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.memory.MemoryPooled;
import org.example.game.ScreenLoading;

public class Main {

    public static void main(String[] args) {



//        try {
//            TexturePacker.Options options = new TexturePacker.Options("assets/atlases", "physicsDebugShapes",
//                    null, null, null, null,
//                    0,0, TexturePacker.Options.Size.XX_LARGE_8192);
//            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
//            TexturePacker.packTextures(options, "assets/textures/physicsCircle.png", "assets/textures/physicsSquare.png");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (true) return;

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

    public static class Nums implements MemoryPooled {

        public int a;
        public int b;

        public Nums() {

        }

        @Override
        public void reset() {
            a = 0;
            b = 0;
        }
    }

}