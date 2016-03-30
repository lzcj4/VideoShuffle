package com.nero.videoshuffle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStart(canvas);
    }

    private void drawStart(Canvas canvas) {
        final int getWidth = getMeasuredWidth();
        final int getPaddingTop = getPaddingTop() == 0 ? DEFAULTPADDING : getPaddingTop();
        final int getPaddingStart = getPaddingLeft() == 0 ? DEFAULTPADDING : getPaddingLeft();
        final int getPaddingBottom = getPaddingRight() == 0 ? DEFAULTPADDING : getPaddingRight();
        final float cx = (getWidth - getPaddingStart - getPaddingBottom) / 2.0F;
        final float cy = getPaddingTop + DEFAULTRADIUS;

        canvas.drawCircle(cx, cy, DEFAULTRADIUS, mPaint);
        final double cosLeft = Math.cos(2 * Math.PI / 360 * 210);
        final double cosRight = Math.cos(2 * Math.PI / 360 * 330);

        float len = (float) cosLeft * DEFAULTARMLEN;
        float cay = cy + DEFAULTRADIUS;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);

        canvas.drawLine(cx, cay, cx, cay + DEFAULTBODYLEN, mPaint);

        len = (float) cosRight * DEFAULTARMLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);

        cay = cay + DEFAULTBODYLEN;
        len = (float) cosLeft * DEFAULTLEGLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTLEGLEN, mPaint);

        len = (float) cosRight * DEFAULTLEGLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTLEGLEN, mPaint);
    }

    private void drawEnd(Canvas canvas) {
        final int getWidth = getMeasuredWidth();
        final int getPaddingTop = getPaddingTop() == 0 ? DEFAULTPADDING : getPaddingTop();
        final int getPaddingStart = getPaddingLeft() == 0 ? DEFAULTPADDING : getPaddingLeft();
        final int getPaddingBottom = getPaddingRight() == 0 ? DEFAULTPADDING : getPaddingRight();
        final float cx = (getWidth - getPaddingStart - getPaddingBottom) / 2.0F;
        final float cy = getPaddingTop + DEFAULTRADIUS;

        canvas.drawCircle(cx, cy, DEFAULTRADIUS, mPaint);
        final double cosLeft = Math.cos(2 * Math.PI / 360 * 210);
        final double cosRight = Math.cos(2 * Math.PI / 360 * 330);

        float len = (float) cosLeft * DEFAULTARMLEN;
        float cay = cy + DEFAULTRADIUS;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);

        canvas.drawLine(cx, cay, cx, cay + DEFAULTBODYLEN, mPaint);

        len = (float) cosRight * DEFAULTARMLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTARMLEN, mPaint);

        cay = cay + DEFAULTBODYLEN;
        len = (float) cosLeft * DEFAULTLEGLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTLEGLEN, mPaint);

        len = (float) cosRight * DEFAULTLEGLEN;
        canvas.drawLine(cx, cay, cx + len, cay + DEFAULTLEGLEN, mPaint);
    }

    private int dp2pixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
