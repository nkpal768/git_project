package com.inn.foresight.customercare.utils.wrapper;

public class ChildChannelCodeWrapper {
	private String channelName;
	private String channelCode;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String toString() {
		return "ChildChannelCodeWrapper [channelName=" + channelName + ", channelCode=" + channelCode + "]";
	}

}
