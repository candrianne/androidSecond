package com.example.androidtests.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Friend implements Parcelable {
    private User user;
    private List<UserChallenge> challenges;

    public Friend(User user, List<UserChallenge> challenges) {
        this.user = user;
        this.challenges = challenges;
    }

    protected Friend(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        challenges = in.createTypedArrayList(UserChallenge.CREATOR);
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public User getUser() {
        return user;
    }

    public List<UserChallenge> getChallenges() {
        return challenges;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeTypedList(challenges);
    }
}
