package odometry;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public interface OdometerInterface {
	public void getPosition(double[] position);
	public double getX();
	public double getY();
	public double getTheta();
	public void setPosition(double[] position, boolean[] update);
	public void setX(double x);
	public void setY(double y);
	public void setTheta(double theta);
	public EV3LargeRegulatedMotor [] getMotors();
}
