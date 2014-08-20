
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import org.apache.commons.io.FilenameUtils;

/**
 * @author rmarquez
 */
public class AnimationsPanel64 extends ImageTilePanel implements MouseListener
{
    private int i;
  
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
  	
  	private static String decodedDir = userHome + "/pixel/animations/decoded/";  //we'll use the same local hard drive decoded dir as the 32x32's, just need to make sure we don't have the same filename or it will over-write
  	
  	private static String gifSourcePath = "animations64/gifsource/";  //path to the source gifs in the JAR
  	
    private static float GIFfps;
    
    private static int GIFnumFrames;
    
    private static int GIFselectedFileDelay;
    
    private static int GIFresolution;
    
    private static boolean writeMode = false;
    
    private static String fileType;
    
    public AnimationsPanel64(final RgbLedMatrix.Matrix KIND)
    {
	super(KIND);
	imageListPath = "/animations64.text";
        
        AnimateTimer = new ActionListener() 
	{
	    public void actionPerformed(ActionEvent evt) 
	    {
		i++;

		if (i >= GIFnumFrames - 1) 
		{
		    i = 0;
		}
			PixelApp.pixel.SendPixelDecodedFrame(decodedDir, animation_name, i, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
	    }
	};
    }
    
    @Override
	public void mouseClicked(MouseEvent e) {
    	if (PixelApp.getPixelFound() == true) { //let's make sure pixel is found before proceeding or we'll get a crash
			PixelApp.pixel.interactiveMode();
			
			Component command = e.getComponent();
			String localFileImagePath = PixelApp.pixel.getSelectedFilePath(command);
			selectedFileName = FilenameUtils.getName(localFileImagePath); //with the extension .gif
			fileType = FilenameUtils.getExtension(selectedFileName);
			System.out.println("Selected File Name: " + selectedFileName);
			
			if (e.getClickCount() == 3) {
				
		      System.out.println("User triple clicked...");
		      handleSelectedFile(true); //write
		    
	      }
		      
		     else if (e.getClickCount() == 2) {
		      System.out.println("User double clicked...");
		      handleSelectedFile(true); //write
		   
			}
		     
		    
		    else {   //then it must have been a single click so let's just stream the image
		      handleSelectedFile(false); //single click so just stream    
		    		
		    }
    	}
    	else {
    		 String message = "Oops.. PIXEL was not yet detected";
	         PixelApp.statusLabel.setText(message);
    	}
    }	

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void handleSelectedFile(boolean writeMode) {
			
			if (PixelApp.pixel.GIFTxtExists(decodedDir,selectedFileName) == true && PixelApp.pixel.GIFRGB565Exists(decodedDir,selectedFileName) == true) {
			    	System.out.println("This GIF was already decoded");
			    }
			    else {  //the text file is not there so we cannot continue and we must decode, let's first copy the file to home dir
			    
			    	PixelApp.pixel.decodeGIFJar(decodedDir, gifSourcePath, selectedFileName, PixelApp.currentResolution, KIND.width, KIND.height);
			    }
			    
			    if (PixelApp.pixel.GIFNeedsDecoding(decodedDir, selectedFileName, PixelApp.currentResolution) == true) {
			    	System.out.println("Selected LED panel is different than the encoded GIF, need to re-enocde...");
			    	PixelApp.pixel.decodeGIFJar(decodedDir, gifSourcePath, selectedFileName, PixelApp.currentResolution, KIND.width, KIND.height);
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
			            
			             if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //change this to a double click event later
			    			
			    					PixelApp.pixel.interactiveMode();
			    					PixelApp.pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
			    					System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
			    					  int y;
			    				    	 
			    				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
			    				      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
	
			    			    			System.out.println("Writing " + animation_name + " to PIXEL " + "frame " + y + " of " + GIFnumFrames);
			    				 		    PixelApp.pixel.SendPixelDecodedFrame(decodedDir, animation_name, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
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
    
    @Override
    protected String imagePath() 
    {
	return "/animations64";
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
