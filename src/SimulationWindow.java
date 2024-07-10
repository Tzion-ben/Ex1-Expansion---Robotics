import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.geom.Point2D;


public class SimulationWindow {

    private JFrame frame;
    private Sound onSound;
    private Sound offSound;
    private Sound returnHomeSound;

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

    private List<Algo> drones = new ArrayList<>();

    private JButton firstDroneBtn  = new JButton("First Drone");
    private JButton secondDroneBtn  = new JButton("Second Drone");
    private JButton thirdDroneBtn  = new JButton("Third Drone");
    private JButton StartOver = new JButton("Start Over");

    private void initialize() {

        JButton returnBtn = new JButton("Return Home");

        onSound = new Sound("Voices\\on.wav");
        offSound = new Sound("Voices\\OFF.wav");
        returnHomeSound = new Sound("Voices\\RETURN HOME.wav");
        frame = new JFrame();
        frame.setSize(1150, 650);
        frame.setTitle("Drone Simulator");
        frame.setLocationRelativeTo(null);
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

            returnBtn.setVisible(true);
        });
        stopBtn.setBackground(Color.BLUE);
        stopBtn.setBounds(925, 0, 120, 30);
        frame.getContentPane().add(stopBtn);

        /*
         * Speeds
         */
        JButton speedBtn1 = new JButton("speedUp");
        speedBtn1.addActionListener(e -> drones.forEach(Algo::speedUp));
        speedBtn1.setBackground(Color.GREEN);
        speedBtn1.setBounds(850, 45, 120, 30);
        frame.getContentPane().add(speedBtn1);

        JButton speedBtn2 = new JButton("speedDown");
        speedBtn2.addActionListener(e -> drones.forEach(Algo::speedDown));
        speedBtn2.setBackground(Color.GREEN);
        speedBtn2.setBounds(1000, 45, 120, 30);
        frame.getContentPane().add(speedBtn2);

        /*
         * Spins
         */
        JButton spinBtn1 = new JButton("spin180");
        spinBtn1.addActionListener(e -> drones.forEach(drone -> drone.spinBy(180)));
        spinBtn1.setBackground(Color.GREEN);
        spinBtn1.setBounds(850, 90, 120, 30);
        frame.getContentPane().add(spinBtn1);

        JButton spinBtn2 = new JButton("spin90");
        spinBtn2.addActionListener(e -> drones.forEach(drone -> drone.spinBy(90)));
        spinBtn2.setBackground(Color.GREEN);
        spinBtn2.setBounds(1000, 90, 120, 30);
        frame.getContentPane().add(spinBtn2);

        JButton spinBtn3 = new JButton("spin60");
        spinBtn3.addActionListener(e -> drones.forEach(drone -> drone.spinBy(60)));
        spinBtn3.setBackground(Color.GREEN);
        spinBtn3.setBounds(850, 125, 120, 30);
        frame.getContentPane().add(spinBtn3);

        JButton spinBtn4 = new JButton("spin45");
        spinBtn4.addActionListener(e -> drones.forEach(drone -> drone.spinBy(45)));
        spinBtn4.setBackground(Color.GREEN);
        spinBtn4.setBounds(1000, 125, 120, 30);
        frame.getContentPane().add(spinBtn4);

        JButton spinBtn5 = new JButton("spin30");
        spinBtn5.addActionListener(e -> drones.forEach(drone -> drone.spinBy(30)));
        spinBtn5.setBackground(Color.GREEN);
        spinBtn5.setBounds(850, 160, 120, 30);
        frame.getContentPane().add(spinBtn5);

        JButton spinBtn6 = new JButton("spin-30");
        spinBtn6.addActionListener(e -> drones.forEach(drone -> drone.spinBy(-30)));
        spinBtn6.setBackground(Color.GREEN);
        spinBtn6.setBounds(1000, 160, 120, 30);
        frame.getContentPane().add(spinBtn6);

        JButton spinBtn7 = new JButton("spin-45");
        spinBtn7.addActionListener(e -> drones.forEach(drone -> drone.spinBy(-45)));
        spinBtn7.setBackground(Color.GREEN);
        spinBtn7.setBounds(850, 195, 120, 30);
        frame.getContentPane().add(spinBtn7);

        JButton spinBtn8 = new JButton("spin-60");
        spinBtn8.addActionListener(e -> drones.forEach(drone -> drone.spinBy(-60)));
        spinBtn8.setBackground(Color.GREEN);
        spinBtn8.setBounds(1000, 195, 120, 30);
        frame.getContentPane().add(spinBtn8);

        /*
         * Toggle real map
         */
        JButton toggleMapBtn = new JButton("Toggle Map");
        toggleMapBtn.addActionListener(e -> toogleRealMap = !toogleRealMap);
        toggleMapBtn.setBackground(Color.ORANGE);
        toggleMapBtn.setBounds(850, 240, 120, 30);
        frame.getContentPane().add(toggleMapBtn);

        /*
         * Toggle AI
         */
        JButton toggleAIBtn = new JButton("Toggle AI");
        toggleAIBtn.addActionListener(e -> {
            returnBtn.setVisible(!returnBtn.isVisible());
            toogleAI = !toogleAI;
        });
        toggleAIBtn.setBackground(Color.ORANGE);
        toggleAIBtn.setBounds(1000, 240, 120, 30);
        frame.getContentPane().add(toggleAIBtn);

        /*
         * Return to Home
         */

        returnBtn.addActionListener(e -> {
            returnHomeSound.play();
            return_home = !return_home;
            drones.forEach(drone -> {
                drone.speedDown();
                drone.spinBy(180, true, drone::speedUp);
            });
        });
        returnBtn.setVisible(false);
        returnBtn.setBounds(850, 285, 120, 30);
        returnBtn.setBackground(Color.yellow);
        frame.getContentPane().add(returnBtn);

        JButton Graph = new JButton("Open Graph");
        Graph.addActionListener(e -> drones.forEach(drone -> drone.getMGraph().drawGraph()));
        Graph.setBackground(Color.yellow);
        Graph.setBounds(1000, 285, 120, 30);
        frame.getContentPane().add(Graph);

        StartOver.addActionListener(e -> {
            enableOther();
            frame.dispose();
            String[] anotherClassArgs = {"arg1", "arg2"};
            SimulationWindow.main(anotherClassArgs);
        });

        StartOver.setBounds(926, 380, 120, 40);
        StartOver.setVisible(false);
        frame.getContentPane().add(StartOver);

        JButton PerformanceAnalysis = new JButton("Performance Analysis");
        PerformanceAnalysis.addActionListener(e -> {
            String csvFile = "drone_log.csv";
            String outputCsvFile = "drone_area_output.csv";
            List<Point2D> points = DroneAreaCalculator.readCSV(csvFile);

            if (points.size() > 2) {
                double area = DroneAreaCalculator.calculateArea(points);
                System.out.println("The area covered by the drone is: " + area + " square units");
                DroneAreaCalculator.writeAreaToCSV(outputCsvFile, area);
            }
        });
        PerformanceAnalysis.setVisible(true);
        PerformanceAnalysis.setBounds(850, 480, 120, 30);
        frame.getContentPane().add(PerformanceAnalysis);

        /*
         * Info label
         */
        info_label = new JLabel();
        info_label.setBounds(870, 400, 250, 200);
        frame.getContentPane().add(info_label);

        info_label_Of_PID = new JLabel();
        info_label_Of_PID.setBounds(100, 450, 250, 200);
        frame.getContentPane().add(info_label_Of_PID);

        info_label2 = new JLabel();
        info_label2.setBounds(530, 450, 200, 200);
        frame.getContentPane().add(info_label2);

        /*
         * Choose Map Button
         */
        chooseMap();

//        JButton chooseMapBtn = new JButton("Choose Map");
//        chooseMapBtn.setBounds(1300, 50, 120, 50);
//        frame.getContentPane().add(chooseMapBtn);

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
//                        Point startPoint = new Point(50, 50);
                        Map map = new Map(selectedFile.getAbsolutePath());

                        int [] arrPosStart = map.startPos();
//                        map.set_drone_start_point(new Point (arrPosStart[0] , arrPosStart[1]));
                        List <ChoosenDronelgo> choosenTypes = chooseDrones(map);

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


    //will wat for that user to click and choose which drone let to fly
    private List<ChoosenDronelgo> chooseDrones(Map map) {
        List<ChoosenDronelgo> choosenDrones = new ArrayList<>();

        firstDroneBtn.addActionListener(e -> {
            ChoosenDronelgo resul = chooseAlgo(1);

            DroneType droneType1 = new DroneType("Type 1", Arrays.asList(0, 60, -60 , 180));
            if(resul.getAlgoType().equals("AutoAlgo1"))
                drones.add(new AutoAlgo1(map, droneType1, Color.BLUE));
            else
                drones.add(new SLAMAlgo(map, droneType1,Color.BLUE));

            disableOther();
            main();
        });

        secondDroneBtn.addActionListener(e -> {
            ChoosenDronelgo resul = chooseAlgo(2);

            DroneType droneType2 = new DroneType("Type 2", Arrays.asList(0, 90, -90 , 180));
            if(resul.getAlgoType().equals("AutoAlgo1"))
                drones.add(new AutoAlgo1(map, droneType2, Color.RED));
            else
                drones.add(new SLAMAlgo(map, droneType2,Color.RED));

            disableOther();
            main();
        });

        thirdDroneBtn.addActionListener(e -> {
            ChoosenDronelgo resul = chooseAlgo(3);

            DroneType droneType3 = new DroneType("Type 3", Arrays.asList(0, 70, -70 , 180));
            if(resul.getAlgoType().equals("AutoAlgo3"))
                drones.add(new AutoAlgo1(map, droneType3, Color.BLACK));
            else
                drones.add(new SLAMAlgo(map, droneType3,Color.BLACK));

            disableOther();
            main();
        });

        firstDroneBtn.setBackground(Color.RED);
        firstDroneBtn.setBounds(850, 380, 120, 40);
        frame.getContentPane().add(firstDroneBtn);

        secondDroneBtn.setBackground(Color.RED);
        secondDroneBtn.setBounds(1000, 380, 120, 40);
        frame.getContentPane().add(secondDroneBtn);

        thirdDroneBtn.setBackground(Color.RED);
        thirdDroneBtn.setBounds(850, 425, 120, 40);
        frame.getContentPane().add(thirdDroneBtn);


        return choosenDrones;
    }

    private void disableOther() {
        StartOver.setVisible(true);
        firstDroneBtn.setVisible(false);
        secondDroneBtn.setVisible(false);
        thirdDroneBtn.setVisible(false);
    }

    private void enableOther() {
        StartOver.setVisible(false);
        firstDroneBtn.setVisible(true);
        secondDroneBtn.setVisible(true);
        thirdDroneBtn.setVisible(true);
    }

    //will return the algo that choosen and the drone number
    private ChoosenDronelgo chooseAlgo(int droneType) {
        // Options for the JOptionPane
        Object[] options = {"AI Algo", "SLAM Algo"};
        ChoosenDronelgo choosenDronelgo = new ChoosenDronelgo();

        choosenDronelgo.setDroneNum(droneType);
        int choice = JOptionPane.showOptionDialog(frame,
                "Choose an algo:",
                "Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        // Handle the user choice
        if (choice == 0)
            choosenDronelgo.setAlgoType("AutoAlgo1");
         else
            choosenDronelgo.setAlgoType("SLAMAlgo");


         return choosenDronelgo;
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

