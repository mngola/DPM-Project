package behaviour;
import lejos.robotics.subsumption.Behavior;
import navigation.Navigation;


public class BehaviorAvoid implements Behavior
{
	private int leftDist;
	private int rightDist;

	// what to do
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

	// how to stop doing it
	public void suppress()
	{	
		Navigation.stop();
	}

	// when to start doing it
	public boolean takeControl()
	{
		leftDist = Navigation.lPoller.getDistance();
		rightDist = Navigation.rPoller.getDistance();
		return ( leftDist < 10 || rightDist < 10);
	}	
}
