
package org.onebeartoe.pixel;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.onebeartoe.pixel.hardware.Pixel;

/* Then in your classes you will be using it like this:

 class MyClass1 {
    LogMe logMe1 = new LogMe();
    Logger logger2 = logMe1.getLogger();
    logger.info("X 01");
} */

public class LogMe {        

  public static final Logger aLogger = Logger.getLogger("myLogger");
    private static LogMe instance = null;
    public static LogMe getInstance(){
        if(instance==null){
            getLoggerReady();
            instance = new LogMe();
        }
        return instance;
    }
    private static void getLoggerReady(){
        try{
            
            //FileHandler fh = new FileHandler(Pixel.getHomePath() + "pixelcade.log"); //this is returning null
            int FILE_SIZE = 5000 * 1024;  //limit log file to 5 MB
            FileHandler fh = new FileHandler("pixelweb.log", FILE_SIZE, 1, false); //overwrite log each time pixelweb is launched
            //FileHandler fh = new FileHandler("pixelweb.log");
            //fh.setFormatter(new SimpleFormatter());
            fh.setFormatter(new PixelLogFormatter());
            aLogger.addHandler(fh);
            aLogger.setUseParentHandlers(false);
            aLogger.setLevel(Level.ALL);
        } catch(Exception e){
            System.out.print("Error: Logger creation issue: "+e);
        }           
    }
   
}