package com.example.androidtests.services.mappers;

import android.content.Context;

import com.example.androidtests.models.User;
import com.example.androidtests.repositories.web.dto.UserDTO;
import com.example.androidtests.repositories.web.dto.UserFriendDTO;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


import static java.lang.Integer.parseInt;

public class UserMapper {
    private static UserMapper instance = null;

    public static UserMapper getInstance() {
        if (instance == null) {
            instance = new UserMapper();
        }
        return instance;
    }

    public User mapToUser(UserDTO dto, Context context) throws JSONException {
        if (dto == null) {
            return null;
        }

        JSONObject decodedPayload = decodeTokenParts(dto.getJwt());
        return new User(dto.getJwt(),
                decodedPayload.getJSONObject("value").getString("firstname"),
                decodedPayload.getJSONObject("value").getString("lastname"),
                decodedPayload.getJSONObject("value").getString("email"),
                decodedPayload.getJSONObject("value").getInt("id"),
                decodedPayload.getJSONObject("value").getString("photo"));
    }

    public User mapFriendToUser(UserFriendDTO dto)  {
        if (dto == null) {
            return null;
        }
        User newUser = new User();
        newUser.setId(dto.getId());
        newUser.setFirstName(dto.getFirstname());
        newUser.setLastName(dto.getLastname());
        newUser.setPhoto(dto.getPhoto());
        newUser.setBirthYear(dto.getBirthyear());
        newUser.setFirebaseToken(dto.getFirebasetoken());
        return newUser;
    }

    /*public String getTokenProperty(String token, String propertyName) throws JSONException {
        byte[] bytes = Base64.getUrlDecoder().decode(token);
        String decodedString = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("Decoded: " + decodedString);
        return (new JSONObject(decodedString)).getString(propertyName);
    }*/

    public static JSONObject decodeTokenParts(String token) throws JSONException {
        String[] parts = token.split("\\.", 0);
            byte[] bytes = Base64.getUrlDecoder().decode(parts[1]);
            String decodedString = new String(bytes, StandardCharsets.UTF_8);
        return (new JSONObject(decodedString));
    }
}
