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

class QueryUIPanel extends JPanel{
	/*
	 * @ constructor for the class
	 */
	public QueryUIPanel() {
		
		int startXPos = (PANEL_WIDTH - IMG_WIDTH) / 2;
		int startYPos = 10;
		
		setLayout(null);
		
		/* Instantiate the controls */
		/* Load media button */
		loadMediaBtn = new JButton("Load Query Image");
		loadMediaBtn.setBounds(startXPos, startYPos, IMG_WIDTH + 2, BTN_HEIGHT);
		startYPos += BTN_HEIGHT;
		
		/* media name label */
		startYPos += 5;
		mediaNameLbl = new JLabel("Query Image Name");
		mediaNameLbl.setHorizontalAlignment(JLabel.CENTER);
		mediaNameLbl.setBounds(startXPos, startYPos, IMG_WIDTH + 2, 20);
		startYPos += 20;
		
		/* media display label */
		startYPos += 5;
		mediaDispLbl = new JLabel();
		mediaDispLbl.setBounds(startXPos, startYPos, IMG_WIDTH + 2, IMG_HEIGHT + 2);
		mediaDispLbl.setHorizontalAlignment(JLabel.CENTER);
		mediaDispLbl.setVerticalAlignment(JLabel.CENTER);
		mediaDispLbl.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		startYPos += IMG_HEIGHT + 2;
		
		/* search progress bar */
		startYPos += 10;
		searchProgressBar = new JProgressBar();
		searchProgressBar.setBounds(startXPos, startYPos, IMG_WIDTH + 2, 15);
		startYPos += 15;
		
		/* Search button */
		startYPos += 10;
		searchBtn = new JButton("Start Search");
		searchBtn.setBounds(startXPos, startYPos, IMG_WIDTH + 2, BTN_HEIGHT);
		
		PANEL_HEIGHT = startYPos + BTN_HEIGHT + 10;
		//System.out.println("Query Panel height = " + PANEL_HEIGHT);
		
		/* Add the controls to the panel */
		add(loadMediaBtn);
		add(mediaNameLbl);
		add(mediaDispLbl);
		add(searchProgressBar);
		add(searchBtn);
	}
	
	/*
	 * @ this function is responsible for setting the event handlers 
	 * for the controls present on this panel
	 */
	public void addActionListener(ActionListener listener) {
		/* Add the listener for all the buttons */
		loadMediaBtn.addActionListener(listener);
		searchBtn.addActionListener(listener);
	}
	
	/*
	 * @ This function displays the image on the screen at the given place
	 */
	public void displayImage(String name, BufferedImage img) {
		mediaNameLbl.setText(name);
		mediaDispLbl.setIcon(new ImageIcon(img));
	}	
	
	/*
	 * @ private data members
	 */
	private JLabel mediaNameLbl;
	private JLabel mediaDispLbl;
	private JButton loadMediaBtn;
	private JButton searchBtn;
	private JProgressBar searchProgressBar;
	
	//public int PANEL_WIDTH = 380;
	public int PANEL_HEIGHT;
	
	public static final int PANEL_WIDTH = 380;
	private static final int IMG_WIDTH = 352;
	private static final int IMG_HEIGHT = 288;
	private static final int BTN_HEIGHT = 40;
}
