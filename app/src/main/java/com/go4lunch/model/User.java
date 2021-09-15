package com.go4lunch.model;

import androidx.annotation.Nullable;

public class User {
    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String eatingPlace;
    private String eatingPlaceId;
    private String email;

    public User() {
    }

    public User(String uid, String username, String email, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
        this.eatingPlace = "none";
        this.eatingPlaceId = "none";
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getEatingPlace() {
        return eatingPlace;
    }

    public void setEatingPlace(String eatingPlace) {
        this.eatingPlace = eatingPlace;
    }

    public String getEatingPlaceId() {
        return eatingPlaceId;
    }

    public void setEatingPlaceId(String eatingPlaceId) {
        this.eatingPlaceId = eatingPlaceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}