package gui;

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

/**   
* @author Connor MacKinnon
*/
public class ReliableSensorTest {
	
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
	
	//Maze testMaze = createMaze(0, Builder.DFS, true, 0);	TODO double check tests are resetting maze/sensor
	ReliableRobot robot;
	ReliableSensor sensor;
	Maze testMaze;
	
	@Before
	public void setUp() {
		robot = new ReliableRobot();
		sensor = new ReliableSensor();
		testMaze = createMaze(0, Builder.DFS, true, 0);
		sensor.setMaze(testMaze);
		//check if there is more resetting needed for sensors
	}
	
	
	/**
	 * Checks the normal functionality of Distance to Obstacle
	 * This test is correct because using the predetermined maze, 0,0 South has a wall and
	 * 0,1 South has no wall
	 */
	@Test
	final void TestNormalDistance() {
		setUp();
		int[] cords = {0,1};
		float[] flt = {2};
		
		try {
			assertEquals(sensor.distanceToObstacle(cords, CardinalDirection.South, flt),  2);
			//fails if distance to obstacle throws an error
		} catch (Exception e) {
			fail();
		}
	}
	
	
	/**
	 * Tests that sensing the distance to the exit returns Integer.MAX_VALUE
	 * This is correct because we are breaking a wall in the maze and then pointing the sensor coordinate there
	 */
	@Test
	final void TestAtExitDistance() {
		setUp();
		testMaze = createMaze(0, Builder.DFS, true, 0);
		testMaze.getFloorplan().setExitPosition(1, 0);
		int[] cords = {1,0};
		float[] flt = {2};
		try {
			assertEquals(sensor.distanceToObstacle(cords,CardinalDirection.North, flt), Integer.MAX_VALUE );
			//fails if distance to obstacle throws an error
		} catch (Exception e) {
			fail();
		}
	}
	
	/**
	 * Tests that sensing the distance when there is not enough power
	 * returns a PowerFailureException
	 */
	@Test
	final void TestPowerFailure() {
		setUp();
		int[] cords = {0,0};
		float[] flt = {0};
		try {
			 sensor.distanceToObstacle(cords,CardinalDirection.North, flt);
		}
		//if 
		catch (Exception e) {
			assertTrue(true);
		}
		assertTrue(false);
		
	}
	
	/*
	@Test
	final void TestIllegalNotInMaze() {
		setUp();
		int[] cords = {Integer.MAX_VALUE,Integer.MAX_VALUE};
		float[] flt = {2};
		try {
			int distance = sensor.distanceToObstacle(cords,CardinalDirection.North, flt);
		}catch (Exception e) {
			assertTrue(true);
		}
		//double check if this is the correct failure representation
		
		assertTrue(false);
	}*/
	
	
	/**
	 * Asserts the energy consumption is the correct value (1.0)
	 */
	@Test
	final void TestEnergyConsumption() {
		setUp();
		assertEquals(sensor.getEnergyConsumptionForSensing(), 1.0);
	}
	
	

}
