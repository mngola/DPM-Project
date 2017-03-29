package launcher;

import constants.Constants;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import navigation.Navigation;
import odometry.Odometer;

public class Launcher extends Thread implements LauncherInterface {
	
	private static Odometer odom;
	private static Navigation navi;
	private EV3LargeRegulatedMotor lm1;
	private EV3LargeRegulatedMotor lm2;
	int ANGLE_TURN = 100;

	//Constructor
	public Launcher(Odometer odom, Navigation navi, EV3LargeRegulatedMotor launchmotor1, EV3LargeRegulatedMotor launchmotor2){
		this.odom = odom;
		this.navi = navi;
		lm1 = launchmotor1;
		lm2 = launchmotor2;
	}

	public void fire(double targetX, double targetY) {
		
		lm1.rotate(10,true);
		lm2.rotate(10,false);
		
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
		lm1.setSpeed(Constants.LAUNCHER_SPEED);
		lm2.setAcceleration(Constants.ACCELERATION);
		lm2.setSpeed(Constants.LAUNCHER_SPEED);
		
		//27-32-25
		
		
		
		//Shoot the ball
		lm1.rotate(-ANGLE_TURN,true);
		lm2.rotate(-ANGLE_TURN,false);
		

		
		lm1.setSpeed(100);
		lm2.setSpeed(100);
		
		lm1.rotate(ANGLE_TURN+15,true);
		lm2.rotate(ANGLE_TURN+15,false);
		
//		//Rotate the ball back
//		lm1.setAcceleration(1000);
//		lm1.setSpeed(50);
//		lm2.setAcceleration(1000);
//		lm2.setSpeed(50);
//		
//		lm1.rotate(ANGLE_TURN,true);
//		lm2.rotate(ANGLE_TURN,false);
//		
		lm1.stop();
		lm2.stop();
		
		}
	
	
	public void dropClaw(){
		lm1.setSpeed(50);
		lm2.setSpeed(50);
		lm1.rotate(5,true);
		lm2.rotate(5,false);
		
		lm1.flt();
		lm2.flt();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
