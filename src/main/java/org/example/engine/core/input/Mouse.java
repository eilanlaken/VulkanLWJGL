package org.example.engine.core.input;

import org.example.engine.core.graphics.Window;
import org.lwjgl.glfw.*;

public class Mouse {

    private static Window window;
    private static boolean initialized = false;
    private static float sensitivity = 1f; // goes from 0 to infinity. Default is 1.
    private static int prevCursorX;
    private static int prevCursorY;
    private static int cursorX;
    private static int cursorY;
    private static int cursorDeltaX;
    private static int cursorDeltaY;
    private static boolean cursorHidden = false;
    private static boolean cursorInWindow = true;
    private static float verticalScroll = 0;
    private static float horizontalScroll = 0;
    private static int[] mouseButtonsPrevStates = new int[5];
    private static int[] mouseButtonsCurrentStates = new int[5];

    private Mouse() {}

    public static void init(Window window) {
        if (initialized) throw new IllegalStateException("Device input " + Mouse.class.getSimpleName() + " already initialized.");
        Mouse.window = window;

        GLFW.glfwSetMouseButtonCallback(window.getHandle(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtonsPrevStates[button] = Mouse.mouseButtonsCurrentStates[button];
                mouseButtonsCurrentStates[button] = action;
            }
        });

        GLFW.glfwSetCursorPosCallback(window.getHandle(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                prevCursorX = cursorX;
                prevCursorY = cursorY;
                cursorX = (int) xPos;
                cursorY = (int) yPos;
                cursorDeltaX = cursorX - prevCursorX;
                cursorDeltaY = cursorY - prevCursorY;
            }
        });

        GLFW.glfwSetCursorEnterCallback(window.getHandle(), new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                cursorInWindow = entered;
            }
        });

        GLFW.glfwSetScrollCallback(window.getHandle(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                verticalScroll = (float) yOffset;
                horizontalScroll = (float) xOffset;
            }
        });

        initialized = true;
    }

    public static float getVerticalScroll() {
        return verticalScroll;
    }

    public static float getHorizontalScroll() {
        return horizontalScroll;
    }

    public static void hideCursor() {
        GLFW.glfwSetInputMode(window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        cursorHidden = true;
    }

    public static void revealCursor() {
        GLFW.glfwSetInputMode(window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        cursorHidden = false;
    }

    public static boolean isCursorHidden() {
        return cursorHidden;
    }

    public static boolean isInsideWindow() {
        return true;
    }

    public static void setMouseSensitivity(float sensitivity) {
        Mouse.sensitivity = sensitivity;
    }

    public static float getMouseSensitivity() {
        return sensitivity;
    }

    public static int getPrevCursorX() {
        return prevCursorX;
    }

    public static int getPrevCursorY() {
        return prevCursorY;
    }

    public static int getCursorX() {
        return cursorX;
    }

    public static int getCursorY() {
        return cursorY;
    }

    public static int getCursorDeltaX() {
        return cursorDeltaX;
    }

    public static int getCursorDeltaY() {
        return cursorDeltaY;
    }

    public static boolean isCursorInWindow() {
        return cursorInWindow;
    }

    public static boolean isButtonPressed(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_PRESS;
    }

    public static boolean isButtonReleased(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    public static boolean isButtonClicked(final Button button) {
        return mouseButtonsPrevStates[button.glfwCode] == GLFW.GLFW_PRESS && mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    public static void resetInternalState() {
        verticalScroll = 0;
        horizontalScroll = 0;
        cursorDeltaX = 0;
        cursorDeltaY = 0;
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_1] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_1];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_2] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_2];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_3] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_3];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_4] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_4];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_5] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_5];
    }

    public enum Button {

        LEFT(GLFW.GLFW_MOUSE_BUTTON_1),
        RIGHT(GLFW.GLFW_MOUSE_BUTTON_2),
        MIDDLE(GLFW.GLFW_MOUSE_BUTTON_3),
        BACK(GLFW.GLFW_MOUSE_BUTTON_4),
        FORWARD(GLFW.GLFW_MOUSE_BUTTON_5)
        ;

        public final int glfwCode;

        Button(final int glfwCode) {
            this.glfwCode = glfwCode;
        }

    }
}
