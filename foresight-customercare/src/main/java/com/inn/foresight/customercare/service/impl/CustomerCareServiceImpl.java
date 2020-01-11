package com.inn.foresight.customercare.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.inn.bpmn.utils.enums.WOCategory;
import com.inn.commons.configuration.ConfigUtils;
import com.inn.commons.http.HttpException;
import com.inn.commons.io.image.ImageUtils;
import com.inn.commons.lang.StringUtils;
import com.inn.commons.maps.Corner;
import com.inn.commons.maps.LatLng;
import com.inn.commons.maps.geometry.MensurationUtils;
import com.inn.commons.maps.geometry.PointUtils;
import com.inn.commons.maps.nns.NNS;
import com.inn.commons.maps.tiles.Tile;
import com.inn.commons.maps.tiles.TileUtils;
import com.inn.commons.unit.Length;
import com.inn.core.generic.exceptions.application.DaoException;
import com.inn.core.generic.exceptions.application.RestException;
import com.inn.core.generic.service.impl.AbstractService;
import com.inn.core.generic.utils.ApplicationContextProvider;
import com.inn.foresight.core.generic.utils.ConfigUtil;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.core.infra.dao.IJobHistoryDao;
import com.inn.foresight.core.infra.dao.INEBandDetailDao;
import com.inn.foresight.core.infra.dao.IRANDetailDao;
import com.inn.foresight.core.infra.model.JobHistory;
import com.inn.foresight.core.infra.model.NEBandDetail;
import com.inn.foresight.core.infra.model.RANDetail;
import com.inn.foresight.core.infra.utils.InfraConstants;
import com.inn.foresight.core.infra.utils.enums.Domain;
import com.inn.foresight.core.infra.utils.enums.NEStatus;
import com.inn.foresight.core.infra.utils.enums.NEType;
import com.inn.foresight.core.infra.utils.enums.Vendor;
import com.inn.foresight.core.maplayer.service.IGenericMapService;
import com.inn.foresight.core.maplayer.utils.GenericMapUtils;
import com.inn.foresight.customercare.dao.ICustomerCareDao;
import com.inn.foresight.customercare.service.ICustomerCareService;
import com.inn.foresight.customercare.utils.CustomerCareConstants;
import com.inn.foresight.customercare.utils.CustomerCareEnum;
import com.inn.foresight.customercare.utils.CustomerCareUtils;
import com.inn.foresight.customercare.utils.wrapper.BBMDetailWrapper;
import com.inn.foresight.customercare.utils.wrapper.CapacityDataWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareNEWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSectorWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareUserWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareWrapper;
import com.inn.foresight.customercare.utils.wrapper.HomeWorkLocationWrapper;
import com.inn.foresight.module.cm.workorder.dao.ICMChangeRequestDao;
import com.inn.foresight.module.cm.workorder.model.CMChangeRequest;
import com.inn.foresight.module.coverage.service.ICoverageService;
import com.inn.foresight.module.fm.core.wrapper.ActiveAlarmOutageTimeWrapper;
import com.inn.foresight.module.fm.core.wrapper.AlarmDataWrapper;
import com.inn.foresight.module.fm.layer.wrapper.NEHaveAlarm;
import com.inn.foresight.module.nv.app.constants.AppConstants;
import com.inn.foresight.module.nv.app.dao.IDeviceInfoDao;
import com.inn.foresight.module.nv.app.model.DeviceInfo;
import com.inn.foresight.module.nv.app.service.IDeviceInfoService;
import com.inn.foresight.module.nv.app.utils.AppUtils;
import com.inn.foresight.module.nv.customercare.service.INVCustomerCareService;
import com.inn.foresight.module.nv.customercare.wrapper.NVCustomerCareDataWrapper;
import com.inn.foresight.module.nv.device.dao.INVDeviceDataDao;
import com.inn.foresight.module.nv.device.model.Device;
import com.inn.foresight.module.nv.device.model.NVDeviceData;
import com.inn.foresight.module.nv.pushnotification.dao.IPushNotificationDao;
import com.inn.foresight.module.nv.pushnotification.model.PushNotification;
import com.inn.foresight.module.nv.pushnotification.model.PushNotification.ModuleName;
import com.inn.foresight.module.nv.pushnotification.model.PushNotification.OS;
import com.inn.foresight.module.nv.pushnotification.model.PushNotification.Status;
import com.inn.foresight.module.nv.pushnotification.service.IPushNotificationService;
import com.inn.foresight.module.nv.pushnotification.wrapper.PushNotificationWrapper;
import com.inn.foresight.module.nv.workorder.constant.NVWorkorderConstant;
import com.inn.foresight.module.pm.capacitydetail.dao.CapacityDetailDao;
import com.inn.foresight.module.pm.service.PMDataUtilityService;
import com.inn.foresight.module.pm.utils.PMConstants;
import com.inn.foresight.module.pm.wrapper.KPIRequestWrapper;
import com.inn.foresight.module.pm.wrapper.KPIResponseWrapper;
import com.inn.product.security.spring.userdetails.CustomerInfo;
import com.inn.product.security.utils.AuthenticationCommonUtil;
import com.inn.product.systemconfiguration.dao.SystemConfigurationDao;
import com.inn.product.systemconfiguration.model.SystemConfiguration;
import com.inn.product.um.geography.model.GeographyL1;
import com.inn.product.um.geography.model.GeographyL2;
import com.inn.product.um.geography.model.GeographyL3;
import com.inn.product.um.geography.model.GeographyL4;
import com.inn.product.um.geography.model.OtherGeography;
import com.inn.product.um.geography.model.SalesL1;
import com.inn.product.um.geography.model.SalesL2;
import com.inn.product.um.geography.model.SalesL3;
import com.inn.product.um.geography.model.SalesL4;
import com.inn.product.um.role.model.Role;
import com.inn.product.um.role.model.UserRole;
import com.inn.product.um.role.model.UserRoleGeography;
import com.inn.product.um.role.utils.wrapper.UserRoleGeographyDetails;
import com.inn.product.um.user.model.User;
import com.inn.product.um.user.service.UserContextService;
import com.inn.product.um.user.utils.UmConstants;
import com.inn.product.um.user.utils.UmUtils;

@Service("CustomerCareServiceImpl")
public class CustomerCareServiceImpl extends AbstractService<Integer, Object> implements ICustomerCareService, CustomerCareConstants {

	private static Logger logger = LogManager.getLogger(CustomerCareServiceImpl.class);

	private static final String JSI = "JSI";
	private static final String PM = "PM";
	private static final String BSP = "BSP";
	private static final String SITE_SYS = "SITE_SYS";
	private static final String TICKET_JSON_NOT_AVAILABLE = "Ticket Json not available";
	private static final String SNCMAP = "SNCMap";
	private static final String PMMAP = "PMMap";
	private static final String PCMAP = "PCMAP";

	@Autowired
	private INEBandDetailDao ineBandDetailDao;

	@Autowired
	private IDeviceInfoDao iDeviceInfoDao;

	@Autowired
	private INVDeviceDataDao iNVDeviceDataDao;

	@Autowired
	private SystemConfigurationDao iSystemConfigurationDao;

	@Autowired
	private IPushNotificationDao iPushNotificationDao;

	@Autowired
	private IRANDetailDao iRANDetailDao;

	@Autowired
	private ICustomerCareDao iCustomerCareDao;

	@Autowired
	private CapacityDetailDao capacityDetailDao;

	@Autowired
	private IJobHistoryDao iJobHistoryDao;

	@Autowired
	private UserContextService userInContext;

	@Autowired
	private PMDataUtilityService pmDataUtilityService;

	@Autowired
	private IPushNotificationService iPushNotificationService;

	@Autowired
	private CustomerInfo customerInfo;
	
	@Autowired
	private ICMChangeRequestDao cmChangeRequestDao;

	@Autowired
	public void setDao(ICustomerCareDao iCustomerCareDao) {
		super.setDao(iCustomerCareDao);
		this.iCustomerCareDao = iCustomerCareDao;
	}


	@SuppressWarnings({ "serial" })
	@Override
	public List<String> getPlannedBSPByNename(String nename, String siteStatus, String band, String startTime, String endTime) {
		logger.info("Going to get Planned BSP by NEname {} and SiteStatus {} ", nename, siteStatus);
		List<String> plannedBspList = new ArrayList<>();
		try {
			if (nename != null && Utils.checkForValueInString(getBspKeyBySiteStatusAndband(siteStatus, band))) {
				String bspDate = null;
				bspDate = getPlannedBspDate(siteStatus, band, startTime, bspDate);
				if (Utils.checkForValueInString(bspDate)) {
					String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(LSMR_BSP_PLANNED_URL)),
							ForesightConstants.FORWARD_SLASH, nename.replaceAll(ForesightConstants.SPACE, ForesightConstants.URL_SPACE_REPLACER),
							ForesightConstants.FORWARD_SLASH, bspDate, ForesightConstants.FORWARD_SLASH, siteStatus.toUpperCase(),
							ForesightConstants.QUESTIONMARK, ConfigUtils.getString(CC_BSP_DATA_BAND_KEY, ForesightConstants.BAND),
							ForesightConstants.EQUALS, band);
					logger.info("URI for getting planned bsp by nename : {}", url);
					String response = CustomerCareUtils.makeGETRequest(url);
					plannedBspList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<String>>() {
					});
				}
			}
		} catch (Exception e) {
			logger.error("Error in getting Planned Bsp data By neName from the dropwizard Exception {}", Utils.getStackTrace(e));
		}
		logger.info("plannedBspList size : {}", plannedBspList.size());
		return plannedBspList;
	}

	private String getPlannedBspDate(String siteStatus, String band, String startTime, String bspDate) {
		try {
			if (Utils.checkForValueInString(startTime)) {
				logger.info("Going to get planned bsp for giving start time : {}", getDateByTimeStamp(startTime));
				ICoverageService iCoverageService = ApplicationContextProvider.getApplicationContext().getBean(ICoverageService.class);
				String bspKPI = ConfigUtils.getString(CC_KPI_FOR_BSP_DATE);
				String bspPrefix = ConfigUtils.getString(CC_PREFIX_FOR_BSP_DATE);
				bspKPI = Utils.checkForValueInString(bspKPI) ? bspKPI : BSP_KPI;
				bspPrefix = Utils.checkForValueInString(bspPrefix) ? bspPrefix : PL_PREFIX;

				bspDate = iCoverageService
						.getNearestLayerDate(bspKPI, band, bspPrefix, Utils.parseDateToString(getDateByTimeStamp(startTime), DATE_FORMAT_DDMMYY))
						.get(ForesightConstants.DATE);
				bspDate = Utils.parseDateToString(Utils.parseStringToDate(bspDate, ForesightConstants.DATE_FORMAT_yyyy_MM_dd), DATE_FORMAT_DDMMYY);
			} else {
				bspDate = convertDateByKey(getBspKeyBySiteStatusAndband(siteStatus, band));
			}
		} catch (ParseException parseException) {
			logger.error("Error in getting planned bsp date : {}", parseException.getMessage());
		}
		logger.info("found planned bsp date : {}", bspDate);
		return bspDate;
	}

	@Override
	public CustomerCareWrapper getPlannedBspNeNameByLatAndLong(Double latitude, Double longitude, String band, Integer zoomLevel, String siteStatus,
			String startTime, String endTime) {
		CustomerCareWrapper customerCareWrapper = null;
		logger.info("Going to get Planned Nename by Lat {} Long {} band {} zoomLevel {} siteStatus {} ", latitude, longitude, band, zoomLevel,
				siteStatus);
		try {
			String url = null;
			String bspDate = null;
			bspDate = getPlannedBspDate(siteStatus, band, startTime, bspDate);
			if (Utils.checkForValueInString(bspDate)) {
				url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtil.getConfigProp(LSMR_BSP_PLANNED_NE_NAME_URL)),
						ForesightConstants.FORWARD_SLASH, latitude, ForesightConstants.FORWARD_SLASH, longitude, ForesightConstants.FORWARD_SLASH,
						band, ForesightConstants.FORWARD_SLASH, bspDate, ForesightConstants.FORWARD_SLASH, siteStatus,
						ForesightConstants.FORWARD_SLASH, zoomLevel);
			}
			logger.info("URI for getting planned bsp nename by lat long : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			customerCareWrapper = populatePlannedBSPData(customerCareWrapper, response);
		} catch (Exception e) {
			logger.error("Error in getting Planned Bsp data By neName from the dropwizard Exception {}", Utils.getStackTrace(e));
		}
		return customerCareWrapper;
	}

	private CustomerCareWrapper populatePlannedBSPData(CustomerCareWrapper customerCareWrapper, String response) {
		if (response != null) {
			logger.info("Response Success {} ", response);
			List<String> neNameList = Arrays.asList(response);
			if (Utils.isValidList(neNameList)) {
				List<NEBandDetail> neBandDetails = ineBandDetailDao.getNEBandDetails(null, neNameList, null, null, null, null, null, null, null);
				if (Utils.isValidList(neBandDetails)) {
					for (NEBandDetail neBandDetail : neBandDetails) {
						customerCareWrapper = new CustomerCareWrapper();
						customerCareWrapper.setLatitude(neBandDetail.getNetworkElement().getLatitude());
						customerCareWrapper.setLongitude(neBandDetail.getNetworkElement().getLongitude());
						customerCareWrapper.setNeName(response);
						customerCareWrapper.setCurrentStage(neBandDetail.getCurrentStage());
					}
				} else {
					customerCareWrapper = new CustomerCareWrapper();
					customerCareWrapper.setNeName(response);
				}
			}
		}
		return customerCareWrapper;
	}

	private String convertDateByKey(String dateKey) {
		String parseDate = ForesightConstants.BLANK_STRING;
		try {
			String bspAndSncDate = getSystemConfigurationData(dateKey);
			if (Utils.checkForValueInString(bspAndSncDate)) {
				parseDate = Utils.parseDateToString(Utils.parseStringToDate(bspAndSncDate, DATE_FORMAT_YYYY_MM_DD), DATE_FORMAT_DDMMYY);
				logger.info("convertDateByKey Parse Data found : {}", parseDate);
			} else {
				throw new RestException("date can't be null");
			}
		} catch (Exception exception) {
			logger.warn("Unable to convert bsp Date Message {} ", exception.getMessage());
		}
		return parseDate;
	}

	@SuppressWarnings("serial")
	private String getSystemConfigurationData(String name) {
		String data = null;
		try {
			String url = CustomerCareUtils.appendData(
					CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_SYSTEM_CONFIGURATION_DATA_BY_NAME)),
					ForesightConstants.FORWARD_SLASH, name);
			logger.info("url for getting system configuration data: {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				logger.info("system configuration response : {}", response);
				data = CustomerCareUtils.parseGsonData(response, new TypeToken<String>() {
				});
			}
		} catch (HttpException e) {
			logger.error("Error in getting system configuration data from ms : {}", Utils.getStackTrace(e));
		}
		return data;
	}

	private String getBspKeyBySiteStatusAndband(String siteStatus, String band) {
		String bspKey = ForesightConstants.BLANK_STRING;
		if (Utils.checkForValueInString(siteStatus) && Utils.checkForValueInString(band)) {
			if (siteStatus.trim().equalsIgnoreCase(ONAIR)) {
				String onairStartKey = ConfigUtils.getString(CC_ONAIR_BSP_KEY);
				bspKey = (Utils.checkForValueInString(onairStartKey) ? onairStartKey : OABSP) + band;
			}
			if (siteStatus.trim().equalsIgnoreCase(PLANNED)) {
				String plannedStartKey = ConfigUtils.getString(CC_PLANNED_BSP_KEY);
				bspKey = (Utils.checkForValueInString(plannedStartKey) ? plannedStartKey : PLBSP) + band;
			}
		}
		return bspKey;
	}

	@Override
	public Map<String, String> getCoveragePerception(Double latitude, Double longitude, String kpi, Integer zoomLevel, String siteStatus, String band,
			String dimension, String startTime, String endTime, HttpServletRequest request) {
		logger.info(
				"Inside getCoveragePerception with lat : {} , long : {} , kpi : {}, zoomlevel : {} , siteStatus : {}, band : {}, dimension : {}, starttime : {}, endTime : {}",
				latitude, longitude, kpi, zoomLevel, siteStatus, band, dimension, startTime, endTime);
		Map<String, String> resultMap = new HashMap<>();
		List<String> neidList = new ArrayList<>();
		try {
			String bspDate = null;
			String sncRSRPKey = ConfigUtils.getString(CC_SNC_RSRP_KEY);
			sncRSRPKey = Utils.checkForValueInString(sncRSRPKey) ? sncRSRPKey : SNCRSRP;

			resultMap = getAverageRsrpValue(ConfigUtils.getString(SMARTLAYER_TABLE_NAME), latitude, longitude,
					ConfigUtils.getString(SMARTLAYER_COLUMN_FAMILY), kpi, convertDateByKey(sncRSRPKey + band), zoomLevel, siteStatus, band);
			if (Utils.checkForValueInString(getBspKeyBySiteStatusAndband(siteStatus, band))) {
				bspDate = getPlannedBspDate(siteStatus, band, startTime, bspDate);
				if (Utils.checkForValueInString(bspDate)) {
					neidList = getOnAirBSPSites(latitude, longitude, siteStatus, band, bspDate);
					logger.info("neidList ============== data {} ", neidList);
				}
			}
			getHUCAndOutageCountByNeidList(startTime, endTime, resultMap, neidList, request);
		} catch (Exception exception) {
			logger.error("Unable to get Coverage perception Exception {} ", Utils.getStackTrace(exception));
		}
		return resultMap;
	}

	private void getHUCAndOutageCountByNeidList(String startTime, String endTime, Map<String, String> resultMap, List<String> neidList,
			HttpServletRequest request) {
		if (neidList != null && !neidList.isEmpty()) {
			if (Utils.isValidMap(resultMap)) {
				getHUCByNeIdList(neidList, resultMap, startTime, endTime);
				getOutageByNeIdList(neidList, resultMap, request);
			} else {
				resultMap.put(OUTAGE_STATUS, NO_OUTAGE);
				resultMap.put(OUTAGE_VALUE, NO_OUTAGE_VALUE);
				resultMap.put(HIGHLY_UTILIZED, NO_CONGESTION);
				resultMap.put(SITE_STATUS_IN_NOVELVOX, ZERO_OUT_OF_ZERO_CELL_SERVING_LOCATION_HAVE_OUTAGES);
				resultMap.put(SITE_CONGESTION_STATUS, NO_CONGESTION_IN_THIS_AREA);
				resultMap.put(INDDOOR_OUTDOOR_RESULT, NOVELVOX_NO_COVERAGE);
			}
		}
	}

	private void getOutageByNeIdList(List<String> neidList, Map<String, String> resultMap, HttpServletRequest request) {
		logger.info("Going to get Outage Site Count ");
		try {
			String url = ConfigUtils.getString(CC_OUTAGE_ALARM_COUNT_URL);
			MockHttpServletRequest httpServletRequest = makeMockServletRequestURL(request, url);
			logger.info("Alarm bsp site count URL : {}", Utils.getDropwizardUrlWithPrefix(httpServletRequest));

			String response = CustomerCareUtils.makePOSTRequest(Utils.getDropwizardUrlWithPrefix(httpServletRequest), getEntityForURL(neidList));
			if (response != null && !response.isEmpty()) {
				logger.info("outage sites count response {} ", response);
				populateMapForOutageCount(neidList, resultMap, response);
			}
		} catch (Exception e) {
			logger.error("unable to getOutageCountOfSites ,Error : {} ", Utils.getStackTrace(e));
		}
	}

	private MockHttpServletRequest makeMockServletRequestURL(HttpServletRequest request, String url) {
		logger.info("Going to make url {} and query string : {}", url, request.getQueryString());
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRequestURI(url);
		httpServletRequest.setQueryString(request.getQueryString());
		logger.info("Going to make alarm data URL : {} and query string : {}", url, request.getQueryString());
		return httpServletRequest;
	}

	private <T> StringEntity getEntityForURL(T dataJson) {
		return (new StringEntity(new Gson().toJson(dataJson), ContentType.APPLICATION_JSON));
	}

	private void populateMapForOutageCount(List<String> neIds, Map<String, String> resultMap, String response) {
		if (NumberUtils.isNumber(response)) {
			double outagePercentage = ((Double.parseDouble(response) / neIds.size() * 1.0) * HUNDRED);
			if (outagePercentage <= ZERO) {
				String partialOutageValue = Integer.valueOf(response) + OUT_OF + neIds.size() + CELLS_SERVING_LOCATION_HAVE_OUTAGES;
				resultMap.put(OUTAGE_STATUS, NO_OUTAGE);
				resultMap.put(OUTAGE_VALUE, partialOutageValue);
			} else if (outagePercentage >= HUNDRED) {
				resultMap.put(OUTAGE_STATUS, FULL_OUTAGE);
				resultMap.put(OUTAGE_VALUE, FULL_OUTAGE_VALUE);
			} else {
				logger.info("neIds.size() =========================== {} ", neIds.size());
				Integer totalOutageCell = Integer.valueOf(response);
				logger.info("totalOutageCell =========================== {} ", totalOutageCell);
				String partialOutageValue = totalOutageCell + OUT_OF + neIds.size() + CELLS_SERVING_LOCATION_HAVE_OUTAGES;
				resultMap.put(OUTAGE_STATUS, PARTIAL_OUTAGE);
				resultMap.put(OUTAGE_VALUE, partialOutageValue);
			}
		}
	}

	private static List<String> getOnAirBSPSites(Double latitude, Double longitude, String siteStatus, String band, String date) {
		List<String> bspNeIdList = null;
		ICustomerCareDao careDao = ApplicationContextProvider.getApplicationContext().getBean(ICustomerCareDao.class);
		
		bspNeIdList = careDao.getNeIdListByBSPSiteList(getOnAirBspSiteByLatAndLong(latitude, longitude, siteStatus, band, date));
//		bspNeIdList= careDao.getNeIdListByBSPSiteList(ConfigUtils.getStringList("NENAME_CELL_LIST"));
		
		logger.info("================== Found ONAIR BSP Site List : {}", bspNeIdList);
		return bspNeIdList;
	}

	@SuppressWarnings({ "serial" })
	private static List<String> getOnAirBspSiteByLatAndLong(Double latitude, Double longitude, String siteStatus, String band, String date) {
		List<String> bspSiteIdList = new ArrayList<>();
		try {
			LatLng latLng = new LatLng();
			latLng.setLatitude(latitude);
			latLng.setLongitude(longitude);

			Double distance = ConfigUtils.getDouble(CC_NOVELVOX_DISTANCE_RANGE);
			distance = (Utils.isValidDouble(distance) ? distance : ForesightConstants.NOVELVOX_DISTANCE_RANGE);
			logger.info("BSP Distance range for finding sites in a square of : {} meter", distance);
			Corner corner = PointUtils.getViewPortAroundPoint(latLng, distance);
			logger.info("Found Corner for BSP : {}", corner);

			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtil.getConfigProp(COVERAGE_PERCEPTION_URL)),
					ForesightConstants.FORWARD_SLASH, corner.getSouthWestLat(), ForesightConstants.FORWARD_SLASH, corner.getSouthWestLon(),
					ForesightConstants.FORWARD_SLASH, corner.getNorthEastLat(), ForesightConstants.FORWARD_SLASH, corner.getNorthEastLon(),
					ForesightConstants.FORWARD_SLASH, siteStatus, ForesightConstants.FORWARD_SLASH, band, ForesightConstants.FORWARD_SLASH, date);
			logger.info("URI for getting coverage perception sites data by lat long : {} ", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (response != null) {
				logger.info("Response Success {} ", bspSiteIdList);
				bspSiteIdList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<String>>() {
				});
			}
		} catch (Exception exception) {
			logger.warn("Unable to get Planned Bsp Site by latitude {} and longitude {} Exception {} ", latitude, longitude,
					Utils.getStackTrace(exception));
		}
		return bspSiteIdList;
	}

	@Override
	public List<ActiveAlarmOutageTimeWrapper> getActiveAlarmOutageByLatAndLong(Double latitude, Double longitude, String siteStatus, String band,
			String startTime, String endTime, HttpServletRequest request) {
		logger.info("Going to get Active Alarm Outage By latitude {}, longitude {}, startTime : {} and endTime : {}  ", latitude, longitude,
				startTime, endTime);
		List<ActiveAlarmOutageTimeWrapper> neDetailByNEIdList = new ArrayList<>();
		try {
			String bspDate = null;
			bspDate = getPlannedBspDate(siteStatus, band, startTime, bspDate);
			if (bspDate != null) {
				List<String> neidList = getOnAirBspSiteByLatAndLong(latitude, longitude, siteStatus, band, bspDate);

//				List<String> neidList = ConfigUtils.getStringList("NENAME_CELL_LIST");

				
				if (neidList != null && !neidList.isEmpty()) {
					logger.info("neid list for outage : {}", neidList.size());
					neDetailByNEIdList = iCustomerCareDao.getNEDetailByNEIdList(neidList);
					logger.info("neDetailByNEIdList : {}", neDetailByNEIdList);

					if (Utils.isValidList(neDetailByNEIdList)) {
						neidList = neDetailByNEIdList.stream().map(ActiveAlarmOutageTimeWrapper::getNeId).collect(Collectors.toList());
						logger.info("BSP Site data from DB neid : {}", neidList);
						Map<String, ActiveAlarmOutageTimeWrapper> map = convertNEDetailListToMap(neDetailByNEIdList);

						List<ActiveAlarmOutageTimeWrapper> activeAlarmList = getActiveAlarmData(neidList, request);
						logger.info(" activeAlarmList : {}", activeAlarmList);

						if (Utils.isValidList(activeAlarmList)) {
							populateActiveAlarmDetailMap(map, activeAlarmList);
							neDetailByNEIdList = convertNEDetailMapToList(neDetailByNEIdList, map);
						}
						logger.info("finalList {}", neDetailByNEIdList);
					}
				}
			}
		} catch (Exception exception) {
			logger.error("Error while fetching Active Alarm by latitude {} longitude {} Exception {} ", latitude, longitude,
					Utils.getStackTrace(exception));
		}
		return neDetailByNEIdList;
	}

	private List<ActiveAlarmOutageTimeWrapper> convertNEDetailMapToList(List<ActiveAlarmOutageTimeWrapper> neDetailByNEIdList,
			Map<String, ActiveAlarmOutageTimeWrapper> map) {
		neDetailByNEIdList.clear();
		neDetailByNEIdList = new ArrayList<>(map.values());
		return neDetailByNEIdList;
	}

	private void populateActiveAlarmDetailMap(Map<String, ActiveAlarmOutageTimeWrapper> map, List<ActiveAlarmOutageTimeWrapper> activeAlarmList) {
		for (ActiveAlarmOutageTimeWrapper activeAlarmWrapper : activeAlarmList) {
			ActiveAlarmOutageTimeWrapper wrapperFromMap = map.get(activeAlarmWrapper.getNeId());
			wrapperFromMap.setIsHaveOutage(activeAlarmWrapper.getIsHaveOutage());
			map.put(activeAlarmWrapper.getNeId(), wrapperFromMap);
		}
	}

	@SuppressWarnings("serial")
	private List<ActiveAlarmOutageTimeWrapper> getActiveAlarmData(List<String> neidList, HttpServletRequest request) throws HttpException {
		String url = ConfigUtils.getString(CC_OUTAGE_ALARM_LIST_URL);

		MockHttpServletRequest httpServletRequest = makeMockServletRequestURL(request, url);
		logger.info("Alarm bsp site list URL : {}", Utils.getDropwizardUrlWithPrefix(httpServletRequest));

		String response = CustomerCareUtils.makePOSTRequest(Utils.getDropwizardUrlWithPrefix(httpServletRequest), getEntityForURL(neidList));
		if (Utils.checkForValueInString(response)) {
			logger.info("getActiveAlarmData response found ");
			return CustomerCareUtils.parseGsonData(response, new TypeToken<List<ActiveAlarmOutageTimeWrapper>>() {
			});
		} else {
			return null;
		}
	}

	private Map<String, ActiveAlarmOutageTimeWrapper> convertNEDetailListToMap(List<ActiveAlarmOutageTimeWrapper> neDetailByNEIdList) {
		Map<String, ActiveAlarmOutageTimeWrapper> map = neDetailByNEIdList.stream()
				.collect(Collectors.toMap(ActiveAlarmOutageTimeWrapper::getNeId, activeAlarmOutageTimeWrapper -> activeAlarmOutageTimeWrapper));
		logger.info("map {}", map);
		return map;
	}

	@Override
	public List<CapacityDataWrapper> getHUCCellsByLatAndLong(Double latitude, Double longitude, String siteStatus, String band, String startTime,
			String endTime) {
		logger.info("Going to get highly utilized cell by latitude {}, longitude {}, startTime :{} and endTime : {} ", latitude, longitude, startTime,
				endTime);
		List<CapacityDataWrapper> capacityDetailWrappers = new ArrayList<>();
		try {
			String bspDate = null;
			List<String> neIdList = new ArrayList<>();
			bspDate = getHUCCellsBspDate(siteStatus, band, startTime);
			if (Utils.checkForValueInString(bspDate)) {
				logger.info("found HUC Cells bsp date : {}", bspDate);
				neIdList = getOnAirBSPSites(latitude, longitude, siteStatus, band, bspDate);
				logger.info("getHUCCellsByLatAndLong ============== data {} ", neIdList);
			}
			capacityDetailWrappers = getHUCCellDetailForNeid(startTime, endTime, capacityDetailWrappers, neIdList);
		} catch (Exception exception) {
			logger.error("Unable to fetch Highly utilized cell from Capacity Detail Exception {} ", Utils.getStackTrace(exception));
		}
		logger.info("LAST capacityDetailWrappers {}", capacityDetailWrappers.size());
		return capacityDetailWrappers;
	}

	private String getHUCCellsBspDate(String siteStatus, String band, String startTime) {
		String bspDate;
		if (Utils.checkForValueInString(startTime)) {
			bspDate = Utils.parseDateToString(getDateByTimeStamp(startTime), DATE_FORMAT_DDMMYY);
			logger.info("Going to get HUC cells bsp date for startime : {}", bspDate);
		} else {
			bspDate = convertDateByKey(getBspKeyBySiteStatusAndband(siteStatus, band));
		}
		return bspDate;
	}

	private List<CapacityDataWrapper> getHUCCellDetailForNeid(String startTime, String endTime, List<CapacityDataWrapper> capacityDetailWrappers,
			List<String> neIdList) {
		if (neIdList != null && !neIdList.isEmpty()) {
			if (Utils.checkForValueInString(startTime) && Utils.checkForValueInString(endTime)) {

				// remove code here for history call
				capacityDetailWrappers = getHUCDetailByTime(neIdList, startTime, endTime, ZERO, capacityDetailWrappers);
			} else {
				getHUCCellsByNeIdList(capacityDetailWrappers, neIdList);
			}
		} else {
			throw new RestException("Neid List can not " + neIdList);
		}
		return capacityDetailWrappers;
	}

	private void getHUCCellsByNeIdList(List<CapacityDataWrapper> capacityDetailWrappers, List<String> neIdList) {
		List<Object[]> list = getHighlyUtilisedCellDetailFromDB(neIdList, null);
		if (Utils.isValidList(list)) {
			for (Object[] Object : list) {
				capacityDetailWrappers.add(new CapacityDataWrapper(
						Object[ZERO] != null && (boolean) Object[ZERO] == ForesightConstants.TRUE ? ForesightConstants.TRUE
								: ForesightConstants.FALSE,
						Object[ONE] != null ? Object[ONE].toString() : null, Object[TWO] != null ? Double.parseDouble(Object[TWO].toString()) : null,
						Object[THREE] != null ? Double.parseDouble(Object[THREE].toString()) : null,
						Object[FOUR] != null ? Integer.parseInt(Object[FOUR].toString()) : null,
						Object[FIVE] != null ? Integer.parseInt(Object[FIVE].toString()) : null,
						Object[SIX] != null ? Object[SIX].toString() : null));
			}
		}
	}

	@Override
	public Map<String, String> createPushNotification(String imsi, String notificationType, String deviceOs, String deviceId) {
		logger.info("Going to create push notification by imsi {} , notification type {} , device os : {} and device id : {} ", imsi,
				notificationType, deviceOs, deviceId);
		Map<String, String> pushNotificationMap = new HashMap<>();
		PushNotificationWrapper pushNotificationWrapper;
		try {
			pushNotificationWrapper = setPushNotificationWrapperByNotificationType(imsi, new PushNotificationWrapper(), notificationType,
					ConfigUtils.getBoolean(NV_LIVE_LOCATION), deviceOs, deviceId);
			if (CustomerCareUtils.checkNullObject(pushNotificationWrapper)) {
				PushNotificationWrapper pNotificationWrapper = iPushNotificationService.createNotificationForCustomercare(pushNotificationWrapper);
				setResponseOfPushNotification(imsi, notificationType, deviceOs, deviceId, pushNotificationMap, pushNotificationWrapper,
						iPushNotificationService, pNotificationWrapper);
			}
		} catch (Exception e) {
			setErrorMessage(pushNotificationMap);
			logger.error("Error in creating push notification : {}", Utils.getStackTrace(e));
		}
		logger.info("Respone of create push notification map : {}", pushNotificationMap);
		return pushNotificationMap;
	}

	private void setResponseOfPushNotification(String imsi, String notificationType, String deviceOs, String deviceId,
			Map<String, String> pushNotificationMap, PushNotificationWrapper pushNotificationWrapper,
			IPushNotificationService iPushNotificationService, PushNotificationWrapper pNotificationWrapper) {
		String notificationId = null;
		if (pNotificationWrapper != null && pNotificationWrapper.getPushnotificationId() != null && pNotificationWrapper.getStatus() != null
				&& pNotificationWrapper.getStatus().equals(Status.SENT)) {
			notificationId = String.valueOf(pNotificationWrapper.getPushnotificationId());
			pushNotificationMap.put(PUSHNOTIFICATION_KEY, notificationId);
			pushNotificationMap.put(PUSHNOTIFICATION_TYPE, NV_NOTIFICATION_TYPE);
			logger.info("Pushnotification id for nv : {}", notificationId);
		} else if (ConfigUtils.getBoolean(MYSF_LIVE_LOCATION) && !deviceOs.equalsIgnoreCase(DEVICE_OS_IOS)) {
			logger.info("Pushnotification Id for mysf : {}", pushNotificationWrapper.getPushnotificationId());
			pushNotificationWrapper.setPushnotificationId(null);
			sendNotificationForMysf(imsi, notificationType, pushNotificationMap, pushNotificationWrapper, iPushNotificationService, deviceOs,
					deviceId);
		} else {
			setErrorMessage(pushNotificationMap);
			logger.error("Notification failed");
		}
	}

	private void setErrorMessage(Map<String, String> pushNotificationMap) {
		pushNotificationMap.put(ERR_MESSAGE, PUSHNOTIFICATION_FAILED);
	}

	private void sendNotificationForMysf(String imsi, String notificationType, Map<String, String> pushNotificationMap,
			PushNotificationWrapper pushNotificationWrapper, IPushNotificationService iPushNotificationService, String deviceOs, String deviceId) {
		logger.info("Going to sent notification for mysf : {}", ConfigUtils.getBoolean(MYSF_LIVE_LOCATION));
		try {
			String notificationId = null;
			PushNotificationWrapper pNotificationWrapper;
			setPushNotificationWrapperByNotificationType(imsi, pushNotificationWrapper, notificationType, ConfigUtils.getBoolean(MYSF_LIVE_LOCATION),
					deviceOs, deviceId);
			pNotificationWrapper = iPushNotificationService.createNotificationForCustomercare(pushNotificationWrapper);
			if (pNotificationWrapper != null && pNotificationWrapper.getPushnotificationId() != null && pNotificationWrapper.getStatus() != null
					&& pNotificationWrapper.getStatus().equals(Status.SENT)) {
				notificationId = String.valueOf(pNotificationWrapper.getPushnotificationId());
				pushNotificationMap.put(PUSHNOTIFICATION_KEY, notificationId);
				pushNotificationMap.put(PUSHNOTIFICATION_TYPE, MYSF_NOTIFICATION_TYPE);
				logger.info("Pushnotification id for mysf : {}", notificationId);
			} else {
				setErrorMessage(pushNotificationMap);
				logger.error("Notification failed");
			}
		} catch (Exception e) {
			setErrorMessage(pushNotificationMap);
			logger.error("Error in sending notification for mysf: {}", Utils.getStackTrace(e));
		}
	}

	private PushNotificationWrapper setPushNotificationWrapperByNotificationType(String imsi, PushNotificationWrapper pushNotificationWrapper,
			String notificationType, Boolean liveLocatioinKey, String deviceOs, String deviceId) {
		logger.info("Inside setPushNotificationWrapperByNotificationType and live location key {} and deviceOs {} device id", liveLocatioinKey,
				deviceOs, deviceId);
		try {
			User user = userInContext.getUserInContextnew();
			pushNotificationWrapper.setCreatorId(user.getUserid());
			pushNotificationWrapper.setModuleName(ModuleName.NV_CUSTOMER_CARE);
			setImsiOrDeviceIdInNotification(imsi, pushNotificationWrapper, deviceId);
			Map<String, String> payLoad = new HashMap<>();
			payLoad.put(NVWorkorderConstant.KEY_MODULE_NAME, ModuleName.NV_CUSTOMER_CARE.name());
			setPushNotificationType(pushNotificationWrapper, notificationType, liveLocatioinKey, deviceOs, payLoad);
			pushNotificationWrapper.setNotificationPayLoad(new Gson().toJson(payLoad));
		} catch (Exception e) {
			logger.error("Error in setting push notification wrapper by notification type : {}", Utils.getStackTrace(e));
		}
		logger.info("Notification  Status : {}  Device Id : {} : Remark :  {}", pushNotificationWrapper.getStatus(),
				pushNotificationWrapper.getDeviceId(), pushNotificationWrapper.getRemark());
		return pushNotificationWrapper;
	}

	private void setPushNotificationType(PushNotificationWrapper pushNotificationWrapper, String notificationType, Boolean liveLocatioinKey,
			String deviceOs, Map<String, String> payLoad) {
		logger.info("Notification type is : {}", notificationType);
		if (notificationType.equals(CustomerCareEnum.QUICKTEST.getValue())) {
			pushNotificationWrapper.setMobileOsType(checkDeviceOs(deviceOs));
			payLoad.put(NVWorkorderConstant.KEY_NOTIFICATION_MESSAGE, CustomerCareEnum.QUICKTEST.getValue());
			pushNotificationWrapper.setIsToUseClientFCMKey(ConfigUtils.getBoolean(IS_TO_USE_CLIENT_FCM_KEY_FOR_QUICK_TEST));
		} else if (notificationType.equals(CustomerCareEnum.LIVE_LOCATION.getValue())) {
			pushNotificationWrapper.setMobileOsType(checkDeviceOs(deviceOs));
			payLoad.put(NVWorkorderConstant.KEY_NOTIFICATION_MESSAGE, CustomerCareEnum.LIVE_LOCATION.getValue());
			pushNotificationWrapper.setIsToUseClientFCMKey(liveLocatioinKey);
		}
	}

	private void setImsiOrDeviceIdInNotification(String imsi, PushNotificationWrapper pushNotificationWrapper, String deviceId) {
		if (Utils.checkForValueInString(imsi)) {
			logger.info("Create notificaton for imsi : {}", imsi);
			pushNotificationWrapper.setImsi(imsi);
		}
		if (Utils.checkForValueInString(deviceId)) {
			logger.info("Create notificaton for device id  : {}", deviceId);
			pushNotificationWrapper.setDeviceId(deviceId);
		}
	}

	private OS checkDeviceOs(String deviceOs) {
		logger.info("Notification device OS : {}", deviceOs);
		if (Utils.checkForValueInString(deviceOs)) {
			if (deviceOs.equalsIgnoreCase(OS.ANDROID.name())) {
				return OS.ANDROID;
			} else if (deviceOs.equalsIgnoreCase(OS.IOS.name())) {
				return OS.IOS;
			}
		}
		return null;
	}

	@Override
	public Map<String, String> sendNotificationAcknowledgement(String notificationId) {
		logger.info("Going to send acknowledgement of notification id : {}", notificationId);
		Map<String, String> map = new HashMap<>();
		String rowKey = null;
		String errMessage = null;
		String status = null;
		try {
			if (Utils.checkForValueInString(notificationId)) {
				PushNotification pushNotificaton = null;
				pushNotificaton = iPushNotificationDao.findByPk(Integer.parseInt(notificationId));
				if (pushNotificaton != null && pushNotificaton.isRecieved() != null && pushNotificaton.isRecieved()) {
					status = ACCEPTED;
					if (Utils.checkForValueInString(pushNotificaton.getRemark())) {
						rowKey = pushNotificaton.getRemark();
					} else {
						errMessage = ROW_KEY_NOT_FOUND;
					}
				} else {
					errMessage = PUSHNOTIFICATION_NOT_ACCEPTED;
				}
			}
		} catch (Exception e) {
			logger.error("Error in sending notification acknowledgement : {}", Utils.getStackTrace(e));
		}
		map.put(STATUS, status);
		map.put(ERR_MESSAGE, errMessage);
		map.put(ROW_KEY, rowKey);
		return map;
	}

	@SuppressWarnings("serial")
	private Map<String, HomeWorkLocationWrapper> getHomeAndWorkLocationByImsi(String imsi, String timeStamp, String callType) {
		logger.info("Going to fetch Home and Work location data for {} ", imsi);
		Map<String, HomeWorkLocationWrapper> locationMap = new TreeMap<>();
		try {
			if (imsi != null && Utils.checkForValueInString(timeStamp)) {
				Integer weekNumberOfTimeStamp = null;
				weekNumberOfTimeStamp = getWeekNoByTimestamp(timeStamp);
				String homeWorkUrl = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(LSR_DATA_URL)),
						ForesightConstants.FORWARD_SLASH, imsi, ForesightConstants.FORWARD_SLASH, weekNumberOfTimeStamp);
				logger.info("URI for getting Home And Work Location data from the dropwizard : {}", homeWorkUrl);
				String homeWorkResponse = CustomerCareUtils.makeGETRequest(homeWorkUrl);
				if (homeWorkResponse != null) {
					locationMap = CustomerCareUtils.parseGsonData(homeWorkResponse, new TypeToken<Map<String, HomeWorkLocationWrapper>>() {
					});
					getBtsNameByEcgi(locationMap);
				} else {
					throw new RestException("No Home and Work Location found for " + imsi);
				}
			} else {
				throw new RestException("Imsi can not be " + imsi);
			}
		} catch (Exception exception) {
			logger.error("Unable to fetch Home and Work Location data by imsi {} Exception {} ", imsi, Utils.getStackTrace(exception));
		}
		return locationMap;
	}

	private Map<String, Object> getWeekNoByDate(String timeStamp) {
		logger.info("Going to get week no by date {} ", timeStamp);
		Map<String, Object> weekNumberMap = null;
		try {
			if (timeStamp != null && !timeStamp.isEmpty()) {
				long longDate = Long.parseLong(timeStamp);
				Date newDate = new Date(longDate);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(newDate);
				LocalDate localDate = LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate();
				LocalDate todayDate = LocalDate.now();

				String type = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_HOME_WORK_WEEK_NO_NAME_KEY), SYS_TYPE_JOB);
				String name = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_HOME_WORK_WEEK_NO_TYPE_KEY),
						SYS_NAME_HOME_WORK_LOCATION_JOB);

				long systemConfigurationTimeStamp = Long.parseLong(iSystemConfigurationDao.getValueByNameAndType(type, name));
				Date sysDate = new Date(systemConfigurationTimeStamp);
				Calendar sysCalendar = Calendar.getInstance();
				sysCalendar.setTime(sysDate);
				LocalDate sysLocalDate = LocalDateTime.ofInstant(sysCalendar.toInstant(), ZoneId.systemDefault()).toLocalDate();
				TemporalField weekOfYear = WeekFields.of(Locale.UK).weekOfWeekBasedYear();
				weekNumberMap = new HashMap<>();
				weekNumberMap.put(WEEK, localDate.get(weekOfYear));
				weekNumberMap.put(DAY, localDate.getDayOfWeek().toString());
				weekNumberMap.put(CURRENT_WEEK, todayDate.get(weekOfYear));
				weekNumberMap.put(CURRENT_WEEKS_DAY, todayDate.getDayOfWeek().toString());
				weekNumberMap.put(HOME_WORK_WEEK_NUMBER, sysLocalDate.get(weekOfYear));
				logger.info("week no is {} and day is {}", localDate.get(weekOfYear), localDate.getDayOfWeek());
			}
		} catch (Exception exception) {
			logger.info("Error in converting weekDay by TimeStamp {} ", exception);
		}
		logger.info("weekNumberMap : {}", weekNumberMap);
		return weekNumberMap;
	}

	private Integer getWeekNoByTimestamp(String timeStamp) {
		logger.info("Going to week no by time stamp");
		Integer weekNumberOfTimeStamp;
		Map<String, Object> weekDayMap = null;
		Integer latestDataAvailable = ForesightConstants.ZERO;
		weekDayMap = getWeekNoByDate(timeStamp);
		if (weekDayMap != null && !weekDayMap.isEmpty()) {
			weekNumberOfTimeStamp = (Integer) weekDayMap.get(WEEK);
			Integer currentWeekNumber = (Integer) weekDayMap.get(CURRENT_WEEK);
			Integer weekNumberDatabasedate = (Integer) weekDayMap.get(HOME_WORK_WEEK_NUMBER);
			latestDataAvailable = weekNumberDatabasedate - ForesightConstants.TWO;
			if (latestDataAvailable < ForesightConstants.ZERO) {
				latestDataAvailable = FIFTY_ONE_INTEGER;
			} else if (latestDataAvailable == ForesightConstants.ZERO) {
				latestDataAvailable = FIFTY_TWO_INTEGER;
			}
			if (currentWeekNumber == weekNumberDatabasedate - ForesightConstants.ONE || currentWeekNumber.equals(weekNumberDatabasedate)) {
				latestDataAvailable = getLatestWeekDate(weekNumberOfTimeStamp, currentWeekNumber, latestDataAvailable);
			}
			logger.info("found week no to fetch data : {} ", weekNumberOfTimeStamp);
		}
		return latestDataAvailable;
	}

	private Integer getLatestWeekDate(Integer weekNumberOfTimeStamp, Integer currentWeekNumber, Integer latestDataAvailable) {
		if (weekNumberOfTimeStamp.equals(currentWeekNumber)) {
			latestDataAvailable += ForesightConstants.ZERO;
		} else if (weekNumberOfTimeStamp < currentWeekNumber) {
			latestDataAvailable -= ForesightConstants.ONE;
		} else if (weekNumberOfTimeStamp == FIFTY_TWO_INTEGER) {
			latestDataAvailable -= ForesightConstants.ONE;
		}
		return latestDataAvailable;
	}

	private void getBtsNameByEcgi(Map<String, HomeWorkLocationWrapper> locationMap) {
		if (locationMap != null && !locationMap.isEmpty() && locationMap.keySet() != null && !locationMap.keySet().isEmpty()) {
			for (Map.Entry<String, HomeWorkLocationWrapper> entry : locationMap.entrySet()) {
				if (entry.getKey() != null) {
					HomeWorkLocationWrapper wrapper = locationMap.get(entry.getKey());
					if (wrapper != null && wrapper.getCellId() != null && !wrapper.getCellId().isEmpty()) {
						setBtsNameAndPciByEcgi(wrapper);
					}
				}
			}
		}
	}

	private void setBtsNameAndPciByEcgi(HomeWorkLocationWrapper wrapper) {
		List<Object[]> list = iRANDetailDao.getBtsNameByEcgi(wrapper.getCellId());
		try {
			if (list != null && !list.isEmpty()) {
				wrapper.setBtsName(list.get(ZERO)[ZERO] != null ? list.get(ZERO)[ZERO].toString() : null);
				wrapper.setPci(list.get(ZERO)[ONE] != null ? list.get(ZERO)[ONE].toString() : null);
				wrapper.setNeFrequency(list.get(ZERO)[TWO] != null ? list.get(ZERO)[TWO].toString() : null);
			}
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
			logger.error("Error in getting data by cell id  : {}", Utils.getStackTrace(arrayIndexOutOfBoundsException));
		}
	}

	@Override
	public NVCustomerCareDataWrapper getNvAndDeviceDetailData(String imsi, String rowKey, String deviceId) {
		NVCustomerCareDataWrapper customerDetail = new NVCustomerCareDataWrapper();
		logger.info("Going to get nv and device detail for imsi : {} , rowKey : {} and device id : {}", imsi, rowKey, deviceId);
		try {
			String type = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_NV_DATA_TYPE), NV_TYPE);
			if (Utils.checkForValueInString(imsi) || Utils.checkForValueInString(deviceId)) {
				customerDetail = getNVActiveDeviceDataByImsiOrDeviceId(type, imsi, deviceId);
				customerDetail = updateDeviceData(customerDetail);
			}
			if (Utils.checkForValueInString(rowKey)) {
				customerDetail = getNVActivePassiveDeviceDataByRowKey(type, rowKey);
				customerDetail = updateDeviceData(customerDetail);
			}
		} catch (Exception e) {
			logger.error("Error in getting NV and Device data : {}", Utils.getStackTrace(e));
		}
		return customerDetail;
	}

	private NVCustomerCareDataWrapper updateDeviceData(NVCustomerCareDataWrapper customerDetail) {
		logger.info("Inside updateDeviceData");
		try {
			if (customerDetail != null && customerDetail.getModel() != null && customerDetail.getMake() != null) {
				String modelName = customerDetail.getModel().trim().toLowerCase() + ForesightConstants.UNDERSCORE
						+ customerDetail.getMake().trim().toLowerCase();
				Device device = getDeviceDataFromHbase(modelName);
				if (device != null) {
					customerDetail = updateMatchedDeviceData(customerDetail, device);
					setDeviceDataIntoWrapper(customerDetail, device);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating device data : {}", Utils.getStackTrace(e));
		}
		return customerDetail;
	}

	@SuppressWarnings("serial")
	private Device getDeviceDataFromHbase(String modelName) {
		Device device = null;
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_DEVICE_DATA_BY_MODEL_NAME)),
					ForesightConstants.QUESTIONMARK, DEVICE_MODEL_NAME, ForesightConstants.EQUALS,
					modelName.replace(ForesightConstants.SPACE, ForesightConstants.BLANK_STRING));
			logger.info("url for getting device data : {}", url);
			String deviceData = CustomerCareUtils.makeGETRequest(url);
			logger.info("device data response : {}", deviceData);
			if (Utils.checkForValueInString(deviceData)) {
				device = CustomerCareUtils.parseGsonData(deviceData, new TypeToken<Device>() {
				});
			}
		} catch (HttpException e) {
			logger.error("Error in getting device data from ms : {}", Utils.getStackTrace(e));
		}
		return device;
	}

	private NVCustomerCareDataWrapper updateMatchedDeviceData(NVCustomerCareDataWrapper customerDetail, Device device) {
		logger.info("Inside updateMatchedDeviceData");
		try {
			if (customerDetail.getModel() == null && device.getModelName() != null) {
				customerDetail.setModel(device.getModelName());
			}
			if (customerDetail.getDeviceOS() == null && device.getOs() != null) {
				customerDetail.setDeviceOS(device.getOs());
			}
			if (customerDetail.getDualSimEnabled().equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)) {
				customerDetail.setDualSimEnabled(STRING_YES);
			}
			if (customerDetail.getDualSimEnabled().equalsIgnoreCase(ForesightConstants.FALSE_LOWERCASE)) {
				customerDetail.setDualSimEnabled(STRING_NO);
			}
			if (customerDetail.getDualSimEnabled() == null && device.getSim() != null) {
				if (device.getSim().trim().toUpperCase().contains(DUAL_SIM.trim().toUpperCase())) {
					customerDetail.setDualSimEnabled(STRING_YES);
				} else {
					customerDetail.setDualSimEnabled(STRING_NO);
				}
			}
			if (customerDetail.getChipSet() == null && device.getChipset() != null) {
				customerDetail.setChipSet(device.getChipset());
			}
		} catch (Exception e) {
			logger.error("Error in updating matched data from device : {}", Utils.getStackTrace(e));
		}
		return customerDetail;
	}

	private NVCustomerCareDataWrapper setDeviceDataIntoWrapper(NVCustomerCareDataWrapper customerDetail, Device device) {
		logger.info("Inside setDeviceDataIntoWrapper");
		try {
			if (device.getBand4g() != null) {
				customerDetail.setBand4G(device.getBand4g());
			}
			if (device.getImageUrl() != null) {
				customerDetail.setImageUrl(device.getImageUrl() != null ? device.getImageUrl() : null);
			}
			if (device.getFirmwareVersion() != null) {
				customerDetail.setDeviceFirmwareVersion(YES);
				customerDetail.setFirmwareVersion(device.getFirmwareVersion());
			} else {
				customerDetail.setDeviceFirmwareVersion(NO);
			}
			if (device.getBand4g() != null) {
				if (device.getBand4g().contains(ForesightConstants.STRING_2300) || device.getBand4g().contains(ForesightConstants.STRING_1800)
						|| device.getBand4g().contains(ForesightConstants.STRING_850)) {
					customerDetail.setLteCompatible(YES);
				} else {
					customerDetail.setLteCompatible(NO);
				}
			}
			if (device.getVolteSupport() != null) {
				if (device.getVolteSupport().equalsIgnoreCase(ForesightConstants.TRUE_CAPS)) {
					customerDetail.setDeviceCompatiable(VOLTE);
				} else {
					customerDetail.setDeviceCompatiable(device.getTechnology());
				}
			}
		} catch (Exception e) {
			logger.error("Error in setting device data into wrapper : {}", Utils.getStackTrace(e));
		}
		return customerDetail;
	}

	private NVCustomerCareDataWrapper getNVActiveDeviceDataByImsiOrDeviceId(String type, String imsi, String deviceId) {
		logger.info("Going to get nv active data for imsi : {} or device id : {}", imsi, deviceId);
		NVCustomerCareDataWrapper wrapper = new NVCustomerCareDataWrapper();
		try {
			deviceId = getLatestDeviceIdByImsi(imsi, deviceId);
			INVCustomerCareService iNVCustomerCareService = ApplicationContextProvider.getApplicationContext().getBean(INVCustomerCareService.class);
			String nvData = iNVCustomerCareService.getNVLatestData(type, deviceId, null, null, null, null, null, 1l, null);
			wrapper = parseNVData(wrapper, nvData);
		} catch (Exception e) {
			logger.error("Error in getting NV Data from Hbase by imsi  : {}", Utils.getStackTrace(e));
		}
		return wrapper;
	}

	private String getLatestDeviceIdByImsi(String imsi, String deviceId) {
		logger.info("Going to get latest device id by imsi : {} and device id : {}", imsi, deviceId);
		if (Utils.checkForValueInString(imsi)) {
			DeviceInfo deviceInfo = iDeviceInfoDao.getDeviceInfoByImsi(imsi);
			if (deviceInfo != null && deviceInfo.getDeviceId() != null) {
				deviceId = deviceInfo.getDeviceId();
			}
		}
		return deviceId;
	}

	private NVCustomerCareDataWrapper getNVActivePassiveDeviceDataByRowKey(String type, String rowKey) {
		logger.info("Going to  get NV active data by rowkey : {}", rowKey);
		NVCustomerCareDataWrapper wrapper = new NVCustomerCareDataWrapper();
		try {
			INVCustomerCareService iNVCustomerCareService = ApplicationContextProvider.getApplicationContext().getBean(INVCustomerCareService.class);
			String nvData = iNVCustomerCareService.getNVLatestData(type, null, null, null, null, null, null, 1l, rowKey);
			wrapper = parseNVData(wrapper, nvData);
			/**
			 * Code for updating last location after performing speed test if
			 * (validateNVWrapperData(wrapper)) {
			 * updateNVLastLocationByDeviceId(wrapper.getDeviceId(), wrapper.getLatitude(),
			 * wrapper.getLongitude(), wrapper.getCapturedOn(), wrapper.getCgi()); }
			 */
		} catch (Exception e) {
			logger.error("Error in getting NV Data from Hbase by rowkey : {}", Utils.getStackTrace(e));
		}
		return wrapper;
	}

	private boolean validateNVWrapperData(NVCustomerCareDataWrapper wrapper) {
		return wrapper != null && wrapper.getDeviceId() != null && !wrapper.getDeviceId().isEmpty() && wrapper.getLatitude() != null
				&& wrapper.getLongitude() != null && wrapper.getCapturedOn() != null;
	}

	@Transactional
	public Boolean updateNVLastLocationByDeviceId(String deviceId, Double latitude, Double longitude, Long capturedTime, Integer cgi) {
		logger.info("Going to update NV last location by deviceId : {}", deviceId);
		Boolean status = FALSE;
		try {
			NVDeviceData nvDeviceData = iNVDeviceDataDao.getDeviceByDeviceId(deviceId);
			if (nvDeviceData != null) {
				Date capturedDate = new Date(capturedTime);
				if (capturedDate.compareTo(nvDeviceData.getModificationTime()) > ZERO) {
					logger.info("captured time {} is bigger then modification time {}", capturedDate, nvDeviceData.getModificationTime());
					nvDeviceData.setLatitude(latitude);
					nvDeviceData.setLongitude(longitude);
					nvDeviceData.setCgi(cgi != null ? cgi : null);
					nvDeviceData.setModificationTime(capturedDate);
					nvDeviceData = iNVDeviceDataDao.update(nvDeviceData);
					if (nvDeviceData != null && nvDeviceData.getModificationTime().compareTo(capturedDate) == ZERO) {
						status = TRUE;
						logger.info("Last location updated successfully");
					} else {
						logger.info("Last location updating failed");
					}
				} else {
					logger.info("captured time {} is smaller then modification time {}", capturedDate, nvDeviceData.getModificationTime());
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating last location from nv speed test : {}", Utils.getStackTrace(e));
		}
		return status;
	}

	private NVCustomerCareDataWrapper parseNVData(NVCustomerCareDataWrapper nvDataWrapper, String nvData) {
		logger.info("Going to parse NV data");
		try {
			if (Utils.checkForValueInString(nvData) && nvData.length() > TWO) {
				JSONArray jsonArray = new JSONArray(AuthenticationCommonUtil.checkForValueDecryption(nvData));
				JSONObject jObject = null;
				for (int i = ZERO; i < jsonArray.length(); i++) {
					jObject = jsonArray.getJSONObject(i);
				}
				ObjectMapper mapper = new ObjectMapper();
				if (jObject != null) {
					nvDataWrapper = mapper.readValue(jObject.toString(), NVCustomerCareDataWrapper.class);
					Map<Integer, RANDetail> ranDetailMap = getRANDetailMap(Arrays.asList(nvDataWrapper.getCgi()));
					updateNVWrapperData(nvDataWrapper, ranDetailMap);
				}
			}
		} catch (Exception e) {
			logger.error("Error in parsing NV data : {}", Utils.getStackTrace(e));
		}
		return nvDataWrapper;
	}

	@SuppressWarnings("unchecked")
	private NVCustomerCareDataWrapper updateNVWrapperData(NVCustomerCareDataWrapper nvDataWrapper, Map<Integer, RANDetail> ranDetailMap) {
		logger.info("Inside updateNVWrapperData");
		try {
			if (nvDataWrapper.getDualSimEnable() != null) {
				nvDataWrapper.setDualSimEnabled(nvDataWrapper.getDualSimEnable().toString());

				if (nvDataWrapper.getDualSimEnabled().equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)) {
					nvDataWrapper.setDualSimEnabled(STRING_YES);
				} else if (nvDataWrapper.getDualSimEnabled().equalsIgnoreCase(ForesightConstants.FALSE_LOWERCASE)) {
					nvDataWrapper.setDualSimEnabled(STRING_NO);
				}
			}
			if (nvDataWrapper.getAddress() != null) {
				nvDataWrapper.setAddress(nvDataWrapper.getAddress().replace(ForesightConstants.ASTERISK, ForesightConstants.COMMA));
			}

			List<String> operatorList = ConfigUtils.getStringList(CC_NV_DATA_OPERATORS);
			CustomerCareUtils.validateListData(operatorList);
			operatorList.replaceAll(String::toUpperCase);

			if (Utils.isValidList(operatorList)) {
				if (Utils.checkForValueInString(nvDataWrapper.getDataSimDetail())) {
					if (operatorList.contains(nvDataWrapper.getDataSimDetail().toUpperCase())) {
						nvDataWrapper.setDataSimDetail(STRING_YES);
					} else {
						nvDataWrapper.setDataSimDetail(STRING_NO);
					}
				}
				if (Utils.checkForValueInString(nvDataWrapper.getVoiceSimDetail())) {
					if (operatorList.contains(nvDataWrapper.getVoiceSimDetail().toUpperCase())) {
						nvDataWrapper.setVoiceSimDetail(STRING_YES);
					} else {
						nvDataWrapper.setVoiceSimDetail(STRING_NO);
					}
				}
			}
			setBtsCodeAndSiteInfo(nvDataWrapper, ranDetailMap);
		} catch (Exception e) {
			logger.error("Error in updating NV data from wrapper : {}", Utils.getStackTrace(e));
		}
		return nvDataWrapper;
	}

	private void setBtsCodeAndSiteInfo(NVCustomerCareDataWrapper nvDataWrapper, Map<Integer, RANDetail> ranDetailMap) {
		if (ranDetailMap != null && nvDataWrapper != null && nvDataWrapper.getCgi() != null && ranDetailMap.containsKey(nvDataWrapper.getCgi())) {
			nvDataWrapper.setBtsCode(ranDetailMap.get(nvDataWrapper.getCgi()).getNetworkElement().getNeName());
			nvDataWrapper.setNeStatus(ranDetailMap.get(nvDataWrapper.getCgi()).getNetworkElement().getNeStatus().toString());
		}
	}

	private void setBtsCodeAndSiteInfoBycgi(NVCustomerCareDataWrapper nvDataWrapper) {
		if (nvDataWrapper != null && nvDataWrapper.getCgi() != null) {
			List<Object[]> data = getBtsDetailByCgi(nvDataWrapper.getCgi().toString());
			if (data != null && !data.isEmpty()) {
				logger.info("BTS DATA : {}", data.size());
				nvDataWrapper.setBtsCode(data.get(ZERO)[ZERO].toString());
				/**
				 * set site address of site
				 * nvDataWrapper.setSiteAddress(data.get(ZERO)[ONE].toString());
				 */
				nvDataWrapper.setNeStatus(data.get(ZERO)[TWO].toString());
			}
		}
	}

	@Override
	public List<NVCustomerCareDataWrapper> getLatestPushNotificationHistory(String imsi, Long noOfRecords, String deviceId) {
		logger.info("Inside getLatestPushNotificationHistory for imsi : {}, device id : {} and noofrecords : {}", imsi, deviceId, noOfRecords);
		NVCustomerCareDataWrapper wrapper = null;
		List<NVCustomerCareDataWrapper> customerDetailList = new ArrayList<>();
		logger.error("Time when hit getLatestPushNotificationHistory in millisec :{}", System.currentTimeMillis());
		try {
			deviceId = getLatestDeviceIdByImsi(imsi, deviceId);
			INVCustomerCareService iNVCustomerCareService = ApplicationContextProvider.getApplicationContext().getBean(INVCustomerCareService.class);
			logger.error("Time when hit going to MS for getLatestPushNotificationHistory in millisec :{}", System.currentTimeMillis());

			String type = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_NV_DATA_TYPE), NV_TYPE);
			String nvData = iNVCustomerCareService.getNVLatestData(type, deviceId, null, null, null, null, null, noOfRecords, null);
			logger.error("Time when got response from MS :{}", System.currentTimeMillis());
			customerDetailList = parseNVDataList(wrapper, nvData);
			if (Utils.isValidList(customerDetailList)) {
				logger.error("Time when returning response from APPS :{}", System.currentTimeMillis());
				return customerDetailList;
			}
		} catch (Exception e) {
			logger.error("Error in getting NV Data up to 10 records : {}", Utils.getStackTrace(e));
		}
		return customerDetailList;
	}

	@SuppressWarnings("serial")
	private List<NVCustomerCareDataWrapper> parseNVDataList(NVCustomerCareDataWrapper wrapper, String nvData) {
		List<NVCustomerCareDataWrapper> nvDataList = new ArrayList<>();
		try {
			if (Utils.checkForValueInString(nvData) && nvData.length() > TWO) {
				Type type = new TypeToken<List<NVCustomerCareDataWrapper>>() {
				}.getType();
				nvDataList = new Gson().fromJson(AuthenticationCommonUtil.checkForValueDecryption(nvData), type);
				List<Integer> cgiList = nvDataList.stream().filter(v -> v.getCgi() != null).map(NVCustomerCareDataWrapper::getCgi)
						.collect(Collectors.toList());
				Map<Integer, RANDetail> ranDetailMap = getRANDetailMap(cgiList);
				for (int i = ZERO; i < nvDataList.size(); i++) {
					updateNVWrapperData(nvDataList.get(i), ranDetailMap);
				}
			}
		} catch (Exception e) {
			logger.error("Error in parsing NV data into list : {}", Utils.getStackTrace(e));
		}
		return nvDataList;
	}

	private Map<Integer, RANDetail> getRANDetailMap(List<Integer> cgiList) {
		Map<Integer, RANDetail> ranDetailMap = new HashMap<>();
		List<RANDetail> list = iCustomerCareDao.getRANDetailByCGI(cgiList);
		if (Utils.isValidList(list)) {
			list.forEach(v -> ranDetailMap.put(v.getCgi(), v));
		}
		return ranDetailMap;
	}

	@Override
	public Map<String, Double> getDistanceBetweenPoints(Double lat1, Double long1, String btsName) {
		Map<String, Double> distanceMap = new HashMap<>();
		try {
			if (latLongValidation(lat1, long1, btsName)) {
				LatLng point1 = new LatLng();
				LatLng point2 = new LatLng();
				Map<String, Object> searchNEDetail = iCustomerCareDao.searchNEDetail(btsName);
				if (Utils.isValidMap(searchNEDetail)) {
					point1.setLatitude(lat1);
					point1.setLongitude(long1);
					point2.setLatitude((Double) searchNEDetail.get(InfraConstants.NE_LATITUDE_KEY));
					point2.setLongitude((Double) searchNEDetail.get(InfraConstants.NE_LONGITUDE_KEY));
					Double length = MensurationUtils.distanceBetweenPoints(point1, point2);
					distanceMap.put(DISTANCE, length);
					setLatitudeOfPoints(distanceMap, searchNEDetail);
					setLongitudeOfPoints(distanceMap, searchNEDetail);
				}
			} else {
				distanceMap.put(DISTANCE, 0D);
				distanceMap.put(LATITUDE, 0.0);
				distanceMap.put(LONGITUDE, 0.0);
			}
		} catch (Exception e) {
			logger.error("Exception while getDistanceBetweenPoints : {} ", Utils.getStackTrace(e));
		}
		return distanceMap;
	}

	private void setLongitudeOfPoints(Map<String, Double> distance, Map<String, Object> searchNEDetail) {
		if (Utils.isValidDouble((Double) searchNEDetail.get(InfraConstants.NE_LONGITUDE_KEY)))
			distance.put(LONGITUDE, (Double) searchNEDetail.get(InfraConstants.NE_LONGITUDE_KEY));
		else
			distance.put(LONGITUDE, null);
	}

	private void setLatitudeOfPoints(Map<String, Double> distance, Map<String, Object> searchNEDetail) {
		if (Utils.isValidDouble((Double) searchNEDetail.get(InfraConstants.NE_LATITUDE_KEY)))
			distance.put(LATITUDE, (Double) searchNEDetail.get(InfraConstants.NE_LATITUDE_KEY));
		else
			distance.put(LATITUDE, null);
	}

	private Boolean latLongValidation(Double lat1, Double long1, String btsName) {
		return (lat1 != null && long1 != null && btsName != null);
	}

	@SuppressWarnings("serial")
	@Override
	public List<CustomerCareSiteWrapper> searchPlannedSitesByPinLocation(Double latitude, Double longitude) {
		logger.info("Going to get Planned Sites By latitude {} longitude {} ", latitude, longitude);
		List<CustomerCareSiteWrapper> nearestPlannedSiteList = new ArrayList<>();
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_PLANNED_SITES_BY_LAT_LONG)),
					ForesightConstants.FORWARD_SLASH, latitude, ForesightConstants.FORWARD_SLASH, longitude);
			logger.info("search planned sites url : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				logger.info("Planned site responose : {}", response.length());
				nearestPlannedSiteList = new Gson().fromJson(response, new TypeToken<List<CustomerCareSiteWrapper>>() {
				}.getType());
			}
			logger.info("Total Planned Sites from app server {} ", nearestPlannedSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting onair sites by LatLong from ms : {} ", Utils.getStackTrace(e));
		}
		return nearestPlannedSiteList;
	}

	@Override
	public String getPlannedSitesByLatLong(Double latitude, Double longitude) {
		logger.info("Going to get OnAir and Planned Sites By latitude {} longitude {} ", latitude, longitude);
		String plannedSiteList = null;
		try {
			List<CustomerCareSiteWrapper> nearestPlannedSiteList = null;
			nearestPlannedSiteList = getNearestPlannedSitesByPinLocation(latitude, longitude, FALSE);
			if (Utils.isValidList(nearestPlannedSiteList)) {
				logger.info("Total Planned Sites from ms {} ", nearestPlannedSiteList.size());
				plannedSiteList = new Gson().toJson(nearestPlannedSiteList);
			}
		} catch (Exception e) {
			logger.error("Error while getting planned sites by LatLong : {} ", Utils.getStackTrace(e));
		}
		return plannedSiteList;
	}

	@SuppressWarnings("serial")
	@Override
	public List<CustomerCareSiteWrapper> searchOnAirSitesByPinLocation(Double latitude, Double longitude) {
		logger.info("Going to get OnAir Sites By latitude {} longitude {} ", latitude, longitude);
		List<CustomerCareSiteWrapper> nearestSiteList = new ArrayList<>();
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_ON_AIR_SITES_BY_LAT_LONG)),
					ForesightConstants.FORWARD_SLASH, latitude, ForesightConstants.FORWARD_SLASH, longitude);
			logger.info("search on air sites url : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				nearestSiteList = new Gson().fromJson(response, new TypeToken<List<CustomerCareSiteWrapper>>() {
				}.getType());
			}
			logger.info("Total Onair Sites from app server {} ", nearestSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting onair sites by LatLong from ms: {} ", Utils.getStackTrace(e));
		}
		return nearestSiteList;
	}

	@Override
	public List<CustomerCareSiteWrapper> getOnAirSitesByLatLong(Double latitude, Double longitude) {
		logger.info("Going to get OnAir and Planned Sites By latitude {} longitude {} from hbase ", latitude, longitude);
		List<CustomerCareSiteWrapper> nearestSiteList = new ArrayList<>();
		try {
			nearestSiteList = getNearestOnAirSitesByPinLocation(latitude, longitude, FALSE);
			logger.info("Total Onair Sites from ms {} ", nearestSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting onair or planned sites by LatLong : {} ", Utils.getStackTrace(e));

		}
		return nearestSiteList;
	}

	private List<CustomerCareSiteWrapper> getNearestOnAirSitesByPinLocation(Double latitude, Double longitude, boolean isRadiusCall) {
		List<CustomerCareSiteWrapper> customerCareSiteWrappers = new ArrayList<>();
		logger.info("Going to get Nearest Onair Sites By latitude {} and Longitude {} ", latitude, longitude);
		try {
			List<CustomerCareNEWrapper> onAirNearestSites = getNearesOnAirSites(latitude, longitude, CustomerCareUtils.getOnairsiteList(),
					isRadiusCall);

			if (Utils.isValidList(onAirNearestSites)) {
				List<CustomerCareSiteWrapper> careSiteWrappers = new ArrayList<>();
				logger.info("Populated ONAIR Site Data to show from NNS {} ", onAirNearestSites.size());
				onAirNearestSites.forEach(customerCareNEWrapper -> {
					List<CustomerCareSectorWrapper> careSectorWrappers = customerCareNEWrapper.getCustomerCareSectorWrappers();
					careSectorWrappers.forEach(customerCareSectorWrapper -> {
						CustomerCareSiteWrapper customerCareSiteWrapper = new CustomerCareSiteWrapper(customerCareNEWrapper.getLatitude(),
								customerCareNEWrapper.getLongitude(), customerCareNEWrapper.getSapid(), customerCareNEWrapper.getSapid(),
								customerCareSectorWrapper.getNeId(), customerCareSectorWrapper.getCellId(), customerCareNEWrapper.getNeStatus(),
								customerCareSectorWrapper.getPci(), customerCareSectorWrapper.getAzimuth(),
								customerCareSectorWrapper.getNeFrequency(), customerCareNEWrapper.getDomain(), customerCareNEWrapper.getVendor(),
								customerCareSectorWrapper.getCarrier(), customerCareSectorWrapper.getSectorId(),
								customerCareSectorWrapper.getIsHighlyUtilized(), customerCareSectorWrapper.getParentneId(),
								customerCareSectorWrapper.getTechnology(), customerCareSectorWrapper.getNeType());
						careSiteWrappers.add(customerCareSiteWrapper);
					});
				});
				customerCareSiteWrappers = careSiteWrappers.stream().sorted(Comparator.comparing(CustomerCareSiteWrapper::getNeFrequency).reversed())
						.collect(Collectors.toList());

				logger.info("Populated ONAIR Cells Data : {} crossponding Sites : {}", customerCareSiteWrappers.size(), onAirNearestSites.size());
			}
		} catch (Exception e) {
			logger.error("Error while getting Nearest Onair Sites by LatLong : {}", Utils.getStackTrace(e));
		}
		return customerCareSiteWrappers;
	}

	public List<CustomerCareSiteWrapper> getNearestPlannedSitesByPinLocation(Double latitude, Double longitude, boolean isRadiusCall) {
		List<CustomerCareSiteWrapper> plannedNearestSites = new ArrayList<>();
		logger.info("Going to get Nearest Planned Sites By latitude {} and Longitude {} ", latitude, longitude);
		try {
			plannedNearestSites = getNearestPlannnedSites(latitude, longitude, CustomerCareUtils.getPlannedsiteList(), isRadiusCall);
		} catch (Exception e) {
			logger.error("Error while getting Nearest Planned Sites by LatLong : {}", Utils.getStackTrace(e));
		}
		return plannedNearestSites;
	}

	private List<CustomerCareSiteWrapper> getNearestPlannnedSites(Double latitude, Double longitude, NNS<CustomerCareSiteWrapper> plannedNearestList,
			boolean isRadiusCall) {
		List<CustomerCareSiteWrapper> plannedNearestSites = new ArrayList<>();
		if (CustomerCareUtils.checkNullObject(plannedNearestList)) {
			if (isRadiusCall) {
				plannedNearestSites = plannedNearestList.getLocationInRange(new LatLng(latitude, longitude),
						Length.meter(ConfigUtils.getInteger(NUMBER_OF_SITES_IN_METER)));
			} else {
				plannedNearestSites = plannedNearestList.getNearestLocations(new LatLng(latitude, longitude), ConfigUtils.getInteger(NUMBER_OF_JSI));
			}
		}
		return plannedNearestSites;
	}

	private List<CustomerCareNEWrapper> getNearesOnAirSites(Double latitude, Double longitude, NNS<CustomerCareNEWrapper> onAirNearestList,
			boolean isRadiusCall) {
		List<CustomerCareNEWrapper> onAirNearestSites = new ArrayList<>();
		if (CustomerCareUtils.checkNullObject(onAirNearestList)) {
			if (isRadiusCall) {
				logger.info("Finding ONAIR site visualization count By Radius : {}", ConfigUtils.getInteger(NUMBER_OF_SITES_IN_METER));
				onAirNearestSites = onAirNearestList.getLocationInRange(new LatLng(latitude, longitude),
						Length.meter(ConfigUtils.getInteger(NUMBER_OF_SITES_IN_METER)));
			} else {
				logger.info("Finding ONAIR site visualization count : {}", ConfigUtils.getInteger(NUMBER_OF_JSI));
				onAirNearestSites = onAirNearestList.getNearestLocations(new LatLng(latitude, longitude), ConfigUtils.getInteger(NUMBER_OF_JSI));
			}
			logger.info("Found ONAIR Site from NNS : {} for lat : {} and long : {} ", onAirNearestSites.size(), latitude, longitude);
		}
		return onAirNearestSites;
	}

	@Override
	public List<SystemConfiguration> getAllCustomerCareArea() {
		logger.info("Inside getAllCustomerCareArea");
		List<SystemConfiguration> list = new ArrayList<>();
		try {
			String type = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_CUSTOMER_CARE_REGION_KEY), CUSTOMER_CARE_REGION);
			list = iSystemConfigurationDao.getSystemConfigurationByType(type);
		} catch (Exception e) {
			logger.error("Exception while getAllCustomerCareArea : {} ", Utils.getStackTrace(e));
		}
		return list;
	}

	@Override
	public List<KPIResponseWrapper> getKpiDetailsForNE(KPIRequestWrapper kpiRequestWrapper) {
		logger.info("Going to get Kpi Detail for NE : {}", kpiRequestWrapper);
		List<KPIResponseWrapper> kpiResponseWrapperList = new ArrayList<>();
		try {
			if (kpiRequestWrapper != null) {
				kpiRequestWrapper = getKPIDateByVendor(kpiRequestWrapper);

				setKPIListInWrapper(kpiRequestWrapper);

				kpiResponseWrapperList = getKPIDataFromHbase(kpiRequestWrapper);

			}
		} catch (Exception exception) {
			logger.error("Unable to get Kpi Details for NE Exception {} ", Utils.getStackTrace(exception));
		}
		return kpiResponseWrapperList;
	}

	private void setKPIListInWrapper(KPIRequestWrapper kpiRequestWrapper) {
		try {
			if (!Utils.isValidList(kpiRequestWrapper.getKpiList())) {
				String kpiList = iSystemConfigurationDao.getValueByName(RAN_PERFORMANCE_KPI_LIST);
				logger.info("KPI List : {}", kpiList);
				if (Utils.checkForValueInString(kpiList)) {
					kpiRequestWrapper.setKpiList(Utils.getListFromStringSeperatedByPattern(kpiList, ForesightConstants.COMMA));
					logger.info("KPI List setted in wrapper : {}", kpiRequestWrapper.getKpiList());
				}
			}
		} catch (Exception e) {
			logger.error("Error in settingg kpi list in wrapper Exception : {}", Utils.getStackTrace(e));
		}
	}

	private KPIRequestWrapper getKPIDateByVendor(KPIRequestWrapper kpiRequestWrapper) {
		logger.info("Going to get kpi date by vendor : {} and frequncy : {}", kpiRequestWrapper.getVendor(), kpiRequestWrapper.getFrequency());

		if (kpiRequestWrapper.getFrequency() != null && kpiRequestWrapper.getFrequency().equalsIgnoreCase(PMConstants.FREQUENCY_BBH)) {
			logger.info("BBH Date Data : {}", getBBHDate(kpiRequestWrapper.getVendor()));

			setTimeForAllFrequency(getBBHDate(kpiRequestWrapper.getVendor()),
					CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_PM_KPI_BBH_DATE_FORMAT), KPI_BBH_DATA_DATE_FORMAT),
					kpiRequestWrapper);

		} else if (kpiRequestWrapper.getFrequency() != null && kpiRequestWrapper.getFrequency().equalsIgnoreCase(PMConstants.FREQUENCY_PERHOUR)) {
			logger.info("Hourly Date Data : {}", getHourlyDate(kpiRequestWrapper.getVendor()));
			setTimeForHourlyData(getHourlyDate(kpiRequestWrapper.getVendor()), kpiRequestWrapper);

		} else {
			logger.info("Going to get all kpi date by vendor ");
			if (Utils.checkForValueInString(kpiRequestWrapper.getFrequency())) {
				setTimeForAllFrequency(getALLPMFrequencyDate(kpiRequestWrapper.getVendor()),
						CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_PM_KPI_ALL_DATE_FORMAT), KPI_ALL_DATA_DATE_FORMAT),
						kpiRequestWrapper);
			}
		}
		return kpiRequestWrapper;
	}

	@SuppressWarnings("serial")
	private List<KPIResponseWrapper> getKPIDataFromHbase(KPIRequestWrapper kpiRequestWrapper) throws HttpException {
		List<KPIResponseWrapper> kpiResponseWrappers = new ArrayList<>();
		if (kpiRequestWrapper != null) {
			logger.info("Going to get kpi data from hbase : {}", kpiRequestWrapper.getNeIdList().size());
			String url = CustomerCareUtils.appendBaseURL(ConfigUtil.getConfigProp(GET_KPI_DATA_URL));
			logger.info("Url for Performance Kpi Detail {} {} ", url, getEntityForURL(kpiRequestWrapper));
			String response = CustomerCareUtils.makePOSTRequest(url, getEntityForURL(kpiRequestWrapper));
			logger.info("Response of Performance is {} ", response.length());
			if (Utils.checkForValueInString(response)) {
				kpiResponseWrappers = new Gson().fromJson(response, new TypeToken<List<KPIResponseWrapper>>() {
				}.getType());
			}
		}
		return kpiResponseWrappers;
	}

	@Transactional
	@Override
	public List<KPIResponseWrapper> getPMKPIDatafromHBase(KPIRequestWrapper kpiRequestWrapper) {
		logger.info("Going to get kpe data ");
		List<KPIResponseWrapper> kpiResponseWrappers = new ArrayList<>();
		if (kpiRequestWrapper != null) {
			logger.info("kpiRequestWrapper wrapper : {}", kpiRequestWrapper);
			try {
				kpiResponseWrappers = pmDataUtilityService.getKpiDetailsForNE(kpiRequestWrapper.getDomain(), kpiRequestWrapper.getVendor(),
						kpiRequestWrapper.getFrequency(), kpiRequestWrapper.getNodeType(), kpiRequestWrapper.getKpiList(),
						kpiRequestWrapper.getNeIdList(), kpiRequestWrapper.getStartDate(), kpiRequestWrapper.getEndDate(),
						kpiRequestWrapper.getUtilizationKey());
				logger.info("Found PM KPI Data : {}", kpiResponseWrappers.size());
			} catch (Exception e) {
				logger.error("Error in getting kpi data from hbase Eception :  {}", Utils.getStackTrace(e));
			}
		}
		return kpiResponseWrappers;
	}

	private List<KPIResponseWrapper> getKpiDetailByUtilizationKey(List<KPIResponseWrapper> kpiResponseWrapperList,
			List<KPIResponseWrapper> kpiResponseWrappers, Map<String, Boolean> hucMap) {
		logger.info("Going to get kpi detail by utilization key ");
		if (kpiResponseWrappers != null && !kpiResponseWrappers.isEmpty()) {
			String utilizationValue = getUtilizationValueForHUC();
			if (Utils.checkForValueInString(utilizationValue)) {
				for (KPIResponseWrapper kpiResponseWrapper : kpiResponseWrappers) {

					String utilizationKPIKey = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_PM_UTILIZATION_KPI_KEY), KPI_KEY);

					if (kpiResponseWrapper.getKpiValues().get(utilizationKPIKey) != null
							&& kpiResponseWrapper.getKpiValues().get(utilizationKPIKey) > Integer.parseInt(utilizationValue)) {
						kpiResponseWrapper.setIsHighlyUtilized(TRUE);
						populateHucInMap(hucMap, kpiResponseWrapper);
						kpiResponseWrapperList.add(kpiResponseWrapper);
					} else {
						kpiResponseWrapper.setIsHighlyUtilized(FALSE);
						populateHucInMap(hucMap, kpiResponseWrapper);
						kpiResponseWrapperList.add(kpiResponseWrapper);
					}
				}
				logger.info("kpiResponseWrapperList size : {}", kpiResponseWrapperList.size());
			}
		}
		return kpiResponseWrapperList;
	}

	private void populateHucInMap(Map<String, Boolean> hucMap, KPIResponseWrapper kpiResponseWrapper) {
		if (Utils.isValidMap(hucMap)) {
			hucMap.put(kpiResponseWrapper.getNeName() + ForesightConstants.UNDERSCORE + kpiResponseWrapper.getCellId(),
					kpiResponseWrapper.getIsHighlyUtilized());
		}
	}

	private String getUtilizationValueForHUC() {
		String utilizationValue = iSystemConfigurationDao.getValueByNameAndType(CUSTOMER_CARE, PRB_UTILIZATION_KEY);
		logger.info("KPI value : {}", utilizationValue);
		return utilizationValue;
	}

	private void getHUCByNeIdList(List<String> neIdList, Map<String, String> resultMap, String startTime, String endTime) {
		try {
			if (neIdList != null && !neIdList.isEmpty()) {
				Integer hucCount = ForesightConstants.ZERO;
				if (Utils.checkForValueInString(startTime) && Utils.checkForValueInString(endTime)) {

					// remove code here for history code
					logger.info("Going to get HUC Cells Count data");
					Map<String, CapacityDataWrapper> nokiaZTEMap = new HashMap<>();
					hucCount = setKPIRequestWrapperListByNeid(neIdList, startTime, endTime, nokiaZTEMap, hucCount);
					logger.info("KPI response wrapper list count : {}", hucCount);
					populateHUCResultMapByCount(neIdList, resultMap, hucCount);
				} else {
					populateHUCResultMapByCount(neIdList, resultMap, getHighlyUtilisedCellDetailFromDB(neIdList, TRUE).size());
				}
			}
		} catch (Exception exception) {
			logger.warn("Unable to get data from Congestion Message {} ", exception.getMessage());
		}
	}

	@SuppressWarnings("serial")
	private Map<Vendor, String> getVendorKeyMapData() {
		Map<Vendor, String> data = null;
		try {
			String url = CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_VENDOR_KEY_MAP_URL));
			logger.info("url for vendor key map data: {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				data = CustomerCareUtils.parseGsonData(response, new TypeToken<Map<Vendor, String>>() {
				});
				logger.info("Vendor Key Map on App server : {}", data);
			}
		} catch (HttpException e) {
			logger.error("Error in getting vendor date map data from ms : {}", Utils.getStackTrace(e));
		}
		return data;
	}

	@SuppressWarnings("serial")
	private Map<Vendor, String> getVendorDateMapData() {
		Map<Vendor, String> data = null;
		try {
			String url = CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_VENDOR_DATE_MAP_URL));
			logger.info("url for vendor date map data: {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				data = CustomerCareUtils.parseGsonData(response, new TypeToken<Map<Vendor, String>>() {
				});
				logger.info("Vendor Date Map on App server : {}", data);
			}
		} catch (HttpException e) {
			logger.error("Error in getting vendor date map data from ms : {}", Utils.getStackTrace(e));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getHighlyUtilisedCellDetailFromDB(List<String> neIdList, Boolean isHighUtilised) {
		List<Object[]> capacityData = new ArrayList<>();
		Map<Vendor, String> vendorDateMap = getVendorDateMapData();
		if (Utils.isValidMap(vendorDateMap)) {
			logger.info("Found vendorDateMap  size : {}", vendorDateMap.size());
			List<String> cellNETypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_CELL_LIST);
			List<String> domainList = ConfigUtils.getStringList(CC_RAN_DOMAIN_LIST);
			CustomerCareUtils.validateListData(domainList, cellNETypeList);
			String neStatus = ConfigUtils.getString(CC_RAN_NESTATUS_ONAIR);

			capacityData = capacityDetailDao.getAllHUCCellsDetail(Utils.convertStringToEnumList(Domain.class, domainList), NEStatus.valueOf(neStatus),
					Utils.convertStringToEnumList(NEType.class, cellNETypeList), vendorDateMap, isHighUtilised, neIdList);
			logger.info("Highly Utilised Cell Count : {}", capacityData.size());
		}
		return capacityData;
	}

	private List<CapacityDataWrapper> getHUCDetailByTime(List<String> neIdList, String startTime, String endTime, Integer hucCount,
			List<CapacityDataWrapper> capacityDataWrappers) {
		logger.info("Going to get huc detail by time  neIdList : {}, startTime : {} , endTime :{}", neIdList, startTime, endTime);
		Map<String, CapacityDataWrapper> nokiaZTEMap = new HashMap<>();
		setKPIRequestWrapperListByNeid(neIdList, startTime, endTime, nokiaZTEMap, hucCount);
		if (Utils.isValidMap(nokiaZTEMap)) {
			capacityDataWrappers = nokiaZTEMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
			logger.info("Converted map to list : {}", capacityDataWrappers.size());
		}
		return capacityDataWrappers;
	}

	private Integer setKPIRequestWrapperListByNeid(List<String> neIdList, String startTime, String endTime,
			Map<String, CapacityDataWrapper> nokiaZTEMap, Integer hucCount) {
		logger.info("Going to set kpi request wrapper list by neid");
		try {
			List<String> nokiaNeIdList = new ArrayList<>();
			List<String> zteNeIdList = new ArrayList<>();
			String prbUtilization = getUtilizationValueForHUC();
			Double utilization = prbUtilization != null ? Double.parseDouble(prbUtilization) : null;

			// remove code here history call
			getSitesDataByVendor(neIdList, startTime, endTime, nokiaZTEMap, nokiaNeIdList, NOKIA_VENDOR);
			getSitesDataByVendor(neIdList, startTime, endTime, nokiaZTEMap, zteNeIdList, ZTE_VENDOR);
			logger.info("zte nename list : {}", zteNeIdList);
			logger.info("nokia nename list : {}", nokiaNeIdList);
			hucCount = populateKpiData(endTime, nokiaZTEMap, hucCount, zteNeIdList, utilization, ZTE_VENDOR);
			hucCount = populateKpiData(endTime, nokiaZTEMap, hucCount, nokiaNeIdList, utilization, NOKIA_VENDOR);
			logger.info("HUC Count : {}", hucCount);
			logger.info("FINAL MAP : {}", nokiaZTEMap.size());
		} catch (Exception e) {
			logger.error("Error in setting kpi request wrapper : {}", Utils.getStackTrace(e));
		}
		return hucCount;
	}

	private Integer populateKpiData(String endTime, Map<String, CapacityDataWrapper> nokiaZTEMap, Integer hucCount, List<String> neIdList,
			Double utilization, String vendor) throws HttpException {
		List<KPIResponseWrapper> responseWrappers = getKPIDataFromHbase(setKPIRequestData(neIdList, endTime, RAN_DOMAIN, vendor));
		if (responseWrappers != null && !responseWrappers.isEmpty()) {
			logger.info("KPI Reponse : {}", responseWrappers.size());
			for (KPIResponseWrapper responseWrapper : responseWrappers) {
				if (responseWrapper.getNeName() != null && responseWrapper.getCellId() != null
						&& nokiaZTEMap.get(responseWrapper.getNeName() + ForesightConstants.UNDERSCORE + responseWrapper.getCellId()) != null) {
					CapacityDataWrapper dataWrapper = nokiaZTEMap
							.get(responseWrapper.getNeName() + ForesightConstants.UNDERSCORE + responseWrapper.getCellId());

					String utilizationKPIKey = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_PM_UTILIZATION_KPI_KEY), KPI_KEY);

					if (responseWrapper.getKpiValues().get(utilizationKPIKey) != null
							&& responseWrapper.getKpiValues().get(utilizationKPIKey) > utilization) {
						dataWrapper.setIsHighlyUtilized(TRUE);
						hucCount++;
					} else {
						dataWrapper.setIsHighlyUtilized(FALSE);
					}
				}
			}
		}
		return hucCount;
	}

	private void getSitesDataByVendor(List<String> neIdList, String startTime, String endTime, Map<String, CapacityDataWrapper> nokiaZTEMap,
			List<String> vendorNeIdList, String vendor) {
		List<Object[]> vendorList = iCustomerCareDao.getSiteDetailByNeidList(neIdList, vendor,
				Utils.parseDateToString(getDateByTimeStamp(startTime), DATE_FORMAT_YYYY_MM_DD),
				Utils.parseDateToString(getDateByTimeStamp(endTime), DATE_FORMAT_YYYY_MM_DD));
		setSiteInfoByVendorList(nokiaZTEMap, vendorNeIdList, vendorList);
	}

	private void setSiteInfoByVendorList(Map<String, CapacityDataWrapper> nokiaZTEMap, List<String> vendorNeIdList, List<Object[]> vendorList) {
		if (Utils.isValidList(vendorList)) {
			logger.info("vendorList :  {}", vendorList.size());
			for (Object[] object : vendorList) {
				CapacityDataWrapper wrapper = new CapacityDataWrapper(null, object[ZERO] != null ? object[ZERO].toString() : null,
						object[ONE] != null ? Double.parseDouble(object[ONE].toString()) : null,
						object[TWO] != null ? Double.parseDouble(object[TWO].toString()) : null,
						object[THREE] != null ? Integer.parseInt(object[THREE].toString()) : null,
						object[FOUR] != null ? Integer.parseInt(object[FOUR].toString()) : null,
						object[FIVE] != null ? object[FIVE].toString() : null);
				nokiaZTEMap.put(object[ZERO] != null ? object[ZERO].toString() : null, wrapper);
				vendorNeIdList.add(object[ZERO] != null ? object[ZERO].toString() : null);
			}
		}
	}

	private KPIRequestWrapper setKPIRequestData(List<String> neIdList, String endTime, String domain, String vendor) {
		logger.info("Going to set kpi data ");
		KPIRequestWrapper kpiRequestWrapper = null;
		try {
			if (neIdList != null && !neIdList.isEmpty()) {
				kpiRequestWrapper = new KPIRequestWrapper();
				kpiRequestWrapper.setDomain(domain);
				kpiRequestWrapper.setVendor(vendor);
				kpiRequestWrapper.setNeIdList(neIdList);
				kpiRequestWrapper.setNodeType(PMConstants.CELL_C);
				kpiRequestWrapper.setFrequency(PMConstants.FREQUENCY_PERHOUR);
				kpiRequestWrapper.setStartDate(Long.parseLong(endTime));
				kpiRequestWrapper.setEndDate(Long.parseLong(endTime));

				String utilizationKPIKey = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_PM_UTILIZATION_KPI_KEY), KPI_KEY);
				kpiRequestWrapper.setKpiList(Arrays.asList(utilizationKPIKey));

				getKPIDateByVendor(kpiRequestWrapper);
				logger.info("KPI Request Data : {}", kpiRequestWrapper);
			}
		} catch (Exception e) {
			logger.error("Error in setting kpi request data  : {}", Utils.getStackTrace(e));
		}
		return kpiRequestWrapper;
	}

	private void populateHUCResultMapByCount(List<String> neIdList, Map<String, String> resultMap, Integer count) {
		if (count == ForesightConstants.ZERO_INT) {
			resultMap.put(SITE_CONGESTION_STATUS, NO_CONGESTION_IN_THIS_AREA);
			resultMap.put(HIGHLY_UTILIZED, NO_CONGESTION);
		} else if (neIdList.size() > count) {
			resultMap.put(SITE_CONGESTION_STATUS, count + OUT_OF + neIdList.size() + CELLS_SERVING_LOCATION_HAVE_CONGESTION);
			resultMap.put(HIGHLY_UTILIZED, PARTIAL_CONGESTION);
		} else if (neIdList.size() == count) {
			resultMap.put(SITE_CONGESTION_STATUS, ALL_CELL_SERVING_LOCATION_HAVE_CONGESTION);
			resultMap.put(HIGHLY_UTILIZED, FULL_CONGESTION);
		}
		logger.info("Found Map : {}", resultMap);
	}

	private Map<String, HomeWorkLocationWrapper> getNVLiveLocationByImsiAndId(String imsi, String notificationId, String deviceId) {
		logger.info("Going to get NV Live Location by Imsi {}, notificationId {} and device id : {}  ", imsi, notificationId, deviceId);
		Map<String, HomeWorkLocationWrapper> map = new HashMap<>();
		try {

			if (Utils.checkForValueInString(imsi) || Utils.checkForValueInString(deviceId)) {
				logger.info("Going to get last location by imsi : {} or device id : {}", imsi, deviceId);
				IDeviceInfoService deviceInfoService = ApplicationContextProvider.getApplicationContext().getBean(IDeviceInfoService.class);
				DeviceInfo deviceInfo = deviceInfoService.getDeviceInfoDetailByImsiOrDeviceId(imsi, deviceId);
				if (deviceInfo != null && deviceInfo.getDeviceId() != null) {
					getLatLonByDeviceIdFromNVDevice(map, deviceInfo.getDeviceId());
				}
			}
			if (Utils.checkForValueInString(notificationId)) {
				logger.info("Going to get last location by notification id : {}", notificationId);
				PushNotification pushNotification = null;
				pushNotification = iPushNotificationDao.findByPk(Integer.parseInt(notificationId));
				if (pushNotification != null && pushNotification.getDeviceInfo().getDeviceId() != null) {
					logger.info("PushNotificatio device id : {}", pushNotification.getDeviceInfo().getDeviceId());
					getLatLonByDeviceIdFromNVDevice(map, pushNotification.getDeviceInfo().getDeviceId());
				}
			}
		} catch (Exception e) {
			logger.error("Error in getting NV live location by imsi and notification id : {}", Utils.getStackTrace(e));
		}
		logger.info("Fount last location : {}", map);
		return map;
	}

	private void getLatLonByDeviceIdFromNVDevice(Map<String, HomeWorkLocationWrapper> map, String deviceInfoId) {
		logger.info("Inside getLatLonByDeviceIdFromNVDevice");
		try {
			NVDeviceData nvDeviceData = iNVDeviceDataDao.getDeviceByDeviceId(deviceInfoId);
			if (nvDeviceData != null) {
				logger.info("Data is found : {}", nvDeviceData.getDeviceInfo().getId());
				setNVDeviceDataIntoWrapper(map, nvDeviceData);
			} else {
				logger.info("Device data is null");
			}
		} catch (DaoException daoException) {
			logger.error("Error in lat long  daoException : {}", Utils.getStackTrace(daoException));
		} catch (NoResultException resultException) {
			logger.error("Error in lat long noResultException : {}", resultException.getMessage());
		} catch (Exception e) {
			logger.error("Error in getting lat long by device id from nv device: {}", Utils.getStackTrace(e));
		}
	}

	private void setNVDeviceDataIntoWrapper(Map<String, HomeWorkLocationWrapper> map, NVDeviceData nvDeviceData) {
		if (nvDeviceData.getLatitude() != null && nvDeviceData.getLongitude() != null) {
			HomeWorkLocationWrapper wrapper = new HomeWorkLocationWrapper();
			wrapper.setLatitude(nvDeviceData.getLatitude().toString());
			wrapper.setLongitude(nvDeviceData.getLongitude().toString());
			if (nvDeviceData.getCgi() != null) {
				getBtsDetailByCgiFromNV(nvDeviceData, wrapper);
			}
			if (nvDeviceData.getModificationTime() != null) {
				wrapper.setCapturedTime(Utils.getStringDateByFormat(nvDeviceData.getModificationTime(), DATE_FORMAT_DD_MM_YYYY_HH_MM_SS_A));
			}
			map.put(CustomerCareEnum.LIVE_LOCATION.getValue(), wrapper);
			logger.info("NV Wrapper {}  and MAP {}", wrapper, map);
		} else {
			logger.info("latitude and longitude both are null");
		}
	}

	private void getBtsDetailByCgiFromNV(NVDeviceData nvDeviceData, HomeWorkLocationWrapper wrapper) {
		logger.info("Going to get bts name by cgi");
		try {
			List<Object[]> data = getBtsDetailByCgi(nvDeviceData.getCgi().toString());
			if (data != null && !data.isEmpty()) {
				logger.info("BTS NAME : {}", data.get(ZERO)[ZERO]);
				wrapper.setCellId(nvDeviceData.getCgi().toString());
				wrapper.setBtsName(data.get(ZERO)[ZERO] != null ? data.get(ZERO)[ZERO].toString() : null);
				wrapper.setPci(data.get(ZERO)[THREE] != null ? data.get(ZERO)[THREE].toString() : null);
				wrapper.setNeFrequency(data.get(ZERO)[FOUR] != null ? data.get(ZERO)[FOUR].toString() : null);
			}
		} catch (Exception e) {
			logger.error("Error in  getting bts code by cgi Exception : {}", Utils.getStackTrace(e));
		}
	}

	@Override
	public BBMDetailWrapper getBBMLocationByMsisdn(String msisdn) {
		logger.info("Going to get BBM location by msisdn : {} ", msisdn);
		BBMDetailWrapper wrapper = new BBMDetailWrapper();
		try {
			DeviceInfo deviceInfo = iDeviceInfoDao.getBBMLocationByMsisdn(msisdn);
			if (deviceInfo != null && deviceInfo.getDeviceId() != null) {
				wrapper = makeRequestToGetBBMDataByDeviceId(new StringBuilder(deviceInfo.getDeviceId()).reverse().toString(), wrapper);
				setBBMCustomerInformation(wrapper, deviceInfo);
			}
		} catch (Exception exception) {
			logger.error("Error in getting BBM location by msisdn : {}", Utils.getStackTrace(exception));
		}
		logger.info("BBM Data has found : {}", wrapper);
		return wrapper;
	}

	private void setBBMCustomerInformation(BBMDetailWrapper wrapper, DeviceInfo deviceInfo) {
		logger.info("Inside setBBMCustomerInformation");
		try {
			if (deviceInfo.getImei() != null) {
				wrapper.setBbmPin(deviceInfo.getImei());
			}
			if (deviceInfo.getIsRegistered()) {
				wrapper.setBbmMDNState(ACTIVE);
				wrapper.setRegistrationDate(
						deviceInfo.getRegistrationDate() != null ? Utils.parseDateToString(deviceInfo.getRegistrationDate(), DATE_FORMAT_YYYY_MM_DD)
								: null);
			} else {
				wrapper.setBbmMDNState(NON_ACTIVE);
				wrapper.setDeregistrationDate(deviceInfo.getDeregistrationDate() != null
						? Utils.parseDateToString(deviceInfo.getDeregistrationDate(), DATE_FORMAT_YYYY_MM_DD)
						: null);
			}
			if (wrapper.getCgi() != null) {
				List<Object[]> data = getBtsDetailByCgi(wrapper.getCgi());
				if (data != null) {
					logger.info("BTS DATA : {}", data.size());
					wrapper.setBtsCode(data.get(ZERO)[ZERO].toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error in setting bbm customer information : {}", Utils.getStackTrace(e));
		}
	}

	@SuppressWarnings("serial")
	public BBMDetailWrapper makeRequestToGetBBMDataByDeviceId(String deviceId, BBMDetailWrapper wrapper) {
		logger.info("Inside makeRequestToGetBBMDataByDeviceId");
		try {
			List<BBMDetailWrapper> wrapperList = null;
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(BBM_DATA_BASE_URL)), deviceId);
			logger.info("Url for getting bbm data from hbase : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (response != null && !response.isEmpty()) {
				wrapperList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<BBMDetailWrapper>>() {
				});
				if (wrapperList != null && !wrapperList.isEmpty())
					wrapper = wrapperList.get(ZERO);
			}
			logger.info("BBM DATA : {} ", wrapper);
		} catch (Exception e) {
			logger.error("Error in getting BBM data from hbase : {}", Utils.getStackTrace(e));
		}
		return wrapper;
	}

	@Override
	public List<BBMDetailWrapper> getLatestBBMLocationHistory(String msisdn, String minTimeRange, String maxTimeRange) {
		logger.info("Going to get latest bbm location history by msisdn {}", msisdn);
		List<BBMDetailWrapper> wrapperList = new ArrayList<>();
		try {
			DeviceInfo deviceInfo = iDeviceInfoDao.getBBMLocationByMsisdn(msisdn);
			if (deviceInfo != null && deviceInfo.getDeviceId() != null) {
				wrapperList = makeRequestToGetBBMDataByTimeRange(new StringBuilder(deviceInfo.getDeviceId()).reverse().toString(), minTimeRange,
						maxTimeRange, wrapperList);
			}
		} catch (Exception e) {
			logger.error("Error in getting latest BBM location history : {}", Utils.getStackTrace(e));
		}
		return wrapperList;
	}

	@SuppressWarnings({ "serial" })
	public List<BBMDetailWrapper> makeRequestToGetBBMDataByTimeRange(String deviceId, String minTimeRange, String maxTimeRange,
			List<BBMDetailWrapper> wrapperList) {
		logger.info("Inside makeRequestToGetBBMDataByTimeRange");
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(BBM_DATA_BASE_URL_FOR_HISTORY)),
					DEVICE_ID_PREFIX, deviceId, MIN_TIME_RANGE, minTimeRange, MAX_TIME_RANGE, maxTimeRange);
			logger.info("Url for getting bbm user history data from hbase : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (response != null && !response.isEmpty()) {
				wrapperList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<BBMDetailWrapper>>() {
				});
			}
			logger.info("BBM DATA LIST : {} ", wrapperList);
		} catch (Exception e) {
			logger.error("Error in getting BBM data by time range from hbase : {}", Utils.getStackTrace(e));
		}
		return wrapperList;
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, String> getNVInstallationDetail(String imsi, String deviceId, String deviceOs) {
		logger.info("Going to get NV installation info by Imsi : {} , device id : {} and device os : {}", imsi, deviceId, deviceOs);
		Map<String, String> map = new HashMap<>();
		String status = NV_NOT_INSTALLED;
		try {
			Boolean isInstalled = FALSE;

			List<String> valueList = Arrays.asList(String.valueOf(imsi), String.valueOf(deviceId), String.valueOf(deviceOs));
			String query = GenericMapUtils.createGenericQuery(valueList, IMSI, DEVICE_ID, DEVICE_OS_LOWERCASE);
			String baseUrl = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(NV_INSTALLATION_DETAIL_URL)), query);
			logger.info("Going to get nv installation detail from hbase url : {}", baseUrl);

			String response = CustomerCareUtils.makeGETRequest(baseUrl);
			if (response != null && !response.isEmpty()) {
				logger.info("NV response : {}", response);
				isInstalled = CustomerCareUtils.parseGsonData(response, new TypeToken<Boolean>() {
				});
				status = setNVInstallationStatus(isInstalled);
				map.put(STATUS, setNVInstallationStatus(isInstalled));
			}
		} catch (Exception e) {
			logger.error("Error in getting nv installation information : {}", Utils.getStackTrace(e));
		}
		logger.info("STATUS : {}", status);
		map.put(STATUS, status);
		return map;
	}

	private String setNVInstallationStatus(Boolean isInstalled) {
		String status;
		if (isInstalled) {
			status = NV_INSTALLED;
		} else {
			status = NV_NOT_INSTALLED;
		}
		return status;
	}

	private Map<String, String> getAverageRsrpValue(String tableName, Double lat, Double lon, String image, String kpi, String date,
			Integer zoomLevel, String siteStatus, String band) throws IOException {
		logger.info(
				"Going to get Average RSRP value for table : {}, lat : {}, long : {}, image : {}, kpi :{}, data : {}, zoomlevel :{}, siteStatus : {}, band  : {} ",
				tableName, lat, lon, image, kpi, date, zoomLevel, siteStatus, band);
		Map<String, String> resultMap = new HashMap<>();
		LatLng location = new LatLng(lat, lon);
		Tile tileId = new Tile(location, zoomLevel);

		IGenericMapService genericMapService = ApplicationContextProvider.getApplicationContext().getBean(IGenericMapService.class);
		try {
			BufferedImage bufferedImage = ImageUtils.toBufferedImage(
					genericMapService.getImageForKpiAndZone(tableName, tileId.getIdWithZoom(), image, kpi, date, siteStatus, null, band, FALSE));
			int[] tileImagePixel = TileUtils.getTileImagePixel(location, tileId);
			if (bufferedImage != null) {
				Integer colorVal = bufferedImage.getRGB(tileImagePixel[NumberUtils.INTEGER_ZERO], tileImagePixel[NumberUtils.INTEGER_ONE]);
				if (colorVal != null) {
					logger.info("color value : {}", colorVal);
					Integer rsrpValue = getRsrpValueFromHbase(colorVal);
					logger.info("rsrpValue ================== {} ", rsrpValue);
					resultMap = getCoverageByRsrpValue(rsrpValue, resultMap);
				}
			}
		} catch (IOException e) {
			logger.error("Error in getting buffered image: {}", Utils.getStackTrace(e));
		}
		return resultMap;
	}

	@SuppressWarnings("serial")
	private Integer getRsrpValueFromHbase(Integer colorVal) {
		Integer rsrpValue = null;
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_COLOR_MAP_DATA)),
					ForesightConstants.FORWARD_SLASH, colorVal);
			logger.info("url for color map data : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			logger.info("COLOR Map response : {}", response);
			if (Utils.checkForValueInString(response)) {
				logger.info("color map response : {}", response);
				rsrpValue = new Gson().fromJson(response, new TypeToken<Integer>() {
				}.getType());
			}
		} catch (HttpException e) {
			logger.error("Error in getting color map data from hbase : {}", e.getMessage());
		} catch (Exception ex) {
			logger.error("Error in getting rsrp value Eception :  {}", Utils.getStackTrace(ex));
		}
		return rsrpValue;
	}

	private Map<String, String> getCoverageByRsrpValue(Integer rsrpValue, Map<String, String> resultMap) {
		if (rsrpValue != null) {
			if (ConfigUtils.getDouble(GOOD_OUTDOOR_GOOD_INDOOR) >= rsrpValue && rsrpValue >= ConfigUtils.getDouble(GOOD_OUTDOOR_WEAK_INDOOR)) {
				resultMap.put(ForesightConstants.INDDOOR_OUTDOOR_RESULT, ConfigUtils.getString(ForesightConstants.NOVELVOX_GOOD_OUTDOOR_INDORE));
			} else if (ConfigUtils.getDouble(GOOD_OUTDOOR_WEAK_INDOOR) >= rsrpValue && rsrpValue >= ConfigUtils.getDouble(WEAK_OUTDOOR_NO_INDOOR)) {
				resultMap.put(ForesightConstants.INDDOOR_OUTDOOR_RESULT,
						ConfigUtils.getString(ForesightConstants.NOVELVOX_GOOD_OUTDOOR_LIMITED_INDORE));
			} else if (ConfigUtils.getDouble(WEAK_OUTDOOR_NO_INDOOR) >= rsrpValue && rsrpValue >= ConfigUtils.getDouble(NO_OUTDOOR_NO_INDOOR)) {
				resultMap.put(ForesightConstants.INDDOOR_OUTDOOR_RESULT, ConfigUtils.getString(ForesightConstants.NOVELVOX_OUTDOOR_ONLY));
			} else {
				resultMap.put(ForesightConstants.INDDOOR_OUTDOOR_RESULT, ConfigUtils.getString(ForesightConstants.NOVELVOX_NO_COVERAGE));
			}
		} else {
			resultMap.put(ForesightConstants.INDDOOR_OUTDOOR_RESULT, ConfigUtils.getString(ForesightConstants.NOVELVOX_NO_COVERAGE));
		}
		return resultMap;
	}

	@SuppressWarnings("serial")
	@Override
	public List<String> getGeographyDetailsByLatLong(Double latitude, Double longitude, String type) {
		logger.info("Going to get geography detail by latitude : {} longitude : {} type : {}", latitude, longitude, type);
		List<String> geographyList = new ArrayList<>();
		try {
			String response = getGeographyByResionType(latitude, longitude, type);
			if (Utils.checkForValueInString(response)) {
				geographyList = new Gson().fromJson(response, new TypeToken<List<String>>() {
				}.getType());
			}
		} catch (Exception e) {
			logger.error("Error in fetching geography data from dropwizard : {}", Utils.getStackTrace(e));
		}
		logger.info("GEOGRAPHY LIST : {}", geographyList);
		return geographyList;
	}

	/***
	 * Need to remove it
	 * 
	 * private static List<String> populateAllGeopgraphyDataIntoMap(Double latitude,
	 * Double longitude, List<String> geographyList) { logger.info("Inside
	 * populateAllGeopgraphyDataIntoMap"); try { return geographyList =
	 * populateGeographyData(latitude, longitude, null,geographyList); } catch
	 * (Exception e) { logger.error("Error in populating all geography data into map
	 * for lat : {} long : {} Exception : {}", latitude, longitude,
	 * Utils.getStackTrace(e)); return geographyList; } }
	 */

	/**
	 * need to remove this methodpopulateGeographyData
	 * 
	 * private static List<String> populateAllGeopgraphyDataIntoMap(Double latitude,
	 * Double longitude, List<String> geographyList) { logger.info("Inside
	 * populateAllGeopgraphyDataIntoMap"); try { if
	 * (!Utils.isValidMap(geographyMap)) { logger.info("Geography Map is NULL");
	 * geographyMap = new HashMap<>(); geographyList =
	 * populateGeographyData(latitude, longitude, null); geographyMap.put(latitude +
	 * ForesightConstants.UNDERSCORE + longitude, geographyList); } else if
	 * (Utils.isValidMap(geographyMap)) { if
	 * (Utils.isValidList(geographyMap.get(latitude + ForesightConstants.UNDERSCORE
	 * + longitude))) { logger.info("Geography Map already populated");
	 * geographyList = geographyMap.get(latitude + ForesightConstants.UNDERSCORE +
	 * longitude); } else { logger.info("Going to refresh Geography Map");
	 * geographyMap = new HashMap<>(); geographyList =
	 * populateGeographyData(latitude, longitude, null); geographyMap.put(latitude +
	 * ForesightConstants.UNDERSCORE + longitude, geographyList); } } } catch
	 * (Exception e) { logger.error("Error in populating all geography data into map
	 * for lat : {} long : {} Exception : {}", latitude, longitude,
	 * Utils.getStackTrace(e)); } logger.info("GEOGRAPHY MAP : {}", geographyMap);
	 * return geographyList; }
	 * 
	 * @param geographyList2
	 */

	/***
	 * @SuppressWarnings("serial") public static List<String>
	 * populateGeographyData(Double latitude, Double longitude, String type,
	 * List<String> geographyList) { logger.info("Inside populateGeographyData ");
	 * try { String response = getGeographyByResionType(latitude, longitude, type);
	 * if (Utils.checkForValueInString(response)) { geographyList = new
	 * Gson().fromJson(response, new TypeToken<List<String>>() { }.getType()); } }
	 * catch (Exception e) { logger.error("Error in fetching geography data from
	 * dropwizard : {}", Utils.getStackTrace(e)); } logger.info("GEOGRAPHY LIST :
	 * {}", geographyList); return geographyList; }
	 */

	private static String getGeographyByResionType(Double latitude, Double longitude, String type) {
		String response = null;
		try {
			String dbType = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_CUSTOMER_CARE_REGION_KEY), CUSTOMER_CARE_REGION);
			if (type != null && type.equalsIgnoreCase(dbType)) {
				response = getGeographyDataFromMS(latitude, longitude, CC_CUSOMER_CARE_GEOGRAPHYNAME_URL);
				logger.info("Customercare Geography {} response {}", type, response);
			} else {
				response = getGeographyDataFromMS(latitude, longitude, CC_GET_ALL_GEOGRAPHY_DATA_URL);
				logger.info("All Geography {} response {}", type, response);
			}
		} catch (HttpException httpException) {
			logger.error("Error in making http request Exception :  {}", httpException.getMessage());
		} catch (Exception e) {
			logger.error("Error in getting geography data Exception : {}", Utils.getStackTrace(e));
		}
		return response;
	}

	private static String getGeographyDataFromMS(Double latitude, Double longitude, String methodURL) throws HttpException {
		String query = getQueryParamsForGeography(latitude, longitude);
		String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(methodURL)), query);
		logger.info("URL for Geography Data : {}", url);
		return CustomerCareUtils.makeGETRequest(url);
	}

	private static String getQueryParamsForGeography(Double latitude, Double longitude) {
		List<String> valueList = Arrays.asList(String.valueOf(latitude), String.valueOf(longitude));
		List<String> latLongKey = ConfigUtils.getStringList(CC_GEOGRAPHY_LAT_LONG_KEYS);
		return (GenericMapUtils.createGenericQuery(valueList, latLongKey.get(ForesightConstants.ZERO), latLongKey.get(ForesightConstants.ONE)));
	}

	@Override
	public Map<String, HomeWorkLocationWrapper> getNVLiveAndHomeWorkLocationByImsi(String imsi, String notificationId, String locationType,
			String deviceId, String timeStamp, String callType) {
		logger.info("Going to get nv live and home work location by imsi {} notificationId {} locationType {} device id : {}", imsi, notificationId,
				locationType, deviceId);
		Map<String, HomeWorkLocationWrapper> liveAndHomeWorkMap = new HashMap<>();
		try {
			if (locationType.equalsIgnoreCase(CustomerCareEnum.HOME_WORK_LOCATION.getValue())) {
				liveAndHomeWorkMap = getHomeAndWorkLocationByImsi(imsi, timeStamp, callType);
			} else if (locationType.equalsIgnoreCase(CustomerCareEnum.LIVE_LOCATION.getValue())) {
				liveAndHomeWorkMap = getNVLiveLocationByImsiAndId(imsi, notificationId, deviceId);
			}
		} catch (Exception exception) {
			logger.error("Error in get nv live and home work location by imsi : {} locationType : {} Exception {}", imsi, locationType,
					Utils.getStackTrace(exception));
		}
		logger.info("LIVE and HOME WORK Location data {}", liveAndHomeWorkMap);
		return liveAndHomeWorkMap;
	}

	@SuppressWarnings("serial")
	@Override
	public List<NEHaveAlarm> isSiteHaveAlarm(List<String> neIds, String startTime, String endTime) {
		logger.info("Going to get outage alarm for neIds : {} startTime : {} endTime : {} ", neIds.size(), startTime, endTime);
		List<NEHaveAlarm> outageHistoryList = new ArrayList<>();
		try {
			String alarmCategory = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_ALARM_CATEGORY_KEY), OUTAGE_ALARM_CATEGORY);
			List<String> valueList = new ArrayList<>();
			valueList.add(startTime);
			valueList.add(alarmCategory);
			String query = null;
			query = setQueryStringForOutageAlarm(endTime, valueList);
			logger.info("Query String : {}", query);
			String baseUrl = ConfigUtils.getString(OUTAGE_ALARM_HISTORY_URL);
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
			httpServletRequest.setRequestURI(baseUrl);
			httpServletRequest.setQueryString(query);
			logger.info("Url for outage alarm history  : {}", Utils.getDropwizardUrlWithPrefix(httpServletRequest));
			String response = CustomerCareUtils.makePOSTRequest(Utils.getDropwizardUrlWithPrefix(httpServletRequest), getEntityForURL(neIds));
			if (response != null && !response.isEmpty()) {
				outageHistoryList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<NEHaveAlarm>>() {
				});
				logger.info("Outage alarm history response : {}", outageHistoryList.size());
			}
		} catch (Exception e) {
			logger.error("Error in getting outage alarm : {}", Utils.getStackTrace(e));
		}

		return outageHistoryList;
	}

	private String setQueryStringForOutageAlarm(String endTime, List<String> valueList) {
		String query = null;
		if (Utils.checkForValueInString(endTime)) {
			valueList.add(endTime);
			query = GenericMapUtils.createGenericQuery(valueList, START_TIME, ALARM_CATEGORY, END_TIME);
		} else {
			query = GenericMapUtils.createGenericQuery(valueList, START_TIME, ALARM_CATEGORY);
		}
		return query;
	}

	@Override
	public Map<String, String> getCoverageHoleMitigationDate(String timeStamp, String band) {
		logger.info("Going to get date for coverage hole mitigation by current date : {} and band : {}", timeStamp, band);
		Map<String, String> finalMap = new HashMap<>();
		try {

			String coverageHoleKey = ConfigUtils.getString(MITIGATION_COVERAGE_HOLE_KEY);

			long testTimeStamp = Long.parseLong(timeStamp);
			LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(testTimeStamp), TimeZone.getDefault().toZoneId());
			LocalDate givenDate = triggerTime.toLocalDate();

			TemporalField weekOfYear = WeekFields.of(Locale.UK).weekOfWeekBasedYear();
			int weekofGivenDate = givenDate.get(weekOfYear);

			if (Utils.checkForValueInString(coverageHoleKey)) {
				/** code to pick the date from system configuration table from value column */

				band = Utils.checkForValueInString(band) ? band : ForesightConstants.BLANK_STRING;

				String systemConfigurationDate = iSystemConfigurationDao.getValueByName((CustomerCareUtils.appendData(coverageHoleKey, band)));
				logger.info("System Configuration Dao Date : {}", systemConfigurationDate);
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate dateFromSysConfig = LocalDate.parse(systemConfigurationDate, dtf);
				int weekOfDatabaseDate = dateFromSysConfig.get(weekOfYear);

				/** code to pick data from Job history */
				List<Object[]> dataFromJobHistory = iJobHistoryDao.getWeekNoAndValueByName((coverageHoleKey + band));
				String resultDate = null;
				resultDate = setDateByWeekNo(weekofGivenDate, weekOfDatabaseDate, dataFromJobHistory);
				logger.info("Result Date : {}", resultDate);
				if (Utils.checkForValueInString(resultDate)) {
					SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
					Date dateByWeekNo = df.parse(resultDate);
					String finadate = Utils.getStringDateByFormat(dateByWeekNo, ForesightConstants.DATE_FORMAT_dd_MM_yy);
					finalMap.put(ForesightConstants.DATE, finadate);
				}
			}
		} catch (Exception e) {
			finalMap.put(ForesightConstants.DATE, null);
			logger.info("Error in gettiing date for coveragre hole :{}", Utils.getStackTrace(e));
		}
		logger.info("Final Map : {}", finalMap);
		return finalMap;

	}

	private String setDateByWeekNo(int weekofGivenDate, int weekOfDatabaseDate, List<Object[]> dataFromJobHistory) {
		String resultDate = null;
		if (Utils.isValidList(dataFromJobHistory)) {
			if (weekOfDatabaseDate == weekofGivenDate) {
				resultDate = dataFromJobHistory.get(ONE)[ONE].toString();
			} else if (weekOfDatabaseDate < weekofGivenDate) {
				resultDate = dataFromJobHistory.get(ZERO)[ONE].toString();
			} else {
				resultDate = dataFromJobHistory.get(ONE)[ONE].toString();
			}
		}
		return resultDate;
	}

	@Override
	public List<String> getDeviceOSDetail() {
		logger.info("Going to fetch DeviceOs detail");
		List<String> deviceOsList = new ArrayList<>();
		try {
			deviceOsList = ConfigUtils.getStringList(CUSTOMER_CARE_DEVICE_OS_LIST);
			logger.info("DeviceOs List {} ", deviceOsList);
		} catch (Exception exception) {
			logger.error("Unable to fetch DeviceOs Exception {} ", Utils.getStackTrace(exception));
		}
		return deviceOsList;
	}

	@Override
	public Map<String, String> getDeviceOsDetailByImsi(String imsi) {
		logger.info("Going to get device os detail by imsi : {}", imsi);
		Map<String, String> deviceOsMap = new HashMap<>();
		String deviceOs = null;
		String deviceId = null;
		String errMessage = null;
		try {
			DeviceInfo deviceInfo = iDeviceInfoDao.getDeviceInfoByImsi(imsi);
			if (deviceInfo != null) {
				deviceOs = setDeviceOs(deviceOs, deviceInfo);
				if (CustomerCareUtils.checkNullObject(deviceInfo) && Utils.checkForValueInString(deviceInfo.getDeviceId())) {
					deviceId = deviceInfo.getDeviceId();
					logger.info("Device Id : {}", deviceId);
				}
			} else {
				errMessage = "Device not found";
			}
		} catch (Exception e) {
			logger.error("Error in getting device os detail by imsi : {} Exception  : {}", imsi, Utils.getStackTrace(e));
		}
		deviceOsMap.put(DEVICE_OS, deviceOs);
		deviceOsMap.put(DEVICE_ID, deviceId);
		deviceOsMap.put(ERR_MESSAGE, errMessage);
		logger.info("Device OS Map : {}", deviceOsMap);
		return deviceOsMap;
	}

	private String setDeviceOs(String os, DeviceInfo deviceInfo) {
		String apkOs = deviceInfo.getApkDetail().getApkOS().name();
		if (Utils.checkForValueInString(apkOs)) {
			logger.info("APK OS : {}", apkOs);
			if (apkOs.equalsIgnoreCase(DEVICE_OS_ANDROID)) {
				os = DEVICE_OS_ANDROID;
			} else if (apkOs.equalsIgnoreCase(DEVICE_OS_IPHONE)) {
				os = DEVICE_OS_IOS;
			}
		}
		return os;
	}

	private String generateUniqueIdWithPrefix(String id, String prefix) {
		return CustomerCareUtils.appendData(prefix, StringUtils.leftPad(id, 15, AppConstants.DEVICE_ID_PADDING_CONSTANT));
	}

	@Override
	public Map<String, String> getDeviceByDeviceId(String deviceId) {
		logger.info("Going to get device by device id : {}", deviceId);
		Map<String, String> deviceDetailMap = new HashMap<>();
		String finalDeviceId = null;
		String completeDeviceId = generateUniqueIdWithPrefix(deviceId, AppUtils.DEVICE_ID_PREFIX);
		try {
			if (Utils.checkForValueInString(completeDeviceId)) {
				DeviceInfo deviceInfo = iDeviceInfoDao.getDeviceInfoByDeviceId(completeDeviceId);
				if (deviceInfo != null && Utils.checkForValueInString(deviceInfo.getDeviceId())) {
					finalDeviceId = deviceInfo.getDeviceId();
					logger.info("Final device id : {}", finalDeviceId);
				} else {
					deviceDetailMap.put(ERR_MESSAGE, "Please enter correct device id");
				}
			}
		} catch (DaoException daoException) {
			logger.error("Error in getting device by deviceId  : {} DaoException : {}", deviceId, Utils.getStackTrace(daoException));
		} catch (Exception e) {
			logger.error("Error in getting device by devideId : {} Exception : {}", deviceId, Utils.getStackTrace(e));
		}
		deviceDetailMap.put(DEVICE_ID, finalDeviceId);
		logger.info("Device id map :{}", deviceDetailMap);
		return deviceDetailMap;
	}

	@Override
	public Map<String, Integer> getLiveLocationCount() {
		logger.info("Live location count value : {}", ConfigUtils.getInteger(CC_LIVE_LOCATION_COUNT));
		Map<String, Integer> countMap = new HashMap<>();
		try {
			countMap.put(LIVE_LOCATION_COUNT, ConfigUtils.getInteger(CC_LIVE_LOCATION_COUNT));
		} catch (Exception e) {
			logger.error("Error in getting live location count : {}", Utils.getStackTrace(e));
		}
		logger.info("Live location count : {}", countMap);
		return countMap;
	}

	@Override
	public Map<String, String> sendAcknowledgementForLiveLocation(String notificationId) {
		logger.info("Going to send Acknowledgement for live location notificationId {} ", notificationId);
		Map<String, String> liveLocationMap = new HashMap<>();
		try {
			if (Utils.checkForValueInString(notificationId)) {
				PushNotification pushNotification = iPushNotificationDao.findByPk(Integer.parseInt(notificationId));
				String locationReason = pushNotification.getLiveLocationDetail();
				if (Utils.checkForValueInString(locationReason)) {
					populateLiveLocationMap(liveLocationMap, pushNotification, locationReason);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to send Acknowledgement for Live Location {} Exception {} ", notificationId, Utils.getStackTrace(e));
		}
		logger.info("Live location map : {}", liveLocationMap);
		return liveLocationMap;
	}

	private void populateLiveLocationMap(Map<String, String> liveLocationMap, PushNotification pushNotification, String locationReason) {
		try {
			JSONObject jsonObject = new JSONObject(locationReason);
			liveLocationMap.put(CAPTURED_TIME,
					pushNotification.getExecutionTime() != null
							? Utils.getStringDateByFormat(pushNotification.getExecutionTime(), DATE_FORMAT_DD_MM_YYYY_HH_MM_SS_A)
							: null);
			if (jsonObject.has(LOCATION_REASON)) {
				liveLocationMap.put(NV_REMARK, jsonObject.getString(LOCATION_REASON) != null ? jsonObject.getString(LOCATION_REASON) : null);
			}
			if (jsonObject.has(LATITUDE)) {
				liveLocationMap.put(LATITUDE,
						String.valueOf(jsonObject.getDouble(LATITUDE)) != null ? String.valueOf(jsonObject.getDouble(LATITUDE)) : null);
			}
			if (jsonObject.has(LONGITUDE)) {
				liveLocationMap.put(LONGITUDE,
						String.valueOf(jsonObject.getDouble(LATITUDE)) != null ? String.valueOf(jsonObject.getDouble(LONGITUDE)) : null);
			}
			setLiveLocationInfoByCgi(liveLocationMap, jsonObject);
		} catch (JSONException jsonException) {
			logger.error("Error in populating live location map JsonExecption : {}", Utils.getStackTrace(jsonException));
		}
	}

	private void setLiveLocationInfoByCgi(Map<String, String> liveLocationMap, JSONObject jsonObject) {
		try {
			if (jsonObject.has(CELL_ID) && String.valueOf(jsonObject.getBigInteger(CELL_ID)) != null) {
				liveLocationMap.put(ECGI, String.valueOf(jsonObject.getBigInteger(CELL_ID)));
				List<Object[]> data = getBtsDetailByCgi(String.valueOf(jsonObject.getBigInteger(CELL_ID)));
				if (data != null && !data.isEmpty()) {
					liveLocationMap.put(BTS_NAME, data.get(ZERO)[ZERO] != null ? data.get(ZERO)[ZERO].toString() : null);
					liveLocationMap.put(PCI, data.get(ZERO)[THREE] != null ? data.get(ZERO)[THREE].toString() : null);
					liveLocationMap.put(NE_FREQUENCY, data.get(ZERO)[FOUR] != null ? data.get(ZERO)[FOUR].toString() : null);
				}
			}
		} catch (JSONException jsonException) {
			logger.error("Error in setting live location detail JsonException  : {}", Utils.getStackTrace(jsonException));
		}
	}

	private List<Object[]> getBtsDetailByCgi(String cgi) {
		return iRANDetailDao.getBtsDetailByCgi(cgi);
	}

	@Override
	@Transactional
	public Map<String, String> updateDeviceInfoByDeviceId(String deviceId, String imsi, String msisdn) {
		logger.info("Going to update device info by device id : {} , imsi : {} and msisdn : {}", deviceId, imsi, msisdn);
		Map<String, String> deviceMap = new HashMap<>();
		try {
			int updateCount = iDeviceInfoDao.updateDeviceInfoByDeviceId(deviceId, imsi, msisdn);
			if (updateCount != ZERO) {
				deviceMap.put(ForesightConstants.MESSAGE, "Updated successfully");
			} else {
				deviceMap.put(ForesightConstants.MESSAGE, "Updating unsuccessfully");
			}
		} catch (DaoException daoException) {
			logger.error("Error in updating device info by device id : {} DaoException  : {}", deviceId, Utils.getStackTrace(daoException));
		} catch (Exception e) {
			logger.error("Error in updating device info by device id : {} Exception : {}", deviceId, Utils.getStackTrace(e));
		}
		logger.info("Updated device Map : {}", deviceMap);
		return deviceMap;
	}

	@Override
	public List<CustomerCareSiteWrapper> getSiteHistoryDataByViewPort(Double southWestLat, Double northEastLat, Double southWestLong,
			Double northEastLong, String startTime, String endTime) {
		logger.info(
				"Going to get site history data by view port southWestLat : {}, northEastLat : {}, southWestLong : {}, northEastLong : {}, startTime : {} and endTime : {} ",
				southWestLat, northEastLat, southWestLong, northEastLong, startTime, endTime);
		List<CustomerCareSiteWrapper> careSiteWrappers = new ArrayList<>();
		try {
			// remove code here for history code
			List<Object[]> siteList = iCustomerCareDao.getSiteHistoryDataByViewPort(southWestLat, northEastLat, southWestLong, northEastLong,
					Utils.parseDateToString(getDateByTimeStamp(startTime), DATE_FORMAT_YYYY_MM_DD), NEStatus.ONAIR.toString());
			if (siteList != null && !siteList.isEmpty()) {
				logger.info("Site list size : {}", siteList.size());
				careSiteWrappers = new ArrayList<>();
				Map<String, List<CustomerCareSiteWrapper>> siteHistoryMap = new HashMap<>();

				for (Object[] siteData : siteList) {
					setSiteHistoryDateIntoWrapper(siteHistoryMap, siteData, null);
				}
				if (Utils.isValidMap(siteHistoryMap)) {
					careSiteWrappers = siteHistoryMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
					logger.info("Site wrapper size for site history : {}", careSiteWrappers.size());
				}
			}
		} catch (Exception e) {
			logger.error("Error in getting site history data by view port : {}", Utils.getStackTrace(e));
		}
		return careSiteWrappers;
	}

	@Override
	public Map<String, Boolean> getHighlyUtilizedCellsDetail(String startTime, String endTime, Map<String, List<String>> siteList) {
		logger.info("Going to populate map for highly utilized cells");
		Map<String, Boolean> hucMap = new HashMap<>();
		List<String> nokiaNeNameList = new ArrayList<>();
		List<String> zteNeNameList = new ArrayList<>();
		try {
			if (siteList.containsKey(ZTE_VENDOR)) {
				zteNeNameList = siteList.get(ZTE_VENDOR);
			}
			if (siteList.containsKey(NOKIA_VENDOR)) {
				nokiaNeNameList = siteList.get(NOKIA_VENDOR);
			}
			logger.info("site zte nename list : {}", zteNeNameList.size());
			logger.info("site nokia nename list : {}", nokiaNeNameList.size());
			getHUCDataByVendorAndNeid(endTime, zteNeNameList, ZTE_VENDOR, hucMap);
			getHUCDataByVendorAndNeid(endTime, nokiaNeNameList, NOKIA_VENDOR, hucMap);
			logger.info("final hucMap  size : {}", hucMap.size());
		} catch (Exception e) {
			logger.error("Error in getting highly utilized cells  : {}", Utils.getStackTrace(e));
		}
		return hucMap;
	}

	private void getHUCDataByVendorAndNeid(String endTime, List<String> neNameList, String vendor, Map<String, Boolean> hucMap) {
		logger.info("Going to get HUC Data by vendor : {} ", vendor);

		KPIRequestWrapper reqWrapper = setKPIRequestData(neNameList, endTime, RAN_DOMAIN, vendor);
		if (reqWrapper != null) {
			List<KPIResponseWrapper> kpiResponseWrapperList = new ArrayList<>();
			try {
				List<KPIResponseWrapper> kpiResponseWrappers = getKPIDataFromHbase(reqWrapper);
				getKpiDetailByUtilizationKey(kpiResponseWrapperList, kpiResponseWrappers, hucMap);
				logger.info("HUC MAP : {}", hucMap != null ? hucMap.size() : null);
			} catch (HttpException e) {
				logger.error("Error in getting kpi response : {}", Utils.getStackTrace(e));
			} catch (Exception e) {
				logger.error("Error in getting huc data  : {}", Utils.getStackTrace(e));
			}
		}
	}

	private void setSiteHistoryDateIntoWrapper(Map<String, List<CustomerCareSiteWrapper>> siteHistoryMap, Object[] siteData, Boolean isHUCCell) {
		List<CustomerCareSiteWrapper> customerCareSiteWrappers;
		String neName = StringUtils.substringBeforeLast(String.valueOf(siteData[TWO]), ForesightConstants.UNDERSCORE);
		if (siteHistoryMap.get(neName) != null) {
			CustomerCareSiteWrapper careSiteWrapper = setCustomerCareSiteWrapperData(siteData, isHUCCell, neName);
			List<CustomerCareSiteWrapper> siteWrappers = siteHistoryMap.get(neName);
			siteWrappers.add(careSiteWrapper);
			siteHistoryMap.put(neName, siteWrappers);
		} else {
			customerCareSiteWrappers = new ArrayList<>();
			CustomerCareSiteWrapper careSiteWrapper = setCustomerCareSiteWrapperData(siteData, isHUCCell, neName);
			customerCareSiteWrappers.add(careSiteWrapper);
			siteHistoryMap.put(neName, customerCareSiteWrappers);
		}
	}

	private CustomerCareSiteWrapper setCustomerCareSiteWrapperData(Object[] siteData, Boolean isHUCCell, String neName) {
		return new CustomerCareSiteWrapper(siteData[ZERO] != null ? Double.parseDouble(String.valueOf(siteData[ZERO])) : null,
				siteData[ONE] != null ? Double.parseDouble(String.valueOf(siteData[ONE])) : null, neName, neName,
				siteData[FOUR] != null ? String.valueOf(siteData[FOUR]) : null,
				siteData[TEN] != null ? Integer.parseInt(String.valueOf(siteData[TEN])) : null,
				siteData[SEVEN] != null ? String.valueOf(siteData[SEVEN]) : null,
				siteData[EIGHT] != null ? Integer.parseInt(String.valueOf(siteData[EIGHT])) : null,
				siteData[NINE] != null ? Integer.parseInt(String.valueOf(siteData[NINE])) : null,
				siteData[THREE] != null ? String.valueOf(siteData[THREE]) : null, siteData[FIVE] != null ? String.valueOf(siteData[FIVE]) : null,
				siteData[SIX] != null ? String.valueOf(siteData[SIX]) : null, siteData[EIGHTEEN] != null ? String.valueOf(siteData[EIGHTEEN]) : null,
				siteData[NINETEEN] != null ? Integer.parseInt(String.valueOf(siteData[NINETEEN])) : null, isHUCCell, neName, null, null);
	}

	private Date getDateByTimeStamp(String timeStamp) {
		if (Utils.checkForValueInString(timeStamp)) {
			try {
				return new Date(Long.parseLong(timeStamp));
			} catch (NumberFormatException formatException) {
				logger.error("Error in parse string to long for timeStamp :{} NumberFormatExcepton : {}", timeStamp,
						Utils.getStackTrace(formatException));
			} catch (Exception e) {
				logger.error("Error in get date by time stamp : {}", Utils.getStackTrace(e));
			}
		}
		return null;
	}

	public List<CustomerCareSiteWrapper> getHighlyUtilizedCellByNeidList(List<String> neidList, String startTime, String endTime) {
		List<CustomerCareSiteWrapper> siteWrapperList = new ArrayList<>();
		List<Object[]> hucSiteList = iCustomerCareDao.getHighlyUtilizedCellByNeidList(neidList, startTime, endTime);
		if (hucSiteList != null && !hucSiteList.isEmpty()) {
			CustomerCareSiteWrapper careSiteWrapper = new CustomerCareSiteWrapper();
			hucSiteList.forEach(object -> {
				careSiteWrapper.setNeId(object[ZERO] != null ? String.valueOf(object[ZERO]) : null);
				careSiteWrapper.setIsHighlyUtilized(
						object[ONE] != null && String.valueOf(object[ONE]) != null ? Boolean.parseBoolean(String.valueOf(object[ONE])) : null);
				siteWrapperList.add(careSiteWrapper);
			});
		}
		return siteWrapperList;
	}

	@Override
	public List<CustomerCareSiteWrapper> getPlannedSiteHistoryByViewPort(Double southWestLat, Double northEastLat, Double southWestLong,
			Double northEastLong, String startTime, String endTime) {
		logger.info(
				"Going to get planned site history data by view port southWestLat : {}, northEastLat : {}, southWestLong : {}, northEastLong : {}, startTime : {} and endTime : {} ",
				southWestLat, northEastLat, southWestLong, northEastLong, startTime, endTime);
		List<CustomerCareSiteWrapper> careSiteWrappers = new ArrayList<>();
		try {
			List<Object[]> siteList = iCustomerCareDao.getSiteHistoryDataByViewPort(southWestLat, northEastLat, southWestLong, northEastLong,
					Utils.parseDateToString(getDateByTimeStamp(startTime), DATE_FORMAT_YYYY_MM_DD), NEStatus.PLANNED.toString());
			populateSitesDataInWrapper(careSiteWrappers, siteList);
		} catch (Exception e) {
			logger.error("Error in getting planned site history by view port  : {}", Utils.getStackTrace(e));
		}
		return careSiteWrappers;
	}

	private void populateSitesDataInWrapper(List<CustomerCareSiteWrapper> careSiteWrappers, List<Object[]> siteList) {
		if (siteList != null && !siteList.isEmpty()) {
			logger.info("Site list size : {}", siteList.size());
			siteList.forEach(siteData -> {
				String neName = siteData[TWENTY] != null ? String.valueOf(siteData[TWENTY]) : null;
				CustomerCareSiteWrapper careSiteWrapper = new CustomerCareSiteWrapper(
						siteData[ZERO] != null ? Double.parseDouble(String.valueOf(siteData[ZERO])) : null,
						siteData[ONE] != null ? Double.parseDouble(String.valueOf(siteData[ONE])) : null, neName, neName,
						siteData[TWENTY_ONE] != null ? String.valueOf(siteData[TWENTY_ONE]) : null,
						siteData[TEN] != null ? Integer.parseInt(String.valueOf(siteData[TEN])) : null,
						siteData[SEVEN] != null ? String.valueOf(siteData[SEVEN]) : null,
						siteData[EIGHT] != null && String.valueOf(siteData[EIGHT]) != null ? Integer.parseInt(String.valueOf(siteData[EIGHT])) : null,
						siteData[NINE] != null && String.valueOf(siteData[NINE]) != null ? Integer.parseInt(String.valueOf(siteData[NINE])) : null,
						siteData[THREE] != null ? String.valueOf(siteData[THREE]) : null,
						siteData[FIVE] != null ? String.valueOf(siteData[FIVE]) : null, siteData[SIX] != null ? String.valueOf(siteData[SIX]) : null,
						siteData[EIGHTEEN] != null ? String.valueOf(siteData[EIGHTEEN]) : null,
						siteData[NINETEEN] != null ? Integer.parseInt(String.valueOf(siteData[NINETEEN])) : null, null, null, null, null);
				careSiteWrappers.add(careSiteWrapper);
			});
			logger.info("Planned site for history : {}", careSiteWrappers.size());
		}
	}

	private String getALLPMFrequencyDate(String vendor) {
		logger.info("ALL PM Frequency date method");
		String startString = ConfigUtils.getString(CC_ALL_PERFORMANCE_STARTING_KEY);
		String endString = ConfigUtils.getString(CC_ALL_PERFORMANCE_ENDTING_KEY);
		String date = null;
		date = getDateByKey(vendor, startString, endString, date);
		return date;
	}

	private String getBBHDate(String vendor) {
		logger.info("BBH date method");
		String startString = ConfigUtils.getString(CC_BBH_PERFORMANCE_STARTING_KEY);
		String endString = ConfigUtils.getString(CC_BBH_PERFORMANCE_ENDTING_KEY);
		String date = null;
		date = getDateByKey(vendor, startString, endString, date);
		return date;
	}

	private String getDateByKey(String vendor, String startString, String endString, String date) {
		try {
			if (Utils.checkForValueInString(startString) && Utils.checkForValueInString(endString)) {
				date = CustomerCareUtils.appendData(startString, vendor, endString);
				logger.info("PM ALL Frequency Data Key : {}", date);
				date = iSystemConfigurationDao.getValueByName(date);
				logger.info("PM Date found : {}", date);
			}
		} catch (Exception e) {
			logger.error("Error in getting date by key Exception : {}", Utils.getStackTrace(e));
		}
		return date;
	}

	public String getHourlyDate(String vendor) {
		String startString = ConfigUtils.getString(CC_HOURLY_PERFORMANCE_STARTING_KEY);
		String endString = ConfigUtils.getString(CC_HOURLY_PERFORMANCE_ENDTING_KEY);
		String date = null;
		date = getDateByKey(vendor, startString, endString, date);
		return date;
	}

	private Integer getHourByDate(Date date) {
		if (date != null) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(date);
			return calender.get(Calendar.HOUR_OF_DAY);
		}
		return null;
	}

	private KPIRequestWrapper setTimeForAllFrequency(String dbTime, String dbFormat, KPIRequestWrapper kpiRequestWrapper) {
		logger.info("Going to get date for kpi bbh data by time : {}", dbTime);
		if (Utils.checkForValueInString(dbTime)) {
			Date dbDate = Utils.getDateFromStringFormat(dbTime, dbFormat);
			kpiRequestWrapper.setStartDate(dbDate.getTime());
			kpiRequestWrapper.setEndDate(dbDate.getTime());
		}
		return kpiRequestWrapper;
	}

	private KPIRequestWrapper setTimeForHourlyData(String dbTime, KPIRequestWrapper kpiRequestWrapper) {
		logger.info("Going to get date for kpi hourly data by time : {}", dbTime);
		if (Utils.checkForValueInString(dbTime)) {
			Date localDate = new Date(kpiRequestWrapper.getStartDate());
			Integer myHour = getHourByDate(localDate);
			Date dbDate = Utils.getDateFromStringFormat(dbTime, KPI_HOURLY_DATA_DATE_FORMAT);
			Integer dbHour = getHourByDate(dbDate);
			if (dbDate != null && dbHour != null) {
				if (localDate.compareTo(dbDate) == ForesightConstants.ZERO) {
					logger.info("Get Data by DB Date : {}", dbDate);
					if (myHour.equals(dbHour) || myHour > dbHour) {
						kpiRequestWrapper.setStartDate(dbDate.getTime());
						kpiRequestWrapper.setEndDate(dbDate.getTime());
						logger.info("Get Data by DB Hour for same date db time : {} and local time : {}", dbDate, localDate);
					}
				}
				if (localDate.compareTo(dbDate) > ForesightConstants.ZERO) {
					kpiRequestWrapper.setStartDate(dbDate.getTime());
					kpiRequestWrapper.setEndDate(dbDate.getTime());
					logger.info("Get Data by DB Hour for bigger date db time : {} and local time : {}", dbDate, localDate);
				}
			} else {
				logger.info("No data in db for kpi call");
			}
		}
		return kpiRequestWrapper;
	}

	@Override
	public Map<String, String> getCustomerLocationDetail() {
		logger.info("Going to get customer location detail by user permission");
		Map<String, String> customerLocationMap = new HashMap<>();
		try {
			Set<String> permissions = customerInfo.getPermissions();
			if (permissions != null && !permissions.isEmpty()) {
				logger.info("Permisson list size : {}", permissions.size());
				String customerLocation = setCustomerLocationValue(permissions);
				if (Utils.checkForValueInString(customerLocation)) {
					logger.info("Customer Location detail : {}", customerLocation);
					customerLocationMap.put("customer_location", customerLocation);
				}
			}
		} catch (Exception e) {
			logger.error("Error in getting customer location detail : {}", Utils.getStackTrace(e));
		}
		return customerLocationMap;
	}

	private String setCustomerLocationValue(Set<String> permissionSet) {
		String customerLocation = null;
		if (permissionSet.contains(ROLE_CC_HOME_WORK_LOCATION_view) && permissionSet.contains(ROLE_CC_LAST_LOCATION_view)) {
			customerLocation = iSystemConfigurationDao.getValueByName(CUSTOMER_LOCATION_ALL_VALUES);
		} else if (permissionSet.contains(ROLE_CC_HOME_WORK_LOCATION_view)) {
			customerLocation = iSystemConfigurationDao.getValueByName(CUSTOMER_HOME_WORK_LOCATION);
		} else if (permissionSet.contains(ROLE_CC_LAST_LOCATION_view)) {
			customerLocation = iSystemConfigurationDao.getValueByName(CUSTOMER_LAST_LOCATION);
		}
		return customerLocation;
	}

	@Override
	public Map<String, String> getCustomerCareUserList() {
		logger.info("Going to get customercare user for logged in user");
		Map<String, String> resultMap = new HashMap<>();
		JSONObject json = new JSONObject();
		List<JSONObject> userList = new ArrayList<>();
		String otherUserKey = null;
		String clientUserKey = null;
		String bbmUserKey = null;
		try {
			Set<String> permissions = customerInfo.getPermissions();
			if (permissions != null && !permissions.isEmpty()) {
				logger.info("Permisson list size : {}", permissions.size());
				List<String> userNameList = ConfigUtils.getStringList(CUSTOMER_USER_NAME_VALUE_LIST);
				bbmUserKey = setBBMUserData(json, userList, permissions, otherUserKey, clientUserKey, userNameList);
				clientUserKey = setClientUserData(json, userList, permissions, otherUserKey, bbmUserKey, userNameList);
				otherUserKey = setOtherUserData(json, userList, permissions, clientUserKey, bbmUserKey, userNameList);
				json.put(USER_LIST, userList);
				String resultString = json.toString();
				resultMap.put(RESULT, resultString);
				logger.info("Final customer user map : {}", resultMap);
			}
		} catch (Exception e) {
			logger.error("Error in getting customercare user list : {}", Utils.getStackTrace(e));
		}
		return resultMap;
	}

	private String setOtherUserData(JSONObject json, List<JSONObject> userList, Set<String> permissionSet, String clientUserKey, String bbmUserKey,
			List<String> userNameList) {
		String otherUserKey = null;
		if (permissionSet.contains(ROLE_CC_OTHER_USER_VIEW)) {
			String userKey = ConfigUtils.getStringList(CUSTOMER_USER_NAME_KEY_LIST).get(ForesightConstants.TWO);
			otherUserKey = userKey;
			JSONObject userJson = new JSONObject();
			userJson.put(CC_VALUE, userNameList.get(TWO));
			setCustomerCareUserList(json, userList, userJson, userKey, otherUserKey, clientUserKey, bbmUserKey);
		}
		return otherUserKey;
	}

	private String setClientUserData(JSONObject json, List<JSONObject> userList, Set<String> permissionSet, String otherUserKey, String bbmUserKey,
			List<String> userNameList) {
		String clientUserKey = null;
		if (permissionSet.contains(ROLE_CC_CLIENT_USER_VIEW)) {
			String userKey = ConfigUtils.getStringList(CUSTOMER_USER_NAME_KEY_LIST).get(ForesightConstants.ONE);
			clientUserKey = userKey;
			JSONObject userJson = new JSONObject();
			userJson.put(CC_VALUE, userNameList.get(ONE));
			setCustomerCareUserList(json, userList, userJson, userKey, otherUserKey, clientUserKey, bbmUserKey);
		}
		return clientUserKey;
	}

	private String setBBMUserData(JSONObject json, List<JSONObject> userList, Set<String> permissionSet, String otherUserKey, String clientUserKey,
			List<String> userNameList) {
		String bbmUserKey = null;
		if (permissionSet.contains(ROLE_CC_BBM_USER_VIEW)) {
			String userKey = ConfigUtils.getStringList(CUSTOMER_USER_NAME_KEY_LIST).get(ForesightConstants.ZERO);
			bbmUserKey = userKey;
			JSONObject userJson = new JSONObject();
			userJson.put(CC_VALUE, userNameList.get(ZERO));
			setCustomerCareUserList(json, userList, userJson, userKey, otherUserKey, clientUserKey, bbmUserKey);
		}
		return bbmUserKey;
	}

	private void setCustomerCareUserList(JSONObject json, List<JSONObject> userlist, JSONObject userJson, String userKey, String otherUserKey,
			String clientUserKey, String bbmUserKey) {
		try {
			if (Utils.checkForValueInString(userKey)) {
				json.put(DEFAULT_SELECTED_USER, userKey);
				userJson.put(CC_ID, userKey);
				userlist.add(userJson);
				if (ConfigUtils.getBoolean(IS_CUSTOMER_CARE_CLIENT_USER) && Utils.checkForValueInString(otherUserKey)
						&& userJson.get(CC_ID).equals(otherUserKey)) {
					userlist.remove(userJson);
					json.put(DEFAULT_SELECTED_USER, clientUserKey != null ? clientUserKey : bbmUserKey);
				} else if (ConfigUtils.getBoolean(IS_CUSTOMER_CARE_OTHER_USER) && Utils.checkForValueInString(clientUserKey)
						&& userJson.get(CC_ID).equals(clientUserKey)) {
					userlist.remove(userJson);
					json.put(DEFAULT_SELECTED_USER, otherUserKey != null ? otherUserKey : bbmUserKey);
				}
			}
		} catch (JSONException e) {
			logger.error("Error in setting customer care user list  : {}", Utils.getStackTrace(e));
		}
	}

	@Override
	public String getSystemConfigurationDataByName(String name) {
		logger.info("Going to get system configuration data by name : {}", name);
		String data = CustomerCareUtils.getSysConfMap().get(name);
		logger.info("data : {}  for name : {}", data, name);
		return data;
	}

	@Override
	public String getDeviceDataByModelName(String modelName) {
		logger.info("Going to get device data for : {}", modelName);
		String deviceData = null;
		Device device = CustomerCareUtils.getDeviceDataMap().get(modelName);
		if (device != null) {
			logger.info(" Device model : {} and Device image url : {}", device.getModelName(), device.getImageUrl());
			deviceData = new Gson().toJson(device);
		}
		return deviceData;
	}

	@Override
	public Double getColorMapData(Integer colorValue) {
		Double data = null;
		try {
			data = CustomerCareUtils.getLoadColorMap().get(colorValue);
			logger.info("color map data : {} for value : {}", data, colorValue);
		} catch (Exception e) {
			logger.error("Error in getting color map data Exception : {}", Utils.getStackTrace(e));
		}
		return data;
	}

	@Override
	@Transactional
	public Boolean updateLastLocationForDeviceId(String deviceId) {
		logger.info("Going to update last location for device id : {}", deviceId);
		Boolean status = FALSE;
		try {
			String type = CustomerCareUtils.getValidatedData(ConfigUtils.getString(CC_NV_DATA_TYPE), NV_TYPE);
			NVCustomerCareDataWrapper dataWrapper = getNVActiveDeviceDataByImsiOrDeviceId(type, null, deviceId);
			if (validateNVWrapperData(dataWrapper)) {
				logger.info("Last speed test captured time  : {}", new Date(dataWrapper.getCapturedOn()));
				status = updateNVLastLocationByDeviceId(dataWrapper.getDeviceId(), dataWrapper.getLatitude(), dataWrapper.getLongitude(),
						dataWrapper.getCapturedOn(), dataWrapper.getCgi());
			}
		} catch (Exception e) {
			logger.error("Error in updating last location for device id :{} and Exception : {}", deviceId, Utils.getStackTrace(e));
		}
		return status;
	}

	@Override
	public CustomerCareUserWrapper getCustomerCareUserincontext() {
		logger.info("Going to get customer care user in context data");
		CustomerCareUserWrapper wrapper = new CustomerCareUserWrapper();
		try {
			User user = userInContext.getUserInContextnew();
			wrapper = setDataInCustomerCareUserInContextWrapper(user);
		} catch (Exception e) {
			logger.error("Exception in getCustomerCareUserincontext : {}", Utils.getStackTrace(e));
		}
		return wrapper;
	}

	private CustomerCareUserWrapper setDataInCustomerCareUserInContextWrapper(User user) {
		logger.info("Inside setDataInUserInContextWrapper ");
		CustomerCareUserWrapper wrapper = new CustomerCareUserWrapper();
		wrapper.setUserid(user.getUserid());
		wrapper.setContactNumber(user.getContactNumber());
		wrapper.setCreationTime(user.getCreationTime());
		wrapper.setEmail(user.getEmail());
		wrapper.setUserName(user.getUserName());
		wrapper.setFirstName(user.getFirstName());
		wrapper.setLastName(user.getLastName());
		wrapper.setModificationTime(user.getModificationTime());
		wrapper.setUserSearch(user.getUserName());
		wrapper.setImagePath(user.getImagePath());
//		wrapper.setVendor(customerinfo.getVendor());
		wrapper.setActiveRole(setUserRoleGeographyForActiveRole(user.getUserRole(), UmUtils.getDominantRole(user)));
		wrapper.setRole(convertUserRoleGeographyDetails(removeActiveUserRole(user.getUserRole())));
		return wrapper;
	}

	private List<UserRoleGeographyDetails> convertUserRoleGeographyDetails(Set<UserRole> userRoleSet) {
		logger.info("Inside ConvertUserRoleGeographyDetails ");
		ArrayList<UserRoleGeographyDetails> userRoleGeographyDetailsList = new ArrayList<>();
		if (userRoleSet != null) {
			Iterator<UserRole> userRole = userRoleSet.iterator();
			while (userRole.hasNext()) {
				UserRole ur = userRole.next();
				userRoleGeographyDetailsList.add(setUserRoleGeography(ur));
			}
		}
		logger.info("UserRoleGeographyDetailsList {} ", userRoleGeographyDetailsList.size());
		return userRoleGeographyDetailsList;
	}

	private Set<UserRole> removeActiveUserRole(Set<UserRole> userRoleSet) {
		logger.info("Inside removeActiveUserRole");
		HashSet<UserRole> newuserrole = new HashSet<>();
		Iterator<UserRole> userRole = userRoleSet.iterator();
		while (userRole.hasNext()) {
			UserRole ur = userRole.next();
			newuserrole.add(ur);
		}
		return newuserrole;
	}

	private UserRoleGeographyDetails setUserRoleGeographyForActiveRole(Set<UserRole> userRoleSet, Role activeRole) {
		logger.info("Inside setUserRoleGeographyForActiveRole ");
		UserRoleGeographyDetails userRoleGeographyDetail = new UserRoleGeographyDetails();
		Iterator<UserRole> userRole = userRoleSet.iterator();
		while (userRole.hasNext()) {
			UserRole ur = userRole.next();
			if (ur.getRole() != null) {
				logger.info("Active Role : {} ", activeRole.getRoleId(), " and role id  : {} ", ur.getRole().getRoleId());
				if (ur.getRole().getRoleId().intValue() == activeRole.getRoleId().intValue()) {
					userRoleGeographyDetail = setUserRoleGeography(ur);
				}
			}
		}
		return userRoleGeographyDetail;
	}

	private UserRoleGeographyDetails setUserRoleGeography(UserRole userRole) {
		logger.info("inside  setUserRoleGeography active ");
		UserRoleGeographyDetails userRoleGeographyDetails = setGeography(userRole.getUserRoleGeography(), userRole.getRole().getLevelType(),
				userRole.getRole().getWorkSpace().getGeotype());
		userRoleGeographyDetails.setRole(userRole.getRole());
		userRoleGeographyDetails.setUserRoleId(userRole.getId());
		userRoleGeographyDetails.setUserId(userRole.getUser().getUserid());
		// userRoleGeographyDetails.setDefaultModuleId(userRole.getRole().getModule().getModuleid());
		return userRoleGeographyDetails;
	}

	private UserRoleGeographyDetails setGeography(Set<UserRoleGeography> userRoleGeography, String levelType, String geoType) {
		logger.info("Inside setGeography ");
		UserRoleGeographyDetails userRoleGeographyDetails = new UserRoleGeographyDetails();
		if (userRoleGeography != null && geoType != null) {
			logger.info("Inside setGeography GEO_TYPE : {}", geoType);
			byte networkType = MINUS_ONE;
			networkType = setNetworkType(geoType, networkType);
			switch (networkType) {
			case ZERO:
				setNetworkTypeGeography(userRoleGeographyDetails, userRoleGeography, levelType);
				break;
			case ONE:
				setSalesTypeGeography(userRoleGeographyDetails, userRoleGeography, levelType);
				break;
			default:
				logger.info("No network type");
			}
		}
		logger.info("USER_GEO_DETAILS {} ", userRoleGeographyDetails);
		return userRoleGeographyDetails;
	}

	private byte setNetworkType(String geoType, byte networkType) {
		switch (geoType.hashCode()) {
		case -1733499378:
			if (geoType.equals(UmConstants.NETWORK)) {
				networkType = ZERO;
			}
			break;
		case 78663916:
			if (geoType.equals(UmConstants.SALES)) {
				networkType = ONE;
			}
			break;
		default:
			logger.info("default case");
		}
		return networkType;
	}

	private void setNetworkTypeGeography(UserRoleGeographyDetails userRoleGeographyDetails, Set<UserRoleGeography> userRoleGeography,
			String levelType) {
		ArrayList<GeographyL1> geographyL1 = new ArrayList<>();
		ArrayList<GeographyL2> geographyL2 = new ArrayList<>();
		ArrayList<GeographyL3> geographyL3 = new ArrayList<>();
		ArrayList<GeographyL4> geographyL4 = new ArrayList<>();
		ArrayList<OtherGeography> otherGeography = new ArrayList<>();
		Integer activeGeographyId = null;
		if (userRoleGeography != null && levelType != null) {
			logger.info("LEVEL TYPE {}", levelType);
			byte activeGeography = MINUS_ONE;
			activeGeography = setActiveGeographyValue(levelType, activeGeography);
			activeGeographyId = getGeoByActiveGeoValue(userRoleGeography, geographyL1, geographyL2, geographyL3, geographyL4, otherGeography,
					activeGeographyId, activeGeography);
		}
		userRoleGeographyDetails.setGeographyL1(geographyL1);
		userRoleGeographyDetails.setGeographyL2(geographyL2);
		userRoleGeographyDetails.setGeographyL3(geographyL3);
		userRoleGeographyDetails.setGeographyL4(geographyL4);
		userRoleGeographyDetails.setOtherGeography(otherGeography);
		logger.info("ActiveGeographyID :{}", activeGeographyId);
		userRoleGeographyDetails.setActiveGeographyId(activeGeographyId);
	}

	private Integer getGeoByActiveGeoValue(Set<UserRoleGeography> userRoleGeography, ArrayList<GeographyL1> geographyL1,
			ArrayList<GeographyL2> geographyL2, ArrayList<GeographyL3> geographyL3, ArrayList<GeographyL4> geographyL4,
			ArrayList<OtherGeography> otherGeography, Integer activeGeographyId, byte activeGeography) {
		switch (activeGeography) {
		case ZERO:
			activeGeographyId = setGeographyL1(userRoleGeography, geographyL1, activeGeographyId);
			break;
		case ONE:
			activeGeographyId = setGeographyL2(userRoleGeography, geographyL1, geographyL2, activeGeographyId);
			break;
		case TWO:
			activeGeographyId = setGeographyL3(userRoleGeography, geographyL1, geographyL2, geographyL3, activeGeographyId);
			break;
		case THREE:
			activeGeographyId = setGeographyL4(userRoleGeography, geographyL1, geographyL2, geographyL3, geographyL4, activeGeographyId);
			break;
		case FOUR:
			break;
		default:
			activeGeographyId = setOthergeography(userRoleGeography, otherGeography, activeGeographyId);
		}
		return activeGeographyId;
	}

	private byte setActiveGeographyValue(String levelType, byte activeGeography) {
		switch (levelType.hashCode()) {
		case 2405:

			if (levelType.equals(UmConstants.L1)) {
				activeGeography = ZERO;
			}
			break;
		case 2406:
			if (levelType.equals(UmConstants.L2)) {
				activeGeography = ONE;
			}
			break;
		case 2407:
			if (levelType.equals(UmConstants.L3)) {
				activeGeography = TWO;
			}
			break;
		case 2408:
			if (levelType.equals(UmConstants.L4)) {
				activeGeography = THREE;
			}
			break;
		case 77271:
			if (levelType.equals(UmConstants.NHQ)) {
				activeGeography = FOUR;
			}
			break;
		default:
			logger.info("Default level type");
		}
		return activeGeography;
	}

	private Integer setOthergeography(Set<UserRoleGeography> userRoleGeography, List<OtherGeography> otherGeography, Integer activeGeographyId) {
		logger.info(" Inside setOthergeography ");
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg3 = userRoleGeography.iterator(); arg3.hasNext(); otherGeography.add(urg.getOtherGeography())) {
			urg = arg3.next();
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getOtherGeography().getId();
			}
		}
		return activeGeographyId;
	}

	private void setSalesTypeGeography(UserRoleGeographyDetails userRoleGeographyDetails, Set<UserRoleGeography> userRoleGeography,
			String levelType) {
		ArrayList<SalesL1> salesL1 = new ArrayList<>();
		ArrayList<SalesL2> salesL2 = new ArrayList<>();
		ArrayList<SalesL3> salesL3 = new ArrayList<>();
		ArrayList<SalesL4> salesL4 = new ArrayList<>();
		ArrayList<OtherGeography> otherGeography = new ArrayList<>();
		Integer activeGeographyId = null;
		if (userRoleGeography != null && levelType != null) {
			byte activeGeo = MINUS_ONE;
			activeGeo = setActiveGeographyValue(levelType, activeGeo);
			activeGeographyId = setSalesGeoByActiveGeoValue(userRoleGeography, salesL1, salesL2, salesL3, salesL4, otherGeography, activeGeographyId,
					activeGeo);
		}
		userRoleGeographyDetails.setSalesl1(salesL1);
		userRoleGeographyDetails.setSalesl2(salesL2);
		userRoleGeographyDetails.setSalesl3(salesL3);
		userRoleGeographyDetails.setSalesl4(salesL4);
		userRoleGeographyDetails.setOtherGeography(otherGeography);
		userRoleGeographyDetails.setActiveGeographyId(activeGeographyId);
	}

	private Integer setSalesGeoByActiveGeoValue(Set<UserRoleGeography> userRoleGeography, ArrayList<SalesL1> salesL1, ArrayList<SalesL2> salesL2,
			ArrayList<SalesL3> salesL3, ArrayList<SalesL4> salesL4, ArrayList<OtherGeography> otherGeography, Integer activeGeographyId,
			byte activeGeo) {
		switch (activeGeo) {
		case ZERO:
			activeGeographyId = setSalesL1(userRoleGeography, salesL1, activeGeographyId);
			break;
		case ONE:
			activeGeographyId = setsalesL2(userRoleGeography, salesL1, salesL2, activeGeographyId);
			break;
		case TWO:
			activeGeographyId = setSalesL3(userRoleGeography, salesL1, salesL2, salesL3, activeGeographyId);
			break;
		case THREE:
			activeGeographyId = setsalesL4(userRoleGeography, salesL1, salesL2, salesL3, salesL4, activeGeographyId);
			break;
		case FOUR:
			break;
		default:
			activeGeographyId = setOthergeography(userRoleGeography, otherGeography, activeGeographyId);
		}
		return activeGeographyId;
	}

	private Integer setGeographyL1(Set<UserRoleGeography> userRoleGeography, List<GeographyL1> geographyL1, Integer activeGeographyId) {
		logger.info(" Inside setGeographyL1");
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg3 = userRoleGeography.iterator(); arg3.hasNext(); geographyL1.add(urg.getGeographyL1())) {
			urg = arg3.next();
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getGeographyL1().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setSalesL1(Set<UserRoleGeography> userRoleGeography, List<SalesL1> salesL1, Integer activeGeographyId) {
		logger.info(" Inside setSalesL1");
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg3 = userRoleGeography.iterator(); arg3.hasNext(); salesL1.add(urg.getSalesL1())) {
			urg = arg3.next();
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getSalesL1().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setGeographyL2(Set<UserRoleGeography> userRoleGeography, List<GeographyL1> geographyL1, List<GeographyL2> geographyL2,
			Integer activeGeographyId) {
		logger.info(" Inside setGeographyL2");
		Boolean isL1 = Boolean.valueOf(TRUE);
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg5 = userRoleGeography.iterator(); arg5.hasNext(); geographyL2.add(urg.getGeographyL2())) {
			urg = arg5.next();
			if (isL1.booleanValue() && urg.getGeographyL2() != null) {
				geographyL1.add(urg.getGeographyL2().getGeographyL1());
				isL1 = Boolean.valueOf(FALSE);
			}
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getGeographyL2().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setsalesL2(Set<UserRoleGeography> userRoleGeography, List<SalesL1> salesL1, List<SalesL2> salesL2, Integer activeGeographyId) {
		logger.info("Inside setSalesL2");
		Boolean isL1 = Boolean.valueOf(TRUE);
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg5 = userRoleGeography.iterator(); arg5.hasNext(); salesL2.add(urg.getSalesL2())) {
			urg = arg5.next();
			if (isL1.booleanValue() && urg.getSalesL2() != null) {
				salesL1.add(urg.getSalesL2().getSalesL1());
				isL1 = Boolean.valueOf(FALSE);
			}
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getSalesL2().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setGeographyL3(Set<UserRoleGeography> userRoleGeography, List<GeographyL1> geographyL1, List<GeographyL2> geographyL2,
			List<GeographyL3> geographyL3, Integer activeGeographyId) {
		logger.info("Inside setGeographyL3");
		Boolean isL2 = Boolean.valueOf(TRUE);
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg6 = userRoleGeography.iterator(); arg6.hasNext(); geographyL3.add(urg.getGeographyL3())) {
			urg = arg6.next();
			if (isL2.booleanValue()) {
				geographyL1.add(urg.getGeographyL3().getGeographyL2().getGeographyL1());
				geographyL2.add(urg.getGeographyL3().getGeographyL2());
				isL2 = Boolean.valueOf(FALSE);
			}
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getGeographyL3().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setSalesL3(Set<UserRoleGeography> userRoleGeography, List<SalesL1> salesL1, List<SalesL2> salesL2, List<SalesL3> salesL3,
			Integer activeGeographyId) {
		logger.info("Inside setSalesL3");
		Boolean isL2 = Boolean.valueOf(TRUE);
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg6 = userRoleGeography.iterator(); arg6.hasNext(); salesL3.add(urg.getSalesL3())) {
			urg = arg6.next();
			if (isL2.booleanValue() && urg.getSalesL3() != null && urg.getSalesL3().getSalesL2() != null) {
				salesL1.add(urg.getSalesL3().getSalesL2().getSalesL1());
				salesL2.add(urg.getSalesL3().getSalesL2());
				isL2 = Boolean.valueOf(FALSE);
			}
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getSalesL3().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setGeographyL4(Set<UserRoleGeography> userRoleGeography, List<GeographyL1> geographyL1, List<GeographyL2> geographyL2,
			List<GeographyL3> geographyL3, List<GeographyL4> geographyL4, Integer activeGeographyId) {
		logger.info("Inside setGeographyL4");
		Boolean isL3 = Boolean.valueOf(TRUE);
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> arg7 = userRoleGeography.iterator(); arg7.hasNext(); geographyL4.add(urg.getGeographyL4())) {
			urg = arg7.next();
			if (isL3.booleanValue() && urg.getGeographyL4() != null && urg.getGeographyL4().getGeographyL3() != null) {
				geographyL1.add(urg.getGeographyL4().getGeographyL3().getGeographyL2().getGeographyL1());
				geographyL2.add(urg.getGeographyL4().getGeographyL3().getGeographyL2());
				geographyL3.add(urg.getGeographyL4().getGeographyL3());
				isL3 = Boolean.valueOf(FALSE);
			}
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getGeographyL4().getId();
			}
		}
		return activeGeographyId;
	}

	private Integer setsalesL4(Set<UserRoleGeography> userRoleGeography, List<SalesL1> salesL1, List<SalesL2> salesL2, List<SalesL3> salesL3,
			List<SalesL4> salesL4, Integer activeGeographyId) {
		logger.info(" Inside setSalesL4");
		Boolean isL3 = Boolean.valueOf(TRUE);
		UserRoleGeography urg;
		for (Iterator<UserRoleGeography> userRoleGeo = userRoleGeography.iterator(); userRoleGeo.hasNext(); salesL4.add(urg.getSalesL4())) {
			urg = userRoleGeo.next();
			if (isL3.booleanValue() && urg.getSalesL4() != null && urg.getSalesL4().getSalesL3() != null
					&& urg.getSalesL4().getSalesL3().getSalesL2() != null) {
				salesL1.add(urg.getSalesL4().getSalesL3().getSalesL2().getSalesL1());
				salesL2.add(urg.getSalesL4().getSalesL3().getSalesL2());
				salesL3.add(urg.getSalesL4().getSalesL3());
				isL3 = Boolean.valueOf(FALSE);
			}
			if (urg.getActive().booleanValue()) {
				activeGeographyId = urg.getSalesL4().getId();
			}
		}
		return activeGeographyId;
	}

	@SuppressWarnings("serial")
	@Override
	public NVCustomerCareDataWrapper getLatestSpeedTestDataByDeviceId(String deviceId, Double latitude, Double longitude) {
		logger.info("Going to get latest speed test data By deviceid : {} , latitude {} and longitude {} ", deviceId, latitude, longitude);
		NVCustomerCareDataWrapper nvDataWrapper = new NVCustomerCareDataWrapper();
		try {
			String url = CustomerCareUtils.appendBaseURL(ConfigUtils.getString(GET_LATEST_SPEED_TEST_DATA_URL));
			List<String> valueList = Arrays.asList(deviceId, latitude.toString(), longitude.toString());
			String query = GenericMapUtils.createGenericQuery(valueList, DEVICE_ID, LATITUDE, LONGITUDE);
			String bashUrl = CustomerCareUtils.appendData(url, query);
			logger.info("latest speed test data url : {}", bashUrl);
			String response = CustomerCareUtils.makeGETRequest(bashUrl);
			if (Utils.checkForValueInString(response)) {
				nvDataWrapper = CustomerCareUtils.parseGsonData(response, new TypeToken<NVCustomerCareDataWrapper>() {
				});
				setBtsCodeAndSiteInfoBycgi(nvDataWrapper);
			}
			logger.info("nvDataWrapper  :{}", nvDataWrapper);
		} catch (Exception e) {
			logger.error("Error while getting latest speed test data from ms: {} ", Utils.getStackTrace(e));
		}
		return nvDataWrapper;
	}

	@SuppressWarnings("serial")
	private Map<String, String> getDataFromMS(String methodURL) {
		Map<String, String> data = null;
		try {
			String url = CustomerCareUtils.appendBaseURL(methodURL);
			logger.info("url for initializing cc data : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				data = CustomerCareUtils.parseGsonData(response, new TypeToken<Map<String, String>>() {
				});
				logger.info("Resonse from Micro Service : {}", data);
			}
		} catch (HttpException e) {
			logger.error("Error in getting data from ms : {}", Utils.getStackTrace(e));
		}
		return data;
	}

	@Override
	public Map<String, String> initializeData(String dataFor) {
		logger.info("Going to initialize Customer Care Data for : {}", dataFor);
		try {
			if (dataFor.equalsIgnoreCase(JSI) && ConfigUtils.getString(CC_INITIALIZE_JSI_DATA) != null) {
				return getDataFromMS(ConfigUtils.getString(CC_INITIALIZE_JSI_DATA));
			}
			if (dataFor.equalsIgnoreCase(BSP) && ConfigUtils.getString(CC_INITIALIZE_BSP_DATA) != null) {
				return getDataFromMS(ConfigUtils.getString(CC_INITIALIZE_BSP_DATA));
			}
			if (dataFor.equalsIgnoreCase(PM) && ConfigUtils.getString(CC_INITIALIZE_PM_DATA) != null) {
				return getDataFromMS(ConfigUtils.getString(CC_INITIALIZE_PM_DATA));
			}
			if (dataFor.equalsIgnoreCase(SITE_SYS) && ConfigUtils.getString(CC_INITIALIZE_SITES_SYS_DATA) != null) {
				return getDataFromMS(ConfigUtils.getString(CC_INITIALIZE_SITES_SYS_DATA));
			}
			// if (dataFor.equalsIgnoreCase(GEOGRAPHY)) {
			// geographyMap = new HashMap<>();
			// logger.info("Going to refresh Geography Map in tomcat memory : {}",
			// geographyMap.size());
			// return ImmutableMap.of(ForesightConstants.MESSAGE,
			// ForesightConstants.SUCCESS);
			// }
		} catch (Exception e) {
			logger.error("Error in initializing Customer Care data Exception : {}", Utils.getStackTrace(e));
			return new HashMap<>();
		}
		return new HashMap<>();
	}

	@Override
	public String getTicketJson() {
		logger.info("Going to get ticket default json");
		try {
			String ticketJsonKey = CustomerCareUtils.getValidatedData(ConfigUtils.getString(DEFAULT_TICKET_JSON_KEY), TICKET_JSON);
			String ticketJson = iSystemConfigurationDao.getValueByName(ticketJsonKey);
			if (Utils.checkForValueInString(ticketJson)) {
				return ticketJson;
			} else {
				return TICKET_JSON_NOT_AVAILABLE;
			}
		} catch (Exception exception) {
			logger.error("Error in getting json for ticket {}", Utils.getStackTrace(exception));
			return TICKET_JSON_NOT_AVAILABLE;
		}
	}

	@Override
	public Map<String, Map<String, Long>> getLatestDataTime() {
		logger.info("Going to get latest data time map...");
		Map<String, Map<String, Long>> latestDataTimeMap = new HashMap<>();
		try {
			latestDataTimeMap.put(SNCMAP, getSNCDataMap());
			latestDataTimeMap.put(PMMAP, getPMDataMap());
			latestDataTimeMap.put(PCMAP, getPCDataMap());
			logger.info("Final MAP for latest date for data :{}", latestDataTimeMap);
		} catch (Exception exception) {
			logger.error("Error in getting latest data time, Exception : {}", Utils.getStackTrace(exception));
		}
		return latestDataTimeMap;
	}

	private Map<String, Long> getPCDataMap() {
		try {
			Map<String, Long> pcMap = new HashMap<>();
			getPCMapTime(pcMap, ConfigUtils.getString(CC_PL_RSRP_KEY), ConfigUtils.getString(CC_SHOW_DATA_FOR_BAND));
			getPCMapTime(pcMap, ConfigUtils.getString(CC_OA_RSRP_KEY), ConfigUtils.getString(CC_SHOW_DATA_FOR_BAND));
			return pcMap;
		} catch (DaoException daoException) {
			logger.error("Error in getting PC Data time DaoException : {}", Utils.getStackTrace(daoException));
			return null;
		} catch (Exception exception) {
			logger.error("Error in getting PC Data Exception : {}", Utils.getStackTrace(exception));
			return null;
		}
	}

	private void getPCMapTime(Map<String, Long> pcMap, Object... strings) {
		String pcDataKey = CustomerCareUtils.appendData(strings);
		pcMap.put((String) strings[ZERO],
				Utils.parseStringToDate(iSystemConfigurationDao.getValueByName(pcDataKey), DATE_FORMAT_YYYY_MM_DD).getTime());
	}

	private Map<String, Long> getSNCDataMap() {
		try {
			Map<String, Long> sncMap = new HashMap<>();
			String sncDataKey = CustomerCareUtils.appendData(ConfigUtils.getString(CC_SNC_RSRP_KEY), ConfigUtils.getString(CC_SHOW_DATA_FOR_BAND));
			logger.info("SNC Data Key : {}", sncDataKey);
			SystemConfiguration sysconfSNC = iSystemConfigurationDao.getSystemConfigurationByName(sncDataKey).get(ZERO);

			if (CustomerCareUtils.checkNullObject(sysconfSNC)) {
				JobHistory jobHistory = iJobHistoryDao.getJobhistoryByNameandValue(sysconfSNC.getName(), sysconfSNC.getValue());
				if (CustomerCareUtils.checkNullObject(jobHistory)) {
					sncMap.put(ConfigUtils.getString(CC_SNC_RSRP_KEY), jobHistory.getCreationTime().getTime());
				}
			}
			return sncMap;
		} catch (DaoException daoException) {
			logger.error("Error in getting SNC Data time DaoException : {}", Utils.getStackTrace(daoException));
			return null;
		} catch (Exception exception) {
			logger.error("Error in getting SNC Data Exception : {}", Utils.getStackTrace(exception));
			return null;
		}
	}

	private Map<String, Long> getPMDataMap() {
		Map<String, Long> pmMap = new HashMap<>();
		try {
			Map<Vendor, String> vendorKeyMap = getVendorKeyMapData();
			if (Utils.isValidMap(vendorKeyMap)) {
				vendorKeyMap.forEach((key, value) -> {
					String date = getSystemConfigurationData(value);
					logger.info("PM Congestion Date : {} and str : {}, TimeStamp : {}", Utils.parseStringToDate(date, KPI_BBH_DATA_DATE_FORMAT), date,
							Utils.parseStringToDate(date, KPI_BBH_DATA_DATE_FORMAT).getTime());

					pmMap.put(key.toString(), Utils.parseStringToDate(date, KPI_BBH_DATA_DATE_FORMAT).getTime());
				});
			} else {
				logger.error("vendorKeyMap is null");
			}
			return pmMap;
		} catch (DaoException daoException) {
			logger.error("Error in getting PM data Time  DaoException : {}", Utils.getStackTrace(daoException));
			return null;
		} catch (Exception exception) {
			logger.error("Error in getting PM Map Exception : {}", Utils.getStackTrace(exception));
			return null;
		}
	}

	@SuppressWarnings("serial")
	@Override
	public List<NEHaveAlarm> isSiteHaveAlarms(List<String> neIds, HttpServletRequest request) {
		logger.info("Going to get outage alarm for neIds : {} ", neIds.size());
		List<NEHaveAlarm> outageHistoryList = new ArrayList<>();
		try {
			String url = ConfigUtils.getString(CC_OUTAGE_VISUALIZATOIN_URL);

			MockHttpServletRequest httpServletRequest = makeMockServletRequestURL(request, url);

			logger.info("isSiteHaveAlarm Found URL : {}", Utils.getDropwizardUrlWithPrefix(httpServletRequest));
			String response = CustomerCareUtils.makePOSTRequest(Utils.getDropwizardUrlWithPrefix(httpServletRequest), getEntityForURL(neIds));
			if (response != null && !response.isEmpty()) {
				outageHistoryList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<NEHaveAlarm>>() {
				});
				logger.info("isSiteHaveAlarms response : {}", outageHistoryList.size());
			}
		} catch (Exception e) {
			logger.error("Error in getting outage alarm for visualization : {}", Utils.getStackTrace(e));
		}
		return outageHistoryList;
	}

	@SuppressWarnings("serial")
	@Override
	public List<AlarmDataWrapper> getAlarmHistoryForNEId(String neId, HttpServletRequest request) {
		logger.info("Going to get alarm history for neid : {}", neId);
		List<AlarmDataWrapper> outageHistoryList = null;
		try {
			String url = CustomerCareUtils.appendData(ConfigUtils.getString(CC_OUTAGE_ALARM_HISTORY_URL), neId);

			MockHttpServletRequest httpServletRequest = makeMockServletRequestURL(request, url);

			logger.info("Alarm history list URL : {}", Utils.getDropwizardUrlWithPrefix(httpServletRequest));

			String response = CustomerCareUtils.makeGETRequest(Utils.getDropwizardUrlWithPrefix(httpServletRequest));
			if (response != null && !response.isEmpty()) {
				outageHistoryList = CustomerCareUtils.parseGsonData(response, new TypeToken<List<AlarmDataWrapper>>() {
				});
				logger.info("Alarm History for neid list : {}", outageHistoryList.size());
				return outageHistoryList;
			}
		} catch (Exception e) {
			logger.error("Error in getting alarm history for neid : {} Exception : {}", neId, Utils.getStackTrace(e));
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, Integer> getAlarmHistoryCountForNEId(String neId, HttpServletRequest request) {
		logger.info("Going to get alarm history count for neid : {} ", neId);
		try {
			String url = CustomerCareUtils.appendData(ConfigUtils.getString(CC_OUTAGE_ALARM_HISTORY_COUNT_URL), neId);

			MockHttpServletRequest httpServletRequest = makeMockServletRequestURL(request, url);

			logger.info("Alarm history count URL : {}", Utils.getDropwizardUrlWithPrefix(httpServletRequest));

			String response = CustomerCareUtils.makeGETRequest(Utils.getDropwizardUrlWithPrefix(httpServletRequest));
			if (response != null && !response.isEmpty()) {
				Integer count = CustomerCareUtils.parseGsonData(response, new TypeToken<Integer>() {
				});
				logger.info("Outage alarm history count : {}", count);
				return ImmutableMap.of(ForesightConstants.COUNT, count);
			}
		} catch (Exception e) {
			logger.error("Error in getting alarm history count for neid : {} Exception : {}", neId, Utils.getStackTrace(e));
		}
		return new HashMap<>();
	}

	@SuppressWarnings("serial")
	@Override
	public List<CustomerCareSiteWrapper> searchOnAirSitesByRadius(Double latitude, Double longitude) {
		logger.info("Going to get OnAir Sites By latitude {} longitude {} @searchOnAirSitesByRadius ", latitude, longitude);
		List<CustomerCareSiteWrapper> nearestSiteList = new ArrayList<>();
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_ON_AIR_SITES_BY_RADIUS)),
					ForesightConstants.FORWARD_SLASH, latitude, ForesightConstants.FORWARD_SLASH, longitude);
			logger.info("search on air sites by radius  url : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				nearestSiteList = new Gson().fromJson(response, new TypeToken<List<CustomerCareSiteWrapper>>() {
				}.getType());
			}
			logger.info("Total Onair Sites Present in Radius {} ", nearestSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting onair sites by Radius from ms: {} ", Utils.getStackTrace(e));
		}
		return nearestSiteList;
	}

	@Override
	public List<CustomerCareSiteWrapper> getOnAirSitesByRadius(Double latitude, Double longitude) {
		logger.info("Going to get OnAir and Planned Sites By latitude {} longitude {} from hbase @getOnAirSitesByRadius ", latitude, longitude);
		List<CustomerCareSiteWrapper> nearestSiteList = new ArrayList<>();
		try {
			nearestSiteList = getNearestOnAirSitesByPinLocation(latitude, longitude, TRUE);
			logger.info("Total Onair Sites from ms for radius {} ", nearestSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting onair or planned sites by LatLong : {} ", Utils.getStackTrace(e));

		}
		return nearestSiteList;
	}

	@SuppressWarnings("serial")
	@Override
	public List<CustomerCareSiteWrapper> searchPlannedSitesByRadius(Double latitude, Double longitude) {
		logger.info("Going to get Planned Sites By latitude {} longitude {} ", latitude, longitude);
		List<CustomerCareSiteWrapper> nearestPlannedSiteList = new ArrayList<>();
		try {
			String url = CustomerCareUtils.appendData(CustomerCareUtils.appendBaseURL(ConfigUtils.getString(CC_GET_PLANNED_SITES_BY_RADIUS)),
					ForesightConstants.FORWARD_SLASH, latitude, ForesightConstants.FORWARD_SLASH, longitude);
			logger.info("search planned sites url : {}", url);
			String response = CustomerCareUtils.makeGETRequest(url);
			if (Utils.checkForValueInString(response)) {
				logger.info("Planned site responose : {}", response.length());
				nearestPlannedSiteList = new Gson().fromJson(response, new TypeToken<List<CustomerCareSiteWrapper>>() {
				}.getType());
			}
			logger.info("Total Planned Sites from app server by radius {} ", nearestPlannedSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting onair sites by LatLong from ms : {} ", Utils.getStackTrace(e));
		}
		return nearestPlannedSiteList;
	}

	@Override
	public String getPlannedSitesByRadius(Double latitude, Double longitude) {
		logger.info("Going to get OnAir and Planned Sites By latitude {} longitude {} @getPlannedSitesByRadius", latitude, longitude);
		String plannedSiteList = null;
		try {
			List<CustomerCareSiteWrapper> nearestPlannedSiteList = null;
			nearestPlannedSiteList = getNearestPlannedSitesByPinLocation(latitude, longitude, TRUE);
			if (Utils.isValidList(nearestPlannedSiteList)) {
				logger.info("Total Planned Sites from ms for radius {} ", nearestPlannedSiteList.size());
				plannedSiteList = new Gson().toJson(nearestPlannedSiteList);
			}
		} catch (Exception e) {
			logger.error("Error while getting planned sites by LatLong : {} ", Utils.getStackTrace(e));
		}
		return plannedSiteList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional
	public Map getCRDetailsForNEIds(List<String> woCategory, String columnKey, List<String> cmStatusList, List<String> neIds, Long startTime, Long endTime) {
		logger.info("Going to get CR details for woCategory : {},columnKey : {} , cmStatus : {},  startTime : {}, endTime : {}", woCategory, columnKey, cmStatusList, startTime, endTime);
		Map crDetailMap = new HashMap();
		Set<String> neIdSet = new HashSet();
		try {
			List<Long> dateList = new ArrayList();
			List<WOCategory> woCategoryList = getWOCategoryList(woCategory);
			List<CMChangeRequest> cmChangeRequestList = cmChangeRequestDao.getCRDetailsForNEIds(woCategoryList, columnKey, cmStatusList, neIds, startTime, endTime);
			if (cmChangeRequestList != null && !cmChangeRequestList.isEmpty()) {
				logger.info("Total CRDetails retrieved from CMChangeRequest : {}", cmChangeRequestList.size());
				for (CMChangeRequest cmChangeRequest : cmChangeRequestList) {
					try {
						if (cmChangeRequest.getNetworkElement() != null) {
							neIdSet.add(cmChangeRequest.getNetworkElement().getNeId());
						}
						Map<String, String> metaData = cmChangeRequest.getBpmWorkorder().getWorkOrderMeta();
						if (metaData != null && !metaData.get("totalSATime").equalsIgnoreCase("0")) {
							logger.info("BPMNWorkorder id : {}", cmChangeRequest.getBpmWorkorder().getId());
							dateList.add(startTime);
							dateList.add(endTime);
						}
					} catch (Exception exception) {
						logger.error("Error in getting modified CRDetails list. Message : {}", exception.getMessage());
					}
				}
				logger.info("Total unique sites count : {}", neIdSet.size());
				logger.info("dateList : {}", dateList);
				if (dateList != null && !dateList.isEmpty()) {
					crDetailMap.put("minTime", Collections.min(dateList));
					crDetailMap.put("maxTime", Collections.max(dateList));
					crDetailMap.put("count", neIdSet.size());
				}
				logger.info("Map : {}", crDetailMap);
			}
		} catch (Exception exception) {
			logger.error("Error in getting CRDetails for woCategory : {} , columnKey : {} , cmStatus : {},  startTime : {}, endTime : {}. Exception : {}", woCategory, columnKey, cmStatusList,
					startTime, endTime, Utils.getStackTrace(exception));
		}
		return crDetailMap;
	}
	
	
	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	private List<WOCategory> getWOCategoryList(List<String> woCategories) {
		logger.info("Going to get WOCategory enum list");
		List<WOCategory> woCategoryList = new ArrayList();
		for (String woCategory : woCategories) {
			try {
				woCategoryList.add(WOCategory.valueOf(woCategory));
			} catch (Exception exception) {
				logger.info("Error in getting WOCategory enum list. Message : {}", exception.getMessage());
			}
		}
		return woCategoryList;
	}

}
