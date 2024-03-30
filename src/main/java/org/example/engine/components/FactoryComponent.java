package org.example.engine.components;

import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.TextureRegion;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Shape2DPolygon;
import org.example.engine.core.math.Vector2;

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
        float dx = x2 - x1;
        float dy = y2 - y1;
        Vector2 strokeVector = new Vector2(dx, dy);
        strokeVector.rotate90(1);
        strokeVector.nor().scl(stroke * 0.5f, stroke * 0.5f);
        System.out.println(strokeVector);
        Shape2DPolygon polygon = new Shape2DPolygon(new float[] {x1 + strokeVector.x, y1 + strokeVector.y, x1 - strokeVector.x, y1 - strokeVector.y, x2 - strokeVector.x, y2 - strokeVector.y, x2 + strokeVector.x, y2 + strokeVector.y});
        return new ComponentGraphics2DShape(ComponentGraphics2DShape.LINE, tint, polygon, customShader, customAttributes);
    }

    public static ComponentGraphics2DShape createShapeRectangleFilled(float width, float height, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        Shape2DPolygon polygon = new Shape2DPolygon(new float[] {-widthHalf, heightHalf, -widthHalf, -heightHalf, widthHalf, -heightHalf, widthHalf, heightHalf});
        System.out.println("int rect: " + Arrays.toString(polygon.indices));

        return new ComponentGraphics2DShape(ComponentGraphics2DShape.RECTANGLE, tint, polygon, customShader, customAttributes);
    }

    public static ComponentGraphics2DShape createShapeRectangleHollow(float width, float height, float stroke, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        if (stroke < 1) throw new IllegalArgumentException("Stroke must be at least 1. Got: " + stroke);
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        final float strokeHalf = stroke * 0.5f;
        Shape2DPolygon polygon = new Shape2DPolygon(new float[]{
                -widthHalf - strokeHalf, heightHalf + strokeHalf, -widthHalf - strokeHalf, -heightHalf - strokeHalf, widthHalf + strokeHalf, -heightHalf - strokeHalf, widthHalf + strokeHalf, heightHalf + strokeHalf,
                -widthHalf + strokeHalf, heightHalf - strokeHalf, -widthHalf + strokeHalf, -heightHalf + strokeHalf, widthHalf - strokeHalf, -heightHalf + strokeHalf, widthHalf - strokeHalf, heightHalf - strokeHalf
        }, new int[] { 4 });
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

    public static ComponentGraphics2DShape createShapeCircleHollow(float r, int refinement, float stroke, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        if (refinement < 3) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 3. Got: " + refinement);
        if (stroke < 1) throw new IllegalArgumentException("Stroke must be at least 1. Got: " + stroke);
        float outerRadius = r + stroke * 0.5f;
        float innerRadius = r - stroke * 0.5f;
        float[] vertices = new float[refinement * 2 * 2];
        for (int i = 0; i < refinement * 2; i += 2) { // outer rim
            float angle = 360f * (i * 0.5f) / refinement;
            vertices[i] = outerRadius * MathUtils.cosDeg(angle);
            vertices[i+1] = outerRadius * MathUtils.sinDeg(angle);
        }
        for (int i = refinement * 2; i < refinement * 2 * 2; i += 2) { // outer rim
            float angle = 360f * (i * 0.5f) / refinement;
            vertices[i] = innerRadius * MathUtils.cosDeg(angle);
            vertices[i+1] = innerRadius * MathUtils.sinDeg(angle);
        }
        Shape2DPolygon polygon = new Shape2DPolygon(vertices, new int[] { refinement });
        return new ComponentGraphics2DShape(ComponentGraphics2DShape.CIRCLE, tint, polygon, customShader, customAttributes);
    }


}
