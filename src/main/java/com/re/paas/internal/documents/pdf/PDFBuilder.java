package com.re.paas.internal.documents.pdf;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.forms.Column;
import com.re.paas.api.forms.SizeSpec;
import com.re.paas.internal.documents.pdf.gen.Constants;
import com.re.paas.internal.documents.pdf.gen.Image;
import com.re.paas.internal.documents.pdf.gen.InputControl;
import com.re.paas.internal.documents.pdf.gen.Row;
import com.re.paas.internal.documents.pdf.gen.Table;
import com.re.paas.internal.documents.pdf.gen.TextControl;
import com.re.paas.internal.documents.pdf.gen.XCoordinate;
import com.re.paas.internal.documents.pdf.gen.InputControl.Size;
import com.re.paas.internal.documents.pdf.gen.TextControl.UnderlineType;

public class PDFBuilder {

	private final PDDocument document;
	private PDPage currentPage;
	private int currentPageIndex = -1;

	private int lastPageIndex_withNoYOffset_ = -1;

	private PDPageContentStream stream;

	public float CURRENT_Y = Constants.DEFAULT_BORDER_Y;

	static boolean bottomToTop = true;

	/*
	 * 
	 * 
	 * 
	 * //Drawing section separator
	 * 
	 * PDDocument document = new PDDocument(); PDPage page = new PDPage();
	 * document.addPage(page); PDPageContentStream content = new
	 * PDPageContentStream(document, page); PDFont font =
	 * PDType1Font.HELVETICA_BOLD;
	 * 
	 * int cursorX = 70; int cursorY = 500;
	 * 
	 * //draw rectangle content.setNonStrokingColor(200, 200, 200); //gray
	 * background content.fillRect(cursorX, cursorY, 100, 50);
	 * 
	 * //draw text content.setNonStrokingColor(0, 0, 0); //black text
	 * content.beginText(); content.setFont(font, 12);
	 * content.moveTextPositionByAmount(cursorX, cursorY);
	 * content.drawString("Test Data"); content.endText();
	 * 
	 * content.close(); document.save(new File("textOnBackground.pdf"));
	 * document.close();
	 * 
	 */

	public PDFBuilder() {

		this.document = new PDDocument();

		this.currentPage = new PDPage();
		this.currentPageIndex = currentPageIndex + 1;
		this.document.addPage(this.currentPage);

		// Start a new content stream which will "hold" the to be created content
		try {
			this.stream = new PDPageContentStream(this.document, this.currentPage);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		// System.out.println("PDF Document initialized with Page Index " +
		// currentPageIndex);
	}

	public void resetY() {
		CURRENT_Y = Constants.DEFAULT_BORDER_Y;
	}

	public float incrementY(float value) {
		if (bottomToTop) {
			return Constants.FULL_PAGE_HEIGHT - (CURRENT_Y + value);
		} else {
			return CURRENT_Y + value;
		}
	}

	public float decrementY(float value) {
		if (bottomToTop) {
			return Constants.FULL_PAGE_HEIGHT - (CURRENT_Y - value);
		} else {
			return CURRENT_Y - value;
		}
	}

	public float nextY(float lineDistance) {

		// Check IsPageEnd
		// Assuming the height of the TextContent is 30
		if (isPageEnd(lineDistance)) {
			newPage();
			if (bottomToTop) {
				return Constants.FULL_PAGE_HEIGHT - CURRENT_Y;
			} else {
				return CURRENT_Y;
			}
		}

		CURRENT_Y = CURRENT_Y + lineDistance;
		if (bottomToTop) {
			return Constants.FULL_PAGE_HEIGHT - CURRENT_Y;
		} else {
			return CURRENT_Y;
		}
	}

	public float nextY() {

		// Check IsPageEnd
		if (isPageEnd(LineDistance.MEDIUM)) {
			newPage();
			if (bottomToTop) {
				return Constants.FULL_PAGE_HEIGHT - CURRENT_Y;
			} else {
				return CURRENT_Y;
			}
		}

		CURRENT_Y = CURRENT_Y + LineDistance.MEDIUM;
		if (bottomToTop) {
			return Constants.FULL_PAGE_HEIGHT - CURRENT_Y;
		} else {
			return CURRENT_Y;
		}
	}

	public float currentY() {
		if (bottomToTop) {
			return Constants.FULL_PAGE_HEIGHT - CURRENT_Y;
		} else {
			return CURRENT_Y;
		}
	}

	public float footerY() {
		if (bottomToTop) {
			return Constants.DEFAULT_BORDER_Y - 10f;
		} else {
			return Constants.FULL_PAGE_HEIGHT - (Constants.DEFAULT_BORDER_Y - 10f);
		}
	}

	public boolean isPageEnd(float lineDistance) {
		// Assuming the height of the TextContent is LineDistance.MEDIUM
		return Constants.FULL_PAGE_HEIGHT < CURRENT_Y + Constants.DEFAULT_BORDER_Y + lineDistance;
	}

	public boolean isPageEnd(float lineDistance, float elementHeight) {
		// Assuming the height of the TextContent is LineDistance.MEDIUM
		return Constants.FULL_PAGE_HEIGHT < CURRENT_Y + Constants.DEFAULT_BORDER_Y + lineDistance + elementHeight;
	}

	public void newPage() {

		try {

			this.stream.close();

			this.currentPageIndex = currentPageIndex + 1;

			// Check if this page index already exists
			try {

				this.currentPage = document.getPage(currentPageIndex);
				// Execution here, means that the page exists, do not overwrite
				this.stream = new PDPageContentStream(document, this.currentPage, AppendMode.APPEND, false, false);
				// System.out.println("Page with Index " + currentPageIndex + "
				// already exists");

			} catch (IndexOutOfBoundsException e) {

				this.currentPage = new PDPage();
				document.addPage(this.currentPage);

				this.stream = new PDPageContentStream(document, this.currentPage);
				// System.out.println("Page with Index " + currentPageIndex + " was
				// created");
			}

			// Reset Y
			CURRENT_Y = Constants.DEFAULT_BORDER_Y;

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public void writeText(XCoordinate container, SizeSpec size, TextControl text) {

		Float fontSize = text.getFontSize() > 0.0f && text.getFontSize() < size.getTextFontSize() ? text.getFontSize()
				: size.getTextFontSize();

		try {

			stream.setFont(getFont(text.getFont()), fontSize);

			stream.beginText();
			// incrementY((float) (size.getCellHeight() + 0.2))
			stream.newLineAtOffset(container.getStart(), currentY());

			stream.showText(text.getText());
			/*
			 * float containerWidth = (container.getStop() - container.getStart()) -
			 * DEFAULT_XS_PADDING;
			 * 
			 * int pixelsInLine = 0;
			 * 
			 * char[] characters = text.getText().toCharArray();
			 * 
			 * for (int i = 0; i < characters.length; i++) { Character character =
			 * characters[i]; stream.showText(character.toString());
			 * 
			 * pixelsInLine += size.getPixelWidthPerCharacter();
			 * 
			 * if (pixelsInLine > containerWidth && i + 2 < characters.length) {
			 * 
			 * // nextY(..) stream.endText(); stream.beginText();
			 * stream.newLineAtOffset(container.getStart(), nextY((float)
			 * (size.getCellHeight() + 0.2))); pixelsInLine = 0; } }
			 */
			stream.endText();

			if (text.getUnderline().equals(UnderlineType.SCALE)) {

				// The line should be few pixels before/after Text (X gradient)
				stream.moveTo(container.getStart() - Constants.DEFAULT_SMALL_PADDING, nextY(LineDistance.MINI));

				stream.lineTo(container.getStop() + Constants.DEFAULT_SMALL_PADDING, currentY());
				stream.stroke();

			}

			if (text.getUnderline().equals(UnderlineType.FULL)) {

				// The line should be few pixels before/after Text (X gradient)
				stream.moveTo(Constants.DEFAULT_BORDER_X, nextY(LineDistance.MINI));

				stream.lineTo(Constants.FULL_PAGE_WIDTH - Constants.DEFAULT_BORDER_X, currentY());
				stream.stroke();

			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public void writeInputControl(XCoordinate container, SizeSpec size, InputControl control) {

		float containerWidth = (container.getStop() - container.getStart()) - Constants.DEFAULT_XS_PADDING;
		float computedWidth = control.getComputedWidth() < containerWidth ? control.getComputedWidth() : containerWidth;

		try {

			switch (control.getBorderType()) {
			case BOTTOM_ONLY:

				stream.moveTo(container.getStart(), incrementY((float) (size.getCellHeight() * 0.2)));
				stream.lineTo((container.getStart() + computedWidth), incrementY((float) (size.getCellHeight() * 0.2)));
				stream.stroke();
				break;

			case FULL:

				float height = (float) (control.getWidth().equals(Size.SMALL) ? (size.getCellHeight() / 2)
						: (size.getCellHeight() / 1.1));

				stream.addRect(

						container.getStart(),

						incrementY((float) (control.getWidth().equals(Size.SMALL) ? (height * 0.25) : (height * 0.35))),

						computedWidth,

						control.getWidth().equals(Size.SMALL) ? computedWidth : height);

				stream.stroke();
				break;
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public void writeImage(XCoordinate container, SizeSpec size, Image image) {

		float containerWidth = (container.getStop() - container.getStart()) - Constants.DEFAULT_XS_PADDING;

		float height = SizeSpec.fromPixel(image.getHeight());
		float computedHeight = height < size.getCellHeight() ? height : size.getCellHeight();
		try {
			PDImageXObject img = PDImageXObject.createFromFile(
					image.withDimension(SizeSpec.toPixel(computedHeight), SizeSpec.toPixel(containerWidth))
							.getAbsolutePath(),
					document);

			// Check IsPageEnd
			if (isPageEnd(LineDistance.MEDIUM, size.getCellHeight())) {
				newPage();
			}

			stream.drawImage(img, container.getStart(), currentY());
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public PDColor getColor(COSName itemName) {
		COSBase c = this.currentPage.getCOSObject().getItem(itemName);
		if (c instanceof COSArray) {
			PDColorSpace colorSpace = null;
			switch (((COSArray) c).size()) {
			case 1:
				colorSpace = PDDeviceGray.INSTANCE;
				break;
			case 3:
				colorSpace = PDDeviceRGB.INSTANCE;
				break;
			// case 4:
			// colorSpace = PDDeviceCMYK.INSTANCE;
			// break; TODO
			default:
				break;
			}
			return new PDColor((COSArray) c, colorSpace);
		}
		return null;
	}

	public void appendStroke(float distance) {

		if (isPageEnd(LineDistance.MEDIUM, 5f)) {
			newPage();
		}
		try {
			// The line should be few pixels(5) before/after Text (X gradient)
			stream.moveTo(Constants.DEFAULT_BORDER_X - 5f, nextY(distance));

			stream.lineTo(Constants.FULL_PAGE_WIDTH - Constants.DEFAULT_BORDER_X, currentY());
			stream.stroke();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public void drawRect(Color color, float width, float height, boolean fill) {
		drawRect(color, width, height, fill, true, true);
	}

	public void drawRect(Color color, float width, float height, boolean fill, boolean paddingBefore,
			boolean paddingAfter) {
		drawRect(color, Constants.DEFAULT_BORDER_X, currentY(), width, height, fill, paddingBefore, paddingAfter);
	}

	public void drawRect(Color color, float x, float y, float width, float height, boolean fill, boolean paddingBefore,
			boolean paddingAfter) {

		if (paddingBefore) {
			nextY(Constants.DEFAULT_SMALL_PADDING);
		}

		try {
			stream.addRect(x, y, width, height);
			stream.setNonStrokingColor(color);
			if (fill) {
				stream.fill();
			} else {
				stream.stroke();
			}
			stream.setNonStrokingColor(Color.BLACK);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		if (paddingAfter) {
			nextY(height);
			nextY(Constants.DEFAULT_SMALL_PADDING * 2);
		}
	}

	public void appendTable(Table table, boolean noOffsetY) {

		float YOffset = CURRENT_Y;

		if (lastPageIndex_withNoYOffset_ == -1 && noOffsetY) {
			// This is the first page in the group
			// Set the current Page
			System.out.println("Setting the table start Page Index to " + currentPageIndex);
			lastPageIndex_withNoYOffset_ = currentPageIndex;
		}

		for (Row o : table) {

			if (o.getPadding() != null) {
				// Add padding
				nextY(o.getPadding().getCellHeight() / 2);
			}

			SizeSpec sizeSpec = o.getFontSpec();

			// Check IsPageEnd
			if (isPageEnd(sizeSpec.getCellHeight())) {
				newPage();
			}

			// Add cell height
			nextY(sizeSpec.getCellHeight() / 2);

			float currentXStart = table.getConfig().getStartX();

			for (Column column : o) {

				if (column.value() == null) {
					continue;
				}

				float currentXEnd = ((float) currentXStart + column.getWidth());

				if (column.value() instanceof InputControl) {

					InputControl input = (InputControl) column.value();
					writeInputControl(new XCoordinate(currentXStart, currentXEnd),
							o.getComponentSizeSpec() != null ? o.getComponentSizeSpec() : sizeSpec, input);

				} else if (column.value() instanceof TextControl) {

					TextControl text = (TextControl) column.value();

					if (text.getText() != null) {
						writeText(new XCoordinate(currentXStart, currentXEnd), sizeSpec, text);
					}

				} else if (column.value() instanceof Image) {

					Image image = (Image) column.value();

					if (image.getImage() != null) {
						writeImage(new XCoordinate(currentXStart, currentXEnd), sizeSpec, image);
					}
				}

				// Increment by averageWidthX
				currentXStart = currentXEnd;
			}

			// Add cell height
			nextY(sizeSpec.getCellHeight() / 2);

			if (o.getPadding() != null) {
				// Add padding
				nextY(o.getPadding().getCellHeight() / 2);
			}
		}

		/*
		 * if (lastPageIndex_withNoYOffset_ != -1 && lastPageIndex_withNoYOffset_ !=
		 * currentPageIndex) { // restore the page stream this.currentPage =
		 * document.getPage(currentPageIndex); this.stream.close(); this.stream = new
		 * PDPageContentStream(document, currentPage); }
		 */

		if (noOffsetY) {
			// This indicates that that table belongs to a group whose members
			// must start at the same Y axis.
			// Set Y back to YOffset
			CURRENT_Y = YOffset;

			// We may need to get the exact page where the previous table(s)
			// started from

			if (lastPageIndex_withNoYOffset_ != currentPageIndex) {

				try {
					// Scroll back to the first page of the table
					System.out.println("Scrolling back to Page Index " + lastPageIndex_withNoYOffset_);
					this.stream.close();
					this.currentPage = document.getPage(lastPageIndex_withNoYOffset_);
					this.currentPageIndex = lastPageIndex_withNoYOffset_;
					this.stream = new PDPageContentStream(document, this.currentPage, AppendMode.APPEND, false, false);
				} catch (IOException e) {
					Exceptions.throwRuntime(e);
				}
			}

		} else {
			// Commit group table append operation
			lastPageIndex_withNoYOffset_ = -1;
		}

	}

	private PDFont getFont(String font) throws IOException {
		switch (font) {
		case "1":
			return PDType1Font.COURIER;
		case "2":
			return PDType1Font.COURIER_BOLD;
		case "3":
			return PDType1Font.COURIER_BOLD_OBLIQUE;
		case "4":
			return PDType1Font.COURIER_OBLIQUE;
		case "5":
			return PDType1Font.HELVETICA;
		case "6":
			return PDType1Font.HELVETICA_BOLD;
		case "7":
			return PDType1Font.HELVETICA_BOLD_OBLIQUE;
		case "8":
			return PDType1Font.HELVETICA_OBLIQUE;
		case "9":
			return PDType1Font.SYMBOL;
		case "10":
			return PDType1Font.TIMES_BOLD;
		case "11":
			return PDType1Font.TIMES_BOLD_ITALIC;
		case "12":
			return PDType1Font.TIMES_ITALIC;
		case "13":
			return PDType1Font.TIMES_ROMAN;
		case "14":
			return PDType1Font.ZAPF_DINGBATS;

		default:
			return PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream(font));
		}
	}

	/**
	 * This commits all changes. By default, all documents are signed.
	 */
	public void flush(Path path) {

		assert path.endsWith(".pdf");

		Path unsigned = Paths.get(path.toString().replaceFirst(Pattern.quote(".pdf"), "_unsigned.pdf"));

		try {

			OutputStream out = Files.newOutputStream(unsigned);
			stream.close();

			document.save(out);
			document.close();

			out.close();
			
			PdfUtil.signPdf(unsigned, path);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public static class LineDistance {
		public static float MINI = 10;
		public static float SMALL = 20;
		public static float MEDIUM = 30;
		public static float LARGE = 40;
	}

	static {
		// We need to disable LittleCMS in favor of the old KCMS(Kodak Color
		// Management System)
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
	}

}
