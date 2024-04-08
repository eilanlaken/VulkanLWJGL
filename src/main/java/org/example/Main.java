package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.TuplePair;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Shape2DCircle;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.z_Physics2DWorld;
import org.example.game.ScreenLoading;

public class Main {

    private static Array<TuplePair<Val, Val>> merged = new Array<>();


    public static void main(String[] args) {

        Array<Val> vals = new Array<>();
        for (int i = 0; i < 17 * 2; i++) {
            vals.add(new Val());
        }

        Array<TuplePair<Val,Val>> pool = new Array<>();
        for (int i = 0; i < 17; i++) {
            pool.add(new TuplePair<>(vals.get(MathUtils.random(vals.size)), vals.get(MathUtils.random(vals.size))));
        }

        Array<TuplePair<Val, Val>> tuples_1 = new Array<>();
        for (int i = 0; i < 5; i++) tuples_1.add(pool.pop());
        Array<TuplePair<Val, Val>> tuples_2 = new Array<>();
        for (int i = 0; i < 8; i++) tuples_2.add(pool.pop());
        Array<TuplePair<Val, Val>> tuples_3 = new Array<>();
        for (int i = 0; i < 4; i++) tuples_3.add(pool.pop());

        System.out.println("tuples 1: ");
        System.out.println(tuples_1 + "\n");

        System.out.println("tuples 2: ");
        System.out.println(tuples_2 + "\n");

        System.out.println("tuples 3: ");
        System.out.println(tuples_3 + "\n");

        merged.clear();
        merge(tuples_1);
        merge(tuples_2);
        merge(tuples_3);

        System.out.println("merged:");
        System.out.println(merged);

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

    private static void merge(Array<TuplePair<Val, Val>> arr) {
        for (TuplePair<Val, Val> cellCandidates : arr) {
            boolean present = false;
            for (TuplePair<Val, Val> candidates : merged) {
                if ((candidates.first == cellCandidates.first && candidates.second == cellCandidates.second) || (candidates.first == cellCandidates.second && candidates.second == cellCandidates.first)) {
                    present = true;
                    break;
                }
            }
            if (!present) merged.add(cellCandidates);
        }
    }

    public static class Val {

        public int a;

        public Val() {
            a = MathUtils.random(6);
        }

        @Override
        public String toString() {
            return super.toString() + "" + a;
        }
    }

}