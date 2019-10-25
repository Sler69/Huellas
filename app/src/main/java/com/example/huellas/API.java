package com.example.huellas;

import android.media.Image;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("extract_minutiae")
    Call<ResponseBody> extract_minutiae(@Header("Authorization") String token ,@Field("type1") String type1, @Field("type2") String type2, @Field("fingerprint") String fingerprint);


}
