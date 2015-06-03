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
    public GVRSceneObject createObject(String type, String name) {
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

        objects.put(name, obj);

        return obj;
    }

    float[] yAxis = { 0f, 1f, 0f };
    public void rotateObject(String name, float angle, float x, float y, float z) {
    	GVRSceneObject obj = objects.get(name);
    	if (obj == null)
    		return;

    	obj.getTransform().setRotationByAxis(angle, x,y,z);
    }

    public void moveObject(String name, float x, float y, float z) {
    	GVRSceneObject obj = objects.get(name);
    	if (obj == null)
    		return;

    	obj.getTransform().setPosition(x, y, z);
    }

    public void translateObject(String name, float x, float y, float z) {
    	GVRSceneObject obj = objects.get(name);
    	if (obj == null)
    		return;

    	obj.getTransform().translate(x, y, z);
    }

    public void scaleObject(String name, float x, float y, float z) {
    	GVRSceneObject obj = objects.get(name);
    	if (obj == null)
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

    @Override
    public void onStep() {
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

    // temp
    String objType = "cube";
    float yPos = 0f;

    public void onSingleTap(MotionEvent event) {
    	mContext.runOnGlThread(new Runnable() {
    		@Override
			public void run() {
        		GVRSceneObject obj = createObject(objType, "cube");

                obj.getTransform().setPosition(2f, yPos, -2.0f);
                mContainer.addChildObject(obj);

                yPos += 2f;
    		}
    	});
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