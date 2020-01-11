package com.inn.foresight.customercare.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inn.foresight.customercare.enums.SMSType;
import com.inn.product.um.user.model.User;

@XmlRootElement(name = "SMSDetail")
@Entity
@Table(name = "SMSDetail")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class SMSDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "smsdetailid_pk")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creatorid_fk", nullable = false)
	private User userId;

	@Basic
	@Column(name = "msisdn")
	private String msisdn;

	@Basic
	@Column(name = "sentdatetime")
	private Date sentDateTime;

	@Basic
	@Column(name = "smstype")
	@Enumerated(EnumType.STRING)
	private SMSType smsType;

	@Basic
	@Column(name = "duration")
	private String duration;

	@Basic
	@Column(name = "message")
	private String message;

	@Basic
	@Column(name = "searchvalue")
	private String searchValue;

	@Column(name = "executiverole")
	private String executiveRole;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public Date getSentDateTime() {
		return sentDateTime;
	}

	public void setSentDateTime(Date sentDateTime) {
		this.sentDateTime = sentDateTime;
	}

	public SMSType getSmsType() {
		return smsType;
	}

	public void setSmsType(SMSType smsType) {
		this.smsType = smsType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public String getExecutiveRole() {
		return executiveRole;
	}

	public void setExecutiveRole(String executiveRole) {
		this.executiveRole = executiveRole;
	}

	@Override
	public String toString() {
		return "SMSDetail [id=" + id + ", userId=" + userId + ", msisdn=" + msisdn + ", sentDateTime=" + sentDateTime + ", smsType=" + smsType + ", duration=" + duration + ", message=" + message
				+ ", searchValue=" + searchValue + ", executiveRole=" + executiveRole + "]";
	}
}