package org.example.engine.ecs;

import org.example.engine.core.graphics.GraphicsColor;
import org.example.engine.core.graphics.GraphicsShaderProgram;
import org.example.engine.core.shape.Shape2DPolygon;

import java.util.HashMap;

public class ComponentGraphics2DShape extends Component {

    public static final Component.Category category = Category.GRAPHICS;

    public static final int LINE      = 0;
    public static final int RECTANGLE = 1;
    public static final int CIRCLE    = 2;
    public static final int POLYGON   = 3;
    public static final int CURVE     = 4; // TODO: implement.

    public final int shape;
    public GraphicsColor tint;
    public final Shape2DPolygon polygon;
    public GraphicsShaderProgram customShader;
    public HashMap<String, Object> customAttributes;

    protected ComponentGraphics2DShape(int shape, GraphicsColor tint, Shape2DPolygon polygon, GraphicsShaderProgram customShader, HashMap<String, Object> customAttributes) {
        super(category);
        this.shape = shape;
        this.tint = tint;
        this.polygon = polygon;
        this.customShader = customShader;
        this.customAttributes = customAttributes;
    }

}
