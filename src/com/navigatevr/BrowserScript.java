package com.navigatevr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScript;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;

import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.oculus.VRTouchPadGestureDetector.SwipeDirection;

public class BrowserScript extends GVRScript {

    private static final String TAG = "BrowserScript";

    private GVRAnimationEngine mAnimationEngine;

    private MainActivity mActivity;
    private GVRContext mContext;

    private GVRScene mScene;
    private GVRCameraRig mRig;

    private List<GVRPicker.GVRPickedObject> pickedObjects;
    private float[] hitLocation = new float[3];

    private GVRSceneObject mContainer;

    private Cursor mCursor;

    private Browser browser;
    private boolean browserFocused = false;

    private EditText editText;
    private boolean activated = false;

    private boolean buttonFocused = false;
    private GVRSceneObject focusedButton = null;

    private float DEFAULT_OPACITY = 0.3f;
    private float FOCUSED_OPACITY = 0.9f;

    private Map<String, GVRSceneObject> objects = new HashMap<String, GVRSceneObject>();
    private Map<String, String> dict = new HashMap<String, String>();

    private Queue<String> taskQueue = new ArrayBlockingQueue<String>(16);

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

        mCursor = new Cursor(mContext);
        mRig.getOwnerObject().addChildObject(mCursor.getSceneObject());

        createBrowser();
    }

    public void createBrowser() {
        float distance = 3f;

        float width = 2f;
        float height = 2f;

        WebView webView = mActivity.getWebView();
        browser = new Browser(mContext, mActivity, width, height, webView);

        editText = browser.getEditText();

        GVRSceneObject screenObject = browser.getScreenObject();
        attachDefaultEyePointee(screenObject);

        browser.getSceneObject().getTransform().setPosition(0f, 0f, -distance);
        //webViewObject.pauseRender();

        mContainer.addChildObject( browser.getSceneObject() );


        // nav buttons
        String[] buttons = { "reload", "back", "forward" };
        int[] buttonTextures = { R.raw.refresh_button, R.raw.back_button, R.raw.forward_button };

        for (int i = 0; i < buttons.length; i++) {
            GVRTexture texture = mContext.loadTexture(
                    new GVRAndroidResource(mContext, buttonTextures[i] ));

            GVRSceneObject buttonObject = new GVRSceneObject(mContext, 0.3f, 0.3f, texture);
            buttonObject.setName( buttons[i] );
            buttonObject.getRenderData().getMaterial().setOpacity(DEFAULT_OPACITY);
            attachDefaultEyePointee(buttonObject);

            buttonObject.getTransform().setPosition(-1.3f, 0.85f - 0.5f*i, -distance);
            mContainer.addChildObject(buttonObject);
        }
    }


    // make a scene object of type
    public GVRSceneObject createObject(String name, String type) {
        GVRTexture texture = mContext.loadTexture(
                new GVRAndroidResource(mContext, R.raw.earthmap1k ));

        GVRSceneObject obj;
        if (type == "cube")
            obj = new GVRCubeSceneObject(mContext);
        else if (type == "sphere")
            obj = new GVRSphereSceneObject(mContext);
        else // default : plane
            obj = new GVRSceneObject(mContext);

        //obj.setName(name);

        GVRMaterial material = new GVRMaterial(mContext);
        material.setMainTexture(texture);
        obj.getRenderData().setMaterial(material);

        objects.put(name, obj);

        return obj;
    }

    public void rotateObject(String name, float angle, float x, float y, float z) {
        GVRSceneObject obj = objects.get(name);
        if (obj == null)
            return;

        obj.getTransform().setRotationByAxis(angle, x,y,z);
    }

    public void rotationObject(String name, float w, float x, float y, float z) {
        GVRSceneObject obj = objects.get(name);
        if (obj == null)
            return;

        obj.getTransform().setRotation(w, x, y, z);
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

    public String getValue(String key) {
        return dict.get(key);
    }

    public void setValue(String key, String value) {
        dict.put(key, value);
    }

    @Override
    public void onStep() {
        processTaskQueue();

        pickedObjects = GVRPicker.findObjects(mScene, 0f,0f,0f, 0f,0f,-1f);

        for (GVRPicker.GVRPickedObject pickedObject : pickedObjects) {
            GVRSceneObject obj = pickedObject.getHitObject();

            hitLocation = pickedObject.getHitLocation();

            if (obj instanceof NaviWebViewSceneObject) {
                browserFocused = true;
                buttonFocused = false;

                /*String coords =
                        String.format("%.3g%n", hitLocation[0]) + "," +
                        String.format("%.3g%n", hitLocation[1]);

                editText.setText(coords);*/
            } else {
                browserFocused = false;
                buttonFocused = true;

                focusedButton = obj;

                focusedButton.getRenderData().getMaterial().setOpacity(FOCUSED_OPACITY);
            }

            break;
        }

        // reset
        if (pickedObjects.size() == 0) {
            browserFocused = false;
            buttonFocused = false;

            if (focusedButton != null)
                focusedButton.getRenderData().getMaterial().setOpacity(DEFAULT_OPACITY);
        }
    }

    public void processTaskQueue() {
        if (taskQueue.size() != 0) {
            String task = taskQueue.poll();

            String[] pieces = task.split(":");
            if (pieces.length == 2) {
                String name = pieces[0];
                String type = pieces[1];

                if (type.equals("cube")) // temp
                    create(name, "cube");
            }
        }
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (!activated) {
            editText.setActivated(true);
            editText.requestFocus();
            activated = true;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            String navText = editText.getText().toString();

            if ( Patterns.WEB_URL.matcher(navText).matches() ) {
                if (!navText.toLowerCase().startsWith("http://"))
                    navText = "http://" + navText;
                browser.getWebView().loadUrl(navText);
            }

            return;
        }

        editText.dispatchKeyEvent(event);
    }

    public void onKeyUp(int keyCode, KeyEvent event) {
        if (!activated) {
            editText.setActivated(true);
            editText.requestFocus();
            activated = true;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return;
        }

        editText.dispatchKeyEvent(event);
    }

    public void click() {
        WebView webView = browser.getWebView();

        final long uMillis = SystemClock.uptimeMillis();

        int width = 1024, height = 1024; // defined in MainActivity

        float hitX = this.hitLocation[0];
        float hitY = this.hitLocation[1] * -1f;

        float x = (hitX + 1f) * width/2;
        float y = (hitY + 1f) * height/2;

        webView.dispatchTouchEvent(MotionEvent.obtain(uMillis, uMillis,
                MotionEvent.ACTION_DOWN, x, y, 0));
        webView.dispatchTouchEvent(MotionEvent.obtain(uMillis, uMillis,
                MotionEvent.ACTION_UP, x, y, 0));
    }

    public void scroll(int direction, float velocity) {
        WebView webView = browser.getWebView();

        int dy = direction * 10;

        webView.scrollBy(0, dy);
    }

    public void refreshWebView() {
        Toast.makeText(mActivity, "refresh", Toast.LENGTH_SHORT).show();
        browser.getWebView().reload();
    }

    public void navigateForward() {
        Toast.makeText(mActivity, "forward", Toast.LENGTH_SHORT).show();

        if (browser.getWebView().canGoForward())
            browser.getWebView().goForward();
    }

    public void navigateBack() {
        Toast.makeText(mActivity, "back", Toast.LENGTH_SHORT).show();

        if (browser.getWebView().canGoBack())
            browser.getWebView().goBack();
    }

    public void pageUp() {
        browser.getWebView().pageUp(false);
    }

    public void pageDown() {
        browser.getWebView().pageDown(false);
    }

    public void createNewObject(String name, String type, String texture) {
        taskQueue.add(name+":"+type);
    }

    public void create(String name, String type) {

        class CreateTask implements Runnable {
            final String name;
            final String type;
            public CreateTask(String _name, String _type) {
                this.name = _name;
                this.type = _type;
            }

            @Override
            public void run() {
                GVRSceneObject obj = createObject(this.name, this.type);

                mContainer.addChildObject(obj);
            }
        }

        CreateTask ct = new CreateTask(name, type);

        mContext.runOnGlThread(ct);
    }

    public void onSingleTap(MotionEvent event) {
        if (browserFocused) {
            click();
        } else if (buttonFocused) {
            String buttonAction = focusedButton.getName();

            if (buttonAction.equals("reload")) {
                refreshWebView();
            } else if (buttonAction.equals("forward")) {
                navigateForward();
            } else if (buttonAction.equals("back")) {
                navigateBack();
            }
        } else {

        }
    }

    public void onButtonDown() {

    }

    public void onLongButtonPress(int keyCode) {

    }

    public void onTouchEvent(MotionEvent event) {

    }

    public boolean onSwipe(MotionEvent e, SwipeDirection swipeDirection,
            float velocityX, float velocityY) {

        switch (swipeDirection) {
            case Up:
                scroll(-1, velocityY);
                break;
            case Down:
                scroll(1, velocityY);
                break;
            case Forward:
                break;
            case Backward:
                break;
        }
        return true;
    }

    protected void attachDefaultEyePointee(GVRSceneObject sceneObject) {
        GVREyePointeeHolder eyePointeeHolder = new GVREyePointeeHolder(mContext);
        GVRMesh mesh = sceneObject.getRenderData().getMesh();
        GVRMeshEyePointee eyePointee = new GVRMeshEyePointee(mContext, mesh);
        eyePointeeHolder.addPointee(eyePointee);
        sceneObject.attachEyePointeeHolder(eyePointeeHolder);
    }

    // for debug, hide
    /*@Override
    public SplashMode getSplashMode() {
        return SplashMode.NONE;
    }*/

    // custom splash
    /*@Override
    public GVRTexture getSplashTexture(GVRContext ctx) {
        GVRTexture tex = ctx.loadTexture(
                new GVRAndroidResource( ctx, R.raw.? ) );
        return tex;
    }*/


}