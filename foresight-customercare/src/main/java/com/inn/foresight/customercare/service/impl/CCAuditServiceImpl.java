package com.inn.foresight.customercare.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inn.core.generic.service.impl.AbstractService;
import com.inn.foresight.core.generic.utils.ForesightConstants;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.customercare.dao.ICCAuditDao;
import com.inn.foresight.customercare.model.CCAudit;
import com.inn.foresight.customercare.service.ICCAuditService;
import com.inn.foresight.customercare.utils.CustomerCareUtils;
import com.inn.product.um.user.service.UserContextService;

@Service("CCAuditServiceImpl")
public class CCAuditServiceImpl extends AbstractService<Integer, CCAudit> implements ICCAuditService {

	private Logger logger = LogManager.getLogger(CCAuditServiceImpl.class);

	@Autowired
	private ICCAuditDao iCCAuditDao;

	@Autowired
	private UserContextService userContextService;

	@Override
	@Transactional
	public String createCCAudit(String searchValue) {
		logger.info("inside createCCAudit");
		try {
			if (userContextService.getUserInContextnew() != null) {
				CCAudit ccAudit = new CCAudit();
				ccAudit.setSearchValue(searchValue);
				ccAudit.setUser(userContextService.getUserInContextnew());
				ccAudit.setAuditTime(new Date());
				iCCAuditDao.create(ccAudit);
				return ForesightConstants.SUCCESS_JSON;
			} else {
				return ForesightConstants.FAILED_JSON;
			}
		} catch (Exception exception) {
			logger.error("exception in auditing request {}", Utils.getStackTrace(exception));
			CustomerCareUtils.handleTransactionException();
			return ForesightConstants.FAILED_JSON;
		}
	}

}
