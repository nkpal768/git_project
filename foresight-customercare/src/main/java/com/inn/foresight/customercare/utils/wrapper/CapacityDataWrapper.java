package com.inn.foresight.customercare.utils.wrapper;

import com.inn.core.generic.wrapper.JpaWrapper;
import com.inn.core.generic.wrapper.RestWrapper;

@RestWrapper
@JpaWrapper
public class CapacityDataWrapper {

	private Integer band;
	private Double latitude;
	private Double longitude;
	private String neName;
	private String sapid;
	private Integer cellId;
	private Boolean isHighlyUtilized;
	private String neId;

	public CapacityDataWrapper(Boolean isHighlyUtilized, String neName, Double latitude, Double longitude, Integer cellId, Integer band,
			String neId) {
		super();
		this.band = band;
		this.latitude = latitude;
		this.longitude = longitude;
		this.neName = neName;
		this.sapid = neName;
		this.cellId = cellId;
		this.isHighlyUtilized = isHighlyUtilized;
		this.neId = neId;
	}

	public Integer getBand() {
		return band;
	}

	public void setBand(Integer band) {
		this.band = band;
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

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	public Boolean getIsHighlyUtilized() {
		return isHighlyUtilized;
	}

	public void setIsHighlyUtilized(Boolean isHighlyUtilized) {
		this.isHighlyUtilized = isHighlyUtilized;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	@Override
	public String toString() {
		return "CapacityDataWrapper [band=" + band + ", latitude=" + latitude + ", longitude=" + longitude + ", neName=" + neName + ", sapid=" + sapid
				+ ", cellId=" + cellId + ", isHighlyUtilized=" + isHighlyUtilized + ", neId=" + neId + "]";
	}
}
