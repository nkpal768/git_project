package com.inn.foresight.customercare.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inn.product.um.user.model.User;

@Entity
@Table(name = "CCAudit")
@XmlRootElement(name = "CCAudit")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class CCAudit implements Serializable {
	
	private static final long serialVersionUID = -8980979195221014512L;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "auditid_pk")
	private Integer id;

	@Basic
	@Column(name = "searchvalue")
	private String searchValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid_fk", nullable = false)
	private User user;

	@Basic
	@Column(name = "audittime")
	private Date auditTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	@Override
	public String toString() {
		return "CCAudit [id=" + id + ", searchValue=" + searchValue + ", user=" + user + ", auditTime=" + auditTime
				+ "]";
	}

}
