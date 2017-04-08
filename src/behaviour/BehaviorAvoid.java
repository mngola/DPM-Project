package behaviour;
import lejos.robotics.subsumption.Behavior;
import navigation.Navigation;


public class BehaviorAvoid implements Behavior
{
	private int leftDist;
	private int rightDist;

	/**
	 * The functions the behaviour should perform. The avoidance routine should
	 * turn 90 degrees based on which sensor detected the obstacle. Move forward, turn back 90 degrees
	 * move forward and then yield. 
	 *
	 */
	public void action()
	{	
		try
		{
			if(leftDist < rightDist) {
				Navigation.turn(Navigation.odometer.getTheta()+90);
				Navigation.goForward(30);
				Navigation.turn(Navigation.odometer.getTheta()-90);
				Navigation.goForward(leftDist+30);
			} else {
				Navigation.turn(Navigation.odometer.getTheta()-90);
				Navigation.goForward(30);
				Navigation.turn(Navigation.odometer.getTheta()+90);
				Navigation.goForward(rightDist+30);
			}
			Navigation.stopMotors();
			Thread.yield();
			Thread.sleep(2000);
		}
		catch(Exception ex)
		{
		}
	}

	/**
	 * The method for stopping the behaviour
	 */
	public void suppress()
	{	
		Navigation.stop();
	}
	
	/**
	 * 
	 * @return A boolean determining when this behaviour should take control
	 */
	public boolean takeControl()
	{
		leftDist = Navigation.lPoller.getDistance();
		rightDist = Navigation.rPoller.getDistance();
		return ( leftDist < 10 || rightDist < 10);
	}	
}
