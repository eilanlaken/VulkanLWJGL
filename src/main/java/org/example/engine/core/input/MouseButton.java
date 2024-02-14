package org.example.engine.core.input;

import org.lwjgl.glfw.GLFW;

public enum  MouseButton {

    LEFT(GLFW.GLFW_MOUSE_BUTTON_1),
    RIGHT(GLFW.GLFW_MOUSE_BUTTON_2),
    MIDDLE(GLFW.GLFW_MOUSE_BUTTON_3),
    BACK(GLFW.GLFW_MOUSE_BUTTON_4),
    FORWARD(GLFW.GLFW_MOUSE_BUTTON_5)
    ;

    public final int glfwCode;

    MouseButton(final int glfwCode) {
        this.glfwCode = glfwCode;
    }

}
