
package com.ledpixelart.pc;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;
import java.awt.BorderLayout;

import java.awt.Container;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PixelApp extends IOIOSwingApp
{    
    private boolean foundPixel;
    
    private final Logger logger;
    
    private static RgbLedMatrix matrix_;

    public RgbLedMatrix getMatrix_() {
        return matrix_;
    }

    public void setMatrix_(RgbLedMatrix matrix_) {
        this.matrix_ = matrix_;
    }
    
    private static RgbLedMatrix.Matrix KIND;   

    private JFileChooser userDirectoryChooser;
    
    private JPanel userPanel;
    
    private PixelTilePanel userTilePanel;
    
    private List<PixelTilePanel> imagePanels;
    
    private static IOIO ioiO;   
    
    public PixelApp()
    {
	logger = Logger.getLogger(PixelApp.class.getName());//.log(Level.SEVERE, message, ex);	

        userDirectoryChooser = new JFileChooser();
        userDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	
	imagePanels = new ArrayList();
        
        KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
    }

    public static void main(String[] args) throws Exception 
    {		
	// ui stuff
	new PixelApp().go(args);			

	///************ this set of code loads a raw RGB565 image *****************
	// BitmapInputStream = PixelAlbumPC.class.getClassLoader().getResourceAsStream("images/gumball.rgb565"); 
	//loads in a raw file in rgb565 format, use the windows program paint.net with the rgb565 plug-in to convert png, jpg, etc to this format
	//BitmapInputStream = PixelAlbumPC.class.getClassLoader().getResourceAsStream("images/ginseng.rgb565"); 
	// loadRGB565();
	//**************************************************************************
	
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
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
	
	JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");                
	
        //matrix_ = getMatrix();
	PixelTilePanel imagesPanelReal = new ImageTilePanel(KIND);
	imagesPanelReal.populate();
	imagePanels.add(imagesPanelReal);
	tabbedPane.addTab("Images", icon, imagesPanelReal, "Load built-in images.");
        
	PixelTilePanel animationsPanel = new AnimationsPanel(KIND);
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
	userTilePanel = new UserProvidedPanel(KIND, homeDirectory);
	userTilePanel.populate();
	imagePanels.add(userTilePanel);
	userPanel.add(userTilePanel, BorderLayout.CENTER);
        tabbedPane.addTab("User Defined", icon, userPanel, "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);        
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	frame.add(tabbedPane, BorderLayout.CENTER);	
	frame.setSize(500, 450);
	
	// center it
	frame.setLocationRelativeTo(null); 
	
	frame.setVisible(true);
	
	return frame;
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
		matrix_ = ioio_.openRgbLedMatrix(KIND);
		setPixelFound();
		System.out.println("Found PIXEL: " + matrix_ + "\n");
		System.out.println("You may now select one of the animations\n");
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
                    RgbLedMatrix matrix_ = getMatrix();
                    
                    userTilePanel = new UserProvidedPanel(KIND, directory);
		    userTilePanel.populate();
                    userPanel.add(userTilePanel, BorderLayout.CENTER);
                }
            }            
        }        
    }
    
    public static RgbLedMatrix getMatrix() 
    {
        if (matrix_==null) {
            try 
            {
                matrix_ = ioiO.openRgbLedMatrix(KIND);
            } 
            catch (ConnectionLostException ex) 
            {
                String message = "The IOIO connection was lost.";
                Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, message, ex);
            }
        }
        
        return matrix_;
    }
    
}
