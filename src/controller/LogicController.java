package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import model.ColorHistogram;
import model.RGBImg;
import model.RGBVid;
import view.MainWindow;

public class LogicController implements ActionListener, Runnable {
	
	/*
	 * Constructor of the class
	 */
	public LogicController(MainWindow window) {
		/* Add this class as the action listener for the window */
		window.addActionListenerToWindow(this);
		this.display = window;
		display.showProgStatusMsg("");
		
		chldThd = new Thread(this, "parallel thread");
		chldThd.start();
	}

	/*
	 * @ This function will be used for updating the GUI and doing the main process,
	 * since the main thread will be busy handling the events of the GUI
	 */
	public void run() {
		int	cmdVal = 0;
		int rv = 0;
		
		while(true) {
			cmdVal = command(0);
			if(cmdVal == PLAY) {
				playMetaVideo();
			} 
			else if(cmdVal == SEARCH) {
				if(queryImg == null) {
					display.showProgStatusMsg("Load query image first!!");
				} 
				else {
					if(metaImg != null) {
						/* Search the log in the image */
						
						if(0 == (rv =searchImageInImage(queryImg, metaImg))) {
							display.showProgStatusMsg("Can't find the logo");
							
						}
						else {
							if(rv == 1) {
								display.showProgStatusMsg("Logo found !!!");
							} else if(rv == 2) {
								display.showProgStatusMsg("Logo found - THE WHOLE IMAGE!!!");
							}
							
							/* Display the updated meta image*/
							display.displayMetaImg(metaImg.getImageName(), metaImg.getdisplayImage());
						}
					} 
					else if(metaVideo != null) {
						display.showProgStatusMsg("Search in Meta Video not implemented");
					} 
					else {
						/* Show the error message on the screen */
						display.showProgStatusMsg("Load a meta image also!!");
					}
				}
			}
		}
	}

	/*
	 * @ This function will be called upon receiving any button event from the GUI
	 */
	public void actionPerformed(ActionEvent event) {
		/* These actions are generated by the buttons present on the GUI window */
		if("Load Meta Image/Video".equals(event.getActionCommand())) {

			File file = display.getFileChosenByUser();
			if(file != null) {
				if(file.getName().contains(".rgb")) {
					/* Load the meta image */
					metaImg = new RGBImg(MEDIA_WIDTH, MEDIA_HEIGHT, file.getName());
					metaImg.readStandaloneImg(file.getAbsolutePath());
					
					/* Display the image on the display screen. Video controls should not show on
					 * the screen in this case */
					display.displayMetaImg(metaImg.getImageName(), metaImg.getdisplayImage());
					display.showProgStatusMsg("");
					metaVideo = null;
				}
				else {
					/* Load the meta video */
					metaVideo = new RGBVid(MEDIA_WIDTH, MEDIA_HEIGHT, file.getName());
					metaVideo.readVideoFile(file.getAbsolutePath());
					
					/* Display the video on the display screen. This would mean just showing the first
					 * frame of the video on the screen and enabling the video control buttons */
					display.displayMetaVideoFrame(metaVideo.getVideoName(), metaVideo.getBegVideoFrame(),
																			metaVideo.getTotalFrameCnt());
					display.showProgStatusMsg("");
					metaImg = null;
				}
			}
		}
		else if("Load Query Image".equals(event.getActionCommand())) {
			/* Load the query image on the left side of the window */
			File file = display.getFileChosenByUser();
			if(file != null) {
				queryImg = new RGBImg(MEDIA_WIDTH, MEDIA_HEIGHT, file.getName());
				queryImg.readStandaloneImg(file.getAbsolutePath());
				
				display.displayQueryImg(queryImg.getImageName(), queryImg.getdisplayImage());
				display.showProgStatusMsg("");
			}
		}
		else if("Start Search".equals(event.getActionCommand())) {
			/* Give command to the child thread for searching the query image in the meta image/video */
			command(SEARCH);
		}
		else if("<<".equals(event.getActionCommand())) {
			display.updateMetaVideoFrame(metaVideo.getPrevVideoFrame(), metaVideo.getFrameIdx());
		}
		else if("PLAY".equals(event.getActionCommand())) {
			display.enableStop();
			
			/* Give the command to the child thread for playing the video */
			command(PLAY);
		}
		else if("STOP".equals(event.getActionCommand())) {
			display.enablePlay();
			display.displayMetaVideoFrame(metaVideo.getVideoName(), metaVideo.getBegVideoFrame(),
					metaVideo.getTotalFrameCnt());
		}
		else if(">>".equals(event.getActionCommand())) {
			display.updateMetaVideoFrame(metaVideo.getNxtVideoFrame(), metaVideo.getFrameIdx());
		}
	}
	
	/* 
	 * @ This function is responsible for searching the query image in a meta image
	 */
	private int searchImageInImage(RGBImg queryImg, RGBImg metaImg) {
		int				rv			= 0;
		int				section		= 0;
		int				order[]		= new int[9];
		double			minVal		= 0;
		double			dist[]		= new double[9];
		ColorHistogram	queryHist	= new ColorHistogram();
		ColorHistogram	metaHist	= new ColorHistogram();
		
		/* 
		 * ---------------------------------------------------------------------
		 * LEVEL 1: Check for the whole image first
		 * ---------------------------------------------------------------------
		 */
		queryHist.calculateStats(queryImg);
		metaHist.calculateStats(metaImg);
		if(metaHist.doChiSquareComp(queryHist) <= 50) {
			System.out.println("The whole image matched");
			return 2;
		}
		
		/* Resetting the histograms */
		queryHist.resetStats();
		queryHist.calculateStats(queryImg.getScaledImg(0.25F, true));
		
		/* 
		 * ---------------------------------------------------------------------
		 * LEVEL 2: Check for the 9 quadrants of the image 
		 * ---------------------------------------------------------------------
		 */
		for(int i = 0; i < 9; i++) {
			metaHist.resetStats();
			metaHist.calculateStats(metaImg.getImgSubSection(i+1));
			/* 
			 * Calculate the distance value 
			 * Bhattachraya distance computation is not giving good results at all,
			 * use CHI-SQUARE comparison function for now.
			 */
			dist[i] = metaHist.doChiSquareComp(queryHist);
			order[i] = i+1;
		}
		
		/* Sort the values along with quadrant indices in increasing order. This is done to consider the
		 * quadrant with minimum distance values first, as that quadrant would have better chances of
		 * finding a match for the logo */
		for(int i = 0; i < dist.length; i++) {
			for(int j = i + 1; j < dist.length; j++) {
				if(dist[j] < dist[i]) {
					/* Swap the entries */
					double dTmp = dist[j];
					dist[j] = dist[i];
					dist[i] = dTmp;
					/* Swap the corresponding quadrant order */
					int iTmp = order[j];
					order[j] = order[i];
					order[i] = iTmp;
				}
			}
		}
		
		/* For debugging */
		System.out.println("LEVEL 2 VALUES");
		System.out.println("-------------------------------------------------");
		for(int i = 0; i < dist.length; i++) {
			System.out.println("quadrant["+order[i]+"]'s value="+dist[i]);
		}
		System.out.println("-------------------------------------------------");
		
		/* The quadrant with the least distance values will be the first in the array. Just check if 
		 * it meets the valid fault tolerance or LEVEL 3 search is required */
		if(dist[0] < 1000) {
			/* Found the quadrant */
			System.out.println("Found a LVL 2 match in quadrant=["+order[0]+"]");
			metaImg.highlightImgSubSection(order[0]);
			return 1;
		}
		
		/* LEVEL 3: Further search in 9 sub-blocks of those 9 quadrants */
		queryHist.resetStats();
		queryHist.calculateStats(queryImg.getScaledImg(0.125F, true));
		
		for(int i = 0; i < 9; i++) {
			RGBImg	quadrant = metaImg.getImgSubSection(order[i]);
			double	subDist	= 0;
			double	minDist = 0;
			
			for(int j=1; j <= 9; j++) {
				metaHist.resetStats();
				metaHist.calculateStats(quadrant.getImgSubSection(j));
				
				subDist = metaHist.doChiSquareComp(queryHist);
				if(j == 1) {
					minDist = subDist;
				}
				else if(minVal > subDist){
					minDist = subDist;
				}
			}
			/* Update the distance vector with the least distanc value found in the
			 * 9 sub-quadrants of the quadrant of the main image */
			dist[i] = minDist;
		}
		
		/* Sort the distances vector again */
		for(int i = 0; i < dist.length; i++) {
			for(int j = i + 1; j < dist.length; j++) {
				if(dist[j] < dist[i]) {
					/* Swap the entries */
					double dTmp = dist[j];
					dist[j] = dist[i];
					dist[i] = dTmp;
					/* Swap the corresponding quadrant order */
					int iTmp = order[j];
					order[j] = order[i];
					order[i] = iTmp;
				}
			}
		}
		
		/* For debugging */
		System.out.println("LEVEL 3 VALUES");
		System.out.println("-------------------------------------------------");
		for(int i = 0; i < dist.length; i++) {
			System.out.println("quadrant["+order[i]+"]'s value="+dist[i]);
		}
		System.out.println("-------------------------------------------------");
		
		/* The quadrant with the least distance values will be the first in the array. Just check if 
		 * it meets the valid fault tolerance or LEVEL 3 search is required */
		if(dist[0] < 1000) {
			/* Found the quadrant */
			System.out.println("Found a LVL 3 match in quadrant=["+order[0]+"]");
			metaImg.highlightImgSubSection(order[0]);
			return 1;
		}
		
		return 0;
	}
	
	/* 
	 * This function is supposed to act as a synchronized communication medium between the main
	 * thread and the child thread
	 */
	private synchronized int command(int cmdVal) {
		int	rv	= 0;
		
		if(chldThd == Thread.currentThread()) {
			/* Child thread's running context */
			while(iCmd == 0) {
				try {
					wait();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			rv = iCmd;
			iCmd = 0;
		}
		else {
			/* Main thread's running context */
			iCmd = cmdVal;
			notify();
		}
		return rv;
	}
	
	private void playMetaVideo() {
		int frameCnt = metaVideo.getTotalFrameCnt();
		int	curIdx = metaVideo.getFrameIdx();
		
		/* Show the frames on the screen */
		while(curIdx < (frameCnt - 1)) {
			display.updateMetaVideoFrame(metaVideo.getNxtVideoFrame(), metaVideo.getFrameIdx());
			/* Sleep before displaying the next screen (Adding latency to make the video frames
			 * look like animation) */
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			curIdx = metaVideo.getFrameIdx();
		}
	}
	
	/* members of the class */
	private RGBImg		queryImg;
	private RGBImg		metaImg;
	private RGBVid		metaVideo;
	private MainWindow	display;
	private Thread		chldThd;
	private int			iCmd;
	
	private static final int MEDIA_WIDTH = 352;
	private static final int MEDIA_HEIGHT = 288;
	
	private static final int PLAY	= 1;
	private static final int SEARCH	= 2;
}
