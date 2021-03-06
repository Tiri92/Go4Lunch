package com.go4lunch.model.details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantDetailResult {

	@SerializedName("website")
	private String website;

	@SerializedName("opening_hours")
	private OpeningHours openingHours;

	@SerializedName("place_id")
	private String placeId;

	@SerializedName("photos")
	private List<PhotosItem> photos;

	@SerializedName("name")
	private String name;

	@SerializedName("geometry")
	private Geometry geometry;

	@SerializedName("vicinity")
	private String vicinity;

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

	public String getPlaceId(){
		return placeId;
	}

	public double getRating(){
		return rating;
	}

	public List<PhotosItem> getPhotos(){
		return photos;
	}

	public String getVicinity(){
		return vicinity;
	}

	public String getName(){
		return name;
	}

	public Geometry getGeometry(){
		return geometry;
	}

	public String getFormattedPhoneNumber(){
		return formattedPhoneNumber;
	}

	public String getUrl(){
		return url;
	}
}