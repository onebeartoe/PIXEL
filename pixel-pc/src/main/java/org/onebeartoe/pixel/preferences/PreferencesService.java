
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;

/**
 * @author rmarquez
 */
public interface PreferencesService
{
    String get(String key, String defaultValue);
    
    void restoreUserPluginPreferences() throws Exception;
    
    Dimension restoreWindowDimension() throws Exception;
    
    Point restoreWindowLocation() throws Exception;
    
    void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel);
    
    void saveUserPluginPreferences();
    
    void saveWindowPreferences(JFrame window);
}
