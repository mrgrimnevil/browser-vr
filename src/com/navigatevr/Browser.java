package com.navigatevr;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRSceneObject;

import android.webkit.WebView;

public class Browser {

    private static final String TAG = "Browser";

    private static int id = 0;

    private GVRSceneObject sceneObject;
    private NaviWebViewSceneObject webViewObject;

    public Browser(GVRContext gvrContext, WebView webView) {
        sceneObject = new GVRSceneObject(gvrContext);

        webViewObject = new NaviWebViewSceneObject(gvrContext, 3f, 3f, webView);
        webViewObject.setName("webview-"+ id++);

        sceneObject.addChildObject(webViewObject);
    }

    public GVRSceneObject getSceneObject() {
        return sceneObject;
    }

}
