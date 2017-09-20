import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MandelbrotFrame extends JFrame {

	private JPanel contentPane;
	private MandPanel imgPanel;
	private JTextField rMinField, rMaxField, iMinField, iMaxField, itField, uPointField;
	private Complex uPoint; 
	private boolean isPoint;
	private JuliaSaves jSaves;

	/**
	 * Create the frame.
	 */
	public MandelbrotFrame(JuliaSaves js) {
		jSaves = js;
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				init();
			}
		});
	}
	
	/**
	 * Adds the various components to the  frame
	 */
	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 1024, 720 );
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		/*
		 * The north part of the layout contains the text fields describing the bounds of the displayed complex plane,
		 *Which can be modified by th user
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
		
		/*
		 * The south panel contains the text field for changing the iteration number, and to display the point
		 * currently selected by the user and the button to display the related julia set
		 */
		JPanel southPanel = new JPanel();
		GridLayout gl = new GridLayout(1,6);
		gl.setHgap(10);
		southPanel.setLayout(gl);
		
		itField = new JTextField("100");
		JButton genButton = new JButton("Generate Fractal");
		genButton.addActionListener(new genButtonListener());
		southPanel.add(new JLabel("no. of Iterations"));
		southPanel.add(itField);
		
		JLabel uPointLabel = new JLabel("User Selected Point");
		uPointField = new  JTextField("test");
		JButton juliaButton = new JButton("generate julia set");
		juliaButton.addActionListener(new JuliaButtonListener());
		
		JButton loadJuliaButton = new JButton("Load Favourite Julia Fractal");
		loadJuliaButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new LoadJuliaFrame(jSaves);
			}
			
		});
		
		southPanel.add(genButton);
		southPanel.add(uPointLabel);
		southPanel.add(uPointField);
		southPanel.add(juliaButton);
		southPanel.add(loadJuliaButton);

		
		//This is the central element where the fractal image will appear
		imgPanel = new MandPanel();
		
		contentPane.add(imgPanel, BorderLayout.CENTER);
		contentPane.add(axisBoxPanel, BorderLayout.NORTH);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		
		setContentPane(contentPane);
		this.setVisible(true);
	}
	
	/**
	 * This class can calculate the number of iteration for a given complex number before it escapes the Mandelbrot Set.
	 * It can also calculate a smoothed colour value for this complex point, but this is based only on the individual value
	 */
	private class MandCalc {
		
		private int maxIts;
		int noIts = 0;
		Complex escapeZ = Complex.ZERO;
		private Complex c;
		
		private MandCalc(double x, double y, int its) {
			c = new Complex(x,y);
			maxIts = its;
			noIts = z(1,Complex.ZERO);
		}
		
		/*
		 * The recursive formula for the Mandelbrot Set where z(i+1) = z(i)*z(i) + c, and z(0) = 0
		 */
		private int z(int i, Complex prevZ) {
			if(i == 0) return z(1, Complex.ZERO);
			if(i >= maxIts) return maxIts;
			else {
				Complex res = c.add(prevZ.square());
				if (res.modulusSquared() < 4.0) {
					return z(i+1, res);
				}
				else {
					escapeZ = res;
					return i;
				}
				
			}
		}
		
		private int getCol() {
			if (noIts == -1) return (0x00 << 16 | 0x00 << 8 | 0x00);
			else {
				float mu = (float) (noIts + 1 - Math.log(Math.log(Math.sqrt(escapeZ.modulusSquared())))/Math.log(2));
				return Color.HSBtoRGB(mu/255,0.8f ,.85f);
			}
		}
		
		private int getIts(){
			return noIts;
		}
	}

	/**
	 * When the generate fractal button is pressed, this calls the drawFractal method
	 */
	private class genButtonListener implements ActionListener {

		double rMin, rMax, iMin, iMax;
		int its;
		
		@Override
		public void actionPerformed(ActionEvent e) {
						try{
							rMin = Double.parseDouble(rMinField.getText());
							rMax = Double.parseDouble(rMaxField.getText());
							iMin = Double.parseDouble(iMinField.getText());
							iMax = Double.parseDouble(iMaxField.getText());
							its = Integer.parseInt(itField.getText());

							new Thread( new FractalThread(rMin, rMax, iMin, iMax, its)).start();
							//imgPanel.drawFractal(rMin, rMax, iMin, iMax, its);
						}
						catch (NumberFormatException nfe) {
							System.out.println(nfe.getMessage());
						}
		}
		
		private	class FractalThread implements Runnable {
			
			double reMin, reMax, imMin, imMax;
			int iterations;
			
			private FractalThread(double rMin, double rMax, double iMin, double iMax, int its) {
				reMin = rMin;
				reMax = rMax;
				imMin = iMin;
				imMax = iMax;
				iterations = its;
			}

			@Override
			public void run() {
				imgPanel.drawFractal(reMin, reMax, imMin, imMax, iterations);
			}
			
		}
	}
	
	private class JuliaButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(isPoint){
				new JuliaFrame(uPoint, jSaves);
			}
		}
	}
	
	// This Displays the fractal generated
	private class MandPanel extends JPanel{
	    private BufferedImage myImage;
	    private int width, height, uX, uY = 0;
	    private double incrX, incrY, minX, maxY;
	    private static final int scaleFactor = 2;
	    private Point boxP1, boxP2;
		private Boolean isDragging = false;
	    	
	    public MandPanel(){
	    	
	        super();
			javax.swing.SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					init();
				}
			});

	    }
	    
	    public void init() {
	        this.setPreferredSize(new Dimension(800,600));
	        myImage = new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB);
	        width = myImage.getWidth();
	        height = myImage.getHeight();
	        for(int w = 0; w < width; w++) {
	        	for(int h = 0; h < height; h++) {
	        		myImage.setRGB(w, h, (64 << 16 | 64 << 8 | 128));
	        	}
	        }
	    }
	    
		private void drawFractal(double rMin, double rMax, double iMin, double iMax, int maxIts){
			minX = rMin;
			maxY = iMax;
			double rWidth = rMax - rMin;
			double zHeight = iMax - iMin;
			double ratio = zHeight/rWidth;
			if (ratio > 0.75){
				height = 600;
				width = (int) (600/ratio);
			}
			else {
				width = 800;
				height = (int) (800 * ratio);
			}
			this.setPreferredSize(new Dimension(width,height));
			myImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			double x, y;
			System.out.println(width + " x " + height);
			incrX = (rWidth)/width;
			incrY = (zHeight)/height;
			System.out.println(incrX);
			System.out.println(incrY);
			
			//The histogram will store how many points correspond to each iteration number
			//We can use this to colour the image later
			int[] histogram = new int[maxIts-1];
			for(int i: histogram){
				i = 0;
			}
			
			int[][] itArray = new int[width][height];
			int totalPixels = 0;
			
			x = rMin;
			for(int w = 0; w < width; w++){
				y = iMax;
				for(int h = 0; h < height; h++) {
					//myImage.setRGB(w, h, new MandSet(x,y,maxIts).getCol());
					int itsHere = new MandCalc(x,y,maxIts).getIts();
					itArray[w][h] = itsHere;
					if (itsHere != maxIts) {
						histogram[itsHere-1] += 1;
						totalPixels++;
					}
					y -= incrY;
				}
				x += incrX;
			}
			
			System.out.println(totalPixels);
			
			for (int w = 0; w < width; w++){
				for (int h = 0; h< height; h++) {
					int itsHere = itArray[w][h];
					if (itsHere == maxIts) myImage.setRGB(w, h, 0);
					else {
						float hue = 0.0f;
						for (int i = 0; i < itArray[w][h]; i ++) {
						  hue += (float) histogram[i] / totalPixels;
						}
						myImage.setRGB(w,h, Color.HSBtoRGB(1.0f-hue/2, 0.8f, hue));
					}
				}
			}

			MandListener myListener = new MandListener();  
			this.addMouseListener(myListener);
			this.addMouseMotionListener(myListener);
			this.repaint();
			
		}
		
		private void zoom(Point sPoint, Point ePoint) {
			double r1, r2, i1, i2;
			double iMin, iMax, rMin, rMax;
			r1 = minX + (sPoint.getX()*incrX);
			r2 = minX + (ePoint.getX()*incrX);
			i1 = maxY - (sPoint.getY()*incrY);
			i2 = maxY - (ePoint.getY()*incrY);
			
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
			
			int its = 100;
			try{
				its = Integer.parseInt(itField.getText());
			}
			catch (NumberFormatException nfe) {
				System.out.println(nfe.getMessage());
			}
			
			rMinField.setText(Double.toString(rMin));
			rMaxField.setText(Double.toString(rMax));
			iMinField.setText(Double.toString(iMin));
			iMaxField.setText(Double.toString(iMax));
			
			drawFractal(rMin,rMax,iMin,iMax, its);
		}

		private class MandListener extends MouseAdapter {
			
			public MandListener(){
			}
			
			public void mouseClicked(MouseEvent e){
				double i, r;
				int x, y;
				x = e.getX();
				y = e.getY();
				System.out.println(x + "," + y);
				if(x < width && y < height) {
					r = minX + (x*incrX);
					i = maxY - (y*incrY);
					uPoint = new Complex(r,i);
					uPointField.setText(uPoint.toString());
					isPoint = true;
					repaint();
				}

			}
			
			public void mousePressed(MouseEvent e) {
				boxP1 = e.getPoint();
				//System.out.println(startingPoint);
			}
			
			public void mouseDragged(MouseEvent e) {
				boxP2 = e.getPoint();
				//This is to avoid confusion between clicking to select a point, and dragging out a box, which must  be at least 20x20 to register
				if(Math.abs(boxP1.getX()-boxP2.getX()) > 20 && Math.abs(boxP1.getY()-boxP2.getY()) > 20) {
					isDragging = true;
					repaint();
				}
				else {
					isDragging = false;
					repaint();
				}
				//System.out.println("Dragging...");
			}
			
			public void mouseReleased(MouseEvent e) {
				if(isDragging) {
					isDragging = false;
					System.out.println(boxP2);
					zoom(boxP1, boxP2);
				}
			}
		}

	    @Override
	    public void paint(Graphics g){
	    	super.paint(g);
	        g.drawImage(myImage, 0, 0, width, height, null);
	        if(isPoint) {
	        	//g.setColor(Color.BLUE);
	        	//g.fillOval((int)(((uPoint.getReal()-minX)/incrX)-5), (int)(((maxY-uPoint.getImaginary())/incrY)-5), 10, 10);
	        	drawPoint(uPoint,g,5,Color.RED);
	        }
	        if(isDragging) {
	        	drawSelectBox(boxP1, boxP2, g);
	        }
	    }
	    
	    public void drawPoint(Complex c, Graphics g, int r, Color col) {
	    	g.setColor(col);
        	g.fillOval((int)(((c.getReal()-minX)/incrX)-r), (int)(((maxY-c.getImaginary())/incrY)-r), r*2, r*2);
	    }
	    
	    public void drawSelectBox(Point sPoint, Point ePoint, Graphics g) {
	    	int rectX,rectY,rectW,rectH;
	    	double sX, eX, sY, eY;
	    	sX = sPoint.getX();
	    	eX = ePoint.getX();
	    	sY = sPoint.getY();
	    	eY = ePoint.getY();
	    	
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
	    	
	    	int red, green, blue, alpha;
	    	alpha = 64;
	    	red = 255;
	    	green = blue = 0;
	    	
	    	g.setColor(new Color(red, green, blue, alpha));
	    	g.fillRect(rectX, rectY, rectW, rectH);
	    }
	}
	
	private int getMandCol(Complex c, int maxIts) {
		int noIts = 0;
		int i = 0;
		Complex d = Complex.ZERO;
		boolean done = false;
		while(!done) {
			if(i >= maxIts) {
				done = true;
				noIts = -1;
			}
			else {
				d = c.add(d.square());
				if (d.modulusSquared() >= 4.0) {
					noIts = i+1;
					done = true;
				}
				else {
					i++;
				}
				
			}
		}
		
		return itsToCol(noIts);

		
	}

	private int itsToCol(int noIts) {
		if (noIts == -1) return (0x00 << 16 | 0x00 << 8 | 0x00);
		else if(noIts < 5) return(0xFF << 16 | 0x00 << 8 | 0x00);
			else if(noIts <10) return(0xFF << 16 | 0x33 << 8 | 0x00);
				else if(noIts < 20) return (0xFF << 16 | 0x77 << 8 | 0x00);
					else if(noIts <50) return (0xFF << 16 | 0xFF << 8 | 0x00);
						else if(noIts < 100) return (0x00 << 16 | 0xFF << 8 | 0xFF);
							else return (0x77 << 16 | 0xFF << 8 | 0xFF);
	}

}
