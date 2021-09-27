package com.go4lunch.model.details;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DetailSearch {

	@SerializedName("result")
	private RestaurantDetailResult result;

	@SerializedName("html_attributions")
	private List<Object> htmlAttributions;

	@SerializedName("status")
	private String status;

	public RestaurantDetailResult getResult(){
		return result;
	}

	public List<Object> getHtmlAttributions(){
		return htmlAttributions;
	}

	public String getStatus(){
		return status;
	}
}