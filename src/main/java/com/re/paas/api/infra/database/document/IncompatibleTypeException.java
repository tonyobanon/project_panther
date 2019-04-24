package com.re.paas.api.infra.database.document;

/**
 * Thrown upon incompatible type during data conversion.
 */
public class IncompatibleTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IncompatibleTypeException(String message) {
        super(message);
    }
}