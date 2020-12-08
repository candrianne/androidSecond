package com.example.androidtests.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String token;
    String firstName,lastName,email, photo;
    Integer id, score;

    public User(String token, String firstName, String lastName, String email, Integer id, String photo) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.score = null;
        this.photo = photo;
    }

    public User() {

    }

    protected User(Parcel in) {
        token = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        score = in.readInt();
        photo = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getToken() {
        return token;
    }

    public void setFirstName(String name) {
        firstName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeInt(id);
        dest.writeInt(score);
        dest.writeString(photo);
    }
}
