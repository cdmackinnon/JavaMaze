package generation;

import org.junit.Test;
import generation.Order.Builder;
import gui.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import java.util.Random;

public class MazeFactoryTest {
	
	/** Hint: "The MazeFactory operates a background thread such
	*that you need to make the test wait for the 
	*termination of the MazeBuilder thread before
	*the test proceeds
	*
	*Factory interface has special method to help"
	*/
	
	private Maze createMaze(int skill, Builder builder, boolean perfect) {
		Random rand = new Random();
		int seed = rand.nextInt()*1000;
		DefaultOrder order = new DefaultOrder(skill, builder, perfect, seed);
		MazeFactory factory = new MazeFactory();
		factory.order(order);
		factory.waitTillDelivered();
		return order.getMaze();
	}

	
	
	
	
	/*This test ensures that generated mazes cannot have multiple exits or no exits
	 * it uses isExitPosition() from distance to check the perimeter of the maze 
	 */
	@Test
	public void hasOneExit() {
		Maze testmaze = createMaze(4, Builder.DFS, true);
		int exits = 0;
		for (int x = 0; x != testmaze.getWidth(); x++) {
			for (int y = 0; y != testmaze.getHeight(); y++) {
				if (x == 0 || x==testmaze.getWidth()-1 || y==0 || y == testmaze.getHeight() - 1) {
					if (testmaze.getFloorplan().isExitPosition(x,y)) {
						exits += 1;
								
				}
			}
		}
		}
		assertEquals(1, exits);
		
	}
	/* 
	 * This test checks that all the distances to the exit
	 * In a distance object the minimum value is 1 so if a value is smaller it is invalid
	 * 
	 * If an area is enclosed it has no path to exit, therefore this test also ensures
	 * that every location is reachable
	 */
	@Test
	public void alwaysReachableExit() {
		Maze testmaze = createMaze(4, Builder.DFS, true);
		Distance dists = testmaze.getMazedists();
		for (int x = 0; x != testmaze.getWidth(); x++) {
			for (int y = 0; y != testmaze.getHeight(); y++) {
					if(dists.getDistanceValue(x, y) < 1){
						fail("Missing path to the exit");
					
					}
				}
		}
	}
	/**This checks that a maze is perfect(has no rooms)
	 * There has to be (n-1) * (n-1) internal walls for n cells to make a perfect maze
	 * Can use hasWall from MazeContainer on each cell in each direction to total the walls
	 * 
	 * Alternatively, could brute force with isInRoom(int x, int y) on all coordinates 
	 */
	@Test
	public void noRooms() {
		Maze testmaze = createMaze(4, Builder.DFS, true);
		int totalwalls = 0;
		for (int x = 0; x != testmaze.getWidth(); x++) {
			for (int y = 0; y != testmaze.getHeight(); y++) {
				
				if (testmaze.getFloorplan().hasWall(x,y, CardinalDirection.South) && (y!= testmaze.getHeight()-1)){
					totalwalls++;
				}
				if (testmaze.getFloorplan().hasWall(x,y, CardinalDirection.East)&& (x!= testmaze.getWidth()-1)){
					totalwalls++;
				}
			}
		}
		assertEquals((testmaze.getWidth() - 1)*(testmaze.getHeight() - 1), totalwalls);
		
			
	}
	/**Checks that mazes are generated randomly
		 * Uses MazeContainer getFloorplan() on two separate mazes and then tests for checks equality
		 */
	@Test
	public void differentMazesSameLevel() {
		Maze testmaze = createMaze(4, Builder.DFS, true);
		Maze testmaze2 = createMaze(4, Builder.DFS, true);
		assertFalse(testmaze.getFloorplan().equals(testmaze2.getFloorplan()));
	}
	/**Checks for the correct width and height based on skill level
	 * Uses getWidth() and getHeight() from MazeContainer
	 */
	@Test
	public void correctMazeProperties() {
		Random rand = new Random();
		int skill = (int)rand.nextDouble()*9;
		Maze testmaze = createMaze(skill, Builder.DFS, true);
		if ((testmaze.getWidth() != Constants.SKILL_X[skill]) && (testmaze.getHeight() != Constants.SKILL_Y[skill])) {
			fail("Width or Height is incorrect");
		}
		}
	
	
	
}
