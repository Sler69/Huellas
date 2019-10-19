package com.example.huellas;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    //Define the base URL
    private static final String BASE_URL = "http://10.48.13.35:3000/api/";

    //Create the Retrofit instance
    public static Retrofit getRetrofit(){
        if(retrofit ==  null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
