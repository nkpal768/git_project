package com.inn.foresight.customercare.utils.wrapper;

import java.util.List;

import com.inn.commons.maps.LatLng;
import com.inn.core.generic.wrapper.JpaWrapper;

@JpaWrapper
public class CustomerCareNEWrapper extends LatLng {

	private String sapid;
	private String neName;
	private String neStatus;
	private String domain;
	private String vendor;
	private List<CustomerCareSectorWrapper> customerCareSectorWrappers;

	public CustomerCareNEWrapper(Double latitude, Double longitude, String neName, String neStatus, String domain, String vendor,
			List<CustomerCareSectorWrapper> customerCareSectorWrappers) {
		super(latitude, longitude);
		this.neName = neName;
		this.sapid = neName;
		this.neStatus = neStatus;
		this.domain = domain;
		this.vendor = vendor;
		this.customerCareSectorWrappers = customerCareSectorWrappers;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getNeStatus() {
		return neStatus;
	}

	public void setNeStatus(String neStatus) {
		this.neStatus = neStatus;
	}

	public List<CustomerCareSectorWrapper> getCustomerCareSectorWrappers() {
		return customerCareSectorWrappers;
	}

	public void setCustomerCareSectorWrappers(List<CustomerCareSectorWrapper> customerCareSectorWrappers) {
		this.customerCareSectorWrappers = customerCareSectorWrappers;
	}

	@Override
	public LatLng setLatitude(Double latitude) {
		return super.setLatitude(latitude);
	}

	@Override
	public LatLng setLongitude(Double longitude) {
		return super.setLongitude(longitude);
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

	@Override
	public String toString() {
		return "CustomerCareNEWrapper [sapid=" + sapid + ", neName=" + neName + ", neStatus=" + neStatus + ", domain=" + domain + ", vendor=" + vendor
				+ ", customerCareSectorWrappers=" + customerCareSectorWrappers + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
