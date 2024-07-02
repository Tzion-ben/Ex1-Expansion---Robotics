import java.text.DecimalFormat;

public class Point {
	public double x;
	public double y;
	public double z;

	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 0; // Default z value
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}

	public Point() {
		x = 0;
		y = 0;
		z = 0;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		return "(" + df.format(x) + "," + df.format(y) + "," + df.format(z) + ")";
	}
}
