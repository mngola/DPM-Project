package Testing;

import display.Display;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import localization.USLocalizer;
import navigation.FullNavigator;
import navigation.Navigation;
import odometry.Odometer;
import polling.USPoller;

public class Tests extends Thread {
	
	
	private localization.USLocalizer.LocalizationType locType = localization.USLocalizer.LocalizationType.FALLING_EDGE;
	
	
	public void run(){
		//All the instances
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(Navigation.leftMotor, Navigation.rightMotor);
		Display display = new Display(odometer, t);
		USPoller uspoller = new USPoller(null, null);
		Navigation navigation = new Navigation(odometer);
		FullNavigator fullnavi = new FullNavigator(odometer, null);
		USLocalizer usloc = new USLocalizer(odometer, uspoller, locType  , fullnavi);
		SquareDriver drive = new SquareDriver();
		Navigate navigate = new Navigate(navigation);
	
		//Start the threads
		odometer.start();
		
		//Comment out all the tests you do not do
	
		//Square Driver (Test Odometry)
		display.start();
		drive.start();
	
		//Navigation (Test Navigation)
		display.start();
		navigate.start();
		
		//Localization
		
	
	while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	System.exit(0);
	
	}

}
