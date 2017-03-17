package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.robotics.filter.MedianFilter;
import lejos.robotics.filter.OffsetCorrectionFilter;

public class USPoller extends Thread implements PollerInterface  {
	private int distance;
	private SampleProvider sensor;
	private float[] sensorData;
	
	public USPoller(SampleProvider us) {
		sensor = us;
		sensorData = new float[sensor.sampleSize()];
	}
	
	public void run() {
		while(true)
		{
			offsetFilterDistance();
			//sensor.fetchSample(sensorData, 0);
			//distance = (int) (sensorData[0]*100.0);
			try {Thread.sleep(Constants.POLLER_PERIOD);} catch(Exception e) {}
		}
	}
	
	@Override
	public int getDistance() {
		return distance;
	}

	private void offsetFilterDistance() {
		OffsetCorrectionFilter average = new OffsetCorrectionFilter(sensor);
		float[] sample = new float[average.sampleSize()];
		average.fetchSample(sample, 0);
		distance = (int) (average.getMean()[0]*100.0);
	}

}
