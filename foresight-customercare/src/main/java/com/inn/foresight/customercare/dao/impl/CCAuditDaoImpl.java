package com.inn.foresight.customercare.dao.impl;

import org.springframework.stereotype.Repository;

import com.inn.core.generic.dao.impl.HibernateGenericDao;
import com.inn.foresight.customercare.dao.ICCAuditDao;
import com.inn.foresight.customercare.model.CCAudit;

@Repository
public class CCAuditDaoImpl extends HibernateGenericDao<Integer, CCAudit> implements ICCAuditDao {

	public CCAuditDaoImpl() {
		super(CCAudit.class);
	}

}
