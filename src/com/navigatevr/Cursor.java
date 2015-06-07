package com.navigatevr;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;

public class Cursor {

    private static final String TAG = "Cursor";

    private GVRSceneObject sceneObject;
    private float distance = 3f;

    public Cursor(GVRContext gvrContext) {
        GVRTexture texture = gvrContext.loadTexture(
                new GVRAndroidResource( gvrContext, R.raw.cross) );

        sceneObject = new GVRSceneObject(gvrContext, 0.05f, 0.05f, texture);

        sceneObject.getRenderData().setDepthTest(false);
        sceneObject.getRenderData().setRenderingOrder(100000);

        this.setDistance( distance );
    }

    public GVRSceneObject getSceneObject() {
        return sceneObject;
    }

    public void setDistance(float distance) {
        sceneObject.getTransform().setPositionZ( -1f*distance );
    }

}
