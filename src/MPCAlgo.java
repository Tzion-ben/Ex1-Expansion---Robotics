import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

public class MPCAlgo implements Algo {

    int map_size = 3000;
    int max_risky_distance = 150;

    public enum PixelState {
        blocked, explored, unexplored, visited
    }

    PixelState[][] map;
    Drone drone;
    Point droneStartingPoint;
    ArrayList<Point> points;

    int isRotating;
    ArrayList<Double> degrees_left;
    ArrayList<Func> degrees_left_func;

    boolean isSpeedUp = false;

    GraphMine mGraph = new GraphMine();
    CPU ai_cpu;

    PriorityQueue<Point> unexploredPointsQueue;

    boolean is_risky = false;
    double risky_distance = 0;
    double lastGyroRotation = 0;
    double max_distance_between_points = 100;

    public MPCAlgo(Map realMap, DroneType droneType, Color color) {
        degrees_left = new ArrayList<>();
        degrees_left_func = new ArrayList<>();
        points = new ArrayList<>();

        drone = new Drone(realMap, droneType, color);

        initMap(realMap);
        isRotating = 0;
        ai_cpu = new CPU(200, "Auto_AI");
        ai_cpu.addFunction(this::update);

        unexploredPointsQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> getDistanceToDrone(p)));
    }

    public GraphMine getMGraph() {
        return mGraph;
    }

    public void initMap(Map realMap) {
        map = new PixelState[map_size][map_size];
        for (int i = 0; i < map_size; i++) {
            for (int j = 0; j < map_size; j++) {
                map[i][j] = PixelState.unexplored;
            }
        }

        droneStartingPoint = new Point(map_size / 2, map_size / 2);
    }

    public void play() {
        drone.play();
        ai_cpu.play();
    }

    public void update(int deltaTime) {
        updateVisited();
        updateMapByLidars();
        ai(deltaTime);

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
        Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x, dronePoint.y + droneStartingPoint.y);

        for (int i = 0; i < drone.lidars.size(); i++) {
            Lidar lidar = drone.lidars.get(i);
            double rotation = drone.getGyroRotation() + lidar.degrees;
            for (int distanceInCM = 0; distanceInCM < lidar.current_distance; distanceInCM++) {
                Point p = Tools.getPointByDistance(fromPoint, rotation, distanceInCM);
                setPixel(p.x, p.y, PixelState.explored);
            }

            if (lidar.current_distance > 0 && lidar.current_distance < WorldParams.lidarLimit - WorldParams.lidarNoise) {
                Point p = Tools.getPointByDistance(fromPoint, rotation, lidar.current_distance);
                setPixel(p.x, p.y, PixelState.blocked);
            }
        }
    }

    public void updateVisited() {
        Point dronePoint = drone.getOpticalSensorLocation();
        Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x, dronePoint.y + droneStartingPoint.y);

        setPixel(fromPoint.x, fromPoint.y, PixelState.visited);
    }

    @Override
    public void setPixel(double x, double y, AutoAlgo1.PixelState state) {

    }

    @Override
    public void setPixel(double x, double y, PixelState state) {
        int xi = (int) x;
        int yi = (int) y;

        if (state == PixelState.visited) {
            map[xi][yi] = state;
            return;
        }

        if (map[xi][yi] == PixelState.unexplored) {
            map[xi][yi] = state;
            unexploredPointsQueue.add(new Point(xi, yi));
        }
    }



    public void paintBlindMap(Graphics g) {
        Color c = g.getColor();

        int i = (int) droneStartingPoint.y - (int) drone.startPoint.x;
        int startY = i;
        for (; i < map_size; i++) {
            int j = (int) droneStartingPoint.x - (int) drone.startPoint.y;
            int startX = j;
            for (; j < map_size; j++) {
                if (map[i][j] != PixelState.unexplored) {
                    if (map[i][j] == PixelState.blocked) {
                        g.setColor(Color.RED);
                    } else if (map[i][j] == PixelState.explored) {
                        g.setColor(Color.WHITE);
                    } else if (map[i][j] == PixelState.visited) {
                        g.setColor(Color.BLUE);
                    }
                    g.drawLine(i - startY, j - startX, i - startY, j - startX);
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

    public void updateRotating(int deltaTime) {
        if (degrees_left.isEmpty()) {
            return;
        }

        double degrees_left_to_rotate = degrees_left.get(0);
        boolean isLeft = degrees_left_to_rotate > 0;
        double curr = drone.getGyroRotation();
        double just_rotated = isLeft ? curr - lastGyroRotation : curr - lastGyroRotation;

        lastGyroRotation = curr;
        degrees_left_to_rotate -= just_rotated;
        degrees_left.set(0, degrees_left_to_rotate);

        if ((isLeft && degrees_left_to_rotate >= 0) || (!isLeft && degrees_left_to_rotate <= 0)) {
            degrees_left.remove(0);
            Func func = degrees_left_func.remove(0);
            if (func != null) {
                func.method();
            }
            if (degrees_left.isEmpty()) {
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
        lastGyroRotation = drone.getGyroRotation();
        if (isFirst) {
            degrees_left.add(0, degrees);
            degrees_left_func.add(0, null);
        } else {
            degrees_left.add(degrees);
            degrees_left_func.add(null);
        }
        isRotating = 1;
    }

    public void spinBy(double degrees) {
        lastGyroRotation = drone.getGyroRotation();
        degrees_left.add(degrees);
        degrees_left_func.add(null);
        isRotating = 1;
    }

    public Point getLastPoint() {
        if (points.isEmpty()) {
            return droneStartingPoint;
        }
        return points.get(points.size() - 1);
    }

    public Point removeLastPoint() {
        if (points.isEmpty()) {
            return droneStartingPoint;
        }
        return points.remove(points.size() - 1);
    }

    public Point getAvgLastPoint() {
        if (points.size() < 2) {
            return droneStartingPoint;
        }
        Point p1 = points.get(points.size() - 1);
        Point p2 = points.get(points.size() - 2);
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    private double getDistanceToDrone(Point p) {
        Point dronePoint = drone.getOpticalSensorLocation();
        Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x, dronePoint.y + droneStartingPoint.y);
        return Tools.getDistanceBetweenPoints(fromPoint, p);
    }

    public void ai(int deltaTime) {
        if (!SimulationWindow.toogleAI) {
            return;
        }

        if (unexploredPointsQueue.isEmpty()) {
            return; // No unexplored points to navigate to
        }

        Point nextTarget = unexploredPointsQueue.peek();
        double distance = getDistanceToDrone(nextTarget);
        if (distance < max_distance_between_points) {
            unexploredPointsQueue.poll(); // Remove the point if it is within the range
            return;
        }

        // Implement A* algorithm to navigate towards the next target
        List<Point> path = aStarPathfinding(drone.getOpticalSensorLocation(), nextTarget);

        if (!path.isEmpty()) {
            Point nextMove = path.get(0);
            double targetRotation = Tools.getRotationToTarget(drone.getGyroRotation(), drone.getOpticalSensorLocation(), nextMove);
            spinBy(targetRotation);
            drone.moveTowards(nextMove , deltaTime);
        }
    }

    private List<Point> aStarPathfinding(Point start, Point goal) {
        Set<Point> closedSet = new HashSet<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        java.util.Map<Point, Point> cameFrom = new HashMap<>();
        java.util.Map<Point, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        openSet.add(new Node(start, 0, getHeuristic(start, goal)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.point.equals(goal)) {
                return reconstructPath(cameFrom, current.point);
            }

            closedSet.add(current.point);

            for (Point neighbor : getNeighbors(current.point)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeGScore = gScore.getOrDefault(current.point, Double.POSITIVE_INFINITY) + getDistanceBetween(current.point, neighbor);

                if (!openSet.contains(new Node(neighbor, 0, 0))) {
                    openSet.add(new Node(neighbor, tentativeGScore, getHeuristic(neighbor, goal)));
                } else if (tentativeGScore >= gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    continue;
                }

                cameFrom.put(neighbor, current.point);
                gScore.put(neighbor, tentativeGScore);
            }
        }

        return new ArrayList<>(); // Return an empty path if no path is found
    }

    private List<Point> reconstructPath(java.util.Map<Point, Point> cameFrom, Point current) {
        List<Point> totalPath = new ArrayList<>();
        totalPath.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }

        Collections.reverse(totalPath);
        return totalPath;
    }

    private double getHeuristic(Point a, Point b) {
        return Tools.getDistanceBetweenPoints(a, b);
    }

    private double getDistanceBetween(Point a, Point b) {
        return Tools.getDistanceBetweenPoints(a, b);
    }

    private List<Point> getNeighbors(Point point) {
        List<Point> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = (int)point.x + dx[i];
            int ny = (int)point.y + dy[i];

            if (nx >= 0 && ny >= 0 && nx < map_size && ny < map_size && map[nx][ny] != PixelState.blocked) {
                neighbors.add(new Point(nx, ny));
            }
        }

        return neighbors;
    }

    private static class Node {
        Point point;
        double gScore;
        double fScore;

        Node(Point point, double gScore, double fScore) {
            this.point = point;
            this.gScore = gScore;
            this.fScore = gScore + fScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return point.equals(node.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
    }


    public boolean isIs_risky() {
        for (Lidar lidar : drone.lidars) {
            if (lidar.current_distance < max_risky_distance) {
                is_risky = true;
                risky_distance = lidar.current_distance;
                return true;
            }
        }
        is_risky = false;
        return false;
    }

    public double getRisky_dis() {
        return risky_distance;
    }

    public Drone getDrone() {
        return drone;
    }
}
