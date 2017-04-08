package main;

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
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import odometry.Odometer;
import polling.GyroPoller;
import polling.USPoller;
import wifi.WifiConnection;

import java.util.Map;

import constants.Constants;
import behaviour.BehaviorAvoid;
import behaviour.BehaviorMove;
import behaviour.BehaviourDefend;
import display.Display;

public class DpmProject {

	//Motors
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor launchMotor1 = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor launchMotor2 = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	//Sensors
	private static final Port usPort1 = LocalEV3.get().getPort("S1");
	private static final Port usPort2 = LocalEV3.get().getPort("S4");
	private static final Port colorPort = LocalEV3.get().getPort("S2");
	private static final Port gyroPort = LocalEV3.get().getPort("S3");
	@SuppressWarnings("unused")
	private static Navigation nav;
	public static Launcher launch;
	public static Odometer odometer;
	private static USLocalizer usl;
	private static LightLocalizer lightloc;
	public static SampleProvider colorSensor;
	public static float[] colorData;

	//Wifi
	private static final String SERVER_IP = "192.168.2.3"; //Competition IP: 192.168.2.3
	private static final int TEAM_NUMBER = 8;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

	//Wifi Data
	public static int d1 = 7;
	private static int[] w;
	public static int[] b = new int[] {-1,2};
	public static String orientation = "E";
	private static int corner=1;
	private static boolean attack=true;

	//Grid Details
	public static int gridWidth = 12;
	public static int gridHeight = 12;
	public static int targetX = 5;
	public static int targetY = 10;
	public static double tileLength = 30.48;
	static int NUMBER_SHOT = 0;
	public static double PositionforDisp[] = new double [3];
	public static SensorMode colorValue;
	public static EV3UltrasonicSensor usSensor1;
	public static EV3UltrasonicSensor usSensor2;


	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) {
		usSensor1 = new EV3UltrasonicSensor(usPort1);
		SampleProvider usDistance1 = usSensor1.getMode("Distance");
		usSensor2 = new EV3UltrasonicSensor(usPort2);
		SampleProvider usDistance2 = usSensor2.getMode("Distance");
		TextLCD t = LocalEV3.get().getTextLCD();	
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");		
		float[] colorData = new float[colorValue.sampleSize()];	
		EV3GyroSensor gyroSensor = new EV3GyroSensor(gyroPort);
		SampleProvider gyroSamples = gyroSensor.getMode("Angle");

		GyroPoller gPoller = new GyroPoller(gyroSamples);
		Odometer odometer = new Odometer(leftMotor, rightMotor,gPoller);
		USPoller usPoller1 = new USPoller(usDistance1);
		USPoller usPoller2 = new USPoller(usDistance2);
		nav = new Navigation(odometer,usPoller1,usPoller2);

		lightloc = new LightLocalizer(odometer, colorValue, colorData,leftMotor,rightMotor);
		usl = new USLocalizer(odometer, usPoller1, LocalizationType.FALLING_EDGE, leftMotor, rightMotor);
		launch = new Launcher(odometer,launchMotor1,launchMotor2);
		Display print = new Display(odometer);

		gPoller.start();
		usPoller1.start();
		usPoller2.start();
		odometer.start();
		launch.start();
		wifiPrint();
		print.start();

		usl.doLocalization();
		lightloc.doTransition();
		lightloc.doLocalization();
		Sound.beep();

		gyroSensor.reset();
		switch(corner) {
		case 1:
			odometer.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] { true, true, true });
			break;
		case 2:
			odometer.setPosition(new double[] { (gridWidth-2)*tileLength, 0.0, 90.0 }, new boolean[] { true, true, true });
			gPoller.setAngle(90);
			break;
		case 3:
			odometer.setPosition(new double[] { (gridWidth-2)*tileLength, (gridHeight-2)*tileLength, 180.0 }, new boolean[] { true, true, true });
			gPoller.setAngle(180);
			break;
		case 4:
			odometer.setPosition(new double[] { 0.0, (gridHeight-2)*tileLength, 270.0 }, new boolean[] { true, true, true });
			gPoller.setAngle(270);
			break;
		}
		odometer.setGyroActive(true);

		if(attack){
			//Save the time
			double timeStart = System.currentTimeMillis();


			//Lock the launchers
			launchMotor1.stop();
			launchMotor2.stop();

			//Find where the dispenser is
			PositionforDisp = getDispenserPosition();
			///////////////////////////////////////////////////////////////
			// create each behavior
			Behavior move = new BehaviorMove(PositionforDisp[0],PositionforDisp[1]);
			Behavior avoid = new BehaviorAvoid();		

			// define an array (vector) of existing behaviors, sorted by priority
			Behavior behaviors[] = { move, avoid };

			// add the behavior vector to a new arbitrator and start arbitration
			Arbitrator arbitrator = new Arbitrator(behaviors);
			arbitrator.go();
			////////////////////////////////////////////////////////////////
			/*Enter the while loop until the end which is basically these steps repeated over and over
			 * 1-Go to the dispenser
			 * 2-Pick up the ball
			 * 3-Go to the shooting zone
			 * 4-Shoot the ball
			 */
			while(System.currentTimeMillis() - timeStart < 7*60*1000){
				//Go to the dispenser
				Navigation.travelTo(PositionforDisp[0], PositionforDisp[1]);
				//Find the dispenser
				findDispenser(colorData,colorValue, odometer);

				//Pick up the ball
				Navigation.travelTo(PositionforDisp[0], PositionforDisp[1]);
				Navigation.turnTo(PositionforDisp[2], true);
				ballDrop();

				//Go to the shooting zone
				Navigation.travelTo(targetX*tileLength, (targetY-d1)*tileLength);
				launch.fire(targetX*tileLength, targetY*tileLength, d1);
			}
		}
		else{
			launchMotor1.setSpeed(50);
			launchMotor2.setSpeed(50);
			launchMotor1.rotate(-45,true);
			launchMotor2.rotate(-45,false);
			//Lock the launchers
			launchMotor1.stop();
			launchMotor2.stop();

			Behavior move = new BehaviourDefend(targetX*tileLength,(targetY - d1 + 0.5)*tileLength);
			Behavior avoid = new BehaviorAvoid();		

			// define an array (vector) of existing behaviors, sorted by priority
			Behavior behaviors[] = { move, avoid };

			// add the behavior vector to a new arbitrator and start arbitration
			Arbitrator arbitrator = new Arbitrator(behaviors);
			arbitrator.go();		
		}

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
				attack = true;
			} 
			else 
			{
				corner = ((Long) data.get("DEF_CORNER")).intValue();
				attack = false;
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

	/**
	 * Depending on the dispenser position and orientation, it calculates the position and angle our robot should have to get the ball from the dispenser
	 * @return An array built like this: [x,y,theta]
	 */
	private static double[] getDispenserPosition(){

		double []position = new double[3];

		switch(orientation){
		case "N":
			position[0] = tileLength*b[0];
			position[1] = tileLength*b[1] + Constants.CLAW_DISTANCE + Constants.BALLDROP_DISTANCE;
			position[2] = 90;
			break;
		case "S":
			position[0] = tileLength*b[0];
			position[1] = tileLength*b[1] - Constants.CLAW_DISTANCE - Constants.BALLDROP_DISTANCE;
			position[2] = 270;
			break;
		case "E":
			position[0] = tileLength*b[0]+ Constants.CLAW_DISTANCE + Constants.BALLDROP_DISTANCE;
			position[1] = tileLength*b[1];
			position[2] = 0;
			break;
		case "W":
			position[0] = tileLength*b[0]- Constants.CLAW_DISTANCE - Constants.BALLDROP_DISTANCE;
			position[1] = tileLength*b[1];
			position[2] = 180;
			break;	

		}

		return position;
	}

	/**
	 * Makes a beep and then wait 3 seconds for the ball to drop
	 */
	public static void ballDrop(){
		Sound.beep();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void findDispenser(float[] colorData, SampleProvider colorSensor, Odometer odom){

		launchMotor1.setSpeed(100);
		launchMotor2.setSpeed(100);
		launchMotor1.rotate(50,true);
		launchMotor2.rotate(50,false);
		launchMotor1.stop();
		launchMotor2.stop();
		//	NUMBER_SHOT += 1;


		Navigation.turnTo(PositionforDisp[2]+45,true);

		int lines = 0;
		boolean overLine = false;
		boolean firstLine = true;



		while (lines < 2) {
			leftMotor.forward();
			rightMotor.backward();
			colorSensor.fetchSample(colorData, 0);
			if ((colorData[0] < Constants.LOWER_LIGHT) && (!overLine)) 
			{
				Sound.beep();
				lines = lines + 1;
				if(firstLine){
					if(orientation == "E" || orientation == "W"){
						odom.setY(b[1]*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians(odom.getTheta())));
					}
					else if(orientation == "N" || orientation == "S"){
						odom.setX(b[0]*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians(odom.getTheta())));
					}
					firstLine = false;
				}
				else{
					if(orientation == "E"){
						odom.setX(Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians(odom.getTheta())));
					}

					else if(orientation == "W"){
						odom.setX((gridWidth-1)*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians(odom.getTheta())));
					}
					else if(orientation == "N"){
						odom.setY(Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians(odom.getTheta())));
					}
					else if(orientation == "S"){
						odom.setY((gridHeight-1)*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians(odom.getTheta())));
					}

				}


				overLine = true;
			}
			if ((colorData[0] > Constants.UPPER_LIGHT) && (overLine)) 
			{
				overLine = false;
			}
		}
		Navigation.stopMotors();
		Sound.beep();
	}
}
