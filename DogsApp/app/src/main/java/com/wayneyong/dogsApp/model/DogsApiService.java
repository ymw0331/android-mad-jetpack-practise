package com.wayneyong.dogsApp.model;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DogsApiService {

    private static final String BASE_URL = " https://raw.githubusercontent.com/";

    private DogsApi api;

    public DogsApiService() {
        //giving retrofit all the info needed

        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //convert list of dogBreed into single observable
                .build()
                .create(DogsApi.class);
    }

    //function declared in DogsApi interface
    public Single<List<DogBreed>> getDogs() {
        return api.getDogs();
    }

    //dynamic url example
//    public Single<List<Cats>> getCats(String url) {
//        return api.getCats(url);
//    }

}
