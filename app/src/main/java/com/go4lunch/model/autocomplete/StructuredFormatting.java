package com.go4lunch.model.autocomplete;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StructuredFormatting{

	@SerializedName("main_text_matched_substrings")
	private List<MainTextMatchedSubstringsItem> mainTextMatchedSubstrings;

	@SerializedName("secondary_text")
	private String secondaryText;

	@SerializedName("main_text")
	private String mainText;

	public List<MainTextMatchedSubstringsItem> getMainTextMatchedSubstrings(){
		return mainTextMatchedSubstrings;
	}

	public String getSecondaryText(){
		return secondaryText;
	}

	public String getMainText(){
		return mainText;
	}
}