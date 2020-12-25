
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.plugins.PluginConfigEntry;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import ioio.lib.api.RgbLedMatrix;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import javax.swing.JFrame;
import org.onebeartoe.pixel.plugins.swing.PixelPanel;

/**
 * @deprecated Use the version at https://github.com/onebeartoe/java-libraries/tree/master/onebeartoe-application/src/main/java/org/onebeartoe/application
 * @author Roberto Marquez
 */
public interface PreferencesService
{
    String get(String key, String defaultValue);
    
    PixelPanel loadPlugin(String jarPath, String className, RgbLedMatrix.Matrix KIND) throws Exception;
    
    List<PixelPanel> restoreUserPluginPreferences(RgbLedMatrix.Matrix KIND, List<PluginConfigEntry> userPluginConfiguration) throws Exception;
    
    Dimension restoreWindowDimension() throws Exception;
    
    Point restoreWindowLocation() throws Exception;
    
    void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel);
    
    void saveUserPluginPreferences(List<PluginConfigEntry> userPluginConfiguration);
    
    void saveWindowPreferences(JFrame window);
}
