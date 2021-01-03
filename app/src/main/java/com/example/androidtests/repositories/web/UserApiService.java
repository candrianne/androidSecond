package com.example.androidtests.repositories.web;

import com.example.androidtests.models.User;
import com.example.androidtests.repositories.web.dto.UserDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserApiService {
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @PATCH("user/")
    Call<ResponseBody> updateUser(@Body User user , @Header("Authorization") String auth);

    @GET("user/{id}")
    Call<UserDTO> getUser(@Path(value = "id") Integer id);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("user/")
    Call<List<UserDTO>> getAllUsers(@Header("Authorization") String auth);
}
