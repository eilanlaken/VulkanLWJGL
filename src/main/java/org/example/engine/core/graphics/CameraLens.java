package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Shape3DFrustum;
import org.example.engine.core.math.Shape3DPlane;
import org.example.engine.core.math.Vector3;

public class CameraLens {

    private static final Vector3[] clipSpacePlanePoints = { // This is the clipping volume
            new Vector3(-1, -1, -1), new Vector3(1, -1, -1), new Vector3(1, 1, -1), new Vector3(-1, 1, -1), // near clipping plane corners
            new Vector3(-1, -1, 1), new Vector3(1, -1, 1), new Vector3(1, 1, 1), new Vector3(-1, 1, 1), // far clipping plane corners
    };
    private static Vector3[] planePoints = {
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // near frustum plane corners
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // far frustum plane corners
    };

    private CameraLensProjectionType projectionType;
    public Vector3 position = new Vector3(0,0,0);
    public Vector3 forward = new Vector3(0,0,-1);
    public Vector3 up = new Vector3(0,1,0);
    public Shape3DFrustum frustum;

    public void updateFrustum(final Matrix4 invPrjView) {
        // calculate corners of the frustum by un-projecting the clip space cube using invPrjView
        for (int i = 0; i < 8; i++) {
            Vector3.project(invPrjView, clipSpacePlanePoints[i], planePoints[i]);
        }
        frustum.set(planePoints);
    }

}
