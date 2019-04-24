package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyTableSpec;

public class PropertyTable implements BaseTable {

	Long id;

	List<Long> listingStatusHistory;

	Integer listingStatus;

	List<Long> listings;

	Integer roomCount;

	Integer bathroomCount;

	Integer parkingSpaceCount;

	Integer paymentPeriod;

	String currency;

	Double price;

	Double basePrice;
	
	Map<Long, Double> prices;

	Map<Long, Double> basePrices;

	List<Long> priceRules;

	Boolean isFullyFurnished;

	List<Long> floorPlan;

	List<String> images;

	String title;

	String videoTourLink;

	List<String> keywords;

	Map<Long, Boolean> properties;

	Integer type;

	String agentOrganization;

	Integer area;

	String address;

	Integer zipCode;

	Integer city;

	String territory;

	String country;

	Date dateCreated;

	Long lastUpdatedBy;

	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return PropertyTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition listingStatusIndex = new IndexDefinition(PropertyTableSpec.LISTING_STATUS_INDEX, Type.GSI)
				.setQueryOptimzed(true)
				.addHashKey(PropertyTableSpec.LISTING_STATUS);

		IndexDefinition roomCountIndex = new IndexDefinition(PropertyTableSpec.ROOM_COUNT_INDEX, Type.GSI)
				.setQueryOptimzed(true)
		.addHashKey(PropertyTableSpec.ROOM_COUNT);
		
		IndexDefinition bathroomCountIndex = new IndexDefinition(PropertyTableSpec.BATHROOM_COUNT_INDEX, Type.GSI)
				.setQueryOptimzed(true)
		.addHashKey(PropertyTableSpec.BATHROOM_COUNT);

		IndexDefinition parkingSpaceCountIndex = new IndexDefinition(PropertyTableSpec.PARKING_SPACE_COUNT_INDEX, Type.GSI)
				.setQueryOptimzed(true)
		.addHashKey(PropertyTableSpec.PARKING_SPACE_COUNT);
		
		IndexDefinition paymentPeriodIndex = new IndexDefinition(PropertyTableSpec.PAYMENT_PERIOD_INDEX, Type.GSI)
				.setQueryOptimzed(true)
		.addHashKey(PropertyTableSpec.PAYMENT_PERIOD);

		IndexDefinition basePriceIndex = new IndexDefinition(PropertyTableSpec.BASE_PRICE_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.BASE_PRICE);
		
		
		IndexDefinition typeIndex = new IndexDefinition(PropertyTableSpec.TYPE_INDEX, Type.GSI)
				.setQueryOptimzed(true)
		.addHashKey(PropertyTableSpec.TYPE);

		IndexDefinition agentOrganizationIndex = new IndexDefinition(PropertyTableSpec.AGENT_ORGANIZATION_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.AGENT_ORGANIZATION);
		
		
		IndexDefinition areaIndex = new IndexDefinition(PropertyTableSpec.AREA_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.AREA);

		IndexDefinition addressIndex = new IndexDefinition(PropertyTableSpec.ADDRESS_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.ADDRESS);
		
		IndexDefinition zipCodeIndex = new IndexDefinition(PropertyTableSpec.ZIP_CODE_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.ZIP_CODE);

		IndexDefinition cityIndex = new IndexDefinition(PropertyTableSpec.CITY_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.CITY);
		
		IndexDefinition territoryIndex = new IndexDefinition(PropertyTableSpec.TERRITORY_INDEX, Type.GSI)
		.addHashKey(PropertyTableSpec.TERRIRORY);

		indexes.add(listingStatusIndex);
		indexes.add(roomCountIndex);
		indexes.add(bathroomCountIndex);
		indexes.add(parkingSpaceCountIndex);
		indexes.add(paymentPeriodIndex);
		indexes.add(basePriceIndex);
		indexes.add(typeIndex);
		indexes.add(agentOrganizationIndex);
		indexes.add(areaIndex);
		indexes.add(addressIndex);
		indexes.add(zipCodeIndex);
		indexes.add(cityIndex);
		indexes.add(territoryIndex);
		
		return indexes;
	}
	
}
