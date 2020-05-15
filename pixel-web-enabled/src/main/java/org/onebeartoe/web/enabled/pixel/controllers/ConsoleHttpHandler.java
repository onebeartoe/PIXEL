
//package org.onebeartoe.web.enabled.pixel.controllers;
//
//import ioio.lib.api.exception.ConnectionLostException;
//import java.awt.Color;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.security.NoSuchAlgorithmException;
//import java.time.Duration;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import static java.util.regex.Pattern.compile;
//import javax.imageio.ImageIO;
//import org.onebeartoe.pixel.hardware.Pixel;
//import org.onebeartoe.system.Sleeper;
//import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
//import org.onebeartoe.pixel.LogMe;
//import org.onebeartoe.pixel.PixelLogFormatter;
//import org.apache.commons.io.FilenameUtils;
//import org.onebeartoe.web.enabled.pixel.CliPixel;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.onebeartoe.web.enabled.pixel.controllers.ArcadeHttpHandler;
//
///**
// * @author Roberto Marquez
// */
//public class ConsoleHttpHandler extends ImageResourceHttpHandler
//{
//    public ConsoleHttpHandler(WebEnabledPixel application)
//    {
//        super(application);
//        
//        //basePath = "arcade/";
//        basePath = "";
//        //defaultImageClassPath = "pacman.png"; //to do change this
//        defaultImageClassPath = "mame"; //to do change this
//        modeName = "arcade";
//    }
//    
//    private void handlePNG(File arcadeFilePNGFullPath, Boolean saveAnimation, int loop, String consoleNameMapped, String PNGNameWithExtension) throws MalformedURLException, IOException, ConnectionLostException {
//        
//        LogMe logMe = LogMe.getInstance();
//        
//        Pixel pixel = application.getPixel();
//        pixel.writeArcadeImage(arcadeFilePNGFullPath, saveAnimation, loop, consoleNameMapped, PNGNameWithExtension,WebEnabledPixel.pixelConnected); //we have the full file path here
//       
//    }
//    
//    private void handleGIF(String consoleName, String arcadeName, Boolean saveAnimation, int loop) {
//         
//        Pixel pixel = application.getPixel();
//        
//        try {
//            pixel.writeArcadeAnimation(consoleName, arcadeName , saveAnimation, loop, WebEnabledPixel.pixelConnected);
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    @Override
//    protected void writeImageResource(String urlParams) throws IOException, ConnectionLostException
//    {
//         
//        Pixel pixel = application.getPixel();
//        
//        String streamOrWrite = null ;
// 	String consoleName = null ;
// 	//String arcadeName = null ;
//        //String arcadeNameExtension = null; 
//        //String arcadeNameOnly = null;
//        //String arcadeFilePath = null;
//        
//        String arcadeFilePathPNG = null;
//        String arcadeFilePathGIF = null;
//        String consoleFilePathPNG = null;
//        String consoleFilePathGIF = null;
//        String defaultConsoleFilePathPNG = null;
//        String consoleNameMapped = null;
//        LogMe logMe = null;
//        String[] consoleArray = new String[] {  "mame", "atari2600", "daphne", "nes", "neogeo", "atarilynx",
//                                                "snes", "atari5200", "atari7800", "atarijaguar", "c64", 
//                                                "genesis", "capcom", "n64", "psp", "psx", "coleco", "dreamcast",
//                                                "fba", "gb", "gba", "ngp", "ngpc", "odyssey",
//                                                "saturn", "megadrive", "gbc", "gamegear", "mastersystem", 
//                                                "sega32x", "3do", "msx", "atari800", "pc",
//                                                "nds", "amiga", "fds", "futurepinball", "amstradcpc",
//                                                "apple2", "intellivision", "macintosh", "ps2", "pcengine",
//                                                "segacd", "sg-1000", "ti99", "vectrex", "virtualboy",
//                                                "visualpinball", "wonderswan", "wonderswancolor", "zinc", "sss",
//                                                "zmachine", "zxspectrum"};
//                 
// 	boolean saveAnimation = false;
//        int loop_ = 0;
//        String text_ = "";
//        String color_ = "";
//        Color color = Color.RED; //default to red color if not added
//        int scrollsmooth_ = 1;
//        Long speeddelay_ = 10L;
//        
//        List<NameValuePair> params = null;
//        try {
//                params = URLEncodedUtils.parse(new URI(urlParams), "UTF-8");
//            } catch (URISyntaxException ex) {
//                Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        /*for (NameValuePair param : params) {
//           
//             switch (param.getName()) {
//
//                    case "t": //scrolling text value
//                        text_ = param.getValue();
//                        break;
//                    case "text": //scrolling speed
//                        text_ = param.getValue();
//                        break;
//                    case "l": //how many times to loop
//                        loop_ = Integer.valueOf(param.getValue());
//                        // Long speed = Long.valueOf(s); //to do for integer
//                        break;
//                    case "loop": //loop
//                       loop_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "c": //color
//                       color_ = param.getValue();
//                       break;
//                    }
//        }*/
//        
//        for (NameValuePair param : params) {
//
//                switch (param.getName()) {
//
//                    case "t": //scrolling text value
//                        text_ = param.getValue();
//                        break;
//                    case "text": //scrolling speed
//                        text_ = param.getValue();
//                        break;
//                    case "l": //how many times to loop
//                        loop_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "loop": //loop
//                        loop_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "ss": //scroll smooth
//                        scrollsmooth_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "scrollsmooth": //scroll smooth
//                        scrollsmooth_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "speed": //scroll smooth
//                        speeddelay_ = Long.valueOf(param.getValue());
//                        break; 
//                    case "c": //color
//                        color_ = param.getValue();
//                        break;
//                }
//            }
//  
//        // /console/stream/mame?t=x?l=x
//        //so now we just need to the left of the ?
//        URI tempURI = null;
//        try {
//             tempURI = new URI("http://localhost:8080" + urlParams);
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(ConsoleHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        String URLPath = tempURI.getPath();
//        //System.out.println("path is: " + URLPath);
//        
//        //String [] arcadeURLarray = urlParams.split("/"); 
//        String [] arcadeURLarray = URLPath.split("/"); 
//        //String [] arcadeURLarray = urlParams.split("&"); 
//        
//        logMe = LogMe.getInstance();
//        if (!CliPixel.getSilentMode()) {
//            System.out.println("console handler received: " + urlParams);
//            logMe.aLogger.info("console handler received: " + urlParams);
//        }
//        
//        if (arcadeURLarray.length == 4) {  //  /console/stream/mame?t=x?l=x
//        	
//                streamOrWrite = arcadeURLarray[2];
//                consoleName = arcadeURLarray[3];
//                //arcadeName = arcadeURLarray[4];
//
//                consoleName = consoleName.trim();
//                consoleName = consoleName.replace("\n", "").replace("\r", "");
//
//                consoleName = consoleName.toLowerCase();
//            //let's see if the console matches one of our known ones and if not, we'll go to the mapping table
//            
//            //or let's first check against the mapping table
//            //and if no match, we'll go the end of the switch statement and the console will stay the same, this will be expensive
//             //System.out.println("Console before mapping: " + consoleName);
//             //let's add some common mappings here and if match we can skip the expensive mapping
//             //so first do we have a match vs. an array of retropie dirs
//             //if yes, we're good
//             //if no, let's check & map a couple common hyperspin and skip the expesive mapping table
//             
//            if (!consoleMatch(consoleArray, consoleName)) {  //if our console already matches, we are good but if not, we need to check it against mapping table
//                //will return original console if no matcn
//                consoleNameMapped = WebEnabledPixel.getConsoleMapping(consoleName);
//            } else {
//                consoleNameMapped = consoleName;                               //we were already mapped so let's just use it
//            }
//            
//            if (consoleNameMapped.equals("mame-libretro")) { //yes this is a hack, some users this was still not getting mapped right
//                consoleNameMapped = "mame";
//            }
//            
//                //more user friendly for the log since technically it's looping forever until stopped
//
//             //System.out.println("Console after mapping: " + consoleNameMapped);
//             if (!CliPixel.getSilentMode()) {
//                System.out.println(streamOrWrite.toUpperCase() + " MODE");
//                System.out.println("Console Before Mapping: " + consoleName);
//                System.out.println("Console Mapped: " + consoleNameMapped);
//                if (loop_ == 0) {
//                    System.out.println("# of Times to Loop: null");
//                } else {
//                    System.out.println("# of Times to Loop: " + loop_);
//                }
//                
//                if (text_ != "") System.out.println("alt text if game file not found: " + text_);
//
//                logMe.aLogger.info(streamOrWrite.toUpperCase() + " MODE");
//                logMe.aLogger.info("Console Before Mapping: " + consoleName);
//                logMe.aLogger.info("Console Mapped: " + consoleNameMapped);
//                 if (loop_ == 0) {
//                    logMe.aLogger.info("# of Times to Loop: null");
//                } else {
//                    logMe.aLogger.info("# of Times to Loop: " + loop_);
//                }
//                 if (text_ != "") logMe.aLogger.info("alt text if marquee file not found: " + text_);
//             }
//             
//            String requestedPath = application.getPixel().getPixelHome() + "console" + "\\" + consoleNameMapped;
//             if (!CliPixel.getSilentMode()) {
//                    System.out.println("Looking for: " + requestedPath  + ".png or .gif");
//                    logMe.aLogger.info("Looking for: " + requestedPath  + ".png or .gif");
//            }
//                    
//            if (streamOrWrite.equals("write")) {  //we're in write mode so gif gets the priority if both gif and png exist, never should write mode be used for front end scrolling
//                saveAnimation = true;
//                
//                 //nothing is there so let's use the generic console
//                        
//                        consoleFilePathGIF = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".gif"; 
//                        File consoleFileGIF = new File(consoleFilePathGIF);
//                    
//                        consoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".png"; 
//                        File consoleFilePNG = new File(consoleFilePathPNG);
//                        
//                        if(consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) { 
//
//                              if (WebEnabledPixel.arduino1MatrixConnected) { //if it's connected, let's write the game text to the MAX7219 led matrix, 7 segment, and OLED which is connected to an Arduino
//                                    WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
//                                    logMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
//                              }
//                            
//                            
//                              if (!CliPixel.getSilentMode()) {
//                                    System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
//                                    logMe.aLogger.info("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
//                              }
//                              handleGIF("console", "default-" + consoleNameMapped + ".gif", saveAnimation, loop_);
//                        }      
//                       
//                        else if (consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) { 
//                             
//                              if (WebEnabledPixel.arduino1MatrixConnected) { //if it's connected, let's write the game text to the MAX7219 led matrix, 7 segment, and OLED which is connected to an Arduino
//                                    WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
//                                    logMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
//                              }
//                            
//                              handlePNG(consoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(consoleFilePathPNG)); //mame/default-marquee.png
//                        }
//                       
//                        else {
//                            
//                            //if there was alt text, let's use that and if not alt text, we'll use the default marquee
//                            
//                            if (text_ != "") {  //the game image or png is not there and alt text was supplied so let's scroll that alt text
//                         
//                                    //Pixel pixel = application.getPixel();
//                                    int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
//
//                                    int yTextOffset = -4;
//                                    int fontSize_ = 22;
//                                    
//                                    long speed = 10L;
//                                    speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset
//                                    if (speeddelay_ != 10L) {  //this means another value was set from a parameter for speed so let's use that
//                                        speed = speeddelay_;
//                                    }
//
//                                    if (color_ != "") {
//                                        color = getColorFromHexOrName(color_);
//                                    }
//
//                                    pixel.scrollText(text_, loop_, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
//                            } 
//                            else {
//                            
//                                if (!CliPixel.getSilentMode()) {
//                                     System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
//                                     logMe.aLogger.info("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
//                                }
//                                defaultConsoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-marquee.png"; 
//                                File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
//
//                                if(defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) { 
//                                        handlePNG(defaultConsoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(defaultConsoleFilePathPNG));
//                                }
//                                else {
//                                        if (!CliPixel.getSilentMode()) {
//                                             System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
//                                             System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
//                                             logMe.aLogger.info("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
//                                             logMe.aLogger.info("Skipping LED marquee " + streamOrWrite + ", please check the files");
//                                        }
//                                }
//                            }
//                        }
//                
//                
//            } else {                      //we're in stream mode so png gets the priority if both png and gif exist
//                saveAnimation = false;
//                
//               
//                   //nothing is there so let's use the generic console
//                        
//                        consoleFilePathGIF = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".gif"; 
//                        File consoleFileGIF = new File(consoleFilePathGIF);
//                    
//                        consoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".png"; 
//                        File consoleFilePNG = new File(consoleFilePathPNG);
//                        
//                        if(consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) { 
//                            
//                              if (WebEnabledPixel.arduino1MatrixConnected) { //if it's connected, let's write the game text to the MAX7219 led matrix, 7 segment, and OLED which is connected to an Arduino
//                                    WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
//                                    logMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
//                              }
//                            
//                              if (!CliPixel.getSilentMode()) {
//                                    System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
//                                    logMe.aLogger.info("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
//                              }
//                              handleGIF("console", "default-" + consoleNameMapped + ".gif", saveAnimation, loop_);
//                        }      
//                       
//                        else if (consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) { 
//                             
//                              
//                            if (WebEnabledPixel.arduino1MatrixConnected) { //if it's connected, let's write the game text to the MAX7219 led matrix, 7 segment, and OLED which is connected to an Arduino
//                                    WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
//                                    logMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
//                            }
//                            
//                            
//                            handlePNG(consoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(consoleFilePathPNG)); //mame/default-marquee.png
//                        }
//                       
//                        else {
//                            
//                            //if there was alt text, let's use that and if not alt text, we'll use the default marquee
//                            
//                            if (text_ != "") {  //the game image or png is not there and alt text was supplied so let's scroll that alt text
//                                
//                                    //Pixel pixel = application.getPixel();
//                                    int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
//
//                                    int yTextOffset = -4;
//                                    int fontSize_ = 22;
//                                    
//                                    long speed = 10L;
//                                    speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset
//                                    if (speeddelay_ != 10L) {  //this means another value was set from a parameter for speed so let's use that
//                                        speed = speeddelay_;
//                                    }
//
//                                    if (color_ != "") {
//                                        color = getColorFromHexOrName(color_);
//                                    }
//
//                                    pixel.scrollText(text_, loop_, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
//                                
//                                    /*
//                                    Pixel pixel = application.getPixel();
//                                    int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
//
//                                    int yTextOffset = -4;
//                                    int fontSize_ = 22;
//                                    long speed = 10L;
//
//                                    speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset
//
//                                    if (color_ != "") {
//                                        color = getColorFromHexOrName(color_);
//                                    }
//
//                                    pixel.scrollText(text_, loop_, speed, color,WebEnabledPixel.pixelConnected);
//                                    */
//                            } 
//                            else {
//                            
//                                if (!CliPixel.getSilentMode()) {
//                                     System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
//                                     logMe.aLogger.info("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
//                                }
//                                defaultConsoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-marquee.png"; 
//                                File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
//
//                                if(defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) { 
//                                        handlePNG(defaultConsoleFilePNG, saveAnimation,loop_,"console",FilenameUtils.getName(defaultConsoleFilePathPNG));
//                                }
//                                else {
//                                        if (!CliPixel.getSilentMode()) {
//                                             System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
//                                             System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
//                                             logMe.aLogger.info("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
//                                             logMe.aLogger.info("Skipping LED marquee " + streamOrWrite + ", please check the files");
//                                        }
//                                }
//                            }
//                        }
//                
//            }
//        }
//        
//        else {
//             System.out.println("** ERROR ** URL format incorect, use http://localhost:8080/console/<stream or write>/<platform name>");
//             System.out.println("Example: http://localhost:8080/conosle/write/mame");
//             logMe.aLogger.severe("** ERROR ** URL format incorect, use http://localhost:8080/console/<stream or write>/<platform name>");
//             logMe.aLogger.severe("Example: http://localhost:8080/conosle/write/mame");
//        }
//    }
//
//    public static boolean consoleMatch(String[] arr, String targetValue) {
//	for(String s: arr){
//		if(s.equals(targetValue))
//			return true;
//	}
//	return false;
//    } 
//    
//    /*
//      public String getConsoleNamefromMapping(String originalConsoleName)
//    {
//         String consoleNameMapped = null; //to do set this if null?
//         
//         originalConsoleName = originalConsoleName.toLowerCase();
//         //add the popular ones first to save time
//          
//         switch (originalConsoleName) {
//            
//            case "mame-libretro":
//                 consoleNameMapped = "mame";
//                 return consoleNameMapped;
//            case "mame-mame4all":
//                consoleNameMapped = "mame";
//                 return consoleNameMapped;
//            case "arcade":
//                consoleNameMapped = "mame";
//                 return consoleNameMapped;
//            case "mame-advmame":
//                consoleNameMapped = "neogeo";
//                 return consoleNameMapped;
//            case "atari 2600":
//                consoleNameMapped = "atari2600";
//                return consoleNameMapped;
//            case "nintendo entertainment system":
//                consoleNameMapped = "nes";
//                return consoleNameMapped;
//            case "nintendo 64":
//                consoleNameMapped = "n64";
//                return consoleNameMapped;
//            case "sony playstation":
//                 consoleNameMapped = "psx";
//                 return consoleNameMapped;
//            case "sony playstation 2":
//                consoleNameMapped = "ps2";
//                 return consoleNameMapped;
//            case "sony pocketstation":
//                consoleNameMapped = "psp";
//                 return consoleNameMapped;
//            case "sony psp":
//                consoleNameMapped = "psp";
//                 return consoleNameMapped;
//            case "amstrad cpc":
//                consoleNameMapped = "amstradcpc";
//                 return consoleNameMapped;
//            case "amstrad gx4000":
//                consoleNameMapped = "amstradcpc";
//                 return consoleNameMapped;
//            case "apple II":
//                consoleNameMapped = "apple2";
//                 return consoleNameMapped;
//            case "atari 5200":
//                consoleNameMapped = "atari5200";
//                 return consoleNameMapped;
//            case "atari 7800":
//                consoleNameMapped = "atari7800";
//                 return consoleNameMapped;
//            case "atari jaguar":
//                consoleNameMapped = "atarijaguar";
//                 return consoleNameMapped;
//            case "atari jaguar cd":
//                consoleNameMapped = "atarijaguar";
//                 return consoleNameMapped;
//            case "atari lynx":
//                consoleNameMapped = "atarilynx";
//                 return consoleNameMapped;
//            case "bandai super vision 8000":
//                consoleNameMapped = "wonderswan";
//                 return consoleNameMapped;
//            case "bandai wonderswan":
//                consoleNameMapped = "wonderswan";
//                 return consoleNameMapped;
//            case "bandai wonderswan color":
//                consoleNameMapped = "wonderswancolor";
//                 return consoleNameMapped;
//            case "capcom classics":
//                consoleNameMapped = "capcom";
//                 return consoleNameMapped;
//            case "capcom play pystem":
//                consoleNameMapped = "capcom";
//                 return consoleNameMapped;
//            case "capcom play system II":
//                consoleNameMapped = "capcom";
//                 return consoleNameMapped;
//            case "capcom play system III":
//                consoleNameMapped = "capcom";
//                 return consoleNameMapped;
//            case "colecovision":
//                consoleNameMapped = "coleco";
//                 return consoleNameMapped;
//            case "commodore 128":
//                consoleNameMapped = "c64";
//                 return consoleNameMapped;
//            case "commodore 16 & plus4":
//                consoleNameMapped = "c64";
//                 return consoleNameMapped;
//            case "commodore 64":
//                consoleNameMapped = "c64";
//                 return consoleNameMapped;
//            case "commodore amiga":
//                consoleNameMapped = "amiga";
//                 return consoleNameMapped;
//            case "commodore amiga cd32":
//                consoleNameMapped = "amiga";
//                 return consoleNameMapped;
//            case "commodore vic-20":
//                consoleNameMapped = "c64";
//                 return consoleNameMapped;
//            case "final burn alpha":
//                consoleNameMapped = "fba";
//                 return consoleNameMapped;
//            case "future pinball":
//                consoleNameMapped = "futurepinball";
//                 return consoleNameMapped;
//            case "gce vectrex":
//                consoleNameMapped = "vectrex";
//                 return consoleNameMapped;
//            case "magnavox odyssey":
//                consoleNameMapped = "odyssey";
//                 return consoleNameMapped;
//            case "magnavox odyssey 2":
//                consoleNameMapped = "odyssey";
//                 return consoleNameMapped;
//            case "mattel intellivision":
//                consoleNameMapped = "intellivision";
//                 return consoleNameMapped;
//            case "microsoft msx":
//                consoleNameMapped = "msx";
//                 return consoleNameMapped;
//            case "microsoft msx2":
//                consoleNameMapped = "msx";
//                 return consoleNameMapped;
//            case "microsoft msx2+":
//                consoleNameMapped = "msx";
//                 return consoleNameMapped;
//            case "microsoft windows 3.x":
//                consoleNameMapped = "pc";
//                 return consoleNameMapped;
//            case "misfit mame":
//                consoleNameMapped = "mame";
//                 return consoleNameMapped;
//            case "nec pc engine":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec pc engine-cd":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec pc-8801":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec pc-9801":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec pc-fx":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec supergrafx":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec turbografx-16":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nec turbografx-cd":
//                consoleNameMapped = "pcengine";
//                 return consoleNameMapped;
//            case "nintendo 64dd":
//                consoleNameMapped = "n64";
//                 return consoleNameMapped;
//            case "nintendo famicom":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo famicom disk system":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo game boy":
//                consoleNameMapped = "gb";
//                 return consoleNameMapped;
//            case "nintendo game boy advance":
//                consoleNameMapped = "gba";
//                 return consoleNameMapped;
//            case "nintendo game boy color":
//                consoleNameMapped = "gbc";
//                 return consoleNameMapped;
//            case "nintendo gamecube":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo pokemon mini":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo satellaview":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo super famicom":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo super game boy":
//                consoleNameMapped = "gba";
//                 return consoleNameMapped;
//            case "nintendo virtual boy":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo wii":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo wii u":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "nintendo wiiware":
//                consoleNameMapped = "nes";
//                 return consoleNameMapped;
//            case "panasonic 3do":
//                consoleNameMapped = "3do";
//                 return consoleNameMapped;
//            case "pc games":
//                consoleNameMapped = "pc";
//                 return consoleNameMapped;
//            case "pinball fx2":
//                consoleNameMapped = "futurepinball";
//                 return consoleNameMapped;
//            case "sega 32x":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega cd":
//                consoleNameMapped = "segacd";
//                 return consoleNameMapped;
//            case "sega classics":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega dreamcast":
//                consoleNameMapped = "dreamcast";
//                 return consoleNameMapped;
//            case "sega game gear":
//                consoleNameMapped = "gamegear";
//                 return consoleNameMapped;
//            case "sega genesis":
//                consoleNameMapped = "genesis";
//                 return consoleNameMapped;
//            case "sega hikaru":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega master system":
//                consoleNameMapped = "mastersystem";
//                 return consoleNameMapped;
//            case "sega model 2":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega model 3":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega naomi":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega pico":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega saturn":
//                consoleNameMapped = "saturn";
//                 return consoleNameMapped;
//            case "sega sc-3000":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega sg-1000":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega st-v":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega triforce":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sega vmu":
//                consoleNameMapped = "sega32x";
//                 return consoleNameMapped;
//            case "sinclair zx spectrum":
//                consoleNameMapped = "zxspectrum";
//                 return consoleNameMapped;
//            case "sinclair zx81":
//                consoleNameMapped = "zxspectrum";
//                 return consoleNameMapped;
//            case "snk classics":
//                consoleNameMapped = "neogeo";
//                 return consoleNameMapped;
//            case "snk neo geo aes":
//                consoleNameMapped = "neogeo";
//                 return consoleNameMapped;
//            case "snk neo geo cd":
//                consoleNameMapped = "neogeo";
//                 return consoleNameMapped;
//            case "snk neo geo mvs":
//                consoleNameMapped = "neogeo";
//                 return consoleNameMapped;
//            case "snk neo geo pocket":
//                consoleNameMapped = "ngp";
//                 return consoleNameMapped;
//            case "snk neo geo pocket color":
//                consoleNameMapped = "ngpc";
//                 return consoleNameMapped;
//            case "sony psp minis":
//                consoleNameMapped = "psp";
//                 return consoleNameMapped;
//            case "super nintendo entertainment system":
//                consoleNameMapped = "snes";
//                 return consoleNameMapped;
//            case "visual pinball":
//                consoleNameMapped = "visualpinball";
//                 return consoleNameMapped;
//            default: 
//                 consoleNameMapped = originalConsoleName;    //we didn't find a match so just return the name you got
//                 return consoleNameMapped;
//        }
//    }
//*/
//         //now check for override file but only go there if it exists
//         //give an example file but give it a different name
//        
//       
//   // }
//      
//    public static Color hex2Rgb(String colorStr) 
//    {
//        return new Color(
//                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
//                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
//                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
//    } 
//    
//     private static boolean isHexadecimal(String input) {
//        
//        final Pattern HEXADECIMAL_PATTERN = compile("\\p{XDigit}+");
//        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
//        return matcher.matches();
//        
//    }
//     
//    public static Color getColorFromHexOrName(String ColorStr) {
//        
//        Color color;   
//        if (isHexadecimal(ColorStr) && ColorStr.length() == 6) {  //hex colors are 6 digits
//                   color = hex2Rgb(ColorStr);
//                    if (!CliPixel.getSilentMode()) System.out.println("Hex color value detected");
//                    } else {   //and if not then color text was entered so let's look for a match
//
//                        switch (ColorStr) {
//
//                            case "red":
//                                color = Color.RED;
//                                break;
//                            case "blue":
//                                color = Color.BLUE;
//                                break;
//                            case "cyan":
//                                color = Color.CYAN;
//                                break;
//                            case "gray":
//                                color = Color.GRAY;
//                                break;
//                            case "darkgray":
//                                color = Color.DARK_GRAY;
//                                break;
//                            case "green":
//                                color = Color.GREEN;
//                                break;
//                            case "lightgray":
//                                color = Color.LIGHT_GRAY;
//                                break;
//                            case "magenta":
//                                color = Color.MAGENTA;
//                                break;
//                            case "orange":
//                                color = Color.ORANGE;
//                                break;
//                            case "pink":
//                                color = Color.PINK;
//                                break;
//                            case "yellow":
//                                color = Color.YELLOW;
//                                break;
//                            case "white":
//                                color = Color.WHITE;
//                                break;
//                            default:
//                                color = Color.RED;
//                                if (!CliPixel.getSilentMode()) System.out.println("Invalid color, defaulting to red");
//                        }
//                   }    
//        
//        return color;
//    } 
//     
//    
//}

package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.CliPixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

public class ConsoleHttpHandler extends ImageResourceHttpHandler {
  public ConsoleHttpHandler(WebEnabledPixel application) {
    super(application);
    this.basePath = "";
    this.defaultImageClassPath = "mame";
    this.modeName = "arcade";
  }
  
  private void handlePNG(File arcadeFilePNGFullPath, Boolean saveAnimation, int loop, String consoleNameMapped, String PNGNameWithExtension) throws MalformedURLException, IOException, ConnectionLostException {
    LogMe logMe = LogMe.getInstance();
    Pixel pixel = this.application.getPixel();
    pixel.writeArcadeImage(arcadeFilePNGFullPath, saveAnimation, loop, consoleNameMapped, PNGNameWithExtension, WebEnabledPixel.pixelConnected);
  }
  
  private void handleGIF(String consoleName, String arcadeName, Boolean saveAnimation, int loop) {
    Pixel pixel = this.application.getPixel();
    try {
      pixel.writeArcadeAnimation(consoleName, arcadeName, saveAnimation.booleanValue(), loop, WebEnabledPixel.pixelConnected);
    } catch (NoSuchAlgorithmException ex) {
      Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
  }
  
  protected void writeImageResource(String urlParams) throws IOException, ConnectionLostException {
    Pixel pixel = this.application.getPixel();
    String streamOrWrite = null;
    String consoleName = null;
    String arcadeFilePathPNG = null;
    String arcadeFilePathGIF = null;
    String consoleFilePathPNG = null;
    String consoleFilePathGIF = null;
    String defaultConsoleFilePathPNG = null;
    String consoleNameMapped = null;
    LogMe logMe = null;
    String[] consoleArray = { 
        "mame", "atari2600", "daphne", "nes", "neogeo", "atarilynx", "snes", "atari5200", "atari7800", "atarijaguar", 
        "c64", "genesis", "capcom", "n64", "psp", "psx", "coleco", "dreamcast", "fba", "gb", 
        "gba", "ngp", "ngpc", "odyssey", "saturn", "megadrive", "gbc", "gamegear", "mastersystem", "sega32x", 
        "3do", "msx", "atari800", "pc", "nds", "amiga", "fds", "futurepinball", "amstradcpc", "apple2", 
        "intellivision", "macintosh", "ps2", "pcengine", "segacd", "sg-1000", "ti99", "vectrex", "virtualboy", "visualpinball", 
        "wonderswan", "wonderswancolor", "zinc", "sss", "zmachine", "zxspectrum" };
    boolean saveAnimation = false;
    int loop_ = 0;
    String text_ = "";
    String color_ = null;
    Color color = null;
    int scrollsmooth_ = 1;
    String speed_ = null;
    Long speed = null;
    Long speeddelay_ = Long.valueOf(10L);
    int fontSize_ = 0;
    int yOffset_ = 0;
    int lines_ = 1;
    String font_ = null;
    List<NameValuePair> params = null;
    try {
      params = URLEncodedUtils.parse(new URI(urlParams), "UTF-8");
    } catch (URISyntaxException ex) {
      Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
    for (NameValuePair param : params) {
      switch (param.getName()) {
        case "t":
          text_ = param.getValue();
          break;
        case "text":
          text_ = param.getValue();
           break;
        case "l":
          loop_ = Integer.valueOf(param.getValue()).intValue();
           break;
        case "loop":
          loop_ = Integer.valueOf(param.getValue()).intValue();
           break;
        case "ss":
          scrollsmooth_ = Integer.valueOf(param.getValue()).intValue();
           break;
        case "scrollsmooth":
          scrollsmooth_ = Integer.valueOf(param.getValue()).intValue();
        case "speed":
          speeddelay_ = Long.valueOf(param.getValue());
           break;
        case "c":
          color_ = param.getValue();
           break;
        case "color":
          color_ = param.getValue();
           break;
        case "font":
          font_ = param.getValue();
           break;
        case "size":
          fontSize_ = Integer.valueOf(param.getValue()).intValue();
           break;
        case "yoffset":
          yOffset_ = Integer.valueOf(param.getValue()).intValue();
           break;
        case "lines":
          lines_ = Integer.valueOf(param.getValue()).intValue();
           break;
      } 
    } 
    URI tempURI = null;
    try {
      tempURI = new URI("http://localhost:8080" + urlParams);
    } catch (URISyntaxException ex) {
      Logger.getLogger(ConsoleHttpHandler.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
    String URLPath = tempURI.getPath();
    String[] arcadeURLarray = URLPath.split("/");
    logMe = LogMe.getInstance();
    if (!CliPixel.getSilentMode()) {
      System.out.println("console handler received: " + urlParams);
      LogMe.aLogger.info("console handler received: " + urlParams);
    } 
    if (arcadeURLarray.length == 4) {
      streamOrWrite = arcadeURLarray[2];
      consoleName = arcadeURLarray[3];
      consoleName = consoleName.trim();
      consoleName = consoleName.replace("\n", "").replace("\r", "");
      consoleName = consoleName.toLowerCase();
      if (!consoleMatch(consoleArray, consoleName)) {
        consoleNameMapped = WebEnabledPixel.getConsoleMapping(consoleName);
      } else {
        consoleNameMapped = consoleName;
      } 
      if (consoleNameMapped.equals("mame-libretro"))
        consoleNameMapped = "mame"; 
      if (!CliPixel.getSilentMode()) {
        System.out.println(streamOrWrite.toUpperCase() + " MODE");
        System.out.println("Console Before Mapping: " + consoleName);
        System.out.println("Console Mapped: " + consoleNameMapped);
        if (loop_ == 0) {
          System.out.println("# of Times to Loop: null");
        } else {
          System.out.println("# of Times to Loop: " + loop_);
        } 
        if (text_ != "")
          System.out.println("alt text if game file not found: " + text_); 
        LogMe.aLogger.info(streamOrWrite.toUpperCase() + " MODE");
        LogMe.aLogger.info("Console Before Mapping: " + consoleName);
        LogMe.aLogger.info("Console Mapped: " + consoleNameMapped);
        if (loop_ == 0) {
          LogMe.aLogger.info("# of Times to Loop: null");
        } else {
          LogMe.aLogger.info("# of Times to Loop: " + loop_);
        } 
        if (text_ != "")
          LogMe.aLogger.info("alt text if marquee file not found: " + text_); 
      } 
      String requestedPath = this.application.getPixel().getPixelHome() + "console\\" + consoleNameMapped;
      if (!CliPixel.getSilentMode()) {
        System.out.println("Looking for: " + requestedPath + ".png or .gif");
        LogMe.aLogger.info("Looking for: " + requestedPath + ".png or .gif");
      } 
      if (streamOrWrite.equals("write")) {
        saveAnimation = true;
        consoleFilePathGIF = this.application.getPixel().getPixelHome() + "console/default-" + consoleNameMapped + ".gif";
        File consoleFileGIF = new File(consoleFilePathGIF);
        consoleFilePathPNG = this.application.getPixel().getPixelHome() + "console/default-" + consoleNameMapped + ".png";
        File consoleFilePNG = new File(consoleFilePathPNG);
        if (consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) {
          if (WebEnabledPixel.arduino1MatrixConnected) {
            WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
            LogMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
          } 
          if (!CliPixel.getSilentMode()) {
            System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
            LogMe.aLogger.info("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
          } 
          handleGIF("console", "default-" + consoleNameMapped + ".gif", Boolean.valueOf(saveAnimation), loop_);
        } else if (consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) {
          if (WebEnabledPixel.arduino1MatrixConnected) {
            WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
            LogMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
          } 
          handlePNG(consoleFilePNG, Boolean.valueOf(saveAnimation), loop_, "console", FilenameUtils.getName(consoleFilePathPNG));
        } else if (text_ != "") {
          int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
          speed = Long.valueOf(10L);
          speed = Long.valueOf(WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID));
          if (speeddelay_.longValue() != 10L)
            speed = speeddelay_; 
          if (color_ != null)
            color = getColorFromHexOrName(color_); 
          pixel.scrollText(text_, loop_, speed.longValue(), color, WebEnabledPixel.pixelConnected, scrollsmooth_);
        } else {
          if (!CliPixel.getSilentMode()) {
            System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
            LogMe.aLogger.info("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
          } 
          defaultConsoleFilePathPNG = this.application.getPixel().getPixelHome() + "console/default-marquee.png";
          File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
          if (defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) {
            handlePNG(defaultConsoleFilePNG, Boolean.valueOf(saveAnimation), loop_, "console", FilenameUtils.getName(defaultConsoleFilePathPNG));
          } else if (!CliPixel.getSilentMode()) {
            System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
            System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
            LogMe.aLogger.info("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
            LogMe.aLogger.info("Skipping LED marquee " + streamOrWrite + ", please check the files");
          } 
        } 
      } else {
        saveAnimation = false;
        consoleFilePathGIF = this.application.getPixel().getPixelHome() + "console/default-" + consoleNameMapped + ".gif";
        File consoleFileGIF = new File(consoleFilePathGIF);
        consoleFilePathPNG = this.application.getPixel().getPixelHome() + "console/default-" + consoleNameMapped + ".png";
        File consoleFilePNG = new File(consoleFilePathPNG);
        if (consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) {
          if (WebEnabledPixel.arduino1MatrixConnected) {
            WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
            LogMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
          } 
          if (!CliPixel.getSilentMode()) {
            System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
            LogMe.aLogger.info("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
          } 
          handleGIF("console", "default-" + consoleNameMapped + ".gif", Boolean.valueOf(saveAnimation), loop_);
        } else if (consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) {
          if (WebEnabledPixel.arduino1MatrixConnected) {
            WebEnabledPixel.writeArduino1Matrix(WebEnabledPixel.getConsoleMetaData(consoleName));
            LogMe.aLogger.info("Accessory Call Console: " + WebEnabledPixel.getConsoleMetaData(consoleName));
          } 
          handlePNG(consoleFilePNG, Boolean.valueOf(saveAnimation), loop_, "console", FilenameUtils.getName(consoleFilePathPNG));
        } else if (text_ != "") {
          if (color_ == null) {
            if (WebEnabledPixel.getTextColor().equals("random")) {
              color = WebEnabledPixel.getRandomColor();
            } else {
              color = WebEnabledPixel.getColorFromHexOrName(WebEnabledPixel.getTextColor());
            } 
          } else {
            color = WebEnabledPixel.getColorFromHexOrName(color_);
          } 
          int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
          speed = Long.valueOf(WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID));
          if (speed_ != null) {
            speed = Long.valueOf(speed_);
            if (speed.longValue() == 0L)
              speed = Long.valueOf(10L); 
          } 
          if (scrollsmooth_ == 0) {
            String scrollSpeedSettings = WebEnabledPixel.getTextScrollSpeed();
            scrollsmooth_ = WebEnabledPixel.getScrollingSmoothSpeed(scrollSpeedSettings);
          } 
          if (font_ == null)
            font_ = WebEnabledPixel.getDefaultFont(); 
          this.application.getPixel();
          Pixel.setFontFamily(font_);
          if (yOffset_ == 0)
            yOffset_ = WebEnabledPixel.getDefaultyTextOffset(); 
          this.application.getPixel();
          Pixel.setYOffset(yOffset_);
          if (fontSize_ == 0)
            fontSize_ = WebEnabledPixel.getDefaultFontSize(); 
          this.application.getPixel();
          Pixel.setFontSize(fontSize_);
          
        if (lines_ == 2) 
            Pixel.setDoubleLine(true);
        else if (lines_ == 4)
            Pixel.setFourLine(true);
        else
            Pixel.setDoubleLine(false); //don't forget to set it back
          
          
          pixel.scrollText(text_, loop_, speed.longValue(), color, WebEnabledPixel.pixelConnected, scrollsmooth_);
        } 
        
        else {
          if (!CliPixel.getSilentMode()) {
            System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
            LogMe.aLogger.info("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
          } 
          defaultConsoleFilePathPNG = this.application.getPixel().getPixelHome() + "console/default-marquee.png";
          File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
          if (defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) {
            handlePNG(defaultConsoleFilePNG, Boolean.valueOf(saveAnimation), loop_, "console", FilenameUtils.getName(defaultConsoleFilePathPNG));
          } else if (!CliPixel.getSilentMode()) {
            System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
            System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
            LogMe.aLogger.info("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
            LogMe.aLogger.info("Skipping LED marquee " + streamOrWrite + ", please check the files");
          } 
        } 
      } 
    } else {
      System.out.println("** ERROR ** URL format incorect, use http://localhost:8080/console/<stream or write>/<platform name>");
      System.out.println("Example: http://localhost:8080/conosle/write/mame");
      LogMe.aLogger.severe("** ERROR ** URL format incorect, use http://localhost:8080/console/<stream or write>/<platform name>");
      LogMe.aLogger.severe("Example: http://localhost:8080/conosle/write/mame");
    } 
  }
  
  public static boolean consoleMatch(String[] arr, String targetValue) {
    for (String s : arr) {
      if (s.equals(targetValue))
        return true; 
    } 
    return false;
  }
  
  public static Color hex2Rgb(String colorStr) {
    return new Color(
        Integer.valueOf(colorStr.substring(0, 2), 16).intValue(), 
        Integer.valueOf(colorStr.substring(2, 4), 16).intValue(), 
        Integer.valueOf(colorStr.substring(4, 6), 16).intValue());
  }
  
  private static boolean isHexadecimal(String input) {
    Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");
    Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
    return matcher.matches();
  }
  
  public static Color getColorFromHexOrName(String ColorStr) {
    Color color;
    if (isHexadecimal(ColorStr) && ColorStr.length() == 6) {
      color = hex2Rgb(ColorStr);
      if (!CliPixel.getSilentMode())
        System.out.println("Hex color value detected"); 
    } else {
      switch (ColorStr) {
        case "red":
          color = Color.RED;
          return color;
        case "blue":
          color = Color.BLUE;
          return color;
        case "cyan":
          color = Color.CYAN;
          return color;
        case "gray":
          color = Color.GRAY;
          return color;
        case "darkgray":
          color = Color.DARK_GRAY;
          return color;
        case "green":
          color = Color.GREEN;
          return color;
        case "lightgray":
          color = Color.LIGHT_GRAY;
          return color;
        case "magenta":
          color = Color.MAGENTA;
          return color;
        case "orange":
          color = Color.ORANGE;
          return color;
        case "pink":
          color = Color.PINK;
          return color;
        case "yellow":
          color = Color.YELLOW;
          return color;
        case "white":
          color = Color.WHITE;
          return color;
      } 
      color = Color.RED;
      if (!CliPixel.getSilentMode())
        System.out.println("Invalid color, defaulting to red"); 
    } 
    return color;
  }
}

