
package com.ledpixelart.pc;

import com.ledpixelart.pcpixelart.PixelAnimationsPC;
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
    
    private Timer timer;
    
    private static ActionListener AnimateTimer;
    
    public AnimationsPanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);
	imageListPath = "/animations.text";
        
        AnimateTimer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	 if (!pixelFound) {  //only go here if PIXEL wa found, other leave the timer
		    	        return;
		    	   }
		    	// System.out.println("animate");
		    	   
		    	 i++;

		    	    if (i >= numFrames - 1) {
		    	        i = 0;
		    	    }

		    	  // framestring = "animations/decoded/boat/boat" + i + ".rgb565";
		    	   String framestring = "animations/decoded/" + animation_name + "/" + animation_name + i + ".rgb565";
		    	    try {
						loadRGB565(framestring);
					} catch (ConnectionLostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

		    	   // if (i == numFrames - 1) {
		    	       // Animate.restart();
		    	  //  }
				          
				      }
		     
			  };
    }
    
    @Override
    public void actionPerformed(ActionEvent event) 
    {
	String command = event.getActionCommand();
	System.out.println("animation comamand: " + command);
        
        String selectedFileName = event.getActionCommand();
    String		decodedDirPath = "animations/decoded";
		
	    //System.out.println("selected file name: " + selectedFileName);
		
		InputStream decodedFile = PixelAnimationsPC.class.getClassLoader().getResourceAsStream(decodedDirPath + "/" + selectedFileName  + "/" + selectedFileName + ".txt"); //decoded/rain/rain.text
		//note can't use file operator here as you can't reference files from a jar file
		
		if (decodedFile != null) 
                {
                    // ok good, now let's read it, we need to get the total numbers of frames and the frame speed
                    
                    String line = "";

	   	      try 
                      {
	   	        BufferedReader 
	   	        br = new BufferedReader(
	   	                new InputStreamReader(decodedFile));
	   	        
	   	       line = br.readLine();
  	            
	   	       // while ((line = br.readLine()) != null) {
	   	         //     text.append(line);
	   	          //    text.append('\n');	   	             
	   	         // }
	   	      }
	   	      catch (IOException e) 
                      {
	   	          //You'll need to add proper error handling here
	   	      }

	   	    String fileAttribs = line.toString();  //now convert to a string	 
	   	    //System.out.println(fileAttribs);
	   	    String fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60
	        String [] fileAttribs2 = fileAttribs.split(fdelim);
	        int selectedFileTotalFrames = Integer.parseInt(fileAttribs2[0].trim());
	        
	   	    //System.out.println("total frames: " + selectedFileTotalFrames);
	   	    //System.out.println(fileAttribs2[0] + " " + fileAttribs2[1] + fileAttribs2[2]);
	        
	    	int selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());
	    	//selectedFileResolution = 32;
	    	//selectedFileResolution = Integer.parseInt(fileAttribs2[2].trim());
                
                
                //****** Now let's setup the animation ******
		i = 0;
		animation_name = event.getActionCommand();
		numFrames = selectedFileTotalFrames;
		// System.out.println("file delay: " + selectedFileDelay);
		
		 timer = new Timer(selectedFileDelay , AnimateTimer);
		 
		 if (timer.isRunning() == true) {
				timer.stop();
		 }
         timer.start();
		}
		
		 // System.out.println(fileAttribs);
		
		
    }

    @Override
    protected String imagePath() 
    {
	return "/animations";
    }
    
}
