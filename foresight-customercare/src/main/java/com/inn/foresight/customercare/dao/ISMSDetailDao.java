package com.inn.foresight.customercare.dao;

import java.util.List;

import com.inn.core.generic.dao.IGenericDao;
import com.inn.foresight.customercare.model.SMSDetail;
import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;

public interface ISMSDetailDao extends IGenericDao<Integer, SMSDetail>{


	String getDistinctMessage();

	Boolean updateMessageForSMSDetail(String message);

	 Long getTotalSMSSent(String searchValue);
	 
	 List<SMSDetailWrapper> getSMSDetail(Integer llimit, Integer ulimit);
	 
	 List<SMSDetailWrapper> searchSMSDetailBySearchValue(String searchValue, Integer llimit, Integer ulimit);

	Integer updateMessageInSystemConfiguration(String value);

}
