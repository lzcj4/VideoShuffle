package com.nero.videoshuffle.netsearch;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by nlang on 4/11/2016.
 */
public interface SearchService {

    @GET("/nero_eng")
    Observable<Object> getHome();
}
