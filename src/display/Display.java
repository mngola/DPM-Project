package display;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import odometry.Odometer;
import constants.Constants;

public class Display extends Thread implements DisplayInterface  {

	private Odometer odom;
	int buttonChoice;

		// constructor.
		public Display(Odometer odom) {
			this.odom = odom;
		}
	
	public static TextLCD t = LocalEV3.get().getTextLCD();
		
	// run method
	public void run(){
		t.clear();
		System.out.flush();
		displayOdometer();
	}
	
	
	@Override
	public void displayMainMenu() {		
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

	@Override
	public void displaySensor() {		
	}
}
