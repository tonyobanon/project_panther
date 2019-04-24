package com.re.paas.api.infra.database.document;

import com.amazonaws.services.dynamodbv2.document.Attribute;
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
}