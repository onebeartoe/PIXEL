
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
    
    void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel);
    
    Dimension restoreWindowDimension() throws Exception;
    
    Point restoreWindowLocation();
    
    void saveWindowPreferences(JFrame window);
}
