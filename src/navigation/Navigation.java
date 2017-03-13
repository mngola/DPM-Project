package navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import odometry.Odometer;
import constants.Constants;

public class Navigation extends Thread implements NavigationInterface {
	//Constants
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	
	protected Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public Navigation(Odometer odo) {
		odometer = odo;

		EV3LargeRegulatedMotor[] motors = odometer.getMotors();
		leftMotor = motors[0];
		rightMotor = motors[1];

		// set acceleration
		leftMotor.setAcceleration(Constants.ACCELERATION);
		rightMotor.setAcceleration(Constants.ACCELERATION);
	}

	public void run() {
		
	}
	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
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

	public void setSpeeds(int lSpd, int rSpd) {
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
	public void setFloat() {
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.flt(true);
		rightMotor.flt(true);
	}

	/*
	 * Stop the motors jointly
	 */
	public void stopMotors() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (!checkIfDone(x,y)) {
			minAng = getDestAngle(x,y);
			turnTo(minAng, false);
			setSpeeds(Constants.FAST_SPEED, Constants.FAST_SPEED);
		}
		this.setSpeeds(0, 0);
	}
	
	protected boolean checkIfDone(double x, double y) {
		return Math.abs(x - odometer.getX()) < CM_ERR
				&& Math.abs(y - odometer.getY()) < CM_ERR;
	}
	
	protected boolean facingDest(double angle) {
		return Math.abs(angle - odometer.getTheta()) < DEG_ERR;
	}
	
	protected double getDestAngle(double x, double y) {
		double minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		return minAng;
	}
	
	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - odometer.getTheta();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - odometer.getTheta();

			if (error < -180.0) {
				setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
			} else if (error < 0.0) {
				setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
			} else if (error > 180.0) {
				setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
			} else {
				setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
			}
		}

		if (stop) {
			setSpeeds(0, 0);
		}
	}
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		travelTo(odometer.getX() + Math.cos(Math.toRadians(odometer.getTheta())) * distance, odometer.getY() + Math.sin(Math.toRadians(odometer.getTheta())) * distance);
	}
}
