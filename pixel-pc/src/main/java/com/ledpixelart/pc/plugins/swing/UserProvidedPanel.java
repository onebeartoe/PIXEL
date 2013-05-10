
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.filters.ImageFilters;
import ioio.lib.api.RgbLedMatrix;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 * @author rmarquez
 */
public class UserProvidedPanel extends ImageTilePanel
{
    
    private JFileChooser userDirectoryChooser;
	
    private File imageDirectory;
    
    private List<File> singleImages;
    
    public UserProvidedPanel(RgbLedMatrix.Matrix KIND, File imageDirectory)
    {
	super(KIND);
        this.imageDirectory = imageDirectory;
	
	userDirectoryChooser = new JFileChooser(this.imageDirectory);	
	userDirectoryChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	
	singleImages = new ArrayList();
		
	JButton userButton = new JButton("Browse for a single image or a folder of images.");
        userButton.addActionListener( new UserButtonListener() );
        add(userButton, BorderLayout.SOUTH);
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

    @Override
    protected List<String> imageNames() throws Exception 
    {
        List<String> images = new ArrayList();
        File [] files = imageDirectory.listFiles(ImageFilters.stills);
        for(File image : files)
        {
	    String path = image.getAbsolutePath();
            images.add(path);
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

    @Override
    public void actionPerformed(ActionEvent event) 
    {
	String command = event.getActionCommand();
	System.out.println("image comamand: " + command);	
  
	String framestring = command;
        try 
        {
            System.out.println("Attemping to load User image: " + framestring);
	    File infile = new File(framestring);	    
	    BufferedImage originalImage = ImageIO.read(infile);
	    PixelApp.pixel.writeImagetoMatrix(originalImage);
        } 
        catch (Exception e1) 
        {
            e1.printStackTrace();
        }
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

