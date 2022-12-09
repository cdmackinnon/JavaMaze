package gui;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

//import org.junit.Test;
import org.junit.jupiter.api.Test;


/**
 * Parent Class for Wizard.test and WallFollower.test
 */
public class DriverTest {
	
	/**
	 * Tests that the driver drives the robot to the exit positon
	 */
	@Test
	public void testIsAtExit() {
		;
	}
	
	/**
	 * Calculates energy before and after and tests that 
	 * the difference of those equals (drivertype).getEnergyConsumption()
	 * 
	 */
	@Test
	public void totalEnergyConsumed() {
		;
	}
	
	/**
	 * Tests that the driver can move the robot a step closer to the solution
	 * (This step may not be geographically closer such as for the wall follower
	 * but it is a step closer to finishing)
	 */
	@Test
	public void testOneStepCloser() {
		;
	}
	
	/**
	 * Tests that the path length is the same as the steps traveled
	 * I.e. Drive one step to the exit and assert that the the path length is 1
	 */
	@Test
	public void testPathLength() {
		;
	}
	
	
	
}
