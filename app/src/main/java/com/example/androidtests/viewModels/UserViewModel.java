package com.example.androidtests.viewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.androidtests.R;
import com.example.androidtests.models.User;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.repositories.web.UserApiService;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {
    private UserApiService userApiService;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userApiService = RetrofitConfigurationService.getInstance(application).userApiService();
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
}
