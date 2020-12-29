package com.example.androidtests.repositories.web;

import com.example.androidtests.models.FriendRequest;
import com.example.androidtests.models.Friendship;
import com.example.androidtests.models.UserLoginRequest;
import com.example.androidtests.repositories.web.dto.UserDTO;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FriendsApiService {
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("friendship/all")
    Call<List<Friendship>> getFriends(@Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("friendRequest/")
    Call<ResponseBody> sendFriendRequest(@Header("Authorization") String auth, @Body FriendRequest request);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @HTTP(method = "DELETE", path="friendRequest/", hasBody = true)
    Call<ResponseBody> deleteFriendRequest(@Header("Authorization") String auth, @Body FriendRequest request);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("friendRequest/")
    Call<List<FriendRequest>> getFriendRequests(@Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("friendship/")
    Call<ResponseBody> createFriendship(@Header("Authorization") String auth, @Body HashMap<String, Integer> data);
}
