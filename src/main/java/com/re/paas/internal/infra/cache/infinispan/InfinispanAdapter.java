package com.re.paas.internal.infra.cache.infinispan;

import java.util.Map;

import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;

public class InfinispanAdapter implements CacheAdapter {

	@Override
	public String name() {
		return "infispan";
	}

	@Override
	public String title() {
		return "infispan";
	}

	@Override
	public String iconUrl() {
		return "https://yt3.ggpht.com/a/AGF-l7-J5rYgs28x8jK3ihtMbeaQg1fcPPP1m_6seg=s900-c-k-c0xffffffff-no-rj-mo";
	}

	@Override
	public CacheFactory<String, Object> getResource(Map<String, String> fields) {
		return new InfinispanCacheFactory(this);
	}
}
