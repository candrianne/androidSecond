package com.example.androidtests.utils;

import com.example.androidtests.models.UserChallenge;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;

public class Profile {
    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int calculateUserScore(List<UserChallenge> challenges) {
        int globalScore = 0;
        for(UserChallenge challenge : challenges) {
            if(challenge.getenddate() != null) {
                globalScore += getDifferenceDays(challenge.getstartdate(), challenge.getenddate()) * challenge.getScore();
            } else {
                globalScore += getDifferenceDays(challenge.getstartdate(), java.util.Calendar.getInstance().getTime()) * challenge.getScore();
            }
        }
        return globalScore;
    }
}
