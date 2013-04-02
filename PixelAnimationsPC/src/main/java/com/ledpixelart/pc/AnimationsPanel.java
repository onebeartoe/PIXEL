
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
        
        AnimateTimer = new ActionListener() 
	{
	    public void actionPerformed(ActionEvent evt) 
	    {
		if (!pixelFound) 
		{  
		    //only go here if PIXEL wa found, other leave the timer
//		    return;
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

	//System.out.println("selected file name: " + selectedFileName);

	InputStream decodedFile = PixelAnimationsPC.class.getClassLoader().getResourceAsStream(decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt"); //decoded/rain/rain.text
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

	    //****** Now let's setup the animation ******
	    i = 0;
	    animation_name = event.getActionCommand();
	    numFrames = selectedFileTotalFrames;
	    // System.out.println("file delay: " + selectedFileDelay);

	    timer = new Timer(selectedFileDelay, AnimateTimer);

	    if (timer.isRunning() == true) 
	    {
		timer.stop();
	    }
	    timer.start();
	}
    }

    @Override
    protected String imagePath() 
    {
	return "/animations";
    }
    
    @Override
    public void stopPixelActivity()
    {
        if(timer != null && timer.isRunning() )
        {            
            timer.stop();
        }
    }
    
}

