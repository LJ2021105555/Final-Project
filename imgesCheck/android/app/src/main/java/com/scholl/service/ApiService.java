package com.scholl.service;

import com.scholl.entity.ImageListResponse;
import com.scholl.entity.ImageResponse;
import com.scholl.entity.LoginRequest;
import com.scholl.entity.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;
import retrofit2.http.GET;

public interface ApiService {

    // 登录接口
    @POST("token/")
    Call<TokenResponse> login(@Body LoginRequest loginRequest);

    // 上传图片接口
    @Multipart
    @POST("images/")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part image);

    // 获取图像列表接口
    @GET("images/")
    Call<ImageListResponse> getImageList();
}
