package com.re.paas.api.logging;

import java.util.Collection;
import java.util.function.Consumer;

public interface LoggerInterceptor extends Consumer<Collection<String>> {
	 
    void accept(Collection<String> t);
 
}
