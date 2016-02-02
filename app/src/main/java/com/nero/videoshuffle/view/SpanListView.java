package com.nero.videoshuffle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by nlang on 1/25/2016.
 */
public class SpanListView extends ListView implements GestureDetector.OnGestureListener, View.OnTouchListener {
    public SpanListView(Context context) {
        this(context, null);
    }

    public SpanListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    GestureDetector mGesture;

    public SpanListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGesture = new GestureDetector(getContext(), this);
        this.setOnTouchListener(this);
    }

    boolean mIsDeleting = false;
    final int DELETESPACE = 10;
    int mSelection = -1;

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    float mLastPos;
    ViewGroup viewGroup;
    Button btn;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float flingPos = e2.getX() - mLastPos;
        if (e2.getX() < mLastPos && Math.abs(flingPos) >= DELETESPACE) {
            addDeleteButton();
        }
        return true;
    }

    private void addDeleteButton() {
        viewGroup = (ViewGroup) getChildAt(mSelection - getFirstVisiblePosition());
        btn = new Button(getContext());
        btn.setText("Delete");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        btn.setLayoutParams(layoutParams);
        viewGroup.addView(btn);
        mIsDeleting = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastPos = event.getX();
            mSelection = pointToPosition((int) event.getX(), (int) event.getY());
        }
        if (mIsDeleting && event.getAction() == MotionEvent.ACTION_DOWN) {
            viewGroup.removeView(btn);
            viewGroup = null;
            btn = null;
            mIsDeleting = false;
            return false;
        } else {
            if (event.getAction() == MotionEvent.ACTION_MOVE &&
                    !mIsDeleting &&
                    event.getX() < mLastPos &&
                    Math.abs(event.getX() - mLastPos) > DELETESPACE) {
                addDeleteButton();
                return true;
            }


            return mGesture.onTouchEvent(event);
        }
    }
}
