package com.re.paas.api.listable;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

public interface ListableIndex<T> extends Resource {

	String id();

	String namespace();

	Map<String, T> getAll(List<String> keys);

	default ClientSearchSpec clientSearchSpec() {
		return null;
	}

	default String asString() {
		return getDelegate().toString(this);
	}

	@Override
	default SpiType getSpiType() {
		return SpiType.LISTABLE_INDEX;
	}

	static AbstractListableIndexDelegate getDelegate() {
		return Singleton.get(AbstractListableIndexDelegate.class);
	}

	static ListableIndex<?> fromString(String identifier) {
		return getDelegate().fromString(identifier);
	}

	/**
	 * This is used to define extra information for listable indexes that also
	 * represent search indexes as well
	 * 
	 * @author anthonyanyanwu
	 *
	 */
	public static class ClientSearchSpec {

		private ClientRBRef name;
		private boolean displayInAppShell;
		private String icon;
		private String listingPageUrl;

		public ClientRBRef getName() {
			return name;
		}

		/**
		 * The friendly name of the search index
		 * @param name
		 * @return
		 */
		public ClientSearchSpec setName(ClientRBRef name) {
			this.name = name;
			return this;
		}

		public String getIcon() {
			return icon;
		}

		public ClientSearchSpec setIcon(String icon) {
			this.icon = icon;
			return this;
		}

		public String getListingPageUrl() {
			return listingPageUrl;
		}

		/**
		 * The URI that is used to orchestrate searches for this search index..
		 * The query param "keyword" is append to this url, and a new page opens
		 * up, for the users to view results
		 * 
		 * @param listingPageUrl
		 * @return
		 */
		public ClientSearchSpec setListingPageUrl(String listingPageUrl) {
			this.listingPageUrl = listingPageUrl;
			return this;
		}

		public boolean isDisplayInAppShell() {
			return displayInAppShell;
		}

		public ClientSearchSpec setDisplayInAppShell(boolean displayInAppShell) {
			this.displayInAppShell = displayInAppShell;
			return this;
		}
	}
}
