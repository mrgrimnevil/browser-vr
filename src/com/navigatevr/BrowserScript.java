package com.navigatevr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScript;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRWebViewSceneObject;

import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.oculus.VRTouchPadGestureDetector.SwipeDirection;

public class BrowserScript extends GVRScript {

    private static final String TAG = "BrowserScript";

	private GVRAnimationEngine mAnimationEngine;

    private MainActivity mActivity;
    private GVRContext mContext;

    private GVRScene mScene;
    private GVRCameraRig mRig;

	private List<GVRPicker.GVRPickedObject> pickedObjects;

    private GVRSceneObject mContainer;

    private GVRWebViewSceneObject webViewObject;

    private Map<String, GVRSceneObject> objects = new HashMap<String, GVRSceneObject>();

    BrowserScript(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onInit(GVRContext gvrContext) {
    	mContext = gvrContext;

		mAnimationEngine = gvrContext.getAnimationEngine();

        mScene = gvrContext.getNextMainScene();
        mRig = mScene.getMainCameraRig();

        mRig.getLeftCamera().setBackgroundColor(Color.DKGRAY);
        mRig.getRightCamera().setBackgroundColor(Color.DKGRAY);


        mContainer = new GVRSceneObject(gvrContext);
        mScene.addSceneObject(mContainer);

        webViewObject = createWebViewObject(gvrContext);

        webViewObject.getTransform().setPosition(0f, 0f, -3.0f);

        mContainer.addChildObject(webViewObject);
    }

    private GVRWebViewSceneObject createWebViewObject(GVRContext gvrContext) {
        WebView webView = mActivity.getWebView();
        GVRWebViewSceneObject webObject = new GVRWebViewSceneObject(gvrContext,
                3.0f, 3.0f, webView);
        webObject.setName("webview-1");

        return webObject;
    }

    // make a scene object of type
    public void createObject(String type, String name) {
		GVRTexture texture = mContext.loadTexture(
				new GVRAndroidResource(mContext, R.raw.earthmap1k ));

        GVRSceneObject obj;
        if (type == "cube")
        	obj = new GVRCubeSceneObject(mContext);
        else if (type == "sphere")
        	obj = new GVRSphereSceneObject(mContext);
        else // default : plane
        	obj = new GVRSceneObject(mContext);

        obj.setName(name);

		GVRMaterial material = new GVRMaterial(mContext);
		material.setMainTexture(texture);
		obj.getRenderData().setMaterial(material);

        obj.getTransform().setPosition(2f, 0f, -2.0f);

        mContainer.addChildObject(obj);

        objects.put(name, obj);
    }

    float angle = 0f;
    float[] yAxis = { 0f, 1f, 0f };
    public void rotateObject(String name) {
    	GVRSceneObject obj = objects.get(name);
    	if (name == null)
    		return;

    	angle += 5f;

    	obj.getTransform().setRotationByAxis(angle, yAxis[0], yAxis[1], yAxis[2]);
    }

    public void moveObject(String name, float x, float y, float z) {
    	GVRSceneObject obj = objects.get(name);
    	if (name == null)
    		return;

    	obj.getTransform().setPosition(x, y, z);
    }

    public void scaleObject(String name, float x, float y, float z) {
    	GVRSceneObject obj = objects.get(name);
    	if (name == null)
    		return;

    	obj.getTransform().setScale(x, y, z);
    }

    public void setBackgroundColor(String colorStr) {
    	try {
    		int color = Color.parseColor(colorStr);
            mRig.getLeftCamera().setBackgroundColor(color);
            mRig.getRightCamera().setBackgroundColor(color);
    	} catch (IllegalArgumentException e) {
    		Log.w(TAG, "Exception : " + e);
    	}
    }

    public void setBackgroundImage(String imageUrl) {

    }

    boolean needToMakeObject = false;
    boolean objCreated = false;
    String objType = "cube";

    @Override
    public void onStep() {
    	if (needToMakeObject && !objCreated) {
    		this.createObject(objType, "cube");
    		needToMakeObject = false;
    		objCreated = true;
    	}


		pickedObjects = GVRPicker.findObjects(mScene, 0f,0f,0f, 0f,0f,-1f);

		for (GVRPicker.GVRPickedObject pickedObject : pickedObjects) {

		}
    }

    public void onPause() {

    }

    public void onKeyDown(int keyCode, KeyEvent event) {

    }

    public void onKeyUp(int keyCode, KeyEvent event) {

    }

    public void onSingleTap(MotionEvent event) {
    	needToMakeObject = true;
    }

    public void onButtonDown() {

    }

    public void onLongButtonPress(int keyCode) {

    }

    public void onTouchEvent(MotionEvent event) {

    }

	public boolean onSwipe(MotionEvent e, SwipeDirection swipeDirection,
			float velocityX, float velocityY) {
		return true;
	}

}