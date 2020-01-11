package com.inn.foresight.customercare.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.inn.core.generic.dao.impl.HibernateGenericDao;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.customercare.dao.IChannelCodeDao;
import com.inn.foresight.customercare.model.ChannelCode;
import com.inn.foresight.customercare.utils.CustomerCareConstants;

@Repository
public class ChannelCodeDaoImpl extends HibernateGenericDao<Integer, ChannelCode> implements IChannelCodeDao {

	private Logger logger = LogManager.getLogger(ChannelCodeDaoImpl.class);

	public ChannelCodeDaoImpl() {
		super(ChannelCode.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChannelCode> getAllChannelDetail() {
		List<ChannelCode> channelCodes = null;
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<ChannelCode> criteriaQuery = criteriaBuilder.createQuery(ChannelCode.class);
			Root<ChannelCode> root = criteriaQuery.from(ChannelCode.class);
			criteriaQuery.select(root);
			criteriaQuery.where(root.get(CustomerCareConstants.CHANNELCODE).get(CustomerCareConstants.CHANNEL_ID).isNull());
			Query query = getEntityManager().createQuery(criteriaQuery);
			channelCodes = query.getResultList();
		} catch (NoResultException noResultException) {
			logger.error("No Result found while getting ChannelCode noResultException {} ", Utils.getStackTrace(noResultException));
		} catch (QueryTimeoutException queryTimeoutException) {
			logger.error("Query TimeOut Exception while getting data from ChannelCode queryTimeoutException {} ",
					Utils.getStackTrace(queryTimeoutException));
		} catch (Exception exception) {
			logger.error("Error in getting all Channel Code Exception {} ", Utils.getStackTrace(exception));
		}
		return channelCodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChannelCode> getChannelDetailByChannelId(Integer channelId) {
		List<ChannelCode> channelCodes = null;
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<ChannelCode> criteriaQuery = criteriaBuilder.createQuery(ChannelCode.class);
			Root<ChannelCode> root = criteriaQuery.from(ChannelCode.class);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get(CustomerCareConstants.CHANNELCODE).get(CustomerCareConstants.CHANNEL_ID), channelId));
			Query query = getEntityManager().createQuery(criteriaQuery);
			channelCodes = query.getResultList();
		} catch (NoResultException noResultException) {
			logger.error("No Result found while getting ChannelCode for channelId {} noResultException {} ", channelId,
					Utils.getStackTrace(noResultException));
		} catch (QueryTimeoutException queryTimeoutException) {
			logger.error("Query TimeOut Exception while getting data for channelId {} from ChannelCode queryTimeoutException {} ", channelId,
					Utils.getStackTrace(queryTimeoutException));
		} catch (Exception exception) {
			logger.error("Error in getting all Channel Code for channelId {} Exception {} ", channelId, Utils.getStackTrace(exception));
		}
		return channelCodes;
	}
}
