import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MainFractalFrame extends JFrame {

	//This is the defualt number of iterations of the fractal algorithms
	private static final int defaultIts = 200;
	
	//Will become the content pane of the window
	private JPanel contentPane;
	
	//The central panel which will contain the generated fractal
	private FractalPanel imgPanel;
	
	//Will display settings for some fractal types
	private JPanel settingsPanel;
	
	//Texts fields for user input
	private JTextField rMinField, rMaxField, iMinField, iMaxField, itField, uPointField;
	
	//A point that can be selected by the user
	private Complex uPoint = Complex.ZERO;
	
	//true only if the user has currently selected a point
	private boolean isPoint;
	
	//The object which stores the saves of julia set images
	private JuliaSaves jSaves;
	
	//The object which will handle generating the  fractals
	private FractalProcessor fp;
	
	//Contains the different options for fractal types and colouring methods
	private JComboBox<String> fractalCombo;
	private String[] fractalArray = {"Mandelbrot","Burning Ship","Orbit-Trap Mandelbrot"};
	
	private JButton juliaButton;

	/*
	 * Create the frame.
	 */
	public MainFractalFrame(JuliaSaves js, FractalProcessor fp) {
		
		//These are created by the main class and passed as parameters
		jSaves = js;
		this.fp = fp;
		
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				init();
			}
		});
	}
	
	/*
	 * Adds the various components to the  frame
	 */
	private void init() {
		
		/*
		 * This part of the code defines the size and position of the frmae
		 */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 1024, 720 );
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		this.setTitle("Fractal Explorer");
		
		/*
		 * The north part of the layout contains the text fields describing the bounds of the displayed complex plane,
		 *Which can be modified by the user
		 */
		JPanel northPanel = new JPanel();
		
		/*
		 * This panel will be in the left of the north panel, and allows the user to manipulate the area of the complex plane shown
		 */
		JPanel axisBoxPanel =  new JPanel();
		axisBoxPanel.setLayout(new GridLayout(2,4));
		rMinField = new JTextField("-2");
		rMaxField = new JTextField("2");
		iMinField = new JTextField("-1.6");
		iMaxField = new JTextField("1.6");
		axisBoxPanel.add(rMaxField);
		axisBoxPanel.add(new JLabel(" Max real"));
		axisBoxPanel.add(iMaxField);
		axisBoxPanel.add(new JLabel(" Max imaginary"));
		axisBoxPanel.add(rMinField);
		axisBoxPanel.add(new JLabel(" Min real"));
		axisBoxPanel.add(iMinField);
		axisBoxPanel.add(new JLabel(" Min imaginary"));
		
		//This button will reset the complex axis to allow the user to zoom back and see the whole fractal again for instance
		JButton resetButton = new JButton("Reset Axis");
		resetButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rMinField.setText("-2");
				rMaxField.setText("2");
				String fractID = getFractCode(fractalCombo.getSelectedItem().toString());
				
				//Different fractals require different parts of the complex plane to display fully
				switch (fractID){
				case "Mand": 
				case "MandOrb":		iMinField.setText("-1.6");
									iMaxField.setText("1.6");
									break;
				case "Burn":		iMinField.setText("-1");
									iMaxField.setText("2");
									break;
				default : System.err.println("invalid fractal name");
				}
			}
			
		});
		
		northPanel.add(axisBoxPanel);
		northPanel.add(resetButton);
		
		/*
		 * The south panel contains the text field for changing the iteration number and a JCombo box allowing the user
		 * to switch between fractal types and colouring methods. The south panel also contains a text field displaying
		 * the complex point selected by the user, a button for generating the related Julia Set, and a button allowing
		 * the user to load saved favourite Julia Fractals.
		 */
		JPanel southPanel = new JPanel();
		GridLayout gl = new GridLayout(1,6);
		gl.setHgap(10);
		southPanel.setLayout(gl);
		
		genButtonListener genBL = new genButtonListener();
		itField = new JTextField(Integer.toString(defaultIts));
		itField.addActionListener(genBL);
		JButton genButton = new JButton("Generate Fractal");
		genButton.addActionListener(genBL);

		
		fractalCombo = new JComboBox<String>(fractalArray);
		fractalCombo.setEditable(false);
		fractalCombo.addActionListener(new ComboListener(fractalCombo));
		
		JLabel uPointLabel = new JLabel("User Selected Point");
		uPointField = new  JTextField("");
		uPointField.setEditable(false);
		juliaButton = new JButton("generate julia set");
		juliaButton.setEnabled(false);
		juliaButton.addActionListener(new JuliaButtonListener());
		
		JButton loadJuliaButton = new JButton("Load Favourite Julia Fractal");
		loadJuliaButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new LoadJuliaFrame(jSaves);
			}
			
		});
		
		southPanel.add(new JLabel("no. of Iterations"));
		southPanel.add(itField);
		southPanel.add(fractalCombo);
		southPanel.add(genButton);
		southPanel.add(uPointLabel);
		southPanel.add(uPointField);
		southPanel.add(juliaButton);
		southPanel.add(loadJuliaButton);

		
		//This is the central panel where the fractal image will appear
		imgPanel = new FractalPanel();
		
		//This will show different settings or none at all depending on the fractal generated
		settingsPanel = new OrbitPanel();
		settingsPanel.setVisible(false);
		
		contentPane.add(imgPanel, BorderLayout.CENTER);
		contentPane.add(northPanel, BorderLayout.NORTH);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		contentPane.add(settingsPanel, BorderLayout.EAST);
		
		setContentPane(contentPane);
		this.setVisible(true);
		
		
	}
	
	/*
	 * Displays the settings panel for orbits if required
	 */
	private class ComboListener implements ActionListener {

		private JComboBox<String> combo;
		
		public ComboListener(JComboBox<String> combo) {
			this.combo = combo;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(getFractCode(combo.getSelectedItem().toString()).equals("MandOrb")) {

				settingsPanel.setVisible(true);
			}
			else{
				settingsPanel.setVisible(false);
			}
		}
		
	}

	/*
	 * When the generate fractal button is pressed, this calls the drawFractal method
	 */
	private class genButtonListener implements ActionListener {

		double rMin, rMax, iMin, iMax;
		int its;
		String fractalName;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
						//this checks that all the user inputs are valid before creating the fractal
						try{
							rMin = Double.parseDouble(rMinField.getText());
							rMax = Double.parseDouble(rMaxField.getText());
							iMin = Double.parseDouble(iMinField.getText());
							iMax = Double.parseDouble(iMaxField.getText());
							its = Integer.parseInt(itField.getText());
							fractalName = fractalCombo.getSelectedItem().toString();
							
							if(rMin >= rMax || iMin >= iMax) {
								JOptionPane.showMessageDialog(null, "The maximum values of the real and imaginary plane must be larger than the minimun values");
							}
							else{
								//Draws the fractal on the image
								new Thread( new FractalThread(rMin, rMax, iMin, iMax, its, fractalName)).start();
							}
						}
						catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, "One of the specified values for complex plane or interation number was not valid");
						}
		}
		
		private	class FractalThread implements Runnable {
			
			double reMin, reMax, imMin, imMax;
			int iterations;
			String fractName;
			
			private FractalThread(double rMin, double rMax, double iMin, double iMax, int its, String fractalName) {
				reMin = rMin;
				reMax = rMax;
				imMin = iMin;
				imMax = iMax;
				iterations = its;
				fractName = fractalName;
			}

			@Override
			public void run() {
				imgPanel.drawFractal(reMin, reMax, imMin, imMax, iterations, fractName);
			}
			
		}
	}
	
	/*
	 * Returns an abbreviated code based on the strings in the combo box which denote the different fractals
	 * that can be generated by the program
	 */
	private String getFractCode(String s) {
		switch(s) {
		case "Mandelbrot": 				return "Mand";
		case "Burning Ship": 			return "Burn";
		case "Orbit-Trap Mandelbrot":	return "MandOrb";
		default: 						return "Err";
		}
	}
	
	/*
	 * This will create a new JFrame with the julia image in when a button is pressed
	 */
	private class JuliaButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(isPoint){
				new JuliaFrame(uPoint, jSaves, fp, getFractCode(fractalCombo.getSelectedItem().toString()));
			}
		}
	}
	
	
	/*
	 * Displays settings for the user to change when generating orbit traps
	 */
	private class OrbitPanel extends JPanel{
		
		private String orbitShape = "Point";
		
		//true if the user wants to use the selected point rather than the origin to centre the orbits on
		private boolean useUpoint = false;

		//Stores the axis which the flat line will intercept (e.g. vertical line intercepts the x axis)
		private char axis = 'x';

		
		private JTextField interceptField;
		private JTextField radField;
		
		public OrbitPanel(){
			this.setLayout(new GridLayout(5,1));
			
			/*
			 * The radioPanel allows the user to select which orbit type to use
			 * The other panels provide settings for each orbit type
			 */
			JPanel radioPanel = new JPanel();
			JPanel pointPanel = new JPanel();
			JPanel linePanel = new JPanel();
			JPanel crossPanel = new JPanel();
			JPanel circPanel = new JPanel();

			//radioPanel
			JRadioButton pointRadio = new JRadioButton("point trap");
			JRadioButton lineRadio = new JRadioButton("line trap");
			JRadioButton crossRadio = new JRadioButton("cross trap");
			JRadioButton circleRadio = new JRadioButton("circle orbit trap");
			ButtonGroup orbitTypeGroup = new ButtonGroup();
			orbitTypeGroup.add(pointRadio);
			orbitTypeGroup.add(lineRadio);
			orbitTypeGroup.add(crossRadio);
			orbitTypeGroup.add(circleRadio);
			radioPanel.add(pointRadio);
			radioPanel.add(lineRadio);
			radioPanel.add(crossRadio);
			radioPanel.add(circleRadio);
			
			this.add(radioPanel);
			
			//If a button with this listener is selected then the origin will be used as centre
			ActionListener origListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					useUpoint = false;
				}
				
			};
			
			//If a button with this listener is selected then the user selected point will be used as centre
			ActionListener upointListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					useUpoint = true;
				}
				
			};
			
			//pointPanel
			JRadioButton origRadio = new JRadioButton("use \"0,0\" as orbit point");
			origRadio.addActionListener(origListener);
			JRadioButton upointRadio = new JRadioButton("use uPoint as orbit point");
			upointRadio.addActionListener(upointListener);
			ButtonGroup pointGroup = new ButtonGroup();
			pointGroup.add(origRadio);
			pointGroup.add(upointRadio);
			pointPanel.add(origRadio);
			pointPanel.add(upointRadio);
			
			this.add(pointPanel);
			
			//linePanel
			linePanel.setLayout(new GridLayout(2,1));
			
			JPanel axisPanel = new JPanel();
			JRadioButton xRadio = new JRadioButton("use line in x axis (real)");
			xRadio.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					axis = 'x';
				}
				
			});
			JRadioButton yRadio = new JRadioButton("use line in y axis (imag)");
			yRadio.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					axis = 'y';
				}
				
			});
			ButtonGroup axisGroup = new ButtonGroup();
			axisGroup.add(xRadio);
			axisGroup.add(yRadio);
			axisPanel.add(xRadio);
			axisPanel.add(yRadio);
			
			JPanel intPanel = new JPanel();
			interceptField = new JTextField(5);
			interceptField.setText("0");
			intPanel.add(interceptField);
			intPanel.add(new JLabel("set real/imaginary intercept"));
			
			linePanel.add(axisPanel);
			linePanel.add(intPanel);
			
			this.add(linePanel);
			
			//crossPanel
			JRadioButton crossOrigRadio = new JRadioButton("use \"0,0\" as cross centre");
			crossOrigRadio.addActionListener(origListener);
			JRadioButton crossUpointRadio = new JRadioButton("use uPoint as cross centre");
			crossUpointRadio.addActionListener(upointListener);
			ButtonGroup crossGroup = new ButtonGroup();
			crossGroup.add(crossOrigRadio);
			crossGroup.add(crossUpointRadio);
			crossPanel.add(crossOrigRadio);
			crossPanel.add(crossUpointRadio);
			
			this.add(crossPanel);
			
			//circle panel
			circPanel.setLayout(new GridLayout(2,1));
			
			JPanel circPointPanel = new JPanel();
			JRadioButton circOrigRadio = new JRadioButton("use \"0,0\" as circle centre");
			circOrigRadio.addActionListener(origListener);
			JRadioButton circUpointRadio = new JRadioButton("use uPoint as circle centre");
			circUpointRadio.addActionListener(upointListener);
			ButtonGroup circGroup = new ButtonGroup();
			circGroup.add(circOrigRadio);
			circGroup.add(circUpointRadio);
			circPanel.add(circOrigRadio);
			circPanel.add(circUpointRadio);
			
			JPanel radPanel = new JPanel();
			radField = new JTextField(5);
			radField.setText("1");
			radPanel.add(radField);
			radPanel.add(new JLabel("set radius of circle"));
			
			circPanel.add(circPointPanel);
			circPanel.add(radPanel);
			
			this.add(circPanel);
			
			
			/*
			 * For each radio button the listener will disable all panels other than the radioPanel and the
			 * panel relevant to the selected orbit type
			 * For some listeners a check is also performed as whether to use the uPoint or origin to centre the orbits
			 */
			pointRadio.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
						enableComps(radioPanel.getParent(),false);
						enableComps(radioPanel,true);
						enableComps(pointPanel,true);
						if (origRadio.isSelected()) {
							useUpoint = false;
						}
						else {
							useUpoint = true;
						}
						orbitShape = "Point";
				}
				
			});
			
			
			lineRadio.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
						enableComps(radioPanel.getParent(),false);
						enableComps(radioPanel,true);
						enableComps(linePanel,true);
						orbitShape = "Line";
				}
				
			});
			
			crossRadio.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
						enableComps(radioPanel.getParent(),false);
						enableComps(radioPanel,true);
						enableComps(crossPanel,true);
						if (crossOrigRadio.isSelected()) {
							useUpoint = false;
						}
						else {
							useUpoint = true;
						}
						orbitShape = "Cross";
				}
				
			});
			
			circleRadio.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
						enableComps(radioPanel.getParent(),false);
						enableComps(radioPanel,true);
						enableComps(circPanel,true);
						if (circOrigRadio.isSelected()) {
							useUpoint = false;
						}
						else {
							useUpoint = true;
						}
						orbitShape = "Circ";
				}
				
			});
			
			/*
			 * Initially the settings default to a point trap centred at the origin
			 * Other orbit types also have some defaults selected
			 */
			
			pointRadio.setSelected(true);
			origRadio.setSelected(true);
			xRadio.setSelected(true);
			crossOrigRadio.setSelected(true);
			circOrigRadio.setSelected(true);
			
			enableComps(this,false);
			enableComps(radioPanel,true);
			enableComps(pointPanel,true);
			orbitShape = "Point";

		}
		
		
		public String getOrbitShape(){
			return orbitShape;
		}
		
		public double getIntercept() {
			double res = 0;
			try{
				res = Double.parseDouble(interceptField.getText());
			}
			catch(NumberFormatException nfe) {
				
			}

			return res;
		}
		
		public double getRad() {
			double res = 0;
			
			try{
				res = Double.parseDouble(radField.getText());
			}
			catch(NumberFormatException nfe) {
				
			}

			return res;
		}
		
		public Complex getPoint() {
			if(useUpoint) {
				return uPoint;
			}
			else {
				return Complex.ZERO;
			}
		}
		
		public char getAxis() {
			return axis;
		}
		
		/*
		 * enables of disabels all elements of a specified container based on the enable parameter
		 */
		public void enableComps(Container cont, boolean enable) {
			
			//gets all the components in the container
	        Component[] compArray = cont.getComponents();
	        
	        //iterates over all elements enabling or disabling them
	        for (Component c : compArray) {
	        	
	            c.setEnabled(enable);
	            
	            //If a component in the container is itself a container, recursively call this function on it
	            if (c instanceof Container) {
	                enableComps((Container) c, enable);
	            }
	        }
	    }
	}
	
	
	
	/*
	 *  This Panel will be in the centre of the frame, and displays the generated fractal
	 */
	private class FractalPanel extends JPanel{
		
		//The Image to display
	    private BufferedImage myImage;
	    
	    //These will be used to generate an image to fit the frame
	    private int width, height;
	    
	    /*
	     * Once the width and height of the image are calculated, these will store the information needed to draw
	     * the fractal on the image which shows the specified part of the complex plane
	     */
	    private double incrX, incrY, minX, maxY;
	    
	    //These are start and end points of the zoom selection box created by the user when they click and drag on the panel
	    private Point boxP1, boxP2;
	    
	    //This is true when the user is trying to drag a zoom box on the panel, and false otherwise
		private Boolean isDragging = false;
		
		//This will be used for alerting the program when the user clicks or drags on the screen
		private MouseAdapter panelListener;
		
		//Stores whether or not the component has any active listeners
		private Boolean hasListeners = false;
	    	
	    public FractalPanel(){
	    	
	        super();
			panelListener = new MandListener();
			
	    }
	    
	    /*
	     * Calculates the size of the image required based on the shape of the complex plane specified, then passes the correctly
	     * sized image to the FractalProcessor class to generate the required fractal
	     */
		private void drawFractal(double rMin, double rMax, double iMin, double iMax, int maxIts, String fractal){

			
			/*
			 * the (0,0) point is in the top left so represents the (min real, max Imaginary) part of the complex plane
			 */
			minX = rMin;
			maxY = iMax;
			

			/*
			 * These parts of code calculate the x/y ratio of the panel and compare this to
			 * the x/y ratio of the required complex plane in order to create an image which fits in the panel
			 */
			
	    	width = this.getWidth();
	    	height = this.getHeight();
			double panelRatio = (double) height / width;
	    	
			double rWidth = rMax - rMin;
			double iHeight = iMax - iMin;
			double complexRatio = iHeight/rWidth;


			if (complexRatio >= panelRatio) {
				width = (int) (height / complexRatio);
			}
			else{
				height = (int) (width * complexRatio);
			}
			
			//Checks for overly extreme plane where one dimension cannot be converted to an integer width > 0
			if(width == 0 || height == 0) {
				JOptionPane.showMessageDialog(null, "Cannot scale image!");
			}
			else {
				//Here we calculate how much of the complex plane each pixel represents in the x and y direction
				myImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
				incrX = (rWidth)/width;
				incrY = (iHeight)/height;
				
				if(getFractCode(fractal).equals("MandOrb")) {
					String orbCode;
					if(settingsPanel instanceof OrbitPanel) {
						orbCode = ((OrbitPanel) settingsPanel).getOrbitShape();
						switch (orbCode) {
						case "Cross":
						case "Point": 	fp.genOrbits(myImage, width, height, incrX, incrY, rMin, iMax, maxIts, ((OrbitPanel) settingsPanel).getPoint(), orbCode);
										break;
						case "Line":	fp.genOrbits(myImage, width, height, incrX, incrY, rMin, iMax, maxIts, ((OrbitPanel) settingsPanel).getPoint(), ((OrbitPanel) settingsPanel).getIntercept(), ((OrbitPanel) settingsPanel).getAxis());
										break;
						case "Circ":	fp.genOrbits(myImage, width, height, incrX, incrY, rMin, iMax, maxIts, ((OrbitPanel) settingsPanel).getPoint(), ((OrbitPanel) settingsPanel).getRad());
										break;
						
						}
					}

				}
				
				else {
					fp.genImg(myImage, width, height, incrX, incrY, rMin, iMax, maxIts, getFractCode(fractal));
				}
				

				repaint();
				
				//Adds listeners if they dont already exist (i.e. if this is the first generated image)
				if(!hasListeners){
					this.addMouseListener(panelListener);
					this.addMouseMotionListener(panelListener);
					hasListeners = true;
				}

			}
			
			
		}
		
		/*
		 * Zooms into the complex plane based on a user drawn box
		 */
		private void zoom(Point sPoint, Point ePoint) {
			
			//This calculates the real and imaginary values of the points specified by the user box
			double r1, r2, i1, i2;
			double iMin, iMax, rMin, rMax;
			r1 = minX + (sPoint.getX()*incrX);
			r2 = minX + (ePoint.getX()*incrX);
			i1 = maxY - (sPoint.getY()*incrY);
			i2 = maxY - (ePoint.getY()*incrY);
			
			// Depending on which point is larger, the minimum and maximum real and imaginary points are calculated
			if(r1>r2) {
				rMin = r2;
				rMax = r1;
			}
			else {
				rMin = r1;
				rMax =r2;
			}
			
			if(i1>i2) {
				iMin = i2;
				iMax =i1;
			}
			else {
				iMin = i1;
				iMax = i2;
			}
			
			// Gets the number of iterations required from the textfield 
			int its = defaultIts;
			try{
				its = Integer.parseInt(itField.getText());
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Not a valid number of iterations, using " + defaultIts + " instead");
			}
			
			//Sets the values displayed in the fields for choosing the plane dimensions to those specified by the box drawn
			rMinField.setText(Double.toString(rMin));
			rMaxField.setText(Double.toString(rMax));
			iMinField.setText(Double.toString(iMin));
			iMaxField.setText(Double.toString(iMax));
			
			//Draws the fractal for the part of the plane specified by the box drawn
			drawFractal(rMin,rMax,iMin,iMax, its, fractalCombo.getSelectedItem().toString());
		}

		
		/*
		 * This listener will deal with the user selecting a point by clicking or zooming in on the image by pressing and dragging
		 */
		private class MandListener extends MouseAdapter {
			
			public MandListener(){
			}
			
			/*
			 * When the mouse is clicked on the panel, a point is drawn and the user point's complex value is calculated
			 * This value is then displayed in a textfield for the user
			 */
			public void mouseClicked(MouseEvent e){
				double i, r;
				int x, y;
				x = e.getX();
				y = e.getY();
				if(x < width && y < height) {
					r = minX + (x*incrX);
					i = maxY - (y*incrY);
					uPoint = new Complex(r,i);
					uPointField.setText(uPoint.toString());
					isPoint = true;
					juliaButton.setEnabled(true);
					repaint();
				}

			}
			
			//When the mouse is pressed if it is inside the image the start point of the selection box is selected
			public void mousePressed(MouseEvent e) {
				if (isInside(e.getPoint())) boxP1 = e.getPoint();
				else boxP1 = null;
			}
			
			//When the mouse is dragged the end point of the selection point is selected (as long as the drawn box is big enough in both directions)
			public void mouseDragged(MouseEvent e) {
				if (isInside(e.getPoint())) {
					boxP2 = e.getPoint();
				}
				//This is to avoid confusion between clicking to select a point, and dragging out a box, which must  be at least 20x20 to register
				if(boxP1 != null && Math.abs(boxP1.getX()-boxP2.getX()) > 20 && Math.abs(boxP1.getY()-boxP2.getY()) > 20) {
					isDragging = true;
					repaint();
				}
				else {
					isDragging = false;
					repaint();
				}
			}
			
			//When the mouse is released, if a box was being created then the plane zooms into where the box describes
			public void mouseReleased(MouseEvent e) {
				if(isDragging) {
					isDragging = false;
					zoom(boxP1, boxP2);
				}
			}
			
			//Tests if a point is within the drawn image
			private boolean isInside(Point p) {
				return (p.getX() <= width && p.getY() <= height);
			}
		}

		/*
		 * When paint is called the image is drawn
		 * If there is a user point selected then it is also drawn
		 * If a selection box is being draggedit is drawn also
		 */
	    @Override
	    public void paint(Graphics g){
	    	super.paint(g);
	        g.drawImage(myImage, 0, 0, width, height, null);
	        if(isPoint) {
	        	drawPoint(uPoint,g,5,Color.RED);
	        }
	        if(isDragging) {
	        	drawSelectBox(boxP1, boxP2, g);
	        }
	    }
	    
	    /*
	     * Draws a point for the specified Complex number with specified radius r and colour col,
	     * providing the point is within the part of the complex plane currently drawn
	     */
	    public void drawPoint(Complex c, Graphics g, int r, Color col) {
	    	g.setColor(col);
	    	int x = (int)(((c.getReal()-minX)/incrX)-r);
	    	int y = (int)(((maxY-c.getImaginary())/incrY)-r);
	    	if(x <= width && y <= height) {
	        	g.fillOval(x, y, r*2, r*2);
	    	}

	    }
	    
	    /*
	     * Draws a semi-transparent selection box for the start and current mouse positions
	     */
	    public void drawSelectBox(Point sPoint, Point ePoint, Graphics g) {
	    	
	    	//The x and y of the top left of the rectangle to be drawn, as well as its width and height
	    	int rectX,rectY,rectW,rectH;
	    	//The x and y values of the two point are seperated out, so the top left position can be found
	    	double sX, eX, sY, eY;
	    	
	    	sX = sPoint.getX();
	    	eX = ePoint.getX();
	    	sY = sPoint.getY();
	    	eY = ePoint.getY();
	    	
	    	//Calculates the dimensions and starting point of the rectangle to be drawn
	    	if (sX > eX){
	    		rectX = (int) eX;
	    		rectW = (int) (sX - eX);
	    	}
	    	else {
	    		rectX = (int) sX;
	    		rectW = (int) (eX - sX);
	    	}
	    	if(sY > eY) {
	    		rectY = (int) eY;
	    		rectH = (int) (sY - eY);
	    	}
	    	else {
	    		rectY = (int) sY;
	    		rectH = (int) (eY - sY);
	    	}
	    	
	    	//Sets the colour of the rectangle to be filled, and then draws it
	    	
	    	int red, green, blue, alpha;
	    	alpha = 64;
	    	red = 255;
	    	green = blue = 0;
	   
	    	g.setColor(new Color(red, green, blue, alpha));
	    	g.fillRect(rectX, rectY, rectW, rectH);
	    }
	}
}
