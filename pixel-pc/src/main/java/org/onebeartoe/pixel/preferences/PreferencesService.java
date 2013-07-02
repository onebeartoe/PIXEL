
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.plugins.PluginConfigEntry;
import com.ledpixelart.pc.plugins.swing.PixelPanel;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import ioio.lib.api.RgbLedMatrix;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import javax.swing.JFrame;

/**
 * @author rmarquez
 */
public interface PreferencesService
{
    String get(String key, String defaultValue);
    
    PixelPanel loadPlugin(String jarPath, String className, RgbLedMatrix.Matrix KIND) throws Exception;
    
    List<PixelPanel> restoreUserPluginPreferences(RgbLedMatrix.Matrix KIND) throws Exception;
    
    Dimension restoreWindowDimension() throws Exception;
    
    Point restoreWindowLocation() throws Exception;
    
    void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel);
    
    void saveUserPluginPreferences(List<PluginConfigEntry> userPluginConfiguration);
    
    void saveWindowPreferences(JFrame window);
}
