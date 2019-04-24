package com.re.paas.api.infra.database.document.utils;

abstract class ValueTransformer {
    abstract Object transform(Object value);
}
