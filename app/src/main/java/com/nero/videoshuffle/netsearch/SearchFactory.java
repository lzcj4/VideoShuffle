package com.nero.videoshuffle.netsearch;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.Retrofit;

/**
 * Created by nlang on 4/11/2016.
 */
public class SearchFactory {
    private static final String HOME_URI = "https://c.getsatisfaction.com";
    private static final String NERO_HOME_URI = HOME_URI + "/nero_eng";
    private static final String SEARCH_URI = NERO_HOME_URI + "/topics/search/show";

    static OkHttpClient httpClient;

    static {
        final int TIMEOUT = 2;
        final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
        httpClient = new OkHttpClient();
        httpClient.setReadTimeout(TIMEOUT, TIMEUNIT);
        httpClient.setConnectTimeout(TIMEOUT, TIMEUNIT);
        httpClient.setWriteTimeout(TIMEOUT, TIMEUNIT);
    }

    public static SearchService getSearchService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HOME_URI).client(httpClient).build();
        SearchService result = retrofit.create(SearchService.class);
        return result;


    }
}
