package com.nero.videoshuffle.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;

import com.nero.videoshuffle.R;

/**
 * Created by nlang on 1/21/2016.
 */
public class CircleImage extends View {
    public CircleImage(Context context) {
        this(context, null);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    int mRingColor = Color.parseColor("#ff000000");
    float mRadius = 0;

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleImage);
        mRingColor = ta.getColor(R.styleable.CircleImage_ring_color, 0);
        mRadius = ta.getDimension(R.styleable.CircleImage_radius, 0);
        initPaint();
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    Paint mMainPaint, mSecondPaint;
    Bitmap mBitmap;
    int mStrokeWidth = 5;

    private void initPaint() {
        mMainPaint = new Paint();
        mMainPaint.setAntiAlias(true);
        mMainPaint.setColor(mRingColor);
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setStrokeWidth(mStrokeWidth);
//        mMainPaint.setStyle(Paint.Style.FILL);


        mSecondPaint = new Paint();
        mSecondPaint.setColor(Color.parseColor("#8800ff00"));
        mSecondPaint.setAntiAlias(true);
        mSecondPaint.setStrokeWidth(mStrokeWidth);
        //mSecondPaint.setStyle(Paint.Style.FILL);

        int w = getWidth();
        int h = getHeight();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float d = dm.density;
        float dd = dm.densityDpi;
        int hp = dm.heightPixels;
        int wp = dm.widthPixels;
    }

    private float getRadius() {
        float w = (getWidth() - getPaddingLeft() - getPaddingRight() - mStrokeWidth - mSecondRadius * 2) / 2.0f;
        float h = (getHeight() - getPaddingTop() - getPaddingBottom() - mStrokeWidth - mSecondRadius * 2) / 2.0f;

        float radius = Math.min(w, h);
        return radius;
    }

    float mDegree = 0;
    float mSecondRadius = 30f;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = getRadius();
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mMainPaint);

        float x = (float) (Math.cos(Math.PI / 180 * mDegree) * radius);
        float y = (float) (Math.sin(Math.PI / 180 * mDegree) * radius);
        int rc = canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.drawCircle(x, y, mSecondRadius, mSecondPaint);
        canvas.restoreToCount(rc);
        startAnimation();

//        canvas.drawCircle(w / 2, h / 2, mRadius, mMainPaint);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.outWidth = (int) mRadius;
//        options.outHeight = (int) mRadius;
//        options.inScaled = true;
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.t, options);
//        canvas.drawBitmap(bitmap, 0, 0, mMainPaint);
    }

    boolean isAnimationing = false;

    private void startAnimation() {
        if (!isAnimationing) {
            ValueAnimator va = ValueAnimator.ofObject(new TypeEvaluator() {
                @Override
                public Object evaluate(float fraction, Object startValue, Object endValue) {
                    float startInt = (float) startValue;
                    float endInt = (float) endValue;
                    return startInt + (endInt - startInt) * fraction;
                }
            }, 0f, 360f);

            va.setDuration(3 * 1000);

            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    mDegree = f;
                    invalidate();
                }
            });
            va.setRepeatMode(ValueAnimator.RESTART);
            va.setRepeatCount(ValueAnimator.INFINITE);
            va.start();
            isAnimationing = true;
        }
    }

    DisplayMetrics dm = getResources().getDisplayMetrics();
    float d = dm.density;

    public int px2dp(int px) {
        return (int) (px / dm.density + 0.5);
    }
}
