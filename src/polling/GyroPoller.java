package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;

public class GyroPoller extends Thread {
	private int angle, offset;
	private SampleProvider sensor;
	private float[] sensorData;
	
	/**
	 * Constructor
	 * @param us the sample provider this class will be polling
	 */
	public GyroPoller(SampleProvider us) {
		sensor = us;
		sensorData = new float[sensor.sampleSize()];
		offset = 0;
	}
	
	public void run() {
		while(true)
		{
			sensor.fetchSample(sensorData, 0);
			angle = (int) sensorData[0];
			try {Thread.sleep(Constants.GPOLLER_PERIOD);} catch(Exception e) {}
		}
	}
	/**
	 * 
	 * @return the heading according to the gyro
	 */
	public int getAngle() {
		return angle + offset;
	}
	
	/**
	 * 
	 * @param value to set the gyroscope heading to 
	 */
	public void setAngle(int value) {
		offset = value;
	}

}
