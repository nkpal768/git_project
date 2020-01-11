package com.inn.foresight.customercare.utils.wrapper;

import java.util.Date;

import com.inn.commons.maps.LatLng;
import com.inn.core.generic.wrapper.JpaWrapper;
import com.inn.core.generic.wrapper.RestWrapper;
import com.inn.foresight.core.infra.utils.enums.Domain;
import com.inn.foresight.core.infra.utils.enums.NEStatus;
import com.inn.foresight.core.infra.utils.enums.Vendor;

@RestWrapper
@JpaWrapper
public class CustomerCareSiteWrapper extends LatLng {

	private static final long serialVersionUID = 1969262316808386724L;

	private String sapid;
	private String neName;
	private String neId;
	private Integer cellId;
	private String neStatus;
	private Integer pci;
	private Integer azimuth;
	private String neFrequency;
	private String taskName;
	private String taskStatus;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private String completionStatus;
	private String domain;
	private String vendor;
	private String carrier;
	private Integer sectorId;
	private Boolean isHighlyUtilized;
	private String parentneId;
	private Integer sector;
	private String technology;
	private String neType;

	public CustomerCareSiteWrapper() {
		super();
	}

	/** on air site constructor */
	public CustomerCareSiteWrapper(Double latitude, Double longitude, String sapid, String neName, String neId, Integer cellId, String neStatus,
			Integer pci, Integer azimuth, String neFrequency, String domain, String vendor, String carrier, Integer sectorId,
			Boolean isHighlyUtilized, String parentneId, String technology, String neType) {
		super();
		this.sapid = sapid;
		this.neName = neName;
		this.neId = neId;
		this.cellId = cellId;
		this.neStatus = neStatus;
		super.latitude = latitude;
		super.longitude = longitude;
		this.pci = pci;
		this.azimuth = azimuth;
		this.neFrequency = neFrequency;
		this.domain = domain;
		this.vendor = vendor;
		this.carrier = carrier;
		this.sectorId = sectorId;
		this.isHighlyUtilized = isHighlyUtilized;
		this.parentneId = parentneId;
		this.sector = sectorId;
		this.technology = technology;
		this.neType = neType;
	}

	/**
	 * this construct is used in customercaredaoimpl --
	 * getPlannedSiteForCustomerCare()
	 */
	public CustomerCareSiteWrapper(String neName, String neId, Integer cellId, Double latitude, Double longitude, Integer pci, Integer azimuth,
			String neFrequency, NEStatus neStatus, String taskName, String taskStatus, Date plannedStartDate, Date plannedEndDate,
			Date actualStartDate, Date actualEndDate, String completionStatus, Vendor vendor, Domain domain) {
		super();
		this.sapid = neName;
		this.neName = neName;
		this.neId = neId;
		this.cellId = cellId;
		super.latitude = latitude;
		super.longitude = longitude;
		this.pci = pci;
		this.azimuth = azimuth;
		this.neFrequency = neFrequency;
		this.neStatus = neStatus != null ? neStatus.name() : null;
		this.taskName = taskName;
		this.taskStatus = taskStatus;
		this.plannedStartDate = plannedStartDate;
		this.plannedEndDate = plannedEndDate;
		this.actualStartDate = actualStartDate;
		this.actualEndDate = actualEndDate;
		this.completionStatus = completionStatus;
		this.vendor = vendor != null ? vendor.name() : null;
		this.domain = domain != null ? domain.name() : null;
	}

	public Integer getSector() {
		return sector;
	}

	public void setSector(Integer sector) {
		this.sector = sector;
	}

	public String getParentneId() {
		return parentneId;
	}

	public void setParentneId(String parentneId) {
		this.parentneId = parentneId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public String getCompletionStatus() {
		return completionStatus;
	}

	public void setCompletionStatus(String completionStatus) {
		this.completionStatus = completionStatus;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	@Override
	public Double getLatitude() {
		return latitude;
	}

	@Override
	public Double getLongitude() {
		return longitude;
	}

	public String getNeStatus() {
		return neStatus;
	}

	public void setNeStatus(String neStatus) {
		this.neStatus = neStatus;
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

	public String getNeFrequency() {
		return neFrequency;
	}

	public void setNeFrequency(String neFrequency) {
		this.neFrequency = neFrequency;
	}

	@Override
	public LatLng setLatitude(Double latitude) {
		return super.setLatitude(latitude);
	}

	@Override
	public LatLng setLongitude(Double longitude) {
		return super.setLongitude(longitude);
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
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
		return "CustomerCareSiteWrapper [sapid=" + sapid + ", neName=" + neName + ", neId=" + neId + ", cellId=" + cellId + ", neStatus=" + neStatus
				+ ", pci=" + pci + ", azimuth=" + azimuth + ", neFrequency=" + neFrequency + ", taskName=" + taskName + ", taskStatus=" + taskStatus
				+ ", plannedStartDate=" + plannedStartDate + ", plannedEndDate=" + plannedEndDate + ", actualStartDate=" + actualStartDate
				+ ", actualEndDate=" + actualEndDate + ", completionStatus=" + completionStatus + ", domain=" + domain + ", vendor=" + vendor
				+ ", carrier=" + carrier + ", sectorId=" + sectorId + ", isHighlyUtilized=" + isHighlyUtilized + ", parentneId=" + parentneId
				+ ", sector=" + sector + ", technology=" + technology + ", neType=" + neType + "]";
	}

}
