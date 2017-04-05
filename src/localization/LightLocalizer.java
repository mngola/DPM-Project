package localization;

import navigation.Navigation;
import odometry.Odometer;
import utility.Utility;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import constants.Constants;

public class LightLocalizer extends Thread {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;
	private EV3LargeRegulatedMotor lfmt;
	private EV3LargeRegulatedMotor rgmt;

	public LightLocalizer(Odometer odometer, SampleProvider colSen, float[] colData, EV3LargeRegulatedMotor leftmotor, EV3LargeRegulatedMotor rightmotor) {
		odo = odometer;
		colorSensor = colSen;
		colorData = colData;
		lfmt = leftmotor; 
		rgmt = rightmotor;
	}

	public void doTransition(){
		colorSensor.fetchSample(colorData, 0);
		while(colorData[0] > Constants.LOWER_LIGHT){
			colorSensor.fetchSample(colorData, 0);
			Navigation.setSpeeds(Constants.ROTATION_SPEED, Constants.ROTATION_SPEED);
		}
		Navigation.stopMotors();
		lfmt.setSpeed(200);
		rgmt.setSpeed(200);
		lfmt.rotate(-Utility.convertDistance(Constants.WHEEL_RADIUS, 15 ), true);
		rgmt.rotate(-Utility.convertDistance(Constants.WHEEL_RADIUS, 15 ), false);
	}

	public void doLocalization() {
		// Drive to the localization location (close to (0,0))
		//navigator.turnTo(45.0, true);
		//navigator.goForward(10.0);

		/*
		 * Track the angles using an array
		 *  index 0: -x axis, index 1: +y axis, index 2: +x axis index 3: -y axis 
		 */


		int lines = 0;
		double[] lineAngles = new double[4];
		// start rotating and clock all 4 gridlines
		Navigation.setSpeeds(Constants.ROTATION_SPEED, -Constants.ROTATION_SPEED);

		boolean overLine = false;
		//Used correcting the line angles
		double lineAngle1 = 0.0;
		double lineAngle2 = 0.0;

		/*
		 * Loop through 4 lines
		 * When the light lowers, and you haven't crossed a line yet record the and
		 * When the rises, record the value and store in the array the average of the two values
		 */
		while (lines < 4) {
			colorSensor.fetchSample(colorData, 0);
			if ((colorData[0] < Constants.LOWER_LIGHT) && (!overLine)) 
			{
				lineAngle1 = odo.getTheta();
				overLine = true;
			}
			if ((colorData[0] > Constants.UPPER_LIGHT) && (overLine)) 
			{
				lineAngle2 = odo.getTheta();
				lineAngles[lines] = (lineAngle2 + lineAngle1) / 2.0;
				lines++;
				//lejos.hardware.Sound.playTone(4000, 100);
				overLine = false;
			}
		}

		Navigation.stopMotors();
		// do trig to compute (0,0) and 0 degrees 

		//Compute the offset of the x distance
		double thetaX = Math.abs(lineAngles[0] - lineAngles[2]);
		if(thetaX > 180.0) {
			thetaX = 360.0 - thetaX;
		}
		double correctX = -Constants.LIGHT_SENSOR_DISTANCE * Math.cos(Math.toRadians(thetaX / 2.0));

		//Compute the offset of the y distance
		double thetaY = Math.abs(lineAngles[1] - lineAngles[3]);
		if(thetaY > 180.0) {
			thetaY = 360.0 - thetaX;
		}
		double correctY = -Constants.LIGHT_SENSOR_DISTANCE * Math.cos(Math.toRadians(thetaY / 2.0));
		
		//Correct Theta
		double deltaTheta = 270 + (thetaX/2.0) - lineAngles[0];
		double correctTheta = odo.getTheta() + deltaTheta;

		//Set the odometer to the corrected position 
		odo.setPosition(new double[] { correctY, correctX, correctTheta }, new boolean[] { true, true, true });

		// when done travel to (0,0) and turn to 0 degrees
		Navigation.travelTo(0.0, 0.0);
		Navigation.turnTo(0, true);
		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		//		//Correct Theta
		//		double deltaTheta = Math.toDegrees(Math.acos(-(correctX+1.7)/Constants.LIGHT_SENSOR_DISTANCE));		
		//		double correctTheta = 180-deltaTheta;
		//
		//
		//		//Set the odometer to the corrected position 
		//		odo.setPosition(new double[] { correctY, correctX, correctTheta }, new boolean[] { true, true, true });
		//
		//		Navigation.turn(-correctTheta);
		//		
		//		// when done travel to (0,0) and turn to 0 degrees
		//		Navigation.travelTo(0.0, 0.0);
		//		Navigation.turnTo(0, true);
		//		odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});

	}

}
