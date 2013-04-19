
package com.ledpixelart.pc.filters;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rmarquez
 */
public class ImageFilters 
{
    
    static final String [] stillEndings = {".gif", ".jpeg", ".jpg", ".png"};
    
    public static ImageFilter stills = new ImageFilter(stillEndings);
    
    static final String [] animationEndings = {".gif"};
        
    static ImageFilter animations = new ImageFilter(animationEndings);     
    
    public static class ImageFilter implements FilenameFilter
    {
	
	List<String> endings;
		
	public ImageFilter(String [] endings)
	{
	    this.endings = new ArrayList();
	    for(String ending : endings)
	    {
		this.endings.add(ending);
	    }	    
	}
	
	public boolean accept(File directory, String string) 
	{
	    boolean accecpted = false;
	    String toLower = string.toLowerCase();
	    for(String ending : endings)
	    {
		if( toLower.endsWith(ending) )
		{
		    accecpted = true;
		    break;
		}
	    }	    

	    return accecpted;
	}
	
    }
    
}
