
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

// rename this to AboutPanel
public class AboutPanel extends JPanel
{
    
    private final String developmentUrl = "http://www.onebeartoe.com";
    private final String pixelURL = "http://ledpixelart.com/";
    
    private JLabel developmentLabel;
    
    public AboutPanel()
    {
	String version = "3.7.0";
	String text = "PIXEL: LED ART " + version;
	String html = "<html><body><h2>" + text + "</h2></body></html>";
	JLabel productLabel = new JLabel(html, JLabel.LEFT);
	String firmware = "Firmware Version: " + PixelApp.pixelFirmware;
	String hardware = "Hardware Version: " + PixelApp.pixelHardwareID;
	String htmlFirmware = "<html><body><h3>" + firmware + "</h3></body></html>";
	String htmlHardware = "<html><body><h3>" + hardware + "</h3></body></html>";
	JLabel firmwareLabel = new JLabel(htmlFirmware, JLabel.LEFT);
	JLabel hardwareLabel = new JLabel(htmlHardware, JLabel.LEFT);
	
	String developmentHtml = "<html><body><h3>" + "Sofware Development<br/><br/>" + developmentUrl + "</h3></body></html>";
	developmentLabel = new JLabel(developmentHtml);
	developmentLabel.addMouseListener(new DevelopmentLabelListener() );
	
	String projectHtml = "<html><body><h3>PIXEL Home Page<br/><br/>" + pixelURL + "</h3></body></html>";
	JLabel projectLabel = new JLabel(projectHtml);
	projectLabel.addMouseListener(new DevelopmentLabelListener() );
	
	GridLayout layout = new GridLayout(5, 1, 6, 6);
	setLayout(layout);
	
	add(productLabel);
	add(projectLabel);
	add(developmentLabel);
	add(firmwareLabel);
	add(hardwareLabel);
	
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
		JOptionPane.showMessageDialog(AboutPanel.this, message);
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
