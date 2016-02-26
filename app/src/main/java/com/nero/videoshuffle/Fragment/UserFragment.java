package com.nero.videoshuffle.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.nero.videoshuffle.R;
import com.nero.videoshuffle.util.MyProxy;
import com.nero.videoshuffle.view.CircleImage;
import com.nero.videoshuffle.view.SpanAdapter;
import com.nero.videoshuffle.view.SpanListView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends ListFragment {

   // @Bind(R.id.circleView)
    CircleImage mCircleView;
    @Bind(android.R.id.list)
    SpanListView mListview;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String[] items = new String[10];
        Random r = new Random(100);
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(r.nextInt(100));
        }
        SpanAdapter adapter = new SpanAdapter(this.getActivity(), items);
        mListview.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    private void testProxy() {

        MyProxy myProxy = new MyProxy();
        myProxy.start();
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
                    ByteArrayOutputStream byo = new ByteArrayOutputStream();
                    int readLen;
                    while ((readLen = bufferedStream.read(buffer)) > 0) {
                        byo.write(buffer, 0, readLen);
                        Arrays.fill(buffer, (byte) 0);
                    }
                    String html = byo.toString();
                    Log.i("MyProxy", html);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
