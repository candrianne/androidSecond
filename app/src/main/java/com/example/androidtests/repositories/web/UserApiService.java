package com.example.androidtests.repositories.web;

import com.example.androidtests.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;

public interface UserApiService {
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @PATCH("user/")
    Call<ResponseBody> updateUser(@Body User user , @Header("Authorization") String auth);
}
