package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.robotics.filter.MedianFilter;

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
			//medianFilterDistance();
			sensor.fetchSample(sensorData, 0);
			distance = (int) (sensorData[0]*100.0);
			try {Thread.sleep(Constants.POLLER_PERIOD);} catch(Exception e) {}
		}
	}
	
	@Override
	public int getDistance() {
		return distance;
	}

	private void medianFilterDistance() {
		SampleProvider average = new MedianFilter(sensor,5);
		float[] sample = new float[average.sampleSize()];
		average.fetchSample(sample, 0);
		distance = (int) (sample[0]*100.0);
	}

}
