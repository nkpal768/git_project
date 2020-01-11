package com.inn.foresight.customercare.utils.wrapper;

import java.io.Serializable;

public class CustomerCareSectorWrapper implements Serializable {

	private static final long serialVersionUID = 5303023606775492172L;

	private Integer cellId;
	private Integer pci;
	private Integer azimuth;
	private String neId;
	private String neFrequency;
	private String carrier;
	private Integer sectorId;
	private Boolean isHighlyUtilized;
	private String parentneId;
	private String technology;
	private String neType;

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	public Integer getPci() {
		return pci;
	}

	public void setPci(Integer pci) {
		this.pci = pci;
	}

	public Integer getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(Integer azimuth) {
		this.azimuth = azimuth;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getNeFrequency() {
		return neFrequency;
	}

	public void setNeFrequency(String neFrequency) {
		this.neFrequency = neFrequency;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Integer getSectorId() {
		return sectorId;
	}

	public void setSectorId(Integer sectorId) {
		this.sectorId = sectorId;
	}

	public Boolean getIsHighlyUtilized() {
		return isHighlyUtilized;
	}

	public void setIsHighlyUtilized(Boolean isHighlyUtilized) {
		this.isHighlyUtilized = isHighlyUtilized;
	}

	public String getParentneId() {
		return parentneId;
	}

	public void setParentneId(String parentneId) {
		this.parentneId = parentneId;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	@Override
	public String toString() {
		return "CustomerCareSectorWrapper [cellId=" + cellId + ", pci=" + pci + ", azimuth=" + azimuth + ", neId=" + neId + ", neFrequency="
				+ neFrequency + ", carrier=" + carrier + ", sectorId=" + sectorId + ", isHighlyUtilized=" + isHighlyUtilized + ", parentneId="
				+ parentneId + ", technology=" + technology + ", neType=" + neType + "]";
	}

}
