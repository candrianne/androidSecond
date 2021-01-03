package com.example.androidtests.repositories.web.dto;

public class UserloginDTO {
    String jwt;

    public UserloginDTO(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
