package com.go4lunch.model.details;

import com.go4lunch.model.details.OpeningHours;
import com.google.gson.annotations.SerializedName;

public class DetailRestaurantResult {

	@SerializedName("website")
	private String website;

	@SerializedName("opening_hours")
	private OpeningHours openingHours;

	@SerializedName("rating")
	private double rating;

	@SerializedName("formatted_phone_number")
	private String formattedPhoneNumber;

	@SerializedName("url")
	private String url;

	public String getWebsite(){
		return website;
	}

	public OpeningHours getOpeningHours(){
		return openingHours;
	}

	public double getRating(){
		return rating;
	}

	public String getFormattedPhoneNumber(){
		return formattedPhoneNumber;
	}

	public String getUrl(){
		return url;
	}
}