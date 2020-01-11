package com.inn.foresight.customercare.utils.wrapper;

import java.util.List;

import com.inn.core.generic.wrapper.RestWrapper;

@RestWrapper
public class ParentChannelCodeWrapper {
	private String channelName;
	private String channelCode;
	List<ChildChannelCodeWrapper> childChannelCodeWrappers;

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

	public List<ChildChannelCodeWrapper> getChildChannelCodeWrappers() {
		return childChannelCodeWrappers;
	}

	public void setChildChannelCodeWrappers(List<ChildChannelCodeWrapper> childChannelCodeWrappers) {
		this.childChannelCodeWrappers = childChannelCodeWrappers;
	}

	@Override
	public String toString() {
		return "ParentChannelCodeWrapper [channelName=" + channelName + ", channelCode=" + channelCode + ", childChannelCodeWrappers="
				+ childChannelCodeWrappers + "]";
	}
}
