package odometry;

public interface OdometerInterface {
	public void getPosition(double[] position, boolean[] update);
	public double getX();
	public double getY();
	public double getTheta();
	public void setPosition(double[] position, boolean[] update);
	public void setX(double x);
	public void setY(double y);
	public void setTheta(double theta);
	public int getLeftMotorTachoCount();
	public void setLeftMotorTachoCount(int leftMotorTachoCount);
	public int getRightMotorTachoCount();
	public void setRightMotorTachoCount(int rightMotorTachoCount);
}
