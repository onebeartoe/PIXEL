
package org.onebeartoe.front.end.pixelcade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import org.apache.commons.io.FilenameUtils;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;

import org.onebeartoe.io.TextFileReader;
import org.onebeartoe.io.buffered.BufferedTextFileReader;

/**
 * @author Roberto Marquez
 */
public class PixelcadeFrontEnd
{
    protected Logger logger;

    private CliPixel cli;
    
    private static String consoleName_ = null;
    
    private static String gameName_ = null;
    
    private static String mode_ = null;
    
    private static String BaseGameName_ = null;
    
    public static String OS = System.getProperty("os.name").toLowerCase();
    
    public static void main(String[] args) throws MalformedURLException, IOException
    {
        
        PixelcadeFrontEnd app = new PixelcadeFrontEnd(args);
        //app.startServer();
        //System.out.println("hello there");
        
        BaseGameName_ = FilenameUtils.getBaseName(gameName_); //stripping out the extension
        
        System.out.println("Console Name: " + consoleName_.toLowerCase());
        System.out.println("Original Game Name: " + gameName_.toLowerCase());
        System.out.println("Base Game Name (no extension): " + BaseGameName_.toLowerCase());
        System.out.println("Mode: " + mode_.toLowerCase());
        
        if ( mode_!=null && consoleName_ !=null && BaseGameName_ != null) {  //let's make sure we have valid command lines before proceeding
            
             if (!mode_.equals("stream") || !mode_.equals("write")) {
                 System.out.println("stream or write for mode was not entered so defaulting to stream mode");
                 mode_ = "stream";
             } 
                
             String URLString = "http://localhost:8080/arcade/" + mode_.toLowerCase() + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
             System.out.println(URLString);
            //new URL("http://localhost:8080/arcade/" + mode_.toLowerCase() + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase()).openConnection();
            /*
             try {
	        	HttpGet httpget = new HttpGet("https://" + snowDomain + "/api/now/stats/incident?sysparm_query=" + getSnowBaseQuery() + "assignment_group%" + entry.getValue().toString() + "%5E" + getSnowSLAExceededQuery() + "&sysparm_count=true&sysparm_avg_fields=priority&sysparm_group_by=assignment_group&sysparm_display_value=true");
	        	httpget.setHeader("Accept", "application/xml");
	            System.out.println("Executing request " + httpget.getRequestLine());
	            CloseableHttpResponse snowXML = httpclient.execute(httpget);
	            try {
	                System.out.println("----------------------------------------");
	                System.out.println(snowXML.getStatusLine());
	                responseBody = EntityUtils.toString(snowXML.getEntity());
	                System.out.println(responseBody);
	            } finally {
	            	snowXML.close();
	            }
	        } finally {
	            httpclient.close();
	        } 
        } */
        }
    }
    
     public PixelcadeFrontEnd(String[] args)
    {
        cli = new CliPixel(args);
        cli.parse();

        String name = getClass().getName();
        logger = Logger.getLogger(name);
        consoleName_ = cli.getConsoleName();
        gameName_ = cli.getGameName();
        mode_ = cli.getMode();
    
    }
     
     public class Handler extends URLStreamHandler {
    /** The classloader to find resources from. */
    private final ClassLoader classLoader;

    public Handler() {
        this.classLoader = getClass().getClassLoader();
    }

    public Handler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final URL resourceUrl = classLoader.getResource(u.getPath());
        return resourceUrl.openConnection();
    }
    
    }
}
