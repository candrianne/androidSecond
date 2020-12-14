package com.example.androidtests.repositories.web;

import android.content.Context;

import com.example.androidtests.utils.ConnectivityCheckInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfigurationService {
    private static final String BASE_URL = "https://ecoloapi.azurewebsites.net/";
    private static LoginApiService loginApiService = null;
    private static UserChallengesApiService userChallengesApiService = null;
    private static UserApiService userApiService = null;
    private static ChallengesApiService challengesApiService = null;
    private Retrofit retrofitClient;

    private RetrofitConfigurationService(Context context) {
        initializeRetrofit(context);
    }

    private void initializeRetrofit(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityCheckInterceptor(context))
                .build();

        this.retrofitClient = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static RetrofitConfigurationService getInstance(Context context) {
        return new RetrofitConfigurationService(context);
    }

    public LoginApiService loginService() {
        if (loginApiService == null) {
            loginApiService = retrofitClient.create(LoginApiService.class);
        }
        return loginApiService;
    }

    public UserApiService userApiService() {
        if (userApiService == null) {
            userApiService = retrofitClient.create(UserApiService.class);
        }
        return userApiService;
    }

    public UserChallengesApiService userChallengeService() {
        if(userChallengesApiService == null) {
            userChallengesApiService = retrofitClient.create(UserChallengesApiService.class);
        }
        return userChallengesApiService;
    }

    public ChallengesApiService challengesApiService() {
        if(challengesApiService == null) {
            challengesApiService = retrofitClient.create(ChallengesApiService.class);
        }
        return challengesApiService;
    }
}

