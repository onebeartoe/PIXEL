
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;
import javax.imageio.ImageIO;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.PixelLogFormatter;
import org.apache.commons.io.FilenameUtils;
import org.onebeartoe.web.enabled.pixel.CliPixel;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * @author Roberto Marquez
 */
public class PinDMDHttpHandler extends ImageResourceHttpHandler
{
    public PinDMDHttpHandler(WebEnabledPixel application)
    {
        super(application);
        
        //basePath = "arcade/";
        basePath = "";
        defaultImageClassPath = "pacman.png"; //to do change this
        modeName = "pindmd";
    }
    
   
    
    @Override
    protected void writeImageResource(String urlParams) throws IOException, ConnectionLostException
    {
         
        //this could receive one frame at a time and just got called each time a frame is sent
        //or it could receive a stream and then writing of frames would need to happen against that stream
        
        Pixel pixel = application.getPixel();
        
      
       
        LogMe logMe = null;
        
         
        int loop_ = 0;
        String text_ = "";
        int scrollsmooth_ = 1;
        Long speeddelay_ = 10L;
        String color_ = "";
        Color color = Color.RED; //default to red color if not added
       // String loopString = "0"; //to do kill this
       
        
        String urlPath = "http://localhost:8080/" + urlParams;
        System.out.println("Pin DMD received URL: " + urlPath);
     
        URL url = new URL(urlPath);
        
        //https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/websocketx/client
        
        
       // URL url = new URL("http://www.objects.com.au/services/sherpa.html");

       /* 
       InputStream in = new BufferedInputStream(url.openStream());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];

        int n = 0;

        while (-1 != (n = in.read(buf))) {

            out.write(buf, 0, n);

        }

        out.close();

        in.close();

        byte[] response = out.toByteArray();
       
        
        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
          is = url.openStream ();
          byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
          int n;

          while ( (n = is.read(byteChunk)) > 0 ) {
            baos.write(byteChunk, 0, n);
          }
        }
        catch (IOException e) {
          System.out.println ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
          e.printStackTrace ();
          // Perform any other exception handling that's appropriate.
        }
        finally {
          if (is != null) { is.close(); }
        }
        */

        
    
            
            //let's now refer to our mapping table for the console names, because console names are different for RetroPie vs. HyperSpin and other front ends
            //to do add a user defined .txt mapping if the console is not found in our mapping table
            
          

            
           
    
 }
     
    
}
