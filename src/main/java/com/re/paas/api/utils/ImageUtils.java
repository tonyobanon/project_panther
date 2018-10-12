
package com.re.paas.api.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtils
{
    public static BufferedImage getScaledInstance(final BufferedImage bufferedImage, final int width, final int height) {
        final Object value_INTERPOLATION_BILINEAR = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        boolean b = true;
        int widthMaintainingAspect = width;
        int heightMaintainingAspect = height;
        if (widthMaintainingAspect == -1) {
            widthMaintainingAspect = getWidthMaintainingAspect(bufferedImage.getWidth(), bufferedImage.getHeight(), heightMaintainingAspect);
        }
        if (heightMaintainingAspect == -1) {
            heightMaintainingAspect = getHeightMaintainingAspect(bufferedImage.getWidth(), bufferedImage.getHeight(), widthMaintainingAspect);
        }
        if (bufferedImage.getWidth() < widthMaintainingAspect || bufferedImage.getHeight() < heightMaintainingAspect) {
            b = false;
        }
        return getScaledInstance(bufferedImage, widthMaintainingAspect, heightMaintainingAspect, value_INTERPOLATION_BILINEAR, b);
    }
    
    public static int getHeightMaintainingAspect(final int n, final int n2, final int n3) {
        return getDimensionMaintainingAspect(1.0f * n2 / (float)n, n3);
    }
    
    public static int getWidthMaintainingAspect(final int n, final int n2, final int n3) {
        return getDimensionMaintainingAspect(1.0f * n / (float)n2, n3);
    }
    
    private static int getDimensionMaintainingAspect(final float n, final int n2) {
        return Math.round((float)n2 * n);
    }
    
    public static BufferedImage getScaledInstance(final BufferedImage bufferedImage, final int width, final int height, final Object o, final boolean b) {
        final int n3 = (bufferedImage.getTransparency() == 1) ? 1 : 2;
        BufferedImage bufferedImage2 = bufferedImage;
        int imageWidth;
        int imageHeight;
        if (b) {
            imageWidth = bufferedImage.getWidth();
            imageHeight = bufferedImage.getHeight();
        }
        else {
            imageWidth = width;
            imageHeight = height;
        }
        do {
            if (b && imageWidth > width) {
                imageWidth /= 2;
                if (imageWidth < width) {
                    imageWidth = width;
                }
            }
            if (b && imageHeight > height) {
                imageHeight /= 2;
                if (imageHeight < height) {
                    imageHeight = height;
                }
            }
            final BufferedImage bufferedImage3 = new BufferedImage(imageWidth, imageHeight, n3);
            final Graphics2D graphics = bufferedImage3.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, o);
            graphics.drawImage(bufferedImage2, 0, 0, imageWidth, imageHeight, null);
            graphics.dispose();
            
            bufferedImage2 = bufferedImage3;
            
        } while (imageWidth != width || imageHeight != height);
        return bufferedImage2;
    }
}
