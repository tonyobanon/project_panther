package com.re.paas.internal.models.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.IndexedNameSpec;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.models.classes.Gender;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationMessageSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationReviewSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationWhistleblowMessageSpec;
import com.re.paas.apps.rex.classes.spec.AgentSpec;
import com.re.paas.apps.rex.classes.spec.CityFeaturesSpec;
import com.re.paas.apps.rex.classes.spec.IssueResolution;
import com.re.paas.apps.rex.classes.spec.ListedProperty;
import com.re.paas.apps.rex.classes.spec.ListedRentPropertySpec;
import com.re.paas.apps.rex.classes.spec.ListedSalePropertySpec;
import com.re.paas.apps.rex.classes.spec.PropertyAvailabilityStatus;
import com.re.paas.apps.rex.classes.spec.PropertyFloorPlanSpec;
import com.re.paas.apps.rex.classes.spec.PropertyListingStatus;
import com.re.paas.apps.rex.classes.spec.PropertyListingStatusSpec;
import com.re.paas.apps.rex.classes.spec.PropertyPOISpec;
import com.re.paas.apps.rex.classes.spec.PropertyPriceRuleSpec;
import com.re.paas.apps.rex.classes.spec.PropertySpec;
import com.re.paas.apps.rex.classes.spec.PropertyType;
import com.re.paas.apps.rex.classes.spec.PropertyUpdateSpec;
import com.re.paas.apps.rex.models.tables.AgentOrganizationMessageTable;
import com.re.paas.apps.rex.models.tables.AgentOrganizationReviewTable;
import com.re.paas.apps.rex.models.tables.AgentOrganizationTable;
import com.re.paas.apps.rex.models.tables.AgentOrganizationWhistleblowMessageTable;
import com.re.paas.apps.rex.models.tables.AgentTable;
import com.re.paas.apps.rex.models.tables.CityFeaturesTable;
import com.re.paas.apps.rex.models.tables.ListedRentPropertyTable;
import com.re.paas.apps.rex.models.tables.ListedSalePropertyTable;
import com.re.paas.apps.rex.models.tables.PropertyFloorPlanTable;
import com.re.paas.apps.rex.models.tables.PropertyListingStatusTable;
import com.re.paas.apps.rex.models.tables.PropertyPOITable;
import com.re.paas.apps.rex.models.tables.PropertyPriceRuleTable;
import com.re.paas.apps.rex.models.tables.PropertyTable;
import com.re.paas.internal.billing.BaseCardInfo;
import com.re.paas.internal.billing.CardInfo;
import com.re.paas.internal.billing.CseTokenInfo;
import com.re.paas.internal.billing.InvoiceItem;
import com.re.paas.internal.billing.InvoicePaymentSpec;
import com.re.paas.internal.billing.InvoicePaymentStatus;
import com.re.paas.internal.billing.InvoiceSpec;
import com.re.paas.internal.billing.InvoiceStatus;
import com.re.paas.internal.classes.spec.ActivitySpec;
import com.re.paas.internal.classes.spec.BlobSpec;
import com.re.paas.internal.classes.spec.PaymentOptions;
import com.re.paas.internal.classes.spec.PriceRuleOperator;
import com.re.paas.internal.classes.spec.PublicHolidaySpec;
import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;
import com.re.paas.internal.models.tables.ActivityStreamTable;
import com.re.paas.internal.models.tables.BlobTable;
import com.re.paas.internal.models.tables.IndexedNameTable;
import com.re.paas.internal.models.tables.forms.FormCompositeFieldTable;
import com.re.paas.internal.models.tables.forms.FormSimpleFieldTable;
import com.re.paas.internal.models.tables.locations.PublicHolidayTable;
import com.re.paas.internal.models.tables.payments.CardTable;
import com.re.paas.internal.models.tables.payments.InvoiceItemTable;
import com.re.paas.internal.models.tables.payments.InvoicePaymentHistoryTable;
import com.re.paas.internal.models.tables.payments.InvoicePaymentTable;
import com.re.paas.internal.models.tables.payments.InvoiceTable;
import com.re.paas.internal.models.tables.users.BaseUserEntity;

public class EntityHelper {

	public static CompositeField toObjectModel(FormCompositeFieldTable entity) {

		CompositeField o = new CompositeField(entity.getId(), entity.getTitle().toString())
				.setItemsSource(entity.getItemsSource()).setDefaultSelections(entity.getDefaultSelections())
				.setAllowMultipleChoice(entity.getAllowMultipleChoice());

		for (Map.Entry<ClientRBRef, Object> e : entity.getOptions().entrySet()) {
			o.withItem(e.getKey(), e.getValue());
		}

		o.setSortOrder(entity.getSortOrder());
		o.setIsRequired(entity.getIsRequired());
		o.setIsVisible(entity.getIsVisible());
		o.setIsDefault(entity.getIsDefault());

		return o;
	}

	public static FormCompositeFieldTable fromObjectModel(String sectionId, Boolean isInternal, CompositeField spec) {

		FormCompositeFieldTable o = new FormCompositeFieldTable()

				.setId(spec.getId() != null ? (String) spec.getId() : Utils.newRandom()).setSection(sectionId)

				.setSortOrder(spec.getSortOrder()).setIsRequired(spec.getIsRequired()).setIsVisible(spec.getIsVisible())
				.setIsDefault(spec.getIsDefault() ? true : null)

				.setItemsSource(spec.getItemsSource()).setDefaultSelections(spec.getDefaultSelections())
				.setAllowMultipleChoice(spec.isAllowMultipleChoice())

				.setDateCreated(Dates.now());

		if (spec.getTitle() instanceof ClientRBRef) {
			o.setTitle((ClientRBRef) spec.getTitle());
		}

		Map<ClientRBRef, Object> options = new HashMap<ClientRBRef, Object>();

		for (Map.Entry<Object, Object> e : spec.getItems().entrySet()) {

			if (e.getKey() instanceof ClientRBRef) {
				options.put((ClientRBRef) e.getKey(), e.getValue());
			}
		}

		o.setOptions(options);
		
		if(!options.isEmpty()) {
			//Assert that all keys are instances of ClientRBRef
			for (Map.Entry<Object, Object> e : spec.getItems().entrySet()) {
				assert (e.getKey() instanceof ClientRBRef);
			}
		}

		return o;
	}

	public static SimpleField toObjectModel(FormSimpleFieldTable entity) {

		SimpleField o = new SimpleField(entity.getId(), InputType.valueOf(entity.getInputType()),
				entity.getTitle().toString())

						.setDefaultValue(entity.getDefaultValue());

		o.setSortOrder(entity.getSortOrder());
		o.setIsRequired(entity.getIsRequired());
		o.setIsVisible(entity.getIsVisible());
		o.setIsDefault(entity.getIsDefault());

		return o;
	}

	public static FormSimpleFieldTable fromObjectModel(String sectionId, Boolean isDefault, SimpleField spec) {

		FormSimpleFieldTable o = new FormSimpleFieldTable()

				.setId(spec.getId() != null ? (String) spec.getId() : Utils.newRandom())

				.setSection(sectionId).setInputType(spec.getInputType().toString())

				.setSortOrder(spec.getSortOrder()).setDefaultValue(spec.getDefaultValue())
				.setIsRequired(spec.getIsRequired()).setIsVisible(spec.getIsVisible())
				.setIsDefault(isDefault ? true : null)

				.setDateCreated(Dates.now());

		if (spec.getTitle() instanceof ClientRBRef) {
			o.setTitle((ClientRBRef) spec.getTitle());
		}

		return o;
	}

	public static UserProfileSpec toObjectModel(BaseUserEntity entity) {

		UserProfileSpec o = new UserProfileSpec()
				.setApplicationId(entity.getApplicationId())
				.setEmail(entity.getEmail())
				// .setPassword(entity.getPassword())
				.setFirstName(entity.getFirstName()).setMiddleName(entity.getMiddleName())
				.setLastName(entity.getLastName()).setImage(entity.getImage()).setPhone(entity.getPhone())
				.setDateOfBirth(entity.getDateOfBirth()).setGender(Gender.from(entity.getGender()))
				.setAddress(entity.getAddress()).setCity(entity.getCity()).setTerritory(entity.getTerritory())
				.setCountry(entity.getCountry()).setFacebookProfile(entity.getFacebookProfile())
				.setTwitterProfile(entity.getTwitterProfile()).setLinkedInProfile(entity.getLinkedInProfile())
				.setSkypeProfile(entity.getSkypeProfile()).setRole(entity.getRole())
				.setPreferredLocale(entity.getPreferredLocale());

		return o;
	}

	public static BaseUserEntity fromObjectModel(String role, Long principal, UserProfileSpec spec) {

		BaseUserEntity o = new BaseUserEntity()

				.setApplicationId(spec.getApplicationId())
				.setEmail(spec.getEmail()).setPassword(spec.getPassword()).setFirstName(spec.getFirstName())
				.setMiddleName(spec.getMiddleName()).setLastName(spec.getLastName()).setImage(spec.getImage())
				.setPhone(spec.getPhone()).setDateOfBirth(spec.getDateOfBirth()).setGender(spec.getGender().getValue())
				.setAddress(spec.getAddress()).setCity(spec.getCity()).setTerritory(spec.getTerritory())
				.setCountry(spec.getCountry()).setFacebookProfile(spec.getFacebookProfile())
				.setTwitterProfile(spec.getTwitterProfile()).setLinkedInProfile(spec.getLinkedInProfile())
				.setSkypeProfile(spec.getSkypeProfile()).setRole(role).setPrincipal(principal)
				.setDateCreated(Dates.now()).setPreferredLocale(spec.getPreferredLocale());

		return o;
	}

	public static BlobSpec toObjectModel(BlobTable entity) {

		BlobSpec o = new BlobSpec().setId(entity.getId()).setData(entity.getData()).setSize(entity.getSize())
				.setMimeType(entity.getMimeType()).setDateCreated(entity.getDateCreated());

		return o;
	}

	public static BlobTable fromObjectModel(BlobSpec spec) {

		BlobTable o = new BlobTable().setId(spec.getId()).setData(spec.getData()).setSize(spec.getSize())
				.setMimeType(spec.getMimeType()).setDateCreated(spec.getDateCreated());

		return o;
	}

	public static IndexedNameSpec toObjectModel(IndexedNameTable entity) {

		IndexedNameSpec o = new IndexedNameSpec().setKey(entity.getEntityId()).setX(entity.getX()).setY(entity.getY())
				.setZ(entity.getZ());

		return o;
	}

	public static ActivitySpec toObjectModel(ActivityStreamTable entity) {

		// Sentence s = GsonFactory.newInstance().fromJson(entity.getActivity(),
		// Sentence.class);

		ActivitySpec o = new ActivitySpec().setId(entity.getId()).setHtml(entity.getActivity())
				.setSubjectImage(entity.getSubjectImage()).setPersonImage(entity.getPersonImage())
				.setLikes(entity.getLikes()).setDate(entity.getDate());

		return o;
	}

	public static AgentOrganizationReviewSpec toObjectModel(AgentOrganizationReviewTable entity) {

		AgentOrganizationReviewSpec o = new AgentOrganizationReviewSpec().setId(entity.getId())
				.setAgentOrganization(entity.getAgentOrganization()).setUserId(entity.getUserId())
				.setDescription(entity.getDescription()).setRating(entity.getRating())
				.setDateCreated(entity.getDateCreated());

		return o;
	}

	public static AgentOrganizationReviewTable fromObjectModel(AgentOrganizationReviewSpec spec) {

		AgentOrganizationReviewTable o = new AgentOrganizationReviewTable()
				.setAgentOrganization(spec.getAgentOrganization()).setUserId(spec.getUserId())
				.setDescription(spec.getDescription()).setRating(spec.getRating())
				.setDateCreated(Dates.now());

		return o;
	}

	public static AgentOrganizationMessageSpec toObjectModel(AgentOrganizationMessageTable entity) {

		AgentOrganizationMessageSpec o = new AgentOrganizationMessageSpec().setId(entity.getId())
				.setAgentOrganization(entity.getAgentOrganization()).setMessage(entity.getMessage())
				.setResolution(IssueResolution.from(entity.getResolution()))
				.setResolutionHistory(entity.getResolutionHistory()).setUserId(entity.getUserId())
				.setName(entity.getName()).setEmail(entity.getEmail()).setMobile(entity.getMobile())
				.setIsRead(entity.getIsRead()).setDateCreated(entity.getDateCreated())
				.setDateCreated(entity.getDateUpdated());

		return o;
	}

	public static AgentOrganizationMessageTable fromObjectModel(AgentOrganizationMessageSpec spec) {

		AgentOrganizationMessageTable o = new AgentOrganizationMessageTable()
				.setAgentOrganization(spec.getAgentOrganization()).setMessage(spec.getMessage())
				.setUserId(spec.getUserId()).setName(spec.getName()).setResolution(IssueResolution.OPEN.getValue())
				.setResolutionHistory(Maps.newHashMap()).setEmail(spec.getEmail()).setMobile(spec.getMobile())
				.setIsRead(false).setDateCreated(Dates.now()).setDateUpdated(Dates.now());

		return o;
	}

	public static AgentOrganizationWhistleblowMessageSpec toObjectModel(
			AgentOrganizationWhistleblowMessageTable entity) {

		AgentOrganizationWhistleblowMessageSpec o = new AgentOrganizationWhistleblowMessageSpec().setId(entity.getId())
				.setAgentOrganization(entity.getAgentOrganization()).setMessage(entity.getMessage())
				.setResolution(IssueResolution.from(entity.getResolution()))
				.setResolutionHistory(entity.getResolutionHistory()).setUserId(entity.getUserId())
				.setName(entity.getName()).setEmail(entity.getEmail()).setMobile(entity.getMobile())
				.setIsRead(entity.getIsRead()).setDateCreated(entity.getDateCreated())
				.setDateCreated(entity.getDateUpdated());

		return o;
	}

	public static AgentOrganizationWhistleblowMessageTable fromObjectModel(
			AgentOrganizationWhistleblowMessageSpec spec) {

		AgentOrganizationWhistleblowMessageTable o = new AgentOrganizationWhistleblowMessageTable()
				.setAgentOrganization(spec.getAgentOrganization()).setMessage(spec.getMessage())
				.setUserId(spec.getUserId()).setName(spec.getName()).setResolution(IssueResolution.OPEN.getValue())
				.setResolutionHistory(Maps.newHashMap()).setEmail(spec.getEmail()).setMobile(spec.getMobile())
				.setIsRead(false).setDateCreated(Dates.now()).setDateUpdated(Dates.now());

		return o;
	}

	public static AgentOrganizationSpec toObjectModel(AgentOrganizationTable entity) {

		AgentOrganizationSpec o = new AgentOrganizationSpec().setId(entity.getId()).setLogo(entity.getLogo())
				.setName(entity.getName()).setPhone(entity.getPhone()).setEmail(entity.getEmail())
				.setRating(entity.getRating()).setAdmin(entity.getAdmin()).setAgents(entity.getAgents())
				.setAddress(entity.getAddress()).setPostalCode(entity.getPostalCode())
				.setCity(entity.getCity()).setTerritory(entity.getTerritory())
				.setCountry(entity.getCountry());

		return o;
	}

	public static AgentOrganizationTable fromObjectModel(AgentOrganizationSpec spec) {

		AgentOrganizationTable o = new AgentOrganizationTable().setLogo(spec.getLogo()).setName(spec.getName())
				.setPhone(spec.getPhone()).setEmail(spec.getEmail()).setRating(spec.getRating())
				.setAdmin(spec.getAdmin()).setAgents(spec.getAgents())
				.setAddress(spec.getAddress()).setPostalCode(spec.getPostalCode())
				.setCity(spec.getCity()).setTerritory(spec.getTerritory()).setCountry(spec.getCountry());

		return o;
	}

	public static AgentSpec toObjectModel(AgentTable entity) {

		AgentSpec o = new AgentSpec().setId(entity.getId()).setAgentOrganization(entity.getAgentOrganization())
				.setIsActive(entity.getIsActive()).setYearsOfExperience(entity.getYearsOfExperience());

		return o;
	}

	public static AgentTable fromObjectModel(AgentSpec spec) {

		AgentTable o = new AgentTable().setAgentOrganization(spec.getAgentOrganization())
				.setYearsOfExperience(spec.getYearsOfExperience());

		return o;
	}

	public static CityFeaturesSpec toObjectModel(CityFeaturesTable entity) {

		CityFeaturesSpec o = new CityFeaturesSpec().setId(entity.getId()).setGoodRoad(entity.getGoodRoad())
				.setPower(entity.getPower()).setSecurity(entity.getSecurity())
				.setSocialization(entity.getSocialization()).setWater(entity.getWater())
				.setDateCreated(entity.getDateCreated());

		return o;
	}

	public static CityFeaturesTable fromObjectModel(CityFeaturesSpec spec) {

		CityFeaturesTable o = new CityFeaturesTable().setId(spec.getId()).setGoodRoad(spec.getGoodRoad())
				.setPower(spec.getPower()).setSecurity(spec.getSecurity()).setSocialization(spec.getSocialization())
				.setWater(spec.getWater()).setDateCreated(spec.getDateCreated());
		return o;
	}

	public static PropertyPOISpec toObjectModel(PropertyPOITable entity) {

		PropertyPOISpec o = new PropertyPOISpec().setId(entity.getId()).setBank(entity.getBank())
				.setPublicTransportation(entity.getPublicTransportation()).setRestaurant(entity.getRestaurant())
				.setSchool(entity.getSchool()).setDateCreated(entity.getDateCreated());

		return o;
	}

	public static PropertyPOITable fromObjectModel(PropertyPOISpec spec) {

		PropertyPOITable o = new PropertyPOITable().setId(spec.getId()).setBank(spec.getBank())
				.setPublicTransportation(spec.getPublicTransportation()).setRestaurant(spec.getRestaurant())
				.setSchool(spec.getSchool()).setDateCreated(spec.getDateCreated());
		return o;
	}

	public static PropertyFloorPlanSpec toObjectModel(PropertyFloorPlanTable entity) {

		PropertyFloorPlanSpec o = new PropertyFloorPlanSpec().setId(entity.getId()).setImages(entity.getImages())
				.setRoomCount(entity.getRoomCount()).setBathroomCount(entity.getBathroomCount())
				.setFloor(entity.getFloor()).setArea(entity.getArea()).setDescription(entity.getDescription())
				.setDateUpdated(entity.getDateUpdated());

		return o;
	}

	public static PropertyFloorPlanTable fromObjectModel(PropertyFloorPlanSpec spec) {

		PropertyFloorPlanTable o = new PropertyFloorPlanTable().setImages(spec.getImages())
				.setRoomCount(spec.getRoomCount()).setBathroomCount(spec.getBathroomCount()).setFloor(spec.getFloor())
				.setArea(spec.getArea()).setDescription(spec.getDescription()).setDateUpdated(spec.getDateUpdated());
		return o;
	}

	public static PropertySpec toObjectModel(PropertyTable entity, List<ListedProperty> listings) {

		PropertySpec o = new PropertySpec().setAddress(entity.getAddress())
				.setAgentOrganization(entity.getAgentOrganization()).setArea(entity.getArea())
				.setBathroomCount(entity.getBathroomCount()).setCity(entity.getCity()).setCountry(entity.getCountry())
				.setDateCreated(entity.getDateCreated()).setDateUpdated(entity.getDateUpdated()).setId(entity.getId())
				.setImages(entity.getImages()).setIsFullyFurnished(entity.getIsFullyFurnished())
				.setFloorPlan(entity.getFloorPlan()).setKeywords(entity.getKeywords()).setListings(listings)
				.setListingStatusHistory(entity.getListingStatusHistory())
				.setListingStatus(PropertyListingStatus.from(entity.getListingStatus()))
				.setParkingSpaceCount(entity.getParkingSpaceCount()).setZipCode(entity.getZipCode())
				.setPaymentPeriod(YearlyPaymentPeriod.from(entity.getPaymentPeriod())).setPrices(entity.getPrices())
				.setBasePrices(entity.getBasePrices()).setPriceRules(entity.getPriceRules()).setPrice(entity.getPrice())
				.setCurrency(entity.getCurrency()).setBasePrice(entity.getBasePrice())
				.setProperties(entity.getProperties()).setRoomCount(entity.getRoomCount())
				.setTerritory(entity.getTerritory()).setTitle(entity.getTitle())
				.setType(PropertyType.from(entity.getType())).setVideoTourLink(entity.getVideoTourLink())
				.setLastUpdatedBy(entity.getLastUpdatedBy());

		return o;
	}

	public static PropertyTable fromObjectModel(PropertySpec spec) {

		PropertyTable o = new PropertyTable().setAddress(spec.getAddress())
				.setAgentOrganization(spec.getAgentOrganization()).setArea(spec.getArea())
				.setBathroomCount(spec.getBathroomCount()).setCity(spec.getCity()).setCountry(spec.getCountry())
				.setDateCreated(Dates.now()).setDateUpdated(Dates.now()).setImages(spec.getImages())
				.setIsFullyFurnished(spec.getIsFullyFurnished()).setFloorPlan(spec.getFloorPlan())
				.setKeywords(spec.getKeywords()).setListingStatus(spec.getListingStatus().getValue())
				.setListingStatusHistory(Lists.newArrayList())
				.setParkingSpaceCount(spec.getParkingSpaceCount()).setPaymentPeriod(spec.getPaymentPeriod().getValue())
				.setPrice(spec.getPrice()).setCurrency(spec.getCurrency()).setBasePrice(spec.getBasePrice())
				.setPrices(Maps.newHashMap()).setBasePrices(Maps.newHashMap()).setPriceRules(spec.getPriceRules())
				.setProperties(spec.getProperties()).setRoomCount(spec.getRoomCount()).setTerritory(spec.getTerritory())
				.setTitle(spec.getTitle()).setType(spec.getType().getValue()).setVideoTourLink(spec.getVideoTourLink())
				.setZipCode(spec.getZipCode()).setLastUpdatedBy(spec.getLastUpdatedBy());
		return o;
	}
	
	public static PropertyTable fromObjectModel(PropertyTable e, PropertyUpdateSpec spec) {

		if (spec.getIsFullyFurnished() != null) {
			e.setIsFullyFurnished(spec.getIsFullyFurnished());
		}
		if (spec.getArea() != null) {
			e.setArea(spec.getArea());
		}
		if (spec.getBathroomCount() != null) {
			e.setBathroomCount(spec.getBathroomCount());
		}
		if (spec.getImages() != null) {
			e.setImages(spec.getImages());
		}
		if (spec.getKeywords() != null) {
			e.setKeywords(spec.getKeywords());
		}
		if (spec.getParkingSpaceCount() != null) {
			e.setParkingSpaceCount(spec.getParkingSpaceCount());
		}
		if (spec.getPaymentPeriod() != null) {
			e.setPaymentPeriod(spec.getPaymentPeriod().getValue());
		}
		if (spec.getCurrency() != null) {
			e.setCurrency(spec.getCurrency());
		}
		if (spec.getPrice() != null) {
			e.setPrice(spec.getPrice());
		}
		if (spec.getProperties() != null) {
			e.setProperties(spec.getProperties());
		}
		if (spec.getRoomCount() != null) {
			e.setRoomCount(spec.getRoomCount());
		}
		if (spec.getTitle() != null) {
			e.setTitle(spec.getTitle());
		}
		if (spec.getType() != null) {
			e.setType(spec.getType().getValue());
		}
		if (spec.getVideoTourLink() != null) {
			e.setVideoTourLink(spec.getVideoTourLink());
		}
		return e;
	}

	public static ListedRentPropertySpec toObjectModel(ListedRentPropertyTable entity) {

		ListedRentPropertySpec o = new ListedRentPropertySpec().setId(entity.getId())
				.setPropertyId(entity.getPropertyId()).setAgentOrganizationId(entity.getAgentOrganizationId())
				.setYearsRequired(entity.getYearsRequired())
				.setAvailabilityStatus(PropertyAvailabilityStatus.from(entity.getAvailabilityStatus()))
				.setDateCreated(entity.getDateCreated()).setDateUpdated(entity.getDateUpdated())
				.setIsHot(entity.getIsHot()).setIsPremium(entity.getIsPremium());

		return o;
	}

	public static ListedRentPropertyTable fromObjectModel(ListedRentPropertySpec spec) {

		ListedRentPropertyTable o = new ListedRentPropertyTable().setPropertyId(spec.getPropertyId())
				.setAgentOrganizationId(spec.getAgentOrganizationId()).setYearsRequired(spec.getYearsRequired())
				.setAvailabilityStatus(spec.getAvailabilityStatus().getValue()).setDateCreated(spec.getDateCreated())
				.setDateUpdated(spec.getDateUpdated()).setIsHot(spec.getIsHot()).setIsPremium(spec.getIsPremium());

		return o;
	}

	public static ListedSalePropertySpec toObjectModel(ListedSalePropertyTable entity) {

		ListedSalePropertySpec o = new ListedSalePropertySpec().setId(entity.getId())
				.setPaymentOption(PaymentOptions.from(entity.getPaymentOption())).setPropertyId(entity.getPropertyId())
				.setAgentOrganizationId(entity.getAgentOrganizationId())
				.setAvailabilityStatus(PropertyAvailabilityStatus.from(entity.getAvailabilityStatus()))
				.setDateCreated(entity.getDateCreated()).setDateUpdated(entity.getDateUpdated())
				.setIsHot(entity.getIsHot()).setIsPremium(entity.getIsPremium());
		;

		return o;
	}

	public static ListedSalePropertyTable fromObjectModel(ListedSalePropertySpec spec) {

		ListedSalePropertyTable o = new ListedSalePropertyTable().setPaymentOption(spec.getPaymentOption().getValue())
				.setPropertyId(spec.getPropertyId()).setAgentOrganizationId(spec.getAgentOrganizationId())
				.setAvailabilityStatus(spec.getAvailabilityStatus().getValue()).setDateCreated(spec.getDateCreated())
				.setDateUpdated(spec.getDateUpdated()).setIsHot(spec.getIsHot()).setIsPremium(spec.getIsPremium());

		return o;
	}

	public static PropertyListingStatusSpec toObjectModel(PropertyListingStatusTable entity) {

		PropertyListingStatusSpec o = new PropertyListingStatusSpec().setId(entity.getId())
				.setPropertyId(entity.getPropertyId()).setPrincipal(entity.getPrincipal())
				.setListingStatus(PropertyListingStatus.from(entity.getListingStatus())).setMessage(entity.getMessage())
				.setAttachments(entity.getAttachments()).setDateCreated(entity.getDateCreated());

		return o;
	}

	public static PropertyListingStatusTable fromObjectModel(PropertyListingStatusSpec spec) {

		PropertyListingStatusTable o = new PropertyListingStatusTable().setPropertyId(spec.getPropertyId())
				.setPrincipal(spec.getPrincipal()).setListingStatus(spec.getListingStatus().getValue())
				.setMessage(spec.getMessage()).setAttachments(spec.getAttachments())
				.setDateCreated(spec.getDateCreated());

		return o;
	}

	public static PropertyPriceRuleSpec toObjectModel(PropertyPriceRuleTable entity) {

		PropertyPriceRuleSpec o = new PropertyPriceRuleSpec().setId(entity.getId())
				.setPropertyId(entity.getPropertyId()).setRules(entity.getRules())
				.setOperator(PriceRuleOperator.from(entity.getOperator())).setPercentile(entity.getPercentile())
				.setPrice(entity.getPrice()).setBasePrice(entity.getBasePrice()).setDateCreated(entity.getDateCreated())
				.setDateUpdated(entity.getDateUpdated());

		return o;
	}

	public static PropertyPriceRuleTable fromObjectModel(PropertyPriceRuleSpec spec) {

		PropertyPriceRuleTable o = new PropertyPriceRuleTable().setPropertyId(spec.getPropertyId())
				.setRules(spec.getRules()).setOperator(spec.getOperator().getValue())
				.setPercentile(spec.getPercentile()).setPrice(spec.getPrice()).setBasePrice(spec.getBasePrice())
				.setDateCreated(Dates.now()).setDateUpdated(Dates.now());
		return o;
	}

	public static CardTable fromObjectModel(Long accountId, BaseCardInfo spec) {

		CardTable e = new CardTable().setAccountId(accountId);

		if (spec instanceof CardInfo) {
			CardInfo info = (CardInfo) spec;
			e.setCardHolder(info.getCardHolder()).setCardNumber(info.getCardNumber()).setCvc(info.getCvc())
					.setExpiryMonth(info.getExpiryMonth()).setExpiryYear(info.getExpiryYear());
		} else {
			CseTokenInfo info = (CseTokenInfo) spec;
			e.setCseToken(info.getCseToken()).setCardSuffix(info.getCardSuffix());
		}

		return e;
	}

	public static BaseCardInfo toObjectModel(CardTable entity) {

		if (entity.getCseToken() == null) {
			CardInfo spec = new CardInfo().setAccountId(entity.getAccountId()).setDateCreated(entity.getDateCreated())

					.setCardHolder(entity.getCardHolder()).setCardNumber(entity.getCardNumber()).setCvc(entity.getCvc())
					.setExpiryMonth(entity.getExpiryMonth()).setExpiryYear(entity.getExpiryYear());

			return spec;
		} else {

			CseTokenInfo spec = new CseTokenInfo().setAccountId(entity.getAccountId()).setDateCreated(entity.getDateCreated())
					.setCseToken(entity.getCseToken()).setCardSuffix(entity.getCardSuffix());

			return spec;
		}
	}

	public static InvoiceItem toObjectModel(InvoiceItemTable entity) {

		InvoiceItem o = new InvoiceItem().setId(entity.getId()).setName(entity.getName())
				.setDescription(entity.getDescription()).setAmount(entity.getAmount())
				.setDateCreated(entity.getDateCreated());

		return o;
	}

	public static InvoiceItemTable fromObjectModel(InvoiceItem spec) {

		InvoiceItemTable o = new InvoiceItemTable().setName(spec.getName()).setDescription(spec.getDescription())
				.setAmount(spec.getAmount()).setDateCreated(Dates.now());

		return o;
	}

	public static InvoiceSpec toObjectModel(InvoiceTable entity) {

		InvoiceSpec o = new InvoiceSpec().setId(entity.getId()).setAccountId(entity.getAccountId())
				.setStatus(InvoiceStatus.from(entity.getStatus())).setStartDate(entity.getStartDate())
				.setEndDate(entity.getEndDate()).setCurrency(entity.getCurrency()).setTotalDue(entity.getTotalDue())
				.setComment(entity.getComment()).setDateCreated(entity.getDateCreated())
				.setDateUpdated(entity.getDateUpdated());

		return o;
	}

	public static InvoiceTable fromObjectModel(InvoiceSpec spec) {

		InvoiceTable o = new InvoiceTable().setAccountId(spec.getAccountId())
				.setOutstanding(spec.getStatus().isOutstanding()).setStatus(spec.getStatus().getValue())
				.setStartDate(Dates.now()).setCurrency(spec.getCurrency()).setTotalDue(spec.getTotalDue())
				.setComment(spec.getComment()).setDateCreated(Dates.now()).setDateUpdated(Dates.now());

		return o;
	}
	
	
	
	public static InvoicePaymentSpec toObjectModel(InvoicePaymentTable entity) {

		InvoicePaymentSpec o = new InvoicePaymentSpec()
				.setInvoiceId(entity.getInvoiceId())
				.setMerchantReference(entity.getMerchantReference())
				.setExtReference(entity.getExtReference())
				.setStatus(InvoicePaymentStatus.from(entity.getStatus()))
				.setMessage(entity.getMessage())
				.setDateCreated(entity.getDateCreated())
				.setDateUpdated(entity.getDateUpdated());

		return o;
	}

	public static InvoicePaymentTable fromObjectModel(InvoicePaymentSpec spec) {

		InvoicePaymentTable o = new InvoicePaymentTable()
				.setInvoiceId(spec.getInvoiceId())
				.setExtReference(spec.getExtReference())
				.setStatus(spec.getStatus().getValue())
				.setMessage(spec.getMessage())
				.setDateCreated(spec.getDateCreated())
				.setDateUpdated(spec.getDateUpdated());
		
		return o;
	}
	
	
	public static InvoicePaymentSpec toObjectModel(InvoicePaymentHistoryTable entity) {

		InvoicePaymentSpec o = new InvoicePaymentSpec()
				.setInvoiceId(entity.getInvoiceId())
				.setExtReference(entity.getExtReference())
				.setStatus(InvoicePaymentStatus.from(entity.getStatus()))
				.setPreviousStatus(entity.getPreviousStatus())
				.setOverwritten(entity.getIsOverwritten())
				.setReconciled(entity.isReconciled())
				.setAdditionalInfo(entity.getAdditionalInfo())
				.setMessage(entity.getMessage())
				.setDateCreated(entity.getDateCreated())
				.setDateUpdated(entity.getDateUpdated());

		return o;
	}

	public static InvoicePaymentHistoryTable fromObjectModel2(Long id, InvoicePaymentSpec spec) {

		InvoicePaymentHistoryTable o = new InvoicePaymentHistoryTable()
				.setId(id)
				.setInvoiceId(spec.getInvoiceId())
				.setExtReference(spec.getExtReference())
				.setStatus(spec.getStatus().getValue())
				.setMessage(spec.getMessage())
				.setAdditionalInfo(spec.getAdditionalInfo())
				.setDateCreated(spec.getDateCreated())
				.setDateUpdated(spec.getDateUpdated());
		
		return o;
	}
	
	public static PublicHolidaySpec toObjectModel(PublicHolidayTable entity) {

		PublicHolidaySpec o = new PublicHolidaySpec()
				.setId(entity.getId())
				.setName(entity.getName())
				.setCountry(entity.getCountry())
				.setPublic(entity.isPublic())
				.setDate(entity.getDate())
				.setDateCreated(entity.getDateCreated());

		return o;
	}
	
	public static PublicHolidayTable fromObjectModel(PublicHolidaySpec spec) {

		PublicHolidayTable o = new PublicHolidayTable()
				.setName(spec.getName())
				.setCountry(spec.getCountry())
				.setPublic(spec.isPublic())
				.setDate(spec.getDate())
				.setDateCreated(spec.getDateCreated());

		return o;
	}
	
}
