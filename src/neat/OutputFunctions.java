package neat;

public class OutputFunctions {
	public static double sigmoid(double x) {
		return 1/( 1 + Math.pow(Math.E,(-1*x)));
	}
}
