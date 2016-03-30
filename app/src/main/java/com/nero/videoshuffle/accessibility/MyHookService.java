package com.nero.videoshuffle.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.ImageView;
import android.widget.Switch;

import java.util.List;

/**
 * Created by nlang on 3/7/2016.
 */
public class MyHookService extends AccessibilityService {
    final String TAG = "MyHookService";

    public MyHookService() {
        super();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected()");
    }

    @Override
    protected boolean onGesture(int gestureId) {
        Log.i(TAG, String.format("onGesture() ,gestureId:%s", gestureId));
        return super.onGesture(gestureId);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.i(TAG, String.format("onKeyEvent() ,KeyEvent:%s", event.toString()));
        return super.onKeyEvent(event);
    }

    @Override
    public List<AccessibilityWindowInfo> getWindows() {
        Log.i(TAG, "getWindows");
        return super.getWindows();
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        Log.i(TAG, "getRootInActiveWindow");
        return super.getRootInActiveWindow();
    }

    @Override
    public AccessibilityNodeInfo findFocus(int focus) {
        Log.i(TAG, String.format("findFocus() ,focus:%s", focus));
        return super.findFocus(focus);
    }

    @Override
    public Object getSystemService(String name) {
        Log.i(TAG, String.format("getSystemService() ,name:%s", name));
        return super.getSystemService(name);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, String.format("onAccessibilityEvent() ,AccessibilityEvent:%s", event.toString()));
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (null != rootNode) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("登 录");
            if (null != list) {
                for (AccessibilityNodeInfo node : list) {
                    Log.i(TAG, "================Login==================");
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                }
            }


            //AccessibilityNodeInfo node = AccessibilityUtil.getNodeByClass(rootNode, ImageView.class);

            AccessibilityNodeInfo node = AccessibilityUtil.getNodeInRect(rootNode, (childNode) -> {
                        boolean result = false;
                        if (childNode.getClassName().equals(ImageView.class.getName())) {
                            Rect rect = new Rect();
                            childNode.getBoundsInScreen(rect);
                            if (rect.width() > 350 && rect.width() < 450) {
                                result = true;
                            }
                        }
                        return result;
                    }
            );
            if (null != node) {
                Log.i(TAG, "================Action==================");
                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                node.performAction(AccessibilityNodeInfo.ACTION_SELECT);
                node.performAction(AccessibilityNodeInfo.ACTION_COPY);


                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData data = cm.getPrimaryClip();
                    int count = data.getItemCount();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
