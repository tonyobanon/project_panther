package com.re.paas.internal.crytography.impl;

import static com.re.paas.api.forms.input.InputType.BOOLEAN;
import static com.re.paas.api.forms.input.InputType.FILE;
import static com.re.paas.api.forms.input.InputType.SECRET;
import static com.re.paas.api.forms.input.InputType.TEXT;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.crytography.CryptoAdapter;
import com.re.paas.api.crytography.CryptoProvider;
import com.re.paas.api.crytography.KeyStoreProperties;
import com.re.paas.api.crytography.SSLContext;
import com.re.paas.api.crytography.SignContext;
import com.re.paas.api.crytography.TSAClient;
import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.FileTypeDescriptor;
import com.re.paas.internal.forms.FormHelper;

public class DefaultCryptoAdapter implements CryptoAdapter {

	private static Form initForm;
	
	@Override
	public String name() {
		return "default_provider";
	}

	@Override
	public String title() {
		return "Default Crypto Provider";
	}

	@Override
	public String iconUrl() {
		return null;
	}

	@Override
	public Form initForm() {

		if (initForm != null) {
			return initForm;
		}

		Section keyStoreSection = new Section().setTitle("keystore_settings")

				.withField(new SimpleField("keyStoreEnabled", BOOLEAN, "enable_keytore"))

				.withField(new SimpleField("keyStoreFile", FILE.setDescriptor(new FileTypeDescriptor(true)),
						"keystore_file"))

				.withField(new SimpleField("keyStorePassword", SECRET, "keyStore_password"));

		Section certificatesSection = new Section().setTitle("certificates_settings")

				.withField(new SimpleField("sslCertAlias", TEXT, "ssl_cert_alias"))

				.withField(new SimpleField("signCertAlias", TEXT, "signing_Certificate_alias"))
				.withField(new SimpleField("signPrivateKeyAlias", TEXT, "signing_private_key_alias"))
				.withField(new SimpleField("signPrivateKeyPassword", SECRET, "signing_private_key_password"));

		Section tsaSection = new Section().setTitle("tsa_settings")

				.withField(new SimpleField("tsaUrl", TEXT, "tsaUrl"))
				.withField(new SimpleField("tsaUsername", TEXT, "tsa_username"))
				.withField(new SimpleField("tsaPassword", TEXT, "tsa_password"))
				.withField(new SimpleField("tsaDigest", TEXT, "tsa_digest"));

		Form form = new Form().setTitle("crypto_settings").addSection(keyStoreSection).addSection(certificatesSection)
				.addSection(tsaSection);

		initForm = form;
		return form;
	}
	
	private KeyStoreProperties getKeyStoreProperties(Map<String, String> fields) {
		
		KeyStoreProperties properties = new DefaultKeyStoreProperties();

		properties.setKeyStoreEnabled(Boolean.parseBoolean(fields.get("keyStoreEnabled")));
		properties.setKeyStorePassword(fields.get("keyStorePassword"));

		KeyStore ks = null;

		// Load keystore

		try {

			ks = KeyStore.getInstance(properties.getType());

			Path path = FormHelper.getFormFilesPath(this.initForm(), "keyStoreFile");
			
			InputStream ksInput = Files.newInputStream(path);
			ks.load(ksInput, properties.getKeyStorePassword().toCharArray());

		} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			Exceptions.throwRuntime(e);
		}

		properties.setKeyStore(ks);

		SSLContext sslCtx = new SSLContext(fields.get("sslCertAlias"));
		properties.setSslContext(sslCtx);

		SignContext signCtx = new SignContext();
		signCtx.setCertAlias(fields.get("sslCertAlias"));

		String tsaUrl = fields.get("tsaUrl");
		String tsaUsername = fields.get("tsaUsername");
		String tsaPassword = fields.get("tsaPassword");
		String tsaDigest = fields.get("tsaDigest");

		signCtx.setTsaClient(new TSAClient(tsaUrl, tsaUsername, tsaPassword, tsaDigest));

		RSAPrivateKey privateKey = null;

		try {
			privateKey = (RSAPrivateKey) ks.getKey(fields.get("signPrivateKeyAlias"),
					fields.get("signPrivateKeyPassword").toCharArray());

		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			Exceptions.throwRuntime(e);
		}

		signCtx.setPrivateKey(privateKey);

		properties.setSignContext(signCtx);
		
		return properties;
	}

	@Override
	public CryptoProvider getResource(Map<String, String> fields) {
		DefaultCryptoProvider provider = new DefaultCryptoProvider(getKeyStoreProperties(fields));
		return provider;
	}

}
