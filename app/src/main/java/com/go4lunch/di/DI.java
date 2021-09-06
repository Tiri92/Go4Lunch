package com.go4lunch.di;

import com.go4lunch.repositories.GooglePlaceRepository;

public class DI {

    private static final GooglePlaceRepository googlePlaceRepository = new GooglePlaceRepository();

    public static GooglePlaceRepository getGooglePlaceRepository() { return googlePlaceRepository; }

}
