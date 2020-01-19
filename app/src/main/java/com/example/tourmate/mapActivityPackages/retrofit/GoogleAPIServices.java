package com.example.tourmate.mapActivityPackages.retrofit;

import com.example.tourmate.mapActivityPackages.model.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GoogleAPIServices {

    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);
}
