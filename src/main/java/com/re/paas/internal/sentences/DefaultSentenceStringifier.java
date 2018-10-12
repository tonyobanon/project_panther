package com.re.paas.internal.sentences;

import com.re.paas.api.sentences.ObjectEntity;
import com.re.paas.api.sentences.SentenceStringifier;
import com.re.paas.api.sentences.SubjectEntity;

public class DefaultSentenceStringifier implements SentenceStringifier {

	@Override
	public String stringify(Object object) {
		
		if(object instanceof SubjectEntity) {
			return Utils.stringify((SubjectEntity) object);
		}
		
		else if(object instanceof ObjectEntity) {
			return Utils.stringify((ObjectEntity) object);
		}
		
		return object.toString();
	}
}
