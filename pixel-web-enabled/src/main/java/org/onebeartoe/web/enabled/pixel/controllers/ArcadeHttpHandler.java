
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
import org.apache.commons.io.FilenameUtils;

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
    
    private void handlePNG(File file, Boolean saveAnimation) throws MalformedURLException, IOException, ConnectionLostException {
        
        URL url = null; 
        BufferedImage image;
        url = file.toURI().toURL();
        image = ImageIO.read(url);
        System.out.println("PNG image found: " + url.toString());
        
        Pixel pixel = application.getPixel();
        pixel.stopExistingTimer();  //a timer could be running from a gif so we need to kill it here
        
        //if (saveAnimation && pixel.getPIXELHardwareID().substring(0,4).equals("PIXL")) {
        if (saveAnimation) {
            
            pixel.interactiveMode();
            pixel.writeMode(10);
            
             try {
                   pixel.writeImagetoMatrix(image, pixel.KIND.width, pixel.KIND.height);
            } catch (ConnectionLostException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
            } 
             
            try {
                Thread.sleep(100); //this may not be needed but was causing a problem on the writes for the gif animations so adding here to be safe
                //TO DO will a smaller delay still work too?
            } catch (InterruptedException ex) {
                Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
             pixel.playLocalMode();
            
        } else {
            
            pixel.interactiveMode();
            pixel.writeImagetoMatrix(image, pixel.KIND.width, pixel.KIND.height); //to do add save parameter here
        }
        
    }
    
    private void handleGIF(String consoleName, String arcadeName, Boolean saveAnimation) {
         
       
         
        Pixel pixel = application.getPixel();
        
        try {
            pixel.writeArcadeAnimation(consoleName, arcadeName , saveAnimation);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        //Sleeper.sleepo(15);
        //Sleeper.sleepo(100);
    }
    
    @Override
    protected void writeImageResource(String urlParams) throws IOException, ConnectionLostException
    {
        
    	
        
        
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
                    
 	boolean saveAnimation = false;
        //String extension=null;
        
        //to do the slashes will screw up this logic, we could remove the / first or switch to another convention
        
 	
        
        String [] arcadeURLarray = urlParams.split("/"); 
        //String [] arcadeURLarray = urlParams.split("&"); 
        
        //&m=stream or write , &c=console , &g=game
    	
    	System.out.println("arcade handler received " + urlParams);
        
        /* for (int i=0; i < arcadeURLarray.length; i++) { 
            System.out.println("Str["+i+"]:"+arcadeURLarray[i]); 
        } 
        System.out.println(arcadeURLarray.length); //should be 5
        */
        
        if (arcadeURLarray.length == 5) {
        	
        	    streamOrWrite = arcadeURLarray[2];
        	    consoleName = arcadeURLarray[3];
        	    arcadeName = arcadeURLarray[4];
                    
            //to do add code to remove " " in case the user entered those
                    
            //arcadeName could be a full path or just a name, we need to handle both
            String name1 = FilenameUtils.getName(arcadeName);
            String name2 = FilenameUtils.getBaseName(arcadeName);
            //String name3 = FilenameUtils.getExtension(arcadeName);
                    
            //let's make sure this file exists and skip if not
            //arcadeNameExtension = FilenameUtils.getExtension(arcadeName); 
            arcadeNameOnly = FilenameUtils.getBaseName(arcadeName); //stripping out the extension
            
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
             
             consoleNameMapped = getConsoleNamefromMapping(consoleName); //will return original console if no matcn
            
             //System.out.println("Console after mapping: " + consoleNameMapped);
             System.out.println(streamOrWrite.toUpperCase() + " MODE");
             System.out.println("Console Before Mapping: " + consoleName);
             System.out.println("Console Mapped: " + consoleNameMapped);
             System.out.println("Game Name Only: " +  arcadeNameOnly);
           
            //now let's decide if we're going to find the png or gif 
            
             arcadeFilePathPNG = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNameOnly +".png";
             File arcadeFilePNG = new File(arcadeFilePathPNG);
             
             arcadeFilePathGIF = application.getPixel().getPixelHome() + consoleNameMapped + "/" + arcadeNameOnly +".gif";
             File arcadeFileGIF = new File(arcadeFilePathGIF);
             
             //System.out.println("delete PNG path " + arcadeFilePathPNG);
             //System.out.println("delete GiF path " + arcadeFilePathGIF);
            
            if (streamOrWrite.equals("write")) {  //we're in write mode so gif gets the priority if both gif and png exist, never should write mode be used for front end scrolling
                saveAnimation = true;
                
               
                if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                        //System.out.println("delete went here GIF");
                        handleGIF(consoleNameMapped, arcadeNameOnly +".gif", saveAnimation);
                }
                        
                else if(arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) { 
                        //System.out.println("delete went here PNG");
                        handlePNG(arcadeFilePNG, saveAnimation);
                }
                else { //nothing is there so let's use the console
                        
                        consoleFilePathGIF = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".gif"; 
                        File consoleFileGIF = new File(consoleFilePathGIF);
                    
                        consoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".png"; 
                        File consoleFilePNG = new File(consoleFilePathPNG);
                       
                        //System.out.println("delete console gif: " + consoleFilePathGIF);
                        
                        if(consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) { 

                              System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
                              handleGIF("console", "default-" + consoleNameMapped + ".gif", saveAnimation);
                        }
                                
                       
                        else if (consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) { 
                             
                              handlePNG(consoleFilePNG, saveAnimation);
                        }
                        else {
                               System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
                               defaultConsoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-marquee.png"; 
                               File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
                               
                               if(defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) { 
                                       handlePNG(defaultConsoleFilePNG, saveAnimation);
                               }
                               else {
                                       System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
                                       System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
                               }
                        }
                }
                
            } else {                      //we're in stream mode so png gets the priority if both png and gif exist
                saveAnimation = false;
                
                if(arcadeFilePNG.exists() && !arcadeFilePNG.isDirectory()) { 
                        //System.out.println("delete went here PNG");
                        handlePNG(arcadeFilePNG, saveAnimation);
                }
                else if (arcadeFileGIF.exists() && !arcadeFileGIF.isDirectory()) {
                        //System.out.println("delete went here GIF");
                        handleGIF(consoleNameMapped, arcadeNameOnly +".gif", saveAnimation);
                }
                else { //nothing is there so let's use the console
                    
                        consoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".png"; 
                        File consoleFilePNG = new File(consoleFilePathPNG);
                        
                        consoleFilePathGIF = application.getPixel().getPixelHome() + "console/" + "default-" + consoleNameMapped + ".gif"; 
                        File consoleFileGIF = new File(consoleFilePathGIF);
                       
                        //System.out.println("delete console gif: " + consoleFilePathGIF);
                    
                        if(consoleFilePNG.exists() && !consoleFilePNG.isDirectory()) { 

                              handlePNG(consoleFilePNG, saveAnimation);
                        }
                        else if(consoleFileGIF.exists() && !consoleFileGIF.isDirectory()) { 

                                System.out.println("PNG default console LED Marquee file not found, looking for GIF version: " + consoleFilePathPNG);
                                handleGIF("console", "default-" + consoleNameMapped + ".gif", saveAnimation);
                        }
                        else {
                               System.out.println("GIF default console LED Marquee file not found, looking for default marquee: " + consoleFilePathGIF);
                               defaultConsoleFilePathPNG = application.getPixel().getPixelHome() + "console/" + "default-marquee.png"; 
                               File defaultConsoleFilePNG = new File(defaultConsoleFilePathPNG);
                               
                               if(defaultConsoleFilePNG.exists() && !defaultConsoleFilePNG.isDirectory()) { 
                                       handlePNG(defaultConsoleFilePNG, saveAnimation);
                               }
                               else {
                                       System.out.println("Default console LED Marquee file not found: " + defaultConsoleFilePathPNG);
                                       System.out.println("Skipping LED marquee " + streamOrWrite + ", please check the files");
                               }
                        }
                }
            }
            
            
            
            
        }
        
        else {
             System.out.println("** ERROR ** URL format incorect, use http://localhost:8080/arcade/<stream or write>/<platform name>/<game name .gif or .png>");
             System.out.println("Example: http://localhost:8080/arcade/write/mame/pacman.png or http://localhost:8080/arcade/stream/atari2600/digdug.gif");
        }
    }

     
      public String getConsoleNamefromMapping(String originalConsoleName)
    {
         String consoleNameMapped = null; //to do set this if null?
         
         originalConsoleName = originalConsoleName.toLowerCase();
         //add the popular ones first to save time
         //but thsi is not good as is as if we have a retropi, it will go all the way through
          
         switch (originalConsoleName) {
            case "amstrad cpc":
                consoleNameMapped = "amstradcpc";
                 return consoleNameMapped;
            case "amstrad gx4000":
                consoleNameMapped = "amstradcpc";
                 return consoleNameMapped;
            case "apple II":
                consoleNameMapped = "apple2";
                 return consoleNameMapped;
            case "atari 2600":
                consoleNameMapped = "atari2600";
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
            case "nintendo 64":
                consoleNameMapped = "n64";
                 return consoleNameMapped;
            case "nintendo 64dd":
                consoleNameMapped = "n64";
                 return consoleNameMapped;
            case "nintendo entertainment system":
                consoleNameMapped = "nes";
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
    }
         //now check for override file but only go there if it exists
         //give an example file but give it a different name
        
       
   // }
    
}
