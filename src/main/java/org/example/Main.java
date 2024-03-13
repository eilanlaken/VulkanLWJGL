package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.ScreenLoading;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Main {

    public static void main(String[] args) {
        float[] floatArray = {1,2,3,4,5};
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10 * Float.BYTES);
        byteBuffer.order(ByteOrder.nativeOrder()); // Set the byte order to native (usually little-endian or big-endian)
        // Create a FloatBuffer view of the ByteBuffer
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        // Copy the float array to the FloatBuffer

        System.out.println(floatBuffer.limit());
        floatBuffer.put(floatArray,2,2);
        System.out.println(floatBuffer.position());
        floatBuffer.flip();

        while (floatBuffer.hasRemaining()) {
            System.out.println(floatBuffer.get());
        }

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());
    }

}