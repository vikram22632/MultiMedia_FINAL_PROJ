package model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ColorHistogram {
	
	/* Constructor for the histogram */
	public ColorHistogram() {
		color = new long[125];
		peakVal = 0;
		peakPos = 0;
		avgVal = 0;
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
				red = calc64MappingVal(image.red[i]);
				green = calc64MappingVal(image.green[i]);
				blue = calc64MappingVal(image.blue[i]);
							
				try {
					/* Calculate the color index (its on a 9-nary system (like decimal or binary))*/
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
			avgVal +=color[i];
		}
		
		avgVal = avgVal / color.length;
	}
	
	/*
	 * @ Responsible for resetting the histogram statisitcs
	 */
	public void resetStats() {
		for(int i = 0; i < color.length; i++) {
			color[i] = 0;
		}
		
		peakVal = 0;
		peakPos = 0;
		avgVal = 0;
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
	
	
	/* 
	 * @ Responsible for comparing this histogram with other using the CHI SQUARE function
	 */
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
	
	/*
	 * @ Responsible for comparing this histogram with other using the Bhattacharya distance function
	 */
	public double doBhattacharyaDistComp(ColorHistogram otherHist) {
		double result = 0;
		
		try {
			double sqrtProd = 0;
			double avgSqrt = Math.sqrt(this.avgVal * otherHist.avgVal * Math.pow(color.length, 2));
			
			if(otherHist.color.length != this.color.length) {
				throw new Exception("Range lengths do not match");
			}
			
			for(int i = 0; i < color.length; i++) {
				sqrtProd = Math.sqrt(color[i] * otherHist.color[i]);
			}
			
			result = Math.sqrt(1 - (sqrtProd / avgSqrt));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}	
	
	/* @ responsible for calculating the mmaping value to be used fr finding the index of the mapping */
	private int calc64MappingVal(byte colorVal) {
		int mapVal = 0;
		int quant = Utils.get64QuantizedByteVal(colorVal) & 0xFF;

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
	
	/* @ responsible for calculating the mmaping value to be used fr finding the index of the mapping */
	private int calc32MappingVal(byte colorVal) {
		int mapVal = 0;
		int quant = Utils.get32QuantizedByteVal(colorVal) & 0xFF;

		/* Posible quantized values for the colors will be
		 * 0,63,127,191,255 */
		mapVal = quant / 32;
		if((mapVal != 0) || ((quant % 32) != 0)) {
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
	private long	avgVal;
}
