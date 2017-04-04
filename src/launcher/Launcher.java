package launcher;

import constants.Constants;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import navigation.Navigation;
import odometry.Odometer;

public class Launcher extends Thread implements LauncherInterface {
	
	private static Odometer odometer;
	private EV3LargeRegulatedMotor lm1;
	private EV3LargeRegulatedMotor lm2;
	int ANGLE_TURN;

	//Constructor
	public Launcher(Odometer odom, EV3LargeRegulatedMotor launchmotor1, EV3LargeRegulatedMotor launchmotor2){
		odometer = odom;
		lm1 = launchmotor1;
		lm2 = launchmotor2;
	}

	public void fire(double targetX, double targetY, int d1) {
		
		
		
		if(d1 == 5){
			ANGLE_TURN = 90;
		}
		else if(d1 == 6){
			ANGLE_TURN = 100;
		}
		else if(d1 == 7){
			ANGLE_TURN = 125;
		}
		else if(d1 == 8){
			ANGLE_TURN = 130;
		}
		
		
		//Calculate the angle to turn to face the target
		double minAng = (Math.atan2(targetY - odometer.getY(), targetX - odometer.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		
		//Turn to the target
		Navigation.turnTo(minAng, true);
		
		//Set the speed and acceleration
		lm1.setAcceleration(Constants.ACCELERATION);
		lm1.setSpeed(Constants.LAUNCHER_SPEED);
		lm2.setAcceleration(Constants.ACCELERATION);
		lm2.setSpeed(Constants.LAUNCHER_SPEED);
		
		
		//Shoot the ball
		lm1.rotate(-ANGLE_TURN,true);
		lm2.rotate(-ANGLE_TURN,false);
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		//Rotate the ball back
		lm1.setAcceleration(1000);
		lm1.setSpeed(Constants.ROTATION_SPEED);
		lm2.setAcceleration(1000);
		lm2.setSpeed(Constants.ROTATION_SPEED);
		lm1.rotate(ANGLE_TURN);
		lm2.rotate(ANGLE_TURN);
		

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
