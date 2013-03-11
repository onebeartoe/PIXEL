
package com.ledpixelart.pc;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * @author rmarquez
 */
public class UserProvidedPanel extends PixelTilePanel
{
    File imageDirectory;
    
    public UserProvidedPanel(File imageDirectory)
    {
        this.imageDirectory = imageDirectory;
    }

    @Override
    protected ImageIcon getImageIcon(String path) 
    {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                if(toLower.endsWith(".gif") || toLower.endsWith(".jpg"))
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
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void actionPerformed(ActionEvent e) 
    {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
