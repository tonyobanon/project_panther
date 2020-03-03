package com.re.paas.api.sentences;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.KeyValuePair;

public class Sentence implements Serializable {

	private static final long serialVersionUID = 1L;

	// ClientResources.ClientRBRef | SubjectEntity | String
	private Object subject;

	private CustomPredicate predicate;
	private ObjectEntity object;

	// V: ClientResources.ClientRBRef | ObjectEntity | SubjectEntity
	private Map<Preposition, Object> prepositions = new HashMap<>();

	// ClientResources.ClientRBRef | ObjectEntityKind
	private KeyValuePair<SubordinatingConjuction, Object> subordinativeClause;

	// ClientResources.ClientRBRef | Sentence
	private KeyValuePair<CoordinatingConjuction, Object> coordinativeClause;

	public static Sentence newInstance() {
		return new Sentence();
	}

	private Sentence() {
	}

	public Object getSubject() {
		return subject;
	}

	public Sentence setSubject(ClientRBRef subject) {
		this.subject = subject;
		return this;
	}

	public Sentence setSubject(SubjectEntity subject) {
		this.subject = subject;
		return this;
	}

	public Sentence setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public CustomPredicate getPredicate() {
		return predicate;
	}

	public Sentence setPredicate(CustomPredicate predicate) {
		this.predicate = predicate;
		return this;
	}

	public ObjectEntity getObject() {
		return object;
	}

	public Sentence setObject(ObjectEntity object) {
		this.object = object;
		return this;
	}

	public Map<Preposition, Object> getPrepositions() {
		return prepositions;
	}

	public Sentence withPreposition(Preposition preposition, ClientRBRef clause) {
		this.prepositions.put(preposition, clause);
		return this;
	}

	public Sentence withPreposition(Preposition preposition, ObjectEntity clause) {
		this.prepositions.put(preposition, clause);
		return this;
	}

	public Sentence withPreposition(Preposition preposition, SubjectEntity clause) {
		this.prepositions.put(preposition, clause);
		return this;
	}

	public KeyValuePair<SubordinatingConjuction, Object> getSubordinativeClause() {
		return subordinativeClause;
	}

	public Sentence setSubordinativeClause(SubordinatingConjuction conjunction, ClientRBRef clause) {
		this.subordinativeClause = new KeyValuePair<SubordinatingConjuction, Object>(conjunction, clause);
		return this;
	}

	public Sentence setSubordinativeClause(SubordinatingConjuction conjunction, ObjectEntity clause) {
		this.subordinativeClause = new KeyValuePair<SubordinatingConjuction, Object>(conjunction, clause);
		return this;
	}

	public KeyValuePair<CoordinatingConjuction, Object> getCoordinativeClause() {
		return coordinativeClause;
	}

	public Sentence setCoordinativeClause(CoordinatingConjuction conjunction, ClientRBRef clause) {
		this.coordinativeClause = new KeyValuePair<CoordinatingConjuction, Object>(conjunction, clause);
		;
		return this;
	}

	public Sentence setCoordinativeClause(CoordinatingConjuction conjunction, Sentence clause) {
		this.coordinativeClause = new KeyValuePair<CoordinatingConjuction, Object>(conjunction, clause);
		;
		return this;
	}

	public String asString(SentenceStringifier stringifier) {

		StringBuilder builder = new StringBuilder();

		String separator = ClientResources.HtmlCharacterEntities.SPACE.toString();

		builder.append(stringifier.stringify(subject))
		.append(separator).append(predicate)
		.append(separator).append(stringifier.stringify(object));

		prepositions.forEach((k, v) -> {
			builder.append(separator).append(k).append(separator).append(v);
		});

		if (subordinativeClause != null) {
			builder.append(separator).append(subordinativeClause.getKey()).append(separator)
					.append(subordinativeClause.getValue());
		}

		if (coordinativeClause != null) {
			builder.append(ClientResources.HtmlCharacterEntities.COMMA).append(separator)
					.append(coordinativeClause.getKey()).append(separator).append(coordinativeClause.getValue());
		}

		return builder.toString();
	}

}
