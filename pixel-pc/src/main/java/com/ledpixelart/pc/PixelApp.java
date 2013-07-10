
package com.ledpixelart.pc;

import com.ledpixelart.pc.plugins.PluginConfigEntry;
import com.ledpixelart.pc.plugins.swing.AnimationsPanel;
import com.ledpixelart.pc.plugins.swing.ImageTilePanel;

import com.ledpixelart.pc.plugins.swing.PixelTilePanel;
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
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.pixel.plugins.swing.PixelPanel;
import org.onebeartoe.pixel.plugins.swing.ScrollingTextPanel;
import org.onebeartoe.pixel.preferences.JavaPreferencesService;
import org.onebeartoe.pixel.preferences.PixelPreferencesKeys;
import org.onebeartoe.pixel.preferences.PreferencesService;

public class PixelApp extends IOIOSwingApp
{    
    
    private final Logger logger;
    
    private PreferencesService preferenceService;
    
    private Timer searchTimer;
    
    private static IOIO ioiO;
    
    private static RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
     
    public static final Pixel pixel = new Pixel(KIND);
    
    private UserProvidedPanel localImagesPanel;
    
    private List<PixelPanel> builtinPixelPanels;
    
    private List<PixelPanel> userPluginPanels;
    
    private List<PluginConfigEntry> userPluginConfiguration;
    
    private JFrame frame;
    
    private JTabbedPane tabbedPane;
    
    private JFileChooser pluginChooser;
    
    private JLabel statusLabel;
    
    public static final int DEFAULT_HEIGHT = 600;
    
    public static final int DEFAULT_WIDTH = 450;
    
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

	// images tab
	String path = "/tab_icons/apple_small.png";
	URL url = getClass().getResource(path);
        ImageIcon imagesTabIcon = new ImageIcon(url);
	PixelTilePanel imagesPanelReal = new ImageTilePanel(pixel.KIND);
	imagesPanelReal.populate();
	builtinPixelPanels.add(imagesPanelReal);
	
	// user images tab
	String userIconPath = "/tab_icons/my_small.png";
	URL userUrl = getClass().getResource(userIconPath);
	ImageIcon userTabIcon = new ImageIcon(userUrl);	
	String key = PixelPreferencesKeys.userImagesDirectory;	
	String defaultValue = System.getProperty("user.home");
	String localUserPath = preferenceService.get(key, defaultValue);

	File localUserDirectory = new File(localUserPath);
	localImagesPanel = new UserProvidedPanel(pixel.KIND, localUserDirectory);
	localImagesPanel.populate();
	builtinPixelPanels.add(localImagesPanel);

	// animations tab
	String path2 = "/tab_icons/ship_small.png";
	URL url2 = getClass().getResource(path2);
	ImageIcon animationsTabIcon = new ImageIcon(url2);
	final PixelTilePanel animationsPanel = new AnimationsPanel(pixel.KIND);
	animationsPanel.populate();
	builtinPixelPanels.add(animationsPanel);

	// scrolling text panel
	String path3 = "/tab_icons/text_small.png";
	URL url3 = getClass().getResource(path3);
	ImageIcon textTabIcon = new ImageIcon(url3);
        PixelPanel scrollPanel = new ScrollingTextPanel(pixel.KIND);
        builtinPixelPanels.add(scrollPanel);        

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
	tabbedPane.addTab("Images", imagesTabIcon, imagesPanelReal, "Load built-in images.");
	tabbedPane.addTab("My Images", userTabIcon, localImagesPanel, "This panel displays images from your local hard drive.");
        tabbedPane.addTab("Animations", animationsTabIcon, animationsPanel, "Load built-in animations.");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
	
	tabbedPane.setMnemonicAt(2, KeyEvent.VK_4);
	tabbedPane.addTab("Scolling Text", textTabIcon, scrollPanel, "Scrolls a text message across the PIXEL");
	
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
	
	return frame;
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
	menuItem.getAccessibleContext().setAccessibleDescription("update with accessible description");
	menuItem.addActionListener( new InstructionsListener() );
	helpMenu.add(menuItem);
	
	menuItem = new JMenuItem("About");
	KeyStroke aboutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(aboutKeyStroke);
	menuItem.getAccessibleContext().setAccessibleDescription("update with accessible description");
	menuItem.addActionListener( new AboutListener() );
	helpMenu.add(menuItem);
	
	menuItem = new JMenuItem("Exit");
	KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK);
	menuItem.setAccelerator(keyStroke);
	menuItem.addActionListener( new QuitListener() );
	menuItem.getAccessibleContext().setAccessibleDescription("update with accessible description");
	helpMenu.add(menuItem);

	menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
	menuItem.setMnemonic(KeyEvent.VK_D);
	helpMenu.add(menuItem);
	
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
                pixel.ioiO = ioio_;
                
                for(PixelPanel panel : userPluginPanels)
                {
//System.out.println("\nSetting pixel for panel: " + panel.getClass() + "\n");
                    panel.pixel = pixel;
                }
                
                for(PixelPanel panel : builtinPixelPanels)
                {
                    panel.pixel = pixel;
                }
                
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
		String message = "PIXEL was disconected";
		System.out.println(message);
		statusLabel.setText(message);
	    }

	    @Override
	    public void incompatible() 
	    {
		String message = "Incompatible firmware detected";
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
    
    private void savePreferences()
    {
	preferenceService.saveWindowPreferences(frame);
	preferenceService.saveBuiltInPluginsPreferences(localImagesPanel);
    }
    
    private List<PixelPanel> loadPluginPreferences() throws Exception
    {
	List<PixelPanel> foundClasses = preferenceService.restoreUserPluginPreferences(KIND, userPluginConfiguration);
        
/*	
	String path = "../pixel-weather/target/pixel-weather-1.0-SNAPSHOT-jar-with-dependencies.jar";        
	String className = "org.onebeartoe.pixel.plugins.weather.WeatherByWoeid";
//	PixelPanel plugin = loadPlugin(path, className);
//	foundClasses.add(plugin);

	path = "../pixel-games/target/pixel-games-1.0-SNAPSHOT.jar";
	className = "org.onebeartoe.games.pixel.press.your.button.PressYourButton";
//	PixelPanel gamePlugin = loadPlugin(path, className);
//	foundClasses.add(gamePlugin);
*/
        
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
//	    panel.setPixelFound(true);
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
	    String path = "/images/";
	    String iconPath = path + "aaagumball.png";
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
	    String path = "/images/";
	    String iconPath = path + "aaagumball.png";
	    URL resource = getClass().getResource(iconPath);
	    ImageIcon imageIcon = new ImageIcon(resource);
	    String message = "PIXEL Instructions";
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
	    for(PixelPanel panel : builtinPixelPanels)
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
