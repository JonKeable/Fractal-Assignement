import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/*
 * This class handles the storage of saved julia set images
 * The images are only "saved" for the duration of the program's execution
 */
public class JuliaSaves {
	
	//This map stores the images, and their associated names
	private Map<String, Image> imageMap = new HashMap<String, Image>();
	
	public JuliaSaves(){
	}
	
	//adds an image to the map with the specified name as a key
	public void addImage(Image img, String key) {
		//If the name already exists as a key, open a frame asking the user if they wish to overwrite
		if(imageMap.containsKey(key)) {
			new owFrame(img, key);
		}
		else {
			imageMap.put(key, img);
		}
	}
	
	// removes the image with the specified name from the map
	public void removeImage(String key){
		imageMap.remove(key);
	}
	
	// returns the images associated with the specified name
	public Image getImage(String key) {
		return imageMap.get(key);
	}
	
	//returns an array containing all the names of the saved julia images (used to display in a JComboBox)
	public String[] getSaveNames(){
		String[] savesArray = imageMap.keySet().toArray(new String[imageMap.size()]);
		return savesArray;
	}
	
	/*
	 * This frame will be displayed when the user tries to save a julia set with the same name as an existing set
	 * The user will choose whether or not to overwrite the image
	 */
	private class owFrame extends JFrame{
		
		private final String name;
		private final Image image;
		
		public owFrame(Image img, String n) {
			name = n;
			image = img;
			javax.swing.SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					init();
				}
			});	
		}
		
		public void init() {
			//Sets the initial size and position of the frame
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(200, 200, 400, 200 );
			setResizable(false);
			JPanel contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new FlowLayout());
			
			JLabel msgLabel = new JLabel("Julia set with name " + name + " already exists.");
			JLabel ovrLabel = new JLabel("do you wish to overwrite?");
			
			//When clicked the yes button overwrites the previous image and closes the frame
			JButton yesButton = new JButton("Yes");
			yesButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {	
					removeImage(name);
					addImage(image, name);
					closeWindow();
				}
				
			});
			
			//When clicked the image is not overwritten, and the frame is closed
			
			JButton noButton = new JButton("No");
			noButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {	
					closeWindow();
				}
				
			});

			contentPane.add(msgLabel);
			contentPane.add(ovrLabel);
			contentPane.add(yesButton);
			contentPane.add(noButton);
			
			this.setContentPane(contentPane);
			this.setVisible(true);
		}
		
		//disposes of the frame when called
		private void closeWindow(){
			this.dispose();
		}
	}
}
