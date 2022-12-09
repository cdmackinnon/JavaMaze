package gui;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.ModuleLayer.Controller;
import java.util.Random;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import generation.DefaultOrder;
import generation.Maze;
import generation.MazeFactory;
import generation.Order.Builder;

/**   
* @author Connor MacKinnon
*/
public class WizardTest extends DriverTest{

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
	Wizard wizard;
	
	@Before
	public void setUp() {
		//Control controller = new Control();
		robot = new ReliableRobot();
		testMaze = createMaze(0, Builder.DFS, true, 0);
		wizard = new Wizard();
		//controller.setRobotAndDriver(robot, wizard);
		wizard.setRobot(robot);
		//robot.setController(controller);
		//Issues creating a controller for the robot to function with
	}
	
	
	/**
	 * Tests that the robot is at the exit
	 */
	@Test
	@Override
	public void testIsAtExit() {
		setUp();
		try {
			wizard.drive2Exit();
			assertEquals(robot.getCurrentPosition(), testMaze.getExitPosition());
		} catch (Exception e) {
			fail();
		}
	}
	
	/**
	 * Tests that the path length is one shorter than before the step
	 */
	@Test
	@Override
	public void testOneStepCloser() {
		try {
			int[] pos = robot.getCurrentPosition();
			int dist1 = testMaze.getDistanceToExit(pos[0],pos[1]);
			wizard.drive1Step2Exit();
			pos = robot.getCurrentPosition();
			int dist2 = testMaze.getDistanceToExit(pos[0],pos[1]);
			assertEquals(dist2, dist1-1);
		} catch (Exception e) {
			fail();
		}
		
	}
	
	/**
	 * Tests that the path length returns the same as the maze path length
	 */
	@Test
	@Override
	public void testPathLength() {
		try {
			int[] pos = robot.getCurrentPosition();
			assertEquals(wizard.getPathLength(), testMaze.getDistanceToExit(pos[0],pos[1]));
		}catch (Exception e) {
			fail();
		}
	}
	
	/**
	 * Calculates energy before and after and test that 
	 * the difference of those equals wizard.getEnergyConsumption()
	 * 
	 */
	@Test
	@Override
	public void totalEnergyConsumed() {
		try {
			float energyBefore = robot.getBatteryLevel();
			wizard.drive2Exit();
			float energyAfter = robot.getBatteryLevel();
			float consumed = energyBefore - energyAfter;
			assertEquals(wizard.getEnergyConsumption(), consumed);
		}catch (Exception e) {
				fail();
			}
	}
	
}
