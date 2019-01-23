package com.re.paas.api.forms;

import java.util.ArrayList;
import java.util.List;

public class Form {

	private String id;
	private String title;
	List<Section> sections;

	public Form() {
		this.sections = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public Form setId(String id) {
		this.id = id;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Form setTitle(String title) {
		this.title = title;
		return this;
	}

	public List<Section> getSections() {
		return sections;
	}

	public Form setSections(List<Section> sections) {
		this.sections = sections;
		return this;
	}

	public Form addSection(Section section) {
		this.sections.add(section);
		return this;
	}

}
