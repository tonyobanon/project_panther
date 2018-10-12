package com.re.paas.api.reporting.pdf;

public class InputControl {

	private final BorderType borderType;
	private Size width;

	private Float finalWidth;

	public InputControl(BorderType borderType) {
		this.borderType = borderType;
		this.width = Size.MEDIUM;
		computeWidth();
	}

	public BorderType getBorderType() {
		return borderType;
	}
	
	public InputControl withWidth(Float Width) {
		this.finalWidth = Width;
		return this;
	}

	public InputControl withWidth(Size Width) {
		this.width = Width;
		computeWidth();
		return this;
	}

	public Size getWidth() {
		return width;
	}

	public float getComputedWidth() {
		return finalWidth;
	}

	private void computeWidth() {
		switch (width) {

		case SMALL:
			finalWidth = 10f;
			break;

		case MEDIUM:
			finalWidth = 100f;
			break;

		case LARGE:
			finalWidth = 200f;
			break;
		}
	}

	public static enum BorderType {
		BOTTOM_ONLY, FULL
	}

	public static enum Size {
		SMALL, LARGE, MEDIUM
	}

}
