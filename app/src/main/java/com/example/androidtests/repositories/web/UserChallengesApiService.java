package com.example.androidtests.repositories.web;

import com.example.androidtests.models.UserChallenge;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserChallengesApiService {
    @GET("userChallenge/{id}")
    Call<List<UserChallenge>> getAllUserChallenges(@Path(value = "id") Integer id);
}
