package com.go4lunch.model.details;

import com.google.gson.annotations.SerializedName;

public class Open{

	@SerializedName("time")
	private String time;

	@SerializedName("day")
	private int day;

	public String getTime(){
		return time;
	}

	public int getDay(){
		return day;
	}
}