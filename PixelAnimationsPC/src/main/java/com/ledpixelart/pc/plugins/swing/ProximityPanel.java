
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.*;
import com.ledpixelart.pcpixelart.PixelAnimationsPC;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * @author rmarquez
 */
public class ProximityPanel extends ImageTilePanel
    implements ActionListener   // remove this
{
    private AnalogInput proximitySensor;
    
    private int i;
    
    private static int numFrames = 0;
    
    private static String animation_name;
    
    private Timer timer;
    
    ProximityListener proximityListener;
	    
    private Timer animationTimer;
    
    private static ActionListener AnimateTimer;
    
    public ProximityPanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);	
    
	proximityListener = new ProximityListener();
	
        
        AnimateTimer = new ActionListener() 
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
		
		String framestring = "animations/decoded/" + animation_name + "/" + animation_name + i + ".rgb565";
		try 
		{
		    PixelApp.pixel.loadRGB565(framestring);
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
	String command = event.getActionCommand();
	System.out.println("animation comamand: " + command);

	String selectedFileName = event.getActionCommand();
	String decodedDirPath = "animations/decoded";	

	InputStream decodedFile = PixelAnimationsPC.class.getClassLoader().getResourceAsStream(decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt"); //decoded/rain/rain.text

	if (decodedFile != null) 
	{
	    // ok good, now let's read it, we need to get the total numbers of frames and the frame speed

	    String line = "";

	    try 
	    {
		InputStreamReader streamReader = new InputStreamReader(decodedFile);
		BufferedReader br = new BufferedReader(streamReader);
		line = br.readLine();
	    } 
	    catch (IOException e) 
	    {
		//You'll need to add proper error handling here
	    }

	    String fileAttribs = line.toString();  //now convert to a string	 

	    String fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60
	    String[] fileAttribs2 = fileAttribs.split(fdelim);
	    int selectedFileTotalFrames = Integer.parseInt(fileAttribs2[0].trim());

	    int selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());	    

	    //****** Now let's setup the animation ******
	    i = 0;
	    animation_name = event.getActionCommand();
	    numFrames = selectedFileTotalFrames;
	    // System.out.println("file delay: " + selectedFileDelay);

	    animationTimer = new Timer(selectedFileDelay, AnimateTimer);

	    if (animationTimer.isRunning() == true) 
	    {
		animationTimer.stop();
	    }
	    animationTimer.start();
	}
    }
    
    @Override
    public void startPixelActivity()
    {
	System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");
	
	proximitySensor  = PixelApp.getAnalogInput1();
	
	timer = new Timer(1000, proximityListener);
	timer.start();
    }
   
    @Override
    public void stopPixelActivity()
    {
	System.out.println("Preparing to stop PIXEL activity in " + getClass().getSimpleName() + ".");
	
        if(timer != null && timer.isRunning() )
        {            
	    System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }
    }
    
    private class ProximityListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) 
	{
	    if(proximitySensor == null)
	    {
		System.out.println("The proximity sensor is not initialized");
	    }
	    else
	    {
		try 
		{
		    float p = proximitySensor.read();
		    System.out.println("proximity sensor: " + p);
		} 
		catch (Exception ex) 
		{
		    Logger.getLogger(ProximityPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }	    
	}
    }
    
}
