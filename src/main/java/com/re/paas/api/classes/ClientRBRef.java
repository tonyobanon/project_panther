package com.re.paas.api.classes;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.classes.ClientResources.ClientRBRefEntry;
import com.re.paas.api.classes.ClientResources.ClientRBRefEntryType;

public class ClientRBRef {

	private List<ClientRBRefEntry> values = new ArrayList<ClientResources.ClientRBRefEntry>();;

	public ClientRBRef() {
	}
	
	private ClientRBRef(Object value) {
		this(new String[] { value.toString() });
	}

	private ClientRBRef(String... values) {
		for (String v : values) {
			if (!ClientResources.RB_KEY_PATTERN.matcher(v).matches()) {
				throw new IllegalArgumentException("The RB key format: " + v + " is invalid");
			}
			this.values.add(new ClientRBRefEntry().setText(v).setType(ClientRBRefEntryType.TRANSLATE));
		}
	}

	public ClientRBRef add(ClientRBRefEntry value) {
		this.values.add(value);
		return this;
	}

	public ClientRBRef add(String value) {
		this.values.add(new ClientRBRefEntry().setText(value).setType(ClientRBRefEntryType.TRANSLATE));
		return this;
	}

	public static ClientRBRef get(Object value) {
		return new ClientRBRef(value);
	}
	
	public static ClientRBRef get(ClientRBRefEntry value) {
		return new ClientRBRef().add(value);
	}

	public static ClientRBRef forAll(String... values) {
		return new ClientRBRef(values);
	}

	private static String getTag(String value) {
		return "<span class='" + ClientResources.RB_TRANSLATE_CSS_CLASSNAME + "'>" + value + "</span>";
	}

	public String getValue() {
		assert values.size() == 1;
		ClientRBRefEntry v = values.get(0);
		assert v.getType() == ClientRBRefEntryType.TRANSLATE;
		return v.getText();
	}
	
	public List<ClientRBRefEntry> getValues() {
		return values;
	}
	
	public ClientRBRef setValues(List<ClientRBRefEntry> values) {
		this.values = values;
		return this;
	}

	@Override
	@ClientAware
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (ClientRBRefEntry e : values) {
			sb.append(e.getType() == ClientRBRefEntryType.TRANSLATE ? getTag(e.toString()) : e.toString());
			sb.append(ClientResources.HtmlCharacterEntities.SPACE);
		}

		return sb.toString();

	}
}