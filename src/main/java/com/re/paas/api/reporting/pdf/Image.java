package com.re.paas.api.reporting.pdf;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.utils.ImageUtils;
import com.re.paas.api.utils.Utils;


public class Image {

	public static final String DEFAULT_FORMAT = "png";
	private BufferedImage image;

	private int height;
	private int width;

	public Image(String url) {

		if(url == null) {
			return;
		}
		
		try {
			URL imageUrl = new URL(url);
			image = ImageIO.read(imageUrl);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		this.height = image.getHeight();
		this.width = image.getWidth();
	}

	public File withDimension(int maxHeight, int width) {

		try {			

			// Get Aspect Ratio
			float aspectRatio = ((float)this.width / (float)this.height);		
			
			int aspectRatioWidth = Math.round(maxHeight * aspectRatio);
			
			int computedWidth = aspectRatioWidth < width ? aspectRatioWidth : width;

			// Calculate dimension, Scale Image
			BufferedImage scaledImage = ImageUtils.getScaledInstance(this.image, computedWidth, maxHeight);
					//image.getScaledInstance(computedWidth, maxHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			this.width = scaledImage.getWidth();
			this.height = scaledImage.getHeight();

			// Write to temp Folder
			File tempFile = File.createTempFile(Utils.newRandom(), "." + DEFAULT_FORMAT);
			ImageIO.write((RenderedImage) scaledImage, DEFAULT_FORMAT, tempFile);
		
			return tempFile;
			
		} catch (IOException ex) { 
			Exceptions.throwRuntime(ex);
			return null;
		}		
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
	
	public static String getDefaultFormat() {
		return DEFAULT_FORMAT;
	}

	public BufferedImage getImage() {
		return image;
	}
}
