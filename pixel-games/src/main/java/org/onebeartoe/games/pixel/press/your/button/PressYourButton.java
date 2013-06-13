
package org.onebeartoe.games.pixel.press.your.button;

import org.onebeartoe.games.pixel.press.your.button.board.PlayerLabelPanel;
import org.onebeartoe.games.pixel.press.your.button.board.PointPanel;
import org.onebeartoe.games.pixel.press.your.button.screens.NewGamePanel;
import org.onebeartoe.games.pixel.press.your.button.screens.EndOfTurnPanel;
import org.onebeartoe.games.pixel.press.your.button.board.BoardPanel;
import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.plugins.swing.ScrollingTextPanel;
import com.ledpixelart.pc.plugins.swing.SingleThreadedPixelPanel;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix.Matrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * This is a plugin for the PIXEL PC app.  It show a board with moving point and 
 * whammy panels.  A button will stop the board.  If the user lands on a point panel 
 * the points are added the users score.  Landing on a whammy will zero out their 
 * score.
 */
public class PressYourButton extends SingleThreadedPixelPanel
{
    private ActionListener worker = new PressYourButtonWorker(this);
    
    private Thread bigButtonThread;
    
    private List<BoardPanel> boardPanels;
    
    volatile PointPanel curentPointPanel;
    
    protected List<Point> boardPanelLocations;
    
    private PreviewPanel previewPanel;
    
    private EndOfTurnPanel endOfTurnPanel;
    
    private NewGamePanel newGamePanel;
    
    AnalogInput analogInput1;
    
    volatile public GameStates gameState;
    
    volatile public Game currentGame;
    
    private Random locationRandom;
    
    public PressYourButton(Matrix m)
    {
	super(m);	
	
	worker = new PressYourButtonWorker(this);
	
	gameState = GameStates.NEW_GAME_CONFIG;
		
	setupBoardPanels();
	
	setupBoardPanelLocations();
	
	locationRandom = new Random();
	
	setLayout( new BorderLayout() );
	
	JLabel label = new JLabel("Weather Panel");
	
	newGamePanel = new NewGamePanel(this);
	
	previewPanel = new PreviewPanel(this);
	previewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
	
	endOfTurnPanel = new EndOfTurnPanel(this, previewPanel);
	
	add(label, BorderLayout.NORTH);
	add(newGamePanel, BorderLayout.CENTER);
    }
    
    public void endOfGame()
    {
	
    }
    
    public void endOfTurn()
    {

    }
    
    @Override
    public ActionListener getActionListener() 
    {        
        return worker;
    }
    
    @Override
    public ImageIcon getTabIcon()
    {
	System.out.println("\n\n\nusing a custom tab\n");
	String path = "tab-icon.png";
	URL url = getClass().getResource(path);
        ImageIcon imagesTabIcon = new ImageIcon(url);
	
	return imagesTabIcon;
    }
    
    public void newGameConfiguration()
    {
	timer.setDelay(1900);  // milliseconds 
    }
    
    /**
     * this animates the PIXEL with moving point panels and a selected panel
     */
    public void nextPlayersTurn()
    {
	timer.setDelay(940);  // milliseconds 

	int boardWidth = previewPanel.borardDimension.width;
	int boardHeight = previewPanel.borardDimension.height;
	
	BufferedImage img = new BufferedImage(boardWidth, boardHeight, BufferedImage.TYPE_INT_ARGB);	    

	Graphics2D g2d = img.createGraphics();

	g2d.setPaint(Color.BLACK);
	g2d.fillRect(0,0, boardWidth, boardHeight);

	Color textColor = Color.GREEN;	    
	g2d.setPaint(textColor);

	String fontFamily = "Arial";            
	Font font = new Font(fontFamily, Font.PLAIN, 32);

	g2d.setFont(font);

	int rl = locationRandom.nextInt( boardPanelLocations.size() );
	
	int i = 0;
	Collections.shuffle(boardPanels);
	for(Point location : boardPanelLocations)
	{
	    BoardPanel panel = boardPanels.get(i);
	    
	    Color foreground;
	    if(i == rl)
	    {
		curentPointPanel = (PointPanel) panel;
		foreground = Color.RED;
	    }
	    else
	    {
		foreground = Color.WHITE;
	    }
	    
	    panel.draw(g2d, location, foreground);
		
	    i++;
	}
	
	Point labelLocation = new Point(43,43);
	String label = "P" + (currentGame.currentPlayer + 1);
	BoardPanel playerLabel = new PlayerLabelPanel(label);
	playerLabel.draw(g2d, labelLocation, Color.RED);
	
	g2d.dispose();

	previewPanel.setImage(img);

	SwingUtilities.invokeLater( new Runnable() 
	{
	    public void run() 
	    {
		previewPanel.invalidate();
		previewPanel.updateUI();
	    }
	});	    

	writeImageToPixel(img);
    }
    
    public void setCurrentGame(Game game)
    {
	currentGame = game;
	
	remove(newGamePanel);
	add(endOfTurnPanel, BorderLayout.CENTER);
//	add(previewPanel, BorderLayout.CENTER);
    }
	    
    private void setupBoardPanelLocations()
    {	
	boardPanelLocations = new ArrayList();
	
	// top row
	Point p1 = new Point(0,0);
	Point p2 = new Point(43, 0);
	Point p3 = new Point(86, 0);	
	
	// middle row
	Point p4 = new Point(0, 43);
	Point p5 = new Point(86, 43);
	
	// bottom row
	Point p6 = new Point(0, 86);
	Point p7 = new Point(43, 86);
	Point p8 = new Point(86, 86);
		
	boardPanelLocations.add(p1);
	boardPanelLocations.add(p2);
	boardPanelLocations.add(p3);
	boardPanelLocations.add(p4);
	boardPanelLocations.add(p5);
	boardPanelLocations.add(p6);
	boardPanelLocations.add(p7);
	boardPanelLocations.add(p8);
    }
    
    public void showScore()
    {
	timer.setDelay(1500);  // milliseconds 
	
	int width = 128;
	int height = 128;

	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);	    

	Graphics2D g2d = img.createGraphics();

	g2d.setPaint(Color.BLUE);
//	g2d.fillRect(0,0, width, height);

	Color textColor = Color.WHITE;
	g2d.setPaint(textColor);

	String fontFamily = "Arial";            
	Font font = new Font(fontFamily, Font.PLAIN, 34);

	g2d.setFont(font);
	
System.out.println( currentGame.toString() );
//g2d.fillRect(0,0, width/2, height/2);	
	
	
	int verticalGap = 42;
	int i = 0;
	for(Player p : currentGame.players)
	{
	    String s = "P" + (i+1) + " " + p.score;
	    int x = 10;
	    int y = 25 + i * verticalGap;
System.out.println("drawing " + s + " at " + x + ", " + y + " at " + new Date());	    
	    g2d.drawString(s, x, y);
	    i++;
	}
	
	g2d.dispose();

	writeImageToPixel(img);
    }
    
    private void setupBoardPanels()
    {	
	BoardPanel p1 = new PointPanel(Color.YELLOW, 10);
	BoardPanel p2 = new PointPanel(Color.BLUE, 20);
	BoardPanel p3 = new PointPanel(Color.GREEN, 30);
	BoardPanel p4 = new PointPanel(Color.LIGHT_GRAY, 40);
	BoardPanel p5 = new PointPanel(Color.CYAN, 50);
	BoardPanel p6 = new PointPanel(Color.DARK_GRAY, 60);
	BoardPanel p7 = new PointPanel(Color.MAGENTA, 70);
	BoardPanel p8 = new PointPanel(Color.ORANGE, 80);
	BoardPanel p9 = new PointPanel(Color.PINK, 90);
	BoardPanel p10 = new PointPanel(Color.YELLOW, 10);
	BoardPanel p11 = new PointPanel(Color.orange, 20);
	BoardPanel p12 = new PointPanel(Color.BLUE, 30);
	
	boardPanels = new ArrayList();
	boardPanels.add(p1);
	boardPanels.add(p2);	
	boardPanels.add(p3);
	boardPanels.add(p4);
	boardPanels.add(p5);
	boardPanels.add(p6);
	boardPanels.add(p7);
	boardPanels.add(p8);
	boardPanels.add(p9);
	boardPanels.add(p10);
	boardPanels.add(p11);
	boardPanels.add(p12);
    }
    
    @Override
    public void startPixelActivity()
    {
	super.startPixelActivity();
	
	analogInput1 = PixelApp.getAnalogInput1();
	try 
	{
	    if(analogInput1 == null)
	    {
		System.out.println("Analong input 1 is not available.");
	    }
	    else
	    {
		analogInput1.setBuffer(1);
		
		bigButtonThread = new Thread( new BigButtonListener(this) );
		bigButtonThread.start();
	    }
	} 
	catch (ConnectionLostException ex) 
	{
	    Logger.getLogger(PressYourButton.class.getName()).log(Level.SEVERE, null, ex);
	}
    }   
    
    @Override
    public void stopPixelActivity()
    {
	super.stopPixelActivity();
	
	if(bigButtonThread != null)
	{
	    bigButtonThread.stop();
	}
    }
    
    public void turnIsOver()
    {
	Player player = currentGame.players.get(currentGame.currentPlayer);
	player.score += curentPointPanel.amount;

	if(player.score >= currentGame.targetScore)
	{
	    gameState = GameStates.END_OF_GAME;
	}
	else
	{
	    gameState = GameStates.END_OF_TURN;
	}	
    }
    
    private void writeImageToPixel(BufferedImage image)
    {
	if(PixelApp.pixel != null)
	{
	    try 
	    {              
		PixelApp.pixel.writeImagetoMatrix(image);
	    }
	    catch (ConnectionLostException ex) 
	    {
		Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }
    
}
