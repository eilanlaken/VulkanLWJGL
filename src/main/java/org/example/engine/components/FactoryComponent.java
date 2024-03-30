package org.example.engine.components;

import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.TextureRegion;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Shape2DPolygon;

import java.util.Arrays;
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
        Shape2DPolygon polygon = new Shape2DPolygon(new float[] {-width * 0.5f, height * 0.5f, -width * 0.5f, -height * 0.5f, width * 0.5f, -height * 0.5f, width * 0.5f, height * 0.5f});
        return new ComponentGraphics2DShape(ComponentGraphics2DShape.RECTANGLE, tint, polygon, customShader, customAttributes);
    }

    // TODO: cache refinement data.
    public static ComponentGraphics2DShape createShapeCircleFilled(float r, int refinement, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        if (refinement < 3) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 3. Got: " + refinement);
        float[] vertices = new float[refinement * 2];
        for (int i = 0; i < refinement * 2; i += 2) {
            float angle = 360f * (i * 0.5f) / refinement;
            vertices[i] = r * MathUtils.cosDeg(angle);
            vertices[i+1] = r * MathUtils.sinDeg(angle);
        }
        Shape2DPolygon polygon = new Shape2DPolygon(vertices);
        return new ComponentGraphics2DShape(ComponentGraphics2DShape.CIRCLE, tint, polygon, customShader, customAttributes);
    }

    public static ComponentGraphics2DShape createShapeRectangleHollow(float width, float height, float stroke, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {

        return null;
    }

}
