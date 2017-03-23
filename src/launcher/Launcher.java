package launcher;

import constants.Constants;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import navigation.Navigation;
import odometry.Odometer;

public class Launcher extends Thread implements LauncherInterface {
	
	private static Odometer odom;
	private static Navigation navi;
	public static EV3LargeRegulatedMotor motor;

	//Constructor
	public Launcher(Odometer odom, Navigation navi, EV3LargeRegulatedMotor motor){
		this.odom = odom;
		this.navi = navi;
		this.motor = motor;
	}

	public void fire(double targetX, double targetY) {
		
		motor.flt();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Calculate the distance
		double distance = Math.pow(Math.pow(targetY-odom.getY(), 2) + Math.pow(targetX-odom.getX(), 2),1/2);
		
		//Calculate the angle to turn to face the target
		double minAng = (Math.atan2(targetY - odom.getY(), targetX - odom.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		
		//Turn to the target
		navi.turnTo(minAng, true);
		
		//Set the speed and acceleration
		motor.setAcceleration(Constants.ACCELERATION);
		motor.setSpeed((int) (Constants.LAUNCHER_SPEED*distance));
		
		//Shoot the ball
		motor.rotate(180);
		
		//Rotate the ball back
		motor.setAcceleration(1000);
		motor.setSpeed(Constants.ROTATION_SPEED);
		motor.rotate(-180);
		
		motor.flt();
		
		}

}
