
 package com.re.paas.api.infra.database.document.xspec;

import java.util.List;

/**
 * Represents a list of literal values in building expressions.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying list given
 * during construction.
 */
class ListLiteralOperand extends LiteralOperand {
    ListLiteralOperand(List<?> value) {
        super(value);
    }
}
