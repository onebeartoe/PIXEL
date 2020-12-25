
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.filters.ImageFilters;
import com.ledpixelart.pc.plugins.swing.AnimationsPanel.writePIXEL;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.apache.commons.io.FilenameUtils;

/**
 * @author rmarquez
 */
public class UserProvidedPanel extends ImageTilePanel implements MouseListener
{
    
    private JFileChooser userDirectoryChooser;
	
    private File imageDirectory;
    
    private List<File> singleImages;
    
    private volatile Timer timer;
    
    private static ActionListener AnimateTimer;
    
    private static boolean writeMode = false;
    
    private int i;
    
    private float GIFfps;
    
    private int GIFnumFrames;
    
    private int GIFselectedFileDelay;
    
    private int GIFresolution;
    
    private String animation_name;
    
    private String selectedFileName;
    
    private BufferedImage originalImage;
    
    private String localFileImagePath;
    
    private String localFileImagePathString;
    
    private String fileType;

    public UserProvidedPanel(final RgbLedMatrix.Matrix KIND, File imageDirectory)
    {
	super(KIND);
        this.imageDirectory = imageDirectory;
	
	userDirectoryChooser = new JFileChooser(this.imageDirectory);	
	userDirectoryChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	
	singleImages = new ArrayList();
		
	JButton userButton = new JButton("Browse for a single image or a folder of images.");
        userButton.addActionListener( new UserButtonListener() );
        add(userButton, BorderLayout.SOUTH);
        
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
			PixelApp.pixel.sendPixelDecodedFrame(PixelApp.decodedDir, animation_name, i, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
	    }
	};
    }    
        
        
  //  }

    public File getImageDirectory() 
    {	
	return imageDirectory;
    }
    
    @Override
    protected ImageIcon getImageIcon(String path) 
    {
	File file = new File(path);
	URL iconUrl;	
	ImageIcon icon = null;
	try 
	{
	    iconUrl = file.toURI().toURL();
	    icon = new ImageIcon(iconUrl);
	} 
	catch (MalformedURLException ex) 
	{
	    Logger.getLogger(UserProvidedPanel.class.getName()).log(Level.SEVERE, null, ex);
	}	
	
	return icon;
    }
    
    public List<File> getSingleImages() 
    {
        return singleImages;
    }

    @Override
    protected List<String> imageNames() throws Exception 
    {
        List<String> images = new ArrayList();
        File [] files = imageDirectory.listFiles(ImageFilters.stills);
	
	if(files != null)
	{
	    for(File image : files)
	    {
		String path = image.getAbsolutePath();
		images.add(path);
	    }
	}        
	
	for(File image : singleImages)
	{
	    String path = image.getAbsolutePath();
	    images.add(path);
	}
        
        return images;
    }

    @Override
    protected String imagePath() 
    {
	return imageDirectory.getAbsolutePath();
    }

    /*@Override
    public void actionPerformed(ActionEvent event) 
    {
	String imagePath = event.getActionCommand();
	System.out.println("user panel selected: " + imagePath);	
  
	//let's add a check here if the user supplied image is a PNG or a GIF, if it's a GIF then we'll decode and animate it
	
	String selectedFileName = FilenameUtils.getName(imagePath); 
	String fileType = FilenameUtils.getExtension(imagePath);
	String gifNameNoExt = FilenameUtils.removeExtension(selectedFileName); //with no extension
	
	System.out.println("User selected file name: " + selectedFileName);
	System.out.println("User selected file type: " + fileType);
	System.out.println("User selected file name no extension: " + gifNameNoExt);
	
	if (fileType.toLowerCase().contains("gif")) {
		
		 if (PixelApp.pixel.GIFNeedsDecoding(PixelApp.decodedDir, selectedFileName, PixelApp.currentResolution) == true) {
		    	PixelApp.pixel.decodeGIF(PixelApp.decodedDir, imagePath, PixelApp.currentResolution, PixelApp.KIND.width, PixelApp.KIND.height);
		    }

			    //****** Now let's setup the animation ******
			    
			    animation_name = selectedFileName;
			    
			    GIFfps = PixelApp.pixel.getDecodedfps(PixelApp.decodedDir, animation_name); //get the fps //to do fix this later becaause we are getting from internal path
			    GIFnumFrames = PixelApp.pixel.getDecodednumFrames(PixelApp.decodedDir, animation_name);
			    GIFselectedFileDelay = PixelApp.pixel.getDecodedframeDelay(PixelApp.decodedDir, animation_name);
			    GIFresolution = PixelApp.pixel.getDecodedresolution(PixelApp.decodedDir, animation_name);
			    
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
		            writeMode = false;
		            
		             if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //change this to a double click event later
		    			
		    					PixelApp.pixel.interactiveMode();
		    					PixelApp.pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
		    					System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
		    					  int y=0;
		    				    	 
		    				   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
		    				      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!

		    			    			System.out.println("Writing " + animation_name + " to PIXEL " + "frame " + y);
		    				 		    PixelApp.pixel.sendPixelDecodedFrame(PixelApp.decodedDir, animation_name, y, GIFnumFrames, GIFresolution, PixelApp.KIND.width,PixelApp.KIND.height);
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
	
	else {  //we have a static image
		
		stopExistingTimer();   //the timer could have been running if the user selected a gif first, need to kill it 
		
		try 
	        {
	        System.out.println("Attemping to load User image: " + imagePath);  //image path is something like /Users/al/Documents/pngs/aaagumball.png
		    File infile = new File(imagePath);
		    BufferedImage originalImage = ImageIO.read(infile);
		    PixelApp.pixel.writeImagetoMatrix(originalImage);
	        } 
	        catch (Exception e1) 
	        {
	            e1.printStackTrace();
	        }
		}
    }*/
    
    
    @Override
	public void mouseClicked(MouseEvent e) {
      if (PixelApp.getPixelFound() == true) { //let's make sure pixel is found before proceeding or we'll get a crash
			PixelApp.pixel.interactiveMode();
			Component command = e.getComponent(); //this was the only way I could get the path of the image clicked using the mouse listener, probably there is a better way but this works
			
			localFileImagePath = PixelApp.pixel.getSelectedFilePath(command);  //this returns file:/Users/al/Documents/pngs/somegif.gif
			selectedFileName = FilenameUtils.getName(localFileImagePath); //with no extension
			
			fileType = FilenameUtils.getExtension(selectedFileName);
			String gifNameNoExt = FilenameUtils.removeExtension(selectedFileName); //with no extension
		
	        try 
	        {
	         System.out.println("Attemping to load " + localFileImagePath);
		     // URL url = PixelApp.class.getClassLoader().getResource(imagePath); //get rid of this cuz we're loading from the local dir, not classpath
	         URL url = new URL(localFileImagePath); 
	         File file = new File(url.getFile());
	         //localFileImagePathString = url.getPath(); //we're need the path without the file: part for the decodegif method
	         localFileImagePathString = file.getAbsolutePath(); //need this for gif use case, use originalimage for png
	         
		    originalImage = ImageIO.read(url);
		    System.out.println("Selected File Local Absolute Path: " + localFileImagePathString);
		
	        } 
	        catch (Exception e1) 
	        {
	            e1.printStackTrace();
	        }
			
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
		
			if (fileType.toLowerCase().contains("gif")) {
	    		
	   		 if (PixelApp.pixel.GIFNeedsDecoding(PixelApp.decodedDir, selectedFileName, PixelApp.currentResolution) == true) {
	   			    System.out.println("This GIF needs to be encoded: " + localFileImagePathString);
	   		    	PixelApp.pixel.decodeGIF(PixelApp.decodedDir, localFileImagePathString, PixelApp.currentResolution, KIND.width, KIND.height);
	   		    }
	
	   			    //****** Now let's setup the animation ******
	   			    
	   			    animation_name = selectedFileName;
	   			    
	   			    //TO Add a check here to make sure the txt file exists
	   			    
	   			    GIFfps = PixelApp.pixel.getDecodedfps(PixelApp.decodedDir, animation_name); //get the fps //to do fix this later becaause we are getting from internal path
	   			    GIFnumFrames = PixelApp.pixel.getDecodednumFrames(PixelApp.decodedDir, animation_name);
	   			    GIFselectedFileDelay = PixelApp.pixel.getDecodedframeDelay(PixelApp.decodedDir, animation_name);
	   			    GIFresolution = PixelApp.pixel.getDecodedresolution(PixelApp.decodedDir, animation_name);
	   			    
	   			    System.out.println("Selected GIF Resolution: " + GIFresolution);
	   				System.out.println("Current LED Panel Resolution: " + PixelApp.currentResolution);
	   				System.out.println("GIF Width: " + KIND.width);
	   				System.out.println("GIF Height: " + KIND.height);
	   				
	   				stopExistingTimer();
	   				
	   			 if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {  //change this to a double click event later
		    			
						PixelApp.pixel.interactiveMode();
						PixelApp.pixel.writeMode(GIFfps); //need to tell PIXEL the frames per second to use, how fast to play the animations
						System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
						
						Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
						setCursor(hourglassCursor);
						PixelApp.frame.setEnabled(false);
						
						new writePIXEL().execute(); 
						
						/*  int y=0;
					    	 
					   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
					      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
				    			System.out.println("Writing " + animation_name + " to PIXEL " + "frame " + y);
					 		    PixelApp.pixel.sendPixelDecodedFrame(PixelApp.decodedDir, animation_name, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
					   	  } //end for loop
						PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
						System.out.println("Writing " + animation_name + " to PIXEL complete, now displaying...");*/
				
				}
					else {
						   stopExistingTimer();
						   timer = new Timer(GIFselectedFileDelay, AnimateTimer);
						   timer.start();
					} 
			   	}
			   	
			   	else {  //we have a static image
			   		
			   		stopExistingTimer();   //the timer could have been running if the user selected a gif first, need to kill it 
			   		
			   		if (PixelApp.pixelHardwareID.substring(0,4).equals("PIXL") && writeMode == true) {
			   			
			   			PixelApp.pixel.interactiveMode();
						//send loading image
						PixelApp.pixel.writeMode(10); //need to tell PIXEL the frames per second to use, how fast to play the animations
						
						try {
							PixelApp.pixel.writeImagetoMatrix(originalImage, KIND.width,KIND.height);
						} catch (ConnectionLostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						PixelApp.pixel.playLocalMode(); //now tell PIXEL to play locally
			   		}
			   		
			   		else {  //just stream the static image
					   		
					   	 try { 
								PixelApp.pixel.writeImagetoMatrix(originalImage, KIND.width,KIND.height);
							} catch (ConnectionLostException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			   		
			   		}	   
			   	}   
	 
    }
	
	 private class writePIXEL extends SwingWorker<Boolean, Integer> {
			//SwingWorker<Boolean, Integer> writePIXEL = new SwingWorker<Boolean, Integer>() { //use this if you only need once like an startup timer maybe
				   @Override
				   protected Boolean doInBackground() throws Exception {
					   
					   int y=0;
				    	 
					   	  //for (y=0;y<numFrames-1;y++) { //let's loop through and send frame to PIXEL with no delay
					      for (y=0;y<GIFnumFrames;y++) { //Al removed the -1, make sure to test that!!!!!
				    			System.out.println("Writing " + animation_name + " to PIXEL " + "frame " + y);
					 		    PixelApp.pixel.sendPixelDecodedFrame(PixelApp.decodedDir, animation_name, y, GIFnumFrames, GIFresolution, KIND.width,KIND.height);
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
				  };
				  
    
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
    
    private class UserButtonListener implements ActionListener    
    {
        public void actionPerformed(ActionEvent ae) 
        {
            int result = userDirectoryChooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File directory = userDirectoryChooser.getSelectedFile();
                if( directory == null )
                {
                    System.out.println("laters");   
                }
                else
                {
		    if( directory.isDirectory() )
		    {
			imageDirectory = directory;
		    }
		    else
		    {
			singleImages.add(directory);
		    }		    
		    
		    buttonsPanel.removeAll();
		    populate();
                }
            }            
        }        
    }    
    
}

