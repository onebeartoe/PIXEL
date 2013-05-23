
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 * @author rmarquez
 */
public class JavaPreferencesService implements PreferencesService
{
    
    private Preferences preferences;
    
    public JavaPreferencesService()
    {
	preferences = Preferences.userNodeForPackage(PixelApp.class);
    }

    public String get(String key, String defaultValue) 
    {	
	String value = preferences.get(key, defaultValue);
	
	return value;
    }
    
    @Override
    public void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel)
    {
	try 
        {
            // local user images tab
            String key = PixelPcPreferences.userImagesDirectory;
            File directory = localImagesPanel.getImageDirectory();
            String path = directory.getAbsolutePath();
            preferences.put(key, path);
            List<File> singleImages = localImagesPanel.getSingleImages();
            int i = 0;
            for(File image : singleImages)
            {
                key = PixelPcPreferences.singleImage + i;
                path = image.getAbsolutePath();
                preferences.put(key, path);
                i++;                
            }

            preferences.sync();
        } 
        catch (BackingStoreException ex) 
        {
            String message = "The app preferences could not be saved.";
            Logger.getLogger(PixelApp.class.getName()).log(Level.SEVERE, message, ex);
        }
    }

    @Override
    public void saveWindowPreferences(JFrame window)
    {
	
    }
    
}
