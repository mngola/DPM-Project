package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;

public class USPoller extends Thread implements PollerInterface  {
	private int distance;
	private SampleProvider sensor;
	private float[] sensorData;
	
	public USPoller(SampleProvider us, float[] usData) {
		sensor = us;
		sensorData = usData; 
	}
	
	public void run() {
		while(true)
		{
			sensor.fetchSample(sensorData, 0);
			distance = (int) (sensorData[0]*100.0);
			filterDistance();
			try {Thread.sleep(Constants.POLLER_PERIOD);} catch(Exception e) {}
		}
	}
	
	@Override
	public int getDistance() {
		return distance;
	}

	private void filterDistance() {
		// TODO Implement a filter
	}

}
