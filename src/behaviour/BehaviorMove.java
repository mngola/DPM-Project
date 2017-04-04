package behaviour;
import lejos.robotics.subsumption.*;
import main.DpmProject;
import navigation.Navigation;

public class BehaviorMove implements Behavior{
	boolean stop = false;
	double destx,desty;

	public BehaviorMove(double x, double y)
	{
		destx = x;
		desty = y;
	}
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
				Navigation.travelTo(destx,desty);
				if(!Navigation.checkIfDone(destx, desty))
				{
					Navigation.stop();
					break;
				}

				//DpmProject.findDispenser(DpmProject.colorData,DpmProject.colorValue, DpmProject.odometer);
				
				//Navigation.turnTo(desty, true);
				//DpmProject.ballDrop();
				//Pick up the ball
				//Navigation.travelTo(destx, desty);

				//Go to the shooting zone
				Navigation.travelTo(DpmProject.targetX*DpmProject.tileLength, (DpmProject.targetY-DpmProject.d1)*DpmProject.tileLength);
				if(!Navigation.checkIfDone(destx, desty))
				{
					Navigation.stop();
					break;
				}
				DpmProject.launch.fire(DpmProject.targetX*DpmProject.tileLength, DpmProject.targetY*DpmProject.tileLength, DpmProject.d1);
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
