package com.navigatevr;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRSceneObject;

import android.graphics.Color;
import android.webkit.WebView;
import android.widget.EditText;

public class Browser {

    private static final String TAG = "Browser";

    private static int id = 0;

    private GVRSceneObject sceneObject;
    private NaviWebViewSceneObject webViewObject;

    private WebView webView;
    private EditText editText;

    public Browser(GVRContext gvrContext, GVRActivity gvrActivity,
            float width, float height, WebView webView) {
        sceneObject = new GVRSceneObject(gvrContext);

        this.webView = webView;

        webViewObject = new NaviWebViewSceneObject(gvrContext, width, height, webView);
        webViewObject.setName("webview-"+ id++);

        sceneObject.addChildObject(webViewObject);

        float ratio = 1f / 8f;

        // text navigation bar
        EditTextSceneObject navBar = new EditTextSceneObject(gvrContext, gvrActivity,
                width, width * ratio, 1024, (int)(1024 * ratio), "");

        editText = navBar.getTextView();
        editText.setHint("Web address:");

        navBar.setBackgroundColor(Color.WHITE);
        navBar.setTextSize(20);
        navBar.setTextColor(Color.BLACK);

        navBar.getTransform().setPosition(0f, -1.3f, 0f);

        sceneObject.addChildObject( navBar );
    }

    public WebView getWebView() {
        return webView;
    }

    public EditText getEditText() {
        return editText;
    }

    public GVRSceneObject getSceneObject() {
        return sceneObject;
    }

}
