package com.go4lunch.model.details;

import com.go4lunch.model.details.Close;
import com.go4lunch.model.details.Open;
import com.google.gson.annotations.SerializedName;

public class PeriodsItem{

	@SerializedName("close")
	private Close close;

	@SerializedName("open")
	private Open open;

	public Close getClose(){
		return close;
	}

	public Open getOpen(){
		return open;
	}
}