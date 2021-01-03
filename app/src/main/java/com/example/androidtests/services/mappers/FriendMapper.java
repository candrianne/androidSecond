package com.example.androidtests.services.mappers;

import com.example.androidtests.models.Friend;
import com.example.androidtests.models.User;
import com.example.androidtests.models.UserChallenge;

import java.util.List;

public class FriendMapper {
    private static FriendMapper instance = null;

    public static FriendMapper getInstance() {
        if (instance == null) {
            instance = new FriendMapper();
        }
        return instance;
    }

    public Friend mapToFriend(User user, List<UserChallenge> challenges) {
        if (user == null) {
            return null;
        }

        return new Friend(user, challenges);
    }
}
