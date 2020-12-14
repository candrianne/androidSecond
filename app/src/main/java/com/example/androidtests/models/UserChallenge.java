package com.example.androidtests.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserChallenge implements Parcelable {
    private Date startdate;
    private Date enddate;
    private String name;
    private Integer score;

    public UserChallenge(Date startdate, Date enddate, String name, Integer score) {
        this.startdate = startdate;
        this.enddate = enddate;
        this.name = name;
        this.score = score;
    }

    protected UserChallenge(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            score = null;
        } else {
            score = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (score == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(score);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserChallenge> CREATOR = new Creator<UserChallenge>() {
        @Override
        public UserChallenge createFromParcel(Parcel in) {
            return new UserChallenge(in);
        }

        @Override
        public UserChallenge[] newArray(int size) {
            return new UserChallenge[size];
        }
    };

    public Date getstartdate() {
        return startdate;
    }

    public void setstartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getenddate() {
        return enddate;
    }

    public void setenddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
