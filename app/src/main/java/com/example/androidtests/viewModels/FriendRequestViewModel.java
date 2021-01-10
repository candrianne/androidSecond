package com.example.androidtests.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.models.Friend;
import com.example.androidtests.models.FriendRequest;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.repositories.web.FriendsApiService;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.utils.errors.NoConnectivityException;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRequestViewModel extends AndroidViewModel {
    private MutableLiveData<List<FriendRequest>> _friendRequests = new MutableLiveData<>();
    private LiveData<List<FriendRequest>> friendRequests = _friendRequests;
    private MutableLiveData<Boolean> _sent = new MutableLiveData<>();
    private LiveData<Boolean> sent = _sent;
    private MutableLiveData<Boolean> _deleted = new MutableLiveData<>();
    private LiveData<Boolean> deleted = _deleted;
    private MutableLiveData<Boolean> _noRequests = new MutableLiveData<>();
    private LiveData<Boolean> noRequests = _noRequests;
    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;
    private FriendsApiService friendsApiService;

    public FriendRequestViewModel(@NonNull Application application) {
        super(application);
        friendsApiService = RetrofitConfigurationService.getInstance(application).friendsApiService();
    }

    public void getRequests() {
        friendsApiService.getFriendRequests("Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).getToken())
                .enqueue(new Callback<List<FriendRequest>>() {
                    @Override
                    public void onResponse(Call<List<FriendRequest>> call, Response<List<FriendRequest>> response) {
                        if(response.isSuccessful()) {
                            _friendRequests.setValue(response.body());
                            _error.setValue(null);
                        } else {
                            if(response.raw().code() == 404) {
                                _noRequests.setValue(true);
                                _error.setValue(null);
                            } else {
                                _error.setValue(NetworkError.REQUEST_ERROR);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FriendRequest>> call, Throwable t) {
                        if (t instanceof NoConnectivityException) {
                            _error.setValue(NetworkError.NO_CONNECTION);
                        } else {
                            _error.setValue(NetworkError.TECHNICAL_ERROR);
                        }
                    }
                });
    }

    public void sendFriendRequest(int receiverId) {
        FriendRequest request = new FriendRequest(receiverId);
        String auth = "Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).getToken();
        friendsApiService.sendFriendRequest(auth, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                _sent.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _sent.setValue(false);
            }
        });
    }

    public void deleteFriendRequest(int senderId) {
        FriendRequest request = new FriendRequest();
        request.setSender(senderId);
        String auth = "Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).getToken();
        friendsApiService.deleteFriendRequest(auth, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                _deleted.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _deleted.setValue(false);
            }
        });
    }

    public LiveData<NetworkError> getError() {
        return error;
    }
    public LiveData<Boolean> getSent() {
        return sent;
    }
    public LiveData<List<FriendRequest>> getFriendRequests() {
        return friendRequests;
    }
    public LiveData<Boolean> getNoRequests() {
        return noRequests;
    }
    public LiveData<Boolean> getDeleted() {
        return deleted;
    }
}
