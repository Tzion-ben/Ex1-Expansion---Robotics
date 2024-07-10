import java.io.*;
import java.util.*;
import java.awt.geom.Point2D;

public class DroneAreaCalculator {

    public static List<Point2D> readCSV(String csvFile) {
        List<Point2D> points = new ArrayList<>();
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                try {
                    double x = Double.parseDouble(data[1].split("\\)")[0].split("\\(")[1]);
                    double y = Double.parseDouble(data[2].split("\\)")[0].trim());
                    points.add(new Point2D.Double(x, y));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // Skip invalid data points
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }

    public static double calculateArea(List<Point2D> points) {
        List<Point2D> hull = getConvexHull(points);
        return calculatePolygonArea(hull);
    }

    public static List<Point2D> getConvexHull(List<Point2D> points) {
        Collections.sort(points, (p1, p2) -> {
            if (p1.getX() != p2.getX()) {
                return Double.compare(p1.getX(), p2.getX());
            } else {
                return Double.compare(p1.getY(), p2.getY());
            }
        });

        List<Point2D> hull = new ArrayList<>();
        for (int phase = 0; phase < 2; phase++) {
            int start = hull.size();
            for (Point2D p : points) {
                while (hull.size() >= start + 2 && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) <= 0) {
                    hull.remove(hull.size() - 1);
                }
                hull.add(p);
            }
            hull.remove(hull.size() - 1);
            Collections.reverse(points);
        }

        return hull;
    }

    public static double cross(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY()) - (a.getY() - o.getY()) * (b.getX() - o.getX());
    }

    public static double calculatePolygonArea(List<Point2D> points) {
        double area = 0.0;
        for (int i = 0; i < points.size(); i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get((i + 1) % points.size());
            area += p1.getX() * p2.getY() - p2.getX() * p1.getY();
        }
        return Math.abs(area) / 2.0;
    }

    public static void writeAreaToCSV(String csvFile, double area) {
        try (PrintWriter writer = new PrintWriter(new File(csvFile))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Area Covered\n");
            sb.append(area);
            sb.append("\n");
            writer.write(sb.toString());
            System.out.println("Output CSV file created successfully.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
