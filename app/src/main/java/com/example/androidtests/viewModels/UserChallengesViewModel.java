package com.example.androidtests.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.ResumePauseRequest;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.repositories.web.UserChallengesApiService;
import com.example.androidtests.utils.errors.NoConnectivityException;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserChallengesViewModel extends AndroidViewModel {
    private MutableLiveData<List<UserChallenge>> _userChallenges = new MutableLiveData<>();
    private LiveData<List<UserChallenge>> userChallenges = _userChallenges;

    private MutableLiveData<Boolean> _updated = new MutableLiveData<>();
    private LiveData<Boolean> updated = _updated;

    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;

    private UserChallengesApiService apiService;


    public UserChallengesViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitConfigurationService.getInstance(application).userChallengeService();
    }

    public void getAllUserChallenges(Integer id) {
        apiService.getAllUserChallenges(id).enqueue(new Callback<List<UserChallenge>>() {
            @Override
            public void onResponse(Call<List<UserChallenge>> call, Response<List<UserChallenge>> response) {
                if(response.isSuccessful()) {
                    List<UserChallenge> challenges = response.body();
                    for(UserChallenge challenge : challenges)
                        challenge.setAutoState();
                    _userChallenges.setValue(challenges);
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

    public void resumeOrPauseChallenge(String action,  int challengeId) {
        String auth = "Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).getToken();
        ResumePauseRequest body = new ResumePauseRequest(challengeId, action);
        apiService.resumeOrPause(auth, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                _updated.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _updated.setValue(false);
            }
        });
    }

    public void createUserChallenge(int id) {
        String auth = "Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).getToken();
        HashMap<String, Integer> body = new HashMap<>();
        body.put("challengeId", id);
        apiService.createUserChallenge(auth, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                _updated.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _updated.setValue(false);
            }
        });
    }

    public LiveData<List<UserChallenge>> getUserChallenges() {
        return userChallenges;
    }
    public LiveData<NetworkError> getError() {
        return error;
    }

    public LiveData<Boolean> getUpdated() {
        return updated;
    }
}
