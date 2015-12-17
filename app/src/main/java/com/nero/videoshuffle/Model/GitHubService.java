package com.nero.videoshuffle.model;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by nlang on 15/11/24.
 */

public interface GitHubService {

    @GET("/Users/{user}/Repossss")
    Call<List<Repo>> listResp(@Path("user") String user);

    @GET("/users/list")
    Call<User> listUser();

    @POST("/users/add")
    Call<User>addNewUser(@Body User user);

    @GET("/group/{user}/list")
    Call<NotFoundHint> getNotFound(@Path("user") String user);
}
