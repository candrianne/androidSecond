package com.example.androidtests.models;

public class FriendRequest {
    private Integer receiver, sender;
    private String firstname, lastname, photo;

    public FriendRequest() {
    }

    public FriendRequest(Integer receiverId) {
        this.receiver = receiverId;
    }

    public FriendRequest(Integer sender, String firstname, String lastname, String photo) {
        this.sender = sender;
        this.firstname = firstname;
        this.lastname = lastname;
        this.photo = photo;
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

    public Integer getSender() {
        return sender;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }
}
