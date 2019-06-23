
package org.onebeartoe.front.end.pixelcade;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import static javafx.css.StyleOrigin.USER_AGENT;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FilenameUtils;

import org.apache.commons.io.IOUtils;
//import org.ini4j.Ini;


/**
 * @author Al Linke
 */
public class PixelcadeFrontEnd
{
    protected Logger logger;

    private CliPixel cli;
    
    private static String consoleName_ = null;
    
    private static String gameName_ = null;
    
    private static String mode_ = null;
    
    private static String BaseGameName_ = "";
    
    private static HttpURLConnection con;
    
    public static void main(String[] args) throws MalformedURLException, IOException
    {
        
        PixelcadeFrontEnd app = new PixelcadeFrontEnd(args);
        
        
        if ( mode_!=null && consoleName_ !=null && BaseGameName_ != null) {  //let's make sure we have valid command lines before proceeding
            
             
            BaseGameName_ = FilenameUtils.getBaseName(gameName_); //stripping out the extension
        
            System.out.println("Console Name: " + consoleName_.toLowerCase());
            System.out.println("Original Game Name: " + gameName_.toLowerCase());
            System.out.println("Base Game Name (no extension): " + BaseGameName_.toLowerCase());
            System.out.println("Mode: " + mode_.toLowerCase());
            
            if (!mode_.equals("stream") && !mode_.equals("write")) {   
                 System.out.println("stream or write for mode was not entered so defaulting to stream mode");
                 mode_ = "stream";
             }
             
            String URLString = "http://localhost:8080/arcade/" + mode_.toLowerCase() + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
            
            //replace the spaces with %20 as the URL call will fail without this
            URLString=URLString.replaceAll(" ","%20");
            System.out.println("REST Call URL: " + URLString);
                    
            String urlParameters = "";
            //String urlParameters = "name=Jack&occupation=programmer";
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
             
            try {

                    URL myurl = new URL(URLString);
                    //myurl = URLEncoder.encode(myurl,"UTF-8");
                    con = (HttpURLConnection) myurl.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "Java client");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                        wr.write(postData);
                    }

                    StringBuilder content;

                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {

                        String line;
                        content = new StringBuilder();

                        while ((line = in.readLine()) != null) {
                            content.append(line);
                            content.append(System.lineSeparator());
                        }
                    }

                    System.out.println(content.toString());

                } finally {

                    con.disconnect();
                } 
             
        }
        else {
            
            System.out.println("INCORRECT PARAMETERS");
            System.out.println(CliPixel.getInstructions());
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
}
