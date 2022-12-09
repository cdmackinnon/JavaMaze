package gui;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ModuleLayer.Controller;
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
public class UnreliableSensorTest {
	
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
		//sensors are created within the tests depending on reliable vs. unreliable
	}
	
	
	/**
	 * Tests that an unreliable sensor is active for 4 seconds
	 */
	@Test
	final void testSensorTime(){
		setUp();
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.startFailureAndRepairProcess(4, 6);
		if (!sensor.getStatus()) {
			fail();
		}
		try {
			Thread.sleep(4010);	//sleeps a little over to ensure processing is done
		} catch (InterruptedException e) {}
		if (sensor.getStatus()) {
			fail();
		}
		sensor.stopFailureAndRepairProcess();
	}
	
	/**
	 * Tests that an unreliable sensor is inactive for 2 seconds
	 */
	@Test
	final void testFailureTime(){
		setUp();
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.startFailureAndRepairProcess(4, 2);
		if (!sensor.getStatus()) {
			fail();
		}
		try {
			Thread.sleep(4010); //waiting until sensor fails
			Thread.sleep(2010); //waiting until sensor is repaired
		} catch (InterruptedException e) {}
		if (!sensor.getStatus()) {
			fail();
		}
		sensor.stopFailureAndRepairProcess();
	}
	
	/**
	 * Tests that an unreliable sensor starts off as active
	 */
	@Test
	final void testActiveAtStart(){
		setUp();
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.startFailureAndRepairProcess(4, 6);
		if (!sensor.getStatus()) {
			fail();
		}
	}
	
	/**
	 * Tests that multiple unreliable sensors have a delay time in between repairs 
	 * i.e. at least one sensor is always active
	 * (longer test: 14~ seconds)
	 */
	@Test
	final void testDelayedRepair(){
	//different setup to manipulate the amount of sensors the controller initializes
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
	///////////////////////////////////////////////////////
		
		//Sleeping for 1300 for each sensor so they can all initialize (1300 * 3)
		try {Thread.sleep(3950);} catch (InterruptedException e) {}
		
		//tests that there is at least one sensor at all times
		//(waits 2 seconds in between to allow for failure)
		for (int i =0; i<3; i++) {
			assertTrue( ((UnreliableSensor) robot.forward).getStatus() || ((UnreliableSensor) robot.backward).getStatus() || ((UnreliableSensor) robot.left).getStatus() || ((UnreliableSensor) robot.right).getStatus() );
			try {Thread.sleep(2000);} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Checks that the sensor has the correct energy consumption constant
	 */
	@Test
	final void testEnergyConsumption(){
		setUp();
		UnreliableSensor sensor = new UnreliableSensor();
		assertEquals(sensor.getEnergyConsumptionForSensing(), 1);
	}
	
	/**
	 * Checks that all sensors work and don't throw an errors when active and inactive
	 */
	@Test
	final void testInactiveCensorException(){
		setUp();
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.setMaze(testMaze);
		sensor.startFailureAndRepairProcess(0, 10);
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {}
		float[] battery = {Integer.MAX_VALUE};
		Boolean exception = false;
		try {
			sensor.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(), battery);
		} catch (Exception e) {
			exception = true;
			}
		if (!exception) {
			fail();
		}

		}
	
	/**
	 * Checks that all sensors work and don't throw an errors when active and inactive
	 */
	@Test
	final void testPowerFailure(){
		setUp();
		UnreliableSensor sensor = new UnreliableSensor();
		sensor.setMaze(testMaze);
		sensor.startFailureAndRepairProcess(1, 1);
		sensor.setSensorDirection(Direction.FORWARD);
		float[] battery = {0};
		Boolean exception = false;
		try {
			sensor.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(), battery);
		} catch (Exception e) {
			exception = true;
			}
		if (!exception) {
			fail();
		}

		}
		
	
	/**
	 * Checks that all sensors return Distances
	 */
	@Test
	final void testDistances(){
		setUp();
		UnreliableSensor left = new UnreliableSensor();
		left.startFailureAndRepairProcess(100, 0);
		left.setSensorDirection(Direction.LEFT);
		left.setMaze(testMaze);
		UnreliableSensor right = new UnreliableSensor();
		right.startFailureAndRepairProcess(100, 0);
		right.setSensorDirection(Direction.RIGHT);
		right.setMaze(testMaze);
		UnreliableSensor forward = new UnreliableSensor();
		forward.startFailureAndRepairProcess(100, 0);
		forward.setSensorDirection(Direction.FORWARD);
		forward.setMaze(testMaze);
		UnreliableSensor back = new UnreliableSensor();
		back.startFailureAndRepairProcess(100, 0);
		back.setSensorDirection(Direction.BACKWARD);
		back.setMaze(testMaze);
		
		float[] battery = {Integer.MAX_VALUE};
		Boolean exception = false;
		
		try {left.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(), battery);} catch (Exception e) {exception=true;}
		if (exception) {
			fail();
		}
		try {right.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(), battery);} catch (Exception e) {exception=true;}
		if (exception) {
			fail();
		}
		try {forward.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(), battery);} catch (Exception e) {exception=true;}
		if (exception) {
			fail();
		}
		try {back.distanceToObstacle(robot.getCurrentPosition(),robot.getCurrentDirection(), battery);} catch (Exception e) {exception=true;}
		if (exception) {
			fail();
		}
		}
		

	
	
	
}
