package org.example.engine.core.files;

class Asset {

    public final Object obj;
    public final AssetDescriptor descriptor;

    Asset(final Object obj, final AssetDescriptor descriptor) {
        this.obj = obj;
        this.descriptor = descriptor;
    }

}
