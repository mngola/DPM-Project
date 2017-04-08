package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.OffsetCorrectionFilter;

public class USPoller extends Thread implements PollerInterface  {
	private int distance;
	private SampleProvider sensor;
	
	/**
	 * Constructor
	 * @param us the sample provider this class will be polling
	 */
	public USPoller(SampleProvider us) {
		sensor = us;
	}
	
	public void run() {
		while(true)
		{
			offsetFilterDistance();
			try {Thread.sleep(Constants.USPOLLER_PERIOD);} catch(Exception e) {}
		}
	}
	
	@Override
	/**
	 * 
	 * @return the distance the sensor sees
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Computes the distance using the offset correction filter
	 */
	private void offsetFilterDistance() {
		OffsetCorrectionFilter average = new OffsetCorrectionFilter(sensor);
		float[] sample = new float[average.sampleSize()];
		average.fetchSample(sample, 0);
		distance = (int) (average.getMean()[0]*100.0);
	}

}