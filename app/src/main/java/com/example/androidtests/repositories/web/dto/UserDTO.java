package com.example.androidtests.repositories.web.dto;

public class UserDTO {
    String jwt;

    public UserDTO(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
