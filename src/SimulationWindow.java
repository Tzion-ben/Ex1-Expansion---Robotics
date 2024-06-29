import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

public class SimulationWindow {

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SimulationWindow window = new SimulationWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void initialize() {
        frame = new JFrame();
        frame.setSize(1800, 1000);
        frame.setTitle("Drone Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        /*
         * Stop\Resume
         */
        JButton stopBtn = new JButton("Start/Pause");
        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (toogleStop) {
                    algo1.drone.calculateBatteryTime();
                    CPU.stopAllCPUS();
                } else {
                    CPU.resumeAllCPUS();
                }
                toogleStop = !toogleStop;
            }
        });
        stopBtn.setBounds(1300, 0, 170, 50);
        frame.getContentPane().add(stopBtn);

        /*
         * Speeds
         */
        JButton speedBtn1 = new JButton("speedUp");
        speedBtn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.speedUp();
            }
        });
        speedBtn1.setBounds(1300, 100, 100, 50);
        frame.getContentPane().add(speedBtn1);

        JButton speedBtn2 = new JButton("speedDown");
        speedBtn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.speedDown();
            }
        });
        speedBtn2.setBounds(1400, 100, 100, 50);
        frame.getContentPane().add(speedBtn2);

        /*
         * Spins
         */
        JButton spinBtn1 = new JButton("spin180");
        spinBtn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(180);
            }
        });
        spinBtn1.setBounds(1300, 200, 100, 50);
        frame.getContentPane().add(spinBtn1);

        JButton spinBtn2 = new JButton("spin90");
        spinBtn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(90);
            }
        });
        spinBtn2.setBounds(1400, 200, 100, 50);
        frame.getContentPane().add(spinBtn2);

        JButton spinBtn3 = new JButton("spin60");
        spinBtn3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(60);
            }
        });
        spinBtn3.setBounds(1500, 200, 100, 50);
        frame.getContentPane().add(spinBtn3);

        JButton spinBtn4 = new JButton("spin45");
        spinBtn4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(45);
            }
        });
        spinBtn4.setBounds(1300, 300, 100, 50);
        frame.getContentPane().add(spinBtn4);

        JButton spinBtn5 = new JButton("spin30");
        spinBtn5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(30);
            }
        });
        spinBtn5.setBounds(1400, 300, 100, 50);
        frame.getContentPane().add(spinBtn5);

        JButton spinBtn6 = new JButton("spin-30");
        spinBtn6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(-30);
            }
        });
        spinBtn6.setBounds(1500, 300, 100, 50);
        frame.getContentPane().add(spinBtn6);

        JButton spinBtn7 = new JButton("spin-45");
        spinBtn7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(-45);
            }
        });
        spinBtn7.setBounds(1600, 300, 100, 50);
        frame.getContentPane().add(spinBtn7);

        JButton spinBtn8 = new JButton("spin-60");
        spinBtn8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.spinBy(-60);
            }
        });
        spinBtn8.setBounds(1700, 300, 100, 50);
        frame.getContentPane().add(spinBtn8);

        /*
         * Toggle real map
         */
        JButton toggleMapBtn = new JButton("Toggle Map");
        toggleMapBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toogleRealMap = !toogleRealMap;
            }
        });
        toggleMapBtn.setBounds(1300, 400, 120, 50);
        frame.getContentPane().add(toggleMapBtn);

        /*
         * Toggle AI
         */
        JButton toggleAIBtn = new JButton("Toggle AI");
        toggleAIBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toogleAI = !toogleAI;
            }
        });
        toggleAIBtn.setBounds(1400, 400, 120, 50);
        frame.getContentPane().add(toggleAIBtn);

        /*
         * Return to Home
         */
        JButton returnBtn = new JButton("Return Home");
        returnBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                return_home = !return_home;
                algo1.speedDown();
                algo1.spinBy(180, true, new Func() {
                    @Override
                    public void method() {
                        algo1.speedUp();
                    }
                });
            }
        });
        returnBtn.setBounds(1500, 400, 120, 50);
        frame.getContentPane().add(returnBtn);

        JButton Graph = new JButton("Open Graph");
        Graph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                algo1.mGraph.drawGraph();
            }
        });
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
        chooseMapBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        chooseMapBtn.setBounds(1300, 50, 120, 50);
        frame.getContentPane().add(chooseMapBtn);

        main();
    }

    public JLabel info_label2;
    public static boolean toogleRealMap = true;
    public static boolean toogleAI = false;

    public static AutoAlgo1 algo1;

    public void main() {
        // Commenting out the hardcoded map loading
        // int map_num = 5;
        // Point[] startPoints = {
        //         new Point(100, 50),
        //         new Point(50, 60),
        //         new Point(73, 68),
        //         new Point(84, 73),
        //         new Point(92, 100)};

        // Map map = new Map("D:\\Java\\DroneSimulator\\Maps\\p1" + map_num + ".png", startPoints[map_num - 1]);
        // algo1 = new AutoAlgo1(map);

    	try {
    		if(algo1 != null) {
		        Painter painter = new Painter(algo1);
		        painter.setBounds(0, 0, 2000, 2000);
		        frame.getContentPane().add(painter);
		
		        CPU painterCPU = new CPU(200, "painter"); // 60 FPS painter
		        painterCPU.addFunction(frame::repaint);
		        painterCPU.play();
    		}
	
	        algo1.play();
	
	        if(algo1 != null) {
		        CPU updatesCPU = new CPU(60, "updates");
		        updatesCPU.addFunction(algo1.drone::update);
		        updatesCPU.play();
		
		        CPU infoCPU = new CPU(6, "update_info");
		        infoCPU.addFunction(this::updateInfo);
		        infoCPU.play();
    		}
    	}
    	catch (Exception e) {
    		JOptionPane.showMessageDialog(frame, "Please chose a proper Map");
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

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".png")) {
                try {
                    // Assuming the start point is set somewhere else or hardcoded
                    Point startPoint = new Point(100, 50); // Example start point
                    Map map = new Map(selectedFile.getAbsolutePath(), startPoint);
                    algo1 = new AutoAlgo1(map); 
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Error loading map file: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid file. Please select a PNG file.");
            }
        }
        else if(result == JFileChooser.CANCEL_OPTION)
        {
        	System.exit(0);
        }
    }

    public void updateInfo(int deltaTime) {
        info_label.setText(algo1.drone.getInfoHTML());
        info_label2.setText("<html>" + String.valueOf(algo1.counter) + " <BR>isRisky:" + String.valueOf(algo1.is_risky) +
                "<BR>" + String.valueOf(algo1.risky_dis) + "</html>");
    }

    public void stopCPUS() {
        CPU.stopAllCPUS();
    }

    public void resumseCPUS() {
        CPU.stopAllCPUS();
    }

    public static JLabel getInfo_label_Of_PID() {
        return info_label_Of_PID;
    }
}