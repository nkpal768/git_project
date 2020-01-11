package com.inn.foresight.customercare.rest.impl;

import java.util.ArrayList;
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
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.infra.utils.enums.Vendor;
import com.inn.foresight.customercare.service.ICustomerCareService;
import com.inn.foresight.customercare.utils.CustomerCareUtils;
import com.inn.foresight.customercare.utils.wrapper.CustomerCareSiteWrapper;
import com.inn.foresight.module.pm.wrapper.KPIRequestWrapper;
import com.inn.foresight.module.pm.wrapper.KPIResponseWrapper;

@Path("/ms/CustomerCare")
@Produces("application/json")
@Consumes("application/json")
@Service("CustomerCareMicroServiceRestImpl")
public class CustomerCareMicroServiceRestImpl extends AbstractCXFRestService<Integer, Object> {

	private Logger logger = LogManager.getLogger(CustomerCareMicroServiceRestImpl.class);

	@Autowired
	private ICustomerCareService iCustomerCareService;

	@Autowired
	private CustomerCareUtils customerCareUtils;

	public CustomerCareMicroServiceRestImpl() {
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
	@Path("getSystemConfigurationDataByName/{name}")
	public String getSystemConfigurationDataByName(@PathParam("name") String name) {
		logger.info("Going to get system configuration data by name : {}", name);
		return iCustomerCareService.getSystemConfigurationDataByName(name);
	}

	@GET
	@Path("getDeviceDataByModelName")
	public String getDeviceDataByModelName(@QueryParam("modelName") String modelName) {
		logger.info("Going to get device data by name : {}", modelName);
		return iCustomerCareService.getDeviceDataByModelName(modelName);
	}

	@GET
	@Path("getColorMapData/{colorValue}")
	public Double getColorMapData(@PathParam("colorValue") Integer colorValue) {
		logger.info("Going to get color map data by color value : {}", colorValue);
		return iCustomerCareService.getColorMapData(colorValue);
	}

	@GET
	@Path("getOnAirSitesByLatLong/{latitude}/{longitude}")
	public List<CustomerCareSiteWrapper> getOnAirSitesByLatLong(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude) {
		logger.info("Going to get onair Sites for latitude {} longitude {}  from micro service", latitude, longitude);
		return iCustomerCareService.getOnAirSitesByLatLong(latitude, longitude);
	}

	@GET
	@Path("getPlannedSitesByLatLong/{latitude}/{longitude}")
	public String getPlannedSitesByLatLong(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude) {
		logger.info("Going to get planned Sites for latitude {} longitude {} from micro service", latitude, longitude);
		return iCustomerCareService.getPlannedSitesByLatLong(latitude, longitude);
	}

	@POST
	@Path("getPMKPIDatafromHBase")
	public List<KPIResponseWrapper> getPMKPIDatafromHBase(KPIRequestWrapper kpiRequestWrapper) {
		logger.info("Going to get PM kpi data from hbase");
		return iCustomerCareService.getPMKPIDatafromHBase(kpiRequestWrapper);
	}

	@GET
	@Path("initializeCustomerCareJSI")
	public Map<String, String> initializeCustomerCareJSI() {
		logger.info("Going to initialize customer care jsi");
		return customerCareUtils.initializeCustomerCareJSI();
	}

	@GET
	@Path("initializePMVendorData")
	public Map<String, String> initializePMVendorData() {
		logger.info("Going to initialize customer care PM data jsi");
		return customerCareUtils.initializePMVendorData();
	}

	@GET
	@Path("initializeSYSConfigurationPMDateMap")
	public Map<Vendor, String> initializeSYSConfigurationPMDateMap() {
		logger.info("Going to initialize customer care PM data jsi");
		return CustomerCareUtils.initializeSYSConfigurationPMDateMap();
	}

	@GET
	@Path("initializePMVendorKeyMap")
	public Map<Vendor, String> initializePMVendorKeyMap() {
		logger.info("Going to initialize customer care PM vendor key map");
		return CustomerCareUtils.initializePMVendorKeyMap();
	}

	@GET
	@Path("initializeSitesAndSysConfMap")
	public Map<String, String> initializeSitesAndSysConfMap() {
		logger.info("Going to initialize sites and sys conf map");
		return customerCareUtils.initializeSitesAndSysConfMap();
	}

	@GET
	@Path("getOnAirSitesByRadius/{latitude}/{longitude}")
	public List<CustomerCareSiteWrapper> getOnAirSitesByRadius(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude) {
		logger.info("Going to get onair Sites for latitude {} longitude {}  from micro service @getOnAirSitesByRadius", latitude, longitude);
		return iCustomerCareService.getOnAirSitesByRadius(latitude, longitude);
	}

	@GET
	@Path("getPlannedSitesByRadius/{latitude}/{longitude}")
	public String getPlannedSitesByRadius(@PathParam("latitude") Double latitude, @PathParam("longitude") Double longitude) {
		logger.info("Going to get planned Sites for latitude {} longitude {} from micro service @getPlannedSitesByRadius", latitude, longitude);
		return iCustomerCareService.getPlannedSitesByRadius(latitude, longitude);
	}
	
	@GET
	@Path("getCoveragePerception")
	public Map<String, String> getCoveragePerception(@QueryParam("latitude") Double latitude, @QueryParam("longitude") Double longitude,
			@QueryParam("kpi") String kpi, @QueryParam("zoomLevel") Integer zoomLevel, @QueryParam("siteStatus") String siteStatus,
			@QueryParam("band") String band, @QueryParam("dimension") String dimension, @QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime, @Context HttpServletRequest request) {
		logger.info("Going to get coverage perception");
		return iCustomerCareService.getCoveragePerception(latitude, longitude, kpi, zoomLevel, siteStatus, band, dimension, startTime, endTime,
				request);
	}
	
	@POST
	@Path("getCRDetailsForNEIds")
	public Map getCRDetailsForNEIds(@QueryParam("woCategory") List<String> woCategory, @QueryParam("columnKey") String columnKey, @QueryParam("cmStatus") List<String> cmStatusList, List<String> neIds,
			@QueryParam("startTime") Long startTime, @QueryParam("endTime") Long endTime) {
		logger.info("Going to get CR details for woCategory : {} , columnKey : {} , cmStatus : {},  startTime : {}, endTime : {}", woCategory, columnKey, cmStatusList, startTime, endTime);
		if ((woCategory != null && !woCategory.isEmpty()) && columnKey != null && (cmStatusList != null && !cmStatusList.isEmpty()) && neIds != null && !neIds.isEmpty()) {
			return iCustomerCareService.getCRDetailsForNEIds(woCategory, columnKey, cmStatusList, neIds, startTime, endTime);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}
	
}
