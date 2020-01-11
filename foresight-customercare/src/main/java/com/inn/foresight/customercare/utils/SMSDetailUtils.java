package com.inn.foresight.customercare.utils;

import java.util.Date;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.inn.commons.configuration.ConfigUtils;
import com.inn.commons.lang.DateUtils;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.customercare.model.SMSDetail;
import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;

public class SMSDetailUtils {

	private SMSDetailUtils() {
		super();
	}

	public static void getGroupByClauseInCriteriaQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<SMSDetailWrapper> criteriaQuery,
			Root<SMSDetail> root) {
		criteriaQuery.groupBy(root.get(SMSDetailConstants.SMS_DETAIL_MSISDN),
				root.get(SMSDetailConstants.SMS_DETAIL_SENT_BY).get(SMSDetailConstants.SMS_DETAIL_USER_NAME),
				criteriaBuilder.function(SMSDetailConstants.FUNCTION_DATE, Date.class, root.get(SMSDetailConstants.SMS_DETAIL_SENT_DATE_TIME)));
	}

	public static void getWhereClauseInCriteriaQuery(String searchValue, CriteriaBuilder criteriaBuilder,
			CriteriaQuery<SMSDetailWrapper> criteriaQuery, Root<SMSDetail> root) {
		criteriaQuery.where(
				criteriaBuilder.like(criteriaBuilder.upper(root.get(SMSDetailConstants.SMS_DETAIL_SEARCH_VALUE)),
						ForesightConstants.PERCENT + searchValue.toUpperCase() + ForesightConstants.PERCENT),
				criteriaBuilder.and(criteriaBuilder.between(
						criteriaBuilder.function(SMSDetailConstants.FUNCTION_DATE, Date.class,
								root.get(SMSDetailConstants.SMS_DETAIL_SENT_DATE_TIME)),
						DateUtils.addDays(new Date(), ConfigUtils.getInteger(SMSDetailConstants.TIME_PERIOD)), new Date())));
	}

	public static void getSelectionInCriteriaQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<SMSDetailWrapper> criteriaQuery,
			Root<SMSDetail> root) {
		criteriaQuery
				.select(criteriaBuilder.construct(SMSDetailWrapper.class, root.get(SMSDetailConstants.SMS_DETAIL_MSISDN),
						root.get(SMSDetailConstants.SMS_DETAIL_SENT_BY).get(SMSDetailConstants.SMS_DETAIL_USER_NAME),
						criteriaBuilder.function(SMSDetailConstants.FUNCTION_DATE, Date.class,
								root.get(SMSDetailConstants.SMS_DETAIL_SENT_DATE_TIME)),
						root.get(SMSDetailConstants.SMS_DETAIL_DURATION), criteriaBuilder.count(root.get(SMSDetailConstants.SMS_DETAIL_ID)),
						root.get(SMSDetailConstants.SMS_DETAIL_EXECUTIVE_ROLE)));
	}

	public static void setOrderByDateTime(CriteriaBuilder criteriaBuilder, CriteriaQuery<SMSDetailWrapper> criteriaQuery, Root<SMSDetail> root) {
		criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.max(root.get(SMSDetailConstants.SMS_DETAIL_SENT_DATE_TIME))));
	}

	public static void setPaginationInQuery(Integer llimit, Integer ulimit, Query query) {
		if (llimit != null && ulimit != null && llimit >= 0 && ulimit > 0) {
			query.setMaxResults(ulimit - llimit + 1);
			query.setFirstResult(llimit);
		}
	}
}
