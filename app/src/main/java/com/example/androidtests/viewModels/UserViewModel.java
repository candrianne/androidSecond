package com.example.androidtests.viewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.R;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.User;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.repositories.web.UserApiService;
import com.example.androidtests.repositories.web.dto.UserDTO;
import com.example.androidtests.services.mappers.UserMapper;
import com.example.androidtests.utils.errors.NoConnectivityException;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<User> _user = new MutableLiveData<>();
    private LiveData<User> user = _user;

    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;

    private UserApiService userApiService;
    private UserMapper userMapper;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userApiService = RetrofitConfigurationService.getInstance(application).userApiService();
        this.userMapper = UserMapper.getInstance();
    }

    public void updateUser(User user) {
        userApiService.updateUser(user, "Bearer " + user.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplication(), R.string.update_success, Toast.LENGTH_LONG).show();
                SaveSharedPreference.setLogedInUser(getApplication(), user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplication(), R.string.update_fail, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUserById(Integer id) {
        userApiService.getUser(id).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if(response.isSuccessful()) {
                    _user.setValue(userMapper.mapToUser(response.body()));
                    _error.setValue(null);
                } else {
                    _error.setValue(NetworkError.REQUEST_ERROR);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    _error.setValue(NetworkError.NO_CONNECTION);
                } else {
                    _error.setValue(NetworkError.TECHNICAL_ERROR);
                }
            }
        });
    }

    public void getAllUsers() {

    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<NetworkError> getError() {
        return error;
    }
}
