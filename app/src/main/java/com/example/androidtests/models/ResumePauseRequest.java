package com.example.androidtests.models;

public class ResumePauseRequest {
    private Integer challengeId;
    private String action;

    public ResumePauseRequest(Integer challengeId, String action) {
        this.challengeId = challengeId;
        this.action = action;
    }
}
