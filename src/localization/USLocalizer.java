package localization;

import navigation.Navigation;
import odometry.Odometer;
import polling.USPoller;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import constants.Constants;

public class USLocalizer implements LocalizationInterface {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }

	private Odometer odo;
	private USPoller poller;
	private LocalizationType locType;

	private static EV3LargeRegulatedMotor lfmt;
	private static EV3LargeRegulatedMotor rgmt;

	/**
	 * Constructor; requiring odometer, an ultrasonic poller, the localization type, the left and right motors 
	 * @param odom
	 * @param us
	 * @param loc
	 * @param leftmotor
	 * @param rightmotor
	 */
	public USLocalizer(Odometer odom,  USPoller us, LocalizationType loc, EV3LargeRegulatedMotor leftmotor, EV3LargeRegulatedMotor rightmotor) {
		odo = odom;
		poller = us;
		locType = loc;
		lfmt = leftmotor;
		rgmt = rightmotor;
	}

	/**
	 * The ultrasonic localization routine
	 */
	public void doLocalization() {

		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});

		double angleA, angleB, correctionangle;

		if (locType == LocalizationType.FALLING_EDGE) {

			// rotate the robot until it sees no wall

			while(poller.getDistance()<60){
				Navigation.setSpeeds(-Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);
			}
			// keep rotating until the robot sees a wall, then latch the angle

			while(poller.getDistance()>30){
				Navigation.setSpeeds(-Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);
			}
			Navigation.stopMotors();

			angleA = odo.getTheta();
			// switch direction and wait until it sees no wall

			while(poller.getDistance()<60){
				Navigation.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);
			}

			// keep rotating until the robot sees a wall, then latch the angle
			while(poller.getDistance()>30){
				Navigation.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);
			}
			Navigation.stopMotors();
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

	public static void turn(double angle){
		double rotationangle = (15.4*(angle)/(2*(2.12)));
		lfmt.rotate((int) rotationangle, true);
		rgmt.rotate((int) -rotationangle, false);
	}
}
