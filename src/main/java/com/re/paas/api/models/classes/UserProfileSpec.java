package com.re.paas.api.models.classes;

import java.util.Date;

public class UserProfileSpec {
	
	private Long id;
	private Long applicationId;

	private String email;

	private String firstName;
	private String middleName;
	private String lastName;
	private String image;

	private String phone;
	private Date dateOfBirth;
	private Gender gender;

	private String address;

	private Integer city;
	private String territory;
	private String country;
	
	private String facebookProfile;
	private String twitterProfile;
	private String linkedInProfile;
	private String skypeProfile;
	
	private String preferredLocale;

	
	public Long getId() {
		return id;
	}

	public UserProfileSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public UserProfileSpec setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
		return this;
	}

	public String getEmail() {
		return email;
	} 

	public UserProfileSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public UserProfileSpec setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getMiddleName() {
		return middleName;
	}

	public UserProfileSpec setMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public UserProfileSpec setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public String getImage() {
		return image;
	}

	public UserProfileSpec setImage(String image) {
		this.image = image;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public UserProfileSpec setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public UserProfileSpec setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		return this;
	}

	public Gender getGender() {
		return gender;
	}

	public UserProfileSpec setGender(Gender gender) {
		this.gender = gender;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public UserProfileSpec setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public UserProfileSpec setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public UserProfileSpec setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public UserProfileSpec setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getFacebookProfile() {
		return facebookProfile;
	}

	public UserProfileSpec setFacebookProfile(String facebookProfile) {
		this.facebookProfile = facebookProfile;
		return this;
	}

	public String getTwitterProfile() {
		return twitterProfile;
	}

	public UserProfileSpec setTwitterProfile(String twitterProfile) {
		this.twitterProfile = twitterProfile;
		return this;
	}

	public String getLinkedInProfile() {
		return linkedInProfile;
	}

	public UserProfileSpec setLinkedInProfile(String linkedInProfile) {
		this.linkedInProfile = linkedInProfile;
		return this;
	}

	public String getSkypeProfile() {
		return skypeProfile;
	}

	public UserProfileSpec setSkypeProfile(String skypeProfile) {
		this.skypeProfile = skypeProfile;
		return this;
	}

	public String getPreferredLocale() {
		return preferredLocale;
	}

	public UserProfileSpec setPreferredLocale(String preferredLocale) {
		this.preferredLocale = preferredLocale;
		return this;
	}
}
