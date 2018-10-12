package com.re.paas.internal.models.helpers;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.models.classes.Gender;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.models.BlobStoreModel;

public class MockHelper {

	
	public static UserProfileSpec nextMockUser() {

		int rand = new Random().nextInt(6) + 1;

		switch (rand) {
		case 1:
			return createMockAdmin();
		case 2:
			return createMockExamOfficer();
		case 3:
			return createMockDean();
		case 4:
			return createMockHod();
		case 5:
			return createMockLecturer();
		case 6:
			return createMockStudent();
		}
		return createMockStudent();
	}
	
	public static UserProfileSpec createMockStudent() {
		
		//Create user profile
		
		String image = null;
		try {
			image = BlobStoreModel.save(AppDirectory.getInputStream("mock_data/sample_avatars/01.jpeg"));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		UserProfileSpec userSpec =  new UserProfileSpec()
				.setCountry("NG")
				.setTerritory("NG.05")
				.setCity(2566695)
				.setAddress("1, HKN Avenue, Banana Island")
				.setEmail("donald.duke." + Utils.newShortRandom() + "@ymail.com")
				.setFirstName("Donald")
				.setMiddleName("David")
				.setLastName("Duke")
				.setPassword("passXYZ")
				.setGender(Gender.MALE)
				.setPhone(new Random().nextInt(999999999))
				.setImage(image)
				.setDateOfBirth(new Date(1996-1900, 8, 12));
		
		return userSpec;
	}
	
	public static UserProfileSpec createMockLecturer() {
		
		String image = null;
		try {
			image = BlobStoreModel.save(AppDirectory.getInputStream("mock_data/sample_avatars/02.jpg"));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		UserProfileSpec userSpec = new UserProfileSpec()
				.setCountry("NG")
				.setTerritory("NG.05")
				.setCity(2566680)
				.setAddress("1, Diva Street, Chelsea Ave.")
				.setEmail("henry.chapman." + Utils.newShortRandom() + "@ymail.com")
				.setFirstName("Henry")
				.setMiddleName("Tony")
				.setLastName("Chapman")
				.setPassword("passXYZ")
				.setGender(Gender.MALE)
				.setPhone(new Random().nextInt(999999999))
				.setImage(image)
				.setDateOfBirth(new Date(1950-1900, 11, 30));
		
		return userSpec;
	}
	
	public static UserProfileSpec createMockHod() {
		
		String image = null;
		try {
			image = BlobStoreModel.save(AppDirectory.getInputStream("mock_data/sample_avatars/03.jpg"));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		UserProfileSpec userSpec = new  UserProfileSpec()
				.setCountry("NG")
				.setTerritory("NG.05")
				.setCity(2566636)
				.setAddress("26, Rails way Avenue, Indigo Estate")
				.setEmail("vivian.fowler." + Utils.newShortRandom() + "@ymail.com")
				.setFirstName("Vivian")
				.setMiddleName("Rita")
				.setLastName("Fowler")
				.setPassword("passXYZ")
				.setGender(Gender.FEMALE)
				.setPhone(new Random().nextInt(999999999))
				.setImage(image)
				.setDateOfBirth(new Date(1972-1900, 8, 3));
		
		return userSpec;
	}
	
	public static UserProfileSpec createMockDean() {
		
		String image = null;
		try {
			image = BlobStoreModel.save(AppDirectory.getInputStream("mock_data/sample_avatars/04.jpg"));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		UserProfileSpec userSpec = new  UserProfileSpec()
				.setCountry("NG")
				.setTerritory("NG.05")
				.setCity(2351943)
				.setAddress("1, ART Avenue, Dukeson Estate")
				.setEmail("helen.keller." + Utils.newShortRandom() + "@gmail.com")
				.setFirstName("Helen")
				.setMiddleName("Mary")
				.setLastName("Keller")
				.setPassword("passXYZ")
				.setGender(Gender.FEMALE)
				.setPhone(new Random().nextInt(999999999))
				.setImage(image)
				.setDateOfBirth(new Date(1990-1900, 04, 19));
		
		return userSpec;
	}

	public static UserProfileSpec createMockAdmin() {
		
		String image = null;
		try {
			image = BlobStoreModel.save(AppDirectory.getInputStream("mock_data/sample_avatars/05.jpg"));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		UserProfileSpec userSpec = new  UserProfileSpec()
				.setCountry("NG")
				.setTerritory("NG.05")
				.setCity(2566636)
				.setAddress("16, Parkview Estate")
				.setEmail("juliet.rita." + Utils.newShortRandom() + "@ymail.com")
				.setFirstName("Juliet")
				.setMiddleName("Rita")
				.setLastName("Caine")
				.setPassword("passXYZ")
				.setGender(Gender.FEMALE)
				.setPhone(new Random().nextInt(999999999))
				.setImage(image)
				.setDateOfBirth(new Date(1989-1900, 8, 3));
		
		return userSpec;
	}
	
	public static UserProfileSpec createMockExamOfficer() {
		
		String image = null;
		try {
			image = BlobStoreModel.save(AppDirectory.getInputStream("mock_data/sample_avatars/06.jpg"));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		UserProfileSpec userSpec = new  UserProfileSpec()
				.setCountry("NG")
				.setTerritory("NG.05")
				.setCity(2351943)
				.setAddress("34, Thailand Street")
				.setEmail("pascal.ezeama." + Utils.newShortRandom() + "@gmail.com")
				.setFirstName("Pascal")
				.setMiddleName("Jazzman")
				.setLastName("Ezeama")
				.setPassword("passXYZ")
				.setGender(Gender.MALE)
				.setPhone(new Random().nextInt(999999999))
				.setImage(image)
				.setDateOfBirth(new Date(1996-1900, 04, 19));
		
		return userSpec;
	}
}
