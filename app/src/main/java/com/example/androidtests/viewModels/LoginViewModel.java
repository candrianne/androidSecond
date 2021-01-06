package com.example.androidtests.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.User;
import com.example.androidtests.models.UserLoginRequest;
import com.example.androidtests.repositories.web.LoginApiService;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.repositories.web.dto.UserloginDTO;
import com.example.androidtests.services.mappers.UserMapper;
import com.example.androidtests.utils.errors.NoConnectivityException;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<User> _user = new MutableLiveData<>();
    private LiveData<User> user = _user;

    private MutableLiveData<Boolean> _registered = new MutableLiveData<>();
    private LiveData<Boolean> registered = _registered;

    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;

    private LoginApiService loginApiService;
    private UserMapper loginMapper;

    private Application app;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.app = application;
        this.loginApiService = RetrofitConfigurationService.getInstance(application).loginService();
        this.loginMapper = UserMapper.getInstance();
    }

    public void login(UserLoginRequest loginInfos) {
        loginApiService.login(loginInfos).enqueue(new Callback<UserloginDTO>() {
            @Override
            public void onResponse(@NotNull Call<UserloginDTO> call, @NotNull Response<UserloginDTO> response) {
                if(response.isSuccessful()) {
                    try {
                        _user.setValue(loginMapper.mapTokenToUser(response.body(), getApplication()));
                        _error.setValue(null);
                        SaveSharedPreference.setLoggedIn(app, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    _error.setValue(NetworkError.REQUEST_ERROR);
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserloginDTO> call, @NotNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    _error.setValue(NetworkError.NO_CONNECTION);
                } else {
                    _error.setValue(NetworkError.TECHNICAL_ERROR);
                }
            }
        });
    }

    public void register(User newUser) {
        loginApiService.register(newUser).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    _registered.setValue(true);
                }
            }
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                System.out.println("f");
            }
        });
    }

    public LiveData<User> getUser() {
        return user;
    }
    public LiveData<NetworkError> getError() {
        return error;
    }
    public LiveData<Boolean> getRegistered() {return registered;}
}
