package com.navigatevr;

import java.util.List;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScript;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.scene_objects.GVRWebViewSceneObject;

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

    private GVRSceneObject mContainer;

	List<GVRPicker.GVRPickedObject> pickedObjects;

    BrowserScript(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onInit(GVRContext gvrContext) {
    	mContext = gvrContext;

		mAnimationEngine = gvrContext.getAnimationEngine();

        mScene = gvrContext.getNextMainScene();
        mRig = mScene.getMainCameraRig();

        mContainer = new GVRSceneObject(gvrContext);
        mScene.addSceneObject(mContainer);

        GVRWebViewSceneObject webViewObject = createWebViewObject(gvrContext);

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

    public void onSingleTap(MotionEvent event) {

    }

    public void onButtonDown() {

    }

    public void onLongButtonPress(int keyCode) {

    }

    public void onTap() {

    }

    public void onTouchEvent(MotionEvent event) {

    }

	public boolean onSwipe(MotionEvent e, SwipeDirection swipeDirection,
			float velocityX, float velocityY) {
		return true;
	}

}