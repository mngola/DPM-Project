package localization;

import navigation.FullNavigator;
import odometry.Odometer;
import polling.USPoller;
import utility.Utility;
import lejos.hardware.Sound;
import constants.Constants;

// TODO Localization Routine needs to be generalized and Threaded
public class USLocalizer implements LocalizationInterface {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }

	private Odometer odo;
	private USPoller poller;
	private LocalizationType locType;

	private FullNavigator navigator;

	public USLocalizer(Odometer odom,  USPoller us, LocalizationType loc, FullNavigator navi) {
		odo = odom;
		poller = us;
		locType = loc;
		navigator = navi;
	}

	public void doLocalization() {
		//Initialize the position
		double [] pos = {0.0,0.0,0.0};
		double angleA=0.0, angleB=0.0,angleD = 0;
		boolean wall = false, inNoiseMargin = false;

		//if the filter is under the average noise, you're facing a wall
		if(poller.getDistance() < (Constants.LOW_NOISE + Constants.UPPER_NOISE)/2.0)
		{
			wall = true;
		}
		switch(locType) {
		case FALLING_EDGE:
			//begin rotating 
			navigator.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);
			//If the robot starts facing a wall, adjust until it's not 
			while(wall) {
				if(inNoiseMargin && (poller.getDistance() > Constants.UPPER_NOISE)) {
					wall = false;
					inNoiseMargin = false;
				}
				else if(poller.getDistance() > Constants.LOW_NOISE) {
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
				if(inNoiseMargin && (poller.getDistance() < Constants.LOW_NOISE))
				{
					angleA = latchEdge(false);
					Sound.playTone(4000, 200);
					navigator.stopMotors();
					wall = true;
				}
				else if(poller.getDistance() < Constants.UPPER_NOISE)
				{
					inNoiseMargin = true;
				}
			}

			//Rotate in the other direction
			navigator.setSpeeds(-Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);

			wall = false;
			inNoiseMargin = false;
			//Keep rotating till the robot finds the other angle, then latch to it
			while(!wall){
				if(inNoiseMargin && (poller.getDistance() < Constants.LOW_NOISE))
				{
					angleB = latchEdge(false);
					Sound.playTone(4000, 100);
					navigator.stopMotors();
					wall = true;
				}
				else if(poller.getDistance() > Constants.UPPER_NOISE)
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
			break;
		case RISING_EDGE:
			//Begin rotating
			navigator.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);

			//If the robot starts facing a space, adjust until it's not 
			while(!wall) {
				if(inNoiseMargin && (poller.getDistance() < Constants.UPPER_NOISE)) {
					wall = true;
					inNoiseMargin = false;
				}
				else if(poller.getDistance() < Constants.LOW_NOISE) {
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
				if(inNoiseMargin && (poller.getDistance() > Constants.UPPER_NOISE))
				{
					angleA = latchEdge(true);
					Sound.playTone(4000, 200);
					navigator.stopMotors();
					wall = false;
				}
				else if(poller.getDistance() > Constants.LOW_NOISE)
				{
					inNoiseMargin = true;
				}
			}

			//Rotate in other direction
			navigator.setSpeeds(-Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);
			wall = true;
			inNoiseMargin = false;

			//Keep rotating till the robot finds the other angle, then latch to it
			while(wall){
				if(inNoiseMargin && (poller.getDistance() > Constants.UPPER_NOISE))
				{
					angleB = latchEdge(true);
					Sound.playTone(4000, 100);
					navigator.stopMotors();
					wall = false;
					inNoiseMargin = false;
				}
				else if(poller.getDistance() < Constants.LOW_NOISE)
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
			break;
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
				if (inNoiseMargin && (poller.getDistance() > Constants.LOW_NOISE)) {
					ang2 = odo.getTheta();
					wall = false;
				} else if (poller.getDistance() > Constants.UPPER_NOISE) {
					ang1 = odo.getTheta();
					inNoiseMargin = true;
				}
			}
		}
		else {
			boolean wall = false;
			while (!wall) {
				if (inNoiseMargin && (poller.getDistance() < Constants.LOW_NOISE)) {
					ang2 = odo.getTheta();
					wall = true;
				} else if (poller.getDistance() < Constants.UPPER_NOISE) {
					ang1 = odo.getTheta();
					inNoiseMargin = true;
				}
			}
		}

		return (ang1 + ang2) / 2.0;
	}
}
