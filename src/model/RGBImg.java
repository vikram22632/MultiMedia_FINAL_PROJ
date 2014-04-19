package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class RGBImg {
	/*
	 * @ Constructor of the class. This should be used when image is being used as a
	 * frame of a video.
	 */
	public RGBImg(int width, int height) {
		initializeImage(width, height, "");
	}
	
	/*
	 * @ Constructor of the class. This should be used when the image is a stand alone
	 * media
	 */
	public RGBImg(int width, int height, String name) {
		initializeImage(width, height, name);
	}
	
	/*
	 * @ responsible for reading and thus storing a particular frame of the video
	 * from the given input stream
	 */
	public void readStandaloneImg(String path) {
		/* read the image file */
		try {
			InputStream is = new FileInputStream(new File(path));
			/* Read the colour components */
			readImgFrame(is);
			is.close();
			
			String alphaFilePath = path.replace(".rgb", ".alpha");
			File alphaFile = new File(alphaFilePath);
			if(alphaFile.exists() && !alphaFile.isDirectory()) {
				is = new FileInputStream(alphaFile);
				readAlphaComponent(alpha, is);
				is.close();
			}
			else {
				/* Simply assign opaque values for alpha bytes */
				for(int i = 0; i < alpha.length; i++) {
					alpha[i] = (byte)0xff;
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @ responsible for reading and thus storing a particular frame of the video
	 * from the given input stream
	 */
	public void readImgFrame(InputStream is) {
		try {
			/* Read the image components individually from the input stream,
			 * for this particular frame */
			readColorComponent(red, is);
			readColorComponent(green, is);
			readColorComponent(blue, is);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @ Responsible for getting the image to be displayed on the screen
	 */
	public BufferedImage getdisplayImage() {
		int		x		= 0;
		int		y		= 0;
		int		iPixel	= 0;
		int		ind		= 0;

		/* Create an instance of the buffered image to fill with the pixel and colour information */
		BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		/* Fill the colour information for the pixels in the image */
		for(y = 0; y < this.height; y++) {
			for(x = 0; x < this.width; x++) {
				ind = x + (y * width);
				//iPixel = ((this.alpha[ind] & 0xff) << 24) | ((this.red[ind] & 0xff) << 16) | ((this.green[ind] & 0xff)) << 8 | (this.blue[ind] & 0xff);
				iPixel = ((0xff) << 24) | ((this.red[ind] & 0xff) << 16) | ((this.green[ind] & 0xff)) << 8 | (this.blue[ind] & 0xff);
				img.setRGB(x, y, iPixel);
				iPixel = 0;
			}
		}
		
		/* Return the image for any display purposes */
		return img;
	}
	
	/* @ Responsible for getting the name of the image */
	public String getImageName() {
		return this.imgName;
	}
	
	/*
	 * @ Responsible for encircling (actually adding a rectangle) a portion of the image
	 */
	public void highlightImgPortion(int startX, int startY, int endX, int endY) {
		int	iCnt	= 0;
		int	iPos	= 0;
		
		/* TODO: Very Crude Logic Written here, needs to be looked into and improved */
		/* --------- Print Rows --------- */
		/* Top Row */
		iPos = (width * startY) + startX;
		for(iCnt = iPos; iCnt <= iPos + (endX - startX); iCnt++) {
			blue[iCnt] = 0;
			green[iCnt] = 0;
			red[iCnt] = (byte)0xff;
		}
		
		/* Bottom Row */
		iPos = (width * endY) + startX;
		for(iCnt = iPos; iCnt <= iPos + (endX - startX); iCnt++) {
			blue[iCnt] = 0;
			green[iCnt] = 0;
			red[iCnt] = (byte)0xff;
		}
		
		/* Left Column */
		iPos = (width * startY) + startX;
		for(iCnt = iPos; iCnt <= ((width * endY) + startX); iCnt += width) {
			blue[iCnt] = 0;
			green[iCnt] = 0;
			red[iCnt] = (byte)0xff;
		}
		
		/* Right Column */
		iPos = (width * startY) + endX;
		for(iCnt = iPos; iCnt <= ((width * endY) + endX); iCnt += width) {
			blue[iCnt] = 0;
			green[iCnt] = 0;
			red[iCnt] = (byte)0xff;
		}
		
	}
	
	/*
	 * @ Responsible for reading the individual colour components from the input stream
	 * provided as the input
	 */
	private void readColorComponent(byte[] comp, InputStream is) throws IOException {
		int			offset	= 0;
		int			numRead	= 0;
		
		while((offset < comp.length) && ((numRead = is.read(comp, offset, comp.length - offset)) >= 0) ) {
			offset += numRead;
		}
	}
	
	private void readAlphaComponent(byte[] alpha, InputStream is) throws IOException {
		int			offset	= 0;
		int			numRead	= 0;
		
		while((offset < alpha.length) && ((numRead = is.read(alpha, offset, alpha.length - offset)) >= 0) ) {
			offset += numRead;
		}
		
		for(int i =0; i < alpha.length; i++) {
			if(alpha[i] == 1) {
				alpha[i] = (byte) 0xff;
			}
		}
	}

	/*
	 * @ Responsible for initialising the members of the image class
	 */
	private void initializeImage(int width, int height, String name) {
		int size = width * height;
		
		red = new byte[size];
		green = new byte[size];
		blue = new byte[size];
		alpha = new byte[size];
		
		this.width = width;
		this.height = height;
		this.imgName = name;
	}
	
	/* Members of the class */
	private String imgName;
	public int	width;
	public int height;
	public byte red[];
	public byte green[];
	public byte blue[];
	private byte alpha[];
	
	public static final int SUCCESS = 0;
	public static final int FAILURE = -1403;
}
