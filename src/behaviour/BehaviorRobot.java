package behaviour;
import constants.Constants;
import odometry.Odometer;
import polling.USPoller;
import utility.Utility;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class BehaviorRobot {

	protected static Odometer odometer;
	final static float CONVERT_DIST = 20.8f;
	final static float CONVERT_TURN = 1.93f;
	final static int SPEED = 200;

	private static boolean stop = false;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor leftMotor;
	// new state variable
	static USPoller poller;

	public BehaviorRobot(Odometer odo, USPoller usp) {
		odometer = odo;
		poller = usp;


		EV3LargeRegulatedMotor[] motors = odometer.getMotors();
		leftMotor = motors[0];
		rightMotor = motors[1];

		setSpeed(SPEED);

		// create each behavior
		Behavior move = new BehaviorMove();
		Behavior avoid = new BehaviorAvoid();		

		// define an array (vector) of existing behaviors, sorted by priority
		Behavior behaviors[] = { move, avoid };

		// add the behavior vector to a new arbitrator and start arbitration
		Arbitrator arbitrator = new Arbitrator(behaviors);
		arbitrator.go();
	}

	/*
	 * Sets the new speed of both motors to the input value
	 */
	public static void setSpeed(int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}

	/*
	 * Stops all turning and traveling immediately
	 */
	public static void stop()
	{
		stop = true;
	}


	public static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(false);
	}

	public static void setSpeeds(int lSpd, int rSpd) {
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

	public static void turn(double angle, boolean finish) throws Exception
	{
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
		if(finish || stop){
			leftMotor.stop();                                  
			rightMotor.stop(); 
		}
	}

	/*
	 * Drives robot forward or backwards by specified distance.
	 */
	public static void travel(double x, double y) throws Exception
	{
		stop = false;
		double minAng;

		double odoX = odometer.getX();
		double odoY = odometer.getY();
		minAng = Math.toDegrees((Math.atan2(y - odoY, x - odoX)));
		if (minAng < 0)
			minAng += 360.0;
		turn(minAng, true);
		while (((Math.abs(x - odoX) > Constants.CM_ERR) || (Math.abs(y - odoY) > Constants.CM_ERR)) && (!stop))
		{
			odoX = odometer.getX();
			odoY = odometer.getY();
			leftMotor.forward();
			rightMotor.forward();

			minAng = Math.toDegrees((Math.atan2(y - odoY, x - odoX)));
			if (minAng < 0)
				minAng += 360.0;
			turn(minAng, false);
			leftMotor.forward();
			rightMotor.forward();
			//setSpeeds(Constants.FAST_SPEED, Constants.FAST_SPEED);
			Thread.yield();
		}
		stopMotors();
	}

	public static void turnTo(double destTheta)
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

	public static void goForward(double distance) {
		leftMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance),true);		
		rightMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance),false);
	}

}
