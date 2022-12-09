package gui;

import java.lang.ref.Reference;


import generation.CardinalDirection;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
 * 
 * CRC:
 * 
 * Responsibilities: 
 * Inherits all of the reliable robot methods, 
 * Has a selection of sensors that are both reliable and unreliable,
 * Functionality depends on sensor uptime, 
 * Sensor Repairing Functionality
 * 
 * Classes: ReliableRobot, ReliableSensor, UnreliableSensor
 * 
 *
 * @author Connor MacKinnon
 */
public class UnreliableRobot extends ReliableRobot {
	
	
	DistanceSensor forward;
	DistanceSensor backward;
	DistanceSensor left;
	DistanceSensor right; 
	
	
	public UnreliableRobot(Control controller) {
		//this.setController(controller);
		//FORWARD SENSOR
		if (controller.sensorString.substring(0,1).equals("1")) {
			forward = new ReliableSensor();
		}
		else {
			forward = new UnreliableSensor();
			forward.startFailureAndRepairProcess(4, 2);
		}
		
		
		//LEFT SENSOR
		if (controller.sensorString.substring(1,2).equals("1")) {
			left = new ReliableSensor();
		}
		else {
			//waits 1.3 seconds between initializing each unreliable sensor
			//This allows for one sensor to always be active
			try {Thread.sleep(1300);}catch(InterruptedException e){}
			left = new UnreliableSensor();
			left.startFailureAndRepairProcess(4, 2);
		}
		
		
		//RIGHT SENSOR
		if (controller.sensorString.substring(2,3).equals("1")) {
			right = new ReliableSensor();
		}
		else {
			try {Thread.sleep(1300);}catch(InterruptedException e){}
			right = new UnreliableSensor();
			right.startFailureAndRepairProcess(4, 2);
		}
		
		//BACKWARD SENSOR
		if (controller.sensorString.substring(3).equals("1")) {
			backward = new ReliableSensor();
		}
		else {
			try {Thread.sleep(1300);}catch(InterruptedException e){}
			backward = new UnreliableSensor();
			backward.startFailureAndRepairProcess(4, 2);
		}
		
		forward.setMaze(controller.getMaze());
		left.setMaze(controller.getMaze());
		right.setMaze(controller.getMaze());
		backward.setMaze(controller.getMaze());
	}
	
	/**
	* Creates a new sensor object and adds it to the desired direction
	* Uses sensor.setSensorDirecion
	*/
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		if (mountedDirection == Direction.FORWARD) {
			forward = sensor;
			forward.setMaze(controller.getMaze());
			forward.setSensorDirection(mountedDirection);
		}
		if (mountedDirection == Direction.LEFT) {
			left = sensor;
			left.setMaze(controller.getMaze());
			left.setSensorDirection(mountedDirection);
		}
		if (mountedDirection == Direction.RIGHT) {
			right = sensor;
			right.setMaze(controller.getMaze());
			right.setSensorDirection(mountedDirection);
		}
		if (mountedDirection == Direction.BACKWARD) {
			backward = sensor;
			backward.setMaze(controller.getMaze());
			backward.setSensorDirection(mountedDirection);
		}
	}
	
	
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		//subtract battery amount by sensor.getEnergyConsumptionForSensing() 
		//check has_stopped
		//checks the desired sensor direction with the corresponding sensor reference 
		
		if (!hasStopped() && battery>0) {
			
			float[] battery = {getBatteryLevel()};
			this.battery -= 1; //cost of sensing
			
			DistanceSensor reference = null;
			//Creating a reference 
			if (direction == Direction.FORWARD) {
				reference = forward;			//TODO double check that this is only a pointer
			}
			else if (direction == Direction.LEFT) {
				reference = left;
				}
			else if (direction == Direction.RIGHT) {
				reference = right;
				}
			else if (direction == Direction.BACKWARD) {
				reference = backward;
				}
			
			try {
				return(reference.distanceToObstacle(getCurrentPosition(), relativeToCardinalDirection(direction), battery));
				//if sensor is not operating (or out of bounds) return -1
			} catch (Exception e) {
				return -1;
			}
		}
		if (battery <= 0) {
			hasStopped = true;
		}
		return -1;
	}
	
	public CardinalDirection relativeToCardinalDirection(Direction dir) {
		if (dir == Direction.LEFT){
			if (getCurrentDirection() == CardinalDirection.North) {
				return CardinalDirection.East;
			}
			else if (getCurrentDirection() == CardinalDirection.East) {
				return CardinalDirection.South;
			}
			else if (getCurrentDirection() == CardinalDirection.South) {
				return CardinalDirection.West;
			}
			else if (getCurrentDirection() == CardinalDirection.West) {
				return CardinalDirection.North;
			}
		}
		else if (dir == Direction.RIGHT){
			if (getCurrentDirection() == CardinalDirection.North) {
				return CardinalDirection.West;
			}
			else if (getCurrentDirection() == CardinalDirection.West) {
				return CardinalDirection.South;
			}
			else if (getCurrentDirection() == CardinalDirection.South) {
				return CardinalDirection.East;
			}
			else if (getCurrentDirection() == CardinalDirection.East) {
				return CardinalDirection.North;
			}
		}
		//no backwards functionality
		return getCurrentDirection();
		
	}
	
	
		//subtract battery amount by sensor.getEnergyConsumptionForSensing() 
		//check has_stopped
		//checks the distance sensor in the desired direction
		//if that distance is MAX_INTEGER return true, else false
		@Override
		public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
			if (!hasStopped() && battery>0) {
				
				DistanceSensor reference = null;
				//Creating a reference 
				if (direction == Direction.FORWARD) {
					reference = forward;			//TODO double check that this is only a pointer
				}
				else if (direction == Direction.LEFT) {
					reference = left;
					}
				else if (direction == Direction.RIGHT) {
					reference = right;
					}
				else if (direction == Direction.BACKWARD) {
					reference = backward;
					}
				
				//if the sensor is unreliable and not operating the function waits for it to be active
				if (reference instanceof UnreliableSensor) {
					if (!((UnreliableSensor) reference).getStatus()){
						try {Thread.sleep(2050);
						}catch (InterruptedException e) {e.printStackTrace();}
					}
				}
				
				float[] battery = {getBatteryLevel()};
				this.battery -= 1; //cost of sensing
				try {
					if(reference.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), battery)== Integer.MAX_VALUE) {
						return true;
					}
					//if position is invalid returns false
				} catch (Exception e) {
					return false;
				}
			}
			hasStopped = false;
			return false;
		}
	
		
		
		
		
		
	
}
