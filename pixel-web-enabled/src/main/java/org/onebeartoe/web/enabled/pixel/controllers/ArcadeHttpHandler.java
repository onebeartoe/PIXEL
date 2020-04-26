
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.fazecast.jSerialComm.SerialPort;
import java.util.Random;

/**
 * @author Roberto Marquez
 */
public class ArcadeHttpHandler extends ImageResourceHttpHandler
{
    public ArcadeHttpHandler(WebEnabledPixel application)
    {
        super(application);
        
        //basePath = "arcade/";
        basePath = "";
        defaultImageClassPath = "pacman.png"; //to do change this
        modeName = "arcade";
    }
    
    private void handlePNG(File arcadeFilePNGFullPath, Boolean saveAnimation, int loop, String consoleNameMapped, String PNGNameWithExtension) throws MalformedURLException, IOException, ConnectionLostException {
        
        LogMe logMe = LogMe.getInstance();
         
        Pixel pixel = application.getPixel();
        pixel.writeArcadeImage(arcadeFilePNGFullPath, saveAnimation, loop, consoleNameMapped, PNGNameWithExtension,WebEnabledPixel.pixelConnected); //we have the full file path here
        
        
    }
    
    private void handleGIF(String consoleName, String arcadeName, Boolean saveAnimation, int loop) {
         
        Pixel pixel = application.getPixel();
        
        try {
            pixel.writeArcadeAnimation(consoleName, arcadeName , saveAnimation, loop, WebEnabledPixel.pixelConnected);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void writeImageResource(String urlParams) throws IOException, ConnectionLostException
    {
         
        Pixel pixel = application.getPixel();
        
        String streamOrWrite = null ;
 	String consoleName = null ;
 	String arcadeName = null ;
        String arcadeNameExtension = null; 
        String arcadeNameOnly = null;
        //String arcadeFilePath = null;
        
        String arcadeFilePathPNG = null;
        String arcadeFilePathGIF = null;
        String consoleFilePathPNG = null;
        String consoleFilePathGIF = null;
        String defaultConsoleFilePathPNG = null;
        String consoleNameMapped = null;
        LogMe logMe = null;
        String[] consoleArray = new String[] {  "mame", "atari2600", "daphne", "nes", "neogeo", "atarilynx",
                                                "snes", "atari5200", "atari7800", "atarijaguar", "c64", 
                                                "genesis", "capcom", "n64", "psp", "psx", "coleco", "dreamcast",
                                                "fba", "gb", "gba", "ngp", "ngpc", "odyssey",
                                                "saturn", "megadrive", "gbc", "gamegear", "mastersystem", 
                                                "sega32x", "3do", "msx", "atari800", "pc",
                                                "nds", "amiga", "fds", "futurepinball", "amstradcpc",
                                                "apple2", "intellivision", "macintosh", "ps2", "pcengine",
                                                "segacd", "sg-1000", "ti99", "vectrex", "virtualboy",
                                                "visualpinball", "wonderswan", "wonderswancolor", "zinc", "sss",
                                                "zmachine", "zxspectrum"};
                 
        boolean saveAnimation = false;
        int loop_ = 0;
        String text_ = "";
        int scrollsmooth_ = 1;
        Long speeddelay_ = 10L;
        String color_ = "";
        Color color = Color.RED; //default to red color if not added
        int i = 0;
        boolean textSelected = false;
        long speed = 10L; 
       
       if (WebEnabledPixel.isWindows()) {  //unfortunate hack we have to do as scrolling on windows is slower
           scrollsmooth_ = 3;
       } 
       
       
        //to do the slashes will screw up this logic, we could remove the / first or switch to another convention
       
        //urlParams = urlParams.replaceAll("\r", "").replaceAll("\n", ""); //to do : add check for %00 null end of string
        // to do: need to modify pixelcade.exe for the extra post params
        
        //  /text/?t=hello%20world?c=red?s=10?l=2
        
        List<NameValuePair> params = null;
        try {
                params = URLEncodedUtils.parse(new URI(urlParams), "UTF-8");
            } catch (URISyntaxException ex) {
                Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        URI tempURI = null;
        try {
             tempURI = new URI("http://localhost:8080" + urlParams);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String URLPath = tempURI.getPath();
        String [] arcadeURLarray = URLPath.split("/"); 
       
        
        logMe = LogMe.getInstance();
        if (!CliPixel.getSilentMode()) {
            System.out.println("arcade handler received: " + urlParams);
            logMe.aLogger.info("arcade handler received: " + urlParams);
        }
        
        if (arcadeURLarray.length == 5) {
        	
        	    streamOrWrite = arcadeURLarray[2];
        	    consoleName = arcadeURLarray[3];
        	    arcadeName = arcadeURLarray[4];
                    arcadeName = arcadeName.trim();
                    arcadeName = arcadeName.replace("\n", "").replace("\r", "");
            
            //System.out.println("Arcade Name: " + arcadeName);
            //to do add code to remove " " in case the user entered those
                    
            //arcadeName could be a full path or just a name, we need to handle both
            //String name1 = FilenameUtils.getName(arcadeName);
            //String name2 = FilenameUtils.getBaseName(arcadeName);
            //String name3 = FilenameUtils.getExtension(arcadeName);
                    
            //let's make sure this file exists and skip if not
            //arcadeNameExtension = FilenameUtils.getExtension(arcadeName); 
            

            //String fileNameWithOutExt = FilenameUtils.removeExtension(fileNameWithExt);
            //arcadeNameOnly = FilenameUtils.getBaseName(arcadeName); //stripping out the extension like .zip for example
            
            //if the extension is more than 3 chars, then let's treat it as a name that just happens to have a . in it
            
            arcadeNameExtension = FilenameUtils.getExtension(arcadeName);
            
            //System.out.println("arcade name only: " + arcadeName);
            //System.out.println("arcade name extension: " + arcadeNameExtension);
            //System.out.println("arcade name extension length: " + arcadeNameExtension.length());
            
           
            if (arcadeNameExtension.length() > 3) {   //this means there just happened to be a period in the file  name 
                arcadeNameOnly = arcadeName;
            }    
            else  { //there was no extension or it's a valid extension so proceed as normal and remove the extension if it's there
                arcadeNameOnly = FilenameUtils.removeExtension(arcadeName);
            } 
            
            //arcadeNameOnly = FilenameUtils.removeExtension(arcadeName);  //this wasn't working for dr.mario.zip for example and was just returning dr
            //arcadeNameOnly = arcadeName; //turns out the extension was not getting passed
            //arcadeNameOnly = arcadeName.replaceFirst("[.][^.]+$", "");
            
            i = 0;
            for (NameValuePair param : params) {
                i++;
                switch (param.getName()) {

                    case "t": //scrolling text value
                        text_ = param.getValue();
                        textSelected = true;
                        break;
                    case "text": //scrolling text value
                        text_ = param.getValue();
                        textSelected = true;
                        break;
                    case "l": //how many times to loop
                        loop_ = Integer.valueOf(param.getValue());
                        break;
                    case "loop": //loop
                        loop_ = Integer.valueOf(param.getValue());
                        break;
                    case "gt": //game title
                        text_ = WebEnabledPixel.getGameName(arcadeNameOnly);
                        break;
                    case "gametitle": //game title
                        text_ = WebEnabledPixel.getGameName(arcadeNameOnly);
                        break;
                    case "ss": //scroll smooth
                        scrollsmooth_ = Integer.valueOf(param.getValue());
                        break;
                    case "scrollsmooth": //scroll smooth
                        scrollsmooth_ = Integer.valueOf(param.getValue());
                        break;
                    case "speed": //scroll smooth
                        speeddelay_ = Long.valueOf(param.getValue());
                        break; 
                    case "c": //color
                        color_ = param.getValue();
                        break;
                }
            }
            
           
            
            //let's now refer to our mapping table for the console names, because console names are different for RetroPie vs. HyperSpin and other front ends
            //to do add a user defined .txt mapping if the console is not found in our mapping table
            
            consoleName = consoleName.toLowerCase();
            //let's see if the console matches one of our known ones and if not, we'll go to the mapping table
            
            //or let's first check against the mapping table
            //and if no match, we'll go the end of the switch statement and the console will stay the same, this will be expensive
             //System.out.println("Console before mapping: " + consoleName);
             //let's add some common mappings here and if match we can skip the expensive mapping
             //so first do we have a match vs. an array of retropie dirs
             //if yes, we're good
             //if no, let's check & map a couple common hyperspin and skip the expesive mapping table
             
            /*if (!consoleMatch(consoleArray,consoleName)) {  //if our console already matches, we are good but if not, we need to check it against mapping table
                //consoleNameMapped = getConsoleNamefromMapping(consoleName);     //will return original console if no matcn
               // consoleNameMapped = pixel.getConsoleNamefromMapping(consoleName); 
                consoleNameMapped = WebEnabledPixel.getGameName(arcadeNameOnly);
            } else {
                 consoleNameMapped = consoleName;                               //we were already mapped so let's use it
            }*/
            
            if (!consoleMatch(consoleArray, consoleName)) {  //if our console already matches, we are good but if not, we need to check it against mapping table
                //will return original console if no matcn
                consoleNameMapped = WebEnabledPixel.getConsoleMapping(consoleName);
            } else {
                consoleNameMapped = consoleName;                               //we were already mapped so let's just use it
            }
            
            if (consoleNameMapped.equals("mame-libretro")) { //yes this is a hack, some users this was still not getting mapped right
                consoleNameMapped = "mame";
            }
                //more user friendly for the log since technically it's looping forever until stopped

             //System.out.println("Console after mapping: " + consoleNameMapped);
             if (!CliPixel.getSilentMode()) {
                System.out.println(streamOrWrite.toUpperCase() + " MODE");
                System.out.println("Console Before Mapping: " + consoleName);
                System.out.println("Console Mapped: " + consoleNameMapped);
                System.out.println("Game Name Only: " +  arcadeNameOnly);
                if (loop_ == 0) {
                    System.out.println("# of Times to Loop: null");
                } else {
                    System.out.println("# of Times to Loop: " + loop_);
                }
                
                if (text_ != "") System.out.println("alt text if game file not found: " + text_); //game title would have been written here

                logMe.aLogger.info(streamOrWrite.toUpperCase() + " MODE");
                logMe.aLogger.info("Console Before Mapping: " + consoleName);
                logMe.aLogger.info("Console Mapped: " + consoleNameMapped);
                logMe.aLogger.info("Game Name Only: " +  arcadeNameOnly);
                 if (loop_ == 0) {
                    logMe.aLogger.info("# of Times to Loop: null");
                } else {
                    logMe.aLogger.info("# of Times to Loop: " + loop_);
                }
                 if (text_ != "") logMe.aLogger.info("alt text if marquee file not found: " + text_);
             }
           
            //now let's decide if we're going to find the png or gif 
            
             arcadeFilePathPNG = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNameOnly +".png";
             File arcadeFilePNG = new File(arcadeFilePathPNG);
             
             arcadeFilePathGIF = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNameOnly +".gif";
             File arcadeFileGIF = new File(arcadeFilePathGIF);
             
            
            //because retropie wil convert the filename to lower case during the call and Windows will add an _ for the space, we must check for alternative files, specifically all lowercase and spaces substituted with underscore
             if (arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) {
                 //arcadeNameOnly = FilenameUtils.getBaseName(arcadeName);
                 arcadeNameOnly = FilenameUtils.removeExtension(arcadeName);
             } else {
             
                 //file is not there so let's check underscore version
                 String arcadeNameOnlyUnderscore = arcadeNameOnly.replaceAll("_", " ");  //replace underscore with space
                 String arcadeFilePathPNGUnderscore = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNameOnlyUnderscore +".png";
                 arcadeFilePNG = new File(arcadeFilePathPNGUnderscore);
                 
                 if (arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) {
                     arcadeNameOnly = arcadeNameOnlyUnderscore;
                 }
                 else {
                    String arcadeNamelowerCase = arcadeNameOnly.toLowerCase();
                    String arcadeFilePathPNGlowerCase = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNamelowerCase +".png";
                    arcadeFilePNG = new File(arcadeFilePathPNGlowerCase);
                    
                    if (arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) {
                     arcadeNameOnly = arcadeNamelowerCase;
                    }
                 }
             }
             
             if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                 //arcadeNameOnly = FilenameUtils.getBaseName(arcadeName);
                 arcadeNameOnly = FilenameUtils.removeExtension(arcadeName);
             } else {
             
                 //file is not there so let's check underscore version
                 String arcadeNameOnlyUnderscore = arcadeNameOnly.replaceAll("_", " ");  //replace underscore with space, windows front ends do this
                 String arcadeFilePathGIFUnderscore = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNameOnlyUnderscore +".gif";
                 arcadeFileGIF = new File(arcadeFilePathGIFUnderscore);
                 
                 if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                     arcadeNameOnly = arcadeNameOnlyUnderscore;
                 }
                 else {
                    String arcadeNamelowerCase = arcadeNameOnly.toLowerCase();
                    String arcadeFilePathGIFlowerCase = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNamelowerCase +".gif";
                    arcadeFileGIF = new File(arcadeFilePathGIFlowerCase);
                    
                    if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                     arcadeNameOnly = arcadeNamelowerCase;
                    }
                 }
             }
             
             //System.out.println("delete PNG path " + arcadeFilePathPNG);
             //System.out.println("delete GiF path " + arcadeFilePathGIF);
             
            String requestedPath = application.getPixel().getPixelHome() + consoleNameMapped + "\\" + arcadeNameOnly;
             if (!CliPixel.getSilentMode()) {
                    System.out.println("Looking for: " + requestedPath  + ".png or .gif");
                    logMe.aLogger.info("Looking for: " + requestedPath  + ".png or .gif");
            }
                    
            if (streamOrWrite.equals("write")) {  //we're in write mode so gif gets the priority if both gif and png exist, never should write mode be used for front end scrolling
                saveAnimation = true;
                
                //if our accessory is connected, let's write to that
                if (WebEnabledPixel.arduino1MatrixConnected) { //if it's connected, let's write the game text to the MAX7219 led matrix, 7 segment, and OLED which is connected to an Arduino
                            WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getGameMetaData(arcadeNameOnly));
                            logMe.aLogger.info("Accessory Call: " + WebEnabledPixel.getGameMetaData(arcadeNameOnly));
                }
                
               
                if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                        //System.out.println("delete went here GIF");
                        handleGIF(consoleNameMapped, arcadeNameOnly +".gif", saveAnimation, loop_);
                }
                
                //to do we may need to add some additional checks for _ instead of space and lowercase file names
                        
                else if(arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) { 
                        //System.out.println("delete went here PNG");
                        handlePNG(arcadeFilePNG, saveAnimation,loop_,consoleNameMapped,FilenameUtils.getName(arcadeFilePathPNG));
                }
                
                else if (text_ != "" && !text_.equals("nomatch")) {  //the game image or png is not there and alt text was supplied so let's scroll that alt text, if equal to mame.csv not found or rom name no match then we went to the maping table but didn't find anything so if that is the case, then let's skip this and just write the generic image
                         
                         //Pixel pixel = application.getPixel();
                         int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
                         
                         int yTextOffset = -4;
                         int fontSize_ = 22;
                         
                         speed = 10L;
                         speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset
                         if (speeddelay_ != 10L) {  //this means another value was set from a parameter for speed so let's use that
                             speed = speeddelay_;
                         }
                         
                         if (color_ != "") {
                             color = getColorFromHexOrName(color_);
                         }
                         
                         pixel.scrollText(text_, loop_, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
                        
                }
                
                else { //nothing is there so let's use the generic console
                        
                        consoleFilePathGIF = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".gif"; 
                        File consoleFileGIF = new File(consoleFilePathGIF);
                    
                        consoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".png"; 
                        File consoleFilePNG = new File(consoleFilePathPNG);
                        
                        if(consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) { 

                              if (!CliPixel.getSilentMode()) {
                                    System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
                                    logMe.aLogger.info("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
                              }
                              handleGIF("console", "default-" + consoleNameMapped + ".gif", saveAnimation, loop_);
                        }      
                       
                        else if (consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) { 
                             
                              handlePNG(consoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(consoleFilePathPNG)); //mame/default-marquee.png
                        }
                       
                        else {
                               if (!CliPixel.getSilentMode()) {
                                    System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
                                    logMe.aLogger.info("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
                               }
                               defaultConsoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-marquee.png"; 
                               File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
                               
                               if(defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) { 
                                       handlePNG(defaultConsoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(defaultConsoleFilePathPNG));
                               }
                               else {
                                       if (!CliPixel.getSilentMode()) {
                                            System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
                                            System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
                                            logMe.aLogger.info("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
                                            logMe.aLogger.info("Skipping LED marquee " + streamOrWrite + ", please check the files");
                                       }
                               }
                        }
                }
                
            } else {                      //we're in stream mode so png gets the priority if both png and gif exist unless both are there
                saveAnimation = false;
                
                //if our accessory is connected, let's write to that
                if (WebEnabledPixel.arduino1MatrixConnected) { //if it's connected, let's write the game text to the MAX7219 led matrix, 7 segment, and OLED which is connected to an Arduino
                            WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getGameMetaData(arcadeNameOnly));
                            logMe.aLogger.info("Accessory Call: " + WebEnabledPixel.getGameMetaData(arcadeNameOnly));
                }
                
                
                 if(arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory() && arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) { 
                    
                      //in this case both the gif and png exist so we first need to kill the Q, then loop the GIF one time, and then stay on the PNG
                      //http://localhost:8080/arcade/stream/black/dummy
                      handlePNG(arcadeFilePNG, false,0,"black","dummy"); //kill Q and send black frame
                      //handlePNG(File arcadeFilePNGFullPath, Boolean saveAnimation, int loop, String consoleNameMapped, String PNGNameWithExtension)
                      handleGIF(consoleNameMapped, arcadeNameOnly + ".gif", saveAnimation, 1);
                      //handlePNG(arcadeFilePNG, saveAnimation,99999,consoleNameMapped,FilenameUtils.getName(arcadeFilePathPNG)); //99999 means wait until the gif has looped
                      handlePNG(arcadeFilePNG, saveAnimation,99999,consoleNameMapped,arcadeNameOnly + ".png"); //99999 means wait until the gif has looped
                 }
                 
                 else {
                
                    if(arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) { 
                            //System.out.println("delete went here PNG");
                            //handlePNG(arcadeFilePNG, saveAnimation,loop_,consoleNameMapped,FilenameUtils.getName(arcadeFilePathPNG));
                            handlePNG(arcadeFilePNG, saveAnimation,loop_,consoleNameMapped,arcadeNameOnly + ".png");
                    }
                    else if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                            //System.out.println("delete went here GIF");
                            handleGIF(consoleNameMapped, arcadeNameOnly +".gif", saveAnimation, loop_);
                    }
                    
                    else if (text_ != "" && !text_.equals("nomatch")) {  //the game image or png is not there and alt text was supplied so let's scroll that alt text
                        
                         //Pixel pixel = application.getPixel();
                         int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
                         
                         int yTextOffset = -4;
                         int fontSize_ = 22;
                         
                         speed = 10L;  //10 is default for 128x32
                         speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset
                         
                         if (i == 1 && textSelected == true) {  //then only &t was selected and we will override color and speed from settings.ini
                             
                             if (WebEnabledPixel.getTextColor().equals("random")) { //random color is selected in settings.ini so now we need to randomy pick the color
                                 
                                Random randomGenerator = new Random();
                                int randomInt = randomGenerator.nextInt(11) + 1;  //0 to 11 + 1 = random number between 1 and 12
                                //System.out.println("Random number generated is : " + randomInt);
                                
                                  switch (randomInt) {

                                        case 1:
                                            color = Color.RED;
                                            break;
                                        case 2:
                                            color = Color.BLUE;
                                            break;
                                        case 3:
                                            color = Color.CYAN;
                                            break;
                                        case 4:
                                            color = Color.GRAY;
                                            break;
                                        case 5:
                                            color = Color.DARK_GRAY;
                                            break;
                                        case 6:
                                            color = Color.GREEN;
                                            break;
                                        case 7:
                                            color = Color.LIGHT_GRAY;
                                            break;
                                        case 8:
                                            color = Color.MAGENTA;
                                            break;
                                        case 9:
                                            color = Color.ORANGE;
                                            break;
                                        case 10:
                                            color = Color.PINK;
                                            break;
                                        case 11:
                                            color = Color.YELLOW;
                                            break;
                                        case 12:
                                            color = Color.WHITE;
                                            break;
                                        default:
                                            color = Color.MAGENTA;
                                    }
                                 
                             } else {
                                  color = getColorFromHexOrName(WebEnabledPixel.getTextColor()); //getting this from settings.ini
                             }
                             
                             String speedfromSettings = WebEnabledPixel.getTextSpeed(); //getting this from settings.ini
                             
                             switch (speedfromSettings) {
                                    case "slow":
                                         scrollsmooth_ = 1;
                                        break;
                                    case "normal":
                                         scrollsmooth_ = 2;
                                        break;
                                    case "fast":
                                         scrollsmooth_ = 5;
                                        break;
                                    default:
                                         scrollsmooth_ = 2;
                                }   
                             
                         } 
                         
                         else {
                         
                                speed = 10L;  //10 is default for 128x32
                                speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset
                                if (speeddelay_ != 10L) {  //this means another value was set from a parameter for speed so let's use that
                                    speed = speeddelay_;
                                }

                                if (color_ != "") {
                                    color = getColorFromHexOrName(color_);
                                }
                         }
                         
                         pixel.scrollText(text_, loop_, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_); 
                        
                }
                
                    else { //nothing is there so let's use the console
                    
                        consoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".png"; 
                        File consoleFilePNG = new File(consoleFilePathPNG);
                        
                        consoleFilePathGIF = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".gif"; 
                        File consoleFileGIF = new File(consoleFilePathGIF);
                       
                        //System.out.println("delete console gif: " + consoleFilePathGIF);
                    
                        if(consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) { 

                              handlePNG(consoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(consoleFilePathPNG));
                        }
                        else if(consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) { 
                                if (!CliPixel.getSilentMode()) {
                                    System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
                                    logMe.aLogger.info("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
                                }
                                handleGIF("console", "default-" + consoleNameMapped + ".gif", saveAnimation, loop_);
                        }
                        else {
                               if (!CliPixel.getSilentMode()) {
                                    System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
                                    logMe.aLogger.info("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
                               }
                               defaultConsoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-marquee.png"; 
                               File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
                               
                               if(defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) { 
                                       handlePNG(defaultConsoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(defaultConsoleFilePathPNG));
                               }
                               else {
                                         if (!CliPixel.getSilentMode()) {
                                                System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
                                                System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
                                                logMe.aLogger.info("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
                                                logMe.aLogger.info("Skipping LED marquee " + streamOrWrite + ", please check the files");
                                         }
                               }
                        }
                }
              }          
            }
        }
        
        else {
             System.out.println("** ERROR ** URL format incorect, use http://localhost:8080/arcade/<stream or write>/<platform name>/<game name .gif or .png>");
             System.out.println("Example: http://localhost:8080/arcade/write/mame/pacman.png or http://localhost:8080/arcade/stream/atari2600/digdug.gif");
             logMe.aLogger.severe("** ERROR ** URL format incorect, use http://localhost:8080/arcade/<stream or write>/<platform name>/<game name .gif or .png>");
             logMe.aLogger.severe("Example: http://localhost:8080/arcade/write/mame/pacman.png or http://localhost:8080/arcade/stream/atari2600/digdug.gif");
        }
    }
    
    

    public static boolean consoleMatch(String[] arr, String targetValue) {
	for(String s: arr){
		if(s.equals(targetValue))
			return true;
	}
	return false;
    } 
    
    
      /* public static String getConsoleNamefromMapping(String originalConsoleName)
    {
         String consoleNameMapped = null; //to do set this if null?
         
         originalConsoleName = originalConsoleName.toLowerCase();
         //add the popular ones first to save time
          
         switch (originalConsoleName) {
            
            case "mame-libretro":
                 consoleNameMapped = "mame";
                 return consoleNameMapped;
            case "mame-mame4all":
                consoleNameMapped = "mame";
                 return consoleNameMapped;
            case "arcade":
                consoleNameMapped = "mame";
                 return consoleNameMapped;
            case "mame-advmame":
                consoleNameMapped = "neogeo";
                 return consoleNameMapped;
            case "atari 2600":
                consoleNameMapped = "atari2600";
                return consoleNameMapped;
            case "nintendo entertainment system":
                consoleNameMapped = "nes";
                return consoleNameMapped;
            case "nintendo 64":
                consoleNameMapped = "n64";
                return consoleNameMapped;
            case "sony playstation":
                 consoleNameMapped = "psx";
                 return consoleNameMapped;
            case "sony playstation 2":
                consoleNameMapped = "ps2";
                 return consoleNameMapped;
            case "sony pocketstation":
                consoleNameMapped = "psp";
                 return consoleNameMapped;
            case "sony psp":
                consoleNameMapped = "psp";
                 return consoleNameMapped;
            case "amstrad cpc":
                consoleNameMapped = "amstradcpc";
                 return consoleNameMapped;
            case "amstrad gx4000":
                consoleNameMapped = "amstradcpc";
                 return consoleNameMapped;
            case "apple II":
                consoleNameMapped = "apple2";
                 return consoleNameMapped;
            case "atari 5200":
                consoleNameMapped = "atari5200";
                 return consoleNameMapped;
            case "atari 7800":
                consoleNameMapped = "atari7800";
                 return consoleNameMapped;
            case "atari jaguar":
                consoleNameMapped = "atarijaguar";
                 return consoleNameMapped;
            case "atari jaguar cd":
                consoleNameMapped = "atarijaguar";
                 return consoleNameMapped;
            case "atari lynx":
                consoleNameMapped = "atarilynx";
                 return consoleNameMapped;
            case "bandai super vision 8000":
                consoleNameMapped = "wonderswan";
                 return consoleNameMapped;
            case "bandai wonderswan":
                consoleNameMapped = "wonderswan";
                 return consoleNameMapped;
            case "bandai wonderswan color":
                consoleNameMapped = "wonderswancolor";
                 return consoleNameMapped;
            case "capcom classics":
                consoleNameMapped = "capcom";
                 return consoleNameMapped;
            case "capcom play pystem":
                consoleNameMapped = "capcom";
                 return consoleNameMapped;
            case "capcom play system II":
                consoleNameMapped = "capcom";
                 return consoleNameMapped;
            case "capcom play system III":
                consoleNameMapped = "capcom";
                 return consoleNameMapped;
            case "colecovision":
                consoleNameMapped = "coleco";
                 return consoleNameMapped;
            case "commodore 128":
                consoleNameMapped = "c64";
                 return consoleNameMapped;
            case "commodore 16 & plus4":
                consoleNameMapped = "c64";
                 return consoleNameMapped;
            case "commodore 64":
                consoleNameMapped = "c64";
                 return consoleNameMapped;
            case "commodore amiga":
                consoleNameMapped = "amiga";
                 return consoleNameMapped;
            case "commodore amiga cd32":
                consoleNameMapped = "amiga";
                 return consoleNameMapped;
            case "commodore vic-20":
                consoleNameMapped = "c64";
                 return consoleNameMapped;
            case "final burn alpha":
                consoleNameMapped = "fba";
                 return consoleNameMapped;
            case "future pinball":
                consoleNameMapped = "futurepinball";
                 return consoleNameMapped;
            case "gce vectrex":
                consoleNameMapped = "vectrex";
                 return consoleNameMapped;
            case "magnavox odyssey":
                consoleNameMapped = "odyssey";
                 return consoleNameMapped;
            case "magnavox odyssey 2":
                consoleNameMapped = "odyssey";
                 return consoleNameMapped;
            case "mattel intellivision":
                consoleNameMapped = "intellivision";
                 return consoleNameMapped;
            case "microsoft msx":
                consoleNameMapped = "msx";
                 return consoleNameMapped;
            case "microsoft msx2":
                consoleNameMapped = "msx";
                 return consoleNameMapped;
            case "microsoft msx2+":
                consoleNameMapped = "msx";
                 return consoleNameMapped;
            case "microsoft windows 3.x":
                consoleNameMapped = "pc";
                 return consoleNameMapped;
            case "misfit mame":
                consoleNameMapped = "mame";
                 return consoleNameMapped;
            case "nec pc engine":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec pc engine-cd":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec pc-8801":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec pc-9801":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec pc-fx":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec supergrafx":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec turbografx-16":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nec turbografx-cd":
                consoleNameMapped = "pcengine";
                 return consoleNameMapped;
            case "nintendo 64dd":
                consoleNameMapped = "n64";
                 return consoleNameMapped;
            case "nintendo famicom":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo famicom disk system":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo game boy":
                consoleNameMapped = "gb";
                 return consoleNameMapped;
            case "nintendo game boy advance":
                consoleNameMapped = "gba";
                 return consoleNameMapped;
            case "nintendo game boy color":
                consoleNameMapped = "gbc";
                 return consoleNameMapped;
            case "nintendo gamecube":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo pokemon mini":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo satellaview":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo super famicom":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo super game boy":
                consoleNameMapped = "gba";
                 return consoleNameMapped;
            case "nintendo virtual boy":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo wii":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo wii u":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "nintendo wiiware":
                consoleNameMapped = "nes";
                 return consoleNameMapped;
            case "panasonic 3do":
                consoleNameMapped = "3do";
                 return consoleNameMapped;
            case "pc games":
                consoleNameMapped = "pc";
                 return consoleNameMapped;
            case "pinball fx2":
                consoleNameMapped = "futurepinball";
                 return consoleNameMapped;
            case "sega 32x":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega cd":
                consoleNameMapped = "segacd";
                 return consoleNameMapped;
            case "sega classics":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega dreamcast":
                consoleNameMapped = "dreamcast";
                 return consoleNameMapped;
            case "sega game gear":
                consoleNameMapped = "gamegear";
                 return consoleNameMapped;
            case "sega genesis":
                consoleNameMapped = "genesis";
                 return consoleNameMapped;
            case "sega hikaru":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega master system":
                consoleNameMapped = "mastersystem";
                 return consoleNameMapped;
            case "sega model 2":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega model 3":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega naomi":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega pico":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega saturn":
                consoleNameMapped = "saturn";
                 return consoleNameMapped;
            case "sega sc-3000":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega sg-1000":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega st-v":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega triforce":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sega vmu":
                consoleNameMapped = "sega32x";
                 return consoleNameMapped;
            case "sinclair zx spectrum":
                consoleNameMapped = "zxspectrum";
                 return consoleNameMapped;
            case "sinclair zx81":
                consoleNameMapped = "zxspectrum";
                 return consoleNameMapped;
            case "snk classics":
                consoleNameMapped = "neogeo";
                 return consoleNameMapped;
            case "snk neo geo aes":
                consoleNameMapped = "neogeo";
                 return consoleNameMapped;
            case "snk neo geo cd":
                consoleNameMapped = "neogeo";
                 return consoleNameMapped;
            case "snk neo geo mvs":
                consoleNameMapped = "neogeo";
                 return consoleNameMapped;
            case "snk neo geo pocket":
                consoleNameMapped = "ngp";
                 return consoleNameMapped;
            case "snk neo geo pocket color":
                consoleNameMapped = "ngpc";
                 return consoleNameMapped;
            case "sony psp minis":
                consoleNameMapped = "psp";
                 return consoleNameMapped;
            case "super nintendo entertainment system":
                consoleNameMapped = "snes";
                 return consoleNameMapped;
            case "visual pinball":
                consoleNameMapped = "visualpinball";
                 return consoleNameMapped;
            default: 
                 consoleNameMapped = originalConsoleName;    //we didn't find a match so just return the name you got
                 return consoleNameMapped;
        }
    } */
         //now check for override file but only go there if it exists
         //give an example file but give it a different name
        
       
   // }
      
    public static Color hex2Rgb(String colorStr) 
    {
        return new Color(
                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
    } 
    
     private static boolean isHexadecimal(String input) {
        
        final Pattern HEXADECIMAL_PATTERN = compile("\\p{XDigit}+");
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
        return matcher.matches();
        
    }
     
    public static Color getColorFromHexOrName(String ColorStr) {
        
        Color color;   
        if (isHexadecimal(ColorStr) && ColorStr.length() == 6) {  //hex colors are 6 digits
                   color = hex2Rgb(ColorStr);
                    if (!CliPixel.getSilentMode()) System.out.println("Hex color value detected");
                    } else {   //and if not then color text was entered so let's look for a match

                        switch (ColorStr) {

                            case "red":
                                color = Color.RED;
                                break;
                            case "blue":
                                color = Color.BLUE;
                                break;
                            case "cyan":
                                color = Color.CYAN;
                                break;
                            case "gray":
                                color = Color.GRAY;
                                break;
                            case "darkgray":
                                color = Color.DARK_GRAY;
                                break;
                            case "green":
                                color = Color.GREEN;
                                break;
                            case "lightgray":
                                color = Color.LIGHT_GRAY;
                                break;
                            case "magenta":
                                color = Color.MAGENTA;
                                break;
                            case "orange":
                                color = Color.ORANGE;
                                break;
                            case "pink":
                                color = Color.PINK;
                                break;
                            case "yellow":
                                color = Color.YELLOW;
                                break;
                            case "white":
                                color = Color.WHITE;
                                break;
                            default:
                                color = Color.RED;
                                if (!CliPixel.getSilentMode()) System.out.println("Invalid color, defaulting to red");
                        }
                   }    
        
        return color;
    } 
     
    
}
