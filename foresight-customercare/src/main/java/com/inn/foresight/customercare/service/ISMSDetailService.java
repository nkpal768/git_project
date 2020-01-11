package com.inn.foresight.customercare.service;

import java.util.List;
import java.util.Map;

import com.inn.core.generic.service.IGenericService;
import com.inn.foresight.customercare.model.SMSDetail;
import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;
import com.inn.product.systemconfiguration.model.SystemConfiguration;

public interface ISMSDetailService extends IGenericService<Integer, SMSDetail> {

	List<SMSDetailWrapper> getSMSDetail(Integer llimit, Integer ulimit);

	Long getTotalSMSSent(String searchValue);

	List<SMSDetailWrapper> searchSMSDetailBySearchValue(String searchValue, Integer llimit, Integer ulimit);

	String sendSmsBySmsType(String msisdn, String smsType, String imsi,String deviceId,String deviceOs);

	Map<String, String> createSMSDetails(String msisdn, String type);

	SystemConfiguration getMessageUrlForSMSDetail(String name);

	Map<String, String> updateMessageForSMSDetail(SystemConfiguration systemConfiguration);

}
