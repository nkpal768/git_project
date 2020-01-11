package com.inn.foresight.customercare.enums;

public enum SMSType {

	AUTOMATIC("AUTOMATIC"), MANUAL("MANUAL");

	private String value;

	private SMSType(String value) {
		this.value = value;
	}

	private SMSType() {
	}

	public String getValue() {
		return value;
	}

}
