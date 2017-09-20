
public class ComplexTester {
	
	public static void main(String args[]) {
		Complex c = new Complex(2,4);
		Complex d = new Complex(1,-3);
		System.out.println(c.toString());
		System.out.println(d.toString());
		System.out.println(c.add(d).toString());
		System.out.println(c.square());
		System.out.println(d.square());
		System.out.println(c.modulusSquared());
		System.out.println(d.modulusSquared());
		
	}
}
