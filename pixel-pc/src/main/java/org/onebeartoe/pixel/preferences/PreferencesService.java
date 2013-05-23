
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import javax.swing.JFrame;

/**
 * @author rmarquez
 */
public interface PreferencesService
{
    void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel);
    
    void saveWindowPreferences(JFrame window);
    
    String get(String key, String defaultValue);
}
