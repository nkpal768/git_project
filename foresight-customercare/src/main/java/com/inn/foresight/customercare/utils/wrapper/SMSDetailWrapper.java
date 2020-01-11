package com.inn.foresight.customercare.utils.wrapper;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.inn.core.generic.wrapper.JpaWrapper;
import com.inn.foresight.customercare.utils.SMSDetailConstants;

@JpaWrapper
public class SMSDetailWrapper {

	private Long totalSMS;
	private String msisdn;
	private String sentDateTime;
	private String sentBy;
	private String duration;
	private String executiveRole;

	public SMSDetailWrapper(String msisdn, String sentBy, Date sentDateTime, String duration, Long totalSMS, String executiveRole) {
		super();
		this.totalSMS = totalSMS;
		this.msisdn = msisdn;
		this.sentDateTime = sentDateTime != null ? convertDateToString(sentDateTime, SMSDetailConstants.DD_MMM_YYYY) : null;
		this.sentBy = sentBy;
		this.duration = duration;
		this.executiveRole = executiveRole;
	}

	public Long getTotalSMS() {
		return totalSMS;
	}

	public void setTotalSMS(Long totalSMS) {
		this.totalSMS = totalSMS;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getSentDateTime() {
		return sentDateTime;
	}

	public void setSentDateTime(String sentDateTime) {
		this.sentDateTime = sentDateTime;
	}

	public String getSentBy() {
		return sentBy;
	}

	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getExecutiveRole() {
		return executiveRole;
	}

	public void setExecutiveRole(String executiveRole) {
		this.executiveRole = executiveRole;
	}

	@Override
	public String toString() {
		return "SMSDetailWrapper [totalSMS=" + totalSMS + ", msisdn=" + msisdn + ", sentDateTime=" + sentDateTime + ", sentBy=" + sentBy
				+ ", duration=" + duration + ", executiveRole=" + executiveRole + "]";
	}

	private String convertDateToString(Date convertDate, String format) {
		return new SimpleDateFormat(format).format(convertDate);
	}
}