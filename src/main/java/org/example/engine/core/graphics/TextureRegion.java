package org.example.engine.core.graphics;

public class TextureRegion {

    public final Texture texture;
    public final float x;
    public final float y;
    public final float offsetX;
    public final float offsetY;
    public final float packedWidth;
    public final float packedHeight;
    public final float originalWidth;
    public final float originalHeight;
    public final float packedWidthHalf;
    public final float packedHeightHalf;
    public final float originalWidthHalf;
    public final float originalHeightHalf;
    public final float u;
    public final float v;
    public final float u2;
    public final float v2;

    protected TextureRegion(Texture texture) {
        this(texture,0,0,0,0, texture.width, texture.height, texture.width, texture.height);
    }

    protected TextureRegion(Texture texture,
                            int x, int y, int offsetX, int offsetY,
                            int packedWidth, int packedHeight, int originalWidth, int originalHeight) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.packedWidth = packedWidth;
        this.packedHeight = packedHeight;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.packedWidthHalf = packedWidth * 0.5f;
        this.packedHeightHalf = packedHeight * 0.5f;
        this.originalWidthHalf = originalWidth * 0.5f;
        this.originalHeightHalf = originalHeight * 0.5f;

        // TODO: set u,v,u2,v2
        u = v = u2 = v2 = 0;
    }

}
