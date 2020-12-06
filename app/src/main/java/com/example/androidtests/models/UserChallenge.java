package com.example.androidtests.models;


import java.util.Date;

public class UserChallenge {
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
