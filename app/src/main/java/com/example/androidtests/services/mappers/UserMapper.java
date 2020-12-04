package com.example.androidtests.services.mappers;

import com.example.androidtests.models.User;
import com.example.androidtests.repositories.web.dto.UserDTO;

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

    public User mapToUser(UserDTO dto) throws JSONException {
        if (dto == null) {
            return null;
        }

        JSONObject decodedPayload = decodeTokenParts(dto.getJwt());
        return new User(decodedPayload.getJSONObject("value").getString("firstname"),
                decodedPayload.getJSONObject("value").getString("lastname"),
                decodedPayload.getJSONObject("value").getString("email"),
                decodedPayload.getJSONObject("value").getInt("id"));
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
