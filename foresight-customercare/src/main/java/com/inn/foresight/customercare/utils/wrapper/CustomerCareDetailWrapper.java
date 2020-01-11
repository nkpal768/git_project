
package com.inn.foresight.customercare.utils.wrapper;

import java.io.Serializable;

public class CustomerCareDetailWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer mcc;
	private Integer mnc;
	private String imei;
	private String type;
	private Double sinr;
	private Integer rsrp;
	private Double jitter;
	private Double google;
	private String band4G;
	private Double upload;
	private String chipset;
	private Integer cellId;
	private String address;
	private Double youtube;
	private Double latency;
	private String btsCode;
	private Integer rssi;
	private Integer rsrq;
	private String imageUrl;
	private Double pcktLoss;
	private Double download;
	private Double facebook;
	private String serialNo;
	private String operator;
	private String deviceOS;
	private String neStatus;
	private Double latitude;
	private Double longitude;
	private String gpsStatus;
	private String deviceName;
	private Long capturedTime;
	private String buildNumber;
	private String versionName;
	private String dataSimDetail;
	private String dualSimEnable;
	private String lteCompatible;
	private String voiceSimDetail;
	private String firmwareVersion;
	private String deviceCompatiable;
	private String deviceFirmwareVersion;
	private String nvChipset;
	private String make;

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Integer getMcc() {
		return mcc;
	}

	public void setMcc(Integer mcc) {
		this.mcc = mcc;
	}

	public Integer getMnc() {
		return mnc;
	}

	public void setMnc(Integer mnc) {
		this.mnc = mnc;
	}

	public Double getSinr() {
		return sinr;
	}

	public void setSinr(Double sinr) {
		this.sinr = sinr;
	}

	public Integer getRsrp() {
		return rsrp;
	}

	public void setRsrp(Integer rsrp) {
		this.rsrp = rsrp;
	}

	public Double getGoogle() {
		return google;
	}

	public void setGoogle(Double google) {
		this.google = google;
	}

	public Double getUpload() {
		return upload;
	}

	public void setUpload(Double upload) {
		this.upload = upload;
	}

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	public Double getYoutube() {
		return youtube;
	}

	public void setYoutube(Double youtube) {
		this.youtube = youtube;
	}

	public Double getLatency() {
		return latency;
	}

	public void setLatency(Double latency) {
		this.latency = latency;
	}

	public Double getDownload() {
		return download;
	}

	public void setDownload(Double download) {
		this.download = download;
	}

	public Double getFacebook() {
		return facebook;
	}

	public void setFacebook(Double facebook) {
		this.facebook = facebook;
	}

	public String getGpsStatus() {
		return gpsStatus;
	}

	public void setGpsStatus(String gpsStatus) {
		this.gpsStatus = gpsStatus;
	}

	public Long getCapturedTime() {
		return capturedTime;
	}

	public void setCapturedTime(Long capturedTime) {
		this.capturedTime = capturedTime;
	}

	public String getBand4G() {
		return band4G;
	}

	public void setBand4G(String band4g) {
		band4G = band4g;
	}

	public String getDualSimEnable() {
		return dualSimEnable;
	}

	public void setDualSimEnable(String dualSimEnable) {
		this.dualSimEnable = dualSimEnable;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getChipset() {
		return chipset;
	}

	public void setChipset(String chipset) {
		this.chipset = chipset;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDeviceOS() {
		return deviceOS;
	}

	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getDeviceCompatiable() {
		return deviceCompatiable;
	}

	public void setDeviceCompatiable(String deviceCompatiable) {
		this.deviceCompatiable = deviceCompatiable;
	}

	public String getDeviceFirmwareVersion() {
		return deviceFirmwareVersion;
	}

	public void setDeviceFirmwareVersion(String deviceFirmwareVersion) {
		this.deviceFirmwareVersion = deviceFirmwareVersion;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDataSimDetail() {
		return dataSimDetail;
	}

	public void setDataSimDetail(String dataSimDetail) {
		this.dataSimDetail = dataSimDetail;
	}

	public String getVoiceSimDetail() {
		return voiceSimDetail;
	}

	public void setVoiceSimDetail(String voiceSimDetail) {
		this.voiceSimDetail = voiceSimDetail;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getBtsCode() {
		return btsCode;
	}

	public void setBtsCode(String btsCode) {
		this.btsCode = btsCode;
	}

	public String getNeStatus() {
		return neStatus;
	}

	public void setNeStatus(String neStatus) {
		this.neStatus = neStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLteCompatible() {
		return lteCompatible;
	}

	public void setLteCompatible(String lteCompatible) {
		this.lteCompatible = lteCompatible;
	}

	public Double getJitter() {
		return jitter;
	}

	public void setJitter(Double jitter) {
		this.jitter = jitter;
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
	}

	public Integer getRsrq() {
		return rsrq;
	}

	public void setRsrq(Integer rsrq) {
		this.rsrq = rsrq;
	}

	public Double getPcktLoss() {
		return pcktLoss;
	}

	public void setPcktLoss(Double pcktLoss) {
		this.pcktLoss = pcktLoss;
	}

	public String getNvChipset() {
		return nvChipset;
	}

	public void setNvChipset(String nvChipset) {
		this.nvChipset = nvChipset;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	@Override
	public String toString() {
		return "CustomerCareDetailWrapper [mcc=" + mcc + ", mnc=" + mnc + ", imei=" + imei + ", type=" + type + ", sinr=" + sinr + ", rsrp=" + rsrp
				+ ", jitter=" + jitter + ", google=" + google + ", band4G=" + band4G + ", upload=" + upload + ", chipset=" + chipset + ", cellId="
				+ cellId + ", address=" + address + ", youtube=" + youtube + ", latency=" + latency + ", btsCode=" + btsCode + ", rssi=" + rssi
				+ ", rsrq=" + rsrq + ", imageUrl=" + imageUrl + ", pcktLoss=" + pcktLoss + ", download=" + download + ", facebook=" + facebook
				+ ", serialNo=" + serialNo + ", operator=" + operator + ", deviceOS=" + deviceOS + ", neStatus=" + neStatus + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", gpsStatus=" + gpsStatus + ", deviceName=" + deviceName + ", capturedTime=" + capturedTime
				+ ", buildNumber=" + buildNumber + ", versionName=" + versionName + ", dataSimDetail=" + dataSimDetail + ", dualSimEnable="
				+ dualSimEnable + ", lteCompatible=" + lteCompatible + ", voiceSimDetail=" + voiceSimDetail + ", firmwareVersion=" + firmwareVersion
				+ ", deviceCompatiable=" + deviceCompatiable + ", deviceFirmwareVersion=" + deviceFirmwareVersion + ", nvChipset=" + nvChipset
				+ ", make=" + make + "]";
	}

}
