
package com.ledpixelart.pc;


import com.ledpixelart.pc.plugins.PluginConfigEntry;
import com.ledpixelart.pc.plugins.swing.AnimationsPanel;
import com.ledpixelart.pc.plugins.swing.AnimationsPanel64;
import com.ledpixelart.pc.plugins.swing.ImageTilePanel;
import com.ledpixelart.pc.plugins.swing.SettingsTilePanel;

import com.ledpixelart.pc.plugins.swing.PixelTilePanel;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.pixel.plugins.swing.PixelPanel;
import org.onebeartoe.pixel.plugins.swing.ScrollingTextPanel;
//import org.onebeartoe.pixel.plugins.swing.ScrollingTextPanel;
import org.onebeartoe.pixel.preferences.JavaPreferencesService;
import org.onebeartoe.pixel.preferences.PixelPreferencesKeys;
import org.onebeartoe.pixel.preferences.PreferencesService;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class PixelApp extends IOIOSwingApp
{    
    
    private final Logger logger;
    
    private PreferencesService preferenceService;
    
    private Timer searchTimer;
    
    private static IOIO ioiO; 
	 
	public static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
	
	//public static RgbLedMatrix.Matrix KIND;
	
	public static final Pixel pixel = new Pixel(KIND);
	
	private static int selectedFileResolution = 32; 
    
    //private static IOIO ioiO;
    
    //public static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
    
    //public static final Pixel pixel = new Pixel(KIND);
	
	//public static int selectedFileResolution = 32; 
	
	public static int frame_length;
	    
	public static int currentResolution;
	    
	public static int ledMatrixType = 3; //we'll default to PIXEL 32x32 and change this is a command line option is entered specifying otherwise
	
	public static String pixelFirmware = "Not Found";
	 
	public static String pixelHardwareID = "Not Found";
	    
	private static VersionType v;
    
    private UserProvidedPanel localImagesPanel;
    
    private List<PixelPanel> builtinPixelPanels;
    
    private List<PixelPanel> userPluginPanels;
    
    private List<PluginConfigEntry> userPluginConfiguration;
    
    public static JFrame frame;
    
    private JTabbedPane tabbedPane;
    
    private JFileChooser pluginChooser;
    
    public static JLabel statusLabel;
    
    public static final int DEFAULT_HEIGHT = 600;
    
    public static final int DEFAULT_WIDTH = 500;  //was 450
    
	public static String userHome = System.getProperty("user.home");
  	
  	public static String decodedDir = userHome + "/pixel/animations/decoded/";  //users/al/pixel/animations/decoded
  	
  	private static boolean pixelFound = false;
  	
    private static Preferences prefs;
    
    private static int ledMatrix_;

    private static String pixelPrefNode = "/com/ledpixelart/pc";
    
    private static boolean firstTimeUser = true;
    
    private static String osName = System.getProperty("os.name").toLowerCase();
	
    public static boolean isMacOs = osName.startsWith("mac os x");
	
    public static boolean isWindows = osName.startsWith("windows");
    
    public PixelApp()
    {
	String className = PixelApp.class.getName();
	logger = Logger.getLogger(className);
	
	pluginChooser = new JFileChooser();
	pluginChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
	preferenceService = new JavaPreferencesService();

    userPluginConfiguration = new ArrayList();
        
	builtinPixelPanels = new ArrayList();
	
	userPluginPanels = new ArrayList();
	
	
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
	
	 prefs = Preferences.userRoot().node(pixelPrefNode); //let's get our preferences
	 
	 int defaultLEDMatrix = 3; //if the pref does not exist yet for the led matrix, use this and default to pixel 32x32
	 
	 ledMatrixType = prefs.getInt("prefMatrix", defaultLEDMatrix);
	 firstTimeUser = prefs.getBoolean("prefFirstTime", true);

	 setupEnvironment();  //set our default led panel that we get from java preferences
	 
	 // initialize twitter
	 
	
	// images tab
	String path = "/tab_icons/apple_small.png";
	URL url = getClass().getResource(path);
    ImageIcon imagesTabIcon = new ImageIcon(url);
	PixelTilePanel imagesPanelReal = new ImageTilePanel(KIND);
	imagesPanelReal.populate();
	builtinPixelPanels.add(imagesPanelReal);
	
	// user supplied images and GIFs tab
	String userIconPath = "/tab_icons/my_small.png";
	URL userUrl = getClass().getResource(userIconPath);
	ImageIcon userTabIcon = new ImageIcon(userUrl);	
	String key = PixelPreferencesKeys.userImagesDirectory;	
	String defaultValue = System.getProperty("user.home");
	String localUserPath = preferenceService.get(key, defaultValue);
	File localUserDirectory = new File(localUserPath);
	localImagesPanel = new UserProvidedPanel(KIND, localUserDirectory);
	localImagesPanel.populate();
	builtinPixelPanels.add(localImagesPanel);

	// animations tab
	String path2 = "/tab_icons/ship_small.png";
	URL url2 = getClass().getResource(path2);
	ImageIcon animationsTabIcon = new ImageIcon(url2);
	final PixelTilePanel animationsPanel = new AnimationsPanel(KIND);
	animationsPanel.populate();
	builtinPixelPanels.add(animationsPanel);
	
	// animations64 tab
	String path5 = "/tab_icons/boat_small.png";
	URL url5 = getClass().getResource(path5);
	ImageIcon animations64TabIcon = new ImageIcon(url5);
	final PixelTilePanel animationsPanel64 = new AnimationsPanel64(KIND);
	animationsPanel64.populate();
	builtinPixelPanels.add(animationsPanel64);

	// scrolling text panel
	String path3 = "/tab_icons/text_small.png";
	URL url3 = getClass().getResource(path3);
	ImageIcon textTabIcon = new ImageIcon(url3);
    PixelPanel scrollPanel = new ScrollingTextPanel(KIND);
    builtinPixelPanels.add(scrollPanel);        
        
     // settings tab
    String path4 = "/tab_icons/settings_small.png";
    URL url4 = getClass().getResource(path4);
    ImageIcon settingsTabIcon = new ImageIcon(url4);
    PixelPanel settingsPanel = new SettingsTilePanel(KIND);
    builtinPixelPanels.add(settingsPanel);

	frame = new JFrame("PIXEL");
	
	JPanel statusPanel = new JPanel();
	statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
	statusLabel = new JLabel("PIXEL Status: Searching...");
	statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
	statusPanel.add(statusLabel);
	
	JMenuBar menuBar = createMenuBar();
	
	tabbedPane = new JTabbedPane();
	tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener( new TabChangeListener() );
    tabbedPane.addTab("GIFs", animationsTabIcon, animationsPanel, "Load built-in animations.");
    tabbedPane.addTab("GIF64", animations64TabIcon, animationsPanel64, "Load built-in animations for SUPER PIXEL 64x64.");
    tabbedPane.addTab("My Images and GIFs", userTabIcon, localImagesPanel, "This panel displays images and GIFs from your local hard drive.");
    tabbedPane.addTab("Images", imagesTabIcon, imagesPanelReal, "Load built-in images.");
    tabbedPane.addTab("Scolling Text", textTabIcon, scrollPanel, "Scrolling Text");
	tabbedPane.addTab("Settings", settingsTabIcon, settingsPanel, "Settings");
	
	Dimension demension;
	try 
	{
	    demension = preferenceService.restoreWindowDimension();
	} 
	catch (Exception ex) 
	{
	    demension = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	Point location = null;
	try 
	{
	    location = preferenceService.restoreWindowLocation();
	} 
	catch (Exception ex) 
	{
	    logger.log(Level.INFO, ex.getMessage(), ex);
	}
        
        try 
        {
            loadPluginPreferences();
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, null, ex);
        }
	
	frame.addWindowListener(this);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	frame.setLayout( new BorderLayout() );
	frame.setJMenuBar(menuBar);
	frame.add(tabbedPane, BorderLayout.CENTER);	
	frame.add(statusPanel, BorderLayout.SOUTH);
	frame.setSize(demension);		
		
	if(location == null)
	{
	    // center it
	    frame.setLocationRelativeTo(null); 		
	}
	else
	{
	    frame.setLocation(location);
	}

	startSearchTimer();
		
	frame.setVisible(true);
	//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH); // this didn't work on the Mac, didn't spend time troubleshooting
	
	//let's show the user a first time instructions pop up, only shows once as we'll write a preference
	if (firstTimeUser) {
		    String path9 = "/tab_icons/";
		    String iconPath = path9 + "gumball-about64.png";
		    URL resource = getClass().getResource(iconPath);
		    ImageIcon imageIcon = new ImageIcon(resource);
		    String message = "Welcome PIXEL User";
		    InstructionsPanel about = new InstructionsPanel();
		    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
		    prefs.putBoolean("prefFirstTime", false);
	}
	
	return frame;
    }
    
    public static boolean getPixelFound () { //we'll call this method from other classes to make sure pixel was found
    	return pixelFound;
    }
    
    private JMenuBar createMenuBar()	    
    {
	JMenuItem menuItem;
	
	JMenu helpMenu = new JMenu("Help");
	helpMenu.setMnemonic(KeyEvent.VK_A);
	helpMenu.getAccessibleContext().setAccessibleDescription("update with accessible description");
	
	menuItem = new JMenuItem("Instructions");
	KeyStroke instructionsKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(instructionsKeyStroke);
	menuItem.getAccessibleContext().setAccessibleDescription("Instructions");
	menuItem.addActionListener( new InstructionsListener() );
	helpMenu.add(menuItem);
	
	menuItem = new JMenuItem("About");
	KeyStroke aboutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(aboutKeyStroke);
	menuItem.getAccessibleContext().setAccessibleDescription("About this application");
	menuItem.addActionListener( new AboutListener() );
	helpMenu.add(menuItem);
	
	menuItem = new JMenuItem("Exit");
	KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(keyStroke);
	menuItem.addActionListener( new QuitListener() );
	menuItem.getAccessibleContext().setAccessibleDescription("Quit this application");
	helpMenu.add(menuItem);

	//menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));  //Al deleted these,not sure why they were there? was causing an extra space in the menu
	//menuItem.setMnemonic(KeyEvent.VK_D);
	//helpMenu.add(menuItem);
	
	JMenuItem loadPluginsOption = new JMenuItem("Load");
	loadPluginsOption.addActionListener( new LoadPluginListener() );
	JMenuItem clearPluginsOption = new JMenuItem("Clear");
	clearPluginsOption.addActionListener( new ClearPluginsListener() );
	JMenu pluginsMenu = new JMenu("Plugins");
	pluginsMenu.add(loadPluginsOption);
	pluginsMenu.add(clearPluginsOption);
	pluginsMenu.getAccessibleContext().setAccessibleDescription("default plugins menu message");
	
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(helpMenu);
	menuBar.add(pluginsMenu);
	
	return menuBar;
    }
    
    private void displayPlugin(PixelPanel panel)
    {
	ImageIcon icon = panel.getTabIcon();
	String title = panel.getTabTitle();
	tabbedPane.addTab(title, icon, panel, "A weather app for internal and external temps.");
	builtinPixelPanels.add(panel);
    }
    
    private void exit()
    {
	searchTimer.stop();
	
	savePreferences();
	
	System.exit(0);
    }
    
    public byte[] extractBytes(BufferedImage image) throws IOException 
    {
	// get DataBufferBytes from Raster
	WritableRaster raster = image.getRaster();
	DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

	return (data.getData());
    }    
    
   

    @Override
    public IOIOLooper createIOIOLooper(String connectionType, final Object extra) 
    {
	return new BaseIOIOLooper() 
	{
	    private DigitalOutput led_;

	    @Override
	    protected void setup() throws ConnectionLostException, InterruptedException
	    {
	    	
	    	System.out.println("Found PIXEL, entering initialization routine, detected port is..." + extra.toString());
	    	
	    	//*****If we are on a Mac, let's add in the port if the port value is blank
	    	//String osName = System.getProperty("os.name").toLowerCase();
			//boolean isMacOs = osName.startsWith("mac os x");
			//boolean isWindows = osName.startsWith("Windows");
			
			if (isMacOs) 
			{
				if (SettingsTilePanel.pixelPortText.getText().equals("")) {
					
					System.out.println("We're on a Mac and the port value is blank so let's auto-detect");
					prefs.put("prefSavedPort", extra.toString()); //let's write the prefs for the port
					SettingsTilePanel.pixelPortText.setText(extra.toString());
					
					//and now let's pop up a box telling the user what we did and to re-start the app
					String path = "/images/";
				    String iconPath = path + "aaagumball.png";
				    URL resource = getClass().getResource(iconPath);
				    ImageIcon imageIcon = new ImageIcon(resource);
				    String message = "PIXEL Port Detected and Saved, Please Force Quit this Application Now and Restart";
				    MacPixelPort about = new MacPixelPort();
				    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
				}
			}
			
			//if we are not on windows and the port in settings is different than the detected port, we should over-write with the detected port
			// we're not writing the port if we are on windows because windows does not have an issue auto-detecting so this is better because the port does change if the user plugs into a different usb port which will be a usability problem
			if (!isWindows && !SettingsTilePanel.pixelPortText.getText().equals(extra.toString())) {
				prefs.put("prefSavedPort", extra.toString()); //let's write the prefs for the port
				SettingsTilePanel.pixelPortText.setText(extra.toString());
			}
			
			//*************************************************************************
	    	
	    	//**** let's get IOIO version info for the About Screen ****
  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER);
  			//pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
  			//IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
  			//**********************************************************
	    	
	    	led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
	    	
	    	PixelApp.this.ioiO = ioio_;
				
			//setupEnvironment();  //here we set the PIXEL LED matrix type
				
            //pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);   //AL could not make this work, did a quick hack, Roberto probably can change back to the right way
            pixel.matrix = ioio_.openRgbLedMatrix(KIND);
            pixel.ioiO = ioio_;
            System.out.println("Found PIXEL: " + pixel.matrix + "\n");
	    	
            //PixelApp.this.ioiO = ioio_;
            
        	//setupEnvironment();  //here we set the PIXEL LED matrix type
            
            
            //pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);
           // pixel.ioiO = ioio_;
                
                for(PixelPanel panel : userPluginPanels)
                {
                	System.out.println("\nSetting pixel for panel: " + panel.getClass() + "\n");
                    panel.pixel = pixel;
                }
                
                for(PixelPanel panel : builtinPixelPanels)
                {
                    panel.pixel = pixel;
                }
                
		setPixelFound();
		pixelFound = true;
		System.out.println("Found PIXEL: " + pixel.matrix + "\n");
		System.out.println("You may now interact with the PIXEL\n");
		//String message = "PIXEL Status: Connected";
        //PixelApp.this.statusLabel.setText(message);
		
		//TODO: Load something on startup on PIXEL like "Select Image"

		searchTimer.stop(); //need to stop the timer so we don't still display the pixel searching message
		
		String message = "PIXEL FOUND: Click to stream or double click to write";
        PixelApp.this.statusLabel.setText(message);
        
	    }
	    
	    @Override
	    public void disconnected() 
	    {
		String message = "PIXEL was Disconnected";
		System.out.println(message);
		statusLabel.setText(message);
		pixelFound = false;
	    }

	    @Override
	    public void incompatible() 
	    {
		String message = "Incompatible Firmware Detected";
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
		    String message = "The PIXEL connection was lost.";
		    Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, message, ex);
		}
	    }            
        }
        
        return pixel.matrix;
    } 
    
	 private static void setupEnvironment() {
		 
		 switch (ledMatrixType) { 
		     case 0:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
		    	 frame_length = 1024;
		    	 currentResolution = 16;
		    	 break;
		     case 1:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
		    	 frame_length = 1024;
		    	 currentResolution = 16;
		    	 break;
		     case 2:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //an early version of the PIXEL LED panels, only used in a few early prototypes
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		    	 break;
		     case 3:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //the current version of PIXEL 32x32
		    	 frame_length = 2048;
		    	 currentResolution = 32;
		    	 break;
		     case 4:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x32;
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;
		     case 5:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x64; 
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;	 
		     case 6:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_2_MIRRORED; 
		    	 frame_length = 8192;
		    	 currentResolution = 64; 
		    	 break;	 	 
		     case 7:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_4_MIRRORED;
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		     case 8:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_128x32; //horizontal
		    	 frame_length = 8192;
		    	 currentResolution = 128;  
		    	 break;	 
		     case 9:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x128; //vertical mount
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 
		     case 10:
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x64;
		    	 frame_length = 8192;
		    	 currentResolution = 128; 
		    	 break;	 	 		 
		     default:	    		 
		    	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
		    	 frame_length = 2048;
		    	 currentResolution = 32;
	     }
		 
		 System.out.println("went to setup & currentResolution is: " + currentResolution + "\n");
	 }
    
    
	 
	public static void main(String[] args) throws Exception 
    {		
		PixelApp app = new PixelApp();
		app.go(args);		
    }
    
	 public static void setStatusLabel(String message) {
	    statusLabel.setText(message);
	 }
	
	private void savePreferences()
    {
		preferenceService.saveWindowPreferences(frame);
		preferenceService.saveBuiltInPluginsPreferences(localImagesPanel);
    }
    
    private List<PixelPanel> loadPluginPreferences() throws Exception
    {
	List<PixelPanel> foundClasses = preferenceService.restoreUserPluginPreferences(KIND, userPluginConfiguration);
        
	if( foundClasses.isEmpty() )
	{
	    System.out.println("No plugins were found.");
	}
	else
	{
	    for (PixelPanel panel : foundClasses)	    
	    {
		System.out.println ("Found " + panel.getClass());
        userPluginPanels.add(panel);
		displayPlugin(panel);	
	    }
	}
	        
	return foundClasses;
    }

    
    private void setPixelFound()
    {
		for(PixelPanel panel : builtinPixelPanels)
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
    
    @Override
    public void windowClosing(WindowEvent event)
    {
        exit();
    }

    private class AboutListener implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	    String path = "/tab_icons/";
	    String iconPath = path + "gumball-about256.png";
	    URL resource = getClass().getResource(iconPath);
	    ImageIcon imageIcon = new ImageIcon(resource);
	    String message = "About PIXEL";
	    AboutPanel about = new AboutPanel();
	    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
	}
    }
    
    private class ClearPluginsListener implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
        userPluginConfiguration.clear();
	    preferenceService.saveUserPluginPreferences(userPluginConfiguration);
	    
	    for(PixelPanel panel : userPluginPanels)
	    {
		tabbedPane.remove(panel);
	    }
	}
    }
    
    private class InstructionsListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    String path = "/tab_icons/";
	    String iconPath = path + "gumball-about64.png";
	    URL resource = getClass().getResource(iconPath);
	    ImageIcon imageIcon = new ImageIcon(resource);
	    String message = "Welcome PIXEL User";
	    InstructionsPanel about = new InstructionsPanel();
	    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
	}
    }
    
    private class settingsListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    String path = "/images/";
	    String iconPath = path + "aaagumball.png";
	    URL resource = getClass().getResource(iconPath);
	    ImageIcon imageIcon = new ImageIcon(resource);
	    String message = "PIXEL Settings";
	    InstructionsPanel about = new InstructionsPanel();
	    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
	}
    }
    
    private class LoadPluginListener implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	    int result = pluginChooser.showDialog(frame, "Select");
	    if(result == JFileChooser.APPROVE_OPTION)
	    {
		File jar = pluginChooser.getSelectedFile();
		
		String path = jar.getAbsolutePath();
			
		try 
		{
		    List<String> classNames = loadPluginNames(jar);
		    for(String qualifiedClassName : classNames)
		    {
			PixelPanel plugin;
			try 
			{
			    plugin = preferenceService.loadPlugin(path, qualifiedClassName, KIND);
			    userPluginPanels.add(plugin);
			    displayPlugin(plugin);
                            
                            PluginConfigEntry entry = new PluginConfigEntry();
                            entry.jarPath = path;
                            entry.qualifiedClassName = qualifiedClassName;
                            userPluginConfiguration.add(entry);			    			    
                            
                            preferenceService.saveUserPluginPreferences(userPluginConfiguration);
			} 
			catch (Exception ex) 
			{
			    String message = "A problem occured while loading plugin: " + qualifiedClassName;
			    logger.log(Level.SEVERE, message, ex);
			}
		    }
		} 
		catch (Exception ex) 
		{
		    String message = "A problem occured while loading plugin class names.";
		    logger.log(Level.SEVERE, message, ex);
		}
	    }
	}
	
	private List<String> loadPluginNames(File jarfile) throws Exception
	{
	    List<String> classNames = new ArrayList();
	    	    
	    ZipFile zipfile = new ZipFile(jarfile);
	    
	    String entryPath = "plugins.manifest";
	    ZipEntry entry = zipfile.getEntry(entryPath);
	    InputStream inputStream = zipfile.getInputStream(entry);
	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	    String line = br.readLine();  	
	    while (line != null)
	    {
		if( !line.trim().equals("") )
		{
		    classNames.add(line);
		}
		
		line = br.readLine();
	    }	
	    inputStream.close(); 		
	    
	    return classNames;
	}
	
    }
    
    private class QuitListener implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
            exit();
	}
    }
    
    private class SearchTimer implements ActionListener 
    {
	final long searchPeriodLength = 30 * 1000; //30 seconds
	
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
				if (isMacOs) {
					message = "Check that the toggle switch on PIXEL's side is pointing towards 'PC USB' \nand you've connected PIXEL to your Mac using the included USB A-A cable. \n\nThe PIXEL port may be wrong, see the Settings tab to correct. \n\nYou may also need to turn PIXEL on and off during the 'Searching for PIXEL' message.";
				}
				else if (isWindows) {
					message = "Have you Bluetooth paired using code 0000 or 4545 for PIXEL V1? \n\nMake sure the toggle switch on PIXEL's side is pointing towards\nthe 'Bluetooth' position if using Bluetooth or towards 'PC USB' if connecting with USB.";
				}
				else { //then it's linux
					message = "Check that the toggle switch on PIXEL's side is pointing towards 'PC USB' \nand you've connected PIXEL to your computer using the included USB A-A cable. \n\nPlease ensure you follow these first time \nsetup instructions for Linux http://ledpixelart.com/raspberry-pi/ \n\nThe PIXEL port may be wrong, see the Settings tab to correct.";
				}
			    PixelApp.this.statusLabel.setText(message);
			    System.out.println(message);
			    String title = "PIXEL Not Found";
			    JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
			}
			else { 
				String message = "PIXEL FOUND: Click to stream or double click to write";
	            PixelApp.this.statusLabel.setText(message);
			}
	    }
	}
    }
    
    public class TabChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent e) 
	{
	    for(PixelPanel panel : builtinPixelPanels)
	    {
		panel.stopPixelActivity();
		//System.out.println("built in panels: " + builtinPixelPanels);
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
