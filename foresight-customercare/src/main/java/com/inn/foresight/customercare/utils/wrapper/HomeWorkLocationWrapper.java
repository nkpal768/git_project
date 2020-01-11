package com.inn.foresight.customercare.utils.wrapper;

import com.inn.core.generic.wrapper.RestWrapper;

@RestWrapper
public class HomeWorkLocationWrapper {

	private String latitude;
	private String longitude;
	private String imsi;
	private String l2;
	private String imei;
	private String cellId;
	private String btsName;
	private String capturedTime;
	private String pci;
	private String neFrequency;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getL2() {
		return l2;
	}

	public void setL2(String l2) {
		this.l2 = l2;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getBtsName() {
		return btsName;
	}

	public void setBtsName(String btsName) {
		this.btsName = btsName;
	}

	public String getCapturedTime() {
		return capturedTime;
	}

	public void setCapturedTime(String capturedTime) {
		this.capturedTime = capturedTime;
	}

	public String getPci() {
		return pci;
	}

	public void setPci(String pci) {
		this.pci = pci;
	}

	public String getNeFrequency() {
		return neFrequency;
	}

	public void setNeFrequency(String neFrequency) {
		this.neFrequency = neFrequency;
	}

	@Override
	public String toString() {
		return "HomeWorkLocationWrapper [latitude=" + latitude + ", longitude=" + longitude + ", imsi=" + imsi + ", l2=" + l2 + ", imei=" + imei
				+ ", cellId=" + cellId + ", btsName=" + btsName + ", capturedTime=" + capturedTime + ", pci=" + pci + ", neFrequency=" + neFrequency
				+ "]";
	}

}