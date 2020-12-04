package com.example.androidtests.repositories.web;

import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.repositories.web.dto.UserChallengeDTO;

import java.util.List;

import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserChallengeApiService {
    @GET("/userChallenge/{id}")
    Call<List<UserChallenge>> getAllUserChallenges(@Path(value = "id") Integer id);
}
