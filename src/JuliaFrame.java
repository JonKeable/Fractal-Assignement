import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JuliaFrame extends JFrame {

	//The complex point and fractal from the main window
	private final Complex c;
	private final String fractal;
	
	//The fixed number of iterations
	private static final int maxIts = 1000;
	
	//The object which stores the saves of Julia set images
	private JuliaSaves jSaves;
	
	//The panel which will display the generated Julia set
	private JuliaPanel juliaPanel;
	
	//The Image where the Julia Set will be stored
	private BufferedImage myImage;
	
	//The object which will handle generating the  fractals
	private FractalProcessor fp;
	
	//True if the image was loaded from a save (the save button will be disabled)
	private boolean isSaved;
	
	/*
	 * Invoked when generating a new Julia Fractal
	 */
	public JuliaFrame(Complex c, JuliaSaves js, FractalProcessor fp, String fractal){
		
		//These are created by the main class and passed as parameters
		this.c = c;
		this.fp = fp;
		this.fractal = fractal;
		this.setTitle("Julia for " + c);
		
		jSaves = js;
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				init();
				juliaPanel.genFractal();
			}
		});
	}
	
	/*
	 * Invoked when loading a saved Julia Fractal
	 */
	public JuliaFrame(Image img, JuliaSaves js){
		
		//Will not be used as we already have the image
		fractal = "";
		c = Complex.ZERO;
		//Passed by the save loader
		jSaves = js;
		isSaved = true;
		this.setTitle("Favourite Julia");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				init();
				juliaPanel.setImage(img);
			}
		});
	}

	private void init() {
		//Sets the frame's size and position
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(10, 10, 1024, 720 );
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new FlowLayout());
		
		//The panel where the Julia Fractal will be displayed
		juliaPanel = new JuliaPanel();
		contentPane.add(juliaPanel);
		
		//Allows the user to save the image as a faavourite for the duration of the program's execution
		JButton saveButton = new JButton("Make Favourite");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveFrame();
			}
		});
		
		if(isSaved) {
			saveButton.setEnabled(false);
		}
		
		contentPane.add(saveButton);

		
		setContentPane(contentPane);
		this.setVisible(true);
		
	}
	
	/*
	 * Displayed when the make favourite button is pressed
	 * Allows the user to save an image as a favourite
	 */
	private class SaveFrame extends JFrame {
		
		public SaveFrame() {
			javax.swing.SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					init();
				}
			});
		}
		
		public void init() {
			//Sets the size and position of the frame
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(200, 200, 400, 200 );
			setResizable(false);
			JPanel contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new FlowLayout());
			
			//Allows the User to manually name the favourite ...
			JLabel msgLabel = new JLabel("Enter a name for the favourite image you wish to save");
			JTextField saveNameField = new JTextField(10);
			//... or name it based on the complex point c it is based on, as well as the type of fractal
			JButton nameAsCButton = new JButton("use complex point & fractal type as name");
			nameAsCButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					saveNameField.setText(c.toString() + "   " + fractal);
				}
			});
			
			//Tries to save the favourite when pressed, then closes the window
			JButton nameButton = new JButton("Save Favourite");
			nameButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					String name = saveNameField.getText();
					if(!name.equals("")){
						jSaves.addImage(myImage, name);
						closeWindow();
					}

				}
				
			});

			contentPane.add(msgLabel);
			contentPane.add(saveNameField);
			contentPane.add(nameAsCButton);
			contentPane.add(nameButton);
			
			this.setContentPane(contentPane);
			this.setVisible(true);
		}
		
		private void closeWindow(){
			this.dispose();
		}
	}
	
	/*
	 * This is where the Julia Set will be displayed
	 */
	private class JuliaPanel extends JPanel{
		
		//The image will always default to same size and portion of the complex plane
		private static final int width = 750, height = 600;
		private static final double rMin = -2, rMax = 2, iMin = -1.6, iMax = 1.6;
		
		public JuliaPanel(){
	    	
	        super();
	        this.setPreferredSize(new Dimension(width,height));

		}
		
		//Sets the image to the one specified, used when loading a saved favourite
		public void setImage(Image img){
			myImage = (BufferedImage) img;
			repaint();
		}
		
		/*
		 * Calls the fractal generation method from the fractal processor object
		 */
		private void genFractal() {
	        myImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
	        
	        //Calculates how much of the complex plane each pixel represents
			double rWidth = rMax - rMin;
			double zHeight = iMax - iMin;
			double incrX, incrY;
			incrX = (rWidth)/width;
			incrY = (zHeight)/height;
		
			//Generates the image
			fp.genJulia(myImage, width, height, incrX, incrY, rMin, iMax, maxIts, c, fractal);
			
			repaint();
		}

	    @Override
	    public void paint(Graphics g){
	    	super.paint(g);
	        g.drawImage(myImage, 0, 0, width, height, null);
	    }
	}
	
	
	
}
