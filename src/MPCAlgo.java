import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MPCAlgo implements Algo {

    // int map_size = 3000;
    public enum PixelState {
        blocked, explored, unexplored, visited
    }

    PixelState[][] map;
    Drone drone;
    Point droneStartingPoint;

    ArrayList<Point> points;

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    double lastGyroRotation;
    int isRotating;
    ArrayList<Double> degrees_left;
    ArrayList<Func> degrees_left_func;

    boolean isSpeedUp = false;

    GraphMine mGraph = new GraphMine();

    CPU ai_cpu;

    public MPCAlgo(Map realMap, DroneType droneType, Color color) {
        degrees_left = new ArrayList<>();
        degrees_left_func = new ArrayList<>();
        points = new ArrayList<>();

        drone = new Drone(realMap, droneType, color);
        initMap(realMap);

        isRotating = 0;
        ai_cpu = new CPU(200, "MPC_AI");
        ai_cpu.addFunction(this::update);
    }

    public GraphMine getMGraph() {
        return mGraph;
    }

    public boolean isIs_risky() {
        return is_risky;
    }

    public double getRisky_dis() {
        return 150;
    }

    public void initMap(Map realMap) {
        map = new PixelState[realMap.startX()][realMap.startY()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (!realMap.getMapIJ(i, j))
                    map[i][j] = PixelState.blocked;
                else
                    map[i][j] = PixelState.unexplored;
            }
        }
        droneStartingPoint = new Point(drone.startPoint);
    }

    public void play() {
        drone.play();
        ai_cpu.play();
    }

    public void update(int deltaTime) {
        updateVisited();
        updateMapByLidars();
        mpcAlgorithm(deltaTime);

        if (isRotating != 0) {
            updateRotating(deltaTime);
        }
        if (isSpeedUp) {
            drone.speedUp(deltaTime);
        } else {
            drone.slowDown(deltaTime);
        }
    }

    public void speedUp() {
        isSpeedUp = true;
    }

    public void speedDown() {
        isSpeedUp = false;
    }

    public void updateMapByLidars() {
        Point dronePoint = drone.getOpticalSensorLocation();
        Point fromPoint = new Point((int) (dronePoint.x + droneStartingPoint.x),
                (int) (dronePoint.y + droneStartingPoint.y));

        for (int i = 0; i < drone.lidars.size(); i++) {
            Lidar lidar = drone.lidars.get(i);
            double rotation = drone.getGyroRotation() + lidar.degrees;

            for (int distanceInCM = 0; distanceInCM < lidar.current_distance; distanceInCM++) {
                Point p = Tools.getPointByDistance(fromPoint, rotation, distanceInCM);
                setPixel((int) p.x, (int) p.y, PixelState.explored);
            }

            if (lidar.current_distance > 0
                    && lidar.current_distance < WorldParams.lidarLimit - WorldParams.lidarNoise) {
                Point p = Tools.getPointByDistance(fromPoint, rotation, lidar.current_distance);
                setPixel((int) p.x, (int) p.y, PixelState.blocked);
            }
        }
    }

    public void updateVisited() {
        Point dronePoint = drone.getOpticalSensorLocation();
        Point fromPoint = new Point((int) (dronePoint.x + droneStartingPoint.x),
                (int) (dronePoint.y + droneStartingPoint.y));
        setPixel((int) fromPoint.x, (int) fromPoint.y, PixelState.visited);
    }

    @Override
    public void setPixel(double x, double y, AutoAlgo1.PixelState state) {
        // This method can be left empty if not used
    }

    @Override
    public void setPixel(double x, double y, PixelState state) {
        int xi = (int) x;
        int yi = (int) y;

        if (xi < 0 || xi >= map.length || yi < 0 || yi >= map[0].length) {
            return;
        }

        if (state == PixelState.visited) {
            map[xi][yi] = state;
            return;
        }

        if (map[xi][yi] == PixelState.unexplored || map[xi][yi] == PixelState.blocked) {
            map[xi][yi] = state;
        }
    }

    public void paintBlindMap(Graphics g) {
        Color c = g.getColor();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != PixelState.unexplored) {
                    if (map[i][j] == PixelState.blocked) {
                        g.setColor(Color.RED);
                    } else if (map[i][j] == PixelState.explored) {
                        g.setColor(Color.YELLOW);
                    } else if (map[i][j] == PixelState.visited) {
                        g.setColor(Color.BLUE);
                    }
                    g.drawLine(i, j, i, j);
                }
            }
        }
        g.setColor(c);
    }

    public void paintPoints(Graphics g) {
        for (Point p : points) {
            g.drawOval((int) p.x + (int) drone.startPoint.x - 10, (int) p.y + (int) drone.startPoint.y - 10, 20, 20);
        }
    }

    public void paint(Graphics g) {
        if (SimulationWindow.toogleRealMap) {
            drone.realMap.paint(g);
        }
        paintBlindMap(g);
        paintPoints(g);
        drone.paint(g);
    }

    boolean is_init = true;
    boolean is_finish = true;
    boolean is_risky = false;
    boolean is_lidars_max = false;
    boolean start_return_home = false;

    Point init_point;

    public void mpcAlgorithm(int deltaTime) {
        if (!SimulationWindow.toogleAI) {
            return;
        }

        if (is_init) {
            speedUp();
            Point dronePoint = drone.getOpticalSensorLocation();
            init_point = new Point(dronePoint);
            points.add(dronePoint);
            mGraph.addVertex(dronePoint);
            is_init = false;
        }

        Point dronePoint = drone.getOpticalSensorLocation();

        if (SimulationWindow.return_home) {
            if (Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) < 100) {
                if (points.size() <= 1 && Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) < 20) {
                    speedDown();
                    if (Tools.getDistanceBetweenPoints(getLastPoint(), drone.startPoint) < 1) {
                        drone.stop();
                        SimulationWindow.return_home = false;
                    }
                } else {
                    removeLastPoint();
                }
            }
        } else {
            if (Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) >= 100) {
                points.add(dronePoint);
                mGraph.addVertex(dronePoint);
            }
        }

        if (!is_risky) {
            Lidar lidar = drone.lidars.get(0);
            if (lidar.current_distance <= 150) {
                is_risky = true;
            }

            Lidar lidar1 = drone.lidars.get(1);
            if (lidar1.current_distance <= 50) {
                is_risky = true;
            }

            Lidar lidar2 = drone.lidars.get(2);
            if (lidar2.current_distance <= 50) {
                is_risky = true;
            }

        } else {
            if (!is_lidars_max) {
                is_lidars_max = true;
                spinBy(90, true, () -> {
                    is_lidars_max = false;
                    is_risky = false;
                });
            }
        }

        // A* Pathfinding logic to find the next point
        Point nextPoint = findNextPoint();
        if (nextPoint != null && !isBlocked(nextPoint)) {
            moveToNextPoint(nextPoint, deltaTime);
        } else {
            handleNoPathFound();
        }
    }

    private Point findNextPoint() {
        // Implement A* algorithm to find the next point
        return pathfindingAlgorithm();
    }

    private Point pathfindingAlgorithm() {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        boolean[][] closedList = new boolean[map.length][map[0].length];
        Point startPoint = new Point((int) (droneStartingPoint.x + drone.getOpticalSensorLocation().x),
                (int) (droneStartingPoint.y + drone.getOpticalSensorLocation().y));
        Node startNode = new Node(startPoint, null, 0, heuristic(startPoint));
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            closedList[(int) current.point.x][(int) current.point.y] = true;

            if (map[(int) current.point.x][(int) current.point.y] == PixelState.unexplored) {
                return new Point((int) current.point.x, (int) current.point.y);
            }

            for (Point neighbor : getNeighbors(current.point)) {
                if (neighbor.x >= 0 && neighbor.x < map.length && neighbor.y >= 0 && neighbor.y < map[0].length
                        && !closedList[(int) neighbor.x][(int) neighbor.y]
                        && map[(int) neighbor.x][(int) neighbor.y] != PixelState.blocked) {
                    Node neighborNode = new Node(neighbor, current, current.g + 1, heuristic(neighbor));
                    openList.add(neighborNode);
                }
            }
        }
        return null;
    }

    private double heuristic(Point point) {
        // Heuristic function for A* (Manhattan distance to the goal)
        Point goal = findGoal();
        return Math.abs(point.x - goal.x) + Math.abs(point.y - goal.y);
    }

    private Point findGoal() {
        // Simplified goal finding (you can set a specific goal or use another strategy)
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == PixelState.unexplored && map[i][j] != PixelState.blocked) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(map.length - 1, map[0].length - 1);
    }

    private List<Point> getNeighbors(Point p) {
        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(p.x + 1, p.y));
        neighbors.add(new Point(p.x - 1, p.y));
        neighbors.add(new Point(p.x, p.y + 1));
        neighbors.add(new Point(p.x, p.y - 1));
        return neighbors;
    }

    private void moveToNextPoint(Point nextPoint, int deltaTime) {
        if (isBlocked(nextPoint)) {
            handleNoPathFound();
            return;
        }

        double desiredRotation = calculateDesiredRotation(nextPoint);
        adjustRotation(desiredRotation, deltaTime);

        double desiredSpeed = calculateDesiredSpeed(nextPoint);
        adjustSpeed(desiredSpeed, deltaTime);
    }

    private boolean isBlocked(Point point) {
        int x = (int) point.x;
        int y = (int) point.y;
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
            return true;
        }
        return map[x][y] == PixelState.blocked;
    }

    private double calculateDesiredRotation(Point nextPoint) {
        Point dronePoint = drone.getOpticalSensorLocation();
        double dx = nextPoint.x - dronePoint.x;
        double dy = nextPoint.y - dronePoint.y;
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    private double calculateDesiredSpeed(Point nextPoint) {
        double distance = Tools.getDistanceBetweenPoints(drone.getOpticalSensorLocation(), nextPoint);
        return Math.min(WorldParams.max_speed, distance / 10); // Adjust based on distance or other criteria
    }

    private void adjustRotation(double desiredRotation, int deltaTime) {
        double currentRotation = drone.getGyroRotation();
        double rotationDiff = formatRotation(desiredRotation - currentRotation);
        if (Math.abs(rotationDiff) > 1) { // Adding a threshold to avoid unnecessary small adjustments
            int direction = (rotationDiff > 0) ? 1 : -1;
            if (Math.abs(rotationDiff) > 180) {
                direction = -direction;
            }
            drone.rotateLeft(deltaTime * direction);
        }
    }

    private void adjustSpeed(double desiredSpeed, int deltaTime) {
        double currentSpeed = drone.getSpeed();
        double speedDiff = desiredSpeed - currentSpeed;
        if (speedDiff > 0) {
            drone.speedUp(deltaTime);
        } else if (speedDiff < 0) {
            drone.slowDown(deltaTime);
        }
    }

    private void handleNoPathFound() {
        // Rotate the drone 180 degrees when no path is found
        spinBy(180);
    }

    public void updateRotating(int deltaTime) {
        if (degrees_left.size() == 0) {
            return;
        }

        double degrees_left_to_rotate = degrees_left.get(0);
        boolean isLeft = degrees_left_to_rotate > 0;

        double curr = drone.getGyroRotation();
        double just_rotated = isLeft ? curr - lastGyroRotation : curr - lastGyroRotation;

        if (isLeft) {
            if (just_rotated > 0) {
                just_rotated = -(360 - just_rotated);
            }
        } else {
            if (just_rotated < 0) {
                just_rotated = 360 + just_rotated;
            }
        }

        lastGyroRotation = curr;
        degrees_left_to_rotate -= just_rotated;
        degrees_left.set(0, degrees_left_to_rotate);

        if ((isLeft && degrees_left_to_rotate >= 0) || (!isLeft && degrees_left_to_rotate <= 0)) {
            degrees_left.remove(0);

            Func func = degrees_left_func.get(0);
            if (func != null) {
                func.method();
            }
            degrees_left_func.remove(0);

            if (degrees_left.size() == 0) {
                isRotating = 0;
            }
            return;
        }

        int direction = (int) (degrees_left_to_rotate / Math.abs(degrees_left_to_rotate));
        drone.rotateLeft(deltaTime * direction);
    }

    public void spinBy(double degrees, boolean isFirst, Func func) {
        lastGyroRotation = drone.getGyroRotation();
        if (isFirst) {
            degrees_left.add(0, degrees);
            degrees_left_func.add(0, func);
        } else {
            degrees_left.add(degrees);
            degrees_left_func.add(func);
        }
        isRotating = 1;
    }

    public void spinBy(double degrees, boolean isFirst) {
        spinBy(degrees, isFirst, null);
    }

    public void spinBy(double degrees) {
        spinBy(degrees, false, null);
    }

    public Point getLastPoint() {
        if (points.size() == 0) {
            return init_point;
        }
        return points.get(points.size() - 1);
    }

    public Point removeLastPoint() {
        if (points.isEmpty()) {
            return init_point;
        }
        return points.remove(points.size() - 1);
    }

    private static double formatRotation(double rotationValue) {
        rotationValue %= 360;
        if (rotationValue < 0) {
            rotationValue += 360;
        }
        return rotationValue;
    }

    // Node class for A* algorithm
    private class Node implements Comparable<Node> {
        Point point;
        Node parent;
        double g; // Cost from start to current node
        double h; // Heuristic cost from current node to goal

        public Node(Point point, Node parent, double g, double h) {
            this.point = point;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.g + this.h, other.g + other.h);
        }
    }
}
