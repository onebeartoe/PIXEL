
package org.onebeartoe.pixel.plugins;

//import com.ledpixelart.pc.plugins.swing.PixelPanel;
import java.net.URL;
import java.util.List;
import org.onebeartoe.pixel.plugins.swing.PixelPanel;

/**
 * @author rmarquez
 */
public interface PluginService 
{
    List<PixelPanel> load(URL url);
}
