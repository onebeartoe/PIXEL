package org.onebeartoe.front.end.pixelcade;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author curtis-bull
 */
public class CliPixel 
{

 private static final Logger log = Logger.getLogger(CliPixel.class.getName());
 private String[] args = null;
 private Options options = new Options();
 private String mode = ""; //for streaming or writing 
 private String consoleName = "";
 private String gameName = ""; //this can be a full path, just filename, or just basefilename with no extension, what about quotes?
 private String eventID = ""; 
 private String text = "";
 private String color = "";
 private String speed = "";
 private Boolean quit = false;
 private Boolean silent = false;
 private int yTextOffset = 0;
 private static String instructions = "usage: Pixelcade\n" +
"                    -m,--mode <arg>  Sets stream or write mode, options are stream or write\n" +
"                    -c,--console <arg>  Sets the console or platform name, ex. mame, atari2600, nes\n" +
"                    -g,--game <arg>  Sets game / rom name, can be a full path, file name,\n" +
"                        or just the basename without the extension\n" +
"                    -q,--quit Shuts down the Pixelcade Listener (pixelweb.exe)\n" +
"                    -h,--help  show help.\n" +
"                    \n" +
"                    Examples\n" +
"                    VERY IMPORTANT: Enclose any parameters with spaces in double quotes, for example\n" +
"                           Atari 2600 must be enclosed in double quotes\n" +
"                    pixelcade.exe -m stream -p \"Atari 2600\" -g Rampage.bin\n" +
"                    pixelcade.exe -m write -p mame -g d:\\roms\\pacman.zip\n" +
"                    pixelcade.exe -q\n" +
"                    java -jar pixelcade.jar -m stream -p c64 -g \"Video Vermin (World).zip\" \n" +
"                    java -jar pixelcade.jar -m write -p \"Nintendo Entertain System\" -g \"Zelda II - The Adventure of Link (U).zip\"";

 public CliPixel(String[] args) 
 {

  this.args = args;
  
  options.addOption("m", "mode", true, "Sets stream or write mode, use stream or write");
  options.addOption("c", "console", true, "Sets the console or platform name, examples atari2600, mame, Nintendo Entertaintment System");
  options.addOption("g", "game", true, "Sets game or rom name, this can be a full path, just the filename only, or just the basename without the extension");
  options.addOption("e", "event", true, "The event id from EDS");
  options.addOption("t", "text", true, "Scrolling text");
  options.addOption("color", "color", true, "Scrolling text color");
  options.addOption("speed", "speed", true, "Scrolling text speed");
  options.addOption("s", "silent", false, "Run in silent mode");
  options.addOption("q", "quit", false, "Shuts down the Pixelcade Listener (pixelweb.exe)");
  options.addOption("h", "help", false, "show help.");
  //to do add for streaming text too
 }

 public void parse() 
 {
  CommandLineParser parser = new BasicParser();

  CommandLine cmd = null;
  try {
   cmd = parser.parse(options, args);

      if (cmd.hasOption("h")) {
          help();
      }
      
      if (cmd.hasOption("m")) {
          //log.log(Level.INFO, "Using cli argument -m=" + cmd.getOptionValue("m"));
          mode = cmd.getOptionValue("m");
      }
      
      if (cmd.hasOption("c")) {
          //log.log(Level.INFO, "Using cli argument -c=" + cmd.getOptionValue("c"));
          consoleName = cmd.getOptionValue("c");
          //to do need to handle spaces without having to add quotes
      }
      
      if (cmd.hasOption("t")) {
          text = cmd.getOptionValue("t");
      }
      
      if (cmd.hasOption("color")) {
          color = cmd.getOptionValue("color");
      }
      
       if (cmd.hasOption("speed")) {
          speed = cmd.getOptionValue("speed");
      }

      if (cmd.hasOption("g")) {
          //log.log(Level.INFO, "Using cli argument -g=" + cmd.getOptionValue("g"));
          gameName = cmd.getOptionValue("g");
      }
      
      if (cmd.hasOption("e")) {
          eventID = cmd.getOptionValue("e");
      }
      
      if (cmd.hasOption("q")) {
          //log.log(Level.INFO, "Using cli argument -q=" + cmd.getOptionValue("q"));
          quit = true;
      }
      
       if (cmd.hasOption("s")) {
          //log.log(Level.INFO, "Using cli argument -q=" + cmd.getOptionValue("q"));
          silent = true;
      }
      

  } catch (ParseException e) {
   log.log(Level.SEVERE, "Failed to parse command line properties", e);
   help();
  } catch (NumberFormatException e) {
   log.log(Level.SEVERE, "Failed to parse command line properties.  Number expected",e);        
   help();
  }
 }
 
 public String getMode()
 {
    return mode;
 }
 
 public String getConsoleName()
 {
    return consoleName;
 }
 
 public String getGameName()
 {
    return gameName;
 }
 
   public String getText()
 {
    return text;
 }
   
  public String getColor()
 {
    return color;
 }
     
  public String getSpeed()
 {
    return speed;
 }
 
  public String getGEventID()
 {
    return eventID;
 }
 
 public boolean getQuit()
{
    return quit;
}
 
 public boolean getSilentMode()
{
    return silent;
}
 
 public static String getInstructions() {
     return instructions;
 }
 
public int getyTextOffset() {
    return yTextOffset;
}

public void setyTextOffset(int yTextOffset) {
    this.yTextOffset = yTextOffset;
} 
 
private void help() {
  
   System.out.println(instructions); 
 
}
 
 
}
