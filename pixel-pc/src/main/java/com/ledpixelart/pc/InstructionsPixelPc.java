
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
public class InstructionsPixelPc extends JPanel
{
    
    private final String developmentUrl = "http://ledpixelart.com/";
    private final String pixelURL = "http://ledpixelart.com/";
    
    private JLabel developmentLabel;
    
    public InstructionsPixelPc()
    {
	String version = "0.5";
	String text = "PIXEL Instructions";
	String html = "<html><body><h1>" + text + "</h1></body></html>";
	JLabel productLabel = new JLabel(html, JLabel.CENTER);
	
	String developmentHtml = "<html><body>" + "Before this application will function, you must first Bluetooth pair PIXEL to your Android device<br/><br/>PIXEL will show up in Bluetooth settings as “PIXEL”, use pairing code 4545<br/><br/>If this application does not find PIXEL: power off and on PIXEL, Bluetooth pair again, and then re-run this application<br/><br/>More info at http://ledpixelart.com" + "</body></html>";
	developmentLabel = new JLabel(developmentHtml);
	developmentLabel.addMouseListener(new DevelopmentLabelListener() );
	
	//String projectHtml = "<html><body><h3>PIXEL Home Page<br/><br/>" + pixelURL + "</h3></body></html>";
	//JLabel projectLabel = new JLabel(projectHtml);
	//projectLabel.addMouseListener(new DevelopmentLabelListener() );
	
	GridLayout layout = new GridLayout(3, 1, 6, 6);
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
		Logger.getLogger(AboutPixelPc.class.getName()).log(Level.SEVERE, null, ex);
		String message = "Please visit " + developmentUrl + "for more information";
		JOptionPane.showMessageDialog(InstructionsPixelPc.this, message);
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
