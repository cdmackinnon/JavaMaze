package gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import generation.CardinalDirection;
import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;
import generation.Order.Builder;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
* @author Connor MacKinnon
*/
public class ReliableRobotTest {
	
	
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
	
	ReliableRobot robot;
	Maze testMaze;
	
	@Before
	public void setUp() {
		Control controller = new Control();
		Wizard driver = new Wizard();
		controller.setRobotAndDriver(robot, driver);
		robot = new ReliableRobot();
		robot.setController(controller);
		testMaze = createMaze(0, Builder.DFS, true, 0);
		//check if odometer/battery needs resetting each time
		//check that robot starts in same position and doesn't carry over between tests
	}
	
	
	ReliableSensor sensor = new ReliableSensor();
	
	////////////////////////////////////////////////
	//////Movement and Positioning Testing//////////
	////////////////////////////////////////////////
	
	/**
	 * Tests the get position function
	 * this is correct because the robot position should be identical
	 * to the starting position after not having moved
	 */
	@Test
	public final void testGetPosition() {
		setUp();
		try {
			robot.directionFacer(CardinalDirection.South);
			int[] pair = robot.getCurrentPosition();
			robot.move(1);
			pair[1] ++;
			assertEquals(robot.getCurrentPosition()[1],pair[1]);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**
	 * Tests that the robot can move
	 * This is correct because the robot should be in a different location than originally
	 * assumes there is a space to move forward
	 */
	@Test
	public final void testMoving() {
		setUp();
		robot.move(1);
		try {
			assertFalse(robot.getCurrentPosition() == testMaze.getStartingPosition());
		} catch (Exception e) {
			fail();
		}
		
	/**
	 * Tests that the robot can correctly identify not being in a room
	 * correct because a perfect maze should not have the robot in a room ever
	 */
	}
	@Test
	final void testInRoom() {
		setUp();
		int[] pos;
		try {
			pos = robot.getCurrentPosition();
		
		assertEquals(robot.isInsideRoom(),testMaze.getFloorplan().isInRoom(pos[0],pos[1]));
		}catch (Exception e) {
		fail();
		}
	}
	
	//TODO test case where robot is in room, try using Wizard through hole maze checking room status each square
	//or find a seed with a room spawn to immediately check
	
	
	/**
	 * Tests that the robot can rotate directions
	 * uses original direction and compares accordingly
	 */
	
	@Test
	final void testRotate(){
		setUp();
		CardinalDirection dir = robot.getCurrentDirection();
		robot.rotate(Turn.RIGHT);
		if (dir == CardinalDirection.North) {
			assertEquals(robot.getCurrentDirection(), CardinalDirection.East);
		}
		else if (dir == CardinalDirection.East) {
			assertEquals(robot.getCurrentDirection(), CardinalDirection.South);
		}
		else if (dir == CardinalDirection.South) {
			assertEquals(robot.getCurrentDirection(), CardinalDirection.West);
		}
		else if (dir == CardinalDirection.West) {
			assertEquals(robot.getCurrentDirection(), CardinalDirection.North);
		}
	}
	
	
	////////////////////////////////////////////////
	/////////////Battery Testing////////////////////
	////////////////////////////////////////////////
	/**
	 * Tests the battery functionality with move
	 * Moving forward one step (one cell): 6 battery
	 * assumes there is a space to move forward
	 */
	@Test
	final void testBatteryLevelMove() {
		setUp();
		robot.setBatteryLevel(7);
		robot.move(1);
		assertEquals(robot.getBatteryLevel(), 1);
	}
	
	/**
	 * Tests the battery functionality with jump
	 * Jumping costs 40
	 * assumes there is a space to move forward
	 */
	@Test
	final void testBatteryLevelJump() {
		setUp();
		robot.setBatteryLevel(41);
		robot.jump();
		assertEquals(robot.getBatteryLevel(), 1);
	}
	
	/**
	 * Tests the battery functionality with jump
	 * Rotating left or right (quarter turn or 90 degrees): 3
	 */
	@Test
	final void testBatteryLevelRotate() {
		setUp();
		robot.setBatteryLevel(4);
		robot.rotate(Turn.LEFT);
		assertEquals(robot.getBatteryLevel(), 1);
	}
	
	/**
	 * Tests the battery functionality with distance sensing
	 * Distance sensing in one direction or checking if exit is visible in one direction: 1
	 * (may need to add sensor, or this is already a part of the function)
	 */
	final void testBatteryLevelDistanceSense() {
		setUp();
		robot.setBatteryLevel(2);
		robot.distanceToObstacle(Direction.FORWARD);
		assertEquals(robot.getBatteryLevel(), 1);
	}
	
	/**
	 * Tests that the robot stops upon reaching zero battery
	 * hasStopped should equal true
	 */
	@Test
	final void testZeroBattery() {
		setUp();
		robot.setBatteryLevel(0);
		robot.distanceToObstacle(Direction.FORWARD);
		assertTrue(robot.hasStopped());
	}
	
	////////////////////////////////////////////////
	/////////////Odometer Testing///////////////////
	////////////////////////////////////////////////
	
	/**
	 * Tests that the odometer resets to zero
	 */
	@Test
	final void testResetOdometer() {
		setUp();
		robot.resetOdometer();
		assertEquals(robot.getOdometerReading(), 0);
	}
	
	/**
	 * Tests that the odometer counts up
	 * Assumes there is a space to move forward
	 */
	@Test
	final void testOdometer() {
		setUp();
		robot.resetOdometer();
		robot.move(1);
		assertEquals(robot.getOdometerReading(), 1);
	}
	
	
	//TODO Exit based testing when wizard is better understood
	//TODO Double check initialization/setup and that tests are reset each time
	
	
}
