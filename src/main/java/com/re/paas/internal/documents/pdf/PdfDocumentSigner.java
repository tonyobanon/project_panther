
package com.re.paas.internal.documents.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.crypto.KeyStoreConfig;
import com.re.paas.internal.documents.AbstractDocumentSigner;
import com.re.paas.internal.models.ConfigModel;
import com.re.paas.internal.models.LocationModel;


public class PdfDocumentSigner extends AbstractDocumentSigner implements SignatureInterface {

	/**
	 * Signs the given PDF file.
	 */

	public void sign(PDDocument document, OutputStream output) throws IOException {
		
		boolean external = true;
		
		int accessPermissions = getMDPPermission(document);
		if (accessPermissions == 1) {
			throw new IllegalStateException(
					"No changes to the document are permitted due to DocMDP transform parameters dictionary");
		}

		// create signature dictionary
		PDSignature signature = new PDSignature();
		signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
		signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);

		signature.setName(ConfigModel.get(ConfigKeys.ORGANIZATION_NAME));
		signature.setLocation(LocationModel.getCountryName(ConfigModel.get(ConfigKeys.ORGANIZATION_COUNTRY)));

		signature.setReason("I agree...");
		// TODO extract the above details from the signing certificate? Reason
		// as a parameter?

		// the signing date, needed for valid signature
		signature.setSignDate(Calendar.getInstance());

		// Optional: certify
		if (accessPermissions == 0) {
			setMDPPermission(document, signature, 2);
		}
		
		external = external && KeyStoreConfig.get().getSignContext() != null;

		if (external) {

			document.addSignature(signature);
			ExternalSigningSupport externalSigning = document.saveIncrementalForExternalSigning(output);
			// invoke external signature service
			byte[] cmsSignature = sign(externalSigning.getContent());
			// set signature bytes received from the service
			externalSigning.setSignature(cmsSignature);
		
		} else {
			// register signature dictionary and sign interface
			document.addSignature(signature, this);

			// write incremental (only for signing purpose)
			document.saveIncremental(output);
		}
	}

	/**
	 * Get the access permissions granted for this document in the DocMDP transform
	 * parameters dictionary. Details are described in the table "Entries in the
	 * DocMDP transform parameters dictionary" in the PDF specification.
	 *
	 * @param doc document.
	 * @return the permission value. 0 means no DocMDP transform parameters
	 *         dictionary exists. Other return values are 1, 2 or 3. 2 is also
	 *         returned if the DocMDP transform parameters dictionary is found but
	 *         did not contain a /P entry, or if the value is outside the valid
	 *         range.
	 */
	private int getMDPPermission(PDDocument doc) {
		COSBase base = doc.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.PERMS);
		if (base instanceof COSDictionary) {
			COSDictionary permsDict = (COSDictionary) base;
			base = permsDict.getDictionaryObject(COSName.DOCMDP);
			if (base instanceof COSDictionary) {
				COSDictionary signatureDict = (COSDictionary) base;
				base = signatureDict.getDictionaryObject("Reference");
				if (base instanceof COSArray) {
					COSArray refArray = (COSArray) base;
					for (int i = 0; i < refArray.size(); ++i) {
						base = refArray.getObject(i);
						if (base instanceof COSDictionary) {
							COSDictionary sigRefDict = (COSDictionary) base;
							if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject("TransformMethod"))) {
								base = sigRefDict.getDictionaryObject("TransformParams");
								if (base instanceof COSDictionary) {
									COSDictionary transformDict = (COSDictionary) base;
									int accessPermissions = transformDict.getInt(COSName.P, 2);
									if (accessPermissions < 1 || accessPermissions > 3) {
										accessPermissions = 2;
									}
									return accessPermissions;
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}

	private void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions) {
		COSDictionary sigDict = signature.getCOSObject();

		// DocMDP specific stuff
		COSDictionary transformParameters = new COSDictionary();
		transformParameters.setItem(COSName.TYPE, COSName.getPDFName("TransformParams"));
		transformParameters.setInt(COSName.P, accessPermissions);
		transformParameters.setName(COSName.V, "1.2");
		transformParameters.setNeedToBeUpdated(true);

		COSDictionary referenceDict = new COSDictionary();
		referenceDict.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));
		referenceDict.setItem("TransformMethod", COSName.getPDFName("DocMDP"));
		referenceDict.setItem("DigestMethod", COSName.getPDFName("SHA1"));
		referenceDict.setItem("TransformParams", transformParameters);
		referenceDict.setNeedToBeUpdated(true);

		COSArray referenceArray = new COSArray();
		referenceArray.add(referenceDict);
		sigDict.setItem("Reference", referenceArray);
		referenceArray.setNeedToBeUpdated(true);

		// Catalog
		COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
		COSDictionary permsDict = new COSDictionary();
		catalogDict.setItem(COSName.PERMS, permsDict);
		permsDict.setItem(COSName.DOCMDP, signature);
		catalogDict.setNeedToBeUpdated(true);
		permsDict.setNeedToBeUpdated(true);
	}

}