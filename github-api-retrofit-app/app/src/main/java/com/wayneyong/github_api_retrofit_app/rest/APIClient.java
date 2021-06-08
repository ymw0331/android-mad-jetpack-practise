package com.wayneyong.github_api_retrofit_app.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    //base url as endpoint
    public static final String BASE_URL = "https://api.github.com/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {

        //check if instance of retrofit is null, create a new instance
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .build();
//        }
//        return retrofit;

        //Prior to 2.0.0 retroFit, the default converter was a Gson converter, but in 2.0.0, Gson converter needed to be explicitly specified
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
