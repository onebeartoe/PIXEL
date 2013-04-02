
package com.ledpixelart.pc;

import com.ledpixelart.hardware.Pixel;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;
import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.JFileChooser;

import javax.swing.UIManager;
import javax.swing.JButton;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PixelApp extends IOIOSwingApp
{    
    private boolean foundPixel;
    
    private final Logger logger;   

    private JFileChooser userDirectoryChooser;
    
    private JPanel userPanel;
    
    private PixelTilePanel userTilePanel;
    
    private List<PixelTilePanel> imagePanels;
    
    private static IOIO ioiO; 
    
    public static Pixel pixel;
    
    public PixelApp()
    {
	logger = Logger.getLogger(PixelApp.class.getName());//.log(Level.SEVERE, message, ex);	

        userDirectoryChooser = new JFileChooser();
        userDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	
	imagePanels = new ArrayList();
        
        RgbLedMatrix.Matrix KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
	
	pixel = new Pixel(KIND);
    }

    public static void main(String[] args) throws Exception 
    {		
	PixelApp app = new PixelApp();
	app.go(args);		
    }        

    public byte[] extractBytes(BufferedImage image) throws IOException 
    {
	// get DataBufferBytes from Raster
	WritableRaster raster = image.getRaster();
	DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

	return (data.getData());
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

	JFrame frame = new JFrame("PIXEL PC Edition");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	
	JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");                
	
	PixelTilePanel imagesPanelReal = new ImageTilePanel(pixel.KIND);
	imagesPanelReal.populate();
	imagePanels.add(imagesPanelReal);
	tabbedPane.addTab("Images", icon, imagesPanelReal, "Load built-in images.");
        
	final PixelTilePanel animationsPanel = new AnimationsPanel(pixel.KIND);
	animationsPanel.populate();
	imagePanels.add(animationsPanel);
        tabbedPane.addTab("Animations", icon, animationsPanel, "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        JComponent panel3 = makeTextPanel("Panel #3");
        tabbedPane.addTab("Interactive", icon, panel3, "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
                
        userPanel = new JPanel();	
	userPanel.setLayout( new BorderLayout() );
	JButton userButton = new JButton("Browse");
        userButton.addActionListener( new UserButtonListener() );
        userPanel.add(userButton, BorderLayout.NORTH);
	String path = System.getProperty("user.home");
        File homeDirectory = new File(path);
	userTilePanel = new UserProvidedPanel(pixel.KIND, homeDirectory);
	userTilePanel.populate();
	imagePanels.add(userTilePanel);
	userPanel.add(userTilePanel, BorderLayout.CENTER);
        tabbedPane.addTab("User Defined", icon, userPanel, "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);        
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener( new ChangeListener() 
        {
            public void stateChanged(ChangeEvent e) 
            {
		for(PixelTilePanel panel : imagePanels)
		{
		    panel.stopPixelActivity();
		}
                
            }
        });

	JMenuBar menuBar = createMenuBar();
	
	frame.add(tabbedPane, BorderLayout.CENTER);	
	frame.setSize(500, 450);		
	frame.setJMenuBar(menuBar);
	
	// center it
	frame.setLocationRelativeTo(null); 
	
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

	//Create the menu bar.
	menuBar = new JMenuBar();

	//Build the first menu.
	menu = new JMenu("Application");
	menu.setMnemonic(KeyEvent.VK_A);
	menu.getAccessibleContext().setAccessibleDescription(
		"The only menu in this program that has menu items");
	menuBar.add(menu);

	//a group of JMenuItems
	menuItem = new JMenuItem("A text-only menu item",
				 KeyEvent.VK_T);
	menuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_1, ActionEvent.ALT_MASK));
	menuItem.getAccessibleContext().setAccessibleDescription(
		"This doesn't really do anything");
	menu.add(menuItem);

	menuItem = new JMenuItem("Both text and icon",
				 new ImageIcon("images/middle.gif"));
	menuItem.setMnemonic(KeyEvent.VK_B);
	menu.add(menuItem);

	menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
	menuItem.setMnemonic(KeyEvent.VK_D);
	menu.add(menuItem);

	//a group of radio button menu items
	menu.addSeparator();
	ButtonGroup group = new ButtonGroup();
	rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
	rbMenuItem.setSelected(true);
	rbMenuItem.setMnemonic(KeyEvent.VK_R);
	group.add(rbMenuItem);
	menu.add(rbMenuItem);

	rbMenuItem = new JRadioButtonMenuItem("Another one");
	rbMenuItem.setMnemonic(KeyEvent.VK_O);
	group.add(rbMenuItem);
	menu.add(rbMenuItem);

	//a group of check box menu items
	menu.addSeparator();
	cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
	cbMenuItem.setMnemonic(KeyEvent.VK_C);
	menu.add(cbMenuItem);

	cbMenuItem = new JCheckBoxMenuItem("Another one");
	cbMenuItem.setMnemonic(KeyEvent.VK_H);
	menu.add(cbMenuItem);

	//a submenu
	menu.addSeparator();
	submenu = new JMenu("A submenu");
	submenu.setMnemonic(KeyEvent.VK_S);

	menuItem = new JMenuItem("An item in the submenu");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_2, ActionEvent.ALT_MASK));
	submenu.add(menuItem);

	menuItem = new JMenuItem("Another item");
	submenu.add(menuItem);
	menu.add(submenu);

	//Build second menu in the menu bar.
	menu = new JMenu("Plugins");
	menu.setMnemonic(KeyEvent.VK_N);
	menu.getAccessibleContext().setAccessibleDescription("This menu does nothing");
	menuBar.add(menu);
	
	return menuBar;
    }

/* delete me! */
     protected JPanel makeTextPanel(String text) 
     {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
     
    protected ImageIcon createImageIcon(String path) 
    {
        java.net.URL imgURL = PixelApp.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    private void enableButtons() 
    {
	
    }

    @Override
    public IOIOLooper createIOIOLooper(String connectionType, Object extra) 
    {
	return new BaseIOIOLooper() 
	{
	    private DigitalOutput led_;

	    @Override
	    protected void setup() throws ConnectionLostException,
		    InterruptedException 
	    {
		led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
                PixelApp.this.ioiO = ioio_;
		pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);
		setPixelFound();
		System.out.println("Found PIXEL: " + pixel.matrix + "\n");
		System.out.println("You may now interact with the PIXEL\n");
		enableButtons();
		
		//TODO: Load something on startup
	    }	    
	};
    }
    
    private void setPixelFound()
    {
	foundPixel = true;
	for(PixelTilePanel panel : imagePanels)
	{
	    panel.setPixelFound(true);
	}
    }
    
    private class UserButtonListener implements ActionListener    
    {
        public void actionPerformed(ActionEvent ae) 
        {
            int result = userDirectoryChooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File directory = userDirectoryChooser.getSelectedFile();
                if( directory == null )
                {
                    System.out.println("laters");   
                }
                else
                {                    
                    userPanel.remove(userTilePanel);                    
                    userTilePanel = new UserProvidedPanel(pixel.KIND, directory);
		    userTilePanel.populate();
                    userPanel.add(userTilePanel, BorderLayout.CENTER);
                }
            }            
        }        
    }
    
    public static RgbLedMatrix getMatrix() 
    {
        if (pixel.matrix == null) 
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
        
        return pixel.matrix;
    }
    
    public static AnalogInput getAnalogInput1() 
    {
        if (pixel.analogInput1 == null) 
	{
            try 
            {
                pixel.analogInput1 = ioiO.openAnalogInput(1);
            } 
            catch (ConnectionLostException ex) 
            {
                String message = "The IOIO connection was lost.";
                Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, message, ex);
            }
        }
        
        return pixel.analogInput1;
    }
    
}

