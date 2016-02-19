package com.nero.videoshuffle;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.nero.videoshuffle.util.MyProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by nlang on 2/19/2016.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MyProxyTest {
    MyProxy myProxy;

    @Before
    public void initial() {
        myProxy = new MyProxy();
        myProxy.start();
    }

    @Test
    public void testProxy() {

//        Observable<String> observable=Observable.just("http://www.163.com")
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.test())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        Log.i("MyProxy", s);
//                    }
//                });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.163.com");
                    URLConnection con = url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(MyProxy.PORT)));
                    // con.setDoOutput(true);
                    InputStream inputStream = con.getInputStream();
                    BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[1024];
                    StringBuilder sb = new StringBuilder();
                    int readLen = 0;
                    while ((readLen = bufferedStream.read(buffer)) > 0) {
                        String s = new String(buffer, 0, readLen, "utf-8");
                        sb.append(s);
                        Log.i("MyProxy", s);
                    }

                    String html = sb.toString();
                    assertNotNull(null);
                    Log.i("MyProxy", html);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
