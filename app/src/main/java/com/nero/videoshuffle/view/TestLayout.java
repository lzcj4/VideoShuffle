package com.nero.videoshuffle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.nero.videoshuffle.R;

/**
 * TODO: document your custom view class.
 */
public class TestLayout extends LinearLayout {
    final String TAG = this.getClass().getSimpleName();

    public TestLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public TestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TestLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    ViewDragHelper viewDragHelper;

    private void init(AttributeSet attrs, int defStyle) {

        viewDragHelper = ViewDragHelper.create(this, dragCallback);
    }

    ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == testView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int result = Math.min(Math.max(left, getPaddingLeft()), getWidth() - child.getWidth() - getPaddingRight());
            return result;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int result = Math.min(Math.max(top, getPaddingTop()), getHeight() - child.getHeight() - getPaddingBottom());
            return result;
        }
    };

    TestView testView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        testView = (TestView) this.findViewById(R.id.testView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        Log.i(TAG, String.format("/*** dispatchTouchEvent=%s,action=%s ***/", result, ev.getAction()));
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        viewDragHelper.shouldInterceptTouchEvent(ev);
        Log.i(TAG, String.format("/--- onInterceptTouchEvent=%s,action=%s ---/", result, ev.getAction()));
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        viewDragHelper.processTouchEvent(event);
        Log.i(TAG, String.format("/+++ onTouchEvent=%s ,action=%s ++++/", result, event.getAction()));
        return true;
    }
}
