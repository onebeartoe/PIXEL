
package com.ledpixelart.pc;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * @author rmarquez
 */
public class ImageTilePanel extends PixelTilePanel
{
   
    protected String imageListPath = "/images.text";
	    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
	String command = e.getActionCommand();
	System.out.println("image comamand: " + command);
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
