package com.nero.videoshuffle.model;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by nlang on 15/11/24.
 */
public class GitHubServiceImpl {
    public final static String GITBASEURL = "https://api.github.com";
    static OkHttpClient httpClient;

    static {
        httpClient = new OkHttpClient();
       // httpClient.networkInterceptors().add(new StethoInterceptor());
    }

    public static GitHubService getInstance() {
        // httpClient.networkInterceptors().add(new StethoInterceptor());
        httpClient.setReadTimeout(10 * 60, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(10 * 60, TimeUnit.SECONDS);
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(GITBASEURL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubService service = builder.create(GitHubService.class);
        return service;
    }
}
