package Testing;

import constants.Constants;
import navigation.FullNavigator;
import navigation.Navigation;
import utility.Utility;


public class SquareDriver extends Thread {
	
	public void run(){
		
		for (int i = 0; i < 4; i++) {
			// drive forward two tiles
			FullNavigator.leftMotor.setSpeed(Constants.FORWARD_SPEED);
			FullNavigator.rightMotor.setSpeed(Constants.FORWARD_SPEED);

			FullNavigator.leftMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, 60.96*3/2), true);
			FullNavigator.rightMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, 60.96*3/2), false);

			// turn 90 degrees clockwise
			FullNavigator.leftMotor.setSpeed(Constants.ROTATING_SPEED);
			FullNavigator.rightMotor.setSpeed(Constants.ROTATING_SPEED);

			FullNavigator.leftMotor.rotate(Utility.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), true);
			FullNavigator.rightMotor.rotate(-Utility.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), false);
	}
		return;
}
}

