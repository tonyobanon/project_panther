package com.re.paas.api.reporting.pdf;

public class TextControl {
	
	private UnderlineType underline = UnderlineType.NONE;
	
	private String font = Font.TIMES_ROMAN;
	private float fontSize;
	
	private String text;
		
	public TextControl(String value){
		this.text = (value);
	}
	
	public TextControl(Integer value){
		this.text = (Integer.toString(value));
	}
	
	public UnderlineType getUnderline() {
		return underline;
	}

	public TextControl withUnderline(UnderlineType underline) {
		this.underline = underline;
		return this;
	}

	public String getFont() {
		return font;
	}

	public TextControl setFont(String font) {
		this.font = font;
		return this;
	}
	
	public TextControl forSection(){
		this.underline = UnderlineType.FULL;
		this.font = Font.TIMES_BOLD;
		this.fontSize = (12f);
		this.text = text.toUpperCase();
		return this;
	}

	public TextControl forHeader(){
		this.underline = UnderlineType.NONE;
		this.font = Font.HELVETICA_BOLD;
		this.fontSize = (12f);
		this.text = text.toUpperCase();
		return this;
	}
	
	public TextControl forSubHeader(){
		this.underline = UnderlineType.FULL;
		this.font = Font.COURIER_BOLD;
		this.fontSize = (10f);
		return this;
	}
	
	public TextControl withfontsize(float size){
		this.fontSize = (size);
		return this;
	}
	
	public TextControl withFont(String font){
		this.font = (font);
		return this;
	}
	
	public float getFontSize() {
		return fontSize;
	}

	public TextControl setFontSize(float fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public String getText() {
		return text;
	}

	public TextControl setText(String text) {
		this.text = text;
		return this;
	}

	public static enum UnderlineType{
		NONE, SCALE, FULL
	}
	
	@Override
	public String toString() {
		return text;
	}
	
}
