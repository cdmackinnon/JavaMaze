package gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;
import generation.Order.Builder;
import gui.Robot.Direction;

/**
 * @author connormackinnon
 */
public class UnreliableRobotTest {
	//Unreliable Robot extends Reliable Robot and differs by sensors
	//because the difference is inherently sensor based, UnreliableSensorTest has more tests 
	//Reliablerobottest tests most of the functions unreliable robot also uses
	
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
	
	@Before
	public void setUp() {
		//initializes controller, maze, robot, driver and state
		Control controller = new Control();
		testMaze = createMaze(0, Builder.DFS, true, 0);
		WallFollower driver = new WallFollower();
		StatePlaying state = new StatePlaying();
		controller.setState(state);
		//Links those initialized objects
		robot = new UnreliableRobot(controller);
		controller.setRobotAndDriver(robot, driver);
		robot.setController(controller);
		state.setMaze(testMaze);
		driver.setMaze(testMaze);
		driver.setRobot(robot);
		controller.start();
	}
	
	@Test
	final void testInitializeRobot(){
		////////////////////SETUP///////////////////////////////
		Control controller = new Control();
		controller.sensorString = "0000";
		testMaze = createMaze(0, Builder.DFS, true, 0);
		WallFollower driver = new WallFollower();
		StatePlaying state = new StatePlaying();
		controller.setState(state);
		//Links those initialized objects
		robot = new UnreliableRobot(controller);
		controller.setRobotAndDriver(robot, driver);
		
		state.setMaze(testMaze);
		driver.setMaze(testMaze);
		driver.setRobot(robot);
		controller.start();
		///////////////////////////////////////////////////////
		assertTrue( robot.forward instanceof UnreliableSensor);
		assertTrue( robot.backward instanceof UnreliableSensor);
		assertTrue( robot.left instanceof UnreliableSensor);
		assertTrue( robot.right instanceof UnreliableSensor);
	}
	
	
	@Test
	final void testCannotSeeThroughExit() {
		////////////////////SETUP///////////////////////////////
		Control controller = new Control();
		controller.sensorString = "1111";
		testMaze = createMaze(0, Builder.DFS, true, 0);
		WallFollower driver = new WallFollower();
		StatePlaying state = new StatePlaying();
		controller.setState(state);
		//Links those initialized objects
		robot = new UnreliableRobot(controller);
		controller.setRobotAndDriver(robot, driver);
		
		state.setMaze(testMaze);
		driver.setMaze(testMaze);
		driver.setRobot(robot);
		controller.start();
		///////////////////////////////////////////////////////
		robot.setBatteryLevel(3500);
		
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.BACKWARD));
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.LEFT));
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT));
	}
	
	@Test
	final void testSeeThroughExitNonoperationalSensor() {
		setUp();
		robot.setBatteryLevel(3500);
		
		UnreliableSensor forwardSensor = new UnreliableSensor();
		UnreliableSensor backSensor = new UnreliableSensor();
		UnreliableSensor leftSensor = new UnreliableSensor();
		UnreliableSensor rightSensor = new UnreliableSensor();
		robot.addDistanceSensor(forwardSensor, Direction.FORWARD);
		robot.addDistanceSensor(backSensor, Direction.BACKWARD);
		robot.addDistanceSensor(leftSensor, Direction.LEFT);
		robot.addDistanceSensor(rightSensor, Direction.RIGHT);
		forwardSensor.startFailureAndRepairProcess(0, 10);
		backSensor.startFailureAndRepairProcess(0, 10);
		leftSensor.startFailureAndRepairProcess(0, 10);
		rightSensor.startFailureAndRepairProcess(0, 10);
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.BACKWARD));
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.LEFT));
		assertFalse(robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT));
	}	
	
	/**
	 * Tests robot distance to object works when sensors are active
	 *
	 */
	final void testStopRepairing() {
		setUp();
		robot.setBatteryLevel(3500);
		
		UnreliableSensor forwardSensor = new UnreliableSensor();
		UnreliableSensor backSensor = new UnreliableSensor();
		UnreliableSensor leftSensor = new UnreliableSensor();
		UnreliableSensor rightSensor = new UnreliableSensor();
		robot.addDistanceSensor(forwardSensor, Direction.FORWARD);
		robot.addDistanceSensor(backSensor, Direction.BACKWARD);
		robot.addDistanceSensor(leftSensor, Direction.LEFT);
		robot.addDistanceSensor(rightSensor, Direction.RIGHT);
		forwardSensor.startFailureAndRepairProcess(10, 0);
		backSensor.startFailureAndRepairProcess(10, 0);
		leftSensor.startFailureAndRepairProcess(10, 0);
		rightSensor.startFailureAndRepairProcess(10, 0);
		
		
		assertTrue(robot.distanceToObstacle(Direction.FORWARD) >= 0);
		assertTrue(robot.distanceToObstacle(Direction.BACKWARD) >= 0);
		assertTrue(robot.distanceToObstacle(Direction.LEFT) >= 0);
		assertTrue(robot.distanceToObstacle(Direction.RIGHT) >= 0);
	}
	
	
	
	@Test 
	final void testCanSeeThroughExit() {
		////////////////////SETUP///////////////////////////////
		Control controller = new Control();
		controller.sensorString = "1111";
		testMaze = createMaze(0, Builder.DFS, true, 0);
		WallFollower driver = new WallFollower();
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
		///////////////////////////////////////////////////////
		robot.setBatteryLevel(3500);
		
		try {
			System.out.println("here");
			driver.drive2Exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD));
		
		
	}
	
	
}
