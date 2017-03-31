package polling;

import constants.Constants;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.robotics.filter.MedianFilter;
import lejos.robotics.filter.OffsetCorrectionFilter;

public class USPoller extends Thread implements PollerInterface  {
	private int distance1;
	private int distance2;
	private OffsetCorrectionFilter sensor1;
	private OffsetCorrectionFilter sensor2;
	float[] sample1;
	float[] sample2;
	
	public USPoller(SampleProvider us1,SampleProvider us2) {
		sensor1 = new OffsetCorrectionFilter(us1);
		sensor2 = new OffsetCorrectionFilter(us2);
		sample1 = new float[sensor1.sampleSize()];
		sample2 = new float[sensor2.sampleSize()];
	}
	
	public void run() {
		while(true)
		{
			sensor1.fetchSample(sample1, 0);
			sensor2.fetchSample(sample2, 0);
			distance1 = (int) (sensor1.getMean()[0]*100.0);
			distance2 = (int) (sensor2.getMean()[0]*100.0);
			try {Thread.sleep(Constants.USPOLLER_PERIOD);} catch(Exception e) {}
		}
	}
	
	@Override
	public int getDistance() {
		return distance1;
	}

}
