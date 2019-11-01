package com.example.huellas.data;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);

    @Multipart
    @POST("extract_minutiae")
    Call<ResponseBody> extract_minutiae(
            @Header("Authorization") String token ,
            @Part("type1") RequestBody type1,
            @Part("type2") RequestBody type2,
            @Part MultipartBody.Part  fingerprint);

    @Multipart
    @POST("match_1v1")
    Call<ResponseBody> match_1v1(
            @Header("Authorization") String token ,
            @Part("type1") RequestBody type1,
            @Part("type2") RequestBody type2,
            @Part("region") RequestBody region,
            @Part("rotation") RequestBody rotation,
            @Part MultipartBody.Part  fingerprintA,
            @Part MultipartBody.Part fingerprintB);


}
