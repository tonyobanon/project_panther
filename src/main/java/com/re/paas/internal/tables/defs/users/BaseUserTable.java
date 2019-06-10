package com.re.paas.internal.tables.defs.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.Projection;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.users.BaseUserTableSpec;

public class BaseUserTable implements BaseTable {

	Long id;

	Long applicationId;

	String email;
	String password;

	String firstName;
	String middleName;
	String lastName;
	String image;

	String phone;
	Date dateOfBirth;
	Integer gender;

	String address;

	Integer city;
	String territory;
	String country;

	String facebookProfile;
	String twitterProfile;
	String linkedInProfile;
	String skypeProfile;

	String role;

	String preferredLocale;
	Long principal;

	Date dateCreated;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return BaseUserTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition emailIndex = new IndexDefinition(BaseUserTableSpec.EMAIL_INDEX, Type.GSI)
				.addHashKey(BaseUserTableSpec.EMAIL)
				.withProjection(new Projection().withNonKeyAttributes(BaseUserTableSpec.PASSWORD));

		IndexDefinition phoneIndex = new IndexDefinition(BaseUserTableSpec.PHONE_INDEX, Type.GSI)
				.addHashKey(BaseUserTableSpec.PHONE);

		IndexDefinition roleIndex = new IndexDefinition(BaseUserTableSpec.ROLE_INDEX, Type.GSI)
				.addHashKey(BaseUserTableSpec.ROLE);

		indexes.add(emailIndex);
		indexes.add(phoneIndex);
		indexes.add(roleIndex);

		return indexes;
	}

}
