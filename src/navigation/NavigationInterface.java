package navigation;

public interface NavigationInterface {
	public void setSpeeds(float lSpd, float rSpd);
	public void setSpeeds(int lSpd, int rSpd);
	public void setFloat();
	public void stopMotors();
	public void travelTo(double x, double y);
	public void turnTo(double angle, boolean stop);
	public void goForward(double distance);
}
