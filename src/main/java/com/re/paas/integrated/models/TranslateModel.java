package com.re.paas.integrated.models;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.integrated.models.helpers.GoogleHelper;

public class TranslateModel extends BaseModel {

	private static Translate translateService;

	@Override
	public String path() {
		return "core/ext/translate";
	}

	@Override
	public void preInstall() {

	}
	
	@Override
	public void start() {

		// Instantiates a client
			translateService = TranslateOptions.newBuilder()
					.setCredentials(
							GoogleHelper.getCredentials())
					.build().getService();
	}

	/**
	 * @param text
	 *            The text to translate
	 */
	protected static Map<String, String> translate(List<String> text, String sourceLocale, String targetLocale) {

		String sourceLanguage = Locale.forLanguageTag(sourceLocale).getLanguage();
		String targetLanguage = Locale.forLanguageTag(targetLocale).getLanguage();

		final Map<String, String> result = new HashMap<String, String>();

		List<Translation> translations = translateService.translate(text,
				TranslateOption.sourceLanguage(sourceLanguage), TranslateOption.targetLanguage(targetLanguage));

		for (int i = 0; i < translations.size(); i++) {
			Translation t = translations.get(i);
			result.put(text.get(i), t.getTranslatedText());
		}

		return result;
	}

	@Override
	public void install(InstallOptions options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
