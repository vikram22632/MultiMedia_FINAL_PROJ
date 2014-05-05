
package model;

import java.awt.Dimension;

/**
 * @author Vikram Datt Rana ....
 * Please note that this class is not being used right now.
 * Right place to look for similar code would be the Color Histogram
 */
public class RGBHistogram {
	
	/* Default constructor */
	public RGBHistogram() {
		red = new long[32];
		blue = new long[32];
		green = new long[32];
	}
	
	/* Calculates the histogram for the RGB image supplied as argument */
	public void calcImgHistogram(RGBImg image) {
		Dimension imgDim = image.getImgDimensions();
	}
	
	private long red[];
	private long blue[];
	private long green[];
}
