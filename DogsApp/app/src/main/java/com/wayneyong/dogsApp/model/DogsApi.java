package com.wayneyong.dogsApp.model;


//retrofit require interface to communicate with backend

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface DogsApi {

    /*
    Single is part of RxJava, observable that returns a single value Retrofit require which end point to access
    api: https://raw.githubusercontent.com/DevTides/DogsApi/master/dogs.json
    host: https://raw.githubusercontent.com/
    endpoint: DevTides/DogsApi/master/dogs.json
    */

    @GET("DevTides/DogsApi/master/dogs.json")
    Single<List<DogBreed>> getDogs();

    //dynamic url example
//    @GET()
//    Single<List<Cats>> getCats(@Url String url) {

    }
