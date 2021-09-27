package com.go4lunch.model.autocomplete;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AutocompleteSearch {

	@SerializedName("predictions")
	private List<PredictionsResultItem> predictions;

	@SerializedName("status")
	private String status;

	public List<PredictionsResultItem> getPredictions(){
		return predictions;
	}

	public String getStatus(){
		return status;
	}
}