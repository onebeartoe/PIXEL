
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
public class MacPixelPort extends JPanel
{
    
    private final String developmentUrl = "http://electronics.onebeartoe.org/";
    private final String pixelURL = "http://ledpixelart.com/";
    
    private JLabel developmentLabel;
    
    public MacPixelPort()
    {
	String version = "3.0.0";
	String text = "PIXEL PC Version " + version;
	String html = "<html><body><h2>" + "PIXEL's port has been detected and saved, please now close and restart this app" + "</h2></body></html>";
	JLabel productLabel = new JLabel(html, JLabel.CENTER);
	
	String developmentHtml = "<html><body><h3>" + "Sofware Development<br/><br/>" + developmentUrl + "</h3></body></html>";
	developmentLabel = new JLabel(developmentHtml);
	developmentLabel.addMouseListener(new DevelopmentLabelListener() );
	
	String projectHtml = "<html><body><h3>PIXEL Home Page<br/><br/>" + pixelURL + "</h3></body></html>";
	JLabel projectLabel = new JLabel(projectHtml);
	projectLabel.addMouseListener(new DevelopmentLabelListener() );
	
	GridLayout layout = new GridLayout(3, 1, 6, 6);
	setLayout(layout);
	
	add(productLabel);
	//add(developmentLabel);
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
		Logger.getLogger(MacPixelPort.class.getName()).log(Level.SEVERE, null, ex);
		String message = "Please visit " + developmentUrl + "for more information";
		JOptionPane.showMessageDialog(MacPixelPort.this, message);
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
