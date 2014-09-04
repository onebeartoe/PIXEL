
package com.ledpixelart.pc;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author rmarquez
 */

// rename this to InstructionsPanel
public class InstructionsPanel extends JPanel
{
    
    private final String developmentUrl = "http://ledpixelart.com/";
    private final String pixelURL = "http://ledpixelart.com/";
    
    private JLabel developmentLabel;
    
    public InstructionsPanel()
    {
	String version = "3.7.0";
	String text = "Welcome PIXEL User";
	String html = "<html><body><h1>" + text + "</h1></body></html>";
	JLabel productLabel = new JLabel(html, JLabel.CENTER);
	
	String developmentHtml = "<html><body>" + "IMPORTANT: You must first Bluetooth pair or USB connect PIXEL to your device.<br/><br/>Move the toggle switch on PIXEL to the 'PC USB' <br/>position if connecting wtih USB or leave selected on <br/>'Bluetooth' (default) for Bluetooth connections.<br/><br/>PIXEL will show up in Bluetooth settings as “PIXEL”, use <br/>pairing code 0000 or 4545 if you have a PIXEL V1 frame.<br/><br/>If this application does not find PIXEL: power off and on, <br/>Bluetooth pair again, and then re-run this application.<br/><br/>Click to stream GIFs and images or double click to write them <br/>for stand alone mode operation (only PIXEL V2 frames can write). <br/><br/>IMPORTANT: When double clicking to write, let the write <br/>fully finish before clicking somewhere else. <br/><br/>More info at http://ledpixelart.com" + "</body></html>";
	developmentLabel = new JLabel(developmentHtml);
	developmentLabel.addMouseListener(new DevelopmentLabelListener() );
	
	//String projectHtml = "<html><body><h3>PIXEL Home Page<br/><br/>" + pixelURL + "</h3></body></html>";
	//JLabel projectLabel = new JLabel(projectHtml);
	//projectLabel.addMouseListener(new DevelopmentLabelListener() );
	
	GridLayout layout = new GridLayout(2, 1, 6, 6);
	setLayout(layout);
	
	add(productLabel);
	add(developmentLabel);
	//add(projectLabel);
    }
    
    private class DevelopmentLabelListener extends MouseAdapter
    {
	@Override
	public void mouseClicked(MouseEvent e) 
	{
	    Desktop desktop = Desktop.getDesktop();
	    URI uri;
	    try 
	    {
		uri = new URI(developmentUrl);
		desktop.browse(uri);
	    } 
	    catch (Exception ex) 
	    {
		Logger.getLogger(AboutPanel.class.getName()).log(Level.SEVERE, null, ex);
		String message = "Please visit " + developmentUrl + "for more information";
		JOptionPane.showMessageDialog(InstructionsPanel.this, message);
	    }    
        }
	
	  
	
	@Override
	public void mouseEntered(MouseEvent e) 
	{
	    developmentLabel.setForeground(Color.BLUE);	    
	}
	
	@Override
	public void mouseExited(MouseEvent e) 
	{
	    developmentLabel.setForeground(Color.BLACK);
	}
    }
    
}
