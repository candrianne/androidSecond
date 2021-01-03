package com.example.androidtests.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.androidtests.R
import com.example.androidtests.models.NetworkError
import com.example.androidtests.models.User
import com.example.androidtests.models.UserChallenge
import com.example.androidtests.repositories.web.RetrofitConfigurationService
import com.example.androidtests.repositories.web.UserApiService
import com.example.androidtests.repositories.web.UserChallengesApiService
import com.example.androidtests.repositories.web.dto.UserDTO
import com.example.androidtests.services.mappers.UserMapper
import com.example.androidtests.utils.Profile
import com.example.androidtests.utils.errors.NoConnectivityException
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserVModel(application: Application) : AndroidViewModel(application) {
    private var _user = MutableLiveData<User>()
    private var user: LiveData<User> = _user
    private var _users = MutableLiveData<List<User>>()
    var users : LiveData<List<User>> = _users
    private var _error = MutableLiveData<NetworkError?>()
    var error: LiveData<NetworkError?> = _error
    private var userApiService: UserApiService = RetrofitConfigurationService.getInstance(application).userApiService()
    private var userChallengeApiService : UserChallengesApiService = RetrofitConfigurationService.getInstance(application).userChallengeService()
    private var userMapper: UserMapper = UserMapper.getInstance()

    fun updateUser(user: User) {
        userApiService.updateUser(user, "Bearer " + user.token).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Toast.makeText(getApplication(), R.string.update_success, Toast.LENGTH_LONG).show()
                SaveSharedPreference.setLogedInUser(getApplication(), user)
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(getApplication(), R.string.update_fail, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getUserById(id: Int?) {
        userApiService.getUser(id).enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.isSuccessful) {
                    _user.value = userMapper.mapToUser(response.body())
                    _error.value = null
                } else {
                    _error.value = NetworkError.REQUEST_ERROR
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                if (t is NoConnectivityException) {
                    _error.value = NetworkError.NO_CONNECTION
                } else {
                    _error.value = NetworkError.TECHNICAL_ERROR
                }
            }
        })
    }

    fun getAllUsers() {
        val auth = "Bearer " + SaveSharedPreference.getLogedInUser(getApplication()).token;
       userApiService.getAllUsers(auth).enqueue(object : Callback<List<UserDTO>> {
           override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
               if (t is NoConnectivityException) {
                   _error.value = NetworkError.NO_CONNECTION
               } else {
                   _error.value = NetworkError.TECHNICAL_ERROR
               }
           }

           override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
               if(response.isSuccessful) {
                   var users : Array<User?> = arrayOfNulls(response.body()?.size ?: 0)
                   for(ind in response.body()?.indices!!) {
                       users[ind] = userMapper.mapToUser(response.body()!![ind])
                       userChallengeApiService.getAllUserChallenges(users[ind]?.id).enqueue(object : Callback<List<UserChallenge>> {
                           override fun onFailure(call: Call<List<UserChallenge>>, t: Throwable) {
                               if (t is NoConnectivityException) {
                                   _error.value = NetworkError.NO_CONNECTION
                               } else {
                                   _error.value = NetworkError.TECHNICAL_ERROR
                               }
                           }
                           override fun onResponse(call: Call<List<UserChallenge>>, response: Response<List<UserChallenge>>) {
                               if(response.isSuccessful) {
                                   val score = Profile.calculateUserScore(response.body())
                                   users[ind]?.score = score
                                   if(ind == users.size - 1) {
                                       _users.value = users.sortedBy { user -> user?.score }.toList() as List<User>?
                                       _error.value = null
                                   }
                               } else {
                                   _error.value = NetworkError.REQUEST_ERROR
                               }
                           }
                       })
                   }
               } else {
                   _error.value = NetworkError.REQUEST_ERROR
               }
           }
       })
    }
}