package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.robotics.filter.MedianFilter;
import lejos.robotics.filter.OffsetCorrectionFilter;

public class GyroPoller extends Thread {
	private int angle;
	private SampleProvider sensor;
	private float[] sensorData;
	
	public GyroPoller(SampleProvider us) {
		sensor = us;
		sensorData = new float[sensor.sampleSize()];
	}
	
	public void run() {
		while(true)
		{
			sensor.fetchSample(sensorData, 0);
			angle = (int) sensorData[0];
			try {Thread.sleep(Constants.GPOLLER_PERIOD);} catch(Exception e) {}
		}
	}
	
	public int getAngle() {
		return angle;
	}

}
