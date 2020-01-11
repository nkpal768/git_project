package com.inn.foresight.customercare.utils.wrapper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.inn.core.generic.wrapper.JpaWrapper;
import com.inn.core.generic.wrapper.RestWrapper;
import com.inn.product.um.role.utils.wrapper.UserRoleGeographyDetails;

@RestWrapper
@JpaWrapper
public class CustomerCareUserWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer userid;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String contactNumber;
	private Date creationTime;
	private Date modificationTime;
	private Boolean deleted;
	private Boolean enabled;
	private String userSearch;
	private String userName;
	private String imagePath;
	private List<UserRoleGeographyDetails> role;
	private UserRoleGeographyDetails activeRole;
	private String vendor;

	public String getVendor() {
		return this.vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<UserRoleGeographyDetails> getRole() {
		return this.role;
	}

	public void setRole(List<UserRoleGeographyDetails> role) {
		this.role = role;
	}

	public UserRoleGeographyDetails getActiveRole() {
		return this.activeRole;
	}

	public void setActiveRole(UserRoleGeographyDetails activeRole) {
		this.activeRole = activeRole;
	}

	public CustomerCareUserWrapper() {
	}

	public Integer getUserid() {
		return this.userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public CustomerCareUserWrapper(String firstName, String lastName, String userName, String email, String password, String contactNumber,
			Boolean enabled) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.contactNumber = contactNumber;
		this.enabled = enabled;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNumber() {
		return this.contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Date getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getModificationTime() {
		return this.modificationTime;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public Boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getUserSearch() {
		return this.userSearch;
	}

	public void setUserSearch(String userSearch) {
		this.userSearch = userSearch;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "CustomerCareUserWrapper [userid=" + userid + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", contactNumber=" + contactNumber + ", creationTime=" + creationTime + ", modificationTime="
				+ modificationTime + ", deleted=" + deleted + ", enabled=" + enabled + ", userSearch=" + userSearch + ", userName=" + userName
				+ ", imagePath=" + imagePath + ", role=" + role + ", activeRole=" + activeRole + ", vendor=" + vendor + "]";
	}

}