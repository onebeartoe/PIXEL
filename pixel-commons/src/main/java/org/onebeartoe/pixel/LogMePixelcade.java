
package org.onebeartoe.pixel;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.onebeartoe.pixel.hardware.Pixel;


public class LogMePixelcade {        

  public static final Logger pLogger = Logger.getLogger("pixelcadeLogger");
    private static LogMePixelcade instance = null;
    public static LogMePixelcade getInstance(){
        if(instance==null){
            getLoggerReady();
            instance = new LogMePixelcade();
        }
        return instance;
    }
    private static void getLoggerReady(){
        try{
            
            //FileHandler fh = new FileHandler(Pixel.getHomePath() + "pixelcade.log"); //this is returning null
            int FILE_SIZE = 2000 * 1024;  //limit log file to 2 MB
            FileHandler fh = new FileHandler("pixelcade.log", FILE_SIZE, 1, true);
            //fh.setFormatter(new SimpleFormatter());
            fh.setFormatter(new PixelLogFormatter());
            pLogger.addHandler(fh);
            pLogger.setUseParentHandlers(false);
            pLogger.setLevel(Level.ALL);
            
        } catch(Exception e){
            System.out.print("Error: Logger creation issue: "+e);
        }           
    }
   
}