package odometry;

import constants.Constants;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class OdometerCorrection extends Thread {
	
	private Odometer odometer;
	private SampleProvider colorSensor;
	private float[] colorData;
	boolean overLine = false;
	double[] lightPos = new double [2];
	double errormargin = 1.5;
	boolean searchLine = true;
	double tileLength = 30.48;

	public OdometerCorrection(Odometer odo, SampleProvider colSen, float[] colData){
		odometer = odo;
		colorSensor = colSen;
		colorData = colData;
	}
	
	public void run(){
	
	while(true){
		colorSensor.fetchSample(colorData, 0);
		if ((colorData[0] < Constants.LOWER_LIGHT) && (!overLine)){ 
			Sound.beep();
			
			lightPos[0] = odometer.getX()-Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta())));
			lightPos[1] = odometer.getY()-Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta())));
			
			//Search in x
			
			if(-errormargin <= lightPos[0] && lightPos[0] <=errormargin){
				odometer.setX(0+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(odometer.getTheta()));
				searchLine = false;
			}
			else if(-errormargin + 1*tileLength  <= lightPos[0] && lightPos[0] <=  1*tileLength+errormargin){
				odometer.setX(1*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 2*tileLength  <= lightPos[0] && lightPos[0] <=  2*tileLength+errormargin){
				odometer.setX(2*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 3*tileLength  <= lightPos[0] && lightPos[0] <=  3*tileLength+errormargin){
				odometer.setX(3*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 4*tileLength  <= lightPos[0] && lightPos[0] <=  4*tileLength+errormargin){
				odometer.setX(4*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 5*tileLength  <= lightPos[0] && lightPos[0] <=  5*tileLength+errormargin){
				odometer.setX(5*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 6*tileLength  <= lightPos[0] && lightPos[0] <=  6*tileLength+errormargin){
				odometer.setX(6*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 7*tileLength  <= lightPos[0] && lightPos[0] <=  7*tileLength+errormargin){
				odometer.setX(7*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 8*tileLength  <= lightPos[0] && lightPos[0] <=  8*tileLength+errormargin){
				odometer.setX(8*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 9*tileLength  <= lightPos[0] && lightPos[0] <=  9*tileLength+errormargin){
				odometer.setX(9*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 10*tileLength  <= lightPos[0] && lightPos[0] <=  10*tileLength+errormargin){
				odometer.setX(10*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			else if(-errormargin + 11*tileLength  <= lightPos[0] && lightPos[0] <=  12*tileLength+errormargin){
				odometer.setX(12*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.cos(Math.toRadians((odometer.getTheta()))));
				searchLine = false;
			}
			
			
			//Search in y
			
			if(searchLine){
				if(-errormargin  <= lightPos[1] && lightPos[1] <= errormargin){
					odometer.setY(Constants.LIGHT_SENSOR_DISTANCE*Math.sin(odometer.getTheta()));
				}
				else if(-errormargin + 1*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 1*tileLength){
					odometer.setY(1*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 2*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 2*tileLength){
					odometer.setY(2*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 3*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 3*tileLength){
					odometer.setY(3*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 4*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 4*tileLength){
					odometer.setY(4*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 5*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 5*tileLength){
					odometer.setY(5*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 6*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 6*tileLength){
					odometer.setY(6*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 7*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 7*tileLength){
					odometer.setY(7*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 8*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 8*tileLength){
					odometer.setY(8*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 9*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 9*tileLength){
					odometer.setY(9*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 10*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 10*tileLength){
					odometer.setY(10*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 11*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 11*tileLength){
					odometer.setY(11*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
				else if(-errormargin + 12*tileLength  <= lightPos[1] && lightPos[1] <= errormargin+ 12*tileLength){
					odometer.setY(1*tileLength+Constants.LIGHT_SENSOR_DISTANCE*Math.sin(Math.toRadians((odometer.getTheta()))));
				}
			
			overLine = true;
			
			
		}
		}
		
		
		if ((colorData[0] > Constants.UPPER_LIGHT) && (overLine)){
			overLine = false;
		}
		
		try {
			Thread.sleep(Constants.CORRECTION_PERIOD);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	

}
}

