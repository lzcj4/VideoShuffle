package com.nero.videoshuffle.util;

import android.net.Uri;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nlang on 3/10/2016.
 */
public class JXTest {
    public static void testJXJava() {
        Subscription sub = Observable.just("http://www.163.com", "https://github.com/")
                .map((addr) -> {
                    try {
                        URL url = new URL(addr);
                        URLConnection con = url.openConnection();
                        con.connect();
                        long len = con.getContentLength();
                        String content = (String)con.getContent();
                        return content;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((html) -> System.out.println(html));
        sub.isUnsubscribed();
    }
}
