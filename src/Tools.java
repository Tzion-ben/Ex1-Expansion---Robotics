import java.util.Random;

public class Tools {

	/*
	 * distance in CM
	 */
	public static Point getPointByDistance(Point fromPoint, double rotation, double distance) {
		double radians = Math.PI * (rotation / 180);

		double i = distance / WorldParams.CMPerPixel;
		double xi = fromPoint.x + Math.cos(radians) * i;
		double yi = fromPoint.y + Math.sin(radians) * i;

		return new Point(xi, yi);
	}

	/*
	 * Generate noise between min and max values
	 */
	public static double noiseBetween(double min, double max, boolean isNegative) {
		Random rand = new Random();
		double noiseToDistance = 1;
		double noise = (min + rand.nextFloat() * (max - min)) / 100;
		if (!isNegative) {
			return noiseToDistance + noise;
		}

		if (rand.nextBoolean()) {
			return noiseToDistance + noise;
		} else {
			return noiseToDistance - noise;
		}
	}

	public static double getRotationBetweenPoints(Point from, Point to) {
		double y1 = from.y - to.y;
		double x1 = from.x - to.x;

		double radians = Math.atan(y1 / x1);

		double rotation = radians * 180 / Math.PI;
		return rotation;
	}

	public static double getDistanceBetweenPoints(Point from, Point to) {
		double x1 = (from.x - to.x) * (from.x - to.x);
		double y1 = (from.y - to.y) * (from.y - to.y);
		return Math.sqrt(x1 + y1);
	}

	// Optional: If you need to consider z-coordinate in distance calculation
	public static double getDistanceBetweenPoints3D(Point from, Point to) {
		double x1 = (from.x - to.x) * (from.x - to.x);
		double y1 = (from.y - to.y) * (from.y - to.y);
		double z1 = (from.z - to.z) * (from.z - to.z);
		return Math.sqrt(x1 + y1 + z1);
	}
}
