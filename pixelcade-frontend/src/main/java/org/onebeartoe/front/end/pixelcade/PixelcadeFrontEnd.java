
package org.onebeartoe.front.end.pixelcade;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;


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
    
    public static String OS = System.getProperty("os.name").toLowerCase();
    
    private static String errorMsg = "";
    
    private static String exePath = "";
    
    public static void main(String[] args) throws MalformedURLException, IOException
    {
        
        PixelcadeFrontEnd app = new PixelcadeFrontEnd(args);
        
        
        if ( mode_!=null && consoleName_ !=null && BaseGameName_ != null) {  //let's make sure we have valid command lines before proceeding
            
             
            BaseGameName_ = FilenameUtils.getBaseName(gameName_); //stripping out the extension
        
            System.out.println("Console Name: " + consoleName_.toLowerCase());
            System.out.println("Original Game Name: " + gameName_.toLowerCase());
            System.out.println("Base Game Name (no extension): " + BaseGameName_.toLowerCase());
            System.out.println("Mode: " + mode_.toLowerCase());

            if (isWindows()) {
                errorMsg = "Please launch the Pixelcade listener first : \n"
                        + "\n"
                        + System.getProperty("user.dir") + "\\pixelweb.exe";
                
                exePath = System.getProperty("user.dir") + "\\pixelweb.exe";
                
            } else {
                errorMsg = "Please launch the Pixelcade listener first using this command : \n"
                        + "\n"
                        + "cd " + System.getProperty("user.dir") + "\n"
                        + "java -jar pixelweb.jar";
                
                exePath = "cd " + System.getProperty("user.dir") + " && java -jar pixelweb.jar"; //this didn't work, looks like concantenated doesn't work here
            }
            
            if (!mode_.equals("stream") && !mode_.equals("write")) {   
                 System.out.println("stream or write for mode was not entered so defaulting to stream mode");
                 mode_ = "stream";
             }
             
            String URLString = "http://localhost:8080/arcade/" + mode_.toLowerCase() + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
            //TO DO we could add localhost to settings.ini in case somoene wants to enable their system remote via an IP address or domain name instead
            
            
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
                } catch (ConnectException exception) {
                    
                    exceptionHandler();
                    
                } catch (Throwable throwable) {
                    exceptionHandler();
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
     
     private static void exceptionHandler() {
        System.out.println("*** ERROR ***");
        System.out.println("The Pixelcade Listener is not running");
        System.out.println(errorMsg);

        JFrame frame = new JFrame("JOptionPane showMessageDialog example");  //let's show a pop up too so the user doesn't miss it
        JOptionPane.showMessageDialog(frame,
                errorMsg,
                "Pixelcade Listener",
                JOptionPane.ERROR_MESSAGE);
       
        //TO DO get the auto-launch going if pixelweb not running, code below didn't work on windows
        /* try {
            Process process = new ProcessBuilder(exePath).start();
        } catch (IOException ex) {
            Logger.getLogger(PixelcadeFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        } */
        
        System.exit(1);                       //we can't continue because pixelweb is not running
        
        //TO DO possibly could auto-launch the listener using shell cmd from Java, set a timer delay, and then re-run
     }
     
     public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}
     
     
}
