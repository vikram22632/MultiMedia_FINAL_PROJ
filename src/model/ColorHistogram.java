package model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ColorHistogram {
	
	/* Constructor for the histogram */
	public ColorHistogram() {
		color = new long[125];
		peakVal = 0;
		peakPos = 0;
	}
	
	/* @ responsible for calculating the histogram statistics*/
	public void calculateStats(RGBImg image) {
		Dimension imgDim = image.getImgDimensions();
		int totCnt = imgDim.height * imgDim.width;
		int index = 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		
		for(int i = 0; i < totCnt; i++) {
			//if(image.alpha[i] == 0xff) {
				red = calcMappingVal(image.red[i]);
				green = calcMappingVal(image.green[i]);
				blue = calcMappingVal(image.blue[i]);
							
				try {
					/* Calculate the color index (its on a 5-nary system (like decimal or binary))*/
					index = red + green * 5 + blue * 25;
					if(index > 124) {
						throw new Exception("index val greater than 124");
					}
					
					/* Add the pixel count in the corresponding histogram */
					color[index] += 1;
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			//}
		}
		
		/* Check for the peak of the histogram */
		for(int i =0; i < color.length; i++) {
			if(peakVal < color[i]) {
				peakVal = color[i];
				peakPos = i;
			}
		}
	}
	
	/*
	 * Responsible for resetting the histogram statisitcs
	 */
	public void resetStats() {
		for(int i = 0; i < color.length; i++) {
			color[i] = 0;
		}
		
		peakVal = 0;
		peakPos = 0;
	}
	
	public BufferedImage getHistogramImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		return img;
	}
	
	/* @ Responsible for printing the histogram on the console */
	public void printHistogram() {
		System.out.println("histogram");
		System.out.println("------------------------------------------------------");
		for(int i=0; i < color.length; i++) {
			if(color[i] != 0) {
				System.out.println("COLOR-["+(i+1)+"]="+color[i]);
			}
		}
		System.out.println("------------------------------------------------------");
	}
	
	
	/* @ Does and doChiSquareComp for this histogram with other one and returns the result */
	public double doChiSquareComp(ColorHistogram otherHist) {
		double result = 0;
		double tempVal = 0;
		
		try {
			if(otherHist.color.length != this.color.length) {
				throw new Exception("Range lengths do not match");
			}
			
			for(int i = 0; i < color.length; i++) {
				if(color[i] != 0) {
					tempVal = Math.pow(color[i] - otherHist.color[i], 2) / color[i];
				}
				else {
					tempVal = Math.pow(color[i] - otherHist.color[i], 2);
				}
				result += tempVal;
			}
			
			result = Math.sqrt(result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/* @ responsible for calculating the mmaping value to be used fr finding the index of the mapping */
	private int calcMappingVal(byte colorVal) {
		int mapVal = 0;
		int quant = Utils.getQuantizedByteVal(colorVal) & 0xFF;

		/* Posible quantized values for the colors will be
		 * 0,63,127,191,255 */
		mapVal = quant / 64;
		if((mapVal != 0) || ((quant % 64) != 0)) {
			mapVal++;
		}
		
		/* 
		 * The map should have these values now 
		 * 0 -> 0
		 * 63 -> 1
		 * 127 -> 2
		 * 191 -> 3
		 * 255 -> 4 
		 */
		return mapVal;
	}
	 
	/* Member variables */
	private long	color[];
	private long	peakVal;
	private int		peakPos;
}
