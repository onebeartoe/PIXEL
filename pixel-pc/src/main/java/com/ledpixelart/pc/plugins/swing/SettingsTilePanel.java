
package com.ledpixelart.pc.plugins.swing;



import com.ledpixelart.pc.AboutPanel;
import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.RestartPanel;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author rmarquez
 */
public class SettingsTilePanel extends PixelTilePanel
{
       
    protected String imageListPath = "/images.text";
    
    private JTextField textField;
    
    private JComboBox<String> fontFamilyChooser;
    
    protected JPanel textPanel;

    private JSlider scrollSpeedSlider;
     
    private JLabel label;
    
    private static JTextArea mainText;
    
    private static JTextArea portText; 
    
    private static JFrame frame;

    private static JLabel portLabel_;
    
    private static JLabel LEDPanelLabel_;

    private static Preferences prefs;
    
    private static int ledMatrix_;

    private static String pixelPrefNode = "/com/ledpixelart/pc";
    
    private JTextField pixelPortText;
    
    private JButton saveButton;
    
    private static String prefSavedPort_;
    
    private static String combinedText;
    
    private final static boolean shouldFill = true;

	private final static boolean shouldWeightX = true;

   
    private static String serialPortInstructions = "If on Mac OSX or LINUX/Raspberry Pi (USB connections only are supported), you'll need to enter the port for PIXEL in the field above" + "\n"
    		+ "For Mac OSX: if PIXEL is still not found after entering the port, turn PIXEL on and off during the 'Searching for PIXEL' message. PIXEL should then be found." + "\n"
    		+ "Note: the port will change if you plug PIXEL into a different USB port on your computer and you'll need to re-enter the new port for PIXEL" + "\n"
    		+ "If on Windows, you do not need to enter a port for PIXEL and can leave blank unless your PC is unable to find PIXEL" + "\n\n"
    		+ "How to find PIXEL's port:" + "\n"
			+ "- Winddows: Open device manager and look for the COM port # next to the device called 'IOIO OTG', format will be COMXX. Ex. COM9 or COM14" + "\n" 
			+ "- Mac OSX: Type < ls /dev/tty.usb* > from a command prompt, format will be /dev/tty.usbmodemXXXX. Ex. /dev/tty.usbmodem1411 or /dev/tty.usbmodem1421" + "\n" 
			+ "- Raspberry Pi and LINUX: format will be IOIOX. Ex. IOIO0 or IOIO1" + "\n";
    
    public SettingsTilePanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);
    
    textPanel = new JPanel( new BorderLayout());
	
	String labels[] = { "Seeed 32x16", "Adafruit 32x16", "Seeed 32x32", "PIXEL 32x32 (DEFAULT)", "64x32 (2x1 horz)",
			"32x64 (1x2 vert)", "Mirrored (2 displays)","Mirrored (4 displays)","128x32 (4x1 horz)",
			"32x128 (1x4 vert)","SUPER PIXEL 64x64 (2x2 square)"};

    final JComboBox ledMatrixCombo = new JComboBox(labels);
    
    prefs = Preferences.userRoot().node(pixelPrefNode); //let's get our preferences
    
	int defaultLEDMatrix = 3; //if the pref does not exist yet, use this
	
	ledMatrix_ = prefs.getInt("prefMatrix", defaultLEDMatrix);
	
    ledMatrixCombo.setSelectedIndex(ledMatrix_);
    
    ledMatrixCombo.setEditable(true);
    
    portLabel_ = new JLabel("PIXEL Port");
    LEDPanelLabel_ = new JLabel("Select PIXEL Model / LED Panel Type");
    
    //JPanel propertiesPanel = new JPanel( new GridLayout(4,2, 10,10) );
    JPanel propertiesPanel = new JPanel( new GridBagLayout() );
    
  	textPanel.add(propertiesPanel, BorderLayout.NORTH);
  	textPanel.setBorder( BorderFactory.createTitledBorder("Settings") );	
        
    setLayout(new BorderLayout());
  	add(propertiesPanel, BorderLayout.NORTH);
  	
  	prefSavedPort_ = prefs.get("prefSavedPort", ""); //leave it blank if not found
  	pixelPortText = new JTextField(prefSavedPort_);
  	saveButton = new JButton("Save");
  	mainText = new JTextArea("");
    
	GridBagConstraints c = new GridBagConstraints();
	if (shouldFill) {
	c.fill = GridBagConstraints.HORIZONTAL;
	}
	
	if (shouldWeightX) {
		c.weightx = 0.5;
		}
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 0;
	c.gridy = 0;
	c.insets = new Insets(0,30,0,0);  //left and right padding
	propertiesPanel.add(LEDPanelLabel_, c);
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 1;
	c.gridy = 0;
	c.insets = new Insets(0,20,0,400);  //left and right padding
	propertiesPanel.add(ledMatrixCombo, c);
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 0;
	c.gridy = 1;
	c.insets = new Insets(0,30,0,0);  //left and right padding
	propertiesPanel.add(portLabel_, c);
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 1;
	c.gridy = 1;
	c.insets = new Insets(0,20,0,400);  //left and right padding
	propertiesPanel.add(pixelPortText, c);
	
	/// ********** the save button ***********************
		c.insets = new Insets(0,0,0,0);  //leave some space on left and right side of buttons
		c.gridwidth = 2; //from here on, each component should takes up 2 columns
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 30;      
		c.gridx = 0;       
		c.gridy = 2;    
		propertiesPanel.add(saveButton, c);
		
		//*************Main Text Area *******************************************
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = 70;
		c.gridx = 0;
		c.gridy = 4;	
		Font font = new Font("Verdana", Font.PLAIN, 11);
		mainText.setFont(font);
		
		mainText.setLineWrap(true);
		mainText.setWrapStyleWord(true);
		mainText.setEditable(false);
		mainText.setBackground(Color.LIGHT_GRAY);
		mainText.setForeground(Color.BLUE);
		propertiesPanel.add(mainText, c);
		setMainStatus(serialPortInstructions);
  	
  	
  /*	propertiesPanel.add(LEDPanelLabel_);
    propertiesPanel.add(ledMatrixCombo);
    propertiesPanel.add(portLabel_); 
  	propertiesPanel.add(pixelPortText);
  	propertiesPanel.add(saveButton);
  	propertiesPanel.add(mainText);*/
  	
  	
/*  	pixelPortText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				prefs.put("prefSavedPort", pixelPortText.getText());
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				prefs.put("prefSavedPort", pixelPortText.getText());
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
         // implement the methods
     });*/
  	
  	saveButton.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
		    
		    /*if (portText.getText().equals("")) {
		    	combinedText = "Oops... You forgot to enter IOIO's Port\n\n"
		    			+ serialPortInstructions + "\n";
    		    mainText.setText(combinedText);
		    }*/
		    
		    if (!pixelPortText.getText().contains("dev") && !pixelPortText.getText().contains("DEV") //do we need dev if we have tty? TO DO test that
		    		&& !pixelPortText.getText().contains("COM") && !pixelPortText.getText().contains("com") 
		    		&& !pixelPortText.getText().contains("tty") && !pixelPortText.getText().contains("TTY") 
		    		&& !pixelPortText.getText().contains("ioio") && !pixelPortText.getText().contains("IOIO")
		    		&& !pixelPortText.getText().equals("")) {
		    	combinedText = "PIXEL PORT FORMAT IS NOT VALID, SEE VALID FORMATS BELOW\n\n"
		    			+  serialPortInstructions + "\n";
		    	setMainStatus(combinedText);
		    }
		    
		    else {
		    	prefs.put("prefSavedPort", pixelPortText.getText()); //let's write the prefs for the port
		    	setMainStatus("PIXEL Port Saved");
		    }
        }
    });
	
	ledMatrixCombo.addActionListener (new ActionListener () {
	    public void actionPerformed(ActionEvent e) {
	    	
	    	int selectMatrixInt = ledMatrixCombo.getSelectedIndex();
	    	prefs.putInt("prefMatrix", selectMatrixInt);
	    	System.out.println("Selected LED Matrix is: " + selectMatrixInt);
	    	
	    	if (ledMatrix_ != selectMatrixInt) {  //let's prompt the user if the matrix type has changed to restart
		    	String path = "/images/";
			    String iconPath = path + "aaagumball.png";
			    URL resource = getClass().getResource(iconPath);
			    ImageIcon imageIcon = new ImageIcon(resource);
			    String message = "Please Restart";
			    RestartPanel about = new RestartPanel();
			    JOptionPane.showMessageDialog(frame, about, message, JOptionPane.INFORMATION_MESSAGE, imageIcon);
	    	}
	    }
	});
	
    }
    
    private void setMainStatus (String message) {
    	mainText.setText(message);
  	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ImageIcon getImageIcon(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> imageNames() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String imagePath() {
		// TODO Auto-generated method stub
		return null;
	}
}
