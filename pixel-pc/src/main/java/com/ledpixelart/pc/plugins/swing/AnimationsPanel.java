
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.Timer;

/**
 * @author rmarquez
 */
public class AnimationsPanel extends ImageTilePanel
{
    private int i;
    
    private static int numFrames = 0;
    
    private static String animation_name;
    
    private volatile Timer timer;
    
    private static ActionListener AnimateTimer;
    
    private static String selectedFileName;
    
  	private static String decodedDirPath;
  	
  	private static byte[] BitmapBytes;
  	
  	private static short[] frame_;
  	
  	private static String framestring;
  	
  	private static float fps = 0;
    
    public AnimationsPanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);
	imageListPath = "/animations.text";
        
        AnimateTimer = new ActionListener() 
	{
	    public void actionPerformed(ActionEvent evt) 
	    {
		i++;

		if (i >= numFrames - 1) 
		{
		    i = 0;
		}
		
		String framestring = "animations/decoded/" + animation_name + "/" + animation_name + i + ".rgb565";
		
System.out.println("framestring: " + framestring);

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
	String selectedFileName = event.getActionCommand();
	String decodedDirPath = "animations/decoded";

System.out.println("selected file name: " + selectedFileName);
	int i = selectedFileName.lastIndexOf(".");
	selectedFileName = selectedFileName.substring(0, i);
System.out.println("corrected file name: " + selectedFileName);

	String path = decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt";

	InputStream decodedFile = PixelApp.class.getClassLoader().getResourceAsStream(path);
	//note can't use file operator here as you can't reference files from a jar file

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
	    
	    if (selectedFileDelay != 0) {  //then we're doing the FPS override which the user selected from settings
    		fps = 1000.f / selectedFileDelay;
		} else { 
    		fps = 0;
    	}

	    //****** Now let's setup the animation ******
	    
	    animation_name = selectedFileName;
//	    i = 0;
//	    String name = event.getActionCommand();	    	    		
//	    int i = name.lastIndexOf(".");	    
//	    animation_name = name.substring(0, i);	    
	    
	    numFrames = selectedFileTotalFrames;
	    // System.out.println("file delay: " + selectedFileDelay);
            
            stopExistingTimer();
            
            //**** old code here ****
    	    // stopExistingTimer();
    	  //  timer = new Timer(selectedFileDelay, AnimateTimer);
    	  //  timer.start();
    	   //***********************
    	
    			if (PixelApp.pixelFirmware.equals("PIXL0003")) {
    					PixelApp.pixel.interactiveMode();
    					//send loading image
    					PixelApp.pixel.writeMode(fps); //need to tell PIXEL the frames per second to use, how fast to play the animations
    					sendFramesToPIXEL(); //send all the frame to PIXEL
    					PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
    			}
    			else {
    				   stopExistingTimer();
    				   timer = new Timer(selectedFileDelay, AnimateTimer);
    				   timer.start();
    			}    

	   // timer = new Timer(selectedFileDelay, AnimateTimer);
	   // timer.start();
	}
    }

    private static void sendFramesToPIXEL() { 
    	  int y;
     	 
    	  for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
  		
  		framestring = "animations/decoded/" + animation_name + "/" + animation_name + y + ".rgb565";
  		
  			System.out.println("writing to PIXEL frame: " + framestring);

  		try 
  		{
  		    PixelApp.pixel.loadRGB565(framestring);
  		} 
  		catch (ConnectionLostException e1) 
  		{
  		    // TODO Auto-generated catch block
  		    e1.printStackTrace();
  		}
    	  } //end for loop
      	 
      }
    
    @Override
    protected String imagePath() 
    {
	return "/animations";
    }
    
    private void stopExistingTimer()
    {
        if(timer != null && timer.isRunning() )
        {
            System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }        
    }
    
    @Override
    public void stopPixelActivity()
    {
        stopExistingTimer();
    }
    
}
