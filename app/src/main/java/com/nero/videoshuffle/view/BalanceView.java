package com.nero.videoshuffle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nero.videoshuffle.R;

/**
 * TODO: document your custom view class.
 */
public class BalanceView extends RelativeLayout {
    private int mMainColor = Color.RED;
    private int mSecondColor = Color.BLUE;
    private int mThirdColor = Color.GREEN;
    private int mPosition = 0;

    TextView mMainText, mSecondText, mSeeker;

    private TextPaint mTextPaint;

    public BalanceView(Context context) {
        super(context);
        init(null, 0);
    }

    public BalanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BalanceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    public int getSecondColor() {
        return mSecondColor;
    }

    public void setSecondColor(int secondColor) {
        this.mSecondColor = secondColor;
        invalidate();
    }

    public int getMainColor() {
        return mMainColor;
    }

    public void setMainColor(int mainColor) {
        this.mMainColor = mainColor;
        invalidate();
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int pos) {
        this.mPosition = pos;
        invalidate();
    }


    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BalanceView, defStyle, 0);

        mMainColor = a.getColor(
                R.styleable.BalanceView_mainColor,
                mMainColor);

        mSecondColor = a.getColor(
                R.styleable.BalanceView_secondColor,
                mSecondColor);

        mThirdColor = a.getColor(
                R.styleable.BalanceView_thirdColor,
                mThirdColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mPosition = a.getInt(
                R.styleable.BalanceView_position,
                mPosition);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mPosition);
        mTextPaint.setColor(mMainColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        addChildren();
    }

    private int getContentHeight() {
        return Math.round(getHeight() / 4);
    }

    private void addChildren() {

        float radius = getContentHeight();

        mMainText = new TextView(getContext());

        addProgressText(mMainText, mMainColor, radius);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(getWidth() / 2, getContentHeight());
        addView(mMainText, layoutParams);

        mSecondText = new TextView(getContext());
        addProgressText(mSecondText, mSecondColor, radius);
        layoutParams = new LayoutParams(getWidth() / 2, getContentHeight());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(mSecondText, layoutParams);

        mSeeker = new TextView(getContext());

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setColor(mThirdColor);
        bgDrawable.setCornerRadius(getHeight() / 2);
        mSeeker.setBackground(bgDrawable);

        seekerLayoutParams = new LayoutParams(getHeight() / 2, getHeight() / 2);
        seekerLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, mMainText.getId());
        mSeeker.setLayoutParams(seekerLayoutParams);
        addView(mSeeker);
    }

    LayoutParams seekerLayoutParams;

    private void addProgressText(TextView textView, int color, float radius) {
        textView.setGravity(Gravity.CENTER);
        textView.setText("50");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setColor(color);
        bgDrawable.setCornerRadius(radius);
        textView.setBackground(bgDrawable);
    }

    int lastX = 0;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Rect hitRec = new Rect();
        mSeeker.getHitRect(hitRec);
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = x;
        }

        if (hitRec.contains(x, y) && event.getAction() == MotionEvent.ACTION_MOVE) {
            int offset = x - lastX;
            seekerLayoutParams.leftMargin += offset;
            if (seekerLayoutParams.leftMargin <= 0) {
                seekerLayoutParams.leftMargin = 0;
            } else if (seekerLayoutParams.leftMargin + seekerLayoutParams.width > getMeasuredWidth()) {
                seekerLayoutParams.leftMargin = getMeasuredWidth() - seekerLayoutParams.width;
            }
            updateViewLayout(mSeeker, seekerLayoutParams);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;


    }

}
