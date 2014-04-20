package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RGBVid {
	/*
	 * @ Constructor of the class
	 */
	public RGBVid(int width, int height, String name) {
		vidName = name;
		this.width = width;
		this.height = height;
		framesList = new ArrayList<RGBImg>(50);
		curFrameIdx = -1;
	}
	
	/*
	 * @ Responsible for reading and storing the video file from the path
	 * given in the argument
	 */
	public void readVideoFile(String path) {
		try {
			InputStream is = new FileInputStream(new File(path));
			
			while(is.available() > 0) {
				/* Read a new frame from the file */
				RGBImg frame = new RGBImg(width, height);
				frame.readImgFrame(is);
				
				/* Read the image frame to the already existing frame list */
				framesList.add(frame);
			}
			
			/* Close the input stream after reading from the file is complete */
			is.close();
			
		}  catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * @ Responsible for getting the next frame of the video
	 */
	public BufferedImage getNxtVideoFrame() {
		RGBImg nxtFrame = framesList.get(curFrameIdx + 1);
		if(nxtFrame != null) {
			++curFrameIdx;
			return nxtFrame.getdisplayImage();
		}
		else {
			return null;
		}
	}
	
	/*
	 * @ Responsible for getting the prev frame of the video
	 */
	public BufferedImage getPrevVideoFrame() {
		RGBImg nxtFrame = framesList.get(curFrameIdx - 1);
		if(nxtFrame != null) {
			--curFrameIdx;
			return nxtFrame.getdisplayImage();
		}
		else {
			return null;
		}
	}
	
	/* @ Responsible for getting the name of the image */
	public String getVideoName() {
		return this.vidName;
	}
	
	/*
	 * @ Responsible for getting the total frame count of the video file
	 */
	public int getTotalFrameCnt() {
		return framesList.size();
	}
	
	public int getFrameIdx() {
		return curFrameIdx;
	}
	
	/* Class members */
	private int width;
	private int height;
	private int curFrameIdx;
	private String vidName;
	private ArrayList<RGBImg> framesList;
}
