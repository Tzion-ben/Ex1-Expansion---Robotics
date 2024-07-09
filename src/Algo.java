import java.awt.*;

public interface Algo {
    void initMap(Map realMap);
    void play();
    void update(int deltaTime);
    void speedUp();
    void speedDown();
    void updateMapByLidars();
    void updateVisited();
    void setPixel(double x, double y, AutoAlgo1.PixelState state);

    void setPixel(double x, double y, SLAMAlgo.PixelState state);

    void setPixel(double x, double y, MPCAlgo.PixelState state);
    void paintBlindMap(Graphics g);
    void paintPoints(Graphics g);
    void paint(Graphics g);
    void spinBy(double degrees) ;
    void spinBy(double degrees, boolean isFirst);
    void spinBy(double degrees, boolean isFirst, Func func);
    public GraphMine getMGraph();
    public boolean isIs_risky();
    public double getRisky_dis();
    public Drone getDrone();
}
