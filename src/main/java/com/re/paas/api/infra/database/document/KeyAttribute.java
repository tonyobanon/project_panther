package com.re.paas.api.infra.database.document;

import java.util.ArrayList;
import java.util.Collection;

import com.re.paas.api.infra.database.document.utils.ItemUtils;

/**
 * A key attribute which consists of an attribute name and value.
 */
public class KeyAttribute extends Attribute {

    /**
     * A key attribute which consists of an attribute name and value.
     */
    public KeyAttribute(String attrName, Object value) {
        super(attrName, value);
        ItemUtils.checkInvalidAttrName(attrName);
    }
    
	
	public static Collection<KeyAttribute> toCollection(String hashKey, Object hashValue, String rangeKey, Object rangeValue) {
		Collection<KeyAttribute> keys = new ArrayList<>(2);
		
		assert hashKey != null;
		keys.add(new KeyAttribute(hashKey, hashValue));
		
		if (rangeKey != null) {
			keys.add(new KeyAttribute(rangeKey, rangeValue));
		}
		
		return keys;
	}
}