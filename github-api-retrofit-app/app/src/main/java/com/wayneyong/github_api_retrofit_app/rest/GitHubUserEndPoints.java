package com.wayneyong.github_api_retrofit_app.rest;


import com.wayneyong.github_api_retrofit_app.model.GitHubUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


//endpoint needed to be declared in interface, we only define the methods
public interface GitHubUserEndPoints {

    //get method that is located at unique path, with one unique string, dynamically
    //curly bracket has unique string, dynamically taken, subsitute with user input
    @GET("/users/{user}")
    Call<GitHubUser> getUser(@Path("user") String user);


}
