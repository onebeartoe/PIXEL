
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
public class AboutPixelPc extends JPanel
{
    
    private final String developmentUrl = "http://electronics.onebeartoe.org/";
    
    private JLabel developmentLabel;
    
    public AboutPixelPc()
    {
	String version = "0.5";
	String text = "PIXEL PC " + version;
	String html = "<html><body><h1>" + text + "</h1></body></html>";
	JLabel productLabel = new JLabel(html, JLabel.CENTER);
	
	String developmentHtml = "<html><body><h3>" + "Sofware Development:<br/><br/>" + developmentUrl + "</h3></body></html>";
	developmentLabel = new JLabel(developmentHtml);
	developmentLabel.addMouseListener(new DevelopmentLabelListener() );
	
	String projectHtml = "<html><body><h3>Project Home:<br/><br/>http://ledpixelart.com</h3></body></html>";
	JLabel projectLabel = new JLabel(projectHtml);
	
	GridLayout layout = new GridLayout(3, 1, 6, 6);
	setLayout(layout);
	
	add(productLabel);
	add(developmentLabel);
	add(projectLabel);
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
		JOptionPane.showMessageDialog(AboutPixelPc.this, message);
	    }    
        }
	
	@Override
	public void mouseEntered(MouseEvent e) 
	{
	    developmentLabel.setForeground(Color.GREEN);	    
	}
	
	@Override
	public void mouseExited(MouseEvent e) 
	{
	    developmentLabel.setForeground(Color.BLACK);
	}
    }
    
}
