package com.example.androidtests.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidtests.models.Friend;
import com.example.androidtests.models.Friendship;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.User;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.repositories.web.FriendsApiService;
import com.example.androidtests.repositories.web.RetrofitConfigurationService;
import com.example.androidtests.repositories.web.UserApiService;
import com.example.androidtests.repositories.web.UserChallengesApiService;
import com.example.androidtests.repositories.web.dto.UserDTO;
import com.example.androidtests.services.mappers.UserMapper;
import com.example.androidtests.utils.errors.NoConnectivityException;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendshipViewModel extends AndroidViewModel {
    private MutableLiveData<List<Friend>> _friends = new MutableLiveData<>();
    private LiveData<List<Friend>> friends = _friends;
    private MutableLiveData<Boolean> _created = new MutableLiveData<>();
    private LiveData<Boolean> created = _created;
    private MutableLiveData<NetworkError> _error = new MutableLiveData<>();
    private LiveData<NetworkError> error = _error;

    private FriendsApiService friendsApiService;
    private UserApiService userApiService;
    private UserChallengesApiService userChallengesApiService;
    private UserMapper userMapper;
    private int i;


    public FriendshipViewModel(@NonNull Application application) {
        super(application);
        friendsApiService = RetrofitConfigurationService.getInstance(application).friendsApiService();
        this.userApiService = RetrofitConfigurationService.getInstance(application).userApiService();
        this.userChallengesApiService = RetrofitConfigurationService.getInstance(application).userChallengeService();
        this.userMapper = UserMapper.getInstance();
    }

    public void getAllFriends(User connectedUser) {
        List<Friend> friends = new ArrayList<>();
        friendsApiService.getFriends("Bearer " + connectedUser.getToken()).enqueue(new Callback<List<Friendship>>() {
            @Override
            public void onResponse(Call<List<Friendship>> call, Response<List<Friendship>> response) {
                if(response.isSuccessful()) {
                    List<Friendship> friendList = response.body();
                    ArrayList<Integer> idsList = new ArrayList<>();
                    for(Friendship friendship: friendList) {
                        idsList.add(friendship.getIdUser1().equals(connectedUser.getId()) ?
                                friendship.getIdUser2() :
                                friendship.getIdUser1());
                    }
                    i = 0;
                    for(Integer id : idsList) {
                        userApiService.getUser(id).enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                User user = userMapper.mapToUser(response.body());
                                userChallengesApiService.getAllUserChallenges(user.getId()).enqueue(new Callback<List<UserChallenge>>() {
                                    @Override
                                    public void onResponse(Call<List<UserChallenge>> call, Response<List<UserChallenge>> response) {
                                        if(response.isSuccessful()) {
                                            friends.add(new Friend(user, response.body()));
                                            i++;
                                            if(i == idsList.size())
                                                _friends.setValue(friends);
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
                } else {
                    _error.setValue(NetworkError.REQUEST_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<Friendship>> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    _error.setValue(NetworkError.NO_CONNECTION);
                } else {
                    _error.setValue(NetworkError.TECHNICAL_ERROR);
                }
            }
        });
    }

    public void createFriendship(int newFriendId) {
        String auth = "Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).getToken();
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("idNewFriend", newFriendId);
        friendsApiService.createFriendship(auth, hashMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                _created.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _created.setValue(false);
            }
        });
    }

    public LiveData<List<Friend>> getFriends() {
        return friends;
    }

    public LiveData<NetworkError> getError() {
        return error;
    }

    public LiveData<Boolean> getCreated() {
        return created;
    }
}