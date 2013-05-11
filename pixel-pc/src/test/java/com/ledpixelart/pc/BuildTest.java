
package com.ledpixelart.pc;

import com.ledpixelart.pc.filters.ImageFilters;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class BuildTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BuildTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(BuildTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
	File pwd = new File(".");		
	try 
	{
	    String resourcesPath = "src/main/resources";
	    
	    System.out.println("current working directory: " + pwd.getCanonicalPath() );    	    
	    
	    String imagesPath = resourcesPath + "/images";
	    File imagesDirectory = new File(imagesPath);	    
	    System.out.println("images working directory: " + imagesDirectory.getAbsolutePath() );
	    	    
	    String [] imageNames = imagesDirectory.list(ImageFilters.stills);
	    
	    List<String> nameList = Arrays.asList(imageNames);
	    Collections.sort(nameList);
	    
	    System.out.print("adding images: " + imagesDirectory.getAbsolutePath() );
	    StringBuilder text = new StringBuilder();
	    String newLine = System.getProperty("line.separator");
	    for(String name : imageNames)
	    {
		text.append(name);
		text.append(newLine);
		System.out.print(name + " ");
	    }
	    System.out.println();
	    
	    // write the names to a text file
	    Charset charset = Charset.forName("UTF-8");
	    File outfile = new File(resourcesPath + "/" + "images.text");
	    System.out.println("outputing to: " + outfile.getAbsolutePath() );
	    Path outpath = outfile.toPath();
	    BufferedWriter writer = Files.newBufferedWriter(outpath, charset, StandardOpenOption.TRUNCATE_EXISTING);
	    writer.write( text.toString() );
	    writer.close();
	} 
	catch (IOException ex) 
	{
	    Logger.getLogger(BuildTest.class.getName()).log(Level.SEVERE, null, ex);
	}
		
        assertTrue( true );
    }
}
