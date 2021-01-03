package com.example.androidtests.repositories.web.dto;

public class UserDTO {
    private Integer id;
    private String email;
    private String firstname;
    private String lastname;
    private String photo;
    private Integer birthyear;
    private String firebasetoken;

    public UserDTO(Integer id, String email, String firstname, String lastname, String photo, Integer birthyear, String firebasetoken) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.photo = photo;
        this.birthyear = birthyear;
        this.firebasetoken = firebasetoken;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhoto() {
        return photo;
    }

    public Integer getBirthyear() {
        return birthyear;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }
}
