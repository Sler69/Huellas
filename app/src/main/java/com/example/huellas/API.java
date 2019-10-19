package com.example.huellas;

import android.media.Image;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("extract_minutiae")
    Call<ResponseBody> extract_minutiae(@Field("type1") String type1, @Field("type2") String type2, @Field("fingerprint")Image fingerprint);


}
