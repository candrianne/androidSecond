package com.example.androidtests.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.models.Challenge;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.repositories.web.ChallengesApiService;
import com.example.androidtests.repositories.web.LoginApiService;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.services.mappers.UserMapper;
import com.example.androidtests.utils.errors.NoConnectivityException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Challenge>> _challenges = new MutableLiveData<>();
    private LiveData<List<Challenge>> challenges = _challenges;
    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;

    private ChallengesApiService challengesApiService;


    public ChallengesViewModel(@NonNull Application application) {
        super(application);
        this.challengesApiService = RetrofitConfigurationService.getInstance(application).challengesApiService();
    }

    public void sendRequest() {
        challengesApiService.getChallenges().enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if(response.isSuccessful()) {
                   _challenges.setValue(response.body());
                   _error.setValue(null);
                } else {
                    _error.setValue(NetworkError.REQUEST_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    _error.setValue(NetworkError.NO_CONNECTION);
                } else {
                    _error.setValue(NetworkError.TECHNICAL_ERROR);
                }
            }
        });
    }

    public LiveData<List<Challenge>> getChallenges() {
        return challenges;
    }

    public LiveData<NetworkError> getError() {
        return error;
    }
}
