
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;

/**
 * @author rmarquez
 */
public class ImageTilePanel extends PixelTilePanel
{
       
    protected String imageListPath = "/images.text";
    
    private String selectedFileName;
    
    private BufferedImage originalImage;
    
    public ImageTilePanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);	
    }
    
   /* @Override
    public void actionPerformed(ActionEvent event) //no longer using this one, we replaced it with mouselistener but keep it here just in case we need to go back
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
    }    */
	
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

	@Override
	public void mouseClicked(MouseEvent e) {
		
     if (PixelApp.getPixelFound() == true) { //let's make sure pixel is found before proceeding or we'll get a crash
		
		PixelApp.pixel.interactiveMode();
		
		 //System.out.println("image array test " +  PixelTilePanel.imagePathArray.get(2)); //didn't need this it turns out
		//String command = e.getActionCommand(); //this was for actionevent which we're no longer using because it can't do a double click
		
		Component command = e.getComponent();
		String localFileImagePath = PixelApp.pixel.getSelectedFilePath(command);
		selectedFileName = FilenameUtils.getName(localFileImagePath); //with no extension
		System.out.println("Selected File Name: " + selectedFileName);
		String gifNameNoExt = FilenameUtils.removeExtension(selectedFileName); //with no extension
		
	        String imagePath = "images/" + selectedFileName;
		
	        try 
	        {
	            System.out.println("Attemping to load " + imagePath + " from the classpath.");
		    URL url = PixelApp.class.getClassLoader().getResource(imagePath);

		    originalImage = ImageIO.read(url);
		
	        } 
	        catch (Exception e1) 
	        {
	            e1.printStackTrace();
	        }
		
		if (e.getClickCount() == 3) {
			
	      System.out.println("User triple clicked...");
	      if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL")) { //then it's a PIXEL V2 unit that can write to the sd card, otherwise just stream
				PixelApp.pixel.interactiveMode();
				//send loading image
				PixelApp.pixel.writeMode(10); //need to tell PIXEL the frames per second to use, how fast to play the animations
				try {
					PixelApp.pixel.writeImagetoMatrix(originalImage, PixelApp.KIND.width,PixelApp.KIND.height);
				} catch (ConnectionLostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
			}
	      else {  //they triple clicked but it's not a V2 unit so we can only stream
		      try {
					PixelApp.pixel.writeImagetoMatrix(originalImage, PixelApp.KIND.width,PixelApp.KIND.height);
				} catch (ConnectionLostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
      }
	      
	    } else if (e.getClickCount() == 2) {
	      System.out.println("User double clicked...");
	      if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL")) { //then it's a PIXEL V2 unit that can write to the sd card, otherwise just stream
					PixelApp.pixel.interactiveMode();
					//send loading image
					PixelApp.pixel.writeMode(10); //need to tell PIXEL the frames per second to use, how fast to play the animations
					try {
						PixelApp.pixel.writeImagetoMatrix(originalImage, PixelApp.KIND.width,PixelApp.KIND.height);
					} catch (ConnectionLostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
			}
	      else {  //they double clicked but it's not a V2 unit so we can only stream
			      try { 
						PixelApp.pixel.writeImagetoMatrix(originalImage, PixelApp.KIND.width,PixelApp.KIND.height);
					} catch (ConnectionLostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	      }
	    }
	    else {   //then it must have been a single click so let's just stream the image
	    	    
	    		try {
					PixelApp.pixel.writeImagetoMatrix(originalImage, PixelApp.KIND.width,PixelApp.KIND.height);
				} catch (ConnectionLostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
    
}