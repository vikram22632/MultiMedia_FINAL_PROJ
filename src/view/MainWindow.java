package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * @ this class is responsible for the display of the UI to the user
 */
public class MainWindow extends JFrame {
	
	/*
	 * @ Constructor of the display UI class
	 */
	public MainWindow() {
		super("[CSCI 576 (Spring 14): Final Project] Daniel Coleman and Vikram Datt Rana");
		
		int startXPos = 0;
		int startYPos = 5;
		
		JPanel mainPanel = new JPanel(null);
		width = startXPos + QueryUIPanel.PANEL_WIDTH + MetaDataUIPanel.PANEL_WIDTH;
		
		/* Add the status text label on the window */
		statusText = new JLabel("");
		statusText.setFont(new Font("Serif", Font.BOLD, 14));
		statusText.setHorizontalAlignment(JLabel.CENTER);
		statusText.setVerticalAlignment(JLabel.CENTER);
		statusText.setBounds(startXPos, startYPos, width - 5, 20);
		startYPos += 20;
		
		queryUIPanel = new QueryUIPanel();
		queryUIPanel.setBounds(startXPos, startYPos, QueryUIPanel.PANEL_WIDTH, queryUIPanel.PANEL_HEIGHT);
		startXPos += QueryUIPanel.PANEL_WIDTH;
		
		metaDataUIPanel = new MetaDataUIPanel();
		metaDataUIPanel.setBounds(startXPos, startYPos, MetaDataUIPanel.PANEL_WIDTH, metaDataUIPanel.PANEL_HEIGHT);
		
		height = startYPos + queryUIPanel.PANEL_HEIGHT + 5;
		
		/* Add the UI on the screen */
		mainPanel.add(statusText);
		mainPanel.add(queryUIPanel);
		mainPanel.add(metaDataUIPanel);
		mainPanel.setSize(width, height);
		
		add(mainPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width + 16, height + 32);
		setVisible(true);
		setResizable(false);
		
		fileChooser = new JFileChooser();
	}
	
	/*
	 * @ This function is responsible for opening a file choosing window and allow
	 * the user to choose a particular media file
	 */
	public File getFileChosenByUser() {
		File mediaFile = null;
		
		int rv = fileChooser.showOpenDialog(this);
		if(rv == JFileChooser.APPROVE_OPTION) {
			mediaFile = fileChooser.getSelectedFile();
		}
		
		return mediaFile;
	}
	
	/*
	 * @ This function is responsible for adding the action listener to the controls
	 * which would be generating action events
	 */
	public void addActionListenerToWindow(ActionListener listener) {
		queryUIPanel.addActionListener(listener);
		metaDataUIPanel.addActionListener(listener);
	}
	
	/*
	 * @ Responsible for displaying the query image on the display screen
	 */
	public void displayQueryImg(String name, BufferedImage img) {
		queryUIPanel.displayImage(name, img);
	}
	
	/*
	 * @ Responsible for displaying the meta image on the display screen
	 */
	public void displayMetaImg(String name, BufferedImage img) {
		metaDataUIPanel.displayImage(name, img);
	}
	
	/*
	 * @ Responsible for displaying the meta video's first frame on the display screen
	 */
	public void displayMetaVideoFrame(String name, BufferedImage img, int cnt) {
		metaDataUIPanel.displayVideoBegFrame(name, img, cnt);
	}
	
	/*
	 * @ Responsible for updating the frames of the meta video
	 */
	public void updateMetaVideoFrame(BufferedImage img, int cnt) {
		metaDataUIPanel.updateVideoFrames(img, cnt);
	}
	
	/*
	 * @ Responsible for controlling the video panel display (for displaying PLAY options)
	 */
	public void enablePlay() {
		metaDataUIPanel.enablePlay();
	}
	
	/*
	 * @ Responsible for controlling the video panel display (for displaying PAUSE options)
	 */
	public void enableStop() {
		metaDataUIPanel.enableStop();
	}
	
	/* @ Responsible for showing the program status on the main window */
	public void showProgStatusMsg(String statusMsg) {
		if(statusMsg.isEmpty()) {
			statusText.setText("LOAD IMAGES and PLAY");
		} else {
			statusText.setText(statusMsg);
		}
	}
	
	private JLabel statusText;
	private QueryUIPanel queryUIPanel;
	private MetaDataUIPanel metaDataUIPanel;
	private JFileChooser fileChooser;
	private int width;
	private int height;
	
	public static final int WIN_SUC	= 0;
	public static final int	WIN_ERR = 1;
	public static final int WIN_NOR	= 2;
}
