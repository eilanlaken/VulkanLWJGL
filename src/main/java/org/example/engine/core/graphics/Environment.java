package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Matrix4;

public class Environment {

    public Array<EnvironmentLightDirectional> directionalLights;
    public Array<Matrix4> directionalLightsTransforms;

    public Array<EnvironmentLightPoint> pointLights;
    public Array<Matrix4> pointLightsTransforms;

    public Array<EnvironmentLightSpot> spotLights;
    public Array<Matrix4> spotLightsTransforms;

    public void add(final EnvironmentLight light, final Matrix4 transform) {
        if (transform == null) throw new IllegalArgumentException("Light transform cannot be null.");
        if (light instanceof EnvironmentLightDirectional) {
            directionalLights.add((EnvironmentLightDirectional) light);
            pointLightsTransforms.add(transform);
        } else if (light instanceof EnvironmentLightPoint) {
            pointLights.add((EnvironmentLightPoint) light);
            pointLightsTransforms.add(transform);
        } else if (light instanceof EnvironmentLightSpot) {
            spotLights.add((EnvironmentLightSpot) light);
            spotLightsTransforms.add(transform);
        }
    }

    public void clear() {
        directionalLights.clear();
        directionalLightsTransforms.clear();
        pointLights.clear();
        pointLightsTransforms.clear();
        spotLights.clear();
        spotLightsTransforms.clear();
    }

}
