package com.inn.foresight.customercare.dao;

import java.util.List;
import java.util.Map;

import com.inn.core.generic.dao.IGenericDao;
import com.inn.foresight.core.infra.model.RANDetail;
import com.inn.foresight.core.infra.utils.enums.Domain;
import com.inn.foresight.core.infra.utils.enums.NEStatus;
import com.inn.foresight.core.infra.utils.enums.NEType;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.module.fm.core.wrapper.ActiveAlarmOutageTimeWrapper;

public interface ICustomerCareDao extends IGenericDao<Integer, Object>{

	List<CustomerCareSiteWrapper> getPlannedSiteForCustomerCare(List<Domain> domainList, NEStatus neStatus, List<NEType> neTypeList);

	List<Object[]> getHighlyUtilizedCellByNeidList(List<String> neidList, String startTime, String endTime);

	Object[] getSiteDataByNeId(String neid, String startTime, String endTime);

	List<Object[]> getSiteHistoryDataByViewPort(Double southWestLat, Double northEastLat, Double southWestLong, Double northEastLong, String startTime, String neStatus);

	List<Object[]> getSiteDetailByNeidList(List<String> neidList, String vendor, String startTime, String endTime);

	List<RANDetail> getRANDetailByCGI(List<Integer> cgiList);

	Map<String, Object> searchNEDetail(String neName);

	List<String> getNeIdListByBSPSiteList(List<String> bspSiteList);

	List<ActiveAlarmOutageTimeWrapper> getNEDetailByNEIdList(List<String> neIdList);

}
