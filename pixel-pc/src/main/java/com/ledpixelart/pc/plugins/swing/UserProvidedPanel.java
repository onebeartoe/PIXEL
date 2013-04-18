
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
    
    public UserProvidedPanel(RgbLedMatrix.Matrix KIND, File imageDirectory)
    {
	super(KIND);
        this.imageDirectory = imageDirectory;
	
	userDirectoryChooser = new JFileChooser(this.imageDirectory);
        userDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
	JButton userButton = new JButton("Browse");
        userButton.addActionListener( new UserButtonListener() );
        add(userButton, BorderLayout.NORTH);
    }

    @Override
    protected ImageIcon getImageIcon(String path) 
    {
	String url = imagePath() + "/" + path;
	File file = new File(url);
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
        String [] files = imageDirectory.list(ImageFilters.stills);
        for(String image : files)
        {
            images.add(image);
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
        
        String framestring = imageDirectory.getAbsolutePath() + "/" + command + ".png";
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
		    imageDirectory = directory;
		    buttonsPanel.removeAll();
		    populate();
                }
            }            
        }        
    }    
    
}

