
package org.onebeartoe.front.end.pixelcade;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

//import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;
import org.onebeartoe.pixel.LogMePixelcade;
import org.onebeartoe.pixel.hardware.Pixel;


/**
 * @author Al Linke
 */
public class PixelcadeFrontEnd
{
    //protected Logger logger;

    private CliPixel cli;
    
    
    private static String mode_ = "";
      
    private static String consoleName_ = "";
    
    private static String gameName_ = "";
    
    private static String text_ = "";
    
    private static String color_ = "";
    
    private static String speed_ = "";
    
    private static String scrollsmooth_ = "";
    
    private static String eventID_ = "";
    
    private static String loop_ = "";
    
    private static Boolean quit_ = false;
    
    private static Boolean silent_ = false;
    
    private static Boolean gameTitle_ = false;
    
    private static String BaseGameName_ = "";
    
    private static HttpURLConnection con;
    
    public static String OS = System.getProperty("os.name").toLowerCase();
    
    private static String errorMsg = "";
    
    private static String exePath = "";
    
    private static String pixelwebLaunchPath = "";
    
    private static String pixelwebWorkingPath = "";
    
    private static File exeWorkingPath;
    
    public static LogMePixelcade logMePixelcade = null;
    
    private static String URLString = "";
    
    public PixelcadeFrontEnd(String[] args)
        {
            cli = new CliPixel(args);
            cli.parse();

            String name = getClass().getName();
            consoleName_ = cli.getConsoleName();
            gameName_ = cli.getGameName();
            mode_ = cli.getMode();
            quit_ = cli.getQuit();
            silent_ = cli.getSilentMode();
            eventID_ = cli.getGEventID();
            text_ = cli.getText();
            color_ = cli.getColor();
            speed_ = cli.getSpeed();
            scrollsmooth_ = cli.getScrollSmooth();
            loop_ = cli.getLoop();
            gameTitle_ = cli.getGameTitleMode();
        }
    
    public static void main(String[] args) throws MalformedURLException, IOException
    {
        
        logMePixelcade = LogMePixelcade.getInstance();
        
        PixelcadeFrontEnd app = new PixelcadeFrontEnd(args);
        
        if (isWindows()) {
                /*errorMsg = "Pixelcade Listener (pixelweb) is not running: \n"
                        + "We'll attempt to launch it now but it's better going forward\n"
                        + "that you add the listender to your Windnows startup\n" 
                        + System.getProperty("user.dir") + "\\pixelweb.exe"; */
                
                 errorMsg = "Pixelcade Listener (pixelweb) is not running \n"
                        + "Please launch the Pixelcade Listener first\n"
                        + "You may also want to add the listener to your Windows startup\n" 
                        + System.getProperty("user.dir") + "\\pixelweb.exe";

                exePath = System.getProperty("user.dir") + "\\pixelweb.exe";
                pixelwebLaunchPath = System.getProperty("user.dir") + "\\pixelweb.exe";
                exeWorkingPath = new File(System.getProperty("user.dir")); 
                pixelwebWorkingPath = System.getProperty("user.dir");

        } else {
                exePath = "cd " + System.getProperty("user.home") + " && java -jar pixelweb.jar"; //this didn't work, looks like concantenated doesn't work here
                pixelwebLaunchPath = "java -jar " + System.getProperty("user.home") + "/pixelcade/" + "pixelweb.jar";  //java -jar /Users/al/pixelcade/pixelweb.jar
                exeWorkingPath = new File(System.getProperty("user.home") + "/pixelcade/"); 
                pixelwebWorkingPath = System.getProperty("user.home") + "/pixelcade/";

                errorMsg = "Please launch the Pixelcade listener first using this command : \n"
                        + pixelwebLaunchPath  ;
        }
        
        if (quit_) {
            
            System.out.println("Sending shutdown command to Pixelcade Listener");
            String quitURLString = "http://localhost:8080/quit/";
            makeRESTCall(quitURLString);
        }
            
        else if (!eventID_.equals("")) {                            //if an event id is there, then it means we have a call from EDS/HyperSpin and we're not going to have all the parameters
                
             BaseGameName_ = FilenameUtils.getBaseName(gameName_); //stripping out the extension
             
             
              if (!silent_) {
                        System.out.println("Event ID: " + eventID_.toLowerCase());
                        logMePixelcade.pLogger.info("Event ID: " + eventID_.toLowerCase());
                        System.out.println("Mode: " + mode_.toLowerCase());
                        logMePixelcade.pLogger.info("Mode: " + mode_.toLowerCase());
                        System.out.println("Console Name: " + consoleName_.toLowerCase());
                        logMePixelcade.pLogger.info("Console Name: " + consoleName_.toLowerCase());
                        //System.out.println("Original Game Name: " + gameName_.toLowerCase());
                        System.out.println("Game Name: " + BaseGameName_.toLowerCase());
                        logMePixelcade.pLogger.info("Game Name: " + BaseGameName_.toLowerCase());
              }
             
            switch(eventID_) 
            { 
                case "1": //front end started so let's write the default marquee
                    URLString = "http://localhost:8080/arcade/" + "write" + "/" + "marquee" + "/" + "dummy";
                    makeRESTCall(URLString);
                    break; 
                case "2": //front end exited so let's either blank screen or write default marquee
                    URLString = "http://localhost:8080/arcade/" + "write" + "/" + "marquee" + "/" + "dummy";
                    makeRESTCall(URLString);
                    break; 
                case "3": //game was launched so let's stream it
                    URLString = "http://localhost:8080/arcade/" + "sream" + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
                    makeRESTCall(URLString);
                    break;  
                case "5": //screen saver was started so let's play a fun gif animation called screen-saver.gif
                    URLString = "http://localhost:8080/arcade/" + "sream" + "/" + "mame" + "/" + "screen-saver";
                    makeRESTCall(URLString);
                    break;         
                case "7": //a new console was selected, game will be "unknown"
                    URLString = "http://localhost:8080/arcade/" + "sream" + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
                    makeRESTCall(URLString);
                    break; 
                case "9": //game selected but also could be console scrolling too!
                    if (consoleName_.toLowerCase().equals("main menu")) { //then we are scrolling consoles so treat different, console name will be "main menu" and the game name will be the console name
                        URLString = "http://localhost:8080/arcade/" + "sream" + "/" + BaseGameName_.toLowerCase() + "/" + "dummy";
                        makeRESTCall(URLString);
                    } else {
                        URLString = "http://localhost:8080/arcade/" + "sream" + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
                        makeRESTCall(URLString);
                    }
                    break; 
                default: 
                     if (!silent_) {
                        System.out.println("No Event ID match");
                        logMePixelcade.pLogger.info("No Event ID match");
              }
            } 
        }
        
        
        else if (!text_.equals("") && gameName_.equals("")) {  //if there is text and no game, we're in text mode
             
            URLString = "http://localhost:8080/text?t=" + text_;
             
            if (!color_.equals("")) {
                URLString = URLString + "&c=" + color_;
            } 
            if (!speed_.equals("")) {
                URLString = URLString + "&speed=" + speed_;
            }
            if (!scrollsmooth_.equals("")) {
                URLString = URLString + "&ss=" + scrollsmooth_;
            } 
            if (!loop_.equals("")) {
                URLString = URLString + "&l=" + loop_;
            } 
            
            makeRESTCall(URLString);
        }
        
        
        //else if (!text_.equals("")) {  
        //     URLString = "http://localhost:8080/text?t=" + text_;
        //     makeRESTCall(URLString);
        //}
        
        else if (!color_.equals("") && text_.equals("") && !gameTitle_) {  //color was specified but no text so it's a color only command
             URLString = "http://localhost:8080/text/color/" + color_;
             makeRESTCall(URLString);
        }
        
        else if (!speed_.equals("") && text_.equals("") && !gameTitle_) {  //speed was specified but no text so it's a speed only command
             URLString = "http://localhost:8080/text/speed/" + speed_;
             makeRESTCall(URLString);
        }
        
        else if (!scrollsmooth_.equals("") && text_.equals("") && !gameTitle_) {  //scroll smooth was specified but no text so it's a scrollsmooth only command
             URLString = "http://localhost:8080/text/scrollspeed/" + scrollsmooth_;
             makeRESTCall(URLString);
        }
            
        else {       //we're not doing EDS and hyperspin with an event id so just normal REST call
            
                if ( mode_!="" && consoleName_ !="" && gameName_ != "") {  //let's make sure we have valid command lines before proceeding
                //if ( (!mode_.equals("")) && (!consoleName_.equals("")) && (!BaseGameName_.equals(""))) {  //let's make sure we have valid command lines before proceeding
                    
                    BaseGameName_ = FilenameUtils.getBaseName(gameName_); //stripping out the extension

                    if (!silent_) {
                        System.out.println("Mode: " + mode_.toLowerCase());
                        logMePixelcade.pLogger.info("Mode: " + mode_.toLowerCase());
                        System.out.println("Console Name: " + consoleName_.toLowerCase());
                        logMePixelcade.pLogger.info("Console Name: " + consoleName_.toLowerCase());
                        //System.out.println("Original Game Name: " + gameName_.toLowerCase());
                        System.out.println("Game Name: " + BaseGameName_.toLowerCase());
                        logMePixelcade.pLogger.info("Game Name: " + BaseGameName_.toLowerCase());
                    }

                    if (!mode_.equals("stream") && !mode_.equals("write")) {   
                         
                         if (!silent_) System.out.println("stream or write for mode was not entered so defaulting to stream mode");
                         mode_ = "stream";
                     }

                    URLString = "http://localhost:8080/arcade/" + mode_.toLowerCase() + "/" + consoleName_.toLowerCase() + "/" + BaseGameName_.toLowerCase();
                    //TO DO we could add localhost to settings.ini in case somoene wants to enable their system remote via an IP address or domain name instead
                    //now let's append text and loop if those were specified
                    
                    if (!loop_.equals("")) {
                        URLString = URLString + "?l=" + loop_;
                    } else {
                        URLString = URLString + "?l=0";  //had to do this because first parameter has to be ? and then next is &
                    }
                            
                    if (gameTitle_) {  //if game title is specified, we'll take that first before just plan text
                        URLString = URLString + "&gt";
                    } else {

                        if (!text_.equals("")) {
                            URLString = URLString + "&t=" + text_;
                        }
                    }
                    
                    if (!color_.equals("")) {
                        URLString = URLString + "&c=" + color_;
                    } 
                    
                    if (!speed_.equals("")) {
                        URLString = URLString + "&speed=" + speed_;
                    } 
                    
                    if (!scrollsmooth_.equals("")) {
                        URLString = URLString + "&ss=" + scrollsmooth_;
                    } 

                    makeRESTCall(URLString);
                    
                }
                else {

                    System.out.println("INCORRECT PARAMETERS");
                    System.out.println(CliPixel.getInstructions());
                }
        }
            
        
    }
    
    private static void makeRESTCall(String URLString) throws MalformedURLException, IOException {
        //replace the spaces with %20 as the URL call will fail without this
            URLString=URLString.replaceAll(" ","%20");
            if (!silent_) System.out.println("REST Call URL: " + URLString);
           // logMe.aLogger.info("REST Call URL: " + URLString);
                    
            String urlParameters = "";
            StringBuilder content = null;
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

                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {

                        String line;
                        content = new StringBuilder();

                        while ((line = in.readLine()) != null) {
                            content.append(line);
                            content.append(System.lineSeparator());
                        }
                    } catch (Throwable throwable) {
                    quitExceptionHandler();   //this one happens when the shutdown command is sent so let's catch it
                } 
                    
                    if (!silent_) System.out.println(content.toString());

                } finally {

                    con.disconnect();
                } 
    }
    
    
     private static void exceptionHandler() {
        
        if (!quit_) {
           
            System.out.println(errorMsg);  //pixelweb is not running so let's prompt the user
                    
                   if (isWindows()) {
                      
                       //launching pixelweb.exe worked but launched it in a hidden mode so not user friendly, removing for now 
                       /*
                        Process run = Runtime.getRuntime().exec(pixelwebLaunchPath,null,exeWorkingPath);
                       // logMe.aLogger.info("Launching " + pixelwebLaunchPath);
                        try {
                            run.waitFor();
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(PixelcadeFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                           // logMe.aLogger.log(Level.SEVERE, "could not run pixelweb", ex);
                        } */
                        
                         JFrame frame = new JFrame("JOptionPane showMessageDialog example");  //let's show a pop up too so the user doesn't miss it
                            JOptionPane.showMessageDialog(frame,
                                    errorMsg,
                                    "Pixelcade Listener",
                                    JOptionPane.ERROR_MESSAGE);
                        
                        
                    }  else if (isMac()) {
                           
                            
                            JFrame frame = new JFrame("JOptionPane showMessageDialog example");  //let's show a pop up too so the user doesn't miss it
                            JOptionPane.showMessageDialog(frame,
                                    errorMsg,
                                    "Pixelcade Listener",
                                    JOptionPane.ERROR_MESSAGE);
                    } 
        
        
        System.exit(1);                       //we can't continue because pixelweb is not running
        
        //TO DO get the auto-launch going if pixelweb not running, code below didn't work on windows
        /* try {
            Process process = new ProcessBuilder(exePath).start();
        } catch (IOException ex) {
            Logger.getLogger(PixelcadeFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        } */
        
        }
        //TO DO possibly could auto-launch the listener using shell cmd from Java, set a timer delay, and then re-run
     }
     
       private static void quitExceptionHandler() {
       
        
        System.exit(1);                      
        
      
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
