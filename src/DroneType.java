import java.util.List;

public class DroneType {
    public String name;
    public List<Integer> lidarOrientations;

    public DroneType(String name, List<Integer> lidarOrientations) {
        this.name = name;
        this.lidarOrientations = lidarOrientations;
    }
}
