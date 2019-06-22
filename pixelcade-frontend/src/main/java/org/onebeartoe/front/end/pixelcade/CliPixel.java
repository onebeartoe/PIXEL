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
 private String consoleName = null;
 private String gameName = null; //this can be a full path, just filename, or just basefilename with no extension, what about quotes?
 private String mode = null; //for streaming or writing 
 private int yTextOffset = 0;

 public CliPixel(String[] args) 
 {

  this.args = args;
  
  options.addOption("c", "console", true, "Ex. -c=atari2600 or --console='Atari 2600' Sets the console or platform name");
  options.addOption("g", "rom", true, "Sets game or rom name, this can be a full path, just the filename only, or just the basename without the extension");
  options.addOption("m", "mode", true, "Sets stream or write mode");
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
      
      if (cmd.hasOption("c")) {
          //log.log(Level.INFO, "Using cli argument -c=" + cmd.getOptionValue("c"));
          System.out.println("Using cli argument -c=" + cmd.getOptionValue("c"));
          consoleName = cmd.getOptionValue("c");
          //to do need to handle spaces without having to add quotes
      }

      if (cmd.hasOption("g")) {
          //log.log(Level.INFO, "Using cli argument -g=" + cmd.getOptionValue("g"));
          System.out.println("Using cli argument -g=" + cmd.getOptionValue("g"));
          gameName = cmd.getOptionValue("g");
      }

      if (cmd.hasOption("m")) {
          //log.log(Level.INFO, "Using cli argument -m=" + cmd.getOptionValue("m"));
          System.out.println("Using cli argument -m=" + cmd.getOptionValue("m"));
          mode = cmd.getOptionValue("m");
      }

  } catch (ParseException e) {
   log.log(Level.SEVERE, "Failed to parse command line properties", e);
   help();
  } catch (NumberFormatException e) {
   log.log(Level.SEVERE, "Failed to parse command line properties.  Number expected",e);        
   help();
  }
 }
 public String getConsoleName()
 {
    return consoleName;
 }
 
 public String getGameName()
 {
    return gameName;
 }
 
 public String getMode()
 {
    return mode;
 }
 
    public int getyTextOffset() {
        return yTextOffset;
    }

    public void setyTextOffset(int yTextOffset) {
        this.yTextOffset = yTextOffset;
    } 
 
 private void help() {
  // This prints out some help
  HelpFormatter formater = new HelpFormatter();

  formater.printHelp("Main", options);
 }
}
