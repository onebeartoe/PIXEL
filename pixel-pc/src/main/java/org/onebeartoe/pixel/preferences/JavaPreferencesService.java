
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import java.awt.Dimension;
import java.awt.Point;
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
    
    public void restoreUserPluginPreferences() throws Exception 
    {
	return;
    }
	
    public Dimension restoreWindowDimension() 
    {	
	int defaultValue = 450;
	String key = PixelPreferencesKeys.windowWidth;
	int width = preferences.getInt(key, defaultValue);
	
	key = PixelPreferencesKeys.windowHeight;
	int height = preferences.getInt(key, PixelApp.DEFAULT_HEIGHT);
	
	Dimension demension = new Dimension(width, height);
	
	return demension;
    }
    
    @Override
    public Point restoreWindowLocation() throws Exception
    {
	int errorValue = -1;
	String key = PixelPreferencesKeys.windowX;
	int x = preferences.getInt(key, errorValue);

	key = PixelPreferencesKeys.windowY;
	int y = preferences.getInt(key, errorValue);
	
	if(x == errorValue || y == errorValue)
	{
	    // The window location hasn't been saved, yet.
	    
	    throw new Exception();
	}
	
	Point point = new Point(x,y);
	
	return point;
    }
    
    @Override
    public void saveBuiltInPluginsPreferences(UserProvidedPanel localImagesPanel)
    {
	try 
        {
            // local user images tab
            String key = PixelPreferencesKeys.userImagesDirectory;
            File directory = localImagesPanel.getImageDirectory();
            String path = directory.getAbsolutePath();
            preferences.put(key, path);
            List<File> singleImages = localImagesPanel.getSingleImages();
            int i = 0;
            for(File image : singleImages)
            {
                key = PixelPreferencesKeys.singleImage + i;
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

    public void saveUserPluginPreferences() 
    {
	return;
    }
    
    @Override
    public void saveWindowPreferences(JFrame window)
    {
	int x = window.getX();
	String key = PixelPreferencesKeys.windowX;
	preferences.putInt(key, x);
	
	int y = window.getY();
	key = PixelPreferencesKeys.windowY;
	preferences.putInt(key, y);
	
	int width = window.getWidth();	
	key = PixelPreferencesKeys.windowWidth;
	preferences.putInt(key, width);
	
	int height = window.getHeight();
	key = PixelPreferencesKeys.windowHeight;
	preferences.putInt(key, height);
    }


    
}
