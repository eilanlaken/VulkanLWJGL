package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Vector3;

public class Environment {

    private final Color finalAmbient = new Color(0,0,0,1);
    private final Vector3 totalAmbient = new Vector3(0,0,0);
    public Array<EnvironmentLightAmbient> ambientLights = new Array<>();
    public Array<EnvironmentLightDirectional> directionalLights = new Array<>();
    public Array<EnvironmentLightPoint> pointLights = new Array<>();
    public Array<EnvironmentLightSpot> spotLights = new Array<>();

    public void add(final EnvironmentLight light) {
        if (light instanceof EnvironmentLightPoint) pointLights.add((EnvironmentLightPoint) light);
        else if (light instanceof EnvironmentLightSpot) spotLights.add((EnvironmentLightSpot) light);
        else if (light instanceof EnvironmentLightAmbient) ambientLights.add((EnvironmentLightAmbient) light);
        else if (light instanceof EnvironmentLightDirectional) directionalLights.add((EnvironmentLightDirectional) light);
    }

    public Color getTotalAmbient() {
        float totalRed = 0;
        float totalGreen = 0;
        float totalBlue = 0;
        for (EnvironmentLightAmbient ambient : ambientLights) {
            totalRed += ambient.color.r * ambient.intensity;
            totalGreen += ambient.color.g * ambient.intensity;
            totalBlue += ambient.color.b * ambient.intensity;
        }
        totalAmbient.set(totalRed, totalGreen, totalBlue);
        float max = MathUtils.max(totalRed, totalGreen, totalBlue);
        if (max > 1) totalAmbient.scl(1 / max);
        return finalAmbient.set(totalAmbient.x, totalAmbient.y, totalAmbient.z, 1);
    }

    public void clear() {
        ambientLights.clear();
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();
    }

}
