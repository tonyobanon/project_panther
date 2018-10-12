package com.re.paas.api.reporting.pdf;

public class SizeSpec {

	private static final SizeSpec header = new SizeSpec(5);
	private static final SizeSpec body = new SizeSpec(3);
	
	private static final float FONT_SIZE_TO_VSIZE_RATIO = 1.5f;

	private static final float PIXEL_TO_SIZE_RATIO = 1f;

	private Float textFontSize;

	private Float cellHeight;

	public SizeSpec(Integer units) {

		switch (units) {

		case 1:

			this.textFontSize = 8f;
			break;

		case 2:

			this.textFontSize = 9f;
			break;

		case 3:

			this.textFontSize = 10f;
			break;

		case 4:

			this.textFontSize = 12f;
			break;

		case 5:

			this.textFontSize = 14f;
			break;

		case 6:

			this.textFontSize = 16f;
			break;

		case 7:

			this.textFontSize = 18f;
			break;

		case 8:

			this.textFontSize = 20f;
			break;

		case 9:

			this.textFontSize = 22f;
			break;

		case 10:

			this.textFontSize = 26f;
			break;

		case 11:

			this.textFontSize = 28f;
			break;

		case 12:

			this.textFontSize = 30f;
			break;

		}

		fontSizeToCellHeight();

	}

	private void fontSizeToCellHeight() {

		// This would have worked, but font size is not directly proportional to
		// float size
		// this.cellHeight = ((float) this.textFontSize *
		// FONT_SIZE_TO_HSIZE_RATIO);

		float size = 0;
		float inc = 0;

		for (int i = 0; i < Math.round(textFontSize); i++) {
			int n = i + 1;
			inc = (float) (inc + 0.1);
			size = (n * FONT_SIZE_TO_VSIZE_RATIO) - inc;
		}

		this.cellHeight = size;
	}

	public Float getTextFontSize() {
		return textFontSize;
	}

	public Float getCellHeight() {
		return cellHeight;
	}

	public static float fromPixel(int pixels) {
		return ((float) pixels / PIXEL_TO_SIZE_RATIO);
	}

	public static int toPixel(float size) {
		return Math.round((float) size * PIXEL_TO_SIZE_RATIO);
	}
	
	public static SizeSpec header() {
		return header;
	}

	public static SizeSpec body() {
		return body;
	}
}
