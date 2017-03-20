package main;

import navigation.FullNavigator;
import navigation.Navigation;
import localization.USLocalizer;
import localization.USLocalizer.LocalizationType;
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
import wifi.WifiConnection;

import java.util.Map;

import display.Display;

public class DpmProject {

	//Motors
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	//Sensors
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static FullNavigator nav;
	//Wifi
	private static final String SERVER_IP = "192.168.137.1";
	private static final int TEAM_NUMBER = 8;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

	public static void main(String[] args) {
		//Instaniate objects

		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usDistance = usSensor.getMode("Distance");
		TextLCD t = LocalEV3.get().getTextLCD();		


		Odometer odometer = new Odometer(leftMotor, rightMotor);
		USPoller usPoller = new USPoller(usDistance);
		nav = new FullNavigator(odometer,usPoller);
		//USLocalizer usl = new USLocalizer(odometer, usPoller, LocalizationType.FALLING_EDGE, nav);

		usPoller.start();
		odometer.start();
		nav.start();

		Display print = new Display(odometer);
		print.start();
		//wifiPrint();
		//usl.doLocalization();
		//nav.turnTo(0.0, true);
		//odometer.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] { true, true, true });

		//nav.setFloat();
		completeCourse();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}


	private static void wifiPrint() {
		System.out.println("Running..");
		WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
		try {
			@SuppressWarnings("rawtypes")
			Map data = conn.getData();

			// Example 1: Print out all received data
			System.out.println("Map:\n" + data);

			// Example 2 : Print out specific values
			int fwdTeam = ((Long) data.get("FWD_TEAM")).intValue();
			System.out.println("Forward Team: " + fwdTeam);

			int w1 = ((Long) data.get("w1")).intValue();
			System.out.println("Defender zone size w1: " + w1);

			// Example 3: Compare value
			String orientation = (String) data.get("omega");
			if (orientation.equals("N")) {
				System.out.println("Orientation is North");
			}
			else {
				System.out.println("Orientation is not North");
			}

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void completeCourse() {

		int[][] waypoints = {{0,60},{0,0}};

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
