package com.navigatevr;

import org.gearvrf.GVRActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.oculus.VRTouchPadGestureDetector;
import com.oculus.VRTouchPadGestureDetector.OnTouchPadGestureListener;
import com.oculus.VRTouchPadGestureDetector.SwipeDirection;

public class MainActivity extends GVRActivity implements OnTouchPadGestureListener {

    private static final String TAG = "MainActivity";

    private VRTouchPadGestureDetector mDetector = null;

    private static final int BUTTON_INTERVAL = 1000;
    private static final int TAP_INTERVAL = 300;

    private long mLatestButton = 0;
    private long mLatestTap = 0;

    private BrowserScript mScript;

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetector = new VRTouchPadGestureDetector(this);
        this.takeKeyEvents(true);

        createWebView();

        String base = "file:///android_asset/web/";
        String url = base+"index.html";
        loadUrl(url);

        mScript = new BrowserScript(this);

        String deviceCfg = "gvr_note4.xml";

        setScript(mScript, deviceCfg);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {

        // WebView.enableSlowWholeDocumentDraw();

        webView = new WebView(this);

        webView.setInitialScale(100);

        int width = 1024, height = 1024;

        webView.measure(width, height);
        webView.layout(0, 0, width, height);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setNeedInitialFocus(false);
        //settings.setUserAgentString("Navigator/1.0");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebView.setWebContentsDebuggingEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(this), "NAVI");

    }


    WebView getWebView() {
        return webView;
    }

    void loadUrl(String url) {
        webView.loadUrl(url);
    }

    void injectJS(String js) {
        if (!js.startsWith("javascript"))
            js = "javascript:"+js;
        webView.loadUrl(js);
    }

    void injectScript(String jsPath) {
        //
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.pauseTimers();
        mScript.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.resumeTimers();
        mScript.onResume();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > mLatestButton + BUTTON_INTERVAL) {
            mLatestButton = System.currentTimeMillis();
            mScript.onButtonDown();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.v(TAG, "onLongPress");
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mLatestButton = System.currentTimeMillis();
            mScript.onLongButtonPress(keyCode);
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mScript.onKeyDown(keyCode, event);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            mScript.onKeyUp(keyCode, event);
        }

        return false;
    }

    @Override
    public boolean onSingleTap(MotionEvent event) {
        Log.v(TAG, "onSingleTap");
        if (System.currentTimeMillis() > mLatestTap + TAP_INTERVAL) {
            mLatestTap = System.currentTimeMillis();
            mScript.onSingleTap(event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        mScript.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onSwipe(MotionEvent e, SwipeDirection swipeDirection,
            float velocityX, float velocityY) {
        mScript.onSwipe(e, swipeDirection, velocityX, velocityY);
        Log.v(TAG, "onSwipe");
        return false;
    }

    // WebView interface
    class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        public void refreshWebView() {
            mScript.refreshWebView();
        }

        @JavascriptInterface
        public String getValue(String key) {
            return mScript.getValue(key);
        }

        @JavascriptInterface
        public void setValue(String key, String value) {
            mScript.setValue(key, value);
        }

        @JavascriptInterface
        public void setBackgroundColor(String color) {
            mScript.setBackgroundColor(color);
        }

        @JavascriptInterface
        public void setBackgroundImage(String imageUrl) {
            mScript.setBackgroundImage(imageUrl);
        }

        @JavascriptInterface
        public void rotateObject(String name, float angle, float x, float y, float z) {
            mScript.rotateObject(name, angle, x,y,z);
        }

        public void rotationObject(String name, float w, float x, float y, float z) {
            mScript.rotationObject(name, w,x,y,z);
        }

        @JavascriptInterface
        public void moveObject(String name, float x, float y, float z) {
            mScript.moveObject(name, x,y,z);
        }

        @JavascriptInterface
        public void translateObject(String name, float x, float y, float z) {
            mScript.translateObject(name, x,y,z);
        }

        @JavascriptInterface
        public void scaleObject(String name, float x, float y, float z) {
            mScript.scaleObject(name, x,y,z);
        }

        @JavascriptInterface
        public void createObject(String name, String type, String texture) {
            mScript.createNewObject(name, type, texture);
        }
    }

}
