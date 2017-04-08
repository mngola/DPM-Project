package utility;

public class Utility {
	/**
	 * Converts distance to wheel rotations
	 * @param radius
	 * @param distance
	 * @return the number tachnometers to turn the wheels
	 */
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Converts robot rotation to wheel rotation
	 * @param radius
	 * @param width
	 * @param angle
	 * @return the number tachnometers to turn the wheels
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/**
	 * Wraps negatives angles
	 * @param angle
	 * @return the "wrapped" angle
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	/**
	 * Gets the minimum angle between 2 points 
	 * @param a
	 * @param b
	 * @return the angle
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}

	/**
	 * Converts a point on the board to distances needed to travel
	 * @param point a point on the board
	 * @return an array containing the distances the robot needs to travel to reach that point
	 */
	public static int[] pointToDistance(int[] point)
	{
		int[] d = new int[2];
		d[0] = point[0] * 30;
		d[1] = point[1] * 30;
		return d;
	}
}
