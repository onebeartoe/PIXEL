
package com.ledpixelart.pc;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
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
    
    private String animation_name;
    
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
		// System.out.println("animate");

		i++;

		if (i >= numFrames - 1) 
		{
		    i = 0;
		}

		String animationName = evt.getActionCommand();
		// framestring = "animations/decoded/boat/boat" + i + ".rgb565";
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
	System.out.println("image comamand: " + command);
	
	String selectedFileName = event.getActionCommand();
	String decodedDirPath = "animations/decoded";

	//System.out.println("selected file name: " + selectedFileName);

	InputStream decodedFile = PixelApp.class.getClassLoader().getResourceAsStream(decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt"); //decoded/rain/rain.text
	//note can't use file operator here as you can't reference files from a jar file

	int selectedFileTotalFrames = 1;
	int selectedFileDelay = 100;
		
	if (decodedFile != null) 
	{
	    
	    // ok good, now let's read it, we need to get the total numbers of frames and the frame speed
	    String line = null;
	    try 
	    {
		//  BufferedReader br = new BufferedReader(new FileReader(decodedFile));
		BufferedReader br = new BufferedReader(
			new InputStreamReader(decodedFile));

		line = br.readLine();

		// while ((line = br.readLine()) != null) {
		//     text.append(line);
		//    text.append('\n');	   	             
		// }
	    } catch (IOException e) {
		//You'll need to add proper error handling here
	    }

	    String fileAttribs = line.toString();  //now convert to a string	 
	    //System.out.println(fileAttribs);
	    String fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60
	    String [] fileAttribs2 = fileAttribs.split(fdelim);
	    selectedFileTotalFrames = Integer.parseInt(fileAttribs2[0].trim());

	    //System.out.println("total frames: " + selectedFileTotalFrames);
	    //System.out.println(fileAttribs2[0] + " " + fileAttribs2[1] + fileAttribs2[2]);

	    selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());
	    //selectedFileResolution = 32;
	    //selectedFileResolution = Integer.parseInt(fileAttribs2[2].trim());
	}

	// System.out.println(fileAttribs);

	//****** Now let's setup the animation ******
	int i = 0;
	animation_name = event.getActionCommand();
	int numFrames = selectedFileTotalFrames;
	// System.out.println("file delay: " + selectedFileDelay);

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
