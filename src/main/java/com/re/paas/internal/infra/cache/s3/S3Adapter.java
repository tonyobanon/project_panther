package com.re.paas.internal.infra.cache.s3;

import static com.re.paas.api.forms.input.InputType.SECRET;
import static com.re.paas.api.forms.input.InputType.TEXT;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.Region;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.internal.clustering.ClusterWideTask;

public class S3Adapter implements CacheAdapter {

	private static CacheEvicter cacheEvicter;

	@Override
	public String name() {
		return "s3";
	}

	@Override
	public String title() {
		return "amazon_s3";
	}

	@Override
	public String iconUrl() {
		return "https://braze-marketing-assets.s3.amazonaws.com/images/partner_logos/amazon-s3.png";
	}

	@Override
	public Form initForm() {

		Section section = new Section().setTitle(formTitle())

				.withField(new SimpleField("access_key_id", TEXT, "access_key"))
				.withField(new SimpleField("secret_key_id", SECRET, "secret_key"));

		CompositeField regionField = new CompositeField("region", "region");

		availableRegions().forEach((k, v) -> {
			regionField.withItem(k, v);
		});

		section.withField(regionField);

		return new Form().addSection(section);
	}

	protected String formTitle() {
		return "AWS Credentials";
	}

	protected String endpoint() {
		return "s3.amazonaws.com";
	}

	protected Map<String, String> availableRegions() {

		Region[] regions = Region.values();
		Map<String, String> regionsMap = new HashMap<>();

		for (Region region : regions) {
			regionsMap.put(region.getFirstRegionId(), region.getFirstRegionId());
		}

		return regionsMap;
	}

	@Override
	public CacheFactory<String, Object> getResource(Map<String, String> fields) {

		String access_key_id = fields.get("access_key_id");
		String secret_key_id = fields.get("secret_key_id");

		String region = fields.get("region");

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, secret_key_id);

		
		// Register cache eviction task
		
		ClusterWideTask task = new ClusterWideTask(() -> {
			
			S3CacheFactory factory = (S3CacheFactory) CacheAdapter.getDelegate().getCacheFactory();

			CacheEvicter evicter = new CacheEvicter(factory);
			evicter.start();

			this.setCacheEvicter(evicter);
			
		}, () -> CacheAdapter.getDelegate().getCacheFactory() instanceof S3CacheFactory, 5l, 0l);

		ClusteringServices.get().addClusterWideTask(task);

		return new S3CacheFactory(this, "default", awsCreds, region, endpoint());
	}

	protected CacheEvicter getCacheEvicter() {
		return cacheEvicter;
	}

	protected void setCacheEvicter(CacheEvicter cacheEvicter) {
		S3Adapter.cacheEvicter = cacheEvicter;
	}
}
