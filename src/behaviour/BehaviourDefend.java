package behaviour;

import main.DpmProject;
import navigation.Navigation;
import lejos.robotics.subsumption.Behavior;

public class BehaviourDefend implements Behavior {
	boolean stop = false;
	double destx,desty;

	public BehaviourDefend(double x, double y)
	{
		destx = x;
		desty = y;
	}
	
	@Override
	public boolean takeControl() {
		return true;
	}
	/**
	 * Moves the robot to the edge of d1, disables the sensors
	 * This is meant to trigger the attacker's obstacle avoidance routine
	 * preventing them from firing
	 */
	@Override
	public void action() {
		stop = false;
		try
		{
			while (!stop)
			{
				Navigation.travelTo(destx,desty);
				if(Navigation.checkIfDone(destx, desty))
				{
					DpmProject.usSensor1.disable();
					DpmProject.usSensor2.disable();
					Navigation.lPoller.wait(7*60*1000);
					Navigation.rPoller.wait(7*60*1000);
				}
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
