package com.nero.videoshuffle.accessibility;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import rx.functions.Func1;

/**
 * Created by nlang on 3/7/2016.
 */
public class AccessibilityUtil {

    public static AccessibilityNodeInfo getNodeByClass(AccessibilityNodeInfo rootNode, Class<?> clazz) {
        if (rootNode == null) {
            return null;
        }

        AccessibilityNodeInfo result = null;
        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = rootNode.getChild(i);
            if (null != childNode)
                if (childNode.getClassName().equals(clazz.getName())) {
                    result = childNode;
                    break;
                } else if (childNode.getChildCount() > 0) {
                    result = getNodeByClass(childNode, clazz);
                    if (null != result) {
                        break;
                    }
                }
        }
        return result;
    }

    public static AccessibilityNodeInfo getNodeInRect(AccessibilityNodeInfo rootNode, Func1<AccessibilityNodeInfo, Boolean> func) {
        if (rootNode == null) {
            return null;
        }

        AccessibilityNodeInfo result = null;
        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = rootNode.getChild(i);
            if (null != childNode) {
                if (func.call(childNode)) {
                    result = childNode;
                    break;
                } else if (childNode.getChildCount() > 0) {
                    result = getNodeInRect(childNode, func);
                    if (null != result) {
                        break;
                    }
                }
            }
        }

        return result;
    }
}
