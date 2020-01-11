package com.inn.foresight.customercare.utils;

public enum CustomerCareEnum {
	QUICKTEST("QUICKTEST"), LIVE_LOCATION("LIVE_LOCATION"), HOME_WORK_LOCATION("HOME_WORK");

	private final String value;

	public String getValue() {
		return value;
	}

	private CustomerCareEnum(String value) {
		this.value = value;
	}
}