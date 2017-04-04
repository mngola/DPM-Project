package behaviour;
import lejos.robotics.subsumption.*;
import navigation.Navigation;

public class BehaviorMove implements Behavior{
	boolean stop = false;

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		stop = false;
		try
		{
			while (!stop)
			{
				Navigation.travelTo(0,60);
				Navigation.stop();
			}
		}
		catch(Exception ex)
		{
		}
	}

	@Override
	public void suppress() {
		stop = true;
		Navigation.stop();
	}

}
