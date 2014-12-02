
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;

import ioio.lib.api.RgbLedMatrix;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.apache.commons.io.FilenameUtils;

/**
 * @author rmarquez
 */
public class AnimationsPanel extends ImageTilePanel implements MouseListener
{
    private int i;

// This member looks like it is not needed; use selectedFileName everywhere instead.    
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
  	
    private static String decodedDir = userHome + "/pixel/animations/decoded/";
  	
    /**
     * the path to the source gifs in the jar
     */
    private String gifSourcePath = "animations/gifsource/";
  	
//    private static float GIFfps;
    
    private static int GIFnumFrames;
    
//    private static int gifSelectedFileDelay;
    
    private static int GIFresolution;
    
    private static boolean writeMode = false;
    
    private static String fileType;
    
    public AnimationsPanel(final RgbLedMatrix.Matrix KIND)
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
                PixelApp.pixel.SendPixelDecodedFrame(decodedDir, animation_name, i, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
	    }
	};
    }
    
    @Override
    public void mouseClicked(MouseEvent e) 
    {
        // let's make sure pixel is found before proceeding or we'll get a crash
    	if (PixelApp.getPixelFound() == true) 
        { 
//TODO: Move away from using the static reference to the pixel, and use a getter on the application instead.            
            PixelApp.pixel.interactiveMode();
			
            Component command = e.getComponent();
            String localFileImagePath = PixelApp.pixel.getSelectedFilePath(command);
                        
//TODO: WHERE ELSE ARE WE USING APACHE COMMONS, AND IS THE WIEGHT OF THE JAR REALLY NEEDED?
            selectedFileName = FilenameUtils.getName(localFileImagePath); //with the extension .gif
            fileType = FilenameUtils.getExtension(selectedFileName);
            System.out.println("Selected File Name: " + selectedFileName);

            if (e.getClickCount() == 3) 
            {
                System.out.println("User triple clicked...");
                handleSelectedFile(true); //write
		    
            }
            else if (e.getClickCount() == 2) 
            {
                System.out.println("User double clicked...");
                handleSelectedFile(true); //write
		   
            }
            else 
            {   
                // then it must have been a single click so let's just stream the image
                handleSelectedFile(false); //single click so just stream    
            }
    	}
    	else 
        {
    		 String message = "Oops.. PIXEL was not yet detected";
	         PixelApp.statusLabel.setText(message);
    	}
    }	

	
    @Override
	
    public void mousePressed(MouseEvent e) 
    {
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
	
        
    public void handleSelectedFile(boolean writeMode)
    {
        if (PixelApp.pixel.GIFTxtExists(decodedDir,selectedFileName) == true && PixelApp.pixel.GIFRGB565Exists(decodedDir,selectedFileName) == true) 
        {
            System.out.println("This GIF was already decoded");
        }
        else 
        {  
            // the text file is not there so we cannot continue and we must decode, let's first copy the file to home dir
            PixelApp.pixel.decodeGIFJar(decodedDir, gifSourcePath,selectedFileName, PixelApp.currentResolution, KIND.width, KIND.height);
        }
        
        if (PixelApp.pixel.GIFNeedsDecoding(decodedDir, selectedFileName, PixelApp.currentResolution) == true) 
        {
            System.out.println("Selected LED panel is different than the encoded GIF, need to re-enocde...");
            PixelApp.pixel.decodeGIFJar(decodedDir, gifSourcePath, selectedFileName, PixelApp.currentResolution, KIND.width, KIND.height);
        }
	
        //****** Now let's setup the animation ******
        animation_name = selectedFileName;

        //to do fix this later becaause we are getting from internal path
        //get the fps 
        float GIFfps = pixel.getDecodedfps(decodedDir, animation_name);
        
        GIFnumFrames = PixelApp.pixel.getDecodednumFrames(decodedDir, animation_name);
        int gifSelectedFileDelay = pixel.getDecodedframeDelay(decodedDir, animation_name);
        GIFresolution = pixel.getDecodedresolution(decodedDir, animation_name);
				    
        System.out.println("Selected GIF Resolution: " + GIFresolution);
        System.out.println("Current LED Panel Resolution: " + PixelApp.currentResolution);
        System.out.println("GIF Width: " + KIND.width);
        System.out.println("GIF Height: " + KIND.height);

        stopExistingTimer();
			            
        if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true)
        {
            //change this to a double click event later			    			
            PixelApp.pixel.interactiveMode();
            PixelApp.pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
            System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 

            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
            setCursor(hourglassCursor);
            PixelApp.frame.setEnabled(false);
			    					
            new writePIXEL().execute(); //we'll run this in the background and also update the UI with progress
        }
        else
        {
            stopExistingTimer();
            timer = new Timer(gifSelectedFileDelay, AnimateTimer);
            timer.start();
        } 
    }

    class writePIXEL extends SwingWorker<Boolean, Integer> 
    {
	//SwingWorker<Boolean, Integer> writePIXEL = new SwingWorker<Boolean, Integer>() {
		   @Override
		   protected Boolean doInBackground() throws Exception {
			   
			   	int y;
			   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
			      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
			 		    PixelApp.pixel.SendPixelDecodedFrame(decodedDir, animation_name, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
			 		    publish(y); 
			   	  } //end for loop
			   
		
		    return true;
		   }

		   // Can safely update the GUI from this method.
		   protected void done() {
		    
		    boolean status;
		    try {
		     // Retrieve the return value of doInBackground.
		     status = get();
		     //we are done so we can now set PIXEL to local playback mode
		     
		 	 PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
			 System.out.println("PIXEL FOUND: Click to stream or double click to write");
			 String message = "PIXEL FOUND: Click to stream or double click to write";
		     PixelApp.statusLabel.setText(message);  
		     //restore the cursor
		     Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		     setCursor(normalCursor);
		     PixelApp.frame.setEnabled(true);
		      
		     
		    // statusLabel.setText("Completed with status: " + status);
		    } catch (InterruptedException e) {
		     // This is thrown if the thread's interrupted.
		    } catch (ExecutionException e) {
		     // This is thrown if we throw an exception
		     // from doInBackground.
		    }
		   }

		   @Override
		   // Can safely update the GUI from this method.
		   protected void process(List<Integer> chunks) {
		    // Here we receive the values that we publish().
		    // They may come grouped in chunks.
		    int mostRecentValue = chunks.get(chunks.size()-1);
		    System.out.println("DO NOT INTERRUPT: Writing frame " + Integer.toString(mostRecentValue) + " of " + GIFnumFrames);
		    String message = "DO NOT INTERRUPT: Writing frame " + Integer.toString(mostRecentValue) + " of " + GIFnumFrames;
	        PixelApp.statusLabel.setText(message);  
		    //countLabel1.setText(Integer.toString(mostRecentValue));
		   }
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
            System.out.println("Stopping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }        
    }
    
    @Override
    public void stopPixelActivity()
    {
        stopExistingTimer();
    }
    
}
