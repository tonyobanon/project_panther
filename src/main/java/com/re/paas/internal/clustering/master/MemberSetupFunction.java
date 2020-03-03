package com.re.paas.internal.clustering.master;

import static com.re.paas.api.clustering.slave.SlaveFunction.INGEST_ADAPTER_CONFIG;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.Member;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.roles.AbstractMasterRole;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.internal.clustering.model.IngestAdapterConfigRequest;
import com.re.paas.internal.clustering.model.IngestAdapterConfigResponse;
import com.re.paas.internal.clustering.model.MemberSetupRequest;
import com.re.paas.internal.clustering.model.MemberSetupResult;

public class MemberSetupFunction extends AbstractClusterFunction<MemberSetupRequest, MemberSetupResult> {

	@Override
	public Class<? extends AbstractRole> role() {
		return AbstractMasterRole.class;
	}

	@Override
	public Function id() {
		return MasterFunction.MEMBER_SETUP;
	}

	@Override
	public MemberSetupResult delegate(MemberSetupRequest request) {

		Member member = ClusteringServices.get().getMember(request.getMemberId());

		Logger.get().info("MemberSetupRequest: " + member.getMemberId() + ", " + member.getHost());
		
		// Transfer adapter configurations

		IngestAdapterConfigRequest ingestRequest = new IngestAdapterConfigRequest();

		for (AdapterType type : AdapterType.values()) {

			AbstractAdapterDelegate<?, ? extends Adapter<?>> delegate = Singleton.get(type.getDelegateType());
			AdapterConfig config = delegate.getConfig();

			ingestRequest.addAdapterConfig(config);
		}

		CompletableFuture<IngestAdapterConfigResponse> r3 = Function.execute(member.getMemberId(),
				INGEST_ADAPTER_CONFIG, ingestRequest, IngestAdapterConfigResponse.class);

		Boolean success = true;
		MemberSetupResult r = new MemberSetupResult();
		
		for (Entry<AdapterType, Object> e : r3.join().getStatus().entrySet()) {

			AdapterType type = e.getKey();
			Object status = e.getValue();

			if (!status.equals(Boolean.TRUE)) {
				success = false;
				r.addError(type, (Exception) status);
			}
		}
		
		if(!success) {
			return r;
		}
		

		// Transfer applications (note: delegate should contain a pre remove function to
		// indicate that a resource set want to taken down)

		// Set platform (slave) as installed

		// Perform Consolidation(s)

		
		return new MemberSetupResult().setSuccess(true);
	}

}
