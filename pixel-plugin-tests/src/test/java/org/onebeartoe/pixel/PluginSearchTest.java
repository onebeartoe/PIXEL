
package org.onebeartoe.pixel;

import com.ledpixelart.pc.plugins.swing.PixelPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.clapper.util.classutil.AbstractClassFilter;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.InterfaceOnlyClassFilter;
import org.clapper.util.classutil.NotClassFilter;
import org.clapper.util.classutil.SubclassClassFilter;

/**
 * Unit test for simple App.
 */
public class PluginSearchTest extends TestCase
{
    private final String weatherJarPath = "../pixel-weather/target/pixel-weather-1.0-SNAPSHOT.jar";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PluginSearchTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PluginSearchTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testPixelPcJar()
    {
	System.out.println("\n\n\nTesting pixel-pc JAR:");
	
	String jarPath = "../pixel-pc/target/pixel-pc-0.6.jar";
	boolean useFilter = true;
	
	int count = searchForPlugins(jarPath, useFilter);
	
	boolean foundPlugins = count > 0;
	
        assertTrue(foundPlugins);
    }
    
    public void testPixelWeatherJar()
    {
	System.out.println("\n\n\nTesting pixel-weather JAR:");
		
	boolean useFilter = true;
	
	int count = searchForPlugins(weatherJarPath, useFilter);
	
	boolean foundPlugins = count > 0;
	
        assertTrue(foundPlugins);
    }
    
    public void testPixelWeatherJarNoFilter()
    {
	System.out.println("\n\n\nTesting pixel-weather JAR, with no filter:");
		
	boolean useFilter = false;
	
	int count = searchForPlugins(weatherJarPath, useFilter);
	System.out.println("\n\n");
	
	boolean foundPlugins = count > 0;
	
        assertTrue(foundPlugins);
    }    
    
    private int searchForPlugins(String jarPath, boolean useFilter)
    {
        ClassFinder finder = new ClassFinder();
	
	int pluginCount = 0;
	
        File jar = new File(jarPath);
	if( !jar.exists() || !jar.canRead() )
	{
	    System.out.println("The jar exists: " + jar.exists() + "\nThe jar is readable: " + jar.canRead() );
	}
	else
	{
	    finder.add(jar);
	    
	    ClassFilter filter =
		new AndClassFilter(
		    // Must not be an interface
                    new NotClassFilter ( new InterfaceOnlyClassFilter() ),

		    // Must implement the PixelPanel interface

		    new SubclassClassFilter(PixelPanel.class),

		    // Must not be abstract
                    new NotClassFilter( new AbstractClassFilter() )
		    );

	    Collection<ClassInfo> foundClasses = new ArrayList<ClassInfo>();
	    
	    if(useFilter)
	    {
		pluginCount = finder.findClasses(foundClasses, filter);
	    }
	    else
	    {
		pluginCount = finder.findClasses(foundClasses);
	    }

	    if( foundClasses.isEmpty() )
	    {
		System.out.println("No plugins were found.");
	    }
	    else
	    {
		for (ClassInfo classInfo : foundClasses)	    
		{
		    System.out.println ("Found " + classInfo.getClassName());
		}
	    }
	}
	
	return pluginCount;
    }
}
