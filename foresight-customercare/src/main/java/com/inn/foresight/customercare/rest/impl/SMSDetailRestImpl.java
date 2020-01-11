package com.inn.foresight.customercare.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inn.core.generic.exceptions.application.RestException;
import com.inn.core.generic.rest.AbstractCXFRestService;
import com.inn.core.generic.service.IGenericService;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.customercare.model.SMSDetail;
import com.inn.foresight.customercare.rest.ISMSDetailRest;
import com.inn.foresight.customercare.service.ISMSDetailService;
import com.inn.foresight.customercare.utils.wrapper.SMSDetailWrapper;
import com.inn.product.systemconfiguration.model.SystemConfiguration;

@Path("SMSDetail")
@Produces("application/json")
@Consumes("application/json")
@Service("SMSDetailRestImpl")
public class SMSDetailRestImpl extends AbstractCXFRestService<Integer, SMSDetail> implements ISMSDetailRest {

	private Logger logger = LogManager.getLogger(SMSDetailRestImpl.class);

	@Autowired
	private ISMSDetailService iSMSDetailService;

	public SMSDetailRestImpl() {
		super(SMSDetail.class);
	}

	@Override
	public List<SMSDetail> search(SMSDetail entity) {
		return new ArrayList<>();
	}

	@Override
	public SMSDetail findById(@NotNull Integer primaryKey) {
		return null;
	}

	@Override
	public List<SMSDetail> findAll() {
		return new ArrayList<>();
	}

	@Override
	public SMSDetail create(@Valid SMSDetail anEntity) {
		return null;
	}

	@Override
	public SMSDetail update(@Valid SMSDetail anEntity) {
		return null;
	}

	@Override
	public boolean remove(@Valid SMSDetail anEntity) {
		return false;
	}

	@Override
	public void removeById(@NotNull Integer primaryKey) {
		// blank method
	}

	@Override
	public IGenericService<Integer, SMSDetail> getService() {
		return null;
	}

	@Override
	public SearchContext getSearchContext() {
		return null;
	}

	@GET
	@Override
	@Path("getTotalSMSSent")
	public Long getTotalSMSSent(@QueryParam("searchValue") String searchValue) {
		logger.info("Going to fetch total count of SMS sent for searchValue {}", searchValue);
		return iSMSDetailService.getTotalSMSSent(searchValue);
	}

	@GET
	@Override
	@Path("getSMSDetail")
	public List<SMSDetailWrapper> getSMSDetail(@QueryParam("llimit") Integer llimit, @QueryParam("ulimit") Integer ulimit) {
		logger.info("Going to get the details of all SMS llimit{} ulimit{}", llimit, ulimit);
		return iSMSDetailService.getSMSDetail(llimit, ulimit);
	}

	@GET
	@Override
	@Path("searchSMSDetailBySearchValue")
	public List<SMSDetailWrapper> searchSMSDetailBySearchValue(@QueryParam("searchValue") String searchValue, @QueryParam("llimit") Integer llimit,
			@QueryParam("ulimit") Integer ulimit) {
		logger.info("Going to search total sms sent by searchValue {}", searchValue);
		return iSMSDetailService.searchSMSDetailBySearchValue(searchValue, llimit, ulimit);
	}

	@GET
	@Override
	@Path("getMessageUrlForSMSDetail/{name}")
	public SystemConfiguration getMessageUrlForSMSDetail(@PathParam("name") String name) {
		logger.info("Going to get message url for name : {}", name);
		if (name != null && !name.isEmpty()) {
			return iSMSDetailService.getMessageUrlForSMSDetail(name);
		} else {
			throw new RestException(ForesightConstants.INVALID_PARAMETER);
		}
	}
}
