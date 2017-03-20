package navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import constants.Constants;
import odometry.Odometer;
import polling.USPoller;
import utility.Utility;

public class FullNavigator extends Navigation {

	enum State {
		INIT, TURNING, TRAVELLING, EMERGENCY
	};

	State state;

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private boolean isNavigating = false;

	private double destx, desty;

	USPoller usSensor;

	public FullNavigator(Odometer odo, USPoller us) {
		super(odo);
		usSensor = us;
		EV3LargeRegulatedMotor[] motors = odometer.getMotors();
		leftMotor = motors[0];
		rightMotor = motors[1];
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * 
	 * When avoid=true, the nav thread will handle traveling. If you want to
	 * travel without avoidance, this is also possible. In this case,
	 * the method in the Navigation class is used.
	 * 
	 */
	public void travelTo(double x, double y, boolean avoid) {
		if (avoid) {
			destx = x;
			desty = y;
			isNavigating = true;
		} else {
			super.travelTo(x, y);
		}
	}

	
	/*
	 * Updates the h
	 */
	private void updateTravel() {
		double minAng;

		minAng = getDestAngle(destx, desty);
		/*
		 * Use the BasicNavigator turnTo here because 
		 * minAng is going to be very small so just complete
		 * the turn.
		 */
		super.turnTo(minAng,false);
		double dx = destx - odometer.getX(); //The change we want in x and y
		double dy = desty - odometer.getY();
		double distance = Math.sqrt(dx*dx+dy*dy);
		leftMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance), true); //Cover the distance to get to the next point
		rightMotor.rotate(Utility.convertDistance(Constants.WHEEL_RADIUS, distance), true);
		while(!checkIfDone(destx,desty)){
			if(usSensor.getDistance()<20)
			{
				int p =0;
				p++;
			}
			System.out.println(usSensor.getDistance());
		}
	}

	public void run() {
		ObstacleAvoidance avoidance = null;
		state = State.INIT;
		while (true) {
			switch (state) {
			case INIT:
				if (isNavigating) {
					state = State.TURNING;
				}
				break;
			case TURNING:
				double destAngle = getDestAngle(destx, desty);
				turnTo(destAngle);
				if(facingDest(destAngle)){
					setSpeeds(0,0);
					state = State.TRAVELLING;
				}
				break;
			case TRAVELLING:
				if (checkEmergency()) { // order matters!
					state = State.EMERGENCY;
					avoidance = new ObstacleAvoidance(this);
					avoidance.start();
				} 
				else if (!checkIfDone(destx, desty)) {
					updateTravel();
				} 
				else { // Arrived!
					stopMotors();
					isNavigating = false;
					state = State.INIT;
				}
				break;
			case EMERGENCY:
				if (avoidance.resolved()) {
					state = State.TURNING;
				}
				break;
			}
			try {
				Thread.sleep(Constants.NAV_PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkEmergency() {
		return usSensor.getDistance() < 10;
	}


	private void turnTo(double angle) {
		leftMotor.setSpeed(Constants.SLOW_SPEED); //set the speeds at rotating speed
		rightMotor.setSpeed(Constants.SLOW_SPEED);
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
	}

	/*
	 * Go foward a set distance in cm with or without avoidance
	 */
	public void goForward(double distance, boolean avoid) {
		double x = odometer.getX()
				+ Math.cos(Math.toRadians(odometer.getTheta())) * distance;
		double y = odometer.getY()
				+ Math.sin(Math.toRadians(odometer.getTheta())) * distance;

		this.travelTo(x, y, avoid);

	}

	public boolean isTravelling() {
		return isNavigating;
	}

}
