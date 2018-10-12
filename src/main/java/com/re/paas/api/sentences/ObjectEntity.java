package com.re.paas.api.sentences;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.fusion.services.Functionality;

public class ObjectEntity {

	public static ObjectEntity get(ObjectType kind) {
		return new ObjectEntity(kind);
	}

	private ObjectEntity(ObjectType kind) {
		this.article = kind.getArticle();
		this.kind = kind;
	}

	private Article article;
	private String name;
	private ObjectType kind;
	
	private List<Object> qualifiers = new ArrayList<>();
	private List<Object> identifiers = new ArrayList<>();

	public ClientRBRef getClientRBRef() {
		return ClientRBRef.get(this.kind.title());
	}

	public Article getArticle() {
		return article;
	}

	public ObjectEntity setArticle(Article article) {
		this.article = article;
		return this;
	}

	public Functionality getFunctionality() {
		return kind.getFunctionality();
	}

	public String getName() {
		return name;
	}

	public ObjectEntity setName(String name) {
		this.name = name;
		return this;
	}

	public List<Object> getQualifiers() {
		return qualifiers;
	}

	public ObjectEntity addQualifier(ClientRBRef qualifer) {
		this.qualifiers.add(qualifer);
		return this;
	}

	public ObjectEntity addQualifier() {
		this.qualifiers.add(getClientRBRef());
		return this;
	}

	public List<Object> getIdentifiers() {
		return identifiers;
	}

	public ObjectEntity addIdentifier(Object identifier) {
		this.identifiers.add(identifier);
		return this;
	}
	
	public ObjectEntity setIdentifiers(List<Object> identifiers) {
		this.identifiers = identifiers;
		return this;
	}
}
