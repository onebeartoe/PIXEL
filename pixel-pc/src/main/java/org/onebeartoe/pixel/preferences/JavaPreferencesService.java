
package org.onebeartoe.pixel.preferences;

import com.ledpixelart.pc.PixelApp;
import com.ledpixelart.pc.plugins.PluginConfigEntry;
import com.ledpixelart.pc.plugins.swing.UserProvidedPanel;
import ioio.lib.api.RgbLedMatrix;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import org.onebeartoe.pixel.plugins.swing.PixelPanel;

/**
 * @deprecated Use the version at https://github.com/onebeartoe/java-libraries/tree/master/onebeartoe-application/src/main/java/org/onebeartoe/application
 * @author Roberto Marquez
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
    
    /**
AS AN ALTERNATIVE TO THE USER HAVING TO SUPPLY THE CLASS NAME,
TRY THIS CLASS: https://commons.apache.org/proper/commons-discovery/apidocs/index.html?org/apache/commons/discovery/tools/DiscoverSingleton.html
     * @param jarPath
     * @param className
     * @param KIND
     * @return
     * @throws Exception 
     */
    @Override
    public PixelPanel loadPlugin(String jarPath, String className, RgbLedMatrix.Matrix KIND) throws Exception
    {	
        File jar = new File(jarPath);
	if( !jar.exists() || !jar.canRead() )
	{
	    System.out.println("\n\nThere is a problem with the specified JAR.");
	    System.out.println("The jar exists: " + jar.exists() );
	    System.out.println("The jar is readable: " + jar.canRead() );
	}
	
	
	URL url = jar.toURI().toURL();
	URL [] urls = new URL[1];
	urls[0] = url;
	URLClassLoader classLoader = new URLClassLoader(urls);

	Class<?> clazz = classLoader.loadClass(className);

	Constructor<?> constructor = clazz.getConstructor(RgbLedMatrix.Matrix.class);
	Object o = constructor.newInstance(KIND);
	PixelPanel plugin = (PixelPanel) o;
	    
	return plugin;
    }
    
    @Override
    public List<PixelPanel> restoreUserPluginPreferences(RgbLedMatrix.Matrix KIND, List<PluginConfigEntry> userPluginConfiguration) throws Exception 
    {
        List<PixelPanel> plugins = new ArrayList();
        
        int count = preferences.getInt(PixelPreferencesKeys.userPluginCount, 0);
        
        for(int i=0; i<count; i++)
        {
            String key = PixelPreferencesKeys.userPlugin + i;
            String s = preferences.get(key, null);
            if(s == null)
            {
                System.out.println("No class names for " + key);
            }
            else
            {
                String [] strs = s.split(PluginConfigEntry.JAR_CLASS_SEPARATOR);
                if(strs.length > 1)
                {
                    String jarPath = strs[0];
                    String classes = strs[1];
                    String [] classNames = classes.split(PluginConfigEntry.JAR_CLASS_SEPARATOR);
                    for(String name : classNames)
                    {
                        PluginConfigEntry entry = new PluginConfigEntry();
                        entry.jarPath = jarPath;
                        entry.qualifiedClassName = name;
                        userPluginConfiguration.add(entry);

                        PixelPanel plugin = loadPlugin(jarPath, name, KIND);
                        plugins.add(plugin);
                    }
                }
            }
        }
        
	return plugins;
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

    public void saveUserPluginPreferences(List<PluginConfigEntry> userPluginConfigurations)
    {
        Map<String, String> jarsToClasses = new HashMap();

	for(PluginConfigEntry entry : userPluginConfigurations)
        {
            String classes = jarsToClasses.get(entry.jarPath);
            
            if(classes == null)
            {
                classes = entry.qualifiedClassName;
            }
            else
            {
                classes += PluginConfigEntry.CLASS_SEPARATOR + entry.qualifiedClassName;
            }
            
            jarsToClasses.put(entry.jarPath, classes);
        }                

        int i = 0;
        Set<String> keys = jarsToClasses.keySet();
        for(String jarKey : keys)            
        {
            String classes = jarsToClasses.get(jarKey);
            String entry = jarKey + PluginConfigEntry.JAR_CLASS_SEPARATOR + classes;
            String key = PixelPreferencesKeys.userPlugin + i;
            preferences.put(key, entry);
            
            i++;
        }
        
        int count = jarsToClasses.size();        
        preferences.putInt(PixelPreferencesKeys.userPluginCount, count);
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
