package com.navigatevr;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDrawFrameListener;
import org.gearvrf.GVRExternalTexture;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMaterial.GVRShaderType;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditTextSceneObject extends GVRSceneObject {
   private static final int REFRESH_INTERVAL = 20; // frames

   private static final int DEFAULT_WIDTH = 1024;
   private static final int DEFAULT_HEIGHT = 1024;
   private static final float DEFAULT_QUAD_WIDTH = 1.0f;
   private static final float DEFAULT_QUAD_HEIGHT = 1.0f;
   private static final String DEFAULT_TEXT = "";

   private final Surface mSurface;
   private final SurfaceTexture mSurfaceTexture;
   private final LinearLayout mTextViewContainer;
   private final EditText mTextView;

   private int mCount = 0;

   /**
    * Shows a {@link TextView} on a {@linkplain GVRSceneObject scene object}.
    *
    * @param gvrContext
    *            current {@link GVRContext}
    * @param mesh
    *            A {@link GVRMesh} - see
    *            {@link GVRContext#loadMesh(org.gearvrf.GVRAndroidResource)}
    *            and {@link GVRContext#createQuad(float, float)}.
    *
    *            Please note that this mesh controls the size of your scene
    *            object, and it is independent of the size of the internal
    *            {@code TextView}: a large mismatch between the scene object's
    *            size and the view's size will result in 'spidery' or 'blocky'
    *            text.
    * @param gvrActivity
    *            a {@link GVRActivity}
    * @param viewWidth
    *            Width of the {@link TextView}
    * @param viewHeight
    *            Height of the {@link TextView}
    * @param text
    *            {@link CharSequence} to show on the textView
    */
   public EditTextSceneObject(GVRContext gvrContext, GVRMesh mesh,
           GVRActivity gvrActivity, int viewWidth, int viewHeight,
           CharSequence text) {
       super(gvrContext, mesh);

       mTextView = new EditText(gvrActivity);
       mTextView.setLayoutParams(new LayoutParams(viewWidth, viewHeight));
       mTextView.measure(viewWidth, viewHeight);
       mTextView.setBackgroundColor(Color.TRANSPARENT);
       mTextView.setText(text);
       mTextView.setVisibility(View.VISIBLE);

       mTextViewContainer = new LinearLayout(gvrActivity);
       mTextViewContainer.addView(mTextView);
       mTextViewContainer.measure(viewWidth, viewHeight);
       mTextViewContainer.layout(0, 0, viewWidth, viewHeight);
       mTextViewContainer.setVisibility(View.VISIBLE);

       gvrContext.registerDrawFrameListener(mFrameListener);

       GVRTexture texture = new GVRExternalTexture(gvrContext);
       GVRMaterial material = new GVRMaterial(gvrContext, GVRShaderType.OES.ID);
       material.setMainTexture(texture);
       getRenderData().setMaterial(material);

       mSurfaceTexture = new SurfaceTexture(texture.getId());
       mSurface = new Surface(mSurfaceTexture);
       mSurfaceTexture.setDefaultBufferSize(mTextViewContainer.getWidth(),
               mTextViewContainer.getHeight());
   }

   /**
    * Shows a {@link TextView} on a {@linkplain GVRSceneObject scene object}.
    *
    * @param gvrContext
    *            current {@link GVRContext}
    * @param gvrActivity
    *            a {@link GVRActivity}
    * @param width
    *            Scene object height, in GVRF scene graph units.
    *
    *            Please note that your scene object's size, is independent of
    *            the size of the internal {@code TextView}: a large mismatch
    *            between the scene object's size and the view's size will
    *            result in 'spidery' or 'blocky' text.
    *
    * @param height
    *            Scene object's width, in GVRF scene graph units.
    * @param viewWidth
    *            Width of the {@link TextView}
    * @param viewHeight
    *            Height of the {@link TextView}
    * @param text
    *            {@link CharSequence} to show on the textView
    */
   public EditTextSceneObject(GVRContext gvrContext,
           GVRActivity gvrActivity, float width, float height, int viewWidth,
           int viewHeight, CharSequence text) {
       this(gvrContext, gvrContext.createQuad(width, height), gvrActivity,
               viewWidth, viewHeight, text);
   }

   /**
    * Shows a {@link TextView} on a {@linkplain GVRSceneObject scene object}
    * with view default height and width.
    *
    * @param gvrContext
    *            current {@link GVRContext}
    * @param mesh
    *            a {@link GVRMesh} - see
    *            {@link GVRContext#loadMesh(org.gearvrf.GVRAndroidResource)}
    *            and {@link GVRContext#createQuad(float, float)}
    *
    *            Please note that this mesh controls the size of your scene
    *            object, and it is independent of the size of the internal
    *            {@code TextView}: a large mismatch between the scene object's
    *            size and the view's size will result in 'spidery' or 'blocky'
    *            text.
    * @param gvrActivity
    *            a {@link GVRActivity}
    * @param text
    *            {@link CharSequence} to show on the textView
    */
   public EditTextSceneObject(GVRContext gvrContext, GVRMesh mesh,
           GVRActivity gvrActivity, CharSequence text) {
       this(gvrContext, mesh, gvrActivity, DEFAULT_WIDTH, DEFAULT_HEIGHT, text);
   }

   /**
    * Shows a {@link TextView} on a {@linkplain GVRSceneObject scene object}
    * with view's default height and width.
    *
    * @param gvrContext
    *            current {@link GVRContext}
    * @param gvrActivity
    *            a {@link GVRActivity}
    * @param width
    *            Scene object height, in GVRF scene graph units.
    *
    *            Please note that your scene object's size, is independent of
    *            the size of the internal {@code TextView}: a large mismatch
    *            between the scene object's size and the view's size will
    *            result in 'spidery' or 'blocky' text.
    *
    * @param height
    *            Scene object's width, in GVRF scene graph units.
    * @param text
    *            {@link CharSequence} to show on the textView
    */
   public EditTextSceneObject(GVRContext gvrContext,
           GVRActivity gvrActivity, float width, float height,
           CharSequence text) {
       this(gvrContext, gvrContext.createQuad(width, height), gvrActivity,
               text);
   }

   /**
    * Shows a {@link TextView} on a {@linkplain GVRSceneObject scene object}
    * with both view's default height and width and quad's default height and
    * width.
    *
    * @param gvrContext
    *            current {@link GVRContext}
    * @param gvrActivity
    *            a {@link GVRActivity}
    * @param text
    *            {@link CharSequence} to show on the textView
    */
   public EditTextSceneObject(GVRContext gvrContext,
           GVRActivity gvrActivity, CharSequence text) {
       this(gvrContext, gvrActivity, DEFAULT_QUAD_WIDTH, DEFAULT_QUAD_HEIGHT,
               text);
   }

   /**
    * Shows a {@link TextView} on a {@linkplain GVRSceneObject scene object}
    * with both view's default height and width and quad's default height and
    * width. The initial text will be the private {@code DEFAULT_TEXT}
    * constant, or {@code ""}.
    *
    * @param gvrContext
    *            current {@link GVRContext}
    * @param gvrActivity
    *            a {@link GVRActivity}
    */
   public EditTextSceneObject(GVRContext gvrContext, GVRActivity gvrActivity) {
       this(gvrContext, gvrActivity, DEFAULT_TEXT);
   }

   public EditText getTextView() {
    return mTextView;
   }

   /**
    * Set the text size.
    *
    * @param newSize
    *            The new text size.
    */
   public void setTextSize(float size) {
       mTextView.setTextSize(size);
   }

   /**
    * Get the current text size.
    *
    * @return The current text size.
    */
   public float getTextSize() {
       return mTextView.getTextSize();
   }

   /**
    * Set the text color.
    *
    * @param color
    *            The text color, in Android {@link Color} format. The
    *            {@linkplain Color#alpha(int) alpha component} is ignored.
    */
   public void setTextColor(int color) {
       mTextView.setTextColor(color);
   }

   /**
    * Set the text to be displayed.
    *
    * @param text
    *            The new text to be displayed.
    */
   public void setText(CharSequence text) {
       mTextView.setText(text);
   }

   /**
    * Get the current text.
    *
    * @return The text that is currently displayed.
    */
   public CharSequence getText() {
       return mTextView.getText();
   }

   /**
    * Get the current text, as a {@code String}.
    *
    * This is a convenience function, 100% equivalent to {@link #getText()}
    * {@code .toString()}
    *
    * @return The text that is currently displayed.
    */
   public String getTextString() {
       return getText().toString();
   }

   /**
    * Set the view's background color.
    *
    * @param color
    *            The view's background color, in Android {@link Color} format.
    *            The {@linkplain Color#alpha(int) alpha component} is ignored.
    */
   public void setBackgroundColor(int color) {
       mTextViewContainer.setBackgroundColor(color);
   }

   /**
    * Set the view's background {@code Drawable}.
    *
    * @param drawable
    *            The view's background. {@code null} will clear any current
    *            background {@code Drawable}.
    */
   public void setBackGround(Drawable drawable) {
       mTextViewContainer.setBackground(drawable);
   }

   /**
    * Get the view's background {@code Drawable}, if any.
    *
    * @param drawable
    *            The view's background; may be {@code null}.
    */
   public Drawable getBackGround() {
       return mTextViewContainer.getBackground();
   }

   /**
    * Set the view's gravity.
    *
    * @param gravity
    *            The gravity of the internal TextView
    */
   public void setGravity(int gravity) {
       mTextView.setGravity(gravity);
   }

   /**
    * Get the view's gravity.
    *
    * @return The gravity of the internal TextView
    */
   public int getGravity() {
       return mTextView.getGravity();
   }

   private final GVRDrawFrameListener mFrameListener = new GVRDrawFrameListener() {
       @Override
       public void onDrawFrame(float frameTime) {
           if (++mCount > REFRESH_INTERVAL) {
               refresh();
               mCount = 0;
           }
       }
   };

   /** Draws the {@link WebView} onto {@link #mSurfaceTexture} */
   private void refresh() {
       try {
           Canvas canvas = mSurface.lockCanvas(null);
           canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
           mTextViewContainer.draw(canvas);
           mSurface.unlockCanvasAndPost(canvas);
       } catch (Surface.OutOfResourcesException t) {
           Log.e("GVRTextViewObject", "lockCanvas failed");
       }
       mSurfaceTexture.updateTexImage();
   }

}
