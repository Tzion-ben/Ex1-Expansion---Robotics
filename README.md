# Ex1-Expansion---Robotics

The code is based on the assignment guidelines, with the primary objective of developing an effective solution for a small drone to fly autonomously inside an indoor environment without collisions or crashes. This fully autonomous 2D drone simulator aims to be as realistic as possible, incorporating LiDAR sensors, a gyroscope sensor, an optical flow sensor, and a speed sensor. Additionally, it features area mapping capabilities as the drone navigates. The project is written in Java.

Getting Started:
Ensure all files are located within Eclipse or any other suitable explorer. The "Maps" folder contains several maps with designated routes and obstacles. In the "SimulationWindow" main class, you will find a map object with the path to any map you wish to test. When you start the simulator, a dialog window will prompt you to choose your map. If the selected map file is incorrect, the dialog will prompt you to choose again until you select the correct map. In the "Drone" class, there is a path to the image representing the drone. Once these are set up, the simulator is ready to launch.

Sensors:

LiDAR: Measures the distance to obstacles ahead and returns the distance if an obstacle is detected, or a maximum sample distance of 300 if none is detected. Our project uses three LiDAR sensors: one facing forward, one at 90 degrees, and one at -90 degrees.
Gyroscope: Measures the drone's rotation (0-360 degrees).
Optical Flow: Determines the drone's location on the map.
Speed: The maximum speed is 2 meters per second.
Symbols:

Yellow Mark: Represents mapped areas.
Black Circle: Indicates the path the drone has traveled, helping in navigation.
Red Points: Represent wall points.
Blue Line: Indicates the entire route taken by the drone.

Project Expansion:
The project will be expanded to include additional simulation tasks for the "tiny drone." The idea is to build a high-quality open-source project that enables the simulation of various control algorithms and serves as a platform for machine learning applications, such as Reinforcement Learning. Specifically, the goal is to allow the drone to "return to the starting point" based on its measurements.
