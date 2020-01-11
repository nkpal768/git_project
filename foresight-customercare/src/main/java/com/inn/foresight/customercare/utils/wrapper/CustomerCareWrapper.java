package com.inn.foresight.customercare.utils.wrapper;

import com.inn.core.generic.wrapper.RestWrapper;

@RestWrapper
public class CustomerCareWrapper {
	private Double latitude;
	private Double longitude;
	private String currentStage;
	private String neName;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	@Override
	public String toString() {
		return "CustomerCareWrapper [latitude=" + latitude + ", longitude=" + longitude + ", currentStage=" + currentStage + ", neName=" + neName
				+ "]";
	}

}
