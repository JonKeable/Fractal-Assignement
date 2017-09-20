import java.text.DecimalFormat;
import java.text.Format;

public class Complex {

	//The real and iaginary parts of the number (i is not stored in the imaginary part)
	private double real, imaginary;
	//The constant to create the Complex number 0 + 0i, which is of course simply zero
	public static final Complex ZERO = new Complex(0,0);
	
	//Creates a new number based on a specified real and imaginary part
	public Complex(double r, double i) {
		real = r;
		imaginary = i;
	}
	
	public double getReal(){
		return real;
	}
	
	public double getImaginary(){
		return imaginary;
	}
	
	/*
	 * Squares the complex number based on:
	 *  (a+bi)^2 = a^2 + 2abi + b^2.i^2 	
	 *  i = sqrt(-1) => i^2 = -1
	 *  => (a+bi)^2 = (a^2 - b^2) + 2abi
	 */
	public Complex square() {
		double r = (real*real) - (imaginary*imaginary);
		double i = 2*real*imaginary;
		return new Complex(r,i);
	}
	
	/* 
	 * returns the modulus squared based on:
	 * Mod(a+bi) = sqrt(a^2 + b^2)
	 * => [Mod(a+bi)]^2 = a^2 + b^2
	 */
	
	public double modulusSquared() {
		return (real*real + imaginary*imaginary);
	}
	
	//returns the sum of this number and another complex number d
	public Complex add(Complex d) {
		return new Complex(real + d.getReal(), imaginary + d.getImaginary());
	}
	
	//returns this complex number minus another specified complex number
	public Complex sub(Complex d) {
		return new Complex(real - d.getReal(), imaginary - d.getImaginary());
	}
	
	/*
	 * Returns a string in the form "a+bi" to represent this number
	 * negative b will give "a - bi"
	 * a = 0 will give bi
	 * b = 0 will give a
	 * All results are two 2 decimal places
	 */
	public String toString() {
		Format df = new DecimalFormat("##0.00");
		if(real == 0) return (Double.toString(imaginary) + "i");
		else if(imaginary > 0) return(df.format(real) + "+" + df.format(imaginary) + "i") ;
		else if(imaginary == 0) return(df.format(real));
		else return (df.format(real) + df.format(imaginary) + "i");
		
	}
}
