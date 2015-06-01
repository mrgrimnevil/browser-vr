package com.navigatevr;

import org.gearvrf.GVRActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

        createWebView();

        loadUrl("file:///android_asset/index.html");

        mScript = new BrowserScript(this);

        String deviceCfg = "gvr_note4.xml";

        setScript(mScript, deviceCfg);
    }

    @SuppressLint("SetJavaScriptEnabled")
	private void createWebView() {
        webView = new WebView(this);
        webView.setInitialScale(100);

        int width = 1000, height = 1000;

        webView.measure(width, height);
        webView.layout(0, 0, width, height);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setNeedInitialFocus(false);
        settings.setUserAgentString("Navigator/1.0");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }


    WebView getWebView() {
        return webView;
    }

    void loadUrl(String url) {
    	webView.loadUrl(url);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScript.onPause();
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

}
