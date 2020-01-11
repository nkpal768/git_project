package com.inn.foresight.customercare.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.inn.foresight.customercare.utils.wrapper.BBMDetailWrapper;
import com.inn.foresight.customercare.utils.wrapper.CapacityDataWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareUserWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareWrapper;
import com.inn.foresight.customercare.utils.wrapper.HomeWorkLocationWrapper;
import com.inn.foresight.module.fm.core.wrapper.ActiveAlarmOutageTimeWrapper;
import com.inn.foresight.module.fm.core.wrapper.AlarmDataWrapper;
import com.inn.foresight.module.fm.layer.wrapper.NEHaveAlarm;
import com.inn.foresight.module.nv.customercare.wrapper.NVCustomerCareDataWrapper;
import com.inn.foresight.module.pm.wrapper.KPIRequestWrapper;
import com.inn.foresight.module.pm.wrapper.KPIResponseWrapper;
import com.inn.product.systemconfiguration.model.SystemConfiguration;

public interface ICustomerCareService {

	Map<String, String> getCoveragePerception(Double latitude, Double longitude, String kpi, Integer zoomLevel, String siteStatus, String band,
			String dimension, String startTime, String endTime, HttpServletRequest request);

	CustomerCareWrapper getPlannedBspNeNameByLatAndLong(Double latitude, Double longitude, String band, Integer zoomLevel, String siteStatus,
			String startTime, String endTime);

	List<CapacityDataWrapper> getHUCCellsByLatAndLong(Double latitude, Double longitude, String siteStatus, String band, String startTime,
			String endTime);

	NVCustomerCareDataWrapper getNvAndDeviceDetailData(String imsi, String rowKey, String deviceId);

	Map<String, String> sendNotificationAcknowledgement(String notificationId);

	List<String> getPlannedBSPByNename(String nename, String siteStatus, String band, String startTime, String endTime);

	List<NVCustomerCareDataWrapper> getLatestPushNotificationHistory(String imsi, Long noOfRecords, String deviceId);

	List<SystemConfiguration> getAllCustomerCareArea();

	Map<String, Double> getDistanceBetweenPoints(Double lat1, Double long1, String btsName);

	List<CustomerCareSiteWrapper> searchPlannedSitesByPinLocation(Double latitude, Double longitude);

	List<CustomerCareSiteWrapper> searchOnAirSitesByPinLocation(Double latitude, Double longitude);

	List<KPIResponseWrapper> getKpiDetailsForNE(KPIRequestWrapper kpiRequestWrapper);

	Map<String, String> createPushNotification(String imsi, String notificationType, String deviceOs, String deviceId);

	BBMDetailWrapper getBBMLocationByMsisdn(String msisdn);

	List<BBMDetailWrapper> getLatestBBMLocationHistory(String msisdn, String minTimeRange, String maxTimeRange);

	Map<String, String> getNVInstallationDetail(String imsi, String deviceId, String deviceOs);

	Map<String, HomeWorkLocationWrapper> getNVLiveAndHomeWorkLocationByImsi(String imsi, String notificationId, String locationType, String deviceId,
			String timeStamp, String callType);

	List<String> getGeographyDetailsByLatLong(Double latitude, Double longitude, String type);

	Map<String, String> getCoverageHoleMitigationDate(String date, String band);

	List<String> getDeviceOSDetail();

	Map<String, String> getDeviceOsDetailByImsi(String imsi);

	public Map<String, String> getDeviceByDeviceId(String deviceId);

	public Map<String, Integer> getLiveLocationCount();

	Map<String, String> sendAcknowledgementForLiveLocation(String notificationId);

	Map<String, String> updateDeviceInfoByDeviceId(String deviceId, String imsi, String msisdn);

	List<NEHaveAlarm> isSiteHaveAlarm(List<String> neIds, String startTime, String endTime);

	List<ActiveAlarmOutageTimeWrapper> getActiveAlarmOutageByLatAndLong(Double latitude, Double longitude, String siteStatus, String band,
			String startTime, String endTime, HttpServletRequest request);

	List<CustomerCareSiteWrapper> getSiteHistoryDataByViewPort(Double southWestLat, Double northEastLat, Double southWestLong, Double northEastLong,
			String startTime, String endTime);

	List<CustomerCareSiteWrapper> getPlannedSiteHistoryByViewPort(Double southWestLat, Double northEastLat, Double southWestLong,
			Double northEastLong, String startTime, String endTime);

	Map<String, String> getCustomerLocationDetail();

	Map<String, String> getCustomerCareUserList();

	Map<String, Boolean> getHighlyUtilizedCellsDetail(String startTime, String endTime, Map<String, List<String>> siteList);
	
	List<CustomerCareSiteWrapper> searchOnAirSitesByRadius(Double latitude, Double longitude);
	
	List<CustomerCareSiteWrapper> searchPlannedSitesByRadius(Double latitude, Double longitude);


	/** micro service calls for jsi */
	String getSystemConfigurationDataByName(String name);

	String getDeviceDataByModelName(String modelName);

	List<CustomerCareSiteWrapper> getOnAirSitesByLatLong(Double latitude, Double longitude);

	String getPlannedSitesByLatLong(Double latitude, Double longitude);

	Double getColorMapData(Integer colorValue);

	Boolean updateLastLocationForDeviceId(String deviceId);

	CustomerCareUserWrapper getCustomerCareUserincontext();

	NVCustomerCareDataWrapper getLatestSpeedTestDataByDeviceId(String deviceId, Double latitude, Double longitude);

	List<KPIResponseWrapper> getPMKPIDatafromHBase(KPIRequestWrapper kpiRequestWrapper);

	List<Object[]> getHighlyUtilisedCellDetailFromDB(List<String> neIdList, Boolean isHighUtilised);

	Map<String, String> initializeData(String dataFor);

	String getTicketJson();

	Map<String, Map<String, Long>> getLatestDataTime();

	Map<String, Integer> getAlarmHistoryCountForNEId(String neId, HttpServletRequest request);

	List<AlarmDataWrapper> getAlarmHistoryForNEId(String neId, HttpServletRequest request);

	List<NEHaveAlarm> isSiteHaveAlarms(List<String> neIds, HttpServletRequest request);

	String getPlannedSitesByRadius(Double latitude, Double longitude);

	List<CustomerCareSiteWrapper> getOnAirSitesByRadius(Double latitude, Double longitude);

	Map getCRDetailsForNEIds(List<String> woCategory, String columnKey, List<String> cmStatusList, List<String> neIds, Long startTime, Long endTime);



}
