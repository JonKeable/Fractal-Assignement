import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/*
 * This frame is displayed when the user wishes to load a favourited Julia Image
 */
public class LoadJuliaFrame extends JFrame{
	
	//This is passed to the frame when it is opened, and contains the saved Julia Images
	private JuliaSaves jSaves;
	
	public LoadJuliaFrame(JuliaSaves js) {
		jSaves = js;
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				init();
			}
		});
	}
	
	private void init() {
		//Sets the size and position of the frame
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 400, 200 );
		setResizable(false);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new FlowLayout());
		
		//This box and button allows the user to select an image based on its savename and load it
		JComboBox<String> savesBox = new JComboBox<String>(jSaves.getSaveNames());
		JButton loadButton = new JButton("Load Julia Image");
		loadButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new JuliaFrame(jSaves.getImage(savesBox.getSelectedItem().toString()),jSaves);
				closeWindow();
			}
			
		});
		
		contentPane.add(savesBox);
		contentPane.add(loadButton);
		this.setContentPane(contentPane);
		this.setVisible(true);	
	}
	
	private void closeWindow() {
		this.dispose();
	}
}
