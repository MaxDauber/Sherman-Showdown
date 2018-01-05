/**
 * TankComponent.java contains draws and manages a randomly generated map
 * with Tank objects that players can move around to battle against each
 * other to be the last tank remaining.
 * @author Grant Fischer
 * @author Max Dauber
 * Period: 2
 * Date: 5/19/2017
 */

//Imports necessary packages
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;
import javax.swing.Timer;

public class TankComponent extends JComponent
			implements ActionListener, KeyListener
{
	//Declares instance variables
	int numTanks;
	ArrayList<Tank> tanks;
	boolean[][] gridPoints;
	int[][] gridPointsCount;
	ArrayList<Polygon> walls;
	int frameWidth;
	int frameHeight;
	int wallLength;
	int p1Score;
	int p2Score;
	int p3Score;
	int p4Score;
	int tank1ProjectileCount;
	int tank2ProjectileCount;
	int tank3ProjectileCount;
	int tank4ProjectileCount;
	Color p1Col;
	Color p2Col;
	Color p3Col;
	Color p4Col;
	ArrayList<Projectile> tank1Projectiles;
	ArrayList<Projectile> tank2Projectiles;
	ArrayList<Projectile> tank3Projectiles;
	ArrayList<Projectile> tank4Projectiles;
	int ultimateCount;
	ArrayList<ArrayList<Integer>> projectiles;
	boolean addedScore;
	boolean allowEnter;
	
	//Declares and instantiates constants
	final int TANK_WIDTH = 30;
	final int TANK_LENGTH = 50;
	final double STARTING_ANGLE = Math.atan(1.0 * TANK_LENGTH / TANK_WIDTH);
	final double CENTRAL_ANGLE = ((Math.PI / 2) - STARTING_ANGLE) * 2;
	final double TWO_PI = 2 * Math.PI;
	final double ANGLE_MOVE = TWO_PI / 100;
	final double TRANSLATE_MOVE = 4.0;
	final int GRID_SIZE = 10;
	final int WALL_WIDTH = 10;
	final int SCORE_GAP = 100;
	final Color WALL_COLOR = Color.BLACK;
	final Color PROJECTILE_COLOR = Color.GRAY;
	final int MAX_PROJECTILES = 15;
	final int GAP = 3;
	final int PROJECTILE_TIME = 200;
	final double PROJECTILE_MOVE = 5;
	final int DIAMETER = 10;
	final int RADIUS = DIAMETER / 2;
	final Set<Integer> pressed = new HashSet<Integer>();
	
	//Declares and Instantiates animation timer
	Timer drawTimer = new Timer(30, this);
	
	/**
	 * Default constructor for TankComponent which updates instance
	 * variables and starts the timer
	 * @param numPlayers the number of players to add to the game
	 */
	public TankComponent(int numPlayers)
	{
		numTanks = numPlayers;
		
		tanks = new ArrayList<>();
		projectiles = new ArrayList<ArrayList<Integer>>();
		ultimateCount = 0;
		p1Col = Color.BLUE;
		p2Col = Color.RED;
		makeTanks();
		
		walls = new ArrayList<>();
		gridPoints = new boolean[GRID_SIZE - 1][GRID_SIZE - 1];
		gridPointsCount = new int[gridPoints.length][gridPoints[0].length];
		populateGridPoints();
		
		p1Score = 0;
		p2Score = 0;
		p3Score = 0;
		p4Score = 0;
		
		tank1ProjectileCount = 0;
		tank2ProjectileCount = 0;
		tank3ProjectileCount = 0;
		tank4ProjectileCount = 0;
		
		tank1Projectiles = new ArrayList<Projectile>();
		tank2Projectiles = new ArrayList<Projectile>();
		tank3Projectiles = new ArrayList<Projectile>();
		tank4Projectiles = new ArrayList<Projectile>();
		
		addedScore = false;
		allowEnter = false;
				
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(true);
		drawTimer.start();
	}
	
	/**
	 * Fills the 2D gridPoints array with random booleans
	 */
	private void populateGridPoints()
	{
		for(int r = 0; r < gridPoints.length; r++)
		{
			for(int c = 0; c < gridPoints[r].length; c++)
			{
				double rand = Math.random();
				if(rand < .5)
					gridPoints[r][c] = true;
				else
					gridPoints[r][c] = false;
			}
		}
	}
	
	/**
	 * Creates and adds all the players' tanks to the list of tanks
	 */
	private void makeTanks()
	{
		Color c = Color.BLACK;
		for (int j = 0; j < numTanks; j++)
		{
			if (j == 0)
				c = p1Col;
			if (j == 1)
				c = p2Col;
			if (j == 2)
			{
				p3Col = Color.YELLOW;
				c = p3Col;
			}
			if (j == 3)
			{
				p4Col = Color.GREEN;
				c = p4Col;
			}
			int startingCenterX = j * 3 * (800 / 10) + 800 / 10 / 2;
			int startingCenterY = ((int)(Math.random() * 8 + 1)) *
					800 / 10 + 800 / 20;
			tanks.add(new Tank(startingCenterX, startingCenterY,
					STARTING_ANGLE, c));
		}
		for (int i = 0; i < tanks.size(); i++)
		{
			projectiles.add(new ArrayList<Integer>());
		}
	}
	
	/**
	 * Calls repaint method every time the animation timer goes off
	 * @param event the event caused by the animation timer
	 */
	public void actionPerformed(ActionEvent event)
	{
		repaint();
	}
	
	/**
	 * Adds each key being pressed to the set of keys
	 * @param e the event caused by the key press
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		pressed.add(e.getKeyCode());
	}
	
	/**
	 * Removes each key being released from the set of keys
	 * @param e the event caused by the key release
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		pressed.remove(e.getKeyCode());
	}
	
	/**
	 * Adds each key being typed to the set of keys
	 * @param e the event caused by the key being typed
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		pressed.add(e.getKeyCode());
	}
	
	/**
	 * Randomly draws a maze/grid of walls to represent the map
	 * @param gr2 the Graphics2D object to draw the walls in
	 */
	public void drawWalls(Graphics2D gr2)
	{
		gr2.setColor(WALL_COLOR);
		for (int r = 0; r < gridPoints.length - 1; r++)
		{
			for (int c = 0; c < gridPoints[r].length - 1; c++)
			{
				boolean current, right, down = false;
				if (r < gridPoints.length && c < gridPoints[r].length)
				{
					current = gridPoints[r][c];
					right = gridPoints[r + 1][c];
					down = gridPoints[r][c + 1];
					if (current == right && gridPointsCount[r][c] <= 2)
					{
						int topLeftX = r * wallLength + wallLength - WALL_WIDTH / 2;
						int topLeftY = c * wallLength + wallLength - WALL_WIDTH / 2;
						walls.add(new Polygon(new int[]{topLeftX, topLeftX +
								wallLength, topLeftX + wallLength, topLeftX},
								new int[]{topLeftY, topLeftY, topLeftY +
								WALL_WIDTH, topLeftY + WALL_WIDTH}, 4));
						++gridPointsCount[r][c];
					}
					if (current == down && gridPointsCount[r][c] <= 2)
					{
						int topLeftX = r * wallLength + wallLength - WALL_WIDTH / 2;
						int topLeftY = c * wallLength + wallLength - WALL_WIDTH / 2;
						walls.add(new Polygon(new int[]{topLeftX, topLeftX +
								WALL_WIDTH, topLeftX + WALL_WIDTH, topLeftX},
								new int[]{topLeftY, topLeftY, topLeftY +
								wallLength, topLeftY + wallLength}, 4));
						++gridPointsCount[r][c];
					}
				}
				if (r < gridPoints.length && c == gridPoints[r]. length)
				{
					current = gridPoints[r][c];
					right = gridPoints[r + 1][c];
					if (current == right && gridPointsCount[r][c] <= 2)
					{
						int topLeftX = r * wallLength + wallLength - WALL_WIDTH / 2;
						int topLeftY = c * wallLength + wallLength - WALL_WIDTH / 2;
						walls.add(new Polygon(new int[]{topLeftX, topLeftX +
								wallLength, topLeftX + wallLength, topLeftX},
								new int[]{topLeftY, topLeftY, topLeftY +
								WALL_WIDTH, topLeftY + WALL_WIDTH}, 4));
						++gridPointsCount[r][c];
					}
				}
				if (r == gridPoints.length && c < gridPoints[r]. length)
				{
					current = gridPoints[r][c];
					down = gridPoints[r][c + 1];
					if (current == down && gridPointsCount[r][c] <= 2)
					{
						int topLeftX = r * wallLength + wallLength -
								WALL_WIDTH / 2;
						int topLeftY = c * wallLength + wallLength -
								WALL_WIDTH / 2;
						walls.add(new Polygon(new int[]{topLeftX, topLeftX +
								WALL_WIDTH, topLeftX + WALL_WIDTH, topLeftX},
								new int[]{topLeftY, topLeftY, topLeftY +
								wallLength, topLeftY + wallLength}, 4));
						++gridPointsCount[r][c];
					}
				}
			}
		}
		for (Polygon p : walls)
		{
			gr2.fill(p);
		}
	}
	
	/**
	 * Draws the borders of the map
	 * @param gr2 the Graphics2D object to draw the frame borders in
	 */
	private void drawBorders(Graphics2D gr2)
	{
		gr2.setColor(WALL_COLOR);
		gr2.fillRect(0, 0, WALL_WIDTH, frameHeight - SCORE_GAP + WALL_WIDTH);
		gr2.fillRect(frameWidth - WALL_WIDTH, 0, WALL_WIDTH, frameHeight
				- SCORE_GAP + WALL_WIDTH);
		gr2.fillRect(0, 0, frameWidth, WALL_WIDTH);
		gr2.fillRect(0, frameHeight - SCORE_GAP, frameWidth, WALL_WIDTH);
	}
	
	/**
	 * Draws all the tanks in the list of tanks
	 * @param gr2 the Graphics2D object to draw the tanks in
	 */
	private void drawTanks(Graphics2D gr2)
	{
		for (int i = 0; i < numTanks; i++)
		{
			if (tanks.get(i) != null)
			{
				gr2.setColor(tanks.get(i).getColor());
				gr2.fill(tanks.get(i).getPolygon());
				gr2.setColor(Color.BLACK);
				gr2.draw(tanks.get(i).getPolygon());
			}
		}
	}
	
	/**
	 * Shows each player's score at the bottom of the canvas
	 * @param gr2 the Graphics2D object to write the scores in
	 */
	private void drawScore(Graphics2D gr2)
	{
		//Tank 1
		gr2.setColor(p1Col);
		gr2.setFont(new Font("Arial", Font.BOLD, 30));
		gr2.drawString("P1: " + p1Score, 20, frameHeight - 55);
		
		//Tank 2
		gr2.setColor(p2Col);
		gr2.setFont(new Font("Arial", Font.BOLD, 30));
		gr2.drawString("P2: " + p2Score, 20, frameHeight - 15);
		
		//Tank 3
		if (tanks.size() >= 3)
		{
			gr2.setColor(p3Col);
			gr2.setFont(new Font("Arial", Font.BOLD, 30));
			gr2.drawString("P3: " + p3Score, frameWidth - 90,
					frameHeight - 55);
		}
			
		//Tank 4
		if (tanks.size() == 4)
		{
			gr2.setColor(p4Col);
			gr2.setFont(new Font("Arial", Font.BOLD, 30));
			gr2.drawString("P4: " + p4Score, frameWidth - 90,
					frameHeight - 15);
		}
	}
	
	/**
	 * Determines if a tank can move forward or backward without
	 * 		colliding with another tank
	 * @param tank which specific tank to check for
	 * @param foward the direction of motion to check for
	 * @return whether or not the tank will collide with 
	 * 		another tank by translating
	 */
	private boolean willCollideTranslate(int tank, boolean foward)
	{
		boolean collides = false;
		Tank tempTank = new Tank((int)(tanks.get(tank - 1).getCenterX()),
				(int)(tanks.get(tank - 1).getCenterY()), tanks.get(tank - 1)
				.getAngle(), Color.BLUE);
		if (foward)
			tempTank.moveFoward(TRANSLATE_MOVE);
		else
			tempTank.moveFoward(-1 * TRANSLATE_MOVE);
		for (int m = 0; m < numTanks; m++)
		{
			if (m != tank - 1 && tanks.get(m) != null)
			{
				Tank tempTank2 = new Tank((int) (tanks.get(m).getCenterX()),
						(int)(tanks.get(m).getCenterY()), tanks.get(m)
						.getAngle(), Color.RED);
				if (tempTank.getPolygon().contains(tempTank2.getXs()[0],
						tempTank2.getYs()[0])  ||  tempTank.getPolygon()
						.contains(tempTank2.getXs()[1], tempTank2.getYs()[1])
						|| tempTank.getPolygon().contains(tempTank2.getXs()[2],
								tempTank2.getYs()[2]) || tempTank.getPolygon()
						.contains(tempTank2.getXs()[3], tempTank2.getYs()[3]))
				{
					collides = true;
				}
				if (tempTank2.getPolygon().contains(tempTank.getXs()[0],
						tempTank.getYs()[0]) || tempTank2.getPolygon()
						.contains(tempTank.getXs()[1], tempTank.getYs()[1])
						|| tempTank2.getPolygon().contains(tempTank.getXs()[2],
								tempTank.getYs()[2]) || tempTank2.getPolygon()
						.contains(tempTank.getXs()[3], tempTank.getYs()[3]))
				{
					collides = true;
				}
			}
		}
		return collides;
	}
	
	/**
	 * Determines if a tank can rotate left or right without
	 * 		colliding with another tank
	 * @param tank which specific tank to check for
	 * @param left the direction of motion to check for
	 * @return whether or not the tank will collide with 
	 * 		another tank by rotating
	 */
	private boolean willCollideRotate(int tank, boolean left)
	{
		boolean collides = false;
		Tank tempTank = new Tank((int)(tanks.get(tank - 1).getCenterX()),
				(int)(tanks.get(tank - 1).getCenterY()), tanks.get(tank - 1)
				.getAngle(), Color.BLUE);
		if (left)
			tempTank.setAngle(ANGLE_MOVE);
		else
			tempTank.setAngle(-1 * ANGLE_MOVE);
		for (int m = 0; m < numTanks; m++)
		{
			if (m != tank - 1 && tanks.get(m) != null)
			{
				Tank tempTank2 = new Tank((int) (tanks.get(m).getCenterX()),
						(int)(tanks.get(m).getCenterY()), tanks.get(m)
						.getAngle(), Color.RED);
				if (tempTank.getPolygon().contains(tempTank2.getXs()[0],
						tempTank2.getYs()[0])  ||  tempTank.getPolygon()
						.contains(tempTank2.getXs()[1], tempTank2.getYs()[1])
						|| tempTank.getPolygon().contains(tempTank2.getXs()[2],
								tempTank2.getYs()[2]) || tempTank.getPolygon().
						contains(tempTank2.getXs()[3], tempTank2.getYs()[3]))
				{
					collides = true;
				}
				if (tempTank2.getPolygon().contains(tempTank.getXs()[0],
						tempTank.getYs()[0]) || tempTank2.getPolygon()
						.contains(tempTank.getXs()[1], tempTank.getYs()[1])
						|| tempTank2.getPolygon().contains(tempTank.getXs()[2],
								tempTank.getYs()[2]) || tempTank2.getPolygon().
						contains(tempTank.getXs()[3], tempTank.getYs()[3]))
				{
					collides = true;
				}
			}
		}
		return collides;
	}
	
	/**
	 * Determines if a tank can move forward or backward without
	 * 		colliding with a wall
	 * @param tank which specific tank to check for
	 * @param foward the direction of motion to check for
	 * @return whether or not the tank will collide with 
	 * 		a wall by translating
	 */
	private boolean willCollideTranslateWall(int tank, boolean foward)
	{
		boolean collides = false;
		Tank tempTank = new Tank((int)(tanks.get(tank - 1).getCenterX()),
				(int)(tanks.get(tank - 1).getCenterY()),
				tanks.get(tank - 1).getAngle(), Color.BLUE);
		if (foward)
			tempTank.moveFoward(TRANSLATE_MOVE);
		else
			tempTank.moveFoward(-1 * TRANSLATE_MOVE);
		int[] tankXs = tempTank.getXs();
		int[] tankYs = tempTank.getYs();
		for (Polygon p : walls)
		{
			for (int i = 0; i < 4; i++)
			{
				if (p.contains(tankXs[i], tankYs[i]))
				{
					collides = true;
				}
			}
			int[] wallXs = p.xpoints;
			int[] wallYs = p.ypoints;
			for (int j = 0; j < 4; j++)
			{
				if (tempTank.getPolygon().contains(wallXs[j], wallYs[j]))
				{
					collides = true;
				}
			}
		}
		for (int k = 0; k < 4; k++)
		{
			if (tankXs[k] > frameWidth - WALL_WIDTH || tankXs[k] < WALL_WIDTH)
				collides = true;
			if (tankYs[k] > frameHeight - SCORE_GAP || tankYs[k] < WALL_WIDTH)
				collides = true;
		}
		return collides;
	}
	
	/**
	 * Determines if a tank can rotate left or right without
	 * 		colliding with a wall
	 * @param tank which specific tank to check for
	 * @param left the direction of motion to check for
	 * @return whether or not the tank will collide with 
	 * 		a wall by rotating
	 */
	private boolean willCollideRotateWall(int tank, boolean left)
	{
		boolean collides = false;
		Tank tempTank = new Tank((int)(tanks.get(tank - 1).getCenterX()),
				(int)(tanks.get(tank - 1).getCenterY()),
				tanks.get(tank - 1).getAngle(), Color.BLUE);
		if (left)
			tempTank.setAngle(ANGLE_MOVE);
		else
			tempTank.setAngle(-1 * ANGLE_MOVE);
		int[] tankXs = tempTank.getXs();
		int[] tankYs = tempTank.getYs();
		for (Polygon p : walls)
		{
			for (int i = 0; i < 4; i++)
			{
				if (p.contains(tankXs[i], tankYs[i]))
				{
					collides = true;
				}
			}
			int[] wallXs = p.xpoints;
			int[] wallYs = p.ypoints;
			for (int j = 0; j < 4; j++)
			{
				if (tempTank.getPolygon().contains(wallXs[j], wallYs[j]))
				{
					collides = true;
				}
			}
		}
		for (int k = 0; k < 4; k++)
		{
			if (tankXs[k] > frameWidth - WALL_WIDTH || tankXs[k] < WALL_WIDTH)
				collides = true;
			if (tankYs[k] > frameHeight - SCORE_GAP || tankYs[k] < WALL_WIDTH)
				collides = true;
		}
		return collides;
	}
	
	/**
	 * Spawns a projectile in the correct position based off of which tank
	 * 		fired, and with the correct initial direction
	 * @param tank the tank that needs to shoot the projectile
	 * @param the keycode for the key that was pressed to fire a projectile
	 * @return the Projectile object that is being created
	 */
	private Projectile createProjectile(int tank, int key)
	{
		pressed.remove(key);
		double tankAngle = CENTRAL_ANGLE / 2 + tanks.get(tank).getAngle();
		double dx = PROJECTILE_MOVE * Math.cos(tankAngle);
		double dy = -1 * PROJECTILE_MOVE * Math.sin(tankAngle);
		int topLeftY = tanks.get(tank).getYs()[1];
		int topRightY = tanks.get(tank).getYs()[0];
		int topLeftX = tanks.get(tank).getXs()[1];
		int topRightX = tanks.get(tank).getXs()[0];
		int centerY = (int) ((topLeftY + topRightY) / 2);
		int centerX = (int) ((topLeftX + topRightX) / 2);
		return new Projectile(dy, dx, centerX - RADIUS, centerY - RADIUS);
	}
	
	/**
	 * Updates the positions of every projectile and bounces them off walls
	 * @param tankProjectiles the ArrayList of projectile objects for the tank
	 * @param tankProjectileCount the number of current
	 * 		projectiles in the map for a specific tank
	 * @param gr2 the Graphics2D object to draw the projectiles in
	 * @param tank the specific tank whose projectiles
	 * 		are being manipulated
	 */
	private void moveProjectiles(ArrayList<Projectile> tankProjectiles,
			int tankProjectileCount, Graphics2D gr2, int tank)
	{
		if (tankProjectileCount >= 1)
		{
			for (int i = 0; i < tankProjectiles.size(); i++)
			{
				if (ultimateCount < projectiles.get(tank).get(i) +
						PROJECTILE_TIME)
				{
					tankProjectiles.get(i).move();
			    	gr2.setColor(PROJECTILE_COLOR);
					gr2.fill(tankProjectiles.get(i).getCircle());
				}
				if(ultimateCount == projectiles.get(tank).get(i) +
						PROJECTILE_TIME)
				{
					tankProjectiles.remove(i);
					tankProjectileCount--;
					projectiles.get(tank).remove(i);
				}
				for (Polygon p : walls)
				{
					if (tankProjectileCount >= 1)
					{
						double maxX = p.getBounds2D().getMaxX();
						double minX = p.getBounds2D().getMinX();
						double maxY = p.getBounds2D().getMaxY();
						double minY = p.getBounds2D().getMinY();
						double dx = tankProjectiles.get(i).getDX();
						double dy = tankProjectiles.get(i).getDY();
						double projLeftX = tankProjectiles.get(i).getX();
						double projLeftY = tankProjectiles.get(i).getY()
								+ RADIUS;
						double projRightX = tankProjectiles.get(i).getX()
								+ DIAMETER;
						double projRightY = tankProjectiles.get(i).getY()
								+ RADIUS;
						double projUpY = tankProjectiles.get(i).getY();
						double projUpX = tankProjectiles.get(i).getX()
								+ RADIUS;
						double projDownY = tankProjectiles.get(i).getY()
								+ DIAMETER;
						double projDownX = tankProjectiles.get(i).getX()
								+ RADIUS;

						if (dx > 0 && projRightY < maxY && projRightY > minY
								&& projRightX >= minX && projRightX <= maxX)
						{
							tankProjectiles.get(i).flipDX();
						}
						if (dx < 0 && projLeftY < maxY && projLeftY > minY
								&& projLeftX <= maxX && projLeftX >= minX)
						{
							tankProjectiles.get(i).flipDX();
						}
						if (dy > 0 && projDownX > minX && projDownX < maxX
								&& projDownY >= minY && projDownY <= maxY)
						{
							tankProjectiles.get(i).flipDY();
						}
						if (dy < 0 && projUpX > minX && projUpX < maxX
								&& projUpY <= maxY && projUpY >= minY)
						{
							tankProjectiles.get(i).flipDY();
						}
					}
				}
				if (tankProjectileCount >= 1 && (tankProjectiles.get(i).getX()
						<= WALL_WIDTH || tankProjectiles.get(i).getX()
						+ DIAMETER >= frameWidth - WALL_WIDTH))
				{
					tankProjectiles.get(i).flipDX();
				}
				if (tankProjectileCount >= 1 && (tankProjectiles.get(i).getY()
						<= WALL_WIDTH || tankProjectiles.get(i).getY()
						+ DIAMETER >= frameHeight - SCORE_GAP))
				{
					tankProjectiles.get(i).flipDY();
				}
			}
		}
	}
	
	/**
	 * Removes any tank from the map that get hit by a projectile
	 * @param tankProjectiles the ArrayList of projectile objects for the tank
	 */
	private void killTank(ArrayList<Projectile> tankProjectiles)
	{
		for (int m = 0; m < tanks.size(); m++)
		{
			for (int n = 0; n < tankProjectiles.size(); n++)
			{
				Tank tank = tanks.get(m);
				Projectile proj = tankProjectiles.get(n);
				if (tank != null && tank.getPolygon().contains(proj.getCircle()
						.getBounds2D()))
				{
					tanks.set(m, null);
				}
			}
		}
	}
	
	/**
	 * Displays the winning player below the map and updates their score
	 * @param gr2 the Graphics2D object to display the winner in
	 */
	private void getWinner(Graphics2D gr2)
	{
		int winner = 0;
		Tank temp = null;
		for (int i = 0; i < tanks.size(); i++)
		{
			if (tanks.get(i) != null)
			{
				winner = i + 1;
				temp = tanks.get(i);
				if (!addedScore)
				{
					addedScore = true;
					if (winner == 1)
						p1Score++;
					if (winner == 2)
						p2Score++;
					if (winner == 3)
						p3Score++;
					if (winner == 4)
						p4Score++;
				}
			}	
		}
		String winnerText = "Player " + winner + " Wins!";
		gr2.setColor(temp.getColor());
		gr2.setFont(new Font("Arial", Font.BOLD, 60));
		gr2.drawString(winnerText, 170, 840);
		gr2.setColor(Color.BLACK);
		gr2.setFont(new Font("Arial", Font.BOLD, 20));
		gr2.drawString("Press Enter For New Game.", 245, 875);
		allowEnter = true;

	}
	
	/**
	 * Manipulates tank 1 based off of specific keyboard inputs
	 * @param gr2 the Graphics2D object to draw the projectile in
	 */
	private void tank1Controls(Graphics2D gr2)
	{
	    if (pressed.contains(38))
	    	if (tanks.get(0) != null && !willCollideTranslate(1, true)
	    		&& !willCollideTranslateWall(1, true))
	    	{
	    		tanks.get(0).moveFoward(TRANSLATE_MOVE);
	    	}
	    if (pressed.contains(40))
	    	if (tanks.get(0) != null && !willCollideTranslate(1, false)
	    		&& !willCollideTranslateWall(1, false))
	    	{
	    	 	tanks.get(0).moveFoward(-1 * TRANSLATE_MOVE);
	    	}
	    if (pressed.contains(39))
	    	if (tanks.get(0) != null && !willCollideRotate(1, false)
	    		&& !willCollideRotateWall(1, false))
	    	{
	    		tanks.get(0).setAngle(-1 * ANGLE_MOVE);
	    	}
	    if (pressed.contains(37))
	    	if (tanks.get(0) != null && !willCollideRotate(1, true)
	    		&& !willCollideRotateWall(1, true))
	    	{
	    		tanks.get(0).setAngle(ANGLE_MOVE);
	    	}
	    if (tanks.get(0) != null && pressed.contains(17))
	    {
	    	Projectile proj = createProjectile(0, 17);
	    	if (tank1ProjectileCount < 7)
	    	{
	    		tank1ProjectileCount++;
	    		tank1Projectiles.add(proj);
	    		projectiles.get(0).add(ultimateCount);
	    	}
	    	gr2.setColor(PROJECTILE_COLOR);
	    	gr2.fill(proj.getCircle());
	    }
	}
	
	/**
	 * Manipulates tank 2 based off of specific keyboard inputs
	 * @param gr2 the Graphics2D object to draw the projectile in
	 */
	private void tank2Controls(Graphics2D gr2)
	{
		if (pressed.contains(87))
	    	if (tanks.get(1) != null && !willCollideTranslate(2, true)
	    		&& !willCollideTranslateWall(2, true))
	    	{
	    		tanks.get(1).moveFoward(TRANSLATE_MOVE);
	    	}
	    if (pressed.contains(83))
	    	if (tanks.get(1) != null &&  !willCollideTranslate(2, false)
	    		&& !willCollideTranslateWall(2, false))
	    	{
		    	tanks.get(1).moveFoward(-1 * TRANSLATE_MOVE);
	    	}
	    if (pressed.contains(68))
	    	if (tanks.get(1) != null && !willCollideRotate(2, false)
	    		&& !willCollideRotateWall(2, false))
	    	{
	    		tanks.get(1).setAngle(-1 * ANGLE_MOVE);
	    	}
	    if (pressed.contains(65))
	    	if (tanks.get(1) != null && !willCollideRotate(2, true)
	    		&& !willCollideRotateWall(2, true))
	    	{
	    		tanks.get(1).setAngle(ANGLE_MOVE);
	    	}
	    if (tanks.get(1) != null && pressed.contains(81))
	    {
	    	Projectile proj = createProjectile(1, 81);
	    	if (tank2ProjectileCount < 7)
	    	{
	    		tank2ProjectileCount++;
	    		tank2Projectiles.add(proj);
	    		projectiles.get(1).add(ultimateCount);
	    	}
	    	gr2.setColor(PROJECTILE_COLOR);
	    	gr2.fill(proj.getCircle());
	    }
	}
	
	/**
	 * Manipulates tank 3 based off of specific keyboard inputs
	 * @param gr2 the Graphics2D object to draw the projectile in
	 */
	private void tank3Controls(Graphics2D gr2)
	{
		if (numTanks >= 3)
	    {
		    if (pressed.contains(84))
		    	if (tanks.get(2) != null && !willCollideTranslate(3, true)
		    		&& !willCollideTranslateWall(3, true))
		    	{
		    		tanks.get(2).moveFoward(TRANSLATE_MOVE);
		    	}
		    if (pressed.contains(71))
		    	if (tanks.get(2) != null && !willCollideTranslate(3, false)
		    		&& !willCollideTranslateWall(3, false))
		    	{
		    		tanks.get(2).moveFoward(-1 * TRANSLATE_MOVE);
		    	}
		    if (pressed.contains(72))
		    	if (tanks.get(2) != null && !willCollideRotate(3, false)
		    		&& !willCollideRotateWall(3, false))
		    	{
		    		tanks.get(2).setAngle(-1 * ANGLE_MOVE);
		    	}
		    if (pressed.contains(70))
		    	if (tanks.get(2) != null && !willCollideRotate(3, true)
		    		&& !willCollideRotateWall(3, true))
		    	{
		    		tanks.get(2).setAngle(ANGLE_MOVE);
		    	}
		    if (tanks.get(2) != null && pressed.contains(82))
		    {
		    	Projectile proj = createProjectile(2, 82);
		    	if (tank3ProjectileCount < 7)
		    	{
		    		tank3ProjectileCount++;
		    		tank3Projectiles.add(proj);
		    		projectiles.get(2).add(ultimateCount);
		    	}
		    	gr2.setColor(PROJECTILE_COLOR);
		    	gr2.fill(proj.getCircle());
		    }
	    }
	}
	
	/**
	 * Manipulates tank 4 based off of specific keyboard inputs
	 * @param gr2 the Graphics2D object to draw the projectile in
	 */
	private void tank4Controls(Graphics2D gr2)
	{
		if (numTanks == 4)
		{
		    if (pressed.contains(73))
		    	if (tanks.get(3) != null && !willCollideTranslate(4, true)
		    		&& !willCollideTranslateWall(4, true))
		    	{
		    		tanks.get(3).moveFoward(TRANSLATE_MOVE);
		    	}
		    if (pressed.contains(75))
		    	if (tanks.get(3) != null && !willCollideTranslate(4, false)
		    		&& !willCollideTranslateWall(4, false))
		    	{
		    	 	tanks.get(3).moveFoward(-1 * TRANSLATE_MOVE);
		    	}
		    if (pressed.contains(76))
		    	if (tanks.get(3) != null && !willCollideRotate(4, false)
		    		&& !willCollideRotateWall(4, false))
		    	{
		    		tanks.get(3).setAngle(-1 * ANGLE_MOVE);
		    	}
		    if (pressed.contains(74))
		    	if (tanks.get(3) != null && !willCollideRotate(4, true)
		    		&& !willCollideRotateWall(4, true))
		    	{
		    		tanks.get(3).setAngle(ANGLE_MOVE);
		    	}
		    if (tanks.get(3) != null && pressed.contains(85))
		    {
		    	Projectile proj = createProjectile(3, 85);
		    	if (tank4ProjectileCount < 7)
		    	{
		    		tank4ProjectileCount++;
		    		tank4Projectiles.add(proj);
		    		projectiles.get(3).add(ultimateCount);
		    	}
		    	gr2.setColor(PROJECTILE_COLOR);
		    	gr2.fill(proj.getCircle());
		    }
	    }
	}
	
	/**
	 * Checks if the game is still going on or if someone has won
	 * @param gr2 the Graphics2D object to display the text in
	 */
	private void isThereWinner(Graphics2D gr2)
	{
		int nullCount = 0;
		for (int p = 0; p < tanks.size(); p++)
		{
			if (tanks.get(p) == null)
				nullCount++;
		}
		if (nullCount == tanks.size() - 1)
			getWinner(gr2);
	}
	
	/**
	 * Resets the game, map, and tanks for a new match
	 */
	private void resetGame()
	{
		if (allowEnter && pressed.contains(10))
		{
			allowEnter = false;
			pressed.remove(10);
			
			tanks = new ArrayList<>();
			projectiles = new ArrayList<ArrayList<Integer>>();
			ultimateCount = 0;
			makeTanks();
			
			walls = new ArrayList<>();
			gridPoints = new boolean[GRID_SIZE - 1][GRID_SIZE - 1];
			gridPointsCount = new int[gridPoints.length][gridPoints[0].length];
			populateGridPoints();
			
			tank1ProjectileCount = 0;
			tank2ProjectileCount = 0;
			tank3ProjectileCount = 0;
			tank4ProjectileCount = 0;
			tank1Projectiles = new ArrayList<Projectile>();
			tank2Projectiles = new ArrayList<Projectile>();
			tank3Projectiles = new ArrayList<Projectile>();
			tank4Projectiles = new ArrayList<Projectile>();

			addedScore = false;
			allowEnter = false;
		}
	}
	
	/**
	 * Maintains the elements of gameplay including the walls,
	 * 		tanks, scores, and projectiles for each frame of animation
	 * @param g the Graphics object to add shapes and text to
	 */
	@Override
	public void paintComponent(Graphics g) 
	{
		Graphics2D gr2 = (Graphics2D) g;
		frameWidth = getWidth();
		frameHeight = getHeight();
		wallLength = (int)(frameWidth / GRID_SIZE);
		ultimateCount++;
		gr2.setColor(new Color(114, 114, 114));
		gr2.fillRect(0, frameHeight - SCORE_GAP, frameWidth, SCORE_GAP);
		drawWalls(gr2);
		drawBorders(gr2);
		drawTanks(gr2);
		drawScore(gr2);
		tank1Controls(gr2);
		tank2Controls(gr2);
		tank3Controls(gr2);
		tank4Controls(gr2);
		moveProjectiles(tank1Projectiles, tank1ProjectileCount, gr2, 0);
		moveProjectiles(tank2Projectiles, tank2ProjectileCount, gr2, 1);
		moveProjectiles(tank3Projectiles, tank3ProjectileCount, gr2, 2);
		moveProjectiles(tank4Projectiles, tank4ProjectileCount, gr2, 3);
		killTank(tank1Projectiles);
		killTank(tank2Projectiles);
		killTank(tank3Projectiles);
		killTank(tank4Projectiles);
		isThereWinner(gr2);
		resetGame();
	}
}