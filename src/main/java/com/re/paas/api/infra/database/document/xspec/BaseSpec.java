package com.re.paas.api.infra.database.document.xspec;

import java.util.Map;

public abstract class BaseSpec {
    /**
     * Returns the name map which is unmodifiable; or null if there is none.
     */
    public abstract Map<String, String> getNameMap();
}
