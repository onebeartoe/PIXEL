
package com.ledpixelart.pc.plugins.swing;


import com.ledpixelart.pc.AboutPanel;
import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.RestartPanel;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
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
    
    private static String portName_;
     
    private JLabel label;
    
    private static JTextArea mainText;
    
    private static JTextArea portText; 
    
    private static JFrame frame;

    private static JLabel portLabel_;

    private static Preferences prefs;
    
    private static int ledMatrix_;

    private static String pixelPrefNode = "/com/ledpixelart/pc";
   
    private static String serialPortInstructions = "- Enter the port IOIO appears on your computer in the field above" + "\n"
			+ "- Winddows: Open device manager and look for the COM port # next to the device called 'IOIO OTG', format will be COMXX. Ex. COM9 or COM14" + "\n" 
			+ "- Mac OSX: Type < ls /dev/tty.usb* > from a command prompt, format will be /dev/tty.usbmodemXXXX. Ex. /dev/tty.usbmodem1411 or /dev/tty.usbmodem1421" + "\n" 
			+ "- Raspberry Pi and LINUX: format will be IOIOX. Ex. IOIO0 or IOIO1" + "\n";
    
    public SettingsTilePanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);
    
    JButton saveButton = new JButton("Save");
    
    textPanel = new JPanel( new BorderLayout());
	//textPanel.add(configurationPanel, BorderLayout.NORTH);
	textPanel.setBorder( BorderFactory.createTitledBorder("PIXEL Model Selection"));	
	
	String labels[] = { "Seeed 32x16", "Adafruit 32x16", "Seeed 32x32", "PIXEL 32x32 (DEFAULT)", "64x32 (2x1 horz)",
			"32x64 (1x2 vert)", "Mirrored (2 displays)","Mirrored (4 displays)","128x32 (4x1 horz)",
			"32x128 (1x4 vert)","SUPER PIXEL 64x64 (2x2 square)"};

    final JComboBox ledMatrixCombo = new JComboBox(labels);
    
    prefs = Preferences.userRoot().node(pixelPrefNode); //let's get our preferences
	int defaultLEDMatrix = 3; //if the pref does not exist yet, use this
	ledMatrix_ = prefs.getInt("prefMatrix", defaultLEDMatrix);
	
	//portName_ = prefs.get(prefPort_, defaultPortValue);
    
    ledMatrixCombo.setSelectedIndex(ledMatrix_);
    
    ledMatrixCombo.setEditable(true);
        
    JPanel propertiesPanel = new JPanel( new GridLayout(2,1, 10,10) );
    
    propertiesPanel.add(textPanel);
    propertiesPanel.add(ledMatrixCombo);
    //propertiesPanel.add(saveButton);
//	propertiesPanel.add(speedPanel);
        
    setLayout(new BorderLayout());
	add(propertiesPanel, BorderLayout.NORTH);
	
	ledMatrixCombo.addActionListener (new ActionListener () {
	    public void actionPerformed(ActionEvent e) {
	    	
	    	int selectMatrixInt = ledMatrixCombo.getSelectedIndex();
	    	prefs.putInt("prefMatrix", selectMatrixInt);
	    	System.out.println("Selected LED Matrix is: " + selectMatrixInt);
	    	
	    	if (ledMatrix_ != selectMatrixInt) {
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
