package org.onebeartoe.web.enabled.pixel.controllers;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.onebeartoe.pixel.hardware.Pixel;

import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

public class LCDPixelcade {
    
    private static String DEFAULT_COMMAND = "sudo fbi lcdmarquees/pixelcade.png -T 1  --noverbose --nocomments --fixwidth -a";
    private static final String JPG_COMMAND = "sudo fbi lcdmarquees/${named}.jpg -T 1 --noverbose --nocomments --fixwidth -a";
    private static final String PNG_COMMAND = "sudo fbi lcdmarquees/${named}.png -T 1 --noverbose --nocomments --fixwidth -a";
    private static final String SLIDESHOW = "sudo fbi lcdmarquees/* -T 1 -t 2 --noverbose --nocomments --fixwidth -a";
    private static final String RESET_COMMAND = "sudo killall -9 fbi;";
    private static final String MARQUEE_PATH = "lcdmarquees/";
    private static final String ENGINE_PATH = "lcdmarquees/";
    //private static String pixelHome = System.getProperty("user.dir") + "\\";
    private String pixelHome = Pixel.getHomePath();
    public static String theCommand = DEFAULT_COMMAND;
    public static  WindowsLCD windowsLCD = null;
    public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static void main(String[] args) {

        String shell = "bash";
        if(isWindows)
            windowsLCD = new WindowsLCD();
        
        boolean haveFBI = new File(ENGINE_PATH).exists();
        //boolean haveExtraDisplay = new File("/dev/fb1").exists();

        if (!haveFBI && WebEnabledPixel.isUnix()) {
            System.out.print("Image engine failure.\n");
        }
        if (args.length > -1) {
            try {

                if(args.length == 1)
                displayImage(args[args.length - 1]);
                else
                    displayImage(args[0],args[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                displayImage(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isWindows) {
            shell = "CMD.EXE";
            System.out.print("Shell: ${shell}");
        }
    }

    public void setLCDFont(Font font) {
        if(!isWindows) return;

        if(windowsLCD == null)
            windowsLCD = new WindowsLCD();

        windowsLCD.marqueePanel.setFont(font);
    }
    static public void displayImage(String named, String system) throws IOException {
        if(isWindows) {
            if(windowsLCD == null)
            windowsLCD = new WindowsLCD();
            
            windowsLCD.displayImage(named, system);
            return;
        }
//        if (new File(String.format("/home/pi/pixelcade/lcdmarquees/console/default-%s.png", system)).exists())
//            DEFAULT_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/console/default-" + system + ".png -T 1  --noverbose --nocomments --fixwidth -a";
         if (new File(String.format("lcdmarquees/console/default-%s.png", system)).exists())
            DEFAULT_COMMAND = "sudo fbi lcdmarquees/console/default-" + system + ".png -T 1  --noverbose --nocomments --fixwidth -a";

        theCommand = DEFAULT_COMMAND;
        displayImage(named);
    }

    static public void displayImage(String named) throws IOException {  //note this is Pi/linux only!
        if (named == null) return;

        //System.out.print("image: " + named +"\n");
       // String theCommand = DEFAULT_COMMAND;

        if (named != null) if (named.contains("slideshow")) {
            theCommand = SLIDESHOW;
        } else if (new File(MARQUEE_PATH + named + ".png").exists()) {
            theCommand = PNG_COMMAND.replace("${named}", named);
        } else if (new File(MARQUEE_PATH + named + ".jpg").exists())
            theCommand = JPG_COMMAND.replace("${named}", named);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", RESET_COMMAND + theCommand);

        Process process = builder.start();
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert exitCode == 0;
    }
    
    static public void scrollText(String message, Font font, Color color, int speed) {
    if(isWindows){
        if(windowsLCD == null)
            windowsLCD = new WindowsLCD();
        windowsLCD.scrollText(message,font,color, speed);
        return;
    }
}
    
    
    
}



//package org.onebeartoe.web.enabled.pixel.controllers;
//
//import javax.swing.*;
//import java.io.File;
//import java.io.IOException;
//import java.awt.Frame;
//import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
//
//public class LCDPixelcade {
//
//
////    private static String DEFAULT_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/pixelcade.png -T 1  --noverbose --nocomments --fixwidth -a";
////    private static final String JPG_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/${named}.jpg -T 1 --noverbose --nocomments --fixwidth -a";
////    private static final String PNG_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/${named}.png -T 1 --noverbose --nocomments --fixwidth -a";
////    private static final String SLIDESHOW = "sudo fbi /home/pi/pixelcade/lcdmarquees/* -T 1 -t 2 --noverbose --nocomments --fixwidth -a";
////    private static final String RESET_COMMAND = "sudo killall -9 fbi;";
////    private static final String MARQUEE_PATH = "/home/pi/pixelcade/lcdmarquees/";
////    private static final String ENGINE_PATH = "/home/pi/pixelcade/lcdmarquees/";
//    private static String DEFAULT_COMMAND = "sudo fbi lcdmarquees/pixelcade.png -T 1  --noverbose --nocomments --fixwidth -a";
//    private static final String JPG_COMMAND = "sudo fbi lcdmarquees/${named}.jpg -T 1 --noverbose --nocomments --fixwidth -a";
//    private static final String PNG_COMMAND = "sudo fbi lcdmarquees/${named}.png -T 1 --noverbose --nocomments --fixwidth -a";
//    private static final String SLIDESHOW = "sudo fbi lcdmarquees/* -T 1 -t 2 --noverbose --nocomments --fixwidth -a";
//    private static final String RESET_COMMAND = "sudo killall -9 fbi;";
//    private static final String MARQUEE_PATH = "lcdmarquees/";
//    private static final String ENGINE_PATH = "lcdmarquees/";
//    public static String theCommand = DEFAULT_COMMAND;
//    public static  WindowsLCD windowsLCD = null;
//    //public static  WindowsLCD windowsLCD = new WindowsLCD();
//    public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
//    public static void main(String[] args) {
//
//        String shell = "bash";
//        
//        boolean haveFBI = new File(ENGINE_PATH).exists();
//        //boolean haveExtraDisplay = new File("/dev/fb1").exists();
//
//        if (!haveFBI && WebEnabledPixel.isUnix()) {
//            System.out.print("Image engine failure.\n");
//        }
//        if (args.length > -1) {
//            try {
//
//                if(args.length == 1)
//                displayImage(args[args.length - 1]);
//                else
//                    displayImage(args[0],args[1]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                displayImage(null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (isWindows) {
//            shell = "CMD.EXE";
//            System.out.print("Shell: ${shell}");
//        }
//    }
//
//    static public void displayImage(String named, String system) throws IOException {
//        if(isWindows) {
//            new WindowsLCD().displayImage(named, system);
//
//            
//           // if(windowsLCD == null)
//           // windowsLCD = new WindowsLCD();
//           // 
//           // windowsLCD.displayImage(named, system);
//            return;
//        }
////        if (new File(String.format("/home/pi/pixelcade/lcdmarquees/console/default-%s.png", system)).exists())
////            DEFAULT_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/console/default-" + system + ".png -T 1  --noverbose --nocomments --fixwidth -a";
//         if (new File(String.format("lcdmarquees/console/default-%s.png", system)).exists())
//            DEFAULT_COMMAND = "sudo fbi lcdmarquees/console/default-" + system + ".png -T 1  --noverbose --nocomments --fixwidth -a";
//
//        theCommand = DEFAULT_COMMAND;
//        displayImage(named);
//    }
//
//    static public void  displayImage(String named) throws IOException {  //note this is Pi/linux only!
//        if (named == null) return;
//
//        //System.out.print("image: " + named +"\n");
//       // String theCommand = DEFAULT_COMMAND;
//
//        if (named != null) if (named.contains("slideshow")) {
//            theCommand = SLIDESHOW;
//        } else if (new File(MARQUEE_PATH + named + ".png").exists()) {
//            theCommand = PNG_COMMAND.replace("${named}", named);
//        } else if (new File(MARQUEE_PATH + named + ".jpg").exists())
//            theCommand = JPG_COMMAND.replace("${named}", named);
//
//        ProcessBuilder builder = new ProcessBuilder();
//        builder.command("sh", "-c", RESET_COMMAND + theCommand);
//
//        Process process = builder.start();
//        int exitCode = 0;
//        try {
//            exitCode = process.waitFor();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        assert exitCode == 0;
//    }
//}
