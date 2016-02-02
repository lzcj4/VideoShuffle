package com.nero.videoshuffle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nlang on 1/25/2016.
 */
public class CounterView extends View implements View.OnClickListener {
    public CounterView(Context context) {
        this(context, null);
    }

    public CounterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    Paint mPaint;
    int mCount;

    public CounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCount = 0;
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setARGB(50, 255, 0, 0);
        int totalWidth = getWidth();
        int totalHeight = getHeight();
        canvas.drawRect(0, 0, totalWidth, totalHeight, mPaint);
        String str = String.valueOf(mCount);
        Rect rect = new Rect();
        mPaint.setTextSize(50);
        mPaint.getTextBounds(str, 0, str.length(), rect);
        mPaint.setARGB(255, 0, 255, 0);
        canvas.drawText(str, (totalWidth - rect.width()) / 2, (totalHeight + rect.height()) / 2, mPaint);
    }

    @Override
    public void onClick(View v) {
        mCount++;
        invalidate();
    }
}
