package com.example.androidtests.repositories.web;

import com.example.androidtests.models.User;
import com.example.androidtests.models.UserLoginRequest;
import com.example.androidtests.repositories.web.dto.UserloginDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("role/login")
    Call<UserloginDTO> login(@Body UserLoginRequest userLoginRequest);

    @POST("user/")
    Call<ResponseBody> register(@Body User newUser);
}
