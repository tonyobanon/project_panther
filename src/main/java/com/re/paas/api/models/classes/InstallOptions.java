package com.re.paas.api.models.classes;

import java.util.ArrayList;

public class InstallOptions {

	//Company Information
	
	public String companyName;
	public String companyLogoUrl;
	
	public String audience;
	public String country;
	
	//Administrators
	public ArrayList<UserProfileSpec> admins;

	  
	//Mail Credentials -->
	public MailCredentialSpec mailCredentials;
	 

	//System Settings
	public String currency;
	public String timezone;
	public String language;
	
	
	
	public String getCompanyName() {
		return companyName;
	}
	
	public String getCompanyLogoUrl() {
		return companyLogoUrl;
	}


	public String getAudience() {
		return audience;
	}

	public String getCountry() {
		return country;
	}

	public ArrayList<UserProfileSpec> getAdmins() {
		return admins;
	}
	
	public MailCredentialSpec getMailCredentials() {
		return mailCredentials;
	}

	public String getCurrency() {
		return currency;
	}

	public String getTimezone() {
		return timezone;
	}

	public String getLanguage() {
		return language;
	}
}
