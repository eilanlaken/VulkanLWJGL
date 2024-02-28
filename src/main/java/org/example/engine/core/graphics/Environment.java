package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Matrix4;

public class Environment {

    public Array<EnvironmentLightDirectional> directionalLights;
    public Array<EnvironmentLightPoint> pointLights;
    public Array<EnvironmentLightSpot> spotLights;

    public void add(final EnvironmentLight light) {
        if (light instanceof EnvironmentLightDirectional) {
            directionalLights.add((EnvironmentLightDirectional) light);
        } else if (light instanceof EnvironmentLightPoint) {
            pointLights.add((EnvironmentLightPoint) light);
        } else if (light instanceof EnvironmentLightSpot) {
            spotLights.add((EnvironmentLightSpot) light);
        }
    }

    public void clear() {
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();
    }

}
