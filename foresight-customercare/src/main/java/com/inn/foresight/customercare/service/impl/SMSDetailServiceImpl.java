package com.inn.foresight.customercare.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inn.commons.configuration.ConfigUtils;
import com.inn.commons.http.HttpGetRequest;
import com.inn.core.generic.exceptions.application.DaoException;
import com.inn.core.generic.service.impl.AbstractService;
import com.inn.foresight.core.generic.utils.ConfigEnum;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.core.maplayer.utils.GenericMapUtils;
import com.inn.foresight.core.sms.service.ISMPPClient;
import com.inn.foresight.core.subscriber.service.ISubscriberSearch;
import com.inn.foresight.customercare.dao.ISMSDetailDao;
import com.inn.foresight.customercare.enums.SMSType;
import com.inn.foresight.customercare.model.SMSDetail;
import com.inn.foresight.customercare.service.ISMSDetailService;
import com.inn.foresight.customercare.utils.SMSDetailConstants;
import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;
import com.inn.product.security.spring.userdetails.CustomerInfo;
import com.inn.product.systemconfiguration.dao.SystemConfigurationDao;
import com.inn.product.systemconfiguration.model.SystemConfiguration;
import com.inn.product.um.user.service.UserContextService;

@Service("SMSDetailServiceImpl")
public class SMSDetailServiceImpl extends AbstractService<Integer, SMSDetail> implements ISMSDetailService {

	private Logger logger = LogManager.getLogger(SMSDetailServiceImpl.class);

	@Autowired
	ISMSDetailDao iSMSDetailDao;

	@Autowired
	private CustomerInfo customerInfo;

	@Autowired
	ISMPPClient iSMPPClient;

	@Autowired
	private SystemConfigurationDao iSystemConfigurationDao;

	@Autowired
	private UserContextService userInContext;

	@Override
	@Transactional
	public Map<String, String> createSMSDetails(String msisdn, String type) {
		logger.info("Going to create sms details with  msisdn : {} type : {} ", msisdn, type);
		Map<String, String> map = new HashMap<>();
		String message = null;
		try {
			SMSDetail smsDetail = new SMSDetail();
			smsDetail.setSentDateTime(new Date());
			smsDetail.setMsisdn(msisdn);
			smsDetail.setUserId(userInContext.getUserInContextnew());
			setSmsType(type, smsDetail);
			setExecutiveRole(msisdn, smsDetail);
			smsDetail = iSMSDetailDao.create(smsDetail);
			message = setOutputMsg(smsDetail);
		} catch (Exception e) {
			logger.error("Exception while creating sms detail by msisdn : {} Exception : {} ", msisdn, Utils.getStackTrace(e));
		}
		logger.info("SMS detail message : {}", message);
		map.put(ForesightConstants.MESSAGE, message);
		return map;
	}

	private String setOutputMsg(SMSDetail smsDetail) {
		String message;
		if (smsDetail != null) {
			message = "SMS detail created sucessfully";
		} else {
			message = "SMS detail creation failed";
		}
		return message;
	}

	private void setExecutiveRole(String msisdn, SMSDetail smsDetail) {
		String userName = userInContext.getUserInContextnew().getUserName();
		if (userName != null && !userName.isEmpty()) {
			smsDetail.setSearchValue(userName + msisdn);
			Set<String> roleName = customerInfo.getRoleNameForActiveWorkspace();
			logger.info("ROLE Set : {}", roleName);

			if (roleName != null && !roleName.isEmpty()) {
				String roleNameInString = String.join(ForesightConstants.COMMA, roleName);
				logger.info("Role Append string : {}", roleNameInString);
				smsDetail.setExecutiveRole(roleNameInString);
			}
		}
	}

	private void setSmsType(String type, SMSDetail smsDetail) {
		if (type.equalsIgnoreCase(SMSType.AUTOMATIC.toString())) {
			smsDetail.setSmsType(SMSType.AUTOMATIC);
		} else if (type.equalsIgnoreCase(SMSType.MANUAL.toString())) {
			smsDetail.setSmsType(SMSType.MANUAL);
		}
	}

	@Override
	@Transactional
	public Map<String, String> updateMessageForSMSDetail(SystemConfiguration systemConfiguration) {
		logger.info("Going to update sms detail");
		Map<String, String> map = new HashMap<>();
		String msg = ForesightConstants.BLANK_STRING;
		try {
			systemConfiguration = iSystemConfigurationDao.update(systemConfiguration);
			if (systemConfiguration != null) {
				msg = SMSDetailConstants.MESSAGE_UPDATE_SUCCESSFULLY;
			} else {
				msg = SMSDetailConstants.MESSAGE_UPDATION_FAILED;
			}
		} catch (Exception e) {
			logger.error("Error in updating sms detail Exception : {}", Utils.getStackTrace(e));
		}
		map.put(ForesightConstants.MESSAGE, msg);
		logger.info("MESSAGE : {}", msg);
		return map;
	}

	@Override
	public Long getTotalSMSSent(String searchValue) {
		logger.info("Going to count total SMS sent");
		Long totalSMS = null;
		try {
			totalSMS = iSMSDetailDao.getTotalSMSSent(searchValue);
		} catch (DaoException daoException) {
			logger.info("Exception in DAO occured:{}", Utils.getStackTrace(daoException));
		} catch (Exception exception) {
			logger.info("Error in getting total SMS sent: {}", Utils.getStackTrace(exception));
		}
		return totalSMS;
	}

	@Override
	public List<SMSDetailWrapper> getSMSDetail(Integer llimit, Integer ulimit) {
		logger.info("Going to get the details of sent SMS");
		List<SMSDetailWrapper> list = new ArrayList<>();
		try {
			list = iSMSDetailDao.getSMSDetail(llimit, ulimit);
		} catch (Exception e) {
			logger.info("Error in getting the sms details:{}", Utils.getStackTrace(e));
		}
		return list;
	}

	@Override
	public List<SMSDetailWrapper> searchSMSDetailBySearchValue(String searchValue, Integer llimit, Integer ulimit) {
		logger.info("Going to search SMS details by search value");
		List<SMSDetailWrapper> list = new ArrayList<>();
		try {
			if (searchValue != null && !searchValue.isEmpty())
				list = iSMSDetailDao.searchSMSDetailBySearchValue(searchValue, llimit, ulimit);
		} catch (Exception e) {
			logger.info("Error in searching the sms details by search value:{}", Utils.getStackTrace(e));
		}
		return list;
	}

	@Override
	public String sendSmsBySmsType(String msisdn, String smsType, String imsi, String deviceId, String deviceOs) {
		logger.info("Inside sendSmsBySmsType mdn : {} type : {} imsi : {}", msisdn, smsType, imsi);
		String messageId = null;
		try {
			if (smsType.equalsIgnoreCase(SMSType.AUTOMATIC.toString())) {
				if (ConfigUtils.getString(SMSDetailConstants.IMSI_AUTO_SMS_ENABLE).equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)
						&& !checkNVInstallationDetail(imsi, deviceId, deviceOs)) {
					messageId = sendSMSToMsisdn(msisdn);
				}
			} else if (smsType.equalsIgnoreCase(SMSType.MANUAL.toString())) {
				String message = getMessageForSendSms();
				if (Utils.checkForValueInString(message)) {
					messageId = iSMPPClient.sendSMSByMsisdn(ISubscriberSearch.trimMDNByCountryCode(msisdn), message);
				}
			}
		} catch (Exception exception) {
			logger.error("Error in sending message for msisdn {} smsType {} and imsi {} Exception {} ", msisdn, smsType, imsi,
					Utils.getStackTrace(exception));
		}
		return messageId;
	}

	private String getMessageForSendSms() {
		SystemConfiguration systemConfiguration = getMessageUrlForSMSDetail(SMSDetailConstants.CUSTOMER_CARE_AUTO_SMS_URL);
		String message = null;
		if (systemConfiguration != null && systemConfiguration.getValue() != null && !systemConfiguration.getValue().isEmpty()) {
			message = systemConfiguration.getValue();
		}
		logger.info("Message for send sms : {}", message);
		return message;
	}

	private String sendSMSToMsisdn(String msisdn) {
		logger.info("Going to send sms for msisdn : {}", msisdn);
		String messageId = null;
		try {
			List<SystemConfiguration> list = iSystemConfigurationDao.getSystemConfigurationByName(SMSDetailConstants.CUSTOMER_CARE_AUTO_SMS);
			if (list != null && list.get(0).getValue().equalsIgnoreCase(SMSDetailConstants.ENABLE)) {
				String message = getMessageForSendSms();
				if (Utils.checkForValueInString(message)) {
					messageId = iSMPPClient.sendSMSByMsisdn(ISubscriberSearch.trimMDNByCountryCode(msisdn), message);
				}
			}
		} catch (Exception e) {
			logger.error("Error in sending sms for msisdn : {} Exception : {}", msisdn, Utils.getStackTrace(e));
		}
		return messageId;
	}

	private Boolean checkNVInstallationDetail(String imsi, String deviceId, String deviceOs) {
		logger.info("Going to check nv installation detail for imsi : {}", imsi);
		Boolean isInstalled = false;
		try {
			String baseUrl = ConfigUtils.getString(ConfigEnum.MICRO_SERVICE_BASE_URL.getValue())
					+ ConfigUtils.getString(SMSDetailConstants.NV_INSTALLATION_DETAIL_URL);
			List<String> valueList = new ArrayList<>();
			valueList.add(imsi);
			valueList.add(deviceId);
			valueList.add(deviceOs);
			String query = GenericMapUtils.createGenericQuery(valueList, SMSDetailConstants.IMSI, SMSDetailConstants.DEVICE_ID,
					SMSDetailConstants.DEVICE_OS);
			String response = new HttpGetRequest(baseUrl + query).getString();
			if (response != null && !response.isEmpty()) {
				isInstalled = new Gson().fromJson(response, new TypeToken<Boolean>() {
				}.getType());
				logger.info("NV installation detail status : {}", isInstalled);
			}
		} catch (Exception e) {
			logger.error("Error in checking nv installation detail : {} ", Utils.getStackTrace(e));
		}
		return isInstalled;
	}

	@Override
	public SystemConfiguration getMessageUrlForSMSDetail(String name) {
		logger.info("Going to get message url for name : {}", name);
		SystemConfiguration systemConfiguration = null;
		try {
			if (name.equalsIgnoreCase(SMSDetailConstants.CUSTOMER_CARE_AUTO_SMS_URL)) {
				systemConfiguration = iSystemConfigurationDao.getConfigurationByName(name);
			}
		} catch (Exception e) {
			logger.error("Error in getting message url for sms detail : {}", Utils.getStackTrace(e));
		}
		logger.info("System Configuration Data: {}", systemConfiguration);
		return systemConfiguration;
	}

}
