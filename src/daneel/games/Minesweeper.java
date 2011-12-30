package daneel.games;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Minesweeper 
{
	private int height;
	private int width; // height and width of the game board
	
	private int x_min;
	private int y_min; // define the pixels of the board
	private int size; // length of a single cell
	BufferedImage img;
	Rectangle rec;

//	private int flagedCount; // number of mines found
//	private int totalMine; //number of mines in total
//	private boolean changeMade; // if any change has made in this round
	
	
	private int[][] board;
	
	private int errorlog;
	Robot robot; // the robot to press key and click screen;
	
	
	/*
	 * -3 = finished dealing
	 * -2 = flag
	 * -1 = unknown
	 *  0 = no-mine around
	 *  i = i mines around
	 */
	
	public Minesweeper()
	{
		//initialize all variables
		height = 9;
		width = 9;
		size = 78;
		x_min = 493;
		y_min = 175;
//		height = 16;
//		width = 30;
//		size = 50;
//		x_min = 93;
//		y_min = 123;
		errorlog = 0;
		
//		totalMine = 10;
//		changeMade = true;
//		flagedCount = 0;
		rec = new Rectangle(x_min, y_min, size * width, size * height);
		
		board = new int[width][height];
		for (int j = 0; j < height; j++)				
		{
			for (int i = 0; i < width; i++)
			{
				board[i][j] = -1;
			}
		}
		
		try
		{
			robot = new Robot();
		} 
		catch (AWTException e) 
		{
			System.out.println("Error initializing robot");
			e.printStackTrace();
		}
		
	}
	
	private void nextMove()
	{
		// analysis the board
		// determine the next move
		// make the move
		updateBoard();
		for (int j = 0; j < height; j++)				
		{
			for (int i = 0; i < width; i++)
			{
				if (board[i][j] > 0)
				{
					int clickable = getClickable(i, j);
					int flaged = getFlaged(i, j);
					if (flaged == board[i][j])
					{
//						System.out.println("click from " + i + "  " + j);
						click(i, j);
						board[i][j] = -3;
					}
					else if (clickable == board[i][j])
					{
//						System.out.println("mark from " + i + "  " + j);
						markFlags(i, j);
						board[i][j] = -3;
					}
				}
			}
		}
	}
	
	private void markFlags(int x, int y)
	{		
		if (x > 0 && board[x - 1][y] == -1) markFlag(x - 1, y);
		if (y < height -1  && board[x][y + 1] == -1) markFlag(x, y + 1);
		if (x < width - 1 && board[x + 1][y] == -1) markFlag(x + 1, y);
		if (y > 0 && board[x][y - 1] == -1) markFlag(x, y - 1);
		
		if (x > 0 && y > 0 && board[x - 1][y - 1] == -1) markFlag(x - 1, y - 1);
		if (x > 0 && y < height -1  && board[x - 1][y + 1] == -1) markFlag(x - 1, y + 1);
		if (x < width - 1 && y > 0 && board[x + 1][y - 1] == -1) markFlag(x + 1, y - 1);
		if (x < width - 1 && y < height - 1 && board[x + 1][y + 1] == -1) markFlag(x + 1, y + 1);		
	}
	
	private void click(int x, int y)
	{			
		robot.mouseMove(x * size + size / 2 + x_min, y * size + size / 2 + y_min);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);	
//		changeMade = true;
	}
	
	private void markFlag(int x, int y)
	{
		if (board[x][y] == -2) return;
//		flagedCount++;
//		System.out.println("marking " + x + "  " + y);
		robot.mouseMove(x * size + size / 2 + x_min, y * size + size / 2 + y_min);
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
		board[x][y] = -2;		
//		changeMade = true;
	}
	
	private int getClickable(int x, int y)
	{
		// get the number of clickable cells around cell (i, j)
		int count = 0;
		
		if (x > 0 && y > 0 && (board[x - 1][y - 1] == -1 || board[x - 1][y - 1] == -2)) count++;
		if (x > 0 && y < height -1  && (board[x - 1][y + 1] == -1 || board[x - 1][y + 1] == -2)) count++;
		if (x < width - 1 && y > 0 && (board[x + 1][y - 1] == -1 || board[x + 1][y - 1] == -2)) count++;
		if (x < width - 1 && y < height - 1 && (board[x + 1][y + 1] == -1 || board[x + 1][y + 1] == -2)) count++;

		
		if (x > 0 && (board[x - 1][y] == -1 || board[x - 1][y] == -2)) count++;
		if (y < height -1  && (board[x][y + 1] == -1 || board[x][y + 1] == -2)) count++;
		if (x < width - 1 && (board[x + 1][y] == -1 || board[x + 1][y] == -2)) count++;
		if (y > 0 && (board[x][y - 1] == -1 || board[x][y - 1] == -2)) count++;
		
		return count;
	}
	
	private int getFlaged(int x, int y)
	{
		// get the number of flaged cells around cell (i, j)
		int count = 0;
		
		if (x > 0 && y > 0 && board[x - 1][y - 1] == -2) count++;
		if (x > 0 && y < height -1  && board[x - 1][y + 1] == -2) count++;
		if (x < width - 1 && y > 0 && board[x + 1][y - 1] == -2) count++;
		if (x < width - 1 && y < height - 1 && board[x + 1][y + 1] == -2) count++;

		
		if (x > 0 && board[x - 1][y] == -2) count++;
		if (y < height -1  && board[x][y + 1] == -2) count++;
		if (x < width - 1 && board[x + 1][y] == -2) count++;
		if (y > 0 && board[x][y - 1] == -2) count++;
		
		return count;
	}
	
	private void updateBoard()
	{
		//take another screen shoot and update the game board 
		img = robot.createScreenCapture(rec);
			
//		File file = new File("clip.png");
//		try 
//		{
//			ImageIO.write(img, "PNG", file);
//		}
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//		}
		
		for (int j = 0; j < height; j++)				
		{
			for (int i = 0; i < width; i++)
			{
				if (board[i][j] == -1) 
				{
					board[i][j] = getNumber(i, j, img);					
				}
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private int getNumber(int i, int j, BufferedImage img) 
	{
		Color t1 = new Color(img.getRGB((int)((i + 0.8) * size), (int)((j + 0.8) * size))); //first test
		
		if (t1.getBlue() + t1.getRed() + t1.getGreen() < 500)	return -1;	
//		System.out.println(i + "  " + j + "  " + t1.getRed() + "  " + t1.getGreen() + "  " + t1.getBlue());
		
		for (int k = 2; k < size - 2; k += 2)
		{
			Color t2 = new Color(img.getRGB(i * size + k, j * size + size / 2)); //second test
//			System.out.println(i + "  " + j + "  " + t2.getRed() + "  " + t2.getGreen() + "  " + t2.getBlue() + "  " + (int)((i + 0.5) * size) + "  " + ((int)((j + 1) * size) - k));
			if (t2.getRed() > 170 && t2.getGreen() < 10 && t2.getBlue() < 10) return 3;
			if (t2.getGreen() > 100 && t2.getRed() < 40 && t2.getBlue() < 10) return 2;
			if (t2.getBlue() > 185 && t2.getGreen() < 85 && t2.getRed() < 70) return 1;
			if (t2.getBlue() > 120 && t2.getGreen() < 10 && t2.getRed() < 10) return 4;
			if (t2.getRed() > 130 && t2.getGreen() < 10 && t2.getBlue() < 10) return 5;
		}
		
		
		if (t1.getRed() > 150 && t1.getGreen() > 150 && t1.getBlue() > 150) return 0;
		
		BufferedImage temp = img.getSubimage(i * size, j * size, size, size);
		
		File file = new File(errorlog + "error.png");
		errorlog++;
		try 
		{
			ImageIO.write(temp, "PNG", file);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.out.println("Error identifing cell " + i + " " + j + " with t1 color" + "  " + t1.getRed() + "  " + t1.getGreen() + "  " + t1.getBlue());
		return -1;
	}

	public void run() 
	{
		robot.delay(3000);
		robot.setAutoDelay(1);
		int lastUnknown = 0;
		int unknowns = -1;
		randomClick();
		while (unknowns != 0)
		{
			lastUnknown = unknowns;
			nextMove();		
			unknowns = getUnknowns();
			if (unknowns == lastUnknown)
			{
//				randomClick();
//				if (checkFails())
//				{
//					System.out.println("Sorry for that...");
//					return;
//				}
				System.out.println("I can't solve it");
				return;
			}
		}
		System.out.println("Finished!");
	}
	
//	private boolean checkFails() 
//	{
//		robot.delay(25);
//		img = robot.createScreenCapture(rec);
//		
//		int c = img.getRGB(580, 333); 
//		for (int i = 581; i < 600; i+=4)
//		{
//			if (img.getRGB(i, 333) != c) return false;
//		}
//		return true;
//	}

	private int getUnknowns()
	{
		int count = 0;
		for (int j = 0; j < height; j++)				
		{
			for (int i = 0; i < width; i++)
			{
				if (board[i][j] == -1) count++;
			}
		}
		return count;
	}
	
	private void randomClick()
	{
		int count = 0;
		int rand = (int) (Math.random() * getUnknowns());
		for (int j = 0; j < height; j++)				
		{
			for (int i = 0; i < width; i++)
			{
				if (board[i][j] == -1) 
				{
					count++;
					if (count == rand)
					{
						robot.mouseMove(i * size + size / 2 + x_min, j * size + size / 2 + y_min);
						robot.mousePress(InputEvent.BUTTON1_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						robot.mousePress(InputEvent.BUTTON1_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);	
						return;
					}
				}
			}
		}
	}
}
