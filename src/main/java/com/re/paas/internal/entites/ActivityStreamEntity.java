package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotEmpty;

@Entity
@Cache
public class ActivityStreamEntity {

	@Id
	Long id;
	@Index(IfNotEmpty.class)
	String subject;
	@Index(IfNotEmpty.class)
	String person;
	String subjectImage;
	String personImage;
	String activity;
	Integer likes;
	@Index
	Date date;

	public Long getId() {
		return id; 
	}

	public ActivityStreamEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getSubject() {
		return Long.parseLong(subject);
	}

	public ActivityStreamEntity setSubject(Long subject) {
		this.subject = subject != null ? subject.toString() : null;
		return this;
	}

	public Long getPerson() {
		return Long.parseLong(person);
	}
	
	public ActivityStreamEntity setPerson(Long person) {
		this.person = person.toString();
		return this;
	}

	public String getSubjectImage() {
		return subjectImage;
	}

	public ActivityStreamEntity setSubjectImage(String subjectImage) {
		this.subjectImage = subjectImage;
		return this;
	}

	public String getPersonImage() {
		return personImage;
	}

	public ActivityStreamEntity setPersonImage(String personImage) {
		this.personImage = personImage;
		return this;
	}

	public String getActivity() {
		return activity;
	}

	public ActivityStreamEntity setActivity(String activity) {
		this.activity = activity;
		return this;
	}

	public Integer getLikes() {
		return likes;
	}

	public ActivityStreamEntity setLikes(Integer likes) {
		this.likes = likes;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public ActivityStreamEntity setDate(Date date) {
		this.date = date;
		return this;
	}
}
