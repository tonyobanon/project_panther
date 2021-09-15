package com.re.paas.api.infra.database.document;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.re.paas.api.utils.ValidationUtils;

/**
 * Used to represent a primary key that has one or multiple key components.
 */
public class PrimaryKey {
	private final Map<String, KeyAttribute> components = new LinkedHashMap<String, KeyAttribute>();

	public PrimaryKey() {
	}

	/**
	 * Constructs with the specified key components.
	 */
	public PrimaryKey(KeyAttribute... components) {
		addComponents(components);
	}

	/**
	 * Constructs with a hash key.
	 */
	public PrimaryKey(String hashKeyName, Object hashKeyValue) {
		addComponent(hashKeyName, hashKeyValue);
	}

	/**
	 * Constructs with a hash key and a range key.
	 */
	public PrimaryKey(String hashKeyName, Object hashKeyValue, String rangeKeyName, Object rangeKeyValue) {
		if (hashKeyName.equals(rangeKeyName))
			throw new IllegalArgumentException("hashKeyName must not be the same as the rangeKeyName");
		addComponent(hashKeyName, hashKeyValue);

		if (rangeKeyName != null) {
			addComponent(rangeKeyName, rangeKeyValue);
		}
	}

	/**
	 * Returns all the key components of this primary key.
	 */
	public Collection<KeyAttribute> getComponents() {
		return components.values();
	}

	public KeyAttribute getHashComponent() {
		Iterator<Entry<String, KeyAttribute>> it = components.entrySet().iterator();
		try {
			return it.next().getValue();
		} catch (NoSuchElementException ex) {
			return null;
		}
	}

	public KeyAttribute getRangeComponent() {
		Iterator<Entry<String, KeyAttribute>> it = components.entrySet().iterator();
		try {
			it.next();
			return it.next().getValue();
		} catch (NoSuchElementException ex) {
			return null;
		}
	}

	/**
	 * Returns all the key component names of this primary key as a set.
	 */
	public Set<String> getComponentNameSet() {
		return components.keySet();
	}

	/**
	 * Returns true if this primary has the specified key attribute name; false
	 * otherwise.
	 */
	public boolean hasComponent(String attrName) {
		return components.containsKey(attrName);
	}

	/**
	 * Add one or multiple key components to this primary key.
	 *
	 * Note adding a key component with the same name as that of an existing one
	 * would overwrite and become a single key component instead of two.
	 */
	public PrimaryKey addComponents(KeyAttribute... components) {
		if (components != null) {
			for (KeyAttribute ka : components) {
				ValidationUtils.assertNotNull(ka);
				this.components.put(ka.getName(), ka);
			}
		}
		return this;
	}

	/**
	 * Add a key component to this primary key.
	 *
	 * Note adding a key component with the same name as that of an existing one
	 * would overwrite and become a single key component instead of two.
	 */
	public PrimaryKey addComponent(String keyAttributeName, Object keyAttributeValue) {
		components.put(keyAttributeName, new KeyAttribute(keyAttributeName, keyAttributeValue));
		return this;
	}

	@Override
	public String toString() {
		return String.valueOf(components);
	}

	@Override
	public int hashCode() {
		return components.hashCode();
	}

	@Override
	public boolean equals(Object in) {
		if (in instanceof PrimaryKey) {
			PrimaryKey that = (PrimaryKey) in;
			return this.components.equals(that.components);
		} else {
			return false;
		}
	}
}
