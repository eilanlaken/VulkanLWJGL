package org.example.engine.components;

import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.TextureRegion;

import java.util.HashMap;

public final class FactoryComponent {

    /** Transforms **/
    public static ComponentTransform createTransform() {
        return new ComponentTransform(false,0,0,0,0,0,0,1,1,1);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        return new ComponentTransform(isStatic, x, y, z, angleX, angleY, angleZ, scaleX, scaleY, scaleZ);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z) {
        return new ComponentTransform(isStatic, x, y, z, 0, 0, 0, 1, 1, 1);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ) {
        return new ComponentTransform(isStatic, x, y, z, angleX, angleY, angleZ, 1, 1, 1);
    }

    /** Graphics - Sprites **/
    public static ComponentGraphics2DSprite createSprite(TextureRegion region, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        return new ComponentGraphics2DSprite(region, tint, customShader, customAttributes);
    }

    /** Graphics - Sprite Animations **/
    public static ComponentGraphics2DSpriteAnimations createSpriteAnimations() {
        return null;
    }

    /** Graphics - Shapes **/
    public static ComponentGraphics2DShape createShapeLine(float x1, float y1, float x2, float y2, float stroke, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {

        return null;
    }

    public static ComponentGraphics2DShape createShapeRectangleFilled(float width, float height, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {

        return null;
    }

    public static ComponentGraphics2DShape createShapeRectangleHollow(float width, float height, float stroke, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {

        return null;
    }

}