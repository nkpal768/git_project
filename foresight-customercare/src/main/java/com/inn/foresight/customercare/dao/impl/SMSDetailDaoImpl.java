package com.inn.foresight.customercare.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.inn.commons.configuration.ConfigUtils;
import com.inn.commons.lang.DateUtils;
import com.inn.core.generic.dao.impl.HibernateGenericDao;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.customercare.dao.ISMSDetailDao;
import com.inn.foresight.customercare.model.SMSDetail;
import com.inn.foresight.customercare.utils.SMSDetailConstants;
import com.inn.foresight.customercare.utils.SMSDetailUtils;
import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;

@Repository
public class SMSDetailDaoImpl extends HibernateGenericDao<Integer, SMSDetail> implements ISMSDetailDao {

	private Logger logger = LogManager.getLogger(SMSDetailDaoImpl.class);

	public SMSDetailDaoImpl() {
		super(SMSDetail.class);
	}

	@Override
	public String getDistinctMessage() {
		logger.info("Going to fetch distinct message from sms detail");
		String message = ForesightConstants.BLANK_STRING;
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
			Root<SMSDetail> root = criteriaQuery.from(SMSDetail.class);
			criteriaQuery.select(root.get(ForesightConstants.MESSAGE)).distinct(true);
			Query query = getEntityManager().createQuery(criteriaQuery);
			message = (String) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Error in getting distinct message from sms detail : {}", Utils.getStackTrace(e));
		}
		return message;
	}

	@Override
	public Boolean updateMessageForSMSDetail(String message) {
		logger.info("Going to update message for sms detail  : {}", message);
		Boolean status = false;
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaUpdate<SMSDetail> update = criteriaBuilder.createCriteriaUpdate(SMSDetail.class);
			Root<SMSDetail> employee = update.from(SMSDetail.class);
			update.set(employee.get(ForesightConstants.MESSAGE), message);
			Query query = getEntityManager().createQuery(update);
			int rowCount = 0;
			rowCount = query.executeUpdate();
			if (rowCount != 0) {
				status = true;
			}
		} catch (Exception e) {
			logger.error("Error in updating sms detail message in db : {}", Utils.getStackTrace(e));
		}
		return status;
	}

	@Override
	public Long getTotalSMSSent(String searchValue) {
		logger.info("Going to fetch total count of SMS sent");
		Long totalSMS = null;
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<SMSDetailWrapper> criteriaQuery = criteriaBuilder.createQuery(SMSDetailWrapper.class);
			Root<SMSDetail> root = criteriaQuery.from(SMSDetail.class);
			SMSDetailUtils.getSelectionInCriteriaQuery(criteriaBuilder, criteriaQuery, root);
			if (searchValue != null) {
				SMSDetailUtils.getWhereClauseInCriteriaQuery(searchValue, criteriaBuilder, criteriaQuery, root);
			} else {
				criteriaQuery.where((criteriaBuilder.between(
						criteriaBuilder.function(SMSDetailConstants.FUNCTION_DATE, Date.class,
								root.get(SMSDetailConstants.SMS_DETAIL_SENT_DATE_TIME)),
						DateUtils.addDays(new Date(), ConfigUtils.getInteger(SMSDetailConstants.TIME_PERIOD)), new Date())));
			}
			SMSDetailUtils.getGroupByClauseInCriteriaQuery(criteriaBuilder, criteriaQuery, root);
			Query query = getEntityManager().createQuery(criteriaQuery);
			totalSMS = (long) query.getResultList().size();
		} catch (QueryTimeoutException queryTimeoutException) {
			logger.error("Data can't be fetched because of timeout {}", Utils.getStackTrace(queryTimeoutException));
		} catch (NoResultException noResultException) {
			logger.error("Result not found {}", Utils.getStackTrace(noResultException));
		} catch (Exception exception) {
			logger.error("Error while fetching the count of SMS sent {}", Utils.getStackTrace(exception));
		}
		return totalSMS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SMSDetailWrapper> getSMSDetail(Integer llimit, Integer ulimit) {
		logger.info("Going to get the details of sent SMS");
		List<SMSDetailWrapper> list = new ArrayList<>();
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<SMSDetailWrapper> criteriaQuery = criteriaBuilder.createQuery(SMSDetailWrapper.class);
			Root<SMSDetail> root = criteriaQuery.from(SMSDetail.class);
			SMSDetailUtils.getSelectionInCriteriaQuery(criteriaBuilder, criteriaQuery, root);
			criteriaQuery.where((criteriaBuilder.between(
					criteriaBuilder.function(SMSDetailConstants.FUNCTION_DATE, Date.class, root.get(SMSDetailConstants.SMS_DETAIL_SENT_DATE_TIME)),
					DateUtils.addDays(new Date(), ConfigUtils.getInteger(SMSDetailConstants.TIME_PERIOD)), new Date())));
			SMSDetailUtils.getGroupByClauseInCriteriaQuery(criteriaBuilder, criteriaQuery, root);
			SMSDetailUtils.setOrderByDateTime(criteriaBuilder, criteriaQuery, root);
			Query query = getEntityManager().createQuery(criteriaQuery);
			SMSDetailUtils.setPaginationInQuery(llimit, ulimit, query);
			list = query.getResultList();
			logger.info("ResultList: {}", list);
		} catch (QueryTimeoutException queryTimeoutException) {
			logger.error("SMS Details can't be fetched because of timeout: {}", Utils.getStackTrace(queryTimeoutException));
		} catch (NoResultException noResultException) {
			logger.error("Result not found {}", Utils.getStackTrace(noResultException));
		} catch (Exception exception) {
			logger.error("Error while fetching the details of SMS {}", Utils.getStackTrace(exception));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SMSDetailWrapper> searchSMSDetailBySearchValue(String searchValue, Integer llimit, Integer ulimit) {
		logger.info("Going to search SMS details by search value");
		List<SMSDetailWrapper> list = new ArrayList<>();
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<SMSDetailWrapper> criteriaQuery = criteriaBuilder.createQuery(SMSDetailWrapper.class);
			Root<SMSDetail> root = criteriaQuery.from(SMSDetail.class);
			SMSDetailUtils.getSelectionInCriteriaQuery(criteriaBuilder, criteriaQuery, root);
			if (searchValue != null && !searchValue.isEmpty()) {
				SMSDetailUtils.getWhereClauseInCriteriaQuery(searchValue, criteriaBuilder, criteriaQuery, root);
			}
			SMSDetailUtils.getGroupByClauseInCriteriaQuery(criteriaBuilder, criteriaQuery, root);
			SMSDetailUtils.setOrderByDateTime(criteriaBuilder, criteriaQuery, root);
			Query query = getEntityManager().createQuery(criteriaQuery);
			SMSDetailUtils.setPaginationInQuery(llimit, ulimit, query);
			list = query.getResultList();
			logger.info("Result List {}", list);
		} catch (QueryTimeoutException queryTimeoutException) {
			logger.error("Searching can't be performed because of timeout:{}", Utils.getStackTrace(queryTimeoutException));
		} catch (NoResultException noResultException) {
			logger.error("Result not found:{}", Utils.getStackTrace(noResultException));
		} catch (Exception exception) {
			logger.error("Error in getting SMS details by search value : {} ", Utils.getStackTrace(exception));
		}
		return list;
	}

	@Override
	public Integer updateMessageInSystemConfiguration(String value) {
		Integer updateCount = 0;
		try {
			Query query = getEntityManager().createNativeQuery("update SystemConfiguration set value=:value where name='CUSTOMER_CARE_AUTO_SMS_URL'");
			query.setParameter("value", value);
			updateCount = query.executeUpdate();
		} catch (Exception exception) {
			logger.error("Unable to update Message In SystemConfiguration Exception {} ", Utils.getStackTrace(exception));
		}
		return updateCount;
	}

}
