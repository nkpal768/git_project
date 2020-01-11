package com.inn.foresight.customercare.service;

import com.inn.core.generic.service.IGenericService;
import com.inn.foresight.customercare.model.CCAudit;

public interface ICCAuditService extends IGenericService<Integer, CCAudit>{

	String createCCAudit(String searchValue);

}
