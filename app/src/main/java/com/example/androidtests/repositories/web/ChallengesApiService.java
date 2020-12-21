package com.example.androidtests.repositories.web;

import com.example.androidtests.models.Challenge;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ChallengesApiService {
    @GET("challenge/")
    Call<List<Challenge>> getChallenges();
}
