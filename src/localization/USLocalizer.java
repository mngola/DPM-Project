package localization;

import navigation.FullNavigator;
import odometry.Odometer;
import polling.USPoller;
import utility.Utility;
import lejos.hardware.Sound;
// TODO Localization Routine needs to be generalized and Threaded
//TODO Extract constants to Constants file
public class USLocalizer implements LocalizationInterface {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }
	public static int ROTATION_SPEED = 80;

	private Odometer odo;
	USPoller poller;
	private LocalizationType locType;

	private FullNavigator navigator;

	double lowNoise = 101.0;
	double upperNoise = 107.0;

	public USLocalizer(Odometer odom,  USPoller us, LocalizationType loc, FullNavigator navi) {
		odo = odom;
		poller = us;
		locType = loc;
		navigator = navi;
	}

	public void doLocalization() {
		//Initialize the position
		double [] pos = {0.0,0.0,0.0};
		double angleA=0.0, angleB=0.0,angleD;
		boolean wall = false, inNoiseMargin = false;

		//if the filter is under the average noise, you're facing a wall
		if(poller.getDistance() < (lowNoise + upperNoise)/2.0)
		{
			wall = true;
		}
		//Check the localization type
		if (locType == LocalizationType.FALLING_EDGE) {

			//begin rotating 
			navigator.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);

			//If the robot starts facing a wall, adjust until it's not 
			while(wall) {
				if(inNoiseMargin && (poller.getDistance() > upperNoise)) {
					wall = false;
					inNoiseMargin = false;
				}
				else if(poller.getDistance() > lowNoise) {
					inNoiseMargin = true;
				}
			}
			Sound.playTone(4000, 200);
			
			inNoiseMargin = false;
			/*
			 * Loop while there is no wall
			 * When the robot detect the falling edge, compute the angle A to latch to
			 * Stop the robot
			 */
			while(!wall){
				if(inNoiseMargin && (poller.getDistance() < lowNoise))
				{
					angleA = latchEdge(false);
					Sound.playTone(4000, 200);
					navigator.stopMotors();
					wall = true;
				}
				else if(poller.getDistance() < upperNoise)
				{
					inNoiseMargin = true;
				}
			}
			
			//Rotate in the other direction
			navigator.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
			
			wall = false;
			inNoiseMargin = false;
			//Keep rotating till the robot finds the other angle, then latch to it
			while(!wall){
				if(inNoiseMargin && (poller.getDistance() < lowNoise))
				{
					angleB = latchEdge(false);
					Sound.playTone(4000, 100);
					navigator.stopMotors();
					wall = true;
				}
				else if(poller.getDistance() > upperNoise)
				{
					inNoiseMargin = true;
				}
			}
		

			//Compute the angle correction
			if (angleA < angleB) 
			{
				angleD = 45.0 - (angleA + angleB) / 2.0;
			} else {
				angleD = 225.0 - (angleA + angleB) / 2.0;
			}
		} else {
			//Begin rotating
			navigator.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
			
			//If the robot starts facing a space, adjust until it's not 
			while(!wall) {
				if(inNoiseMargin && (poller.getDistance() < upperNoise)) {
					wall = true;
					inNoiseMargin = false;
				}
				else if(poller.getDistance() < lowNoise) {
					inNoiseMargin = true;
				}
			}
			Sound.playTone(4000, 200);
			
			/*
			 * Loop while there is a wall
			 * When the robot detect the rising edge, compute the angle A to latch to
			 * Stop the robot
			 */
			while(wall){
				if(inNoiseMargin && (poller.getDistance() > upperNoise))
				{
					angleA = latchEdge(true);
					Sound.playTone(4000, 200);
					navigator.stopMotors();
					wall = false;
				}
				else if(poller.getDistance() > lowNoise)
				{
					inNoiseMargin = true;
				}
			}

			//Rotate in other direction
			navigator.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
			wall = true;
			inNoiseMargin = false;

			//Keep rotating till the robot finds the other angle, then latch to it
			while(wall){
				if(inNoiseMargin && (poller.getDistance() > upperNoise))
				{
					angleB = latchEdge(true);
					Sound.playTone(4000, 100);
					navigator.stopMotors();
					wall = false;
					inNoiseMargin = false;
				}
				else if(poller.getDistance() < lowNoise)
				{
					inNoiseMargin = true;
				}
			}

			//Compute the angle correction
			if (angleA < angleB) 
			{
				angleD = 225.0 - (angleA + angleB) / 2.0;
			} else {
				angleD = 45.0 - (angleA + angleB) / 2.0;
			}
		}
		//Adjust the odometer with the correction
		pos[2] = Utility.fixDegAngle(odo.getTheta() + angleD);
		odo.setPosition(pos, new boolean[] { true, true, true });
	}

	/*
	 * move: false for falling edge, true for rising edge
	 */
	private double latchEdge(boolean move) {
		double ang1 = 0.0;
		double ang2 = 0.0;
		boolean inNoiseMargin = false;

		if (move) {
			boolean wall = true;
			while (wall) {
				if (inNoiseMargin && (poller.getDistance() > lowNoise)) {
					ang2 = odo.getTheta();
					wall = false;
				} else if (poller.getDistance() > upperNoise) {
					ang1 = odo.getTheta();
					inNoiseMargin = true;
				}
			}
		}
		else {
			boolean wall = false;
			while (!wall) {
				if (inNoiseMargin && (poller.getDistance() < lowNoise)) {
					ang2 = odo.getTheta();
					wall = true;
				} else if (poller.getDistance() < upperNoise) {
					ang1 = odo.getTheta();
					inNoiseMargin = true;
				}
			}
		}

		return (ang1 + ang2) / 2.0;
	}
}
