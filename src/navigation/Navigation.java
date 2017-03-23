package navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import odometry.Odometer;
import utility.Utility;
import constants.Constants;

public class Navigation extends Thread implements NavigationInterface {	
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
		leftMotor.stop(true);
		rightMotor.stop(false);
	}
	
	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		minAng = getDestAngle(x,y);
		double dx = x - odometer.getX(); //The change we want in x and y
		double dy = y - odometer.getY();
		double distance = Math.sqrt(dx*dx+dy*dy);
		turnTo(minAng, false);
		leftMotor.setSpeed(Constants.FAST_SPEED);//set the speeds
		rightMotor.setSpeed(Constants.FAST_SPEED);
		leftMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance), true); //Cover the distance to get to the next point
		rightMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance), false);
//		while (!checkIfDone(x,y)) {
//			setSpeeds(Constants.FAST_SPEED, Constants.FAST_SPEED);
//		}
		stopMotors();
	}
	
	/*
	 * Check if the robot has reached its destination, within the CM_ERR
	 */
	protected boolean checkIfDone(double x, double y) {
		return Math.abs(x - odometer.getX()) < Constants.CM_ERR
				&& Math.abs(y - odometer.getY()) < Constants.CM_ERR;
	}
	
	/*
	 * Check if the robot is facing the correct direction, within the DEG_ERR
	 */
	protected boolean facingDest(double angle) {
		return Math.abs(angle - odometer.getTheta()) < Constants.DEG_ERR;
	}
	
	// Compute the destination angle 
	protected double getDestAngle(double x, double y) {
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
	public void turnTo(double angle, boolean stop) {

		leftMotor.setSpeed(Constants.ROTATION_SPEED); //set the speeds at rotating speed
		rightMotor.setSpeed(Constants.ROTATION_SPEED);
		double correctionangle = odometer.getTheta() - angle;  //The difference between the wanted value and our value
		//To make sure we never go the longer way around
		if(correctionangle<-180){
			correctionangle += 360;
		}
		else if(correctionangle>180){
			correctionangle -= 360;
		}
		leftMotor.rotate(Utility.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, correctionangle), true);
		rightMotor.rotate(-Utility.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, correctionangle), false);
		
//		double error = angle - odometer.getTheta();
//
//		while (Math.abs(error) > Constants.DEG_ERR) {
//
//			error = angle - odometer.getTheta();
//			
//			if (error < -180.0) {
//				setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
//			} else if (error < 0.0) {
//				setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
//			} else if (error > 180.0) {
//				setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
//			} else {
//				setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
//			}
//		}

		if (stop) {
			stopMotors();
		}
	}
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		travelTo(odometer.getX() + Math.cos(Math.toRadians(odometer.getTheta())) * distance, odometer.getY() + Math.sin(Math.toRadians(odometer.getTheta())) * distance);
	}
}
