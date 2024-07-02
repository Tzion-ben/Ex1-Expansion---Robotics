import java.awt.Graphics;
import java.util.Random;

public class Lidar {
	Drone drone;
	double degrees;
	double current_distance = 0;

	public Lidar(Drone drone, double degrees) {
		this.drone = drone;
		this.degrees = degrees;
	}

	public double getDistance(int deltaTime) {
		Point actualPointToShoot = drone.getPointFromStart();
		double rotation = drone.getRotation() + degrees;
		double distanceInCM = 1;

		while (distanceInCM <= WorldParams.lidarLimit) {
			Point p = Tools.getPointByDistance(actualPointToShoot, rotation, distanceInCM);
			int x = (int) p.x;
			int y = (int) p.y;
			int z = drone.realMap.getHeightAt(x, y);

			if (z == -1 || z > drone.getPointFromStart().z) { // Detecting wall or building taller than current height
				break;
			}

			distanceInCM++;
		}

		return distanceInCM;
	}

	public double getSimulationDistance(int deltaTime) {
		Random ran = new Random();
		double distanceInCM;
		if (ran.nextFloat() <= 0.05f) { // 5% of the time, not getting an answer
			distanceInCM = 0;
		} else {
			distanceInCM = getDistance(deltaTime);
			distanceInCM += (int) ran.nextInt(WorldParams.lidarNoise * 2) - WorldParams.lidarNoise; // +- 5 CM to the final calc
		}

		this.current_distance = distanceInCM; // store it for instance get
		return distanceInCM;
	}

	public void paint(Graphics g) {
		Point actualPointToShoot = drone.getPointFromStart();
		double fromRotation = drone.getRotation() + degrees;
		Point to = Tools.getPointByDistance(actualPointToShoot, fromRotation, this.current_distance);

		g.drawLine((int) actualPointToShoot.x, (int) actualPointToShoot.y, (int) to.x, (int) to.y);
	}
}
