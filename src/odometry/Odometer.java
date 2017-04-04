package odometry;

import polling.GyroPoller;
import utility.Utility;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import constants.Constants;

public class Odometer extends Thread implements OdometerInterface {
	// robot position
	private double x, y, theta;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private GyroPoller gyro;
	private double[] oldDH, dDH;
	private boolean gyroActive;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor lm,EV3LargeRegulatedMotor rm, GyroPoller g) {
		leftMotor = lm;
		rightMotor = rm;
		x = 0.0;
		y = 0.0;
		theta = 90.0;
		oldDH = new double[2];
		dDH = new double[2];
		gyro = g;
		gyroActive = false;
	}

	/*
	 * Calculates displacement and heading as title suggests
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * Constants.WHEEL_RADIUS + rightTacho * Constants.WHEEL_RADIUS) * Math.PI / 360.0;
		data[1] = (rightTacho * Constants.WHEEL_RADIUS - leftTacho * Constants.WHEEL_RADIUS) / Constants.TRACK;
	}

	// run method (required for Thread)
	public void run() {
		while (true) {
			getDisplacementAndHeading(dDH);
			dDH[0] -= oldDH[0];
			dDH[1] -= oldDH[1];
			synchronized (this) {
				/**
				 * Don't use the variables x, y, or theta anywhere but here!
				 * Only update the values of x, y, and theta in this block. 
				 * Do not perform complex math
				 * 
				 * Distances are actually arcs
				 * Variables are updated in the synchronization block 
				 */

				if(gyroActive)
				{
					theta = gyro.getAngle();
				}
				else {
					theta += dDH[1];
				}
				theta = Utility.fixDegAngle(theta);
				x += dDH[0] * Math.cos(Math.toRadians(theta));
				y += dDH[0] * Math.sin(Math.toRadians(theta));
			}
			oldDH[0] += dDH[0];
			oldDH[1] += dDH[1];

			try { Thread.sleep(Constants.ODOMETER_PERIOD); } catch (InterruptedException e) {}
		}
	}

	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	public double getTheta() {
		synchronized (this) {
			return theta;
		}
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2]) {
				theta = position[2];
				gyro.setAngle((int)position[2]);
			}
		}
	}

	// accessors
	public void getPosition(double[] position) {
		// ensure that the values don't change while the odometer is running
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}

	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}

	// accessors to motors
	public EV3LargeRegulatedMotor [] getMotors() {
		return new EV3LargeRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}
	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	public void setX(double x) {
		synchronized (this) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (this) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (this) {
			this.theta = theta;
		}
	}

	public void setGyroActive(boolean state) {
		gyroActive = state;
	}
}