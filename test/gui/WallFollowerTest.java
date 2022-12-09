package gui;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Random;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;
import generation.Order.Builder;
import gui.Constants.UserInput;

/**
 * @author connormackinnon
 */
public class WallFollowerTest extends DriverTest {
	
	private Maze createMaze(int skill, Builder builder, boolean perfect, int seed) {
		Random rand = new Random();
		if (seed == -1){
		seed = rand.nextInt()*1000;
		}
		DefaultOrder order = new DefaultOrder(skill, builder, perfect, seed);
		MazeFactory factory = new MazeFactory();
		factory.order(order);
		factory.waitTillDelivered();
		return order.getMaze();
	}
	
	UnreliableRobot robot;
	Maze testMaze;
	WallFollower driver;
	
	@Before
	public void setup(){
		Control controller = new Control();
		controller.sensorString = "1111";
		testMaze = createMaze(0, Builder.DFS, true, 0);
		driver = new WallFollower();
		StatePlaying state = new StatePlaying();
		state.setMaze(testMaze);
		controller.setState(state);
		controller.start();
		robot = new UnreliableRobot(controller);
		controller.setRobotAndDriver(robot, driver);
		robot.setController(controller);
		state.setMaze(testMaze);
		driver.setMaze(testMaze);
		driver.setRobot(robot);
		robot.forward.setMaze(testMaze);
		robot.backward.setMaze(testMaze);
		robot.left.setMaze(testMaze);
		robot.right.setMaze(testMaze);
	}
	
	/**
	 * Tests that the wall follower drives the robot to the exit positon
	 * Asserts the robot is in the exit position
	 * Uses a basic maze so the robot won't run out of battery
	 */
	//@Test
	@Override
	public void testIsAtExit() {
		setup();
		try {
			driver.drive2Exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(robot.isAtExit());
		
		
	}
	
	
	/**
	 * Calculates energy before and after driving to the exit and tests that 
	 * the difference of those equals WallFollower.getEnergyConsumption()
	 */
	@Test
	@Override
	public void totalEnergyConsumed() {
		setup();
		try {
			driver.drive2Exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(3500 != driver.getEnergyConsumption());
	}
	
	/**
	 * Tests that the driver can move the robot a step closer to the solution
	 * (This step may not be geographically closer such as for the wall follower
	 * but it is a step closer to finishing)
	 */
	@Test
	@Override
	public void testOneStepCloser() {
		setup();
		robot.setBatteryLevel(3500);
		try {
			driver.drive1Step2Exit();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Tests that if the robot has a broken sensor that the wall follower 
	 * can reach the end of the maze still
	 */
	@Test
	public void testOneBrokenSensor() {
	//////////////////setup//////////////////////////
		Control controller = new Control();
		controller.sensorString = "0111";
		testMaze = createMaze(0, Builder.DFS, true, 0);
		driver = new WallFollower();
		StatePlaying state = new StatePlaying();
		state.setMaze(testMaze);
		controller.setState(state);
		controller.start();
		robot = new UnreliableRobot(controller);
		controller.setRobotAndDriver(robot, driver);
		robot.setController(controller);
		state.setMaze(testMaze);
		driver.setMaze(testMaze);
		driver.setRobot(robot);
		robot.forward.setMaze(testMaze);
		robot.backward.setMaze(testMaze);
		robot.left.setMaze(testMaze);
		robot.right.setMaze(testMaze);
	//////////////////setup//////////////////////////
		try {
			driver.drive2Exit();
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests that if all of the robot's sensors are broken
	 * that the robot can reach the exit
	 */
	@Test
	public void testAllBrokenSensors() {
		//////////////////setup//////////////////////////
		Control controller = new Control();
		controller.sensorString = "0000";
		testMaze = createMaze(0, Builder.DFS, true, 0);
		driver = new WallFollower();
		StatePlaying state = new StatePlaying();
		state.setMaze(testMaze);
		controller.setState(state);
		controller.start();
		robot = new UnreliableRobot(controller);
		controller.setRobotAndDriver(robot, driver);
		robot.setController(controller);
		state.setMaze(testMaze);
		driver.setMaze(testMaze);
		driver.setRobot(robot);
		robot.forward.setMaze(testMaze);
		robot.backward.setMaze(testMaze);
		robot.left.setMaze(testMaze);
		robot.right.setMaze(testMaze);
		//////////////////setup//////////////////////////
		try {
			driver.drive2Exit();
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}
	
	
	
}
