package com.nero.videoshuffle.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by nlang on 3/29/2016.
 */
public class Chart extends View {
    public Chart(Context context) {
        this(context, null);
    }

    public Chart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intial();
    }

    Paint mPaint;

    private void intial() {
        mPaint = new Paint();
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
    }

    final int DEFAULTPADDING = 20;
    final float DEFAULTRADIUS = 100f;
    final int DEFAULTARMLEN = 150;
    final int DEFAULTBODYLEN = 200;
    final int DEFAULTLEGLEN = 200;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        startAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (i++ % 2 == 0) {
            drawStand(canvas);
        } else {
            drawWalk(canvas);
        }
    }

    private void drawWalk(Canvas canvas) {
        final int getWidth = getMeasuredWidth();
        final int getPaddingTop = getPaddingTop() == 0 ? DEFAULTPADDING : getPaddingTop();
        final int getPaddingStart = getPaddingLeft() == 0 ? DEFAULTPADDING : getPaddingLeft();
        final int getPaddingBottom = getPaddingRight() == 0 ? DEFAULTPADDING : getPaddingRight();
        final float cx = (getWidth - getPaddingStart - getPaddingBottom) / 2.0F;
        final float cy = getPaddingTop + DEFAULTRADIUS;

        //Head
        canvas.drawCircle(cx, cy, DEFAULTRADIUS, mPaint);
        final double cosLeft = Math.cos(2 * Math.PI / 360 * 210);
        final double cosRight = Math.cos(2 * Math.PI / 360 * 330);

        //Arm
        float len = (float) cosLeft * DEFAULTARMLEN;
        float cay = cy + DEFAULTRADIUS;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);
        len = (float) cosRight * DEFAULTARMLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);

        //Body
        canvas.drawLine(cx, cay, cx, cay + DEFAULTBODYLEN, mPaint);

        //Leg
        cay = cay + DEFAULTBODYLEN;
        len = (float) cosLeft * DEFAULTLEGLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTLEGLEN, mPaint);

        len = (float) cosRight * DEFAULTLEGLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTLEGLEN, mPaint);
    }

    private void drawStand(Canvas canvas) {
        final int getWidth = getMeasuredWidth();
        final int getPaddingTop = getPaddingTop() == 0 ? DEFAULTPADDING : getPaddingTop();
        final int getPaddingStart = getPaddingLeft() == 0 ? DEFAULTPADDING : getPaddingLeft();
        final int getPaddingBottom = getPaddingRight() == 0 ? DEFAULTPADDING : getPaddingRight();
        final float cx = (getWidth - getPaddingStart - getPaddingBottom) / 2.0F;
        final float cy = getPaddingTop + DEFAULTRADIUS;

        //head
        canvas.drawCircle(cx, cy, DEFAULTRADIUS, mPaint);

        //Arm
        final double cosLeft = Math.cos(2 * Math.PI / 360 * 210);
        final double cosRight = Math.cos(2 * Math.PI / 360 * 330);

        float len = (float) cosLeft * DEFAULTARMLEN;
        float cay = cy + DEFAULTRADIUS;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);
        len = (float) cosRight * DEFAULTARMLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);

        //Body
        canvas.drawLine(cx, cay, cx, cay + DEFAULTBODYLEN, mPaint);

        //Leg
        final int bodyWidth = 50;
        cay = cay + DEFAULTBODYLEN;
        canvas.drawLine(cx - bodyWidth, cay, cx + bodyWidth, cay, mPaint);

        canvas.drawLine(cx - bodyWidth, cay, cx - bodyWidth, cay + DEFAULTLEGLEN, mPaint);

        canvas.drawLine(cx + bodyWidth, cay, cx + bodyWidth, cay + DEFAULTLEGLEN, mPaint);

    }

    private void drawCircle() {

    }

    private  void  startTimer(){
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        },1000);

    }

    int i = 0;
    boolean isStartAnimator = false;

    private void startAnimator() {
        if (isStartAnimator) {
            return;
        }
        ValueAnimator va = ValueAnimator.ofFloat(1.0f, 2.0f);
        va.addUpdateListener((a) -> {
            float value = (float) a.getAnimatedValue();
            if (value > 1.9f) {
                postInvalidate();
            }
        });
        va.setDuration(500);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setTarget(this);
        va.start();
        isStartAnimator = true;
    }

    private int dp2pixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
