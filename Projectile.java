/**
 * Projectile.java represents a projectile that each tank can
 * shoot at each other as it bounces around the map.
 * @author Grant Fischer
 * @author Max Dauber
 * Period: 2
 * Date: 5/19/2017
 */

//Import necessary packages
import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class Projectile
{
	//Declares instance variables
	int x;
	int y;
	double dy;
	double dx;
	Ellipse2D.Double circle;
	
	//Declares and instantiates constants
	final Color projCol = Color.GRAY;
	final int DIAMETER = 10;
	final double PROJECTILE_MOVE = 60;
	
	/**
	 * The default constructor for a Projectile object that
	 * updates instance variables
	 * @param dy the initial change in the y coordinate
	 * @param dx the initial change in the x coordinate
	 * @param x the initial  in the y coordinate
	 * @param y the initial  in the x coordinate
	 */
	public Projectile(double dy, double dx, int x, int y)
	{
		this.dy = dy;
		this.dx = dx;
		this.y = y;
		this.x = x;
		circle = new Ellipse2D.Double(x, y, DIAMETER, DIAMETER);
	}
	
	/**
	 * Gets the actual shape object of the projectile
	 * @return the Circle object of the projectile
	 */
	public Ellipse2D.Double getCircle()
	{
		return circle;
	}
	
	/**
	 * Reverses the vertical direction of the projectile
	 */
	public void flipDY()
	{
		dy *= -1;
	}
	
	/**
	 * Reverses the horozontal direction of the projectile
	 */
	public void flipDX()
	{
		dx *= -1;
	}
	
	/**
	 * Moves the projectile and updates it actual Shape object
	 */
	public void move()
	{
		x += (int)dx;
		y += (int)dy;
		circle = new Ellipse2D.Double(x, y, DIAMETER, DIAMETER);
	}
	
	/**
	 * Gets the x coordinate of the projectile
	 * @return the x coordinate of the projectile
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Gets the y coordinate of the projectile
	 * @return the y coordinate of the projectile
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * Gets the change in the x coordinate of the projectile
	 * @return the change in the x coordinate of the projectile
	 */
	public double getDX()
	{
		return dx;
	}
	
	/**
	 * Gets the change in the y coordinate of the projectile
	 * @return the change in the y coordinate of the projectile
	 */
	public double getDY()
	{
		return dy;
	}
}
