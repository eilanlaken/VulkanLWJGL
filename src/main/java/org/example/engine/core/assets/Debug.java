package org.example.engine.core.assets;

import org.example.engine.core.memory.Resource;

public class Debug implements Resource {

    public int num;
    public DebugDependency dependency1;
    public DebugDependency dependency2;

    @Override
    public void free() {
        dependency1.free();
        dependency2.free();
    }
}
