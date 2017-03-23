package localization;

import navigation.FullNavigator;
import odometry.Odometer;
import polling.USPoller;
import utility.Utility;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import constants.Constants;

// TODO Localization Routine needs to be generalized and Threaded
public class USLocalizer implements LocalizationInterface {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }

	private Odometer odo;
	private USPoller poller;
	private LocalizationType locType;

	private FullNavigator navigator;
	private static EV3LargeRegulatedMotor lfmt;
	private static EV3LargeRegulatedMotor rgmt;

	public USLocalizer(Odometer odom,  USPoller us, LocalizationType loc, FullNavigator navi, EV3LargeRegulatedMotor leftmotor, EV3LargeRegulatedMotor rightmotor) {
		odo = odom;
		poller = us;
		locType = loc;
		navigator = navi;
		lfmt = leftmotor;
		rgmt = rightmotor;
	}

	public void doLocalization() {
		
	
		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});
		
		double [] pos = new double [3];
		double angleA, angleB, correctionangle;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
					
			
						// rotate the robot until it sees no wall
						
						while(poller.getDistance()<60){
							navigator.setSpeeds(-Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);
						}
						Sound.beep();
						// keep rotating until the robot sees a wall, then latch the angle
						
						while(poller.getDistance()>30){
							navigator.setSpeeds(-Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);
						}
						Sound.beep();
						navigator.stopMotors();
						
						angleA = odo.getTheta();
						// switch direction and wait until it sees no wall
						
						while(poller.getDistance()<60){
							navigator.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);
						}
						Sound.beep();
						
						// keep rotating until the robot sees a wall, then latch the angle
						while(poller.getDistance()>30){
							navigator.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);
						}
						Sound.beep();
						navigator.stopMotors();
						angleB = odo.getTheta();
						// angleA is clockwise from angleB, so assume the average of the
						// angles to the right of angleB is 45 degrees past 'north'
						if(angleA>angleB){
							angleA -= 360;
							correctionangle = (angleB-angleA)/2-46;
							turn(correctionangle-180);
						}
						else{
							angleA -= 360;
							correctionangle = (angleB-angleA)/2-46;
							turn(correctionangle);
						}
						
	
						
						// update the odometer position (example to follow:)
						odo.setPosition(new double [] {0.0, 0.0, 45}, new boolean [] {false, false, true});
						
		
		
		}
	}
	

	/*
	 * move: false for falling edge, true for rising edge
	 */
	private double latchEdge(boolean move) {
		double ang1 = 0.0;
		double ang2 = 0.0;
		boolean inNoiseMargin = false;

		if (move) {
			boolean wall = true;
			while (wall) {
				if (inNoiseMargin && (poller.getDistance() > Constants.LOW_NOISE)) {
					ang2 = odo.getTheta();
					wall = false;
				} else if (poller.getDistance() > Constants.UPPER_NOISE) {
					ang1 = odo.getTheta();
					inNoiseMargin = true;
				}
			}
		}
		else {
			boolean wall = false;
			while (!wall) {
				if (inNoiseMargin && (poller.getDistance() < Constants.LOW_NOISE)) {
					ang2 = odo.getTheta();
					wall = true;
				} else if (poller.getDistance() < Constants.UPPER_NOISE) {
					ang1 = odo.getTheta();
					inNoiseMargin = true;
				}
			}
		}

		return (ang1 + ang2) / 2.0;
	}
	
	public static void turn(double angle){
		double rotationangle = (15.4*(angle)/(2*(2.12)));
		lfmt.rotate((int) rotationangle, true);
		rgmt.rotate((int) -rotationangle, false);
	}
}
