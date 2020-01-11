package com.inn.foresight.customercare.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;

import com.inn.foresight.core.gallery.model.GalleryDetail;
import com.inn.foresight.customercare.utils.wrapper.BBMDetailWrapper;
import com.inn.foresight.customercare.utils.wrapper.CapacityDataWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareUserWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareWrapper;
import com.inn.foresight.customercare.utils.wrapper.HomeWorkLocationWrapper;
import com.inn.foresight.customercare.utils.wrapper.ParentChannelCodeWrapper;
import com.inn.foresight.module.fm.core.wrapper.ActiveAlarmOutageTimeWrapper;
import com.inn.foresight.module.fm.core.wrapper.AlarmDataWrapper;
import com.inn.foresight.module.fm.layer.wrapper.NEHaveAlarm;
import com.inn.foresight.module.nv.customercare.wrapper.NVCustomerCareDataWrapper;
import com.inn.foresight.module.pm.wrapper.KPIRequestWrapper;
import com.inn.foresight.module.pm.wrapper.KPIResponseWrapper;
import com.inn.product.systemconfiguration.model.SystemConfiguration;

public interface ICustomerCareRest {

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_PLANNED_SITES_view')")
	List<String> getPlannedBSPByNename(String nename, String siteStatus, String band, String startTime, String endTime);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_PLANNED_SITES_view')")
	CustomerCareWrapper getPlannedBspNeNameByLatAndLong(Double latitude, Double longitude, String band, Integer zoomLevel, String siteStatus,
			String startTime, String endTime);

	@PreAuthorize("hasRole('ROLE_CC_LOCATION_NETWORK_PERFORMANCE_view')")
	Map<String, String> getCoveragePerception(Double latitude, Double longitude, String kpi, Integer zoomLevel, String siteStatus, String band,
			String dimension, String startTime, String endTime, HttpServletRequest request);

	@PreAuthorize("hasRole('ROLE_CC_LOCATION_NETWORK_PERFORMANCE_view')")
	List<ActiveAlarmOutageTimeWrapper> getActiveAlarmOutageByLatAndLong(Double latitude, Double longitude, String siteStatus, String band,
			String startTime, String endTime, HttpServletRequest request);

	@PreAuthorize("hasRole('ROLE_CC_LOCATION_NETWORK_PERFORMANCE_view')")
	List<CapacityDataWrapper> getHUCCellsByLatAndLong(Double latitude, Double longitude, String siteStatus, String band, String startTime,
			String endTime);

	@PreAuthorize("hasRole('ROLE_CC_PUSH_NOTIFICATION_create')")
	Map<String, String> createPushNotification(String imsi, String notificationType, String deviceOs, String deviceId);

	@PreAuthorize("hasRole('ROLE_CC_SPEED_TEST_SECTION_view')")
	Map<String, String> sendNotificationAcknowledgement(String notificationId);

	@PreAuthorize("hasRole('ROLE_CC_SPEED_TEST_SECTION_view')")
	NVCustomerCareDataWrapper getNvAndDeviceDetailData(String imsi, String rowKey, String deviceId);

	@PreAuthorize("hasRole('ROLE_CC_SPEED_TEST_SECTION_view')")
	List<NVCustomerCareDataWrapper> getLatestPushNotificationHistory(String imsi, Long noOfRecords, String deviceId);

	@PreAuthorize("hasRole('ROLE_CC_SUMMARY_SECTION_view')")
	List<String> getGeographyDetailsByLatLong(Double latitude, Double longitude, String type);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_DETAIL_LOCATION_view')")
	List<GalleryDetail> getGallerySmileForVisualization(Double swLon, Double swLat, Double neLon, Double neLat);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_PLANNED_SITES_view')")
	List<CustomerCareSiteWrapper> searchPlannedSitesByPinLocation(Double latitude, Double longitude);

	@PreAuthorize("hasRole('ROLE_CC_LAST_LOCATION_view')")
	Map<String, Double> getDistanceBetweenPoints(Double lat1, Double long1, String btsName);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_ON_AIR_SITES_view')")
	List<CustomerCareSiteWrapper> searchOnAirSitesByPinLocation(Double latitude, Double longitude);

	@PreAuthorize("hasRole('ROLE_CC_TROUBLE_TICKET_SECTION_view')")
	List<SystemConfiguration> getAllCustomerCareArea();

	@PreAuthorize("hasRole('ROLE_CC_PERFORMANCE_MANAGEMENT_CHART_view')")
	List<KPIResponseWrapper> getKpiDetailsForNE(KPIRequestWrapper kpiRequestWrapper);

	@PreAuthorize("hasRole('ROLE_CC_SEND_SMS_BUTTON_view')")
	Map<String, String> sendSmsBySmsType(String msisdn, String smsType, String imsi, String deviceId, String deviceOs);

	@PreAuthorize("hasRole('ROLE_CC_BBM_USER_View')")
	BBMDetailWrapper getBBMLocationByMsisdn(String msisdn);

	@PreAuthorize("hasRole('ROLE_CC_BBM_USER_View')")
	List<BBMDetailWrapper> getLatestBBMLocationHistory(String msisdn, String minTimeRange, String maxTimeRange);

	@PreAuthorize("hasRole('ROLE_CC_SPEED_TEST_SECTION_view')")
	Map<String, String> getNVInstallationDetail(String imsi, String deviceId, String deviceOs);

	@PreAuthorize("hasRole('ROLE_CC_HOME_WORK_LOCATION_view') and hasRole('ROLE_CC_LAST_LOCATION_view')")
	Map<String, HomeWorkLocationWrapper> getNVLiveAndHomeWorkLocationByImsi(String imsi, String notificationId, String locationType, String deviceId,
			String timeStamp, String callType);

	@PreAuthorize("hasRole('ROLE_CC_SMS_MESSAGE_update')")
	Map<String, String> updateMessageForSMSDetail(SystemConfiguration systemConfiguration);

	@PreAuthorize("hasRole('ROLE_CC_TROUBLE_TICKET_SECTION_view')")
	List<ParentChannelCodeWrapper> getAllChannelCodeDetail();

	@PreAuthorize("hasRole('ROLE_CC_SMS_DETAILS_create')")
	Map<String, String> createSMSDetails(String msisdn, String type);

	@PreAuthorize("hasRole('ROLE_CC_ALARM_VISUALIZATION_view')")
	List<NEHaveAlarm> isSiteHaveAlarm(List<String> neIds, String startTime, String endTime);

	@PreAuthorize("hasRole('ROLE_CC_COVERAGE_HOLE_LAYER_view')")
	Map<String, String> getCoverageHoleMitigationDate(String date, String band);

	@PreAuthorize("hasRole('ROLE_CC_CUSTOMERCARE_DEVICE_OS_view')")
	List<String> getDeviceOSDetail();

	@PreAuthorize("hasRole('ROLE_CC_CUSTOMERCARE_DEVICE_OS_view')")
	Map<String, String> getDeviceOsDetailByImsi(String imsi);

	@PreAuthorize("hasRole('ROLE_CC_CUSTOMERCARE_DEVICE_OS_view')")
	Map<String, String> getDeviceByDeviceId(String deviceId);

	@PreAuthorize("hasRole('ROLE_CC_LAST_LOCATION_view')")
	Map<String, Integer> getLiveLocationCount();

	@PreAuthorize("hasRole('ROLE_CC_LAST_LOCATION_view')")
	Map<String, String> sendAcknowledgementForLiveLocation(String notificationId);

	@PreAuthorize("hasRole('ROLE_CC_DEVICE_INFO_update')")
	Map<String, String> updateDeviceInfoByDeviceId(String deviceId, String imsi, String msisdn);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_ON_AIR_SITES_view')")
	List<CustomerCareSiteWrapper> getSiteHistoryDataByViewPort(Double southWestLat, Double northEastLat, Double southWestLong, Double northEastLong,
			String startTime, String endTime);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_PLANNED_SITES_view')")
	List<CustomerCareSiteWrapper> getPlannedSiteHistoryByViewPort(Double southWestLat, Double northEastLat, Double southWestLong,
			Double northEastLong, String startTime, String endTime);

	@PreAuthorize("hasRole('ROLE_CC_CUSTOMERCARE_USER_view')")
	Map<String, String> getCustomerLocationDetail();

	@PreAuthorize("hasRole('ROLE_CC_CUSTOMERCARE_USER_view')")
	Map<String, String> getCustomerCareUserList();

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_ON_AIR_SITES_view')")
	Map<String, Boolean> getHighlyUtilizedCellsDetail(String startTime, String endTime, Map<String, List<String>> siteList);

	@PreAuthorize("hasRole('ROLE_CC_DEVICE_LAST_LOCATION_update')")
	Boolean updateLastLocationForDeviceId(String deviceId);

	@PreAuthorize("hasRole('ROLE_CC_CUSTOMERCARE_USER_view')")
	CustomerCareUserWrapper getCustomerCareUserincontext();

	@PreAuthorize("hasRole('ROLE_CC_LAST_SPEED_TEST_SECTION_view')")
	NVCustomerCareDataWrapper getLatestSpeedTestDataByDeviceId(String deviceId, Double latitude, Double longitude);

	Map<String, String> initializeData(String data);

	@PreAuthorize("hasRole('ROLE_CC_SUMMARY_RCC_GROUP_TICKET_view') or hasRole('ROLE_CC_SUMMARY_RCC_MY_TICKET_view')")
	String getTicketJson();

	@PreAuthorize("hasRole('ROLE_CC_SEARCH_audit')")
	String ccAudit(String searchvalue);

	@PreAuthorize("hasRole('ROLE_CC_SNC_UPDATE_DATE_TIME_view')")
	Map<String, Map<String, Long>> getLatestDataTime();

	@PreAuthorize("hasRole('ROLE_CC_ALARM_VISUALIZATION_view')")
	List<NEHaveAlarm> isSiteHaveAlarms(List<String> neIds, HttpServletRequest request);

	@PreAuthorize("hasRole('ROLE_CC_ALARM_HISTORY_view')")
	List<AlarmDataWrapper> getAlarmHistoryForNEId(String neId, HttpServletRequest request);

	@PreAuthorize("hasRole('ROLE_CC_ALARM_HISTORY_view')")
	Map<String, Integer> getAlarmHistoryCountForNEId(String neId, HttpServletRequest request);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_ON_AIR_SITES_view')")
	List<CustomerCareSiteWrapper> searchOnAirSitesByRadius(Double latitude, Double longitude);

	@PreAuthorize("hasRole('ROLE_CC_GALLERY_PLANNED_SITES_view')")
	List<CustomerCareSiteWrapper> searchPlannedSitesByRadius(Double latitude, Double longitude);

}
