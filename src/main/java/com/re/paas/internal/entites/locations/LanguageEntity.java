package com.re.paas.internal.entites.locations;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class LanguageEntity {

	@Id
	String code;
	String langName;

	public String getCode() {
		return code;
	}

	public LanguageEntity setCode(String code) {
		this.code = code;
		return this;
	}

	public String getLangName() {
		return langName;
	}

	public LanguageEntity setLangName(String langName) {
		this.langName = langName;
		return this;
	}

}
