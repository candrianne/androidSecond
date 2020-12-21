package com.example.androidtests.repositories.web;

import android.net.wifi.rtt.ResponderLocation;

import com.example.androidtests.models.User;
import com.example.androidtests.models.UserLoginRequest;
import com.example.androidtests.repositories.web.dto.UserDTO;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("role/login")
    Call<UserDTO> login(@Body UserLoginRequest userLoginRequest);

    @POST("user/")
    Call<ResponseBody> register(@Body User newUser);
}
