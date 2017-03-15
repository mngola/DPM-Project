package main;

import navigation.FullNavigator;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import odometry.Odometer;
import polling.USPoller;

public class DpmProject {

	//Motors
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	//Sensors
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static FullNavigator nav;


	public static void main(String[] args) {
		int buttonChoice;

		//Instaniate objects
		final TextLCD t = LocalEV3.get().getTextLCD();

		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usDistance = usSensor.getMode("Distance");
		float[] usData = new float[usDistance.sampleSize()];


		Odometer odometer = new Odometer(leftMotor, rightMotor);
		USPoller usPoller = new USPoller(usDistance, usData);
		nav = new FullNavigator(odometer,usPoller);
		usPoller.start();
		odometer.start();
		nav.start();

		completeCourse();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

	private static void completeCourse() {

		int[][] waypoints = {{60,30},{30,30},{30,60},{60,0}};

		for(int[] point : waypoints){
			nav.travelTo(point[0],point[1],true);
			while(nav.isTravelling()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
