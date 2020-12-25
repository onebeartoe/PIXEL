
package org.onebeartoe.pixel.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.pixel.plugins.PixelPlugin;

/**
 * @author Administrator
 */
public abstract class PixelPanel extends JPanel implements PixelPlugin 
{
    public Pixel pixel;
    
    protected RgbLedMatrix matrix_;
    
    protected RgbLedMatrix.Matrix KIND;    
    
    public PixelPanel(RgbLedMatrix.Matrix KIND)
    {
        this.KIND = KIND;
    }
    
    public ImageIcon getTabIcon()
    {
	System.out.println("\n\n\nusing the default tab\n");
	
	String path = "/tab_icons/apple_small.png";
	URL url = getClass().getResource(path);
        ImageIcon imagesTabIcon = new ImageIcon(url);
	
	return imagesTabIcon;
    }
    
    public String getTabTitle()
    {
	return "Default Plugin Title";
    }
    
}
