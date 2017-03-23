package launcher;

import constants.Constants;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import navigation.Navigation;
import odometry.Odometer;

public class Launcher extends Thread implements LauncherInterface {
	
	private static Odometer odom;
	private static Navigation navi;
	private EV3LargeRegulatedMotor lm1;
	private EV3LargeRegulatedMotor lm2;

	//Constructor
	public Launcher(Odometer odom, Navigation navi, EV3LargeRegulatedMotor launchmotor1, EV3LargeRegulatedMotor launchmotor2){
		this.odom = odom;
		this.navi = navi;
		lm1 = launchmotor1;
		lm2 = launchmotor2;
	}

	public void fire(double targetX, double targetY) {
		
		lm1.flt();
		lm2.flt();
		
		//Calculate the distance
		double distance = Math.pow(Math.pow(targetY-odom.getY(), 2) + Math.pow(targetX-odom.getX(), 2),0.5d);
		
		//Calculate the angle to turn to face the target
		double minAng = (Math.atan2(targetY - odom.getY(), targetX - odom.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		
		//Turn to the target
		navi.turnTo(minAng, true);
		
		//Set the speed and acceleration
		lm1.setAcceleration(Constants.ACCELERATION);
		lm1.setSpeed((int) (Constants.LAUNCHER_CONSTANT*distance));
		lm2.setAcceleration(Constants.ACCELERATION);
		lm2.setSpeed((int) (Constants.LAUNCHER_CONSTANT*distance));
		
		
		//Shoot the ball
		lm1.rotate(-180,true);
		lm2.rotate(-180,false);
		
		//Rotate the ball back
		lm1.setAcceleration(1000);
		lm1.setSpeed(Constants.ROTATION_SPEED);
		lm2.setAcceleration(1000);
		lm2.setSpeed(Constants.ROTATION_SPEED);
		lm1.rotate(180);
		lm2.rotate(180);
		
		lm1.flt();
		lm2.flt();
		
		}

}
