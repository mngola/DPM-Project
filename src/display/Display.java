package display;

import lejos.hardware.lcd.TextLCD;
import odometry.Odometer;
import lejos.hardware.Button;
import constants.Constants;

public class Display extends Thread implements DisplayInterface  {

	private TextLCD t;
	private Odometer odom;
	int buttonChoice;

		// constructor.
		public Display(Odometer odom, TextLCD t) {
			this.odom = odom;
			this.t = t;
		}
		
	// run method
	public void run(){
		
		//Clear the display
			t.clear();
		
		// ask the user what display he wants
					t.drawString("< Main | Local >", 0, 0);
					t.drawString("  Menu |        ", 0, 1);
					t.drawString("    Odometer    ", 0, 2);
					t.drawString("       |        ", 0, 3);
					t.drawString("       |        ", 0, 4);
		
		//Wait for the result
		buttonChoice = Button.waitForAnyPress();
		
		//Clear the display
				t.clear();
		
		switch(buttonChoice){
			
			//Main Menu
			case Button.ID_LEFT: displayMainMenu();
								break;
			//Localization
			case Button.ID_RIGHT: displayLocalization();
								break;
			//Odometer
			case Button.ID_ENTER: displayOdometer();
								break;
			default: break;
		}
		
		
	}
	
	
	@Override
	public void displayMainMenu() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayOdometer() {
		
		//Constants
		double[] position = new double[3];
		long displayStart, displayEnd;
		
		while (true) {
			
			displayStart = System.currentTimeMillis();

			// clear the lines for displaying odometry information
			t.drawString("X:              ", 0, 0);
			t.drawString("Y:              ", 0, 1);
			t.drawString("T:              ", 0, 2);

			// get the odometry information
			odom.getPosition(position);

			// display odometry information
			for (int i = 0; i < 3; i++) {
				t.drawString(formattedDoubleToString(position[i], 2), 3, i);
			}

			// throttle the OdometryDisplay
			displayEnd = System.currentTimeMillis();
			if (displayEnd - displayStart < Constants.DISPLAY_PERIOD) {
				try {
					Thread.sleep(Constants.DISPLAY_PERIOD - (displayEnd - displayStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that OdometryDisplay will be interrupted
					// by another thread
				}
			}
		}	
		
	}

	@Override
	public void displayLocalization() {
		// TODO Auto-generated method stub
		
	}
	
	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;
		
		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";
		
		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long)x;
			if (t < 0)
				t = -t;
			
			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}
			
			result += stack;
		}
		
		// put the decimal, if needed
		if (places > 0) {
			result += ".";
		
			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long)x);
			}
		}
		
		return result;
	}
	
	

}
