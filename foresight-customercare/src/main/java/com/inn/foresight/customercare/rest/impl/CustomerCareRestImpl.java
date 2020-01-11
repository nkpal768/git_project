package com.inn.foresight.customercare.rest.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inn.core.generic.exceptions.application.RestException;
import com.inn.core.generic.rest.AbstractCXFRestService;
import com.inn.core.generic.service.IGenericService;
import com.inn.foresight.core.gallery.model.GalleryDetail;
import com.inn.foresight.core.gallery.service.IGalleryDetailService;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.customercare.rest.ICustomerCareRest;
import com.inn.foresight.customercare.service.ICCAuditService;
import com.inn.foresight.customercare.service.IChannelCodeService;
import com.inn.foresight.customercare.service.ICustomerCareService;
import com.inn.foresight.customercare.service.ISMSDetailService;
import com.inn.foresight.customercare.utils.CustomerCareConstants;
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

@Path("CustomerCare")
@Produces("application/json")
@Consumes("application/json")
@Service("CustomerCareRestImpl")
public class CustomerCareRestImpl extends AbstractCXFRestService<Integer, Object> implements ICustomerCareRest {

	private Logger logger = LogManager.getLogger(CustomerCareRestImpl.class);

	@Autowired
	private ICustomerCareService iCustomerCareService;

	@Autowired
	private IGalleryDetailService iGalleryDetailService;

	@Autowired
	private ISMSDetailService iSMSDetailService;

	@Autowired
	private IChannelCodeService iChannelCodeService;

	@Autowired
	private ICCAuditService iCCAuditService;

	public CustomerCareRestImpl() {
		super(Object.class);
	}

	@Override
	public List<Object> search(Object entity) {
		return new ArrayList<>();
	}

	@Override
	public Object findById(@NotNull Integer primaryKey) {
		return null;
	}

	@Override
	public List<Object> findAll() {
		return new ArrayList<>();
	}

	@Override
	public Object create(@Valid Object anEntity) {
		return null;
	}

	@Override
	public Object update(@Valid Object anEntity) {
		return null;
	}

	@Override
	public boolean remove(@Valid Object anEntity) {
		return false;
	}

	@Override
	public void removeById(@NotNull Integer primaryKey) {
		// Empty block
	}

	@Override
	public IGenericService<Integer, Object> getService() {
		return null;
	}

	@Override
	public SearchContext getSearchContext() {
		return null;
	}

	@GET
	@Override
	@Path("getPlannedBSPByNename/{nename}/{siteStatus}/{band}")
	public List<String> getPlannedBSPByNename(@PathParam("nename") String nename, @PathParam("siteStatus") String siteStatus,
			@PathParam("band") String band, @QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime) {
		logger.info("Going to get Planned BSP by NEname : {} and SiteStatus : {} band : {} startTime : {} and endTime : {} ", nename, siteStatus,
				band, startTime, endTime);
		return iCustomerCareService.getPlannedBSPByNename(nename, siteStatus, band, startTime, endTime);
	}

	@GET
	@Override
	@Path("getPlannedBspNeNameByLatAndLong/{latitude}/{longitude}/{band}/{siteStatus}/{zoomLevel}")
	public CustomerCareWrapper getPlannedBspNeNameByLatAndLong(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude,
			@PathParam("band") String band, @PathParam("zoomLevel") Integer zoomLevel, @PathParam("siteStatus") String siteStatus,
			@QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime) {
		logger.info("Inside Method getPlannedBspNeNameByLatAndLong ");
		return iCustomerCareService.getPlannedBspNeNameByLatAndLong(latitude, longitude, band, zoomLevel, siteStatus, startTime, endTime);
	}

	@GET
	@Override
	@Path("getCoveragePerception")
	public Map<String, String> getCoveragePerception(@QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude,
			@QueryParam("kpi") String kpi, @QueryParam("zoomLevel") Integer zoomLevel, @QueryParam("siteStatus") String siteStatus,
			@QueryParam("band") String band, @QueryParam("dimension") String dimension, @QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime, @Context HttpServletRequest request) {
		logger.info("Going to get coverage perception");
		return iCustomerCareService.getCoveragePerception(latitude, longitude, kpi, zoomLevel, siteStatus, band, dimension, startTime, endTime,
				request);
	}

	@GET
	@Override
	@Path("getActiveAlarmOutageByLatAndLong/{latitude}/{longitude}/{siteStatus}/{band}")
	public List<ActiveAlarmOutageTimeWrapper> getActiveAlarmOutageByLatAndLong(@PathParam("latitude") Double latitude,
			@PathParam("longitude") Double longitude, @PathParam("siteStatus") String siteStatus, @PathParam("band") String band,
			@QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime, @Context HttpServletRequest request) {
		logger.info("Going to get active alarm outage by latlong ");
		return iCustomerCareService.getActiveAlarmOutageByLatAndLong(latitude, longitude, siteStatus, band, startTime, endTime, request);
	}

	@GET
	@Override
	@Path("getHUCCellsByLatAndLong/{latitude}/{longitude}/{siteStatus}/{band}")
	public List<CapacityDataWrapper> getHUCCellsByLatAndLong(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude,
			@PathParam("siteStatus") String siteStatus, @PathParam("band") String band, @QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime) {
		logger.info("Going to get Huc Cells By Lat and Long ");
		return iCustomerCareService.getHUCCellsByLatAndLong(latitude, longitude, siteStatus, band, startTime, endTime);
	}

	@GET
	@Override
	@Path("createPushNotification")
	public Map<String, String> createPushNotification(@QueryParam("imsi") String imsi, @QueryParam("notificationType") String notificationType,
			@QueryParam("deviceOs") String deviceOs, @QueryParam("deviceId") String deviceId) {
		logger.info("Inside createPushNotification");
		if (deviceOs != null && notificationType != null) {
			return iCustomerCareService.createPushNotification(imsi, notificationType, deviceOs, deviceId);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@POST
	@Override
	@Path("sendNotificationAcknowledgement")
	public Map<String, String> sendNotificationAcknowledgement(String notificationId) {
		logger.info("Inside sendNotificationAcknowledgement");
		if (notificationId != null) {
			return iCustomerCareService.sendNotificationAcknowledgement(notificationId);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getNvAndDeviceDetailData")
	public NVCustomerCareDataWrapper getNvAndDeviceDetailData(@QueryParam("imsi") String imsi, @QueryParam("rowKey") String rowKey,
			@QueryParam("deviceId") String deviceId) {
		logger.info("Going to get nv and device detail for imsi : {} , rowkey : {} and device id : {} ", imsi, rowKey, deviceId);
		return iCustomerCareService.getNvAndDeviceDetailData(imsi, rowKey, deviceId);
	}

	@GET
	@Override
	@Path("getLatestPushNotificationHistory")
	public List<NVCustomerCareDataWrapper> getLatestPushNotificationHistory(@QueryParam("imsi") String imsi,
			@QueryParam("noOfRecords") Long noOfRecords, @QueryParam("deviceId") String deviceId) {
		logger.info("Inside getDeviceData");
		return iCustomerCareService.getLatestPushNotificationHistory(imsi, noOfRecords, deviceId);
	}

	@GET
	@Override
	@Path("getGeographyDetailsByLatLong")
	public List<String> getGeographyDetailsByLatLong(@QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude,
			@QueryParam("type") String type) {
		if (latitude != null && longitude != null) {
			return iCustomerCareService.getGeographyDetailsByLatLong(latitude, longitude, type);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getGalleryDetailForVisulisation")
	public List<GalleryDetail> getGallerySmileForVisualization(@QueryParam("SWLon") Double swLon, @QueryParam("SWLat") Double swLat,
			@QueryParam("NELon") Double neLon, @QueryParam("NELat") Double neLat) {
		logger.info("Going to get gellery Detail for visualization SWLat {} SWLon {} NELat {} NELon {}", swLat, swLon, neLat, neLon);
		return iGalleryDetailService.getGallerySmileForVisualization(swLon, swLat, neLon, neLat);
	}

	@GET
	@Override
	@Path("searchPlannedSitesByPinLocation")
	public List<CustomerCareSiteWrapper> searchPlannedSitesByPinLocation(@QueryParam("latitude") Double latitude,
			@QueryParam("longitude") Double longitude) {
		logger.info("Going to search Sites latitude {} longitude {} ", latitude, longitude);
		return iCustomerCareService.searchPlannedSitesByPinLocation(latitude, longitude);
	}

	@GET
	@Override
	@Path("getDistanceBetweenPoints")
	public Map<String, Double> getDistanceBetweenPoints(@QueryParam("latitude1") Double lat1, @QueryParam("longitude1") Double long1,
			@QueryParam("BtsName") String btsName) {
		logger.info("Inside getDistanceBetweenPoints");
		return iCustomerCareService.getDistanceBetweenPoints(lat1, long1, btsName);
	}

	@GET
	@Override
	@Path("getAllCustomerCareArea")
	public List<SystemConfiguration> getAllCustomerCareArea() {
		logger.info("Inside getAllCustomerCareArea");
		return iCustomerCareService.getAllCustomerCareArea();
	}

	@GET
	@Override
	@Path("searchOnAirSitesByPinLocation")
	public List<CustomerCareSiteWrapper> searchOnAirSitesByPinLocation(@QueryParam("latitude") Double latitude,
			@QueryParam("longitude") Double longitude) {
		logger.info("Going to search Sites latitude {} longitude {} ", latitude, longitude);
		return iCustomerCareService.searchOnAirSitesByPinLocation(latitude, longitude);
	}

	@POST
	@Override
	@Path("getKpiDetailsForNE")
	public List<KPIResponseWrapper> getKpiDetailsForNE(KPIRequestWrapper kpiRequestWrapper) {
		return iCustomerCareService.getKpiDetailsForNE(kpiRequestWrapper);
	}

	@GET
	@Override
	@Path("sendSmsBySmsType")
	public Map<String, String> sendSmsBySmsType(@QueryParam("msisdn") String msisdn, @QueryParam("smsType") String smsType,
			@QueryParam("imsi") String imsi, @QueryParam("deviceId") String deviceId, @QueryParam("deviceOs") String deviceOs) {
		logger.info("Going to send SMS for msisdn : {}, smsType : {}, imsi : {}, device id : {}, device OS : {}", msisdn, smsType, imsi, deviceId,
				deviceOs);
		Map<String, String> map = new HashMap<>();
		if (Utils.checkForValueInString(msisdn) && Utils.checkForValueInString(smsType) && Utils.checkForValueInString(deviceOs)) {
			if (iSMSDetailService.sendSmsBySmsType(msisdn, smsType, imsi, deviceId, deviceOs) != null) {
				map.put(CustomerCareConstants.STATUS, CustomerCareConstants.SENT);
			} else {
				map.put(CustomerCareConstants.STATUS, CustomerCareConstants.FAILED);
			}
		} else {
			map.put(CustomerCareConstants.STATUS, CustomerCareConstants.FAILED);
		}
		return map;
	}

	@GET
	@Override
	@Path("getBBMLocationByMsisdn")
	public BBMDetailWrapper getBBMLocationByMsisdn(@QueryParam("msisdn") String msisdn) {
		logger.info("Inside getBBMLocationByMsisdn");
		if (msisdn != null && !msisdn.isEmpty()) {
			return iCustomerCareService.getBBMLocationByMsisdn(msisdn);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getLatestBBMLocationHistory")
	public List<BBMDetailWrapper> getLatestBBMLocationHistory(@QueryParam("msisdn") String msisdn, @QueryParam("minTimeRange") String minTimeRange,
			@QueryParam("maxTimeRange") String maxTimeRange) {
		logger.info("Inside getLatestBBMLocationHistory");
		return iCustomerCareService.getLatestBBMLocationHistory(msisdn, minTimeRange, maxTimeRange);
	}

	@GET
	@Override
	@Path("getNVInstallationDetail")
	public Map<String, String> getNVInstallationDetail(@QueryParam("imsi") String imsi, @QueryParam("deviceId") String deviceId,
			@QueryParam("deviceOs") String deviceOs) {
		logger.info("Inside getNVInstallationDetail");
		if (Utils.checkForValueInString(deviceOs)) {
			return iCustomerCareService.getNVInstallationDetail(imsi, deviceId, deviceOs);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getNVLiveAndHomeWorkLocationByImsi")
	public Map<String, HomeWorkLocationWrapper> getNVLiveAndHomeWorkLocationByImsi(@QueryParam("imsi") String imsi,
			@QueryParam("notificationId") String notificationId, @QueryParam("locationType") String locationType,
			@QueryParam("deviceId") String deviceId, @QueryParam("timeStamp") String timeStamp, @QueryParam("callType") String callType) {
		logger.info("Going to get NVLive and HomeWork Location for Imsi {} notificationId {} locationType {} ", imsi, notificationId, locationType);
		if (imsi != null && !imsi.isEmpty() && locationType != null && !locationType.isEmpty()) {
			return iCustomerCareService.getNVLiveAndHomeWorkLocationByImsi(imsi, notificationId, locationType, deviceId, timeStamp, callType);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@POST
	@Override
	@Path("updateMessageForSMSDetail")
	public Map<String, String> updateMessageForSMSDetail(SystemConfiguration systemConfiguration) {
		logger.info("Inside updateMessageForSMSDetail");
		if (systemConfiguration != null) {
			return iSMSDetailService.updateMessageForSMSDetail(systemConfiguration);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getAllChannelCodeDetail")
	public List<ParentChannelCodeWrapper> getAllChannelCodeDetail() {
		logger.info("Going to get AllChannelCode Detail");
		return iChannelCodeService.getAllChannelCodeDetail();
	}

	@GET
	@Override
	@Path("createSMSDetails")
	public Map<String, String> createSMSDetails(@QueryParam("msisdn") String msisdn, @QueryParam("type") String type) {
		logger.info("Going to create SmsDetail for msisdn {} type {} ", msisdn, type);
		return iSMSDetailService.createSMSDetails(msisdn, type);
	}

	@POST
	@Override
	@Path("isSiteHaveAlarm")
	public List<NEHaveAlarm> isSiteHaveAlarm(List<String> neIds, @QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime) {
		logger.info("Going to get outage alarm for  startTime : {} and endTime : {}", startTime, endTime);
		if (neIds != null && !neIds.isEmpty()) {
			logger.info("neid list size : {}", neIds.size());
			return iCustomerCareService.isSiteHaveAlarm(neIds, startTime, endTime);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getCoverageHoleMitigationDate/{date}")
	public Map<String, String> getCoverageHoleMitigationDate(@PathParam("date") String date, @QueryParam("band") String band) {
		logger.info("Going to get coverage hole mitigation date for current date : {} and band : {}", date, band);
		if (date != null && !date.isEmpty()) {
			return iCustomerCareService.getCoverageHoleMitigationDate(date, band);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getDeviceOSDetail")
	public List<String> getDeviceOSDetail() {
		logger.info("Going to get device detail");
		return iCustomerCareService.getDeviceOSDetail();
	}

	@GET
	@Override
	@Path("getDeviceOsDetailByImsi")
	public Map<String, String> getDeviceOsDetailByImsi(@QueryParam("imsi") String imsi) {
		logger.info("Going to get device os detail by imsi : {}", imsi);
		if (Utils.checkForValueInString(imsi)) {
			return iCustomerCareService.getDeviceOsDetailByImsi(imsi);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getDeviceByDeviceId")
	public Map<String, String> getDeviceByDeviceId(@QueryParam("deviceId") String deviceId) {
		logger.info("Going to get device by device id : {}", deviceId);
		if (deviceId != null) {
			return iCustomerCareService.getDeviceByDeviceId(deviceId);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getLiveLocationCount")
	public Map<String, Integer> getLiveLocationCount() {
		logger.info("Going to get live location count");
		return iCustomerCareService.getLiveLocationCount();
	}

	@GET
	@Override
	@Path("sendAcknowledgementForLiveLocation")
	public Map<String, String> sendAcknowledgementForLiveLocation(@QueryParam("notificationId") String notificationId) {
		logger.info("Going to send acknowledgement for live location for notification id : {}", notificationId);
		if (Utils.checkForValueInString(notificationId)) {
			return iCustomerCareService.sendAcknowledgementForLiveLocation(notificationId);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@POST
	@Override
	@Path("updateDeviceInfoByDeviceId")
	public Map<String, String> updateDeviceInfoByDeviceId(@QueryParam("deviceId") String deviceId, @QueryParam("imsi") String imsi,
			@QueryParam("msisdn") String msisdn) {
		logger.info("Going to update device info by device id : {}, imsi : {} and msisdn : {}", deviceId, imsi, msisdn);
		if (Utils.checkForValueInString(deviceId) && Utils.checkForValueInString(imsi) && Utils.checkForValueInString(msisdn)) {
			return iCustomerCareService.updateDeviceInfoByDeviceId(deviceId, imsi, msisdn);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getSiteHistoryDataByViewPort/{southWestLat}/{northEastLat}/{southWestLong}/{northEastLong}/{startTime}/{endTime}")
	public List<CustomerCareSiteWrapper> getSiteHistoryDataByViewPort(@PathParam("southWestLat") Double southWestLat,
			@PathParam("northEastLat") Double northEastLat, @PathParam("southWestLong") Double southWestLong,
			@PathParam("northEastLong") Double northEastLong, @PathParam("startTime") String startTime, @PathParam("endTime") String endTime) {
		logger.info(
				"Going to get onair site history data by view port southWestLat : {}, northEastLat : {}, southWestLong : {}, northEastLong : {}, startTime : {} and endTime : {} ",
				southWestLat, northEastLat, southWestLong, northEastLong, startTime, endTime);
		if (southWestLat != null && northEastLat != null && northEastLong != null && southWestLong != null && Utils.checkForValueInString(startTime)
				&& Utils.checkForValueInString(endTime)) {
			return iCustomerCareService.getSiteHistoryDataByViewPort(southWestLat, northEastLat, southWestLong, northEastLong, startTime, endTime);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getPlannedSiteHistoryByViewPort/{southWestLat}/{northEastLat}/{southWestLong}/{northEastLong}/{startTime}/{endTime}")
	public List<CustomerCareSiteWrapper> getPlannedSiteHistoryByViewPort(@PathParam("southWestLat") Double southWestLat,
			@PathParam("northEastLat") Double northEastLat, @PathParam("southWestLong") Double southWestLong,
			@PathParam("northEastLong") Double northEastLong, @PathParam("startTime") String startTime, @PathParam("endTime") String endTime) {
		logger.info(
				"Going to get planned site history data by view port southWestLat : {}, northEastLat : {}, southWestLong : {}, northEastLong : {}, startTime : {} and endTime : {} ",
				southWestLat, northEastLat, southWestLong, northEastLong, startTime, endTime);
		if (southWestLat != null && northEastLat != null && northEastLong != null && southWestLong != null && Utils.checkForValueInString(startTime)
				&& Utils.checkForValueInString(endTime)) {
			return iCustomerCareService.getPlannedSiteHistoryByViewPort(southWestLat, northEastLat, southWestLong, northEastLong, startTime, endTime);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getCustomerLocationDetail")
	public Map<String, String> getCustomerLocationDetail() {
		return iCustomerCareService.getCustomerLocationDetail();
	}

	@GET
	@Override
	@Path("getCustomerCareUserList")
	public Map<String, String> getCustomerCareUserList() {
		return iCustomerCareService.getCustomerCareUserList();
	}

	@POST
	@Override
	@Path("getHighlyUtilizedCellsDetail")
	public Map<String, Boolean> getHighlyUtilizedCellsDetail(@QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime,
			Map<String, List<String>> siteList) {
		if (siteList != null && !siteList.isEmpty() && Utils.checkForValueInString(endTime)) {
			return iCustomerCareService.getHighlyUtilizedCellsDetail(startTime, endTime, siteList);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@POST
	@Override
	@Path("updateLastLocationForDeviceId")
	public Boolean updateLastLocationForDeviceId(@QueryParam("deviceId") String deviceId) {
		if (Utils.checkForValueInString(deviceId)) {
			return iCustomerCareService.updateLastLocationForDeviceId(deviceId);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getCustomerCareUserincontext")
	public CustomerCareUserWrapper getCustomerCareUserincontext() {
		logger.info("Inside getCustomerCareUserincontext");
		return iCustomerCareService.getCustomerCareUserincontext();
	}

	@GET
	@Override
	@Path("getLastestSpeedTestDataByDeviceId")
	public NVCustomerCareDataWrapper getLatestSpeedTestDataByDeviceId(@QueryParam("deviceId") String deviceId,
			@QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude) {
		logger.info("Going to get latest speed test data ");
		return iCustomerCareService.getLatestSpeedTestDataByDeviceId(deviceId, latitude, longitude);
	}

	@GET
	@Override
	@Path("initializeData/{data}")
	public Map<String, String> initializeData(@PathParam("data") String data) {
		logger.info("Going to initialize CC data : {}", data);
		return iCustomerCareService.initializeData(data);
	}

	@GET
	@Override
	@Path("getTicketJson")
	public String getTicketJson() {
		logger.info("Going to get ticket json");
		return iCustomerCareService.getTicketJson();
	}

	@POST
	@Override
	@Path("ccAudit")
	public String ccAudit(String searchvalue) {
		logger.info("Going to audit customercare");
		return iCCAuditService.createCCAudit(searchvalue);

	}

	@GET
	@Override
	@Path("getLatestDataTime")
	public Map<String, Map<String, Long>> getLatestDataTime() {
		logger.info("inside getLatestDataTime");
		return iCustomerCareService.getLatestDataTime();
	}

	@POST
	@Override
	@Path("isSiteHaveAlarms")
	public List<NEHaveAlarm> isSiteHaveAlarms(List<String> neIds, @Context HttpServletRequest request) {
		logger.info("Going to get outage alarm ", Utils.isValidList(neIds) ? neIds.size() : 0);
		if (Utils.isValidList(neIds)) {
			return iCustomerCareService.isSiteHaveAlarms(neIds, request);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getAlarmHistoryForNEId/{neId}")
	public List<AlarmDataWrapper> getAlarmHistoryForNEId(@PathParam("neId") String neId, @Context HttpServletRequest request) {
		logger.info("Going to get alarm history for neId: {} ", neId);
		if (Utils.checkForValueInString(neId)) {
			return iCustomerCareService.getAlarmHistoryForNEId(neId, request);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("getAlarmHistoryCountForNEId/{neId}")
	public Map<String, Integer> getAlarmHistoryCountForNEId(@PathParam("neId") String neId, @Context HttpServletRequest request) {
		logger.info("Going to get alarm history count for neId: {}", neId);
		if (Utils.checkForValueInString(neId)) {
			return iCustomerCareService.getAlarmHistoryCountForNEId(neId, request);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}

	@GET
	@Override
	@Path("searchOnAirSitesByRadius")
	public List<CustomerCareSiteWrapper> searchOnAirSitesByRadius(@QueryParam("latitude") Double latitude,
			@QueryParam("longitude") Double longitude) {
		logger.info("Going to search Sites latitude {} longitude {} @searchOnAirSitesByRadius", latitude, longitude);
		return iCustomerCareService.searchOnAirSitesByRadius(latitude, longitude);
	}

	@GET
	@Override
	@Path("searchPlannedSitesByRadius")
	public List<CustomerCareSiteWrapper> searchPlannedSitesByRadius(@QueryParam("latitude") Double latitude,
			@QueryParam("longitude") Double longitude) {
		logger.info("Going to search Sites latitude {} longitude {} @searchPlannedSitesByRadius", latitude, longitude);
		return iCustomerCareService.searchPlannedSitesByRadius(latitude, longitude);
	}
}
