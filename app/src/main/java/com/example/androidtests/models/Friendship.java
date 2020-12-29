package com.example.androidtests.models;

public class Friendship {
    private Integer iduser1, iduser2;

    public Friendship(Integer iduser1, Integer iduser2) {
        this.iduser1 = iduser1;
        this.iduser2 = iduser2;
    }

    public Integer getIdUser1() {
        return iduser1;
    }

    public Integer getIdUser2() {
        return iduser2;
    }
}
