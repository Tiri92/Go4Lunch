package com.go4lunch.model.autocomplete;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PredictionsResultItem {

	@SerializedName("reference")
	private String reference;

	@SerializedName("types")
	private List<String> types;

	@SerializedName("matched_substrings")
	private List<MatchedSubstringsItem> matchedSubstrings;

	@SerializedName("terms")
	private List<TermsItem> terms;

	@SerializedName("structured_formatting")
	private StructuredFormatting structuredFormatting;

	@SerializedName("description")
	private String description;

	@SerializedName("place_id")
	private String placeId;

	public String getReference(){
		return reference;
	}

	public List<String> getTypes(){
		return types;
	}

	public List<MatchedSubstringsItem> getMatchedSubstrings(){
		return matchedSubstrings;
	}

	public List<TermsItem> getTerms(){
		return terms;
	}

	public StructuredFormatting getStructuredFormatting(){
		return structuredFormatting;
	}

	public String getDescription(){
		return description;
	}

	public String getPlaceId(){
		return placeId;
	}
}