package behaviour;
import lejos.hardware.Sound;
import lejos.robotics.subsumption.*;
import main.DpmProject;
import navigation.Navigation;

public class BehaviorMove implements Behavior{
	boolean stop = false;
	double destx,desty;

	/**
	 * 
	 * @param x The x position of the dispenser 
	 * @param y The y position of the dispenser 
	 */
	public BehaviorMove(double x, double y)
	{
		destx = x;
		desty = y;
	}
	@Override
	public boolean takeControl() {
		return true;
	}

	/**
	 * The attack routine of the robot. Moves the robot to the dispenser
	 * lowers the launcher, beeps, moves to the shootin position and fires.
	 */
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
				
				DpmProject.launchMotor1.flt();
				DpmProject.launchMotor2.flt();
				DpmProject.launchMotor1.setSpeed(100);
				DpmProject.launchMotor2.setSpeed(100);
				DpmProject.launchMotor1.rotate(50,true);
				DpmProject.launchMotor2.rotate(50,false);
				DpmProject.launchMotor1.stop();
				DpmProject.launchMotor2.stop();
				
				float angle=0;
				switch(DpmProject.orientation){
				case "N":
					 angle = 90;
					break;
				case "S":
					angle = 270;
					break;
				case "E":
					angle = 0;
					break;
				case "W":
					angle = 180;
					break;
				}
				Navigation.turnTo(angle, true);
				Sound.beep();
				Thread.sleep(3000);
				Navigation.startCorrect=true;
				Thread.yield();

				//Go to the shooting zone
				Navigation.travelTo(DpmProject.targetX*DpmProject.tileLength, (DpmProject.targetY-DpmProject.d1-1/2)*DpmProject.tileLength);
				if(!Navigation.checkIfDone(DpmProject.targetX*DpmProject.tileLength, (DpmProject.targetY-DpmProject.d1-1/2)*DpmProject.tileLength))
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
