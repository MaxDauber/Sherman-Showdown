/**
 * Tank.java represents a tank that the user moves around the map trying
 * to shoot other tanks and remain the last one standing.
 * @author Grant Fischer
 * @author Max Dauber
 * Period: 2
 * Date: 5/19/2017
 */

//Import necessary packages
import java.awt.Color;
import java.awt.Polygon;

public class Tank
{
	//Declares instance variables
	private int xtopright;
	private int ytopright;
	private int xtopleft;
	private int ytopleft;
	private int xbottomright;
	private int ybottomright;
	private int xbottomleft;
	private int ybottomleft;
	private int[] xs;
	private int[] ys;
	private double centerX;
	private double centerY;
	private double angle;
	private Polygon tankShape;
	private int weapon;
	private Color col;
	
	//Declares and instantiates constants
	final double TWO_PI = 2 * Math.PI;
	final int STARTING_WEAPON = 0;
	final int TANK_WIDTH = 30;
	final int TANK_LENGTH = 50;
	final double STARTING_ANGLE = Math.atan(1.0 * TANK_LENGTH / TANK_WIDTH);
	final double CENTRAL_ANGLE = ((Math.PI / 2) - STARTING_ANGLE) * 2;
	final double RADIUS = (Math.sqrt(Math.pow(TANK_LENGTH,  2) + 
			Math.pow(TANK_WIDTH, 2))) / 2.0;
	
	/**
	 * The default constructor for the Tank object that
	 * updates instance variables
	 * @param x the initial x coordinate of the tank
	 * @param y the initial y coordinate of the tank
	 * @param ang the initial angle of the tank
	 * @param col the color of the tank
	 */
	public Tank(int x, int y, double ang, Color col)
	{
		angle = ang;
		
		centerX = x;
		centerY = y;
		
		xtopright = (int)(centerX - RADIUS * Math.cos(Math.PI + this.angle));
		ytopright = (int)(centerY + RADIUS * Math.sin(Math.PI + this.angle));
		
		xtopleft = (int)(centerX + RADIUS * Math.cos(Math.PI + this.angle));
		ytopleft = (int)(centerY + RADIUS * Math.sin(Math.PI + this.angle));
		
		xbottomright = (int)(centerX - RADIUS *
				Math.cos(Math.PI + this.angle));
		ybottomright = (int)(centerY - RADIUS *
				Math.sin(Math.PI + this.angle));
		
		xbottomleft = (int)(centerX + RADIUS * Math.cos(Math.PI + this.angle));
		ybottomleft = (int)(centerY - RADIUS * Math.sin(Math.PI + this.angle));
		
		
		weapon = STARTING_WEAPON;
		this.col = col;
		
		xs = new int[]{xtopright, xtopleft, xbottomleft, xbottomright};
		ys = new int[]{ytopright, ytopleft, ybottomleft, ybottomright};
		
		tankShape = new Polygon(xs, ys, 4);
	}
	
	/**
	 * Moves the center of the tank forward based off of which direction
	 * it is facing then redraws the tank around the center
	 * @param dy the amount to move forward by
	 */
	public void moveFoward(double dy)
	{
		centerY -= (dy * Math.cos((Math.PI / 2) -
				(CENTRAL_ANGLE / 2) - angle));
		centerX += (dy * Math.sin((Math.PI / 2) -
				(CENTRAL_ANGLE / 2) - angle));
		setAngle(0);
	}
	
	/**
	 * Gets the center x coordinate of the tank
	 * @return the center x coordinate of the tank
	 */
	public double getCenterX()
	{
		return centerX;
	}
	
	/**
	 * Gets the center y coordinate of the tank
	 * @return the center y coordinate of the tank
	 */
	public double getCenterY()
	{
		return centerY;
	}
	
	/**
	 * Gets an array of the tank's verticies' x coordinates
	 * @return an array of the tank's verticies' x coordinates
	 */
	public int[] getXs()
	{
		return xs;
	}
	
	/**
	 * Gets an array of the tank's verticies' y coordinates
	 * @return an array of the tank's verticies' y coordinates
	 */
	public int[] getYs()
	{
		return ys;
	}
	
	/**
	 * Rotates the tank by a specified amount of radians
	 * @param dAngle the amount of radians to change the tank's angle by
	 */
	public void setAngle(double dAngle)
	{
		angle += dAngle;
		angle %= TWO_PI;
		
		xtopleft = (int)(centerX - RADIUS *
				Math.cos(Math.PI - angle - CENTRAL_ANGLE));
		ytopleft = (int)(centerY - RADIUS *
				Math.sin(Math.PI - angle - CENTRAL_ANGLE));
		
		xbottomright = (int)(centerX + RADIUS *
				Math.cos(Math.PI - angle - CENTRAL_ANGLE));
		ybottomright = (int)(centerY + RADIUS *
				Math.sin(Math.PI - angle - CENTRAL_ANGLE));
		
		xtopright = (int)(centerX + RADIUS * Math.cos(angle));
		ytopright = (int)(centerY - RADIUS * Math.sin(angle));
		
		xbottomleft = (int)(centerX - RADIUS * Math.cos(angle));
		ybottomleft = (int)(centerY + RADIUS * Math.sin(angle));
		
		xs = new int[]{xtopright, xtopleft, xbottomleft, xbottomright};
		ys = new int[]{ytopright, ytopleft, ybottomleft, ybottomright};
		tankShape = new Polygon(xs, ys, 4);
	}
	
	/**
	 * Gets the angle the tank is facing
	 * @return the angle the tank is facing
	 */
	public double getAngle()
	{
		return angle;
	}
	
	/**
	 * Gets the color of the tank
	 * @return the color of the tank
	 */
	public Color getColor()
	{
		return col;
	}
	
	/**
	 * Gets the Polygon object of the tank
	 * @return the Polygon object of the tank
	 */
	public Polygon getPolygon()
	{
		return tankShape;
	}
}
