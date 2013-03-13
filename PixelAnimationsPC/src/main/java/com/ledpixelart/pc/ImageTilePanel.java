
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
   
    private static int i = 0;
    
    protected String imageListPath = "/images.text";
    
    private String command;
    
    private static ActionListener animateTimer = null;
    
    private int numFrames = 0;
    
    public ImageTilePanel(RgbLedMatrix matrix, RgbLedMatrix.Matrix KIND)
    {
	super(matrix, KIND);
	
	animateTimer = new ActionListener() 
	{
	    public void actionPerformed(ActionEvent evt) 
	    {
		if (!pixelFound) 
		{  
		    //only go here if PIXEL wa found, other leave the timer
		    return;
		}

		i++;

		if (i >= numFrames - 1) 
		{
		    i = 0;
		}

		String animationName = command;		
		String framestring = "/images/" + animationName + ".rgb565";
		try 
		{
		    System.out.println("Attemping to load " + framestring + " from the classpath.");
		    loadRGB565(framestring);
		} 
		catch (ConnectionLostException e1) 
		{
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	};
    }
    
    @Override
    public void actionPerformed(ActionEvent event) 
    {	
	command = event.getActionCommand();
	System.out.println("image comamand: " + command);	
	int selectedFileDelay = 100;
	Timer timer = new Timer(selectedFileDelay, animateTimer);

	if (timer.isRunning() == true) 
        {
	    timer.stop();
	}
	timer.start();
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
