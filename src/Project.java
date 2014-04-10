import javax.swing.UIManager;

import controller.LogicController;
import view.MainWindow;

/*
 * @ Project class is just the beginning of the project, it handles the task of initialising
 * the other relevant classes and get the work going.
 */
public class Project {
	/*
	 * @ Main: Starting point of the project
	 */
	public static void main(String[] args) {
		try {
			  UIManager.setLookAndFeel(
			    UIManager.getSystemLookAndFeelClassName());
			} 
		catch (Exception e) {
			System.out.println("Unable to set the system look and feel");
		}
		
		/* Create an instance of the display and controller to start the work */
		new LogicController(new MainWindow());
	}

}
