package generation;

import org.junit.Test;
import generation.Order.Builder;
import gui.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import java.util.Random;

public class MazeBuilderBoruvkaTest extends MazeFactoryTest {

/** This is a supplemental function for use by the rest of the Boruvka tests.
 * 
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

/** This test checks that a generated maze has a singular exit, no more or less.
 * 
 */
@Test
public void hasOneExit() {
	Maze testmaze = createMaze(4, Builder.Boruvka, true);
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

/** This test checks every tile in the maze has a path to the exit.
 * 
 */
@Test
public void alwaysReachableExit() {
	Maze testmaze = createMaze(4, Builder.Boruvka, true);
	Distance dists = testmaze.getMazedists();
	for (int x = 0; x != testmaze.getWidth(); x++) {
		for (int y = 0; y != testmaze.getHeight(); y++) {
				if(dists.getDistanceValue(x, y) < 1){
					fail("Missing path to the exit");
				
				}
			}
	}
}

@Test
/** This test checks that there are no rooms in the generated maze.
 * 
 */
public void noRooms() {
	Maze testmaze = createMaze(4, Builder.Boruvka, true);
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
/** This test checks ensures that maze generation is random and that two mazes of the same level are unique. 
 * 
 */
@Test
public void differentMazesSameLevel() {
	Maze testmaze = createMaze(4, Builder.Boruvka, true);
	Maze testmaze2 = createMaze(4, Builder.Boruvka, true);
	assertFalse(testmaze.getFloorplan().equals(testmaze2.getFloorplan()));
}
/** This test checks that a generated maze has the correct dimensions depending on the skill input.
 * 
 */
@Test
public void correctMazeProperties() {
	Random rand = new Random();
	int skill = (int)rand.nextDouble()*9;
	Maze testmaze = createMaze(skill, Builder.Boruvka, true);
	if ((testmaze.getWidth() != Constants.SKILL_X[skill]) && (testmaze.getHeight() != Constants.SKILL_Y[skill])) {
		fail("Width or Height is incorrect");
	}
	}

/** This test checks that the edge weights in a Boruvka maze are stored and will not change upon reference.
 * 
 * 
 */
@Test
public void testEdgeWeights() {
Random rand = new Random();
int seed = rand.nextInt()*1000;
DefaultOrder order = new DefaultOrder(4, Builder.Boruvka, true, seed);

MazeBuilderBoruvka test = new MazeBuilderBoruvka();
test.buildOrder(order);
Integer val = test.getEdgeWeight(0, 0, CardinalDirection.East);
Integer val2 = test.getEdgeWeight(0, 0, CardinalDirection.East);

assertEquals(val, val2);

}

/** This test checks that neighboring cells return the same weight for a shared wall.
 * 
 */
@Test
public void testNeighborEdgeWeights() {
Random rand = new Random();
int seed = rand.nextInt()*1000;
DefaultOrder order = new DefaultOrder(4, Builder.Boruvka, true, seed);

MazeBuilderBoruvka test = new MazeBuilderBoruvka();
test.buildOrder(order);
Integer val = test.getEdgeWeight(0, 0, CardinalDirection.East);
Integer val2 = test.getEdgeWeight(1, 0, CardinalDirection.West);

assertEquals(val, val2);

}

/** This test insures that all skill levels generate a working maze. Not all skill levels return a square maze layout.
 * WARNING: this strenuous test 
 * may take up to 3 minutes
 */
@Test
public void testAllSizes() {
	for (int i = 0; i < 10; i++) {
	this.createMaze(i, Builder.Boruvka, true);
	}

}


}