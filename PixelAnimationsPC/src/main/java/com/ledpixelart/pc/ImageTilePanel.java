
package com.ledpixelart.pc;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * @author rmarquez
 */
public class ImageTilePanel extends PixelTilePanel
{
       
    protected String imageListPath = "/images.text";
    
    public ImageTilePanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);	
    }
    
    @Override
    public void actionPerformed(ActionEvent event) 
    {	
	String command = event.getActionCommand();
	System.out.println("image comamand: " + command);	
        
        if (!pixelFound) 
        {  
            //only go here if PIXEL wa found, other leave the timer
            return;
        }

        String framestring = "images/" + command + ".png";
        try 
        {
            System.out.println("Attemping to load " + framestring + " from the classpath.");
            writeImagetoMatrix(framestring);
        } 
        catch (ConnectionLostException e1) 
        {
            e1.printStackTrace();
        }               
    }    
	
    @Override
    protected ImageIcon getImageIcon(String pathName) 
    {
	URL iconUrl = getClass().getResource( imagePath() + "/" + pathName);
	ImageIcon icon = new ImageIcon(iconUrl);
	
	return icon;
    }

    @Override
    protected List<String> imageNames() throws Exception
    {
	List<String> names = new ArrayList();	
	InputStream instream = getClass().getResourceAsStream(imageListPath);
	BufferedReader br = new BufferedReader(new InputStreamReader(instream));
	String line = br.readLine();  	
	while (line != null)   
	{
	    names.add(line);
	    line = br.readLine();
	}	
	instream.close(); 		
	
	return names;
    }       
    
    @Override
    protected String imagePath() 
    {
	return "/images";
    }
    
}
