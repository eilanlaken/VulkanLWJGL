package org.example.engine.ecs;

import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.TextureRegion;

import java.util.HashMap;

public class ComponentGraphics2DSprite extends Component {

    public static final Component.Category category = Category.GRAPHICS;

    public final TextureRegion region;
    public Color tint;
    public ShaderProgram customShader;
    public HashMap<String, Object> customAttributes;

    protected ComponentGraphics2DSprite(TextureRegion region, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        super(category);
        this.region = region;
        this.tint = tint;
        this.customShader = customShader;
        this.customAttributes = customAttributes;
    }

}
