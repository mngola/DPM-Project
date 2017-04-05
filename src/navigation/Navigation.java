package navigation;

import behaviour.BehaviorAvoid;
import behaviour.BehaviorMove;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import odometry.Odometer;
import polling.USPoller;
import utility.Utility;
import constants.Constants;

public class Navigation {	
	public static Odometer odometer;
	private static  EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static boolean stop = false;
	public static USPoller lPoller;
	public static USPoller rPoller;
	static Behavior behaviors[];
	static Arbitrator arbitrator;
	public static boolean beginNav = false;
	public static double destX,destY;
	public static boolean startCorrect = false;
	
	public Navigation(Odometer odo, USPoller lUsp, USPoller rUsp) {
		odometer = odo;
		lPoller = lUsp;
		rPoller = rUsp;

		EV3LargeRegulatedMotor[] motors = odometer.getMotors();
		leftMotor = motors[0];
		rightMotor = motors[1];

		// set acceleration
		leftMotor.setAcceleration(Constants.ACCELERATION);
		rightMotor.setAcceleration(Constants.ACCELERATION);
	}

	
	/*
	 * Functions to set the motor speeds jointly
	 */
	public static void setSpeeds(int lSpd, int rSpd)
	{
		leftMotor.setSpeed(lSpd);
		rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			leftMotor.backward();
		else
			leftMotor.forward();
		if (rSpd < 0)
			rightMotor.backward();
		else
			rightMotor.forward();
	}

	public static void setSpeeds(float lSpd, float rSpd)
	{
		leftMotor.setSpeed(lSpd);
		rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			leftMotor.backward();
		else
			leftMotor.forward();
		if (rSpd < 0)
			rightMotor.backward();
		else
			rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public static void setFloat() {
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.flt(true);
		rightMotor.flt(true);
	}

	/*
	 * Stop the motors jointly
	 */
	public static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(false);
	}

	/*
	 * Stops all turning and traveling immediately
	 */
	public static void stop()
	{
		stop = true;
	}
	
	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public static void travelTo(double x, double y) {
		stop = false;
		double minAng;
		
		minAng = Math.toDegrees((Math.atan2(y - odometer.getY(), x - odometer.getX())));
		if (minAng < 0)
			minAng += 360.0;
		turnTo(minAng, true);
		
		while ((Math.abs(x - odometer.getX()) > Constants.CM_ERR || Math.abs(y - odometer.getY()) > Constants.CM_ERR)&&(!stop)) {
			minAng = Math.toDegrees((Math.atan2(y - odometer.getY(), x - odometer.getX())));
			if (minAng < 0)
				minAng += 360.0;
			turnTo(minAng, false);
			setSpeeds(Constants.FAST_SPEED, Constants.FAST_SPEED);
			Thread.yield();
		}
		stopMotors();
	}
	
	/*
	 * Check if the robot has reached its destination, within the CM_ERR
	 */
	public static boolean checkIfDone(double x, double y) {
		return Math.abs(x - odometer.getX()) < Constants.CM_ERR
				&& Math.abs(y - odometer.getY()) < Constants.CM_ERR;
	}

	/*
	 * Check if the robot is facing the correct direction, within the DEG_ERR
	 */
	protected static boolean facingDest(double angle) {
		return Math.abs(angle - odometer.getTheta()) < Constants.DEG_ERR;
	}

	// Compute the destination angle 
	protected static double getDestAngle(double x, double y) {
		double minAng = Math.toDegrees((Math.atan2(y - odometer.getY(), x - odometer.getX())));
		if (minAng < 0) {
			minAng += 360.0;
		}
		return minAng;
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public static void turnTo(double angle, boolean finish) {

		stop = false;
		
		double error = angle - odometer.getTheta();

		while ((Math.abs(error) > Constants.DEG_ERR)&&(!stop)) {
			if (error < -180.0) {
				setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
			} else if (error < 0.0) {
				setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
			} else if (error > 180.0) {
				setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
			} else {
				setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
			}
			error = angle - odometer.getTheta();
		}
		
		if (finish || stop) {
			stopMotors();
		}
		
		int x=0;
		x++;
	}

	/*
	 * Go foward a set distance in cm
	 */
	public static void goForward(double distance) {
		leftMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance),true);		
		rightMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance),false);
	}
	
	public static void turn(double destTheta)
	{
		double dTheta = destTheta - odometer.getTheta();

		if (dTheta < -180.0) {
			dTheta += 360.0;
		} else if (dTheta > 180.0) {
			dTheta -= 360.0;
		}

		leftMotor.rotate(Utility.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, dTheta), true);
		rightMotor.rotate(-Utility.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, dTheta), false);
	}
}
