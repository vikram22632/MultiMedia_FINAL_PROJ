package view;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class MetaDataUIPanel extends JPanel{
	/*
	 * @ constructor for the class
	 */
	public MetaDataUIPanel() {

		int startXPos = (PANEL_WIDTH - IMG_WIDTH) / 2;
		int startYPos = 10;

		setLayout(null);

		/*
		 * Instantiate the controls
		 */

		/* Load media button */
		loadMediaBtn = new JButton("Load Meta Image/Video");
		loadMediaBtn.setBounds(startXPos, startYPos, IMG_WIDTH + 2, BTN_HEIGHT);
		startYPos += BTN_HEIGHT;

		/* media name label */
		startYPos += 5;
		mediaNameLbl = new JLabel("Meta Image/Video Name");
		mediaNameLbl.setHorizontalAlignment(JLabel.CENTER);
		mediaNameLbl.setBounds(startXPos, startYPos, IMG_WIDTH + 2, 20);
		startYPos += 20;

		/* media display label */
		startYPos += 5;
		mediaDispLbl = new JLabel();
		mediaDispLbl.setBounds(startXPos, startYPos, IMG_WIDTH + 2, IMG_HEIGHT + 2);
		mediaDispLbl.setHorizontalAlignment(JLabel.CENTER);
		mediaDispLbl.setVerticalAlignment(JLabel.CENTER);
		mediaDispLbl.setBorder(BorderFactory.createLineBorder(Color.RED));
		startYPos += IMG_HEIGHT + 2;

		/* Video Control Panel */
		startYPos += 10;
		PANEL_HEIGHT = startYPos;
		vidCtrlPanel = createVidCtrlPanel(startXPos, startYPos);

		/* Hide these controls for now */
		playMediaBtn.setVisible(false);
		prevFrameBtn.setVisible(false);
		nextFrameBtn.setVisible(false);
		videoProgressBar.setVisible(false);

		/* Add the controls to the panel */
		add(loadMediaBtn);
		add(mediaNameLbl);
		add(mediaDispLbl);
		add(vidCtrlPanel);
	}

	/*
	 * @ This function is responsible for setting the event handlers
	 * for the controls present on this panel
	 */
	public void addActionListener(ActionListener listener) {
		/* Add the listener for all the buttons */
		loadMediaBtn.addActionListener(listener);
		playMediaBtn.addActionListener(listener);
		prevFrameBtn.addActionListener(listener);
		nextFrameBtn.addActionListener(listener);
	}

	/*
	 * @ This function displays the image on the screen at the given place
	 */
	public void displayImage(String name, BufferedImage image) {
		mediaNameLbl.setText(name);
		mediaDispLbl.setIcon(new ImageIcon(image));

		/* Hide the play/pause, fwd, rew buttons and the progress bar */
		playMediaBtn.setVisible(false);
		prevFrameBtn.setVisible(false);
		nextFrameBtn.setVisible(false);

		videoProgressBar.setValue(0);
		videoProgressBar.setMaximum(0);
		videoProgressBar.setVisible(false);
	}

	public void displayVideoBegFrame(String name, BufferedImage frame, int totCnt) {
		mediaNameLbl.setText(name);
		mediaDispLbl.setIcon(new ImageIcon(frame));

		/* Hide the play/pause, fwd, rew buttons and the progress bar */
		playMediaBtn.setVisible(true);
		prevFrameBtn.setVisible(true);
		prevFrameBtn.setEnabled(false);
		nextFrameBtn.setVisible(true);
		videoProgressBar.setVisible(true);

		videoProgressBar.setValue(0);
		videoProgressBar.setMaximum(totCnt - 1);
	}

	public void updateVideoFrames(BufferedImage frame, int curFrameCnt) {
		mediaDispLbl.setIcon(new ImageIcon(frame));
		videoProgressBar.setValue(curFrameCnt);

		if(curFrameCnt >= videoProgressBar.getMaximum()) {
			/* Disable the next frame button if the last frame has been displayed */
			nextFrameBtn.setEnabled(false);
		}
		else if(curFrameCnt <= videoProgressBar.getMinimum()) {
			/* Disable the next frame button if the last frame has been displayed */
			prevFrameBtn.setEnabled(false);
		}
		else {
			/* Enable the prev frame button */
			if(!prevFrameBtn.isEnabled()) {
				prevFrameBtn.setEnabled(true);
			}

			/* Enable the next frame button */
			if(!nextFrameBtn.isEnabled()) {
				nextFrameBtn.setEnabled(true);
			}
		}
	}

	public void enableStop() {
		playMediaBtn.setText("STOP");
		prevFrameBtn.setEnabled(false);
		nextFrameBtn.setEnabled(false);
	}
	
	public void enablePlay() {
		playMediaBtn.setText("PLAY");
		prevFrameBtn.setEnabled(true);
		nextFrameBtn.setEnabled(true);
	}
	
	/*
	 * @ This function is responsible for creating the video control panel
	 * on the screen. The video control panel consists of PLAY/PAUSE button,
	 * FWD, REW buttons and video play status bar
	 */
	private JPanel createVidCtrlPanel(int xPos, int yPos) {
		int startXPos = 0;
		int startYPos = 0;
		int width = IMG_WIDTH + 2;
		int btnWidth = 80;
		int gap = (width - 3 * btnWidth) / 4;
		JPanel vidCtrlPanel = new JPanel(null);

		/* Add the video status bar */
		videoProgressBar = new JProgressBar();
		videoProgressBar.setBounds(startXPos, startYPos, width, 15);
		videoProgressBar.setMinimum(0);
		videoProgressBar.setMaximum(0);
		startYPos += 15;

		/* Update the vertical position for the buttons */
		startYPos += 10;

		/* Add  REW button */
		startXPos += gap;
		prevFrameBtn =  new JButton("<<");
		prevFrameBtn.setBounds(startXPos, startYPos, btnWidth, BTN_HEIGHT);
		startXPos += btnWidth;

		/* Add the PLAY button */
		startXPos += gap;
		playMediaBtn = new JButton("PLAY");
		playMediaBtn.setBounds(startXPos, startYPos, btnWidth, BTN_HEIGHT);
		startXPos += btnWidth;

		/* Add the FWD button */
		startXPos += gap;
		nextFrameBtn =  new JButton(">>");
		nextFrameBtn.setBounds(startXPos, startYPos, btnWidth, BTN_HEIGHT);

		startYPos += BTN_HEIGHT;

		vidCtrlPanel.add(videoProgressBar);
		vidCtrlPanel.add(prevFrameBtn);
		vidCtrlPanel.add(playMediaBtn);
		vidCtrlPanel.add(nextFrameBtn);

		/* Set the bounds for this panel */
		startYPos += 10;
		vidCtrlPanel.setBounds(xPos, yPos, width, startYPos);
		PANEL_HEIGHT += startYPos;

		return vidCtrlPanel;
	}

	/*
	 * Private data members
	 */
	private JLabel mediaNameLbl;
	private JLabel mediaDispLbl;
	private JButton loadMediaBtn;
	private JButton playMediaBtn;
	private JButton prevFrameBtn;
	private JButton nextFrameBtn;
	private JProgressBar videoProgressBar;
	private JPanel vidCtrlPanel;


	public int PANEL_HEIGHT = 400;

	public static final int PANEL_WIDTH = 380;

	private static final int IMG_WIDTH = 352;
	private static final int IMG_HEIGHT = 288;
	private static final int BTN_HEIGHT = 40;
}
