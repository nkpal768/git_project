package com.inn.foresight.customercare.rest;

import java.util.List;

import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;
import com.inn.product.systemconfiguration.model.SystemConfiguration;

public interface ISMSDetailRest {

	Long getTotalSMSSent(String searchValue);

	List<SMSDetailWrapper> getSMSDetail(Integer llimit, Integer ulimit);

	List<SMSDetailWrapper> searchSMSDetailBySearchValue(String searchValue, Integer llimit, Integer ulimit);

	SystemConfiguration getMessageUrlForSMSDetail(String name);

	
}
