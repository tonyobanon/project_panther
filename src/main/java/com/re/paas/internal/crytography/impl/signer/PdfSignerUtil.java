package com.re.paas.internal.crytography.impl.signer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.Exceptions;

public class PdfSignerUtil {

	@Todo("Add support for visual signatures")
	public static void signPdf(Path in, Path out) {

		try {

			InputStream inStream = Files.newInputStream(in);
			OutputStream outStream = Files.newOutputStream(out);

			// sign
			try (PDDocument doc = PDDocument.load(inStream)) {

				PdfDocumentSigner signing = new PdfDocumentSigner();
				signing.sign(doc, outStream);
			}
			
			inStream.close();
			outStream.close();

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

}
