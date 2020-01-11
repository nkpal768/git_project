package com.inn.foresight.customercare.utils.wrapper;

import java.io.Serializable;

import com.inn.core.generic.wrapper.RestWrapper;

@RestWrapper
public class BBMDetailWrapper implements Serializable {

	private static final long serialVersionUID = 7123310927397091029L;

	private String bbmPin;
	private String registrationDate;
	private String deregistrationDate;
	private String bbmMDNState;
	private String btsCode;

	private String make;
	private String model;
	private String deviceOs;
	private String bbmAppVersion;
	private String deviceId;
	private String networkType;
	private String operatorName;
	private String mcc;
	private String mnc;
	private String pci;
	private String cgi;
	private String rsrp;
	private String rsrq;
	private String rscp;
	private String sinr;
	private String rxLevel;
	private String jitter;
	private String latency;
	private String latitude;
	private String longitude;
	private String callStartTime;
	private String callEndTime;
	private String callDuration;
	private String releaseCause;
	private String callType;
	private String sessionThroughput;

	public String getBbmPin() {
		return bbmPin;
	}

	public void setBbmPin(String bbmPin) {
		this.bbmPin = bbmPin;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getDeregistrationDate() {
		return deregistrationDate;
	}

	public void setDeregistrationDate(String deregistrationDate) {
		this.deregistrationDate = deregistrationDate;
	}

	public String getBbmMDNState() {
		return bbmMDNState;
	}

	public void setBbmMDNState(String bbmMDNState) {
		this.bbmMDNState = bbmMDNState;
	}

	public String getBtsCode() {
		return btsCode;
	}

	public void setBtsCode(String btsCode) {
		this.btsCode = btsCode;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDeviceOs() {
		return deviceOs;
	}

	public void setDeviceOs(String deviceOs) {
		this.deviceOs = deviceOs;
	}

	public String getBbmAppVersion() {
		return bbmAppVersion;
	}

	public void setBbmAppVersion(String bbmAppVersion) {
		this.bbmAppVersion = bbmAppVersion;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getMnc() {
		return mnc;
	}

	public void setMnc(String mnc) {
		this.mnc = mnc;
	}

	public String getPci() {
		return pci;
	}

	public void setPci(String pci) {
		this.pci = pci;
	}

	public String getCgi() {
		return cgi;
	}

	public void setCgi(String cgi) {
		this.cgi = cgi;
	}

	public String getRsrp() {
		return rsrp;
	}

	public void setRsrp(String rsrp) {
		this.rsrp = rsrp;
	}

	public String getRsrq() {
		return rsrq;
	}

	public void setRsrq(String rsrq) {
		this.rsrq = rsrq;
	}

	public String getRscp() {
		return rscp;
	}

	public void setRscp(String rscp) {
		this.rscp = rscp;
	}

	public String getSinr() {
		return sinr;
	}

	public void setSinr(String sinr) {
		this.sinr = sinr;
	}

	public String getRxLevel() {
		return rxLevel;
	}

	public void setRxLevel(String rxLevel) {
		this.rxLevel = rxLevel;
	}

	public String getJitter() {
		return jitter;
	}

	public void setJitter(String jitter) {
		this.jitter = jitter;
	}

	public String getLatency() {
		return latency;
	}

	public void setLatency(String latency) {
		this.latency = latency;
	}

	public String getCallStartTime() {
		return callStartTime;
	}

	public void setCallStartTime(String callStartTime) {
		this.callStartTime = callStartTime;
	}

	public String getCallEndTime() {
		return callEndTime;
	}

	public void setCallEndTime(String callEndTime) {
		this.callEndTime = callEndTime;
	}

	public String getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}

	public String getReleaseCause() {
		return releaseCause;
	}

	public void setReleaseCause(String releaseCause) {
		this.releaseCause = releaseCause;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getSessionThroughput() {
		return sessionThroughput;
	}

	public void setSessionThroughput(String sessionThroughput) {
		this.sessionThroughput = sessionThroughput;
	}

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

	@Override
	public String toString() {
		return "BBMDetailWrapper [bbmPin=" + bbmPin + ", registrationDate=" + registrationDate + ", deregistrationDate=" + deregistrationDate
				+ ", bbmMDNState=" + bbmMDNState + ", btsCode=" + btsCode + ", make=" + make + ", model=" + model + ", deviceOs=" + deviceOs
				+ ", bbmAppVersion=" + bbmAppVersion + ", deviceId=" + deviceId + ", networkType=" + networkType + ", operatorName=" + operatorName
				+ ", mcc=" + mcc + ", mnc=" + mnc + ", pci=" + pci + ", cgi=" + cgi + ", rsrp=" + rsrp + ", rsrq=" + rsrq + ", rscp=" + rscp
				+ ", sinr=" + sinr + ", rxLevel=" + rxLevel + ", jitter=" + jitter + ", latency=" + latency + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", callStartTime=" + callStartTime + ", callEndTime=" + callEndTime + ", callDuration=" + callDuration
				+ ", releaseCause=" + releaseCause + ", callType=" + callType + ", sessionThroughput=" + sessionThroughput + "]";
	}
}
