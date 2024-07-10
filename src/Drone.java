import java.awt.Color;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Drone {
	public double gyroRotation;
	public Point sensorOpticalFlow;
	private double batteryLevel;
	private long startTime;
	public Point pointFromStart;
	public Point startPoint;
	public List<Lidar> lidars;
	private String drone_img_path = "D:\\Tests\\drone_3_pixels.png";
	public Map realMap;
	private double rotation;
	private double speed;
	private CPU cpu;
	private Color color; // New color attribute
	private Point estimatedPosition;
	private double estimatedOrientation;

	// Add new sensor fields
	private double yaw;
	private double Vx, Vy;
	private double pitch, roll;
	private double accX, accY;

	// New fields for PID controllers
	private PID pitchPID, rollPID, yawPID, altitudePID;
	private double desiredPitch, desiredRoll, desiredYaw, desiredAltitude;

	private double prevVx, prevVy, prevZ; // Previous velocities for acceleration calculation
	private long prevTime; // Previous time for acceleration calculation

	private double windStrength = 0.1;

	public Drone(Map realMap, DroneType droneType, Color color) {
		this.realMap = realMap;
		this.color = color; // Initialize the color

		this.startPoint = realMap.drone_start_point;
		pointFromStart = new Point();
		sensorOpticalFlow = new Point();
		lidars = new ArrayList<>();

		speed = 0.2;
		batteryLevel = 100;
		startTime = System.currentTimeMillis();

		rotation = 0;
		gyroRotation = rotation;

		// Initialize PID controllers
		pitchPID = new PID(1.0, 0.01, 0.1, 10.0);
		rollPID = new PID(1.0, 0.01, 0.1, 10.0);
		yawPID = new PID(1.0, 0.01, 0.1, 10.0);
		altitudePID = new PID(1.0, 0.01, 0.1, 10.0);

		prevVx = 0;
		prevVy = 0;
		prevZ = 0;
		prevTime = System.currentTimeMillis();

		cpu = new CPU(100, "Drone");
		// startSound = new Sound(".wav");
		for (int degrees : droneType.lidarOrientations) {
			addLidar(degrees);
		}
	}

	public void play() {
		cpu.play();
	}

	public void stop() {
		cpu.stop();
	}

	public void addLidar(int degrees) {
		Lidar lidar = new Lidar(this, degrees);
		lidars.add(lidar);
		cpu.addFunction(lidar::getSimulationDistance);
	}

	public Point getPointOnMap() {
		Point opticalSensorLocation = getOpticalSensorLocation();
		return new Point(opticalSensorLocation.x + startPoint.x, opticalSensorLocation.y + startPoint.y);
	}


	public double getSpeed() {
		return speed;
	}

	public void update(int deltaTime) {
		double distancedMoved = (speed * 100) * ((double) deltaTime / 1000);
		pointFromStart = Tools.getPointByDistance(pointFromStart, rotation, distancedMoved);

		double noiseToDistance = Tools.noiseBetween(WorldParams.min_motion_accuracy, WorldParams.max_motion_accuracy, false);
		sensorOpticalFlow = Tools.getPointByDistance(sensorOpticalFlow, rotation, distancedMoved * noiseToDistance);

		double noiseToRotation = Tools.noiseBetween(WorldParams.min_rotation_accuracy, WorldParams.max_rotation_accuracy, false);
		double milli_per_minute = 60000;
		gyroRotation += (1 - noiseToRotation) * deltaTime / milli_per_minute;
		gyroRotation = formatRotation(gyroRotation);

		// Update sensor data
		updateSensors(deltaTime);

		// Update control outputs
		updateControlOutputs(deltaTime);
	}

	public static double formatRotation(double rotationValue) {
		rotationValue %= 360;
		if (rotationValue < 0) {
			rotationValue = 360 - rotationValue;
		}
		return rotationValue;
	}

	public double getRotation() {
		return rotation;
	}

	public double getGyroRotation() {
		return gyroRotation;
	}

	public Point getOpticalSensorLocation() {
		return new Point(sensorOpticalFlow);
	}

	public void rotateLeft(int deltaTime) {
		double rotationChanged = WorldParams.rotation_per_second * deltaTime / 1000;

		rotation += rotationChanged;
		rotation = formatRotation(rotation);

		gyroRotation += rotationChanged;
		gyroRotation = formatRotation(gyroRotation);
	}

	public void rotateRight(int deltaTime) {
		double rotationChanged = -WorldParams.rotation_per_second * deltaTime / 1000;

		rotation += rotationChanged;
		rotation = formatRotation(rotation);

		gyroRotation += rotationChanged;
		gyroRotation = formatRotation(gyroRotation);
	}

	public void moveTowards(Point target, int deltaTime) {
		Point currentLocation = getOpticalSensorLocation();
		double distance = Tools.getDistanceBetweenPoints(currentLocation, target);
		double rotation = Tools.getRotationToTarget(getGyroRotation(), currentLocation, target);

		// Adjust the drone's direction towards the target
		if (rotation > 10) {
			rotateLeft(deltaTime);
		} else if (rotation < -10) {
			rotateRight(-deltaTime);
		}

		// Move the drone forward if it is facing the target direction
		if (Math.abs(rotation) < 10) {
			moveForward(distance);
		}
	}

	public void moveForward(double distance) {
		double distancedMoved = distance;
		pointFromStart = Tools.getPointByDistance(pointFromStart, rotation, distancedMoved);

		double noiseToDistance = Tools.noiseBetween(WorldParams.min_motion_accuracy, WorldParams.max_motion_accuracy, false);
		sensorOpticalFlow = Tools.getPointByDistance(sensorOpticalFlow, rotation, distancedMoved * noiseToDistance);
	}

	public void speedUp(int deltaTime) {
		speed += (WorldParams.accelerate_per_second * deltaTime / 1000);
		if (speed > WorldParams.max_speed) {
			speed = WorldParams.max_speed;
		}
	}

	public void slowDown(int deltaTime) {
		speed -= (WorldParams.accelerate_per_second * deltaTime / 1000);
		if (speed < 0) {
			speed = 0;
		}
	}

	boolean initPaint = false;
	BufferedImage mImage;

	int j = 0;

	public void paint(Graphics g) {
		if (!initPaint) {
			try {
				File f = new File(drone_img_path);
				mImage = ImageIO.read(f);
				initPaint = true;
			} catch (Exception ex) {
				// Handle exception
			}
		}
		// Set the color for the drone
		g.setColor(color);
		// Draw the drone image or representation
		// Point p = getPointOnMap();
		// g.drawImage(mImage, p.getX(), p.getY(), mImage.getWidth(), mImage.getHeight());

		for (int i = 0; i < lidars.size(); i++) {
			Lidar lidar = lidars.get(i);
			lidar.paint(g);
		}
	}

	public String getInfoHTML() {
		DecimalFormat df = new DecimalFormat("#.####");

		String info = "<html>";
		info += "Rotation: " + df.format(rotation) + "<br>";
		info += "Location: " + pointFromStart + "<br>";
		info += "gyroRotation: " + df.format(gyroRotation) + "<br>";
		info += "sensorOpticalFlow: " + sensorOpticalFlow + "<br>";
		info += "BatteryLevel:" + getBatteryLevel() + "<br>";
		info += "Yaw:" + df.format(yaw) + "<br>";
		info += "pitch: " + df.format(pitch) + "<br>";
		info += "roll: " + df.format(roll) + "br";
		info += "Vy: " + df.format(Vy) + "<br>";
		info += "Vx: " + df.format(Vx) + "<br>";
		info += "accX: " + df.format(accX) + "<br>";
		info += "accY: " + df.format(accY) + "<br>";

		info += "</html>";
		return info;
	}

	public long getBatteryLevel() {
		return (long) this.batteryLevel;
	}

	public void calculateBatteryTime() {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = (currentTime - startTime) / 1000; // elapsed time in seconds
		int maxFlightTime = 480; // 8 minutes in seconds

		int remainingTime = maxFlightTime - (int) elapsedTime;
		int batteryLevel = (int) ((remainingTime / (double) maxFlightTime) * 100);
		this.batteryLevel = Math.max(batteryLevel, 0); // Ensure it doesn't go below 0
	}

	// Methods for updating and calculating sensor data
	public void updateSensors(int deltaTime) {
		// Update yaw, pitch, roll based on gyroRotation
		yaw = gyroRotation;
		pitch = calculatePitch();
		roll = calculateRoll();

		// Update velocities based on speed and direction
		Vx = speed * Math.cos(Math.toRadians(rotation));
		Vy = speed * Math.sin(Math.toRadians(rotation));

		// Update accelerations
		accX = calculateAccelerationX(deltaTime);
		accY = calculateAccelerationY(deltaTime);

		// Update previous velocities and time for next acceleration calculation
		prevVx = Vx;
		prevVy = Vy;
		prevTime = System.currentTimeMillis();
	}

	// Implement sensor calculation logic
	private double calculatePitch() {
		return Math.toDegrees(Math.atan2(Vy, Vx));
	}

	private double calculateRoll() {
		return Math.toDegrees(Math.atan2(accY, accX));
	}
	public Point getVelocity() {
		return new Point(Vx, Vy);
	}

	public void setVelocity(double velocityX, double velocityY) {
		this.Vx = velocityX;
		this.Vy = velocityY;
	}

	public void setEstimatedPosition(Point position, double orientation) {
		this.estimatedPosition = position;
		this.estimatedOrientation = orientation;
	}

	public Point getEstimatedPosition() {
		return estimatedPosition;
	}

	public double getEstimatedOrientation() {
		return estimatedOrientation;
	}


	private double calculateAccelerationX(int deltaTime) {
		return (Vx / deltaTime) * 1000;
	}

	private double calculateAccelerationY(int deltaTime) {
		return (Vy / deltaTime) * 1000;
	}

	private void updateControlOutputs(int deltaTime) {
		double dt = deltaTime / 1000.0;

		// Calculate control outputs using PID controllers
		double pitchControl = pitchPID.update(desiredPitch - pitch, dt);
		double rollControl = rollPID.update(desiredRoll - roll, dt);
		double yawControl = yawPID.update(desiredYaw - yaw, dt);

		DecimalFormat df = new DecimalFormat("#.####");

		String info = "<html>";
		info += "Pitch Control: " + pitchControl + "<br>";
		info += "Roll Control: " + rollControl + "<br>";
		info += "Yaw Control: " + yawControl + "<br>";

		SimulationWindow.getInfo_label_Of_PID().setText(info);
	}
}
