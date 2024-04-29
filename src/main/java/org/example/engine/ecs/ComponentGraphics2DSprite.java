package org.example.engine.ecs;

import org.example.engine.core.graphics.GraphicsColor;
import org.example.engine.core.graphics.GraphicsShaderProgram;
import org.example.engine.core.graphics.GraphicsTextureRegion;

import java.util.HashMap;

public class ComponentGraphics2DSprite extends Component {

    public static final Component.Category category = Category.GRAPHICS;

    public final GraphicsTextureRegion region;
    public GraphicsColor tint;
    public GraphicsShaderProgram customShader;
    public HashMap<String, Object> customAttributes;

    protected ComponentGraphics2DSprite(GraphicsTextureRegion region, GraphicsColor tint, GraphicsShaderProgram customShader, HashMap<String, Object> customAttributes) {
        super(category);
        this.region = region;
        this.tint = tint;
        this.customShader = customShader;
        this.customAttributes = customAttributes;
    }

}
