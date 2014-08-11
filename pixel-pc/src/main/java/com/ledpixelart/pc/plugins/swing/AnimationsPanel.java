
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
    
  //  private static int numFrames = 0;
    
   // private static int selectedFileDelay = 0;
    
    private static String animation_name;
    
    private volatile Timer timer;
    
    private static ActionListener AnimateTimer;
    
    private static String selectedFileName;
    
  	private static String decodedDirPath;
  	
  	private static byte[] BitmapBytes;
  	
  	private static short[] frame_;
  	
  	private static String framestring;
  	
  	private static float fps = 0;
  	
  	private static String userHome = System.getProperty("user.home");
  	
  	private static String decodedDir = userHome + "/pixel/animations/decoded/";  //users/al/pixel/animations/decoded
  	
  	private static String soureGIFDir = userHome + "/pixel/animations/decoded/sourcegif/";  //users/al/pixel/animations/decoded
  	
    private static float GIFfps;
    
    private static int GIFnumFrames;
    
    private static int GIFselectedFileDelay;
    
    private static int GIFresolution;
    
    private static boolean writeMode = false;
    
    public AnimationsPanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);
	imageListPath = "/animations.text";
        
        AnimateTimer = new ActionListener() 
	{
	    public void actionPerformed(ActionEvent evt) 
	    {
		i++;

		if (i >= GIFnumFrames - 1) 
		{
		    i = 0;
		}
			PixelApp.pixel.SendPixelDecodedFrame(decodedDir, animation_name, i, GIFnumFrames, GIFresolution, PixelApp.KIND.width,PixelApp.KIND.height);
	    }
	};
    }
    
    
    @Override
    public void actionPerformed(ActionEvent event) 
    {
	
   /* 
    let's first check if the decoded animation exists on the local user directory
    If it's not there, then we'll copy the gif to the source directory and decode it
    If it is there, then let's check if the resolution is correct
    If wrong resolution, then we'll need to delete the file and decode
    If it is there, then we're good and let's stream
    
    we'll use this directory structure
    
    user home/pixel/animations/decoded/
    user home/pixel/animations/decoded/sourcegif/  not sure we need this one?
    */
    	
    String selectedFileName = event.getActionCommand();
    
   /* if (PixelApp.pixel.GIFTxtExists(decodedDir,selectedFileName) == true && PixelApp.pixel.GIFRGB565Exists(decodedDir,selectedFileName) == true) {
    	System.out.println("This GIF was already decoded");
    }
    else {  //the text file is not there so we cannot continue and we must decode, let's first copy the file to home dir
    	//PixelApp.pixel.copyJARGif(selectedFileName, soureGIFDir); //let's first copy the gif to home dir 
    	//and now we need to decode it and create the RGB565 and txt files
    	//PixelApp.pixel.decodeGIFJar(currentDir, gifName, currentResolution, pixelMatrix_width, pixelMatrix_height);
    	//d decodeGIFJar(String decodedDir, String gifName, int currentResolution, int pixelMatrix_width, int pixelMatrix_height) {  //pass the matrix type
    	PixelApp.pixel.decodeGIFJar(decodedDir, selectedFileName, PixelApp.currentResolution, KIND.width, KIND.height);
    }*/
    
    if (PixelApp.pixel.GIFNeedsDecoding(decodedDir, selectedFileName, PixelApp.currentResolution) == true) {
    	PixelApp.pixel.decodeGIFJar(decodedDir, selectedFileName, PixelApp.currentResolution, PixelApp.KIND.width, PixelApp.KIND.height);
    }

	    //****** Now let's setup the animation ******
	    
	    animation_name = selectedFileName;
	    
	    GIFfps = pixel.getDecodedfps(decodedDir, animation_name); //get the fps //to do fix this later becaause we are getting from internal path
	    GIFnumFrames = PixelApp.pixel.getDecodednumFrames(decodedDir, animation_name);
	    GIFselectedFileDelay = pixel.getDecodedframeDelay(decodedDir, animation_name);
	    GIFresolution = pixel.getDecodedresolution(decodedDir, animation_name);
	    
	    System.out.println("Selected GIF Resolution: " + GIFresolution);
		System.out.println("Current LED Panel Resolution: " + PixelApp.currentResolution);
		System.out.println("GIF Width: " + PixelApp.KIND.width);
		System.out.println("GIF Height: " + PixelApp.KIND.height);
            
            stopExistingTimer();
            
            //**** old code here ****
    	    // stopExistingTimer();
    	  //  timer = new Timer(selectedFileDelay, AnimateTimer);
    	  //  timer.start();
    	   //***********************
            writeMode = true;
            
             if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //change this to a double click event later
    				/*	PixelApp.pixel.interactiveMode();
    					//send loading image
    					PixelApp.pixel.writeMode(fps); //need to tell PIXEL the frames per second to use, how fast to play the animations
    					sendFramesToPIXEL(); //send all the frame to PIXEL
    					PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
    				 */  
    		
    					PixelApp.pixel.interactiveMode();
    					//send loading image
    					PixelApp.pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
    					System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
    					  int y;
    				    	 
    				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
    				      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!

    			    			System.out.println("Writing " + animation_name + " to PIXEL " + "frame " + y);
    				 		    PixelApp.pixel.SendPixelDecodedFrame(decodedDir, animation_name, y, GIFnumFrames, GIFresolution, PixelApp.KIND.width,PixelApp.KIND.height);
    				   	  } //end for loop
    					PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
    					System.out.println("Writing " + animation_name + " to PIXEL complete, now displaying...");
    			
    			}
    			else {
    				   stopExistingTimer();
    				   timer = new Timer(GIFselectedFileDelay, AnimateTimer);
    				   timer.start();
    			}    

	}

    /*private static void sendFramesToPIXEL() { 
    	  int y;
     	 
    	  for (y=0;y<GIFnumFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
  		
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
      	 
      }*/
    
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
