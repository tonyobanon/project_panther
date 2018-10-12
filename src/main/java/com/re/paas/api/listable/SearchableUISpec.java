package com.re.paas.api.listable;

public class SearchableUISpec {

	private String name;
	private String icon;
	private String listingPageUrl;

	public String getName() {
		return name;
	}

	public SearchableUISpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public SearchableUISpec setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public String getListingPageUrl() {
		return listingPageUrl;
	}

	public SearchableUISpec setListingPageUrl(String listingPageUrl) {
		this.listingPageUrl = listingPageUrl;
		return this;
	}

}
