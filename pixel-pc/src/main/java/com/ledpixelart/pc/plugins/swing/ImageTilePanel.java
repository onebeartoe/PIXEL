
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import ioio.lib.api.RgbLedMatrix;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author rmarquez
 */
public class ImageTilePanel extends PixelTilePanel
{
       
    protected String imageListPath = "/images.text";
    
    public ImageTilePanel(RgbLedMatrix.Matrix KIND)
    {
	super(KIND);	
    }
    
    @Override
    public void actionPerformed(ActionEvent event) 
    {	
	String command = event.getActionCommand();
	System.out.println("image comamand: " + command);	
        
        String imagePath = "images/" + command + ".png";
        try 
        {
            System.out.println("Attemping to load " + imagePath + " from the classpath.");
	    URL url = PixelApp.class.getClassLoader().getResource(imagePath);

	    BufferedImage originalImage = ImageIO.read(url);
            PixelApp.pixel.writeImagetoMatrix(originalImage);
        } 
        catch (Exception e1) 
        {
            e1.printStackTrace();
        }
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
