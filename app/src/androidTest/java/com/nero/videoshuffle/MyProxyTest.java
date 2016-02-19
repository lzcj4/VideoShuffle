package com.nero.videoshuffle;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.nero.videoshuffle.util.MyProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;

/**
 * Created by nlang on 2/19/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyProxyTest {
    MyProxy myProxy;

    @Before
    public void initial() {
        myProxy = new MyProxy();
        myProxy.start();
    }

    @Test
    public void testProxy() {

        final String uri = "http://www.163.com";
        try {
            URL url = new URL(uri);
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
            assertNotNull(html);
            Log.i("MyProxy", html);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
