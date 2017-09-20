import javax.swing.JFrame;

public class FractalGen {

	public static void main(String[] args) {
		JuliaSaves jSaves = new JuliaSaves();
		FractalProcessor fp = new FractalProcessor();
		JFrame MFrame = new MainFractalFrame(jSaves, fp);
	}

}
