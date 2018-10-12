package com.re.paas.internal.sentences;

import java.util.List;

import com.re.paas.api.annotations.Todo;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.classes.ClientResources.WebResource;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.fusion.ui.AbstractComponent;
import com.re.paas.api.fusion.ui.AbstractUIComponentDelegate;
import com.re.paas.api.sentences.ObjectEntity;
import com.re.paas.api.sentences.SubjectEntity;
import com.re.paas.internal.models.BaseUserModel;

 class Utils {

	private static final String SPACE = ClientResources.HtmlCharacterEntities.SPACE.toString();

	private static String getHtmlATag(String uri, String name) {
		return uri == null ? name : WebResource.get(uri, name).toString();
	}

	private static String _stringify(Functionality functionality, String name, List<Object> identifiers) {

		AbstractUIComponentDelegate clientDelegate = AbstractComponent.getDelegate();
		
		String uri = functionality != null ? clientDelegate.getUri(functionality) : null;

		if (identifiers == null || identifiers.isEmpty()) {
			return getHtmlATag(uri, name);
		}

		List<String> uriParams = uri != null ? clientDelegate.getUriParams(uri) : null;

		if (uriParams == null || uriParams.isEmpty()) {
			return getHtmlATag(uri, name);
		}

		if (identifiers.size() > uriParams.size()) {
			throw new ResourceException(ResourceException.FAILED_VALIDATION);
		}

		StringBuilder builder = new StringBuilder();

		builder.append(uri);

		for (int i = 0; i < identifiers.size(); i++) {

			String k = uriParams.get(i);
			Object v = identifiers.get(i);

			if (v != null) {
				builder.append((i == 0 ? "?" : "&") + k + "=" + v.toString());
			}
		}

		return getHtmlATag(builder.toString(), name);
	}

	@Todo("If subject refers to the current user, transform to 'You' ")
	public static String stringify(SubjectEntity subject) {

		List<Object> identifiers = subject.getIdentifiers();
		String name = BaseUserModel.getPersonName(Long.parseLong(identifiers.get(0).toString()), false).toString();

		return _stringify(subject.getFunctionality(), name, identifiers);
	}

	public static String stringify(ObjectEntity object) {

		StringBuilder sb = new StringBuilder();

		if (object.getName() == null) {
			String o = object.getArticle() + SPACE
					+ _stringify(object.getFunctionality(), object.getClientRBRef().toString(), object.getIdentifiers());
			sb.append(o);
		} else {

			String o = _stringify(object.getFunctionality(), object.getName(), object.getIdentifiers());
			sb.append(o);
		}

		object.getQualifiers().forEach(rbRef -> {
				sb.append(SPACE).append(rbRef.toString());
			});

		return sb.toString();
	}

}
