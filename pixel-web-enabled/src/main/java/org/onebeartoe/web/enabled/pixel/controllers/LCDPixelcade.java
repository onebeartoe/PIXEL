package org.onebeartoe.web.enabled.pixel.controllers;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

public class LCDPixelcade {


//    private static String DEFAULT_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/pixelcade.png -T 1  --noverbose --nocomments --fixwidth -a";
//    private static final String JPG_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/${named}.jpg -T 1 --noverbose --nocomments --fixwidth -a";
//    private static final String PNG_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/${named}.png -T 1 --noverbose --nocomments --fixwidth -a";
//    private static final String SLIDESHOW = "sudo fbi /home/pi/pixelcade/lcdmarquees/* -T 1 -t 2 --noverbose --nocomments --fixwidth -a";
//    private static final String RESET_COMMAND = "sudo killall -9 fbi; killall -9 qml;";
//    private static final String MARQUEE_PATH = "/home/pi/pixelcade/lcdmarquees/";
//    private static final String ENGINE_PATH = "/home/pi/pixelcade/lcdmarquees/";
    private static String pixelHome = "/home/pi/pixelcade/";
    private static String sep = "/";
    private static String fontPath = pixelHome + "fonts/";
    private static int loops = 0;
    private static String fontColor = "purple";
    private static String DEFAULT_COMMAND = "sudo fbi " + pixelHome + "lcdmarquees/pixelcade.png -T 1  -d /dev/fb0 --noverbose --nocomments --fixwidth -a";
    private static final String JPG_COMMAND = "sudo fbi " + pixelHome + "lcdmarquees/${named}.jpg -T 1  -d /dev/fb0 --noverbose --nocomments --fixwidth -a";
    private static final String PNG_COMMAND = "sudo fbi "+ pixelHome + "lcdmarquees/${named}.png -T 1  -d /dev/fb0 --noverbose --nocomments --fixwidth -a";
    private static final String GIF_COMMAND = pixelHome + "gsho  -platform linuxfb " + pixelHome + "${system}/${named}.gif";
    private static final String SLIDESHOW = "sudo fbi " + pixelHome + "lcdmarquees/* -T 1 -d /f]dev/fb0 -t 2 --noverbose --nocomments --fixwidth -a";
    private static final String RESET_COMMAND = "sudo killall -9 fbi; killall -9 qml;";
    private static final String MARQUEE_PATH = pixelHome + "lcdmarquees/";
    private static final String ENGINE_PATH = "/usr/bin/fbi";
    static String NOT_FOUND = pixelHome + "lcdmarquees/" + "pixelcade.png";
    public static String theCommand = DEFAULT_COMMAND;
    public static String gifSystem = "";
    public static  WindowsLCD windowsLCD = null;
    public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static boolean  doGif = false;
    public static void main(String[] args) {

        String shell = "bash";
        if(isWindows){
            windowsLCD = new WindowsLCD();
            pixelHome =  System.getProperty("user.dir") + "\\";
            sep = "\\";
        }


        
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

public void setLCDFont(Font font, String fontFilename) {
        if(!isWindows) {
            this.fontPath = fontFilename; 
            return;

        }

        if(windowsLCD == null)
            windowsLCD = new WindowsLCD();

        windowsLCD.marqueePanel.setFont(font);
        windowsLCD.marqueePanel.setFontFileName(fontFilename);
        if(!windowsLCD.marqueePanel.didHi)
        windowsLCD.marqueePanel.setMessage("Welcome to the House of Fun!");
    }

    public void setNumLoops(int loops){
        this.loops = loops;
        if(isWindows && windowsLCD != null)
            windowsLCD.marqueePanel.setNumLoops(loops);
    }

    static public void displayImage(String named, String system) throws IOException {
        if(!WebEnabledPixel.getLCDMarquee().contains("yes"))
            return;

        if(isWindows) {
            if(windowsLCD == null)
            windowsLCD = new WindowsLCD();
            
            windowsLCD.displayImage(named, system);
            return;
        }
//        if (new File(String.format("/home/pi/pixelcade/lcdmarquees/console/default-%s.png", system)).exists())
//            DEFAULT_COMMAND = "sudo fbi /home/pi/pixelcade/lcdmarquees/console/default-" + system + ".png -T 1  --noverbose --nocomments --fixwidth -a";
        System.out.print("System: " + system +"\n");

	String marqueePath = NOT_FOUND;
        if (new File(String.format("%slcdmarquees/console/default-%s.png",pixelHome, system)).exists()){
            DEFAULT_COMMAND = "sudo fbi" + pixelHome + "lcdmarquees/console/default-" + system + ".png -T 1 -/d /dev/fb0  --noverbose --nocomments --fixwidth -a";
	marqueePath = String.format("%slcdmarquees/console/default-%s.png",pixelHome, system);
	}

        doGif = new File(String.format("%s%s/%s.gif",pixelHome, system,named)).exists();
        gifSystem = system;
	theCommand = DEFAULT_COMMAND;
        displayImage(named);
    	if(marqueePath.contains(NOT_FOUND)){
            scrollText(String.format("%s - %s...",named, system), new Font("Helvetica", Font.PLAIN, 268), Color.magenta, 1);
            return;
        }
	}

    static public void  displayImage(String named) throws IOException {  //note this is Pi/linux only!
        if (named == null) return;

        System.out.print("image: " + named +"\n");
       //theCommand = DEFAULT_COMMAND;

        if (named != null) if (named.contains("slideshow")) {
            theCommand = SLIDESHOW;
	} else if (new File(MARQUEE_PATH + named + ".png").exists()) {
            theCommand = PNG_COMMAND.replace("${named}", named);
        } else if (new File(MARQUEE_PATH + named + ".png").exists()) {
            theCommand = PNG_COMMAND.replace("${named}", named);
        } else if (new File(MARQUEE_PATH + named + ".jpg").exists())
            theCommand = JPG_COMMAND.replace("${named}", named);
	
       if(doGif){
	  theCommand = GIF_COMMAND.replace("${named}", named).replace("${system}", gifSystem);
	  doGif = false;
 	}

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", RESET_COMMAND + theCommand);
        System.out.println("Running cmd: " + "sh -c " +  RESET_COMMAND + theCommand);
        Process process = builder.start();
	
       // int exitCode = 0;
       // try {
       //     exitCode = process.waitFor();
       // } catch (InterruptedException e) {
       //     e.printStackTrace();
       // }
       // assert exitCode == 0;
       
    }

    static public void scrollText(String message, Font font, Color color, int speed) {
        if(isWindows){
            if(windowsLCD == null)
                windowsLCD = new WindowsLCD();

            windowsLCD.scrollText(message,font,color, speed);
            return;
        }

	 System.out.println("Gonna scroll: " + message + "\n");
	 System.out.println(String.format("Font:%s%s Color:%s Speed:%d\n",fontPath,font.getFontName(),fontColor,speed));

    }
}
