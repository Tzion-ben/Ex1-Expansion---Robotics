import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class SimulationWindow {

    private JFrame frame;
    private Sound onSound;
    private Sound offSound;
    private Sound returnHomeSound;

    private JButton firstDrone;
    private JButton secondDrone;
    private JButton thirdDrone;


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SimulationWindow window = new SimulationWindow();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public SimulationWindow() {
        initialize();
    }

    public static JLabel info_label;
    public static JLabel info_label_Of_PID;
    public static boolean return_home = false;
    boolean toogleStop = true;

    private List<Algo> drones;

    private void initialize() {
        onSound = new Sound("Voices\\on.wav");
        offSound = new Sound("Voices\\OFF.wav");
        returnHomeSound = new Sound("Voices\\RETURN HOME.wav");
        frame = new JFrame();
        frame.setSize(1800, 1000);
        frame.setTitle("Drone Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);


        /*
         * Stop/Resume
         */
        JButton stopBtn = new JButton("Start/Pause");
        stopBtn.addActionListener(e -> {
            if (toogleStop) {
                drones.forEach(drone -> drone.getDrone().calculateBatteryTime());
                CPU.stopAllCPUS();
            } else {
                CPU.resumeAllCPUS();
            }
            toogleStop = !toogleStop;
        });
        stopBtn.setBounds(1300, 0, 170, 50);
        frame.getContentPane().add(stopBtn);

        /*
         * Speeds
         */
        JButton speedBtn1 = new JButton("speedUp");
        speedBtn1.addActionListener(e -> drones.forEach(Algo::speedUp));
        speedBtn1.setBounds(1300, 100, 100, 50);
        frame.getContentPane().add(speedBtn1);

        JButton speedBtn2 = new JButton("speedDown");
        speedBtn2.addActionListener(e -> drones.forEach(Algo::speedDown));
        speedBtn2.setBounds(1400, 100, 100, 50);
        frame.getContentPane().add(speedBtn2);

        /*
         * Spins
         */
        JButton spinBtn1 = new JButton("spin180");
        spinBtn1.addActionListener(e -> drones.forEach(drone -> drone.spinBy(180)));
        spinBtn1.setBounds(1300, 200, 100, 50);
        frame.getContentPane().add(spinBtn1);

        JButton spinBtn2 = new JButton("spin90");
        spinBtn2.addActionListener(e -> drones.forEach(drone -> drone.spinBy(90)));
        spinBtn2.setBounds(1400, 200, 100, 50);
        frame.getContentPane().add(spinBtn2);

        JButton spinBtn3 = new JButton("spin60");
        spinBtn3.addActionListener(e -> drones.forEach(drone -> drone.spinBy(60)));
        spinBtn3.setBounds(1500, 200, 100, 50);
        frame.getContentPane().add(spinBtn3);

        JButton spinBtn4 = new JButton("spin45");
        spinBtn4.addActionListener(e -> drones.forEach(drone -> drone.spinBy(45)));
        spinBtn4.setBounds(1300, 300, 100, 50);
        frame.getContentPane().add(spinBtn4);

        JButton spinBtn5 = new JButton("spin30");
        spinBtn5.addActionListener(e -> drones.forEach(drone -> drone.spinBy(30)));
        spinBtn5.setBounds(1400, 300, 100, 50);
        frame.getContentPane().add(spinBtn5);

        JButton spinBtn6 = new JButton("spin-30");
        spinBtn6.addActionListener(e -> drones.forEach(drone -> drone.spinBy(-30)));
        spinBtn6.setBounds(1500, 300, 100, 50);
        frame.getContentPane().add(spinBtn6);

        JButton spinBtn7 = new JButton("spin-45");
        spinBtn7.addActionListener(e -> drones.forEach(drone -> drone.spinBy(-45)));
        spinBtn7.setBounds(1600, 300, 100, 50);
        frame.getContentPane().add(spinBtn7);

        JButton spinBtn8 = new JButton("spin-60");
        spinBtn8.addActionListener(e -> drones.forEach(drone -> drone.spinBy(-60)));
        spinBtn8.setBounds(1700, 300, 100, 50);
        frame.getContentPane().add(spinBtn8);

        /*
         * Toggle real map
         */
        JButton toggleMapBtn = new JButton("Toggle Map");
        toggleMapBtn.addActionListener(e -> toogleRealMap = !toogleRealMap);
        toggleMapBtn.setBounds(1300, 400, 120, 50);
        frame.getContentPane().add(toggleMapBtn);

        /*
         * Toggle AI
         */
        JButton toggleAIBtn = new JButton("Toggle AI");
        toggleAIBtn.addActionListener(e -> toogleAI = !toogleAI);
        toggleAIBtn.setBounds(1400, 400, 120, 50);
        frame.getContentPane().add(toggleAIBtn);

        /*
         * Return to Home
         */
        JButton returnBtn = new JButton("Return Home");
        returnBtn.addActionListener(e -> {
            returnHomeSound.play();
            return_home = !return_home;
            drones.forEach(drone -> {
                drone.speedDown();
                drone.spinBy(180, true, drone::speedUp);
            });
        });
        returnBtn.setBounds(1500, 400, 120, 50);
        frame.getContentPane().add(returnBtn);

        JButton Graph = new JButton("Open Graph");
        Graph.addActionListener(e -> drones.forEach(drone -> drone.getMGraph().drawGraph()));
        Graph.setBounds(1600, 400, 120, 50);
        frame.getContentPane().add(Graph);

        /*
         * Info label
         */
        info_label = new JLabel();
        info_label.setBounds(1300, 500, 300, 200);
        frame.getContentPane().add(info_label);

        info_label_Of_PID = new JLabel();
        info_label_Of_PID.setBounds(100, 500, 300, 200);
        frame.getContentPane().add(info_label_Of_PID);

        info_label2 = new JLabel();
        info_label2.setBounds(1500, 450, 300, 200);
        frame.getContentPane().add(info_label2);

        /*
         * Choose Map Button
         */
        chooseMap();

        JButton chooseMapBtn = new JButton("Choose Map");
        chooseMapBtn.setBounds(1300, 50, 120, 50);
        frame.getContentPane().add(chooseMapBtn);

        main();
    }

    public JLabel info_label2;
    public static boolean toogleRealMap = true;
    public static boolean toogleAI = false;

    public void main() {
        try {
            if (drones != null) {
                Painter painter = new Painter(drones);
                painter.setBounds(0, 0, 2000, 2000);
                frame.getContentPane().add(painter);
                frame.getContentPane().revalidate(); // Add this to ensure layout is updated

                CPU painterCPU = new CPU(200, "painter"); // 60 FPS painter
                painterCPU.addFunction(frame::repaint);
                painterCPU.play();
            }

            drones.forEach(Algo::play);

            if (drones != null) {
                CPU updatesCPU = new CPU(60, "updates");
                updatesCPU.addFunction((e) -> drones.forEach(drone -> drone.getDrone().update(20)));
                updatesCPU.play();

                CPU infoCPU = new CPU(6, "update_info");
                infoCPU.addFunction(this::updateInfo);
                infoCPU.play();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Please choose a proper Map: " + e.getMessage());
            chooseMap();
            main();
        }
    }

    public void chooseMap() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "PNG Images (*.png)";
            }
        });

        while (true) {
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".png")) {
                    try {
                        Point startPoint = new Point(100, 50);
                        Map map = new Map(selectedFile.getAbsolutePath(), startPoint);
                        //DroneType droneType1 = new DroneType("Type 1", Arrays.asList(0, 60, -60));
                        DroneType droneType2 = new DroneType("Type 2", Arrays.asList(0, 80, -80));
                        //DroneType droneType3 = new DroneType("Type 3", Arrays.asList(0, 70, -70));
                        drones = Arrays.asList(
                                //new AutoAlgo1(map, droneType1, Color.BLUE)
                                new MPCAlgo(map, droneType2,Color.RED)
                                //new AutoAlgo1(map, droneType3,Color.BLACK)
                        );
                        break; // Exit the loop if map is successfully loaded
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, "Error loading map file: " + e.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid file. Please select a PNG file.");
                }
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.exit(0);
            }
        }
    }

    public void updateInfo(int deltaTime) {
        StringBuilder infoBuilder = new StringBuilder("<html>");
        for (Algo drone : drones) {
            infoBuilder.append(drone.getDrone().getInfoHTML()).append("<br>");
        }
        infoBuilder.append("</html>");
        info_label.setText(infoBuilder.toString());
        info_label2.setText("<html>isRisky:<br>");
        for (Algo drone : drones) {
            info_label2.setText(info_label2.getText() + String.valueOf(drone.isIs_risky()) + "<br>" + String.valueOf(drone.getRisky_dis()) + "<br>");
        }
        info_label2.setText(info_label2.getText() + "</html>");
    }

    public static JLabel getInfo_label_Of_PID() {
        return info_label_Of_PID;
    }
}
