package model;

import java.awt.Dimension;
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
	
	/* @ get the dimension of the image */
	public Dimension getImgDimensions() {
		return new Dimension(width, height);
	}
	
	/* Returns a scaled image, produced from the current one */
	public RGBImg getScaledImg(float scaleVal, boolean aliasing) {
		int		newHeight		= 0;
		int		newWidth		= 0;
		int		curX			= 0;
		int		curY			= 0;
		int		newTranslatedY	= 0;
		int		newTranslatedX	= 0;
		int		curTranslatedY	= 0;
		int		curTranslatedX	= 0;
		
		/* Compute the height and widht of the new RGB object*/
		newHeight = (int)(this.height * scaleVal);
		newWidth = (int) (this.width * scaleVal);
		
		/* Create instance of the new RGB image */
		RGBImg newImg = new RGBImg(newWidth, newHeight);
		
		/* Check which pixels to map from the current image to the new image */
		/* Fill the color components for those pixels in the new RGB image */
		for(int y=0; y < newHeight; y++) {
			/* Get the translated new height coordinate */
			newTranslatedY = y + (newHeight / 2);
			curTranslatedY = (int) (newTranslatedY / scaleVal);
			curY = curTranslatedY - (this.height / 2);
			
			if((curY < 0) || (curY >= this.height)) {
				/* If the pixel position calculated from the new image doesnt lie in the current height range
				 * we can ignore this whole row. */
				continue;
			}
			
			for(int x = 0; x < newWidth; x++) {
				/* Get the translated new width coordinate */
				newTranslatedX = x + (newWidth / 2);
				curTranslatedX = (int) (newTranslatedX / scaleVal);
				curX = curTranslatedX - (this.width / 2);
				
				if((curX < 0) || (curX >= this.width)) {
					/* If the pixel position calculated from the new image doesnt lie in the current width range
					 * we can ignore this pixel. */
					continue;
				}
				
				if(aliasing != true) {
					newImg.red[x + (y * newWidth)] = this.red[curX + (curY * this.width)];
					newImg.green[x + (y * newWidth)] = this.green[curX + (curY * this.width)];
					newImg.blue[x + (y * newWidth)] = this.blue[curX + (curY * this.width)];
					newImg.alpha[x + (y * newWidth)] = this.alpha[curX + (curY * this.width)];
				}
				else {
					/* Apply 3X3 pre-filter for the reduction of aliasing effects in the output image */
					newImg.red[x + (y * newWidth)] = this.apply3X3Filer(this.red, curX, curY);
					newImg.green[x + (y * newWidth)] = this.apply3X3Filer(this.green, curX, curY);
					newImg.blue[x + (y * newWidth)] = this.apply3X3Filer(this.blue, curX, curY);
					newImg.alpha[x + (y * newWidth)] = this.alpha[curX + (curY * this.width)];
				}
			}
		}

		return newImg;
	}
	
	/* @ Returns the image subsection, give a section number (from 1 to 9) */
	public RGBImg getImgSubSection(int sectionNo) {
		int		begX = 0;
		int		begY = 0;
		int		endX = 0;
		int		endY = 0;
		RGBImg	newImg = new RGBImg(this.width/2, this.height/2);
		
		switch(sectionNo) {
		case 1:
			begX = 0;
			begY = 0;
			break;
		case 2:
			begX = width / 2;
			begY = 0;
			break;
		case 3:
			begX = 0;
			begY = height / 2;
			break;
		case 4:
			begX = width / 2;
			begY = height / 2;
			break;
		case 5:
			begX = width / 4;
			begY = 0;
			break;
		case 6:
			begX = width / 4;
			begY = height / 2;
			break;
		case 7:
			begX = 0;
			begY = height / 4;
			break;
		case 8:
			begX = width / 2;
			begY = height / 4;
			break;
		case 9:
			begX = width / 4;
			begY = height / 4;
			break;
		default:
			return null;
		}
		
		endX = begX + width / 2;
		endY = begY + height / 2;
		
		/* Copy the data into the buffer of the new image */
		int newIdx	= 0;
		int curIdx	= 0;
		
		for(int y = begY; y < endY; y++) {
			for(int x = begX; x < endX; x++) {
				/* Find the current index in the image class data */
				curIdx = x + (y * this.width);
				
				/* Copy the data into the new image */
				newImg.red[newIdx] = this.red[curIdx];
				newImg.green[newIdx] = this.green[curIdx];
				newImg.blue[newIdx] = this.blue[curIdx];
				newImg.alpha[newIdx] = this.alpha[curIdx];
				
				/* Point to the next pixel for the new image */
				newIdx++;
			}
		}
		
		return newImg;
	}

	public RGBImg getColorQuantizedImg() {
		int		totCnt	= this.width * this.height;
		RGBImg	newImg	= new RGBImg(this.width, this.height);
		
		for(int i = 0; i < totCnt; i++) {
			newImg.alpha[i] = this.alpha[i];
			newImg.red[i] = Utils.getQuantizedByteVal(this.red[i]);
			newImg.green[i] = Utils.getQuantizedByteVal(this.green[i]);
			newImg.blue[i] = Utils.getQuantizedByteVal(this.blue[i]);
		}
		return newImg;
	}
	
	/* @ Responsible for highlighting a particular section of the image, given a section number 
	 * between 1 and 9 */
	public void highlightImgSubSection(int sectionNo) {
		int		begX = 0;
		int		begY = 0;
		
		switch(sectionNo) {
		case 1:
			begX = 0;
			begY = 0;
			break;
		case 2:
			begX = width / 2;
			begY = 0;
			break;
		case 3:
			begX = 0;
			begY = height / 2;
			break;
		case 4:
			begX = width / 2;
			begY = height / 2;
			break;
		case 5:
			begX = width / 4;
			begY = 0;
			break;
		case 6:
			begX = width / 4;
			begY = height / 2;
			break;
		case 7:
			begX = 0;
			begY = height / 4;
			break;
		case 8:
			begX = width / 2;
			begY = height / 4;
			break;
		case 9:
			begX = width / 4;
			begY = height / 4;
			break;
		default:
			return;
		}
		
		highlightImgPortion(begX, begY, begX + width/2 -1, begY + height/2 - 1);
	}
	
	/*
	 * @ Responsible for encircling (actually adding a rectangle) a portion of the image
	 */
	public void highlightImgPortion(int startX, int startY, int endX, int endY) {

		int firstPos = (width * startY) + startX;
		int secondPos = (width * endY) + startX;
		int pixelCnt = endX - startX;
		int pos = 0;
		
		/* Mark the rows */
		for(int iCnt = 0; iCnt < pixelCnt; iCnt++) {
			/* Pixels of the top row */
			setRGBInverse(firstPos + iCnt);
			/* Pixels of the bottom row */
			setRGBInverse(secondPos + iCnt);
		}
		
		/* mark the columns */
		secondPos = (width * startY) + endX;
		pixelCnt = (endY - startY) * width;
		for(int iCnt = 0; iCnt < pixelCnt; iCnt+= width) {
			/* Pixels of the left column */
			setRGBInverse(firstPos + iCnt);
			/* Pixels of the right column */
			setRGBInverse(secondPos + iCnt);
		}		
	}
	
	/* Private method to set the inverse coloring of the pixel rows */
	private void setRGBInverse(int pos) {
		
		if((red[pos] <= green[pos]) && (red[pos] <= blue[pos])) {
			red[pos] = (byte)0xFF;
			green[pos] = 0;
			blue[pos] = 0;
		}
		else if((green[pos] <= blue[pos]) && (green[pos] <= red[pos])) {
			/* Green pixel value is already proved less than red in previous block */
			red[pos] = 0;
			green[pos] = (byte)0xFF;
			blue[pos] = 0;
		}
		else if ((blue[pos] <= red[pos]) && (blue[pos] <= green[pos])) {
			red[pos] = 0;
			green[pos] = 0;
			blue[pos] = (byte)0xFF;
		}
		else {
			red[pos] = (byte)0xFF;
			green[pos] = (byte)0xFF;
			blue[pos] = (byte)0xFF;
		}
	}
	
	/* Private method to read color component from the input stream */
	private byte apply3X3Filer(byte[] comp, int x, int y) {
		int	avgVal		= 0;
		int	iCnt		= 0;
		int	jCnt		= 0;
		int	iElemCnt	= 0;
		int	newX		= 0;
		int	newY		= 0;
		
		for(iCnt = -1; iCnt < 2; iCnt++) {
			for(jCnt = -1; jCnt < 2; jCnt++) {
				newY = y + iCnt;
				newX = x + jCnt;
				
				if((newX < 0) || (newX >= this.width) || (newY < 0) || (newY >= this.height)) {
					continue;
				}
				
				avgVal += comp[newX + (newY * this.width)] & 0xff;
				iElemCnt++;
			}
		}
		
		avgVal = (avgVal / iElemCnt) & 0xff;
		return (byte) (avgVal);
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
	public byte alpha[];
	
	public static final int SUCCESS = 0;
	public static final int FAILURE = -1403;
}
