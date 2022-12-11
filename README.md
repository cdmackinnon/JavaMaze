# Maze Generating and Playing Application

This is a Java project that generates a Maze of specified size and allows for user traversal with graphical 3d and 2d interfaces offering a variety of view options. Groundwork code inherited from class project and existing code. 
* Added a fully fleshed robot traversal option that automatically solves the maze
* Added several algorithms to the robot for maze traversal
* Implemented robot sensors using multithreading that would have varying reliability
* Sensors are faulty and become nonfunction so the robot is unable to detect surrounding walls until they are repaired and the robot can resume
* Added maze generation using boruvka's algorithm
* Implemented command line options for different robot algorithms, combinations of sensors with differing reliabilities and choice over maze building type and manual vs automated playing.
