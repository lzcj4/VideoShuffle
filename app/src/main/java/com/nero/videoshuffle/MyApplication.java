package com.nero.videoshuffle;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by nlang on 11/12/2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initial3rdSdk();
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
    }

    private void initial3rdSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        LeakCanary.install(this);

        ImageLoaderConfiguration loaderConf = new ImageLoaderConfiguration.Builder(this)
                //.writeDebugLogs()
//                    .memoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 5))
//                    .taskExecutor(Executors.newFixedThreadPool(3))
//                    .taskExecutorForCachedImages(Executors.newFixedThreadPool(3))
                .build();
        ImageLoader.getInstance().init(loaderConf);
    }
}
