
package org.onebeartoe.pixel.plugins;

import com.ledpixelart.pc.plugins.swing.PixelPanel;
import java.net.URL;
import java.util.List;

/**
 * @author rmarquez
 */
public interface PluginService 
{
    List<PixelPanel> load(URL url);
}
