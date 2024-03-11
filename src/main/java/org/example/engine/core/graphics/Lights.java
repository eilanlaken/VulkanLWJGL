package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector3;

public class Lights {

    private final Color finalAmbient = new Color(0,0,0,1);
    private final Vector3 totalAmbient = new Vector3(0,0,0);
    public Array<LightAmbient> ambientLights = new Array<>();
    public Array<LightDirectional> directionalLights = new Array<>();
    public Array<LightPoint> pointLights = new Array<>();
    public Array<LightSpot> spotLights = new Array<>();

    public void add(final Light light) {
        if (light instanceof LightPoint) pointLights.add((LightPoint) light);
        else if (light instanceof LightSpot) spotLights.add((LightSpot) light);
        else if (light instanceof LightAmbient) ambientLights.add((LightAmbient) light);
        else if (light instanceof LightDirectional) directionalLights.add((LightDirectional) light);
    }

    public Color getTotalAmbient() {
        float totalRed = 0;
        float totalGreen = 0;
        float totalBlue = 0;
        for (LightAmbient ambient : ambientLights) {
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
