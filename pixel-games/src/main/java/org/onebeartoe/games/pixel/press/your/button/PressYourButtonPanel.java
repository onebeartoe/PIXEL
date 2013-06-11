
package org.onebeartoe.games.pixel.press.your.button;

import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.plugins.swing.ScrollingTextPanel;
import com.ledpixelart.pc.plugins.swing.SingleThreadedPixelPanel;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix.Matrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 */
public class PressYourButtonPanel extends SingleThreadedPixelPanel
{
    private ActionListener worker = new PressYourButtonWorker();
    
    private Thread bigButtonThread;
    
    private List<BoardPanel> boardPanels;
    
    protected List<Point> boardPanelLocations;
    
    private PreviewPanel previewPanel;
    
    private AnalogInput analogInput1;
    
    private GameStates gameState;
    
    public PressYourButtonPanel(Matrix m)
    {
	super(m);
	
	tickDelay = 1900;  // milliseconds 
	
	worker = new PressYourButtonWorker();
	
	gameState = GameStates.NEW_GAME_CONFIG;
	
	setupBoardPanels();
	
	setupBoardPanelLocations();
	
	setLayout( new BorderLayout() );
	
	JLabel label = new JLabel("Weather Panel");
	
	previewPanel = new PreviewPanel();	
	
	add(label, BorderLayout.NORTH);
	add(previewPanel, BorderLayout.CENTER);
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
    
    private void newGameConfiguration()
    {
	
    }
    
    private void nextPlayersTurn()
    {
	int boardWidth = 128;
	int boardHeight = 128;

	BufferedImage img = new BufferedImage(boardWidth, boardHeight, BufferedImage.TYPE_INT_ARGB);	    

	Graphics2D g2d = img.createGraphics();

	g2d.setPaint(Color.BLACK);
	g2d.fillRect(0,0, boardWidth, boardHeight);

	Color textColor = Color.GREEN;	    
	g2d.setPaint(textColor);

	String fontFamily = "Arial";            
	Font font = new Font(fontFamily, Font.PLAIN, 32);

	g2d.setFont(font);

	int i = 0;
	Collections.shuffle(boardPanels);
	for(Point location : boardPanelLocations)
	{
	    BoardPanel panel = boardPanels.get(i);
	    panel.draw(g2d, location);
	    i++;
	}

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

	if(PixelApp.pixel != null)
	{
	    try 
	    {              
		PixelApp.pixel.writeImagetoMatrix(img);
	    } 
	    catch (ConnectionLostException ex) 
	    {
		Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}	    
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
    
    private void setupBoardPanels()
    {	
	BoardPanel p1 = new MoneyPanel(Color.YELLOW, 10);
	BoardPanel p2 = new MoneyPanel(Color.BLUE, 20);
	BoardPanel p3 = new MoneyPanel(Color.GREEN, 30);
	BoardPanel p4 = new MoneyPanel(Color.RED, 40);
	BoardPanel p5 = new MoneyPanel(Color.CYAN, 50);
	BoardPanel p6 = new MoneyPanel(Color.DARK_GRAY, 60);
	BoardPanel p7 = new MoneyPanel(Color.MAGENTA, 70);
	BoardPanel p8 = new MoneyPanel(Color.ORANGE, 80);
	BoardPanel p9 = new MoneyPanel(Color.PINK, 90);
	BoardPanel p10 = new MoneyPanel(Color.YELLOW, 10);
	BoardPanel p11 = new MoneyPanel(Color.orange, 20);
	BoardPanel p12 = new MoneyPanel(Color.BLUE, 30);
	
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
	    analogInput1.setBuffer(1);
	} 
	catch (ConnectionLostException ex) 
	{
	    Logger.getLogger(PressYourButtonPanel.class.getName()).log(Level.SEVERE, null, ex);
	}
	
//	analogInput2 = PixelApp.getAnalogInput2();
	
	bigButtonThread = new Thread( new BigButtonListener() );
	bigButtonThread.start();
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
    
    private class BigButtonListener implements Runnable
    {
	private int pressCount = 0;
	
	public void run() 
	{
	    while(true)
	    {		
		try 
		{	
		    if(analogInput1 == null )//|| analogInput2 == null)
		    {
			System.out.println("\nAnalog 1: " + analogInput1);						
		    }
		    else
		    {
			float a1 = analogInput1.readBuffered();
			
			int signal = (int) a1;
			if(signal == 1)
			{
			    pressCount++;
			
			    System.out.println("Analog 1: " + a1 + " - press count: " + pressCount);
			}    
		    }
		    
		    Thread.sleep(60);
		} 
		catch (Exception ex) 
		{
		    Logger.getLogger(PressYourButtonPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	}
    }
    
    private class PressYourButtonWorker implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	    switch(gameState)
	    {
		case NEXT_PLAYERS_TURN:
		{
		    nextPlayersTurn();
		    break;
		}
		default:
		{
		    newGameConfiguration();
		}
	    }
	}
    }
    
    private class PreviewPanel extends JPanel    
    {
	
	private Image image;
	
	@Override
        public void paintComponent(Graphics g) 
	{
            super.paintComponent(g);
	    
	    if(image != null)
	    {
		g.drawImage(image, 0, 0, PressYourButtonPanel.this);
	    }
        }
	
	public void setImage(Image image)
	{
	    this.image = image;
	}
    }
    
}

