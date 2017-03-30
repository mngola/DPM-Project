package main;

import navigation.FullNavigator;
import navigation.Navigation;
import localization.LightLocalizer;
import localization.USLocalizer;
import localization.USLocalizer.LocalizationType;
import launcher.Launcher;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import odometry.Odometer;
import polling.GyroPoller;
import polling.USPoller;
import utility.Utility;
import wifi.WifiConnection;

import java.util.Map;

import display.Display;

public class DpmProject {

	//Motors
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor launchMotor1 = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor launchMotor2 = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	//Sensors
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final Port colorPort = LocalEV3.get().getPort("S2");
	private static final Port gyroPort = LocalEV3.get().getPort("S3");
	private static FullNavigator nav;
	private static Launcher launch;
	
	//Wifi
	private static final String SERVER_IP = "192.168.2.10";
	private static final int TEAM_NUMBER = 8;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

	//Wifi Data
	private static int d1;
	private static int[] w;
	private static int[] b;
	private static String orientation;
	private static int corner;

	//Grid Details
	private static int gridWidth = 4;
	private static int gridHeight = 12;
	private static int targetX = 5;
	private static int targetY = 6;
	private static double tileLength = 30.48;

	public static void main(String[] args) {
		//Instaniate objects

		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usDistance = usSensor.getMode("Distance");
		TextLCD t = LocalEV3.get().getTextLCD();	
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");		
		float[] colorData = new float[colorValue.sampleSize()];	
		EV3GyroSensor gyroSensor = new EV3GyroSensor(gyroPort);
		SampleProvider gyroSamples = gyroSensor.getMode("Angle");

		GyroPoller gPoller = new GyroPoller(gyroSamples);
		Odometer odometer = new Odometer(leftMotor, rightMotor,gPoller);
		USPoller usPoller = new USPoller(usDistance);
		
		
		nav = new FullNavigator(odometer,usPoller);
		LightLocalizer lightloc = new LightLocalizer(odometer, colorValue, colorData, nav,leftMotor,rightMotor);
		USLocalizer usl = new USLocalizer(odometer, usPoller, LocalizationType.FALLING_EDGE, nav, leftMotor, rightMotor);
		launch = new Launcher(odometer,nav,launchMotor1,launchMotor2);
		Display print = new Display(odometer);

		usPoller.start();
		odometer.start();
		nav.start();
		launch.start();
		wifiPrint();
		print.start();

		usl.doLocalization();
		lightloc.doTransition();
		lightloc.doLocalization();
		
		switch(corner) {
			case 1:
				odometer.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] { true, true, true });
				break;
			case 2:
				odometer.setPosition(new double[] { (gridWidth-2)*tileLength, 0.0, 0.0 }, new boolean[] { true, true, true });
				break;
			case 3:
				odometer.setPosition(new double[] { (gridWidth-2)*tileLength, (gridHeight-2)*tileLength, 0.0 }, new boolean[] { true, true, true });
				break;
			case 4:
				odometer.setPosition(new double[] { 0.0, (gridHeight-2)*tileLength, 0.0 }, new boolean[] { true, true, true });
				break;
		}
		gyroSensor.reset();
		gPoller.start();
		attack();
		//nav.travelTo(0, tilelength);
		//nav.travelTo(2*tilelength, 4*tilelength);
		//nav.travelTo(0,0);
		//nav.turnTo(0, true);


		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}


	private static void wifiPrint() {
		System.out.println("Running..");
		WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
		try {
			@SuppressWarnings("rawtypes")
			Map data = conn.getData();
			Sound.beep();
			// Example 1: Print out all received data
			System.out.println("Map:\n" + data);

			// Get the d1 value  
			d1 = ((Long) data.get("d1")).intValue();
			int[] defence = {((Long) data.get("w1")).intValue(),((Long) data.get("w2")).intValue()};
			w = defence;
			int[] balls = {((Long) data.get("bx")).intValue(),((Long) data.get("by")).intValue()};
			b = balls;
			orientation = (String) data.get("omega");
			if(((Long) data.get("FWD_TEAM")).intValue() == TEAM_NUMBER) 
			{
				corner = ((Long) data.get("FWD_CORNER")).intValue();
			} 
			else 
			{
				corner = ((Long) data.get("DEF_CORNER")).intValue();
			}
			System.out.println("d1: " + d1 );
			System.out.println("w1: " + w[0] + " w2: " + w[1]);
			System.out.println("bx: " + b[0] + " by: " + b[1]);
			System.out.println("Orientation: " + orientation);
			System.out.println("Starting Corner: " + corner);
			System.out.println("                              ");
			System.out.println("                              ");
			System.out.println("                              ");
			System.out.println("                              ");
			System.out.println("                              ");
			System.out.println("                              ");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void attack() {
		completeCourse(Utility.pointToDistance(b), Utility.pointToDistance(new int[] {1,1}) );
		launch.fire(targetX*tileLength, targetY*tileLength); //Change this line so that it calls the fire method in the launcher class	}
	}
	
	private static void completeCourse(int[] p1,int[] p2) {

		int[][] waypoints = {p1,p2};

		for(int[] point : waypoints){
			nav.travelTo(point[0],point[1],false);
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
