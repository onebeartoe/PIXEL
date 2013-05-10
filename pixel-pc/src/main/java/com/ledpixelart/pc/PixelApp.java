
package com.ledpixelart.pc;

import com.ledpixelart.hardware.Pixel;
import com.ledpixelart.pc.plugins.swing.AnimationsPanel;
import com.ledpixelart.pc.plugins.swing.ImageTilePanel;
import com.ledpixelart.pc.plugins.swing.PixelPanel;
import com.ledpixelart.pc.plugins.swing.PixelTilePanel;
import com.ledpixelart.pc.plugins.swing.ScrollingTextPanel;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PixelApp extends IOIOSwingApp
{    
    
    private final Logger logger;   
    
    private PixelTilePanel userTilePanel;
    
    private List<PixelPanel> imagePanels;
    
    private static IOIO ioiO;
    
    private JFrame frame;
    
    private JLabel statusLabel;
    
    private Timer searchTimer;
    
    private static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
     
    public static final Pixel pixel = new Pixel(KIND);
    
    public PixelApp()
    {
	logger = Logger.getLogger(PixelApp.class.getName());
	
	imagePanels = new ArrayList();	
    }

    @Override
    protected Window createMainWindow(String args[]) 
    {
	try 
	{
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} 
	catch (Exception ex) 
	{
	    String message = "An error occured while setting the native look and feel.";
	    logger.log(Level.SEVERE, message, ex);	
	}
	
	JMenuBar menuBar = createMenuBar();
	
	JTabbedPane tabbedPane = new JTabbedPane();
	
	String path = "/tab_icons/apple_small.png";
	URL url = getClass().getResource(path);
        ImageIcon imagesTabIcon = new ImageIcon(url);

	String path2 = "/tab_icons/ship_small.png";
	URL url2 = getClass().getResource(path2);
	ImageIcon animationsTabIcon = new ImageIcon(url2);
    
	String path3 = "/tab_icons/text_small.png";
	URL url3 = getClass().getResource(path3);
	ImageIcon textTabIcon = new ImageIcon(url3);
	
	PixelTilePanel imagesPanelReal = new ImageTilePanel(pixel.KIND);
	imagesPanelReal.populate();
	imagePanels.add(imagesPanelReal);
	tabbedPane.addTab("Images", imagesTabIcon, imagesPanelReal, "Load built-in images.");
        
	final PixelTilePanel animationsPanel = new AnimationsPanel(pixel.KIND);
	animationsPanel.populate();
	imagePanels.add(animationsPanel);
        tabbedPane.addTab("Animations", animationsTabIcon, animationsPanel, "Load built-in animations.");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

	// user images tab
	String userPath = "/tab_icons/ship_small.png";
	URL userUrl = getClass().getResource(userPath);
	ImageIcon userTabIcon = new ImageIcon(userUrl);
	String userpath = System.getProperty("user.home");
	File homeDirectory = new File(userpath);
	userTilePanel = new UserProvidedPanel(pixel.KIND, homeDirectory);
	userTilePanel.populate();
	imagePanels.add(userTilePanel);
	tabbedPane.addTab("Local Images", userTabIcon, userTilePanel, "This panel displays images from your local hard drive.");
	tabbedPane.setMnemonicAt(2, KeyEvent.VK_4);
	
        PixelPanel scrollPanel = new ScrollingTextPanel(pixel.KIND);
        imagePanels.add(scrollPanel);
        tabbedPane.addTab("Scolling Text", textTabIcon, scrollPanel, "Scrolls a text message across the PIXEL");
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener( new TabChangeListener() );

	frame = new JFrame("PIXEL");
	
	JPanel statusPanel = new JPanel();
	statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
	statusLabel = new JLabel("PIXEL Status: Searching...");
	statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
	statusPanel.add(statusLabel);
	
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	frame.setLayout( new BorderLayout() );
	frame.setJMenuBar(menuBar);
	frame.add(tabbedPane, BorderLayout.CENTER);	
	frame.add(statusPanel, BorderLayout.SOUTH);
	frame.setSize(450, 600);		
		
	// center it
	frame.setLocationRelativeTo(null); 
	
	startSearchTimer();
	
	frame.setVisible(true);
	
	return frame;
    }
    
    private JMenuBar createMenuBar()	    
    {
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	// Create the menu bar.
	menuBar = new JMenuBar();

	// Build the first menu.
	menu = new JMenu("Help");
	menu.setMnemonic(KeyEvent.VK_A);
	menu.getAccessibleContext().setAccessibleDescription("update with accessible description");
	menuBar.add(menu);	
	
	menuItem = new JMenuItem("Instructions");
	KeyStroke instructionsKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(instructionsKeyStroke);
	menuItem.getAccessibleContext().setAccessibleDescription("update with accessible description");
	menuItem.addActionListener( new InstructionsListener() );
	menu.add(menuItem);
	
	menuItem = new JMenuItem("About");
	KeyStroke aboutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(aboutKeyStroke);
	menuItem.getAccessibleContext().setAccessibleDescription("update with accessible description");
	menuItem.addActionListener( new AboutListener() );
	menu.add(menuItem);
	
	//menuItem = new JMenuItem("Exit", menuIcon);
	menuItem = new JMenuItem("Exit");
	KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(keyStroke);
	menuItem.addActionListener( new QuitListener() );
	menuItem.getAccessibleContext().setAccessibleDescription("update with accessible description");
	menu.add(menuItem);

	menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
	menuItem.setMnemonic(KeyEvent.VK_D);
	menu.add(menuItem);

/*	
	menu = new JMenu("Plugins");
	menu.setMnemonic(KeyEvent.VK_N);
	menu.getAccessibleContext().setAccessibleDescription("This menu does nothing");
	menuBar.add(menu);
*/
	
	return menuBar;
    }
    
    public byte[] extractBytes(BufferedImage image) throws IOException 
    {
	// get DataBufferBytes from Raster
	WritableRaster raster = image.getRaster();
	DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

	return (data.getData());
    }    

    @Override
    public IOIOLooper createIOIOLooper(String connectionType, Object extra) 
    {
	return new BaseIOIOLooper() 
	{
	    private DigitalOutput led_;

	    @Override
	    protected void setup() throws ConnectionLostException, InterruptedException
	    {
		led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
                PixelApp.this.ioiO = ioio_;
		pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);
		setPixelFound();
		System.out.println("Found PIXEL: " + pixel.matrix + "\n");
		System.out.println("You may now interact with the PIXEL\n");
		
//TODO: Load something on startup

		searchTimer.stop(); //need to stop the timer so we don't still display the pixel searching message
		String message = "PIXEL Status: Connected";
	    PixelApp.this.statusLabel.setText(message);
	    }
	    
	    @Override
	    public void disconnected() 
	    {
		String message = "The IOIO was disconected.";
		System.out.println(message);
		statusLabel.setText(message);
	    }

	    @Override
	    public void incompatible() 
	    {
		String message = "The IOIO is incompatible.";
		System.out.println(message);
		statusLabel.setText(message);
	    }
	};
    }
        
    public static RgbLedMatrix getMatrix() 
    {
        if(pixel.matrix == null) 
	{
	    if(ioiO != null)
	    {
		try 
		{
		    pixel.matrix = ioiO.openRgbLedMatrix(pixel.KIND);
		} 
		catch (ConnectionLostException ex) 
		{
		    String message = "The IOIO connection was lost.";
		    Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, message, ex);
		}
	    }            
        }
        
        return pixel.matrix;
    }
    
    public static void main(String[] args) throws Exception 
    {		
	PixelApp app = new PixelApp();
	app.go(args);		
    }
    
    private void setPixelFound()
    {
	for(PixelPanel panel : imagePanels)
	{
	    panel.setPixelFound(true);
	}
    }
    
    private void startSearchTimer()
    {
	int delay = 1000;
	SearchTimer worker = new SearchTimer();
	searchTimer = new Timer(delay, worker);
	searchTimer.start();
    }   
    
    public static AnalogInput getAnalogInput1() 
    {
        if (pixel.analogInput1 == null) 
	{
	    if(ioiO != null)
	    {
		try 
		{
		    pixel.analogInput1 = ioiO.openAnalogInput(32);
		} 
		catch (ConnectionLostException ex) 
		{
		    String message = "The IOIO connection was lost.";
		    Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, message, ex);
		}		
	    }
        }
        
        return pixel.analogInput1;
    }

    private class AboutListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    String path = "/images/";
	    String iconPath = path + "aaagumball.png";
	    URL resource = getClass().getResource(iconPath);
	    ImageIcon imageIcon = new ImageIcon(resource);
	    String message = "About PIXEL";
	    AboutPixelPc about = new AboutPixelPc();
	    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
	}
    }
    
    private class InstructionsListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    String path = "/images/";
	    String iconPath = path + "aaagumball.png";
	    URL resource = getClass().getResource(iconPath);
	    ImageIcon imageIcon = new ImageIcon(resource);
	    String message = "PIXEL Instructions";
	    InstructionsPixelPc about = new InstructionsPixelPc();
	    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
	}
    }
    
    private class QuitListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e)
	{
	    System.exit(1);
	}
    }
    
    private class SearchTimer implements ActionListener 
    {
	final long searchPeriodLength = 45 * 1000;
	
	final long periodStart;
	
	final long periodEnd;
	
	private int dotCount = 0;
	
	String message = "Searching for PIXEL";
	
	StringBuilder label = new StringBuilder(message);
	
	public SearchTimer()
	{
	    label.insert(0, "<html><body><h2>");
	    
	    Date d = new Date();
	    periodStart = d.getTime();
	    periodEnd = periodStart + searchPeriodLength;
	}
	
	public void actionPerformed(ActionEvent e) 
	{	    	    	    
	    if(dotCount > 10)
	    {
		label = new StringBuilder(message);
		label.insert(0, "<html><body><h2>");
		
		dotCount = 0;
	    }
	    else
	    {
		label.append('.');
	    }
	    dotCount++;

	    PixelApp.this.statusLabel.setText( label.toString() );
	    
	    Date d = new Date();
	    long now = d.getTime();
	    if(now > periodEnd)
	    {
		searchTimer.stop();
		if(pixel.matrix == null)
		{
		    message = "A Bluetooth connection to PIXEL could not be established. \n\nPlease ensure you have Bluetooth paired your PC to PIXEL first using code: 4545 and then try again.";
		    PixelApp.this.statusLabel.setText(message);
		    System.out.println(message);
		    String title = "PIXEL Connection Unsuccessful";
		    JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
		}
	    }
	}
    }

    private class TabChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e) 
	{
	    for(PixelPanel panel : imagePanels)
	    {
		panel.stopPixelActivity();
	    }

	    // start the selected panel/tab's PIXEL activity
	    Object o = e.getSource();
	    JTabbedPane tabs = (JTabbedPane) o;
	    Component c = tabs.getSelectedComponent();		
	    PixelPanel p = (PixelPanel) c;
	    p.startPixelActivity();
	}
    }
    
}

