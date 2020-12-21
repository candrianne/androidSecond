package com.example.androidtests.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.repositories.web.UserChallengesApiService;
import com.example.androidtests.utils.errors.NoConnectivityException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserChallengesViewModel extends AndroidViewModel {
    private MutableLiveData<List<UserChallenge>> _userChallenges = new MutableLiveData<>();
    private LiveData<List<UserChallenge>> userChallenges = _userChallenges;

    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;

    private UserChallengesApiService apiService;


    public UserChallengesViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitConfigurationService.getInstance(application).userChallengeService();
    }

    public void sendRequest(Integer id) {
        apiService.getAllUserChallenges(id).enqueue(new Callback<List<UserChallenge>>() {
            @Override
            public void onResponse(Call<List<UserChallenge>> call, Response<List<UserChallenge>> response) {
                if(response.isSuccessful()) {
                    _userChallenges.setValue(response.body());
                    _error.setValue(null);
                } else {
                    _error.setValue(NetworkError.REQUEST_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<UserChallenge>> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    _error.setValue(NetworkError.NO_CONNECTION);
                } else {
                    _error.setValue(NetworkError.TECHNICAL_ERROR);
                }
            }
        });
    }


    public LiveData<List<UserChallenge>> getUserChallenges() {
        return userChallenges;
    }

    public LiveData<NetworkError> getError() {
        return error;
    }
}
