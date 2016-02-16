package com.nero.videoshuffle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by nlang on 2/15/2016.
 */
public class TestView extends View {
    final String TAG = this.getClass().getSimpleName();

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);
        Log.i(TAG, String.format("/*** dispatchTouchEvent=%s  ,action=%s ****/", result, event.getAction()));
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        Log.i(TAG, String.format("/+++ onTouchEvent=%s  ,action=%s ++++/", result, event.getAction()));

        Observable<String> observable = Observable.just("aa").subscribeOn(Schedulers.newThread());
        observable.subscribe(new Action1<String>() {
                                 @Override
                                 public void call(String s) {
                                     Log.d("RX",s);
                                 }
                             }
        );
        return result;
    }
}
