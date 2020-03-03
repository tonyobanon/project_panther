package com.re.paas.api.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ClientResources.ClientRBRefEntry;
import com.re.paas.api.classes.ClientResources.ClientRBRefEntryType;

public class ClientRBRef {

	private List<ClientRBRefEntry> values = new ArrayList<ClientResources.ClientRBRefEntry>();;

	public ClientRBRef() {
	}

	private ClientRBRef(Object value) {
		this(Splitter.on(',').split(value.toString()));
	}

	private ClientRBRef(Iterable<String> values) {
		for (String v : values) {
			if (!ClientResources.RB_KEY_PATTERN.matcher(v).matches()) {
				throw new IllegalArgumentException("The RB key format: " + v + " is invalid");
			}
			String[] arr = v.split(ClientRBRefEntry.TEXT_AND_TYPE_DELIM);
			String text = arr[0];
			ClientRBRefEntryType type = ClientRBRefEntryType.TRANSLATE;

			if (arr.length > 1) {
				type = ClientRBRefEntryType.from(Integer.parseInt(arr[1]));
			}

			this.values.add(new ClientRBRefEntry().setText(text).setType(type));
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
		return new ClientRBRef(Arrays.asList(values));
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
	public String toString() {
		List<String> entries = new ArrayList<>();
		values.forEach(e -> {
			StringBuilder sb = new StringBuilder(e.getText());
			if (e.getType() == ClientRBRefEntryType.NON_TRANSLATE) {
				sb.append(ClientRBRefEntry.TEXT_AND_TYPE_DELIM + e.getType().getValue());
			}
			entries.add(sb.toString());
		});
		return Joiner.on(',').join(entries);
	}

	@ClientAware
	@BlockerTodo("Properly format, remove multiple whitespace")
	public String toHtmlString() {

		StringBuilder sb = new StringBuilder();

		for (ClientRBRefEntry e : values) {
			sb.append(e.getType() == ClientRBRefEntryType.TRANSLATE ? getTag(e.toString()) : e.toString());
			sb.append(ClientResources.HtmlCharacterEntities.SPACE);
		}

		return sb.toString();
	}
}