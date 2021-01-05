package com.example.androidtests.repositories.web;

import androidx.room.Update;

import com.example.androidtests.models.ResumePauseRequest;
import com.example.androidtests.models.UserChallenge;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserChallengesApiService {
    @GET("userChallenge/{id}")
    Call<List<UserChallenge>> getAllUserChallenges(@Path(value = "id") Integer id);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @PATCH("userChallenge/")
    Call<ResponseBody> resumeOrPause(@Header("Authorization") String auth, @Body ResumePauseRequest body);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("userChallenge/")
    Call<ResponseBody> createUserChallenge(@Header("Authorization") String auth, @Body HashMap<String, Integer> body);
}
