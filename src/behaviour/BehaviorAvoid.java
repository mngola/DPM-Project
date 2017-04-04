package behaviour;
import lejos.robotics.subsumption.Behavior;
import navigation.Navigation;


public class BehaviorAvoid implements Behavior
{
	private int dist;

	// what to do
	public void action()
	{	
		try
		{
			Navigation.turn(Navigation.odometer.getTheta()+90);
			Navigation.goForward(30);
			Navigation.turn(Navigation.odometer.getTheta()-90);
			Navigation.goForward(dist+30);
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
		dist = Navigation.lPoller.getDistance();
		return ( dist < 10);
	}	
}
