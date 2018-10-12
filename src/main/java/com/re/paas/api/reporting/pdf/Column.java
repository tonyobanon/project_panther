package com.re.paas.api.reporting.pdf;

import java.awt.geom.AffineTransform;

public class Column {

	//@Used by PDF preprocessors
	private boolean singleRow;

	private float width;
	private float containerWidth;
	private float percentileWidth = 100;

	private Object value;

	protected boolean is_image = false;
	protected boolean is_affline_transform = false;
	protected boolean is_text = false;

	protected boolean is_input_control = false;

	public Column() {
		this.value = new TextControl("");
		is_text = true;
	}

	public Column(String o) {
		this.value = new TextControl(o);
		is_text = true;
	}

	public Column(Image o) {
		this.value = o;
		is_image = true;
	}

	public Column(AffineTransform o) {
		this.value = o;
		is_affline_transform = true;
	}

	public Column(TextControl o) {
		this.value = o;
		is_text = true;
	}

	public Column(InputControl o) {
		this.value = o;
		is_input_control = true;
	}

	public Object value() {
		return value;
	}

	public float getContainerWidth() {
		return containerWidth;
	}

	protected Column withContainerWidth(float width) {
		this.containerWidth = width;
		this.width = ((float) (percentileWidth / 100) * containerWidth);
		return this;
	}

	public Column withPercentileWidth(float width) {
		this.percentileWidth = width;
		return this;
	}
	
	public Column withPercentileWidth(double width) {
		return withPercentileWidth((float)width);
	}
	
	public Column withSingleRow(boolean singleRow) {
		this.singleRow = singleRow;
		return this;
	}
	
	public float getPercentileWidth() {
		return percentileWidth;
	}

	public float getWidth() {
		return width;
	}
	
	public boolean isSingleRow() {
		return singleRow;
	}

	public static enum SupportedValueTypes {
		IMAGE, AFFLINE_TRANSFORM, TEXT_RENDER, INPUT_CONTROL,
	}

}
