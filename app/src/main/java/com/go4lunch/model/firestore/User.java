package com.go4lunch.model.firestore;

import androidx.annotation.Nullable;

import java.util.List;

public class User implements Comparable<User> {
    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String eatingPlace;
    private String eatingPlaceId;
    private String email;
    private List<String> listOfRestaurantsLiked;

    public User() {
    }

    public User(String uid, String username, String email, @Nullable String urlPicture, String eatingPlace, String eatingPlaceId, List<String> listOfRestaurantsLiked) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
        this.eatingPlace = eatingPlace;
        this.eatingPlaceId = eatingPlaceId;
        this.listOfRestaurantsLiked = listOfRestaurantsLiked;
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

    public String setEatingPlace(String eatingPlace) {
        this.eatingPlace = eatingPlace;
        return eatingPlace;
    }

    public String getEatingPlaceId() {
        return eatingPlaceId;
    }

    public String setEatingPlaceId(String eatingPlaceId) {
        this.eatingPlaceId = eatingPlaceId;
        return eatingPlaceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(User user) {
        return user.getEatingPlace().compareTo(this.eatingPlace);
    }

    public List<String> getListOfRestaurantsLiked() {
        return listOfRestaurantsLiked;
    }

    public void setListOfRestaurantsLiked(List<String> listOfRestaurantsLiked) {
        this.listOfRestaurantsLiked = listOfRestaurantsLiked;
    }
}
