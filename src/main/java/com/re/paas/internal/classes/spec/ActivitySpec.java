package com.re.paas.internal.classes.spec;

import java.util.Date;

public class ActivitySpec {

	private Long id;
	private String subjectImage;
	private String personImage;
	private String html;
	private Integer likes;
	private Date date;

	public Long getId() {
		return id;
	}

	public ActivitySpec setId(Long id) {
		this.id = id;
		return this;
	}


	public String getSubjectImage() {
		return subjectImage;
	}

	public ActivitySpec setSubjectImage(String subjectImage) {
		this.subjectImage = subjectImage;
		return this;
	}

	public String getPersonImage() {
		return personImage;
	}

	public ActivitySpec setPersonImage(String personImage) {
		this.personImage = personImage;
		return this;
	}

	public String getHtml() {
		return html;
	}

	public ActivitySpec setHtml(String html) {
		this.html = html;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public ActivitySpec setDate(Date date) {
		this.date = date;
		return this;
	}

	public Integer getLikes() {
		return likes;
	}

	public ActivitySpec setLikes(Integer likes) {
		this.likes = likes;
		return this;
	}
}
