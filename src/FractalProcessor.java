import java.awt.Color;
import java.awt.image.BufferedImage;

public class FractalProcessor {
	
	//Generates orbits for crossed lines and points
	public void genOrbits(BufferedImage img, int width, int height, double dx, double dy, double xMin, double yMax, int its, Complex point, String orbShape) {

		double x, y;

		double[][] distArray = new double[width][height];
		double maxDist = 1.0;
		
		x = xMin;
		for(int w = 0; w < width; w++){
			y = yMax;
			for(int h = 0; h < height; h++) {
					double distHere = 0;
					switch(orbShape) {
					case "Point": 	distHere = getMandDistance(new Complex(x,y), point, its);
									break;
					case "Cross": 	distHere = getMandCrossDistance(new Complex(x,y), point, its);
									break;
					}
					if (distHere > maxDist) {
						maxDist = distHere;
					}
					distArray[w][h] = distHere;
					y -= dy;
			}
			x += dx;
		}
		
		colourImage(img, width, height, distArray, maxDist);
	}
	
	//generates orbits for circles
	public void genOrbits(BufferedImage img, int width, int height, double dx, double dy, double xMin, double yMax, int its, Complex point, double radius) {
		double x, y;

		double[][] distArray = new double[width][height];
		double maxDist = 1.0;
		
		x = xMin;
		for(int w = 0; w < width; w++){
			y = yMax;
			for(int h = 0; h < height; h++) {
					double distHere;
					distHere = getMandDistance(new Complex(x,y), point, radius, its);
					if (distHere > maxDist) {
						maxDist = distHere;
					}
					distArray[w][h] = distHere;
					y -= dy;
			}
			x += dx;
		}
		
		colourImage(img, width, height, distArray, maxDist);
	}
	
	//generates orbits for lines
	public void genOrbits(BufferedImage img, int width, int height, double dx, double dy, double xMin, double yMax, int its, Complex point, double intercept, char axis) {
		double x, y;

		double[][] distArray = new double[width][height];
		double maxDist = 1.0;
		
		x = xMin;
		for(int w = 0; w < width; w++){
			y = yMax;
			for(int h = 0; h < height; h++) {
					double distHere;
					distHere = getMandDistance(new Complex(x,y), intercept, axis, its);
					if (distHere > maxDist) {
						maxDist = distHere;
					}
					distArray[w][h] = distHere;
					y -= dy;
			}
			x += dx;
		}
		
		colourImage(img, width, height, distArray, maxDist);
	}

	/*
	 * Paints a Mandelbrot fractal onto the specified image, based on a specified complex plane, and a specified maximum iterations
	 */
	public void genImg (BufferedImage img, int width, int height, double dx, double dy, double xMin, double yMax, int its, String fractal) {
		
		double x, y;
		
		//The histogram will store how many points correspond to each iteration number
		//We can use this to colour the image later
		int[] histogram = blankHistogram(its);
		
		//Stores the number of iterations for each pixel until divergence
		int[][] itArray = new int[width][height];
		//The total number of divergent pixels (not pixels in the set which will be coloured black)
		int totalPixels = 0;
		
		x = xMin;
		for(int w = 0; w < width; w++){
			y = yMax;
			for(int h = 0; h < height; h++) {
				int itsHere = 0;
				switch(fractal){
					case "Mand" : 		itsHere = getMandIts(new Complex(x,y), new Complex(x,y), its);
										break;
					case "Burn" :		itsHere = getBurnIts(new Complex(x,y),new Complex(x,y), its);
										break;
				}

				itArray[w][h] = itsHere;
				if (itsHere != -1) {
					histogram[itsHere-1] += 1;
					totalPixels++;
				}
				y -= dy;
			}
			x += dx;
		}
		
		colourImage(img, width, height, itArray, histogram, totalPixels);
		
	}
	
	public void genJulia(BufferedImage img, int width, int height, double dx, double dy, double xMin, double yMax, int its, Complex c, String fractal) {
		
		double x,y;
		
		//The histogram will store how many points correspond to each iteration number
		//We can use this to colour the image later
		int[] histogram = blankHistogram(its);
		
		//Stores the number of iterations for each pixel until divergence
		int[][] itArray = new int[width][height];
		//The total number of divergent pixels (not pixels in the set which will be coloured black)
		int totalPixels = 0;
		
        x = xMin;
        for(int w = 1; w < width; w++) {
        	y = yMax;
        	for(int h = 1; h < height; h++) {
        		int itsHere = 0;
        		switch(fractal){
        		case "MandOrb":
        		case "Mand": 	itsHere = getMandIts(c, new Complex(x,y), its);
        						break;
        		case "Burn":	itsHere = getBurnIts(c, new Complex(x,y), its);
        						break;
        		}
        		
        		itArray[w][h] = itsHere;
        		if(itsHere != -1) {
        			histogram[itsHere-1] += 1;
        			totalPixels++;
        		}
        		y -= dy;
        	}
        	x += dx;
        }
        
        colourImage(img, width, height, itArray, histogram, totalPixels);
	}

	/*
	 * The iterative function to generate the number of iterations for a given Complex number in the Mandelbrot set
	 * Passing the point in the plane as both c and d will give the normal fractal, whilst passing a fixed value c
	 * and the point in the plane as d the julia fractal will be generated instead
	 */
	private int getMandIts(Complex c, Complex d, int maxIts) {
		int noIts = 0;
		int i = 1;
		Complex z = d;
		boolean done = false;
		
		while(!done) {
			
			z = c.add(z.square());
			
			if (z.modulusSquared() >= 4.0) {
				noIts = i;
				done = true;
			}
			else {
				if(i >= maxIts) {
					done = true;
					noIts = -1;
				}
				else {
					i++;
				}
			}
		}
		
		return noIts;	
	}
	
	/*
	 * The iterative function to generate the number of iterations for a given Complex number for the Burning Ship 
	 * fractal Passing the point in the plane as both c and d will give the normal fractal, whilst passing a 
	 * fixed value c and the point in the plane as d the julia fractal will be generated instead
	 */
	private int getBurnIts(Complex c, Complex d, int maxIts) {
		int noIts = 0;
		int i = 1;
		Complex z = d;
		boolean done = false;
		
		while(!done) {
			
			z = c.add(new Complex(Math.abs(z.getReal()),-Math.abs(z.getImaginary())).square());
			
			if (z.modulusSquared() >= 4.0) {
				noIts = i;
				done = true;
			}
			else {
				if(i >= maxIts) {
					done = true;
					noIts = -1;
				}
				else {
					i++;
				}
			}
		}
		
		return noIts;	
	}
	
	private int[] blankHistogram(int its){

		int[] histogram = new int[its];
		for(int i = 0; i < histogram.length; i++){
			histogram[i] = 0;
		}
		return histogram;
		
	}
	
	/*
	 * Colours an image based on iterations at different points and a histogram storing the number of pixels 
	 * which have each iteration number
	 */
	private void colourImage(BufferedImage img, int width, int height, int[][] itArray, int[] hist, int totPix) {
		for (int w = 0; w < width; w++){
			for (int h = 0; h< height; h++) {
				int itsHere = itArray[w][h];
				if (itsHere == -1) img.setRGB(w, h, 0);
				else {
					float hue = 0.0f;
					for (int i = 0; i < itsHere; i ++) {
					  hue += (float) hist[i] / totPix;
					}
					img.setRGB(w,h, Color.HSBtoRGB(1.0f - hue/2, 0.8f, hue));
				}
			}
		}
	}
	
	/*
	 * Colours an image based on orbit distance from a point
	 */
	private void colourImage(BufferedImage img, int width, int height, double[][] dArray, double dMax) {
		
		double cRatio = (1.0/Math.sqrt(dMax));
		
		for (int w = 0; w < width; w++){
			for (int h = 0; h< height; h++) {
				double dHere = dArray[w][h];
				
				img.setRGB(w, h, Color.HSBtoRGB((float) (Math.sqrt(dHere)*cRatio), 1.0f, 1.0f));
			}
		}
	}
	
	
	/*
	 * Gets the distance of each iteration of a Complex point through the Mandelbrot set from a point p
	 */
	private double getMandDistance(Complex c, Complex p, int maxIts) {
		double distance = Double.MAX_VALUE;
		Complex z = c;
		
		for(int i = 0; i < maxIts; i++) {
			
			//The Mandelbrot Iteration
			z = c.add(z.square());
			
			//this calculates the distance between each iteration's complex point and the orbit point p
			Complex zMinPoint = z.sub(p);
			double zMinPMod = Math.sqrt(zMinPoint.modulusSquared());
					
			if(zMinPMod < distance){
				distance = zMinPMod;
			}
		}
		
		
		return distance;	
	}
	
	/*
	 * Gets the distance of each iteration of a Complex point through the Mandelbrot set from a circle of centre p, radius r
	 */
	private double getMandDistance(Complex c, Complex p, double r, int maxIts) {
		double distance = Double.MAX_VALUE;
		Complex z = c;
		
		for(int i = 0; i < maxIts; i++) {
			
			//The Mandelbrot Iteration
			z = c.add(z.square());
			
			//calculates the distance of each iteration point from the nearest circle edge
			Complex zMinPoint = z.sub(p);
			double zMinPMod;
			//point is outside circle
			if (zMinPoint.modulusSquared() >= r*r) {
				zMinPMod = Math.sqrt(zMinPoint.modulusSquared()) - r;
			}
			//point is inside circle
			else {
				zMinPMod = r - Math.sqrt(zMinPoint.modulusSquared());
			}

					
			if(zMinPMod < distance){
				distance = zMinPMod;
			}
		}
		
		
		return distance;	
	}
	
	/*
	 * Gets the distance of each iteration of a Complex point through the Mandelbrot set from a flat line with a passed axis intersect
	 */
	private double getMandDistance(Complex c, double intersect, char intAxis, int maxIts) {
		double distance = Double.MAX_VALUE;
		Complex z = c;
		
		for(int i = 0; i < maxIts; i++) {
			
			//The Mandelbrot Iteration
			z = c.add(z.square());
			
			Complex zFromLine = Complex.ZERO;
			
			//calculates the distance of each iteration point from a vertical or horizontal line
			if(intAxis == 'x') {
				zFromLine = z.sub(new Complex(intersect,z.getImaginary()));
			}
			else {
				zFromLine = z.sub(new Complex(z.getReal(), intersect));
			}
			double zFLMod = Math.sqrt(zFromLine.modulusSquared());
					
			if(zFLMod < distance){
				distance = zFLMod;
			}
		}
		
		
		return distance;	
	}
	
	
	/*
	 * Gets the distance of each iteration of a cross centred through point p
	 */
	private double getMandCrossDistance(Complex c, Complex p, int maxIts) {
		double distance = Double.MAX_VALUE;
		Complex z = c;
		
		for(int i = 0; i < maxIts; i++) {
			
			//The Mandelbrot Iteration
			z = c.add(z.square());
			
			/*
			 * calculates the distance to both lines of the cross from the iteration point,
			 * then uses the smallest distance
			 */
	
			Complex zminX = z.sub(new Complex(p.getReal(),z.getImaginary()));
			Complex zminY = z.sub(new Complex(z.getReal(),p.getImaginary()));
			
			Complex zMin;
			if (zminX.modulusSquared() < zminY.modulusSquared()){
				zMin = zminX;
			}
			else{
				zMin = zminY;
			}
			
			double zMod = Math.sqrt(zMin.modulusSquared());
					
			if(zMod < distance){
				distance = zMod;
			}
		}
		
		
		return distance;	
	}
	
}
