package com.inn.foresight.customercare.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.inn.commons.configuration.ConfigUtils;
import com.inn.core.generic.dao.impl.HibernateGenericDao;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.core.infra.dao.INetworkElementDao;
import com.inn.foresight.core.infra.model.NetworkElement;
import com.inn.foresight.core.infra.model.RANDetail;
import com.inn.foresight.core.infra.utils.InfraConstants;
import com.inn.foresight.core.infra.utils.InfraUtils;
import com.inn.foresight.core.infra.utils.enums.Domain;
import com.inn.foresight.core.infra.utils.enums.NEStatus;
import com.inn.foresight.core.infra.utils.enums.NEType;
import com.inn.foresight.customercare.dao.ICustomerCareDao;
import com.inn.foresight.customercare.utils.CustomerCareConstants;
import com.inn.foresight.customercare.utils.CustomerCareUtils;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.module.fm.core.wrapper.ActiveAlarmOutageTimeWrapper;

@Repository
public class CustomerCareDaoImpl extends HibernateGenericDao<Integer, Object> implements ICustomerCareDao, CustomerCareConstants {

	/** The logger. */
	private Logger logger = LogManager.getLogger(CustomerCareDaoImpl.class);

	private static final String NE_TYPE_LIST = "neTypeList";
	private static final String NE_STATUS = "neStatus";
	private static final String DOMAIN_LIST = "domainList";

	@Autowired
	private INetworkElementDao iNetworkElementDao;

	public CustomerCareDaoImpl() {
		super(Object.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CustomerCareSiteWrapper> getPlannedSiteForCustomerCare(List<Domain> domainList, NEStatus neStatus, List<NEType> neTypeList) {
		logger.info("Going to fetch Planned Site Data for domain : {}, nestatus : {} and netype : {}", domainList, neStatus, neTypeList);
		List<CustomerCareSiteWrapper> careSiteWrappers = new ArrayList<>();
		if (Utils.isValidList(domainList) && Utils.isValidList(neTypeList) && neStatus != null) {
			try {
				String namedQuery = "select new com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper(ne.neName,ne.neId,ne.cellNum,ne.latitude,ne.longitude,rd.pci,rd.azimuth,nb.neFrequency,ne.neStatus,n.taskName,n.taskStatus,n.plannedStartDate,n.plannedEndDate,n.actualStartDate,n.actualEndDate,n.completionStatus,ne.vendor,ne.domain) "
						+ " from NETaskDetail n right outer join NEBandDetail nb on n.neBandDetail.id=nb.id left outer join RANDetail rd on rd.neBandDetail.id=nb.id inner join NetworkElement ne on ne.id=nb.networkElement.id where ne.latitude is not null and ne.longitude is not null and "
						+ "  upper(ne.domain) in (:domainList) and upper(ne.neType) in (:neTypeList) and upper(ne.neStatus)=:neStatus and ne.isDeleted=0";

				Query query = getEntityManager().createQuery(namedQuery);

				query.setParameter(DOMAIN_LIST, domainList);
				query.setParameter(NE_STATUS, neStatus);
				query.setParameter(NE_TYPE_LIST, neTypeList);

				careSiteWrappers = query.getResultList();
				logger.info("Total planned Sites is {} ", careSiteWrappers.size());
			} catch (Exception exception) {
				logger.error("Unable to fetch Planned Sites for Customer Care  Exception {} ", Utils.getStackTrace(exception));
			}
		}
		return careSiteWrappers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getSiteHistoryDataByViewPort(Double southWestLat, Double northEastLat, Double southWestLong, Double northEastLong,
			String startTime, String neStatus) {
		logger.info(
				"Going to get site history by view port southWestLat : {}, northEastLat : {}, southWestLong : {}, northEastLong : {}, startTime : {} ",
				southWestLat, northEastLat, southWestLong, northEastLong, startTime);
		List<Object[]> siteList = null;
		try {
			String sqlQuery = "select s.latitude,s.longitude,s.nename, s.nefrequency, s.neid, s.domain, s.vendor, s.nestatus, s.pci, s.azimuth, s.cellId, s.taskname, s.taskstatus, s.plannedstartdate, s.plannedenddate, s.actualstartdate, s.actualenddate, s.completionstatus, s.carrier, s.sector, s.parentnename, s.parentneid from SiteHistory s "
					+ "where s.latitude >= :southWestLat and s.latitude <= :northEastLat and s.longitude >= :southWestLong and s.longitude <= :northEastLong "
					+ "and date(s.creationtime) = date(:startTime) and upper(nestatus) =upper(:neStatus) and upper(netype) =upper(:neType)";
			Query query = getEntityManager().createNativeQuery(sqlQuery);
			query.setParameter(SOUTH_WEST_LAT, southWestLat);
			query.setParameter(NORTH_EAST_LAT, northEastLat);
			query.setParameter(SOUTH_WEST_LONG, southWestLong);
			query.setParameter(NORTH_EAST_LONG, northEastLong);
			query.setParameter(START_TIME, startTime);
			query.setParameter(NE_STATUS, neStatus);
			query.setParameter(NE_TYPE, NEType.MACRO_CELL.toString());
			siteList = query.getResultList();
			logger.info("History sites size : {}", siteList.size());
		} catch (NoResultException noResultException) {
			logger.error("Error in getting  Site History data by View Port NoResultException : {}", noResultException.getMessage());
		} catch (Exception e) {
			logger.error("Error in getting site history by view port : {}", Utils.getStackTrace(e));
		}
		return siteList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getHighlyUtilizedCellByNeidList(List<String> neidList, String startTime, String endTime) {
		logger.info("Going to get highly utilize cell by neid list : {}, startTime : {} and endTime : {}", neidList, startTime, endTime);
		List<Object[]> siteList = null;
		try {
			String sqlQuery = "select s.neid, c.highlyutilised1 from SiteHistory s left join CapacityDetail c on s.siteauditid_pk=c.siteauditid_fk where s.neid in (?) and date_format(c.creationtime,'%Y%m%d')=date_format(?,'%Y%m%d')) and  date_format(c.creationtime,'%Y%m%d')=date_format(?,'%Y%m%d')) ";
			Query query = getEntityManager().createNativeQuery(sqlQuery);
			query.setParameter(ONE, neidList);
			query.setParameter(TWO, startTime);
			query.setParameter(THREE, endTime);
			siteList = query.getResultList();
		} catch (NoResultException noResultException) {
			logger.error("Error in getting HUC by neidList NoResultException : {}", noResultException.getMessage());
		} catch (Exception e) {
			logger.error("Error in getting highly utilize cell by neid list : {}", Utils.getStackTrace(e));
		}
		return siteList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getSiteDetailByNeidList(List<String> neidList, String vendor, String startTime, String endTime) {
		List<Object[]> list = null;
		try {
			Query query = getEntityManager().createNativeQuery(
					"select distinct nename, latitude, longitude, cellid, nefrequency,neid, domain from SiteHistory where neid in :neidList and vendor =:vendor and date(creationtime) >= date(:startTime) and date(creationtime) <= date(:endTime)");
			query.setParameter(NEID_LIST, neidList);
			query.setParameter(NE_VENDOR, vendor);
			query.setParameter(START_TIME, startTime);
			query.setParameter(END_TIME, endTime);
			list = query.getResultList();
			logger.info("Domain vendor list size : {}", list.size());
		} catch (NoResultException noResultException) {
			logger.error("Error in getting data by neid NoResultException : {}", noResultException.getMessage());
		} catch (Exception e) {
			logger.error("Error in getting site detail by neid list : {}", Utils.getStackTrace(e));
		}
		return list;
	}

	@Override
	public Object[] getSiteDataByNeId(String nename, String startTime, String endTime) {
		Object[] latlong = null;
		try {
			Query query = getEntityManager().createNativeQuery(
					"select distinct latitude, longitude, cellid, nefrequency, nename from SiteHistory where nename =:nename and date(creationtime) >= date(:startTime)and date(creationtime) <= date(:endTime)");
			query.setParameter("nename", nename);
			query.setParameter("startTime", startTime);
			query.setParameter("endTime", endTime);
			latlong = (Object[]) query.getSingleResult();
		} catch (NoResultException noResultException) {
			logger.error("Error in getting data by neid   NoResultException : {}", noResultException.getMessage());
		} catch (Exception e) {
			logger.error("Error in getting data by ne id  : {}", Utils.getStackTrace(e));
		}
		return latlong;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RANDetail> getRANDetailByCGI(List<Integer> cgiList) {
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<RANDetail> criteriaQuery = criteriaBuilder.createQuery(RANDetail.class);
			List<Predicate> predicates = new ArrayList<>();
			Root<RANDetail> root = criteriaQuery.from(RANDetail.class);

			if (Utils.isValidList(cgiList)) {
				Expression<String> naNameExpression = root.get("cgi");
				Predicate eNamePredicate = naNameExpression.in(cgiList);
				predicates.add(eNamePredicate);
				predicates.add(criteriaBuilder.equal(root.get("networkElement").get("isDeleted"), ZERO));
				criteriaQuery.select(root).where(predicates.toArray(new Predicate[] {}));
				Query query = getEntityManager().createQuery(criteriaQuery);
				return query.getResultList();

			}
		} catch (Exception e) {
			logger.error("Error in getRANDetailByCGI  : {}", Utils.getStackTrace(e));
		}
		return Collections.emptyList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> searchNEDetail(String neName) {
		logger.info("Going to search NE Detail for nename : {}", neName);
		Map<String, Object> neDetailMap = new HashMap<>();
		try {
			List<String> neTypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_CELL_LIST);
			CustomerCareUtils.validateListData(neTypeList);
			logger.info("NE TYPE List : {}", neTypeList);

			Map<String, List<String>> projection = new HashMap<>();
			InfraUtils.setProjectionList(projection, InfraConstants.NETWORKELEMENT_TABLE, InfraConstants.NE_LATITUDE_KEY,
					InfraConstants.NE_LONGITUDE_KEY, InfraConstants.NE_NENAME_KEY);

			Map<String, List<Map>> filterMap = new HashMap<>();
			List<Map> neFilterList = new ArrayList<>();
			InfraUtils.setFilterMap(filterMap, neFilterList, InfraConstants.NETWORKELEMENT_TABLE, InfraConstants.NE_NETYPE_KEY, neTypeList,
					InfraConstants.NETYPE_ENUM_KEY, InfraConstants.IN_OPERATOR);
			InfraUtils.setFilterMap(filterMap, neFilterList, InfraConstants.NETWORKELEMENT_TABLE, InfraConstants.NE_NENAME_KEY, neName,
					InfraConstants.STRING_DATATYPE_KEY, InfraConstants.EQUALS_OPERATOR);

			List<Tuple> tuples = iNetworkElementDao.searchNEDetail(filterMap, projection, null, null, TRUE, null);

			if (Utils.isValidList(tuples)) {
				Tuple tuple = tuples.get(ForesightConstants.ZERO);
				neDetailMap.put(InfraConstants.NE_NENAME_KEY, tuple.get(InfraConstants.NE_NENAME_KEY));
				neDetailMap.put(InfraConstants.NE_LATITUDE_KEY, tuple.get(InfraConstants.NE_LATITUDE_KEY));
				neDetailMap.put(InfraConstants.NE_LONGITUDE_KEY, tuple.get(InfraConstants.NE_LONGITUDE_KEY));
			}
			logger.info("Found BTS Detail for nename :  {} data : {}", neName, neDetailMap);
		} catch (Exception e) {
			logger.error("Error in fetching  NE Detail for nename : {}  Exception : {}", neName, Utils.getStackTrace(e));
		}
		return neDetailMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getNeIdListByBSPSiteList(List<String> bspSiteList) {
		logger.info("Going to search NE Detail for bsp site list : {}", bspSiteList.size());
		List<String> neIdList = new ArrayList<>();
		try {
			if (Utils.isValidList(bspSiteList)) {
				List<String> cellNETypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_CELL_LIST);
				List<String> domainList = ConfigUtils.getStringList(CC_RAN_DOMAIN_LIST);
				CustomerCareUtils.validateListData(domainList, cellNETypeList);
				String neStatus = ConfigUtils.getString(CC_RAN_NESTATUS_ONAIR);
				String bspListColumn = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_BSP_SITE_LIST_COLUMN), InfraConstants.NE_NEID_KEY);

				Map<String, List<String>> projection = new HashMap<>();
				InfraUtils.setProjectionList(projection, InfraConstants.NETWORKELEMENT_TABLE, InfraConstants.NE_NEID_KEY);

				Map<String, List<Map>> filterMap = new HashMap<>();
				List<Map> neFilterList = new ArrayList<>();
				InfraUtils.setFilterMap(filterMap, neFilterList, InfraConstants.NETWORKELEMENT_TABLE, InfraConstants.NE_NESTATUS_KEY, neStatus,
						InfraConstants.NESTATUS_ENUM_KEY, InfraConstants.EQUALS_OPERATOR);
				InfraUtils.setFilterMap(filterMap, neFilterList, InfraConstants.NETWORKELEMENT_TABLE, InfraConstants.NE_NETYPE_KEY, cellNETypeList,
						InfraConstants.NETYPE_ENUM_KEY, InfraConstants.IN_OPERATOR);
				InfraUtils.setFilterMap(filterMap, neFilterList, InfraConstants.NETWORKELEMENT_TABLE, bspListColumn, bspSiteList,
						InfraConstants.STRING_DATATYPE_KEY, InfraConstants.IN_OPERATOR);

				List<Tuple> tuples = iNetworkElementDao.searchNEDetail(filterMap, projection, null, null, TRUE, null);

				if (Utils.isValidList(tuples)) {
					tuples.forEach(tuple -> neIdList.add((String) tuple.get(InfraConstants.NE_NEID_KEY)));
				}
				logger.info("BSP site list : {} and found neid list : {}", bspSiteList.size(), neIdList.size());
			}
		} catch (Exception e) {
			logger.error("Error in fetching  NE id list Detail for Exception : {}", Utils.getStackTrace(e));
		}
		return neIdList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveAlarmOutageTimeWrapper> getNEDetailByNEIdList(List<String> neIdList) {
		logger.info("Going to get network element detail by neid list : {}", Utils.isValidList(neIdList) ? neIdList.size() : ZERO);
		List<ActiveAlarmOutageTimeWrapper> list = new ArrayList<>();
		try {
			if (neIdList != null && !neIdList.isEmpty()) {

				String bspListColumn = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_BSP_SITE_LIST_COLUMN), InfraConstants.NE_NEID_KEY);
				String neStatus = ConfigUtils.getString(CC_RAN_NESTATUS_ONAIR);
				List<String> cellNETypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_CELL_LIST);
				List<String> domainList = ConfigUtils.getStringList(CC_RAN_DOMAIN_LIST);
				CustomerCareUtils.validateListData(domainList, cellNETypeList);

				CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
				CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
				Root<NetworkElement> root = criteriaQuery.from(NetworkElement.class);
				criteriaQuery.multiselect(root.get(InfraConstants.NE_NEFREQUENCY_KEY), root.get(InfraConstants.NE_LATITUDE_KEY),
						root.get(InfraConstants.NE_LONGITUDE_KEY), root.get(InfraConstants.NE_NENAME_KEY), root.get(InfraConstants.NE_NESTATUS_KEY),
						root.get(InfraConstants.NE_NEID_KEY), root.get(InfraConstants.NE_NETWORKELEMENT_KEY).get(InfraConstants.NE_NEID_KEY));

				List<Predicate> predicateList = new ArrayList<>();
				predicateList.add(criteriaBuilder.equal(root.get(InfraConstants.NE_NESTATUS_KEY), NEStatus.valueOf(neStatus)));
				predicateList.add(root.get(InfraConstants.NE_DOMAIN_KEY).in(Utils.convertStringToEnumList(Domain.class, domainList)));
				predicateList.add(root.get(InfraConstants.NE_NETYPE_KEY).in(Utils.convertStringToEnumList(NEType.class, cellNETypeList)));
				predicateList.add(root.get(bspListColumn).in(neIdList));

				criteriaQuery.where(predicateList.toArray(new Predicate[] {}));
				Query query = getEntityManager().createQuery(criteriaQuery);
				list = populateDataIntoWrapper(query.getResultList(), list);
				logger.info("Found neid list for active alarm data : {}", list.size());
			}
		} catch (Exception exception) {
			logger.error("Error in getting networkelement detail by child neid list Exception :  {}", Utils.getStackTrace(exception));
		}
		return list;
	}

	private List<ActiveAlarmOutageTimeWrapper> populateDataIntoWrapper(List<Object[]> resultData, List<ActiveAlarmOutageTimeWrapper> list) {
		if (resultData != null && !resultData.isEmpty()) {
			list = new ArrayList<>();
			for (Object[] row : resultData) {
				ActiveAlarmOutageTimeWrapper activeAlarmOutageTimeWrapper = new ActiveAlarmOutageTimeWrapper();
				activeAlarmOutageTimeWrapper
						.setBand(Utils.checkForValueInString(row[ZERO].toString()) ? Integer.parseInt(row[ZERO].toString()) : null);
				activeAlarmOutageTimeWrapper.setLatitude((Double) row[ONE]);
				activeAlarmOutageTimeWrapper.setLongitude((Double) row[TWO]);
				activeAlarmOutageTimeWrapper.setNeName((String) row[THREE]);
				activeAlarmOutageTimeWrapper.setNeStatus((NEStatus) row[FOUR]);
				activeAlarmOutageTimeWrapper.setNeId((String) row[FIVE]);
				activeAlarmOutageTimeWrapper.setParentNEId((String) row[SIX]);
				list.add(activeAlarmOutageTimeWrapper);
			}
		}
		return list;
	}

}
