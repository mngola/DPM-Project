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

	/**
	 * Constructor
	 * @param lm The left motor 
	 * @param rm The right motor 
	 * @param g A gyro sensor poller object
	 */
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

	/**
	 * Calculates displacement and heading as title suggests
	 * @param data location to store the displacemen and heading
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * Constants.WHEEL_RADIUS + rightTacho * Constants.WHEEL_RADIUS) * Math.PI / 360.0;
		data[1] = (rightTacho * Constants.WHEEL_RADIUS - leftTacho * Constants.WHEEL_RADIUS) / Constants.TRACK;
	}

	/**
	 * Run method
	 * Updates the fields in a synchronized block
	 */
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

	/**
	 * Gets the x coordinate of the robot
	 * @return the x position of the robot
	 */
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	/**
	 * Gets the y coordinate of the robot
	 * @return the y position of the robot
	 */
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	/**
	 * Gets the current heading of the robot
	 * @return the heading of the robot
	 */
	public double getTheta() {
		synchronized (this) {
			return theta;
		}
	}

	/**
	 * Simultaneously sets the odometer to the specified position 
	 * @param position an array containing the new values
	 * @param update an array of booleans, which values to update
	 */
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

	/**
	 * Simultaneously gets the odometer values
	 * @param position where to store the odometer values 
	 */
	public void getPosition(double[] position) {
		// ensure that the values don't change while the odometer is running
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}

	/**
	 * Simultaneously gets the odometer values
	 * @return an array with the coordinates of the robot
	 */
	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}

	/**
	 * Simultaneously gets the robot's motors
	 * @return an array with references to robots motors
	 */
	public EV3LargeRegulatedMotor [] getMotors() {
		return new EV3LargeRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}
	/**
	 * Gets the robot's left motor
	 * @return a reference to the robot's left motor
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	/**
	 * Gets the robot's right motor
	 * @return a reference to the robot's right motor
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	/**
	 * Sets the x coordinate of the robot
	 * @param the x position of the robot
	 */
	public void setX(double x) {
		synchronized (this) {
			this.x = x;
		}
	}

	/**
	 * Sets the y coordinate of the robot
	 * @param the y position of the robot
	 */
	public void setY(double y) {
		synchronized (this) {
			this.y = y;
		}
	}

	/**
	 * Sets the heading of the robot
	 * @param the heading of the robot
	 */
	public void setTheta(double theta) {
		synchronized (this) {
			this.theta = theta;
		}
	}

	/**
	 * Sets the gyroActive parameter
	 * @param state boolean for whether the gyro is active
	 */
	public void setGyroActive(boolean state) {
		gyroActive = state;
	}
}