
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

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
        
        String imagePath = "images/" + command;
	
        try 
        {
            System.out.println("Attemping to load " + imagePath + " from the classpath.");
	    URL url = PixelApp.class.getClassLoader().getResource(imagePath);

	    BufferedImage originalImage = ImageIO.read(url);
	    
	//	if (PixelApp.pixelFirmware.equals("PIXL0003")) {
	    if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL")) { //then it's a PIXEL V2 unit that can write to the sd card, otherwise just stream
				PixelApp.pixel.interactiveMode();
				//send loading image
				PixelApp.pixel.writeMode(10); //need to tell PIXEL the frames per second to use, how fast to play the animations
				PixelApp.pixel.writeImagetoMatrix(originalImage);
				PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
			}
			else {
				   //stopExistingTimer();
				   //timer = new Timer(selectedFileDelay, AnimateTimer);
				   //timer.start();
				 PixelApp.pixel.writeImagetoMatrix(originalImage);
			}
        } 
        catch (Exception e1) 
        {
            e1.printStackTrace();
        }
    }    
	
    @Override
    protected ImageIcon getImageIcon(String pathName) 
    {
	String path = imagePath() + "/" + pathName;
	URL iconUrl = getClass().getResource(path);
	if(iconUrl == null)
	{
	    System.err.println("iconUrl is null for " + path);
	}
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
