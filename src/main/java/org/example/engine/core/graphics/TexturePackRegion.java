package org.example.engine.core.graphics;

public class TexturePackRegion {

    public final Texture texture;
    public final float u;
    public final float v;
    public final float u2;
    public final float v2;
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public String name;
    public float offsetX;
    public float offsetY;
    public int packedWidth;
    public int packedHeight;
    public int originalWidth;
    public int originalHeight;

    protected TexturePackRegion(final Texture texture,
                                final float u, final float v,
                                final float u2, final float v2) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
        this.width = Math.round(Math.abs(u2 - u) * (float)texture.width);
        this.height = Math.round(Math.abs(v2 - v) * (float)texture.height);
        this.x = Math.round(u * (float) texture.width);
        this.y = Math.round(v * (float) texture.height);
    }

    protected TexturePackRegion(final Texture texture,
                                final int x, final int y,
                                final int width, final int height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        float invTexWidth = 1.0f / (float) texture.width;
        float invTexHeight = 1.0f / (float) texture.height;
        this.u = (float)x * invTexWidth;
        this.v = (float)y * invTexHeight;
        this.u2 = (float)(x + width) * invTexWidth;
        this.v2 = (float)(y + height) * invTexHeight;
    }

}
