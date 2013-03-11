
package com.ledpixelart.pc;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * @author rmarquez
 */
public class UserProvidedPanel extends PixelTilePanel
{
    private File imageDirectory;
    
    public UserProvidedPanel(File imageDirectory)
    {
        this.imageDirectory = imageDirectory;
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
        FilenameFilter filter = new FilenameFilter() 
        {
            public boolean accept(File directory, String string) 
            {
                boolean accecpted = false;
                String toLower = string.toLowerCase();
                if(toLower.endsWith(".gif") || toLower.endsWith(".jpg") || toLower.endsWith(".png"))
                {
                    accecpted = true;
                }
                
                return accecpted;
            }
        };
            
        List<String> images = new ArrayList();
        String [] files = imageDirectory.list(filter);
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
    public void actionPerformed(ActionEvent e) 
    {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
