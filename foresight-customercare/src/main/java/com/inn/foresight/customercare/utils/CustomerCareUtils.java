package com.inn.foresight.customercare.utils;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.inn.commons.configuration.ConfigUtils;
import com.inn.commons.http.HttpException;
import com.inn.commons.http.HttpGetRequest;
import com.inn.commons.http.HttpPostRequest;
import com.inn.commons.maps.nns.NNS;
import com.inn.core.generic.exceptions.application.DaoException;
import com.inn.foresight.core.generic.utils.ConfigEnum;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.core.infra.dao.INetworkElementDao;
import com.inn.foresight.core.infra.dao.IRANDetailDao;
import com.inn.foresight.core.infra.utils.enums.Domain;
import com.inn.foresight.core.infra.utils.enums.NEStatus;
import com.inn.foresight.core.infra.utils.enums.NEType;
import com.inn.foresight.core.infra.utils.enums.Vendor;
import com.inn.foresight.customercare.dao.ICustomerCareDao;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareNEWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSectorWrapper;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.module.bsp.service.IBSPService;
import com.inn.foresight.module.nv.device.dao.IDeviceDao;
import com.inn.foresight.module.nv.device.model.Device;
import com.inn.foresight.module.pm.capacitydetail.dao.CapacityDetailDao;
import com.inn.product.systemconfiguration.dao.SystemConfigurationDao;
import com.inn.product.systemconfiguration.model.SystemConfiguration;

@Service("CustomerCareUtils")
public class CustomerCareUtils implements CustomerCareConstants {

	private static Logger logger = LogManager.getLogger(CustomerCareUtils.class);

	private static final int PM_DATA_LOOP_ITERATION_COUNT = 4;
	private static final int TIME_INTERVAL_VALUE = 7200000;
	private static final String TILE_RANGES = "TILE_RANGES";
	private static final String CC_JSI_SCHEDULING_INTERVAL = "CC_JSI_SCHEDULING_INTERVAL";
	private static final String CC_JSI_SCHEDULING_ITERATION = "CC_JSI_SCHEDULING_ITERATION";
	private static String currentDate;
	private static Map<String, String> sysConfMap;
	private static Map<String, Device> deviceDataMap;
	private static Map<Integer, Double> loadColorMap;
	private static NNS<CustomerCareNEWrapper> onairsiteList;
	private static NNS<CustomerCareSiteWrapper> plannedsiteList;
	private static Map<Vendor, String> sysConfigPMVendorDateMap;
	private static Map<Vendor, String> sysConfigPMVendorKeyMap;

	@Autowired
	private CapacityDetailDao capacityDetailDao;

	@Autowired
	private IDeviceDao iDeviceDao;

	@Autowired
	private SystemConfigurationDao systemConfigurationDao;

	@Autowired
	private INetworkElementDao iNetworkElementDao;

	@Autowired
	private IRANDetailDao iRANDetailDao;

	@Autowired
	private ICustomerCareDao iCustomerCareDao;

	@Autowired
	private IBSPService iBSPService;

	public static Map<String, String> getSysConfMap() {
		return sysConfMap;
	}

	public static Map<String, Device> getDeviceDataMap() {
		return deviceDataMap;
	}

	public static Map<Integer, Double> getLoadColorMap() {
		return loadColorMap;
	}

	public static NNS<CustomerCareNEWrapper> getOnairsiteList() {
		return onairsiteList;
	}

	public static NNS<CustomerCareSiteWrapper> getPlannedsiteList() {
		return plannedsiteList;
	}

	public static Map<Vendor, String> getSysConfigPMVendorDateMap() {
		return sysConfigPMVendorDateMap;
	}

	public static Map<Vendor, String> getSysConfigPMVendorKeyMap() {
		return sysConfigPMVendorKeyMap;
	}

	public Map<String, String> initializeCustomerCareJSI() {
		logger.info("Going to initializing customer care jsi...");
		Map<String, String> statusMap = new HashMap<>();
		try {
			if (ConfigUtils.getString(IS_CUSTOMER_CARE_JSI).equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)) {
				logger.info("Going to start initializing customercare Data...");
				initializeCustomerCareDataInMap();
				statusMap.put(ForesightConstants.MESSAGE, ForesightConstants.SUCCESS);
				logger.info("Customercare data is initialized successfully.");
			}
		} catch (Exception exception) {
			statusMap.put(ForesightConstants.MESSAGE, ForesightConstants.FAILED);
			logger.error("Unable to intilize Customer Care map Exception {} ", Utils.getStackTrace(exception));
		}
		return statusMap;
	}

	/** Method for initializing Site data and Sys conf data with scheduling */
	public Map<String, String> initializeSitesAndSysConfMap() {
		logger.info("Going to initialize Sites and Sys conf map...");
		Map<String, String> resultMap = new HashMap<>();
		try {
			if (ConfigUtils.getString(IS_CUSTOMER_CARE_JSI).equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)) {
				logger.info("Going to start initializing sites and sys conf map...");
				Integer loopIteration = ZERO;
				Integer loopInterval = ZERO;
				for (int i = ONE; i <= (loopIteration = Integer.parseInt(systemConfigurationDao.getValueByName(CC_JSI_SCHEDULING_ITERATION))); i++) {
					loopInterval = Integer.parseInt(systemConfigurationDao.getValueByName(CC_JSI_SCHEDULING_INTERVAL));
					logger.info("Loop Iteration : {} and Loop Interval : {} and looping count : {}", loopIteration, loopInterval, i);
					initializeSysMap();
					refreshNESitesDataList();
					iBSPService.initializeBSPDataMap();
					Thread.sleep(loopInterval);
					logger.info("Going to sleep for interval in mili second : {}", loopInterval);
				}
				String date = Utils.parseDateToString(new Date(), DATE_FORMAT_DD_MM_YYYY_HH_MM_SS_A);
				logger.info("Sites and Sys conf data JSI made successfully for today : {}", date);

				resultMap.put(ForesightConstants.MESSAGE, ForesightConstants.SUCCESS);
			} else {
				resultMap.put(ForesightConstants.MESSAGE, "CC Data JSI Scheduling is OFF");
			}
		} catch (Exception e) {
			logger.error("Error initializing site and sys conf data Exception : {}", Utils.getStackTrace(e));
			resultMap.put(ForesightConstants.MESSAGE, ForesightConstants.FAILED);
		}
		return resultMap;
	}

	public void initializeCustomerCareDataInMap() {
		logger.info("Going to Initialize Customer Care data in Map");
		try {
			if (ConfigUtils.getString(IS_CUSTOMER_CARE_JSI).equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)) {
				getSystemConfigurationMapDateByCurrentDate();

				ExecutorService executors = Executors.newFixedThreadPool(THREE);
				/** CompletableFuture<Void> future = getSystemConfigurationJSI(executors); */
				CompletableFuture<Void> future1 = getSitesDataJSI(executors);
				CompletableFuture<Void> future2 = getDeviceDataJSI(executors);
				CompletableFuture<Void> future3 = getColorMapJSI(executors);
				CompletableFuture<Void> completableFuture = CompletableFuture.allOf(future1, future2, future3);
				getCompatableFuture(completableFuture);
				iBSPService.initializeBSPDataMap();
			}
		} catch (Exception e) {
			logger.error("Error in initializing customer care jsi map : {}", Utils.getStackTrace(e));
		}
	}

	private void getCompatableFuture(CompletableFuture<Void> completableFuture) {
		try {
			completableFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error in  initializing customer care data  : {}", Utils.getStackTrace(e));
			Thread.currentThread().interrupt();
		}
	}

	private NNS<CustomerCareSiteWrapper> initilizePlannedSitesJsi(NNS<CustomerCareSiteWrapper> plannedsiteList) {
		List<CustomerCareSiteWrapper> plannedSiteDataList = getPlannedSiteDataListForCustomerCare();
		if (checkNullObject(plannedSiteDataList) && !plannedSiteDataList.isEmpty()) {
			logger.info("Going to Initilize Planned Site JSI {} ", plannedSiteDataList.size());
			plannedsiteList = new NNS<>(plannedSiteDataList);
		}
		return plannedsiteList;
	}

	private CompletableFuture<Void> getColorMapJSI(ExecutorService executors) {
		return CompletableFuture.runAsync(() -> {
			try {
				initializeColorMapData();
			} catch (Exception exception) {
				logger.warn("Unable to fetch color map Data ", Utils.getStackTrace(exception));
			}
		}, executors);
	}

	private CompletableFuture<Void> getDeviceDataJSI(ExecutorService executors) {
		return CompletableFuture.runAsync(() -> {
			try {
				getDeviceDataInMap();
			} catch (Exception exception) {
				logger.warn("Unable to populate Device data {} ", Utils.getStackTrace(exception));
			}
		}, executors);
	}

	private CompletableFuture<Void> getSitesDataJSI(ExecutorService executors) {
		return CompletableFuture.runAsync(() -> {
			try {
				refreshNESitesDataList();
			} catch (Exception exception) {
				logger.warn("Unable to Refresh NE Sites In Map Exception {} ", Utils.getStackTrace(exception));
			}
		}, executors);
	}

	/**
	 * -- used it later -- private CompletableFuture<Void>
	 * getSystemConfigurationJSI(ExecutorService executors) { return
	 * CompletableFuture.runAsync(() -> { try {
	 * getSystemConfigurationMapDateByCurrentDate(); } catch (Exception exception) {
	 * logger.warn("Unable to populate System Configuration data {} ",
	 * Utils.getStackTrace(exception)); } }, executors); }
	 */

	private void refreshNESitesDataList() {
		logger.info("Going to refresh NE sites data...");
		try {
			if (onairsiteList == null) {
				onairsiteList = intilizeOnAirSitesJsi(onairsiteList);
			} else if (checkNullObject(onairsiteList)) {
				onairsiteList = null;
				if (!checkNullObject(onairsiteList)) {
					onairsiteList = intilizeOnAirSitesJsi(onairsiteList);
				}
			}
			if (plannedsiteList == null) {
				plannedsiteList = initilizePlannedSitesJsi(plannedsiteList);
			} else if (checkNullObject(plannedsiteList)) {
				plannedsiteList = null;
				if (!checkNullObject(plannedsiteList)) {
					plannedsiteList = initilizePlannedSitesJsi(plannedsiteList);
				}
			}
			logger.info("NE Site data is refreshed successfully.");
		} catch (Exception exception) {
			logger.error("Error in refreshing NE sites data in list {}", Utils.getStackTrace(exception));
		}
	}

	public static <T> Boolean checkNullObject(T t) {
		return t != null;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerCareSiteWrapper> getPlannedSiteDataListForCustomerCare() {
		logger.info("Going to populated Planned SiteData List For Customer Care");
		List<CustomerCareSiteWrapper> plannedSiteList = new ArrayList<>();
		try {
			List<String> domainList = ConfigUtils.getStringList(CC_RAN_DOMAIN_LIST);
			String neStatus = ConfigUtils.getString(CC_RAN_NESTATUS_PLANNED);
			List<String> siteNETypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_SITE_LIST);
			validateListData(domainList, siteNETypeList);
			plannedSiteList = iCustomerCareDao.getPlannedSiteForCustomerCare(Utils.convertStringToEnumList(Domain.class, domainList),
					NEStatus.valueOf(neStatus), Utils.convertStringToEnumList(NEType.class, siteNETypeList));
			logger.info("Planned site list {} ", plannedSiteList.size());
		} catch (Exception e) {
			logger.error("Error while getting planned site data for Customer Care : {} ", Utils.getStackTrace(e));
		}
		return plannedSiteList;
	}

	public void initializeColorMapData() {
		logger.info("Going to Intialize Color Map Data ");
		loadColorMap = new HashMap<>();
		String kpi = ConfigUtils.getString(CC_COLOR_MAP_KPI_KEY);
		loadColorMap = getDbfColorLegendMap(Utils.checkForValueInString(kpi) ? kpi : ForesightConstants.RSRP);
		logger.info("Found Color Map Data size {}", loadColorMap.size());
	}

	private void getDeviceDataInMap() {
		logger.info("Going to populate Device data ");
		try {
			List<Device> devices = iDeviceDao.getAllDeviceList();
			logger.info("Total device found {} ", devices.size());
			if (Utils.isValidList(devices)) {
				deviceDataMap = new HashMap<>();
				populateDeviceDataIntoMap(devices);
			} else {
				logger.warn("No Devices Found {} ", devices.size());
			}
			logger.info("Device Map Size {} ", deviceDataMap.size());
		} catch (Exception exception) {
			logger.error("Unable to fetch Devices From DB {} ", exception.getMessage());
		}
	}

	private static void populateDeviceDataIntoMap(List<Device> devices) {
		devices.forEach(device -> {
			try {
				setDeviceInfoInMap(device);
			} catch (Exception exception) {
				logger.warn("Unable to populate device data into map {}", Utils.getStackTrace(exception));
			}
		});
	}

	private static void setDeviceInfoInMap(Device device) {
		if (device.getModelCode() != null && device.getBrand() != null) {
			if (device.getModelCode().contains(ForesightConstants.COMMA)) {
				String[] splitModelCode = device.getModelCode().split(ForesightConstants.COMMA);
				for (String modelName : splitModelCode) {
					if (modelName != null) {
						modelName = modelName.replace(ForesightConstants.SPACE, ForesightConstants.BLANK_STRING);
						deviceDataMap.put(
								modelName.trim().toLowerCase() + ForesightConstants.UNDERSCORE
										+ device.getBrand().replace(ForesightConstants.SPACE, ForesightConstants.BLANK_STRING).trim().toLowerCase(),
								device);
					}
				}
			} else {
				deviceDataMap.put(device.getModelCode().replace(ForesightConstants.SPACE, ForesightConstants.BLANK_STRING).trim().toLowerCase()
						+ ForesightConstants.UNDERSCORE
						+ device.getBrand().replace(ForesightConstants.SPACE, ForesightConstants.BLANK_STRING).trim().toLowerCase(), device);
			}
		}
	}

	public void getSystemConfigurationMapDateByCurrentDate() {
		logger.info("Going to get Date From SystemConfiguration ");
		try {
			if (sysConfMap == null || sysConfMap.isEmpty()) {
				logger.info("System Config Map Initializing .....");
				initializeSysMap();
			} else if (checkNullObject(sysConfMap) && !sysConfMap.isEmpty()
					&& !currentDate.equals(Utils.parseDateToString(new Date(), DATE_FORMAT_DDMMYY))) {
				logger.info("Updating System Config Map  .....");
				currentDate = Utils.parseDateToString(new Date(), DATE_FORMAT_DDMMYY);
				sysConfMap = new HashMap<>();
				getSystemConfiguationMap();
			} else {
				if (isConfigEnabled(CC_IS_MAKE_SYS_DATA_IN_MAP)) {
					logger.info("Going to initialize sys map manually...");
					initializeSysMap();
				} else {
					logger.info("Sys data already populated");
				}
			}
			logger.info("sysConfMap map size : {}", sysConfMap.size());
		} catch (Exception exception) {
			logger.warn("Unable to put Huc Date In Map Message {} ", exception.getMessage());
		}
	}

	private void initializeSysMap() {
		sysConfMap = new HashMap<>();
		currentDate = Utils.parseDateToString(new Date(), DATE_FORMAT_DDMMYY);
		logger.info(" currentDate : {}", currentDate);
		getSystemConfiguationMap();
	}

	private NNS<CustomerCareNEWrapper> intilizeOnAirSitesJsi(NNS<CustomerCareNEWrapper> onairsiteList) {
		List<CustomerCareNEWrapper> careNEWrappers = populateOnairMacroSiteDataInJSI();
		if (Utils.isValidList(careNEWrappers)) {
			logger.info("Going to Initilize OnAir Site JSI {} ", careNEWrappers.size());
			onairsiteList = new NNS<>(careNEWrappers);
		}
		return onairsiteList;
	}

	private static List<String> removeNullEmptyDataFromList(List<String> list) {
		if (Utils.isValidList(list)) {
			while (list.remove(null) || list.remove(ForesightConstants.BLANK_STRING))
				;
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerCareNEWrapper> populateOnairMacroSiteDataInJSI() {
		logger.info("Going to Intilize Onair Sites JSI ");
		Map<String, Boolean> hucMap = new HashMap<>();
		List<CustomerCareNEWrapper> careNEWrappers = new ArrayList<>();
		try {
			List<String> domainList = ConfigUtils.getStringList(CC_RAN_DOMAIN_LIST);
			String neStatus = ConfigUtils.getString(CC_RAN_NESTATUS_ONAIR);
			List<String> siteNETypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_SITE_LIST);
			List<String> cellNETypeList = ConfigUtils.getStringList(CC_RAN_NETYPE_CELL_LIST);
			validateListData(domainList, siteNETypeList, cellNETypeList);

			initializeSYSConfigurationPMDateMap();
			logger.info("System Configuration PM Vendor Date Map : {}", sysConfigPMVendorDateMap);

			Map<Integer, String> macroSiteMap = populateMacroSitesData(domainList, neStatus, siteNETypeList);
			logger.info("Total Macro Sites found : {} ", macroSiteMap.size());

			List<Object[]> macroCellDetails = iRANDetailDao.findAllOnairSites(neStatus, cellNETypeList, domainList);
			logger.info("Macro Site Details Cell size is : {} ", macroCellDetails.size());

			List<Object[]> capacityDetails = getAllHUCDetailOfNE(domainList, neStatus, cellNETypeList, sysConfigPMVendorDateMap);
			populateHucCellDataInJSI(capacityDetails, hucMap);
			logger.info("Total Huc Map size is :  {} ", hucMap.size());

			careNEWrappers = populateOnairSiteDataIntoWrapper(hucMap, careNEWrappers, macroSiteMap, macroCellDetails);

		} catch (Exception e) {
			logger.error("Error in populating onair site in jsi Eception :  {}", Utils.getStackTrace(e));
		}
		return careNEWrappers;
	}

	private List<CustomerCareNEWrapper> populateOnairSiteDataIntoWrapper(Map<String, Boolean> hucMap, List<CustomerCareNEWrapper> careNEWrappers,
			Map<Integer, String> macroSiteMap, List<Object[]> macroCellDetails) {
		Map<String, CustomerCareNEWrapper> onAirSiteMap = new HashMap<>();
		if (Utils.isValidList(macroCellDetails)) {
			populateOnAirMacroSite(hucMap, macroSiteMap, macroCellDetails, onAirSiteMap);
			if (Utils.isValidMap(onAirSiteMap)) {
				careNEWrappers = onAirSiteMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
			}
			logger.info("careNEWrappers {} ", careNEWrappers.size());
		}
		return careNEWrappers;
	}

	private List<Object[]> getAllHUCDetailOfNE(List<String> domainList, String neStatus, List<String> cellNETypeList,
			Map<Vendor, String> vendorDateMap) {
		logger.info("Going to get HUC Cell Detail for  domain : {}, nestatus : {}, cell NEType : {}, vendor date map : {}", domainList, neStatus,
				cellNETypeList, vendorDateMap);
		List<Object[]> capacityDetails = null;
		try {
			capacityDetails = capacityDetailDao.getAllHUCCellsDetail(Utils.convertStringToEnumList(Domain.class, domainList),
					NEStatus.valueOf(neStatus), Utils.convertStringToEnumList(NEType.class, cellNETypeList), vendorDateMap, FALSE, null);
			logger.info("capacityDetails size is : {} ", capacityDetails.size());
		} catch (Exception e) {
			logger.error("Error in getting all huc cell detail  : {}", Utils.getStackTrace(e));
		}
		return capacityDetails;
	}

	private static Map<Vendor, String> getPMVendorDateByKey(List<String> vendorList, String startKey, String endKey) {
		Map<Vendor, String> vendorDateMap = new EnumMap<>(Vendor.class);
		sysConfigPMVendorKeyMap = new EnumMap<>(Vendor.class);
		vendorList.forEach(vendor -> {
			try {
				String key = (startKey + vendor + endKey);
				vendorDateMap.put(Vendor.valueOf(vendor), sysConfMap.get(key));
				sysConfigPMVendorKeyMap.put(Vendor.valueOf(vendor), key);
				logger.info("Vendor : {} , PM key : {} and date  : {}", vendor, key, sysConfMap.get(key));
				logger.info("sysConfigPMVendorKeyMap : {}", sysConfigPMVendorKeyMap);
			} catch (Exception e) {
				logger.error("Error in getting vendor key date map data Exception : {}", Utils.getStackTrace(e));
			}
		});
		return vendorDateMap;
	}

	@SuppressWarnings("unchecked")
	public static void validateListData(List<String>... allList) {
		for (List<String> list : allList) {
			removeNullEmptyDataFromList(list);
		}
	}

	private static void populateOnAirMacroSite(Map<String, Boolean> hucMap, Map<Integer, String> macroSiteMap, List<Object[]> macroCellDetails,
			Map<String, CustomerCareNEWrapper> onAirSiteMap) {
		macroCellDetails.forEach(macroCellData -> {
			try {
				List<CustomerCareSectorWrapper> careSectorWrappers;
				
				if(macroSiteMap.get(macroCellData[ZERO])!=null) {
					if (onAirSiteMap.get(macroSiteMap.get(macroCellData[ZERO])) != null) {
						CustomerCareNEWrapper customerCareNEWrapper = onAirSiteMap.get(macroSiteMap.get(macroCellData[ZERO]));
						customerCareNEWrapper.setCustomerCareSectorWrappers(
								setOnAirCellData(customerCareNEWrapper.getCustomerCareSectorWrappers(), macroCellData, hucMap));
						onAirSiteMap.put(macroSiteMap.get(macroCellData[ZERO]), customerCareNEWrapper);
					} else {
						careSectorWrappers = new ArrayList<>();
						CustomerCareNEWrapper customerCareNEWrapper = new CustomerCareNEWrapper(Double.parseDouble(macroCellData[EIGHT].toString()),
								Double.parseDouble(macroCellData[NINE].toString()), macroSiteMap.get(macroCellData[ZERO]), macroCellData[TEN].toString(),
								macroCellData[ELEVEN] != null ? macroCellData[ELEVEN].toString() : null,
										macroCellData[TWELVE] != null ? macroCellData[TWELVE].toString() : null,
												setOnAirCellData(careSectorWrappers, macroCellData, hucMap));
						onAirSiteMap.put(macroSiteMap.get(macroCellData[ZERO]), customerCareNEWrapper);
					}
				}
			} catch (Exception exception) {
				logger.warn("Unable to populate Onair Macro Site Data Message {} ", exception.getMessage());
			}
		});
	}

	private static List<CustomerCareSectorWrapper> setOnAirCellData(List<CustomerCareSectorWrapper> careSectorWrappers, Object[] macroCellDetail,
			Map<String, Boolean> hucMap) {
		CustomerCareSectorWrapper customerCareSectorWrapper = new CustomerCareSectorWrapper();
		customerCareSectorWrapper.setCellId(macroCellDetail[ONE] != null ? Integer.parseInt(macroCellDetail[ONE].toString()) : null);
		customerCareSectorWrapper.setAzimuth(macroCellDetail[TWO] != null ? Integer.parseInt(macroCellDetail[TWO].toString()) : null);
		customerCareSectorWrapper.setPci(macroCellDetail[THREE] != null ? Integer.parseInt(macroCellDetail[THREE].toString()) : null);
		customerCareSectorWrapper.setNeId(macroCellDetail[FOUR] != null ? macroCellDetail[FOUR].toString() : null);
		customerCareSectorWrapper.setNeFrequency(macroCellDetail[FIVE].toString());
		customerCareSectorWrapper.setCarrier(macroCellDetail[SIX] != null ? macroCellDetail[SIX].toString() : null);
		customerCareSectorWrapper.setSectorId(macroCellDetail[SEVEN] != null ? Integer.parseInt(macroCellDetail[SEVEN].toString()) : null);
		customerCareSectorWrapper.setIsHighlyUtilized(macroCellDetail[THIRTEEN] != null ? hucMap.get(macroCellDetail[THIRTEEN].toString()) : null);
		customerCareSectorWrapper.setParentneId(macroCellDetail[FOURTEEN] != null ? macroCellDetail[FOURTEEN].toString() : null);
		customerCareSectorWrapper.setTechnology(macroCellDetail[FIFTEEN] != null ? macroCellDetail[FIFTEEN].toString() : null);
		customerCareSectorWrapper.setNeType(macroCellDetail[SIXTEEN] != null ? macroCellDetail[SIXTEEN].toString() : null);

		careSectorWrappers.add(customerCareSectorWrapper);
		return careSectorWrappers;
	}

	private Map<Integer, Double> getDbfColorLegendMap(String kpi) {
		logger.info("Going to make color legend map for kpi : {}", kpi);
		Map<String, Map<Integer, Double>> colorMap = new HashMap<>();
		try {
			if (Utils.checkForValueInString(kpi)) {
				String colorMapKey = ConfigUtils.getString(CC_COLOR_MAP_DATA_KEY);
				logger.info("Color Map Db Key : {}", colorMapKey);
				List<SystemConfiguration> systemConfigurationByName = systemConfigurationDao
						.getSystemConfigurationByName(Utils.checkForValueInString(colorMapKey) ? colorMapKey : TILE_RANGES);
				Map<String, Map<Double, List<Integer>>> loadcolorMap = getDBFColorMapFromSysConf(systemConfigurationByName);
				Map<Double, List<Integer>> colorWithLegends = loadcolorMap.get(kpi.toUpperCase());
				TreeMap<Integer, Double> colorWithLegendsInt = new TreeMap<>();
				for (Map.Entry<Double, List<Integer>> entry : colorWithLegends.entrySet()) {
					List<Integer> colorList = entry.getValue();
					if (entry.getKey() < -43) {
						colorWithLegendsInt.put((new Color(colorList.get(ForesightConstants.ZERO), colorList.get(ForesightConstants.ONE),
								colorList.get(ForesightConstants.TWO)).getRGB()), entry.getKey());
					}
				}
				colorMap.put(kpi, colorWithLegendsInt);
			}
		} catch (Exception e) {
			logger.error("Error inside getDbfColorLegendMap method Io exception: {}", Utils.getStackTrace(e));
		}
		logger.info("Loaded Color Map {}", colorMap);
		return colorMap.get(kpi);
	}

	@SuppressWarnings("serial")
	private static Map<String, Map<Double, List<Integer>>> getDBFColorMapFromSysConf(List<SystemConfiguration> systemConfigurationByName) {
		String dbfColorAsString = systemConfigurationByName.get(NumberUtils.INTEGER_ZERO).getValue();
		return new Gson().fromJson(dbfColorAsString, new TypeToken<Map<String, Map<Double, List<Integer>>>>() {
		}.getType());
	}

	private void getSystemConfiguationMap() {
		try {
			List<SystemConfiguration> configurations = getSystemConfiguation();
			if (Utils.isValidList(configurations)) {
				logger.info("sConf {} ", configurations.size());
				configurations.forEach(systemConfiguration -> sysConfMap.put(systemConfiguration.getName(), systemConfiguration.getValue()));
			}
		} catch (DaoException e) {
			logger.error("Error in getting data from system configuration : {}", Utils.getStackTrace(e));
		}
	}

	private Map<Integer, String> populateMacroSitesData(List<String> domainList, String neStatus, List<String> cellNETypeList) {
		logger.info("Going to populate marco site data for neType : {} neStatus : {}, domain : {}", cellNETypeList, neStatus, domainList);
		Map<Integer, String> macroNETypeMap = new HashMap<>();

		List<Object[]> networkElementList = iNetworkElementDao.getSiteDetailForCustomerCare(Utils.convertStringToEnumList(Domain.class, domainList),
				NEStatus.valueOf(neStatus), Utils.convertStringToEnumList(NEType.class, cellNETypeList));

		if (networkElementList != null && !networkElementList.isEmpty()) {
			networkElementList.forEach(networkElement -> {
				try {
					macroNETypeMap.put(Integer.parseInt(networkElement[ZERO].toString()), networkElement[ONE].toString());
				} catch (Exception exception) {
					logger.error("Unable to populate Macro NEType data into map {}", Utils.getStackTrace(exception));
				}
			});
		}
		return macroNETypeMap;
	}

	private static void populateHucCellDataInJSI(List<Object[]> capacityDetails, Map<String, Boolean> hucMap) {
		logger.info("Going to Intilize Huc Cell JSI ");
		try {
			if (capacityDetails != null) {
				capacityDetails.forEach(capacityDetail -> hucMap.put(capacityDetail[ZERO].toString(),
						capacityDetail[ONE] != null ? Boolean.parseBoolean(capacityDetail[ONE].toString()) : null));
			}
		} catch (Exception exception) {
			logger.warn("Unable to populate Huc CellData Message {} ", Utils.getStackTrace(exception));
		}
	}

	private List<SystemConfiguration> getSystemConfiguation() {
		logger.info("Going  get System configuation data ");
		List<SystemConfiguration> list = null;
		try {
			list = systemConfigurationDao.getSystemConfigurationValueAndName();
			logger.info("System Configuation data size : {}", Utils.isValidList(list) ? list.size() : ZERO);
		} catch (Exception e) {
			logger.error("Error in getting system configuation data from db: {}", Utils.getStackTrace(e));
		}
		return list;
	}

	/** PM Highly utilized cell data making call by scheduling every 2 hour */
	public Map<String, String> initializePMVendorData() {
		logger.info("Going to initialize PM vendor Data...");
		Map<String, String> statusMap = new HashMap<>();
		try {
			if (ConfigUtils.getString(IS_CUSTOMER_CARE_JSI).equalsIgnoreCase(ForesightConstants.TRUE_LOWERCASE)) {
				logger.info("Scheduling Enable for making PM vendor data...");

				Map<Vendor, String> processingVendorDateMap = new EnumMap<>(Vendor.class);
				processingVendorDateMap = getVendorDateMapForDataProcessing(processingVendorDateMap);

				Integer loopIteration = ConfigUtils.getInteger(CC_PM_DATA_JSI_LOOP_ITRATION);
				Integer timeInterval = ConfigUtils.getInteger(CC_PM_DATA_JSI_TIME_INTERVAL);
				loopIteration = (Utils.isValidInteger(loopIteration) ? loopIteration : PM_DATA_LOOP_ITERATION_COUNT);
				timeInterval = (Utils.isValidInteger(timeInterval) ? timeInterval : TIME_INTERVAL_VALUE);

				if (Utils.isValidMap(processingVendorDateMap)) {
					while (processingVendorDateMap.size() > ZERO && loopIteration > ZERO) {
						processingVendorDateMap = processingPMDataMap(processingVendorDateMap, loopIteration, timeInterval);
						loopIteration--;
					}
				}
				statusMap.put(ForesightConstants.MESSAGE, ForesightConstants.SUCCESS);
			} else {
				logger.info("Scheduling Disable for making PM vendor Data");
			}
		} catch (Exception exception) {
			statusMap.put(ForesightConstants.MESSAGE, ForesightConstants.FAILED);
			logger.error("Unable to check Huc Date for current Date Exception {} ", Utils.getStackTrace(exception));
		}
		return statusMap;
	}

	private Map<Vendor, String> processingPMDataMap(Map<Vendor, String> processingVendorDateMap, Integer loopIteration, Integer timeInterval) {
		try {
			logger.info("Going to make data for PM vendor {}", processingVendorDateMap);
			refreshNESitesDataList();
			logger.info("Going to sleep for Second : {} and interval : {}", timeInterval, loopIteration);
			Thread.sleep(timeInterval);
			getSystemConfigurationMapDateByCurrentDate();
			getVendorDateMapForDataProcessing(processingVendorDateMap);
			logger.info("Ending loop for PM data processing");
		} catch (Exception e) {
			logger.error("Error in making pm data with time interval Exception : {}", Utils.getStackTrace(e));
		}
		return processingVendorDateMap;
	}

	private Map<Vendor, String> getVendorDateMapForDataProcessing(Map<Vendor, String> processingVendorDateMap) {
		Map<Vendor, String> capacityVendorDateMap = capacityDetailDao.getMaxDateFromCapacityDetail();
		logger.info("Capacity Vendor Data Map {}", capacityVendorDateMap);

		initializeSYSConfigurationPMDateMap();
		logger.info("System Configuration PM Date Map : {}", sysConfigPMVendorDateMap);

		if (Utils.isValidMap(sysConfigPMVendorDateMap) && Utils.isValidMap(capacityVendorDateMap)) {
			capacityVendorDateMap.forEach((vendor, vendorDate) -> {
				if (sysConfigPMVendorDateMap.containsKey(vendor) && sysConfigPMVendorDateMap.get(vendor).equals(vendorDate)) {
					logger.info("PM Data is available for Vendor : {}", vendor);
					processingVendorDateMap.put(vendor, vendorDate);
					// processingVendorDateMap.remove(vendor);

				} else if (sysConfigPMVendorDateMap.containsKey(vendor) && !sysConfigPMVendorDateMap.get(vendor).equals(vendorDate)) {
					logger.info("Adding PM Vendor Data in Proccessing Map : {}", vendor);
					processingVendorDateMap.put(vendor, vendorDate);
				}
			});
		}
		return processingVendorDateMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<Vendor, String> initializeSYSConfigurationPMDateMap() {
		List<String> vendorList = ConfigUtils.getStringList(CC_RAN_VENDOR_LIST);
		validateListData(vendorList);
		String startKey = ConfigUtils.getString(CC_HUC_DATA_STARTING_KEY);
		String endKey = ConfigUtils.getString(CC_HUC_DATA_ENDTING_KEY);
		sysConfigPMVendorDateMap = getPMVendorDateByKey(vendorList, startKey, endKey);
		logger.info("sysConfigPMVendorDateMap : {}", sysConfigPMVendorDateMap.size());
		return sysConfigPMVendorDateMap;
	}

	public static Map<Vendor, String> initializePMVendorKeyMap() {
		logger.info("sysConfigPMVendorKeyMap : {}", sysConfigPMVendorKeyMap.size());
		return sysConfigPMVendorKeyMap;
	}

	public static String getValidatedData(String value, String defaultValue) {
		return (Utils.checkForValueInString(value) ? value : defaultValue);
	}

	public static Boolean isConfigEnabled(String config) {
		return (ConfigUtils.getBoolean(config) != null && ConfigUtils.getBoolean(config).equals(TRUE));
	}

	public static String appendBaseURL(String url) {
		return (ConfigUtils.getString(ConfigEnum.MICRO_SERVICE_BASE_URL.getValue()) + url);
	}

	public static String makeGETRequest(String url) throws HttpException {
		return new HttpGetRequest(url).getString();
	}

	public static String makePOSTRequest(String url, HttpEntity entity) throws HttpException {
		return new HttpPostRequest(url, entity).getString();
	}

	public static <T> T parseGsonData(String response, TypeToken<T> typeOfResponse) {
		return new Gson().fromJson(response, (Type) typeOfResponse.getType());
	}

	public static void handleTransactionException() {
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}

	public static String appendData(Object... varArgs) {
		StringBuilder appendResult = null;
		if (varArgs != null) {
			appendResult = new StringBuilder();
			for (Object str : varArgs) {
				if (checkNullObject(str)) {
					appendResult.append(str);
				}
			}
		}
		return appendResult != null ? appendResult.toString() : null;
	}

	public static Map<String, Object> getQueryStringParameters(String queryString) {
		try {
			return Arrays.asList(queryString.split(ForesightConstants.AMPERSAND)).stream().map(query -> query.split(ForesightConstants.EQUALS))
					.collect(Collectors.toMap(queries -> queries[ZERO], queries -> queries[ONE]));
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
			logger.error("Error in getting query string due to ArrayIndexOutOfBoundsException : {}", arrayIndexOutOfBoundsException.getMessage());
			return null;
		}
	}
}
