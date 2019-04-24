package com.re.paas.api.infra.database.document;

import com.re.paas.api.infra.database.document.utils.ItemUtils;

/**
 * A key/value pair.
 */
public class Attribute {
    private final String name;
    private final Object value;

    public Attribute(String attrName, Object value) {
        ItemUtils.checkInvalidAttrName(attrName);
        this.name = attrName;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" + name + ": " + value + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        // attribute name is never null as enforced in ctor
        hashCode = prime * hashCode + getName().hashCode();
        hashCode = prime * hashCode
                + ((getValue() == null) ? 0 : getValue().hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object in) {
        if (in instanceof Attribute) {
            Attribute that = (Attribute)in;
            if (this.name.equals(that.name)) {
                if (this.value == null)
                    return that.value == null;
                else
                    return this.value.equals(that.value);
            }
        }
        return false;
    }
}
