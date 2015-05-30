package org.onebeartoe.web.enabled.pixel;
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
public class CliPixel {

 private static final Logger log = Logger.getLogger(CliPixel.class.getName());
 private String[] args = null;
 private Options options = new Options();
 private int portOption = 2007;
 private int ledMatrixType = 3;
 private int ledMatrixTypeDefault = 3;
 private int ledMatrixTypeMax = 17;

 public CliPixel(String[] args) {

  this.args = args;

  options.addOption("h", "help", false, "show help.");
  options.addOption("p", "port", true, "Listening port. Default Port = 2007");
  options.addOption("l", "ledmatrix", true, "Sets the LED matrix type. Default = 3\n 0=32x16 Seeed 1=32x16 Adafruit, 2=32x32 Seeed\n" +
                    "3=PIXEL V2, 4=64x32 Seeed, 5=32x64 Seeed, 6=Seeed 2 Mirrored\n" +
                    "7=Seeed 4 Mirrored (does not work), 8=128x32 Seeed, 9=32x128 Seeed\n" +
                    "10=SUPER PIXEL 64x64, 11=32x32 Adafruit, 12=32x32 Adafruit Color Swap\n" +
                    "13=64x32 Adafruit, 14=64x64 Adafruit, 15=128x32 Adafruit\n" +
                    "16=32x128 Adafruit, 17=64x16 Adafruit\n");
  options.addOption("m", "matrix", true, "Sets the LED matrix type, same as l option");
 }

 public void parse() {
  CommandLineParser parser = new BasicParser();

  CommandLine cmd = null;
  try {
   cmd = parser.parse(options, args);

   if (cmd.hasOption("h"))
    help();

   if (cmd.hasOption("p")) {
    log.log(Level.INFO, "Using cli argument -p=" + cmd.getOptionValue("p"));
    portOption = Integer.parseInt(cmd.getOptionValue("p"));
   }
   
    if (cmd.hasOption("l")) {
    log.log(Level.INFO, "Using cli argument -l=" + cmd.getOptionValue("l"));
    ledMatrixType = Integer.parseInt(cmd.getOptionValue("l"));
   }
    
    if (cmd.hasOption("m")) {
    log.log(Level.INFO, "Using cli argument -m=" + cmd.getOptionValue("m"));
    ledMatrixType = Integer.parseInt(cmd.getOptionValue("m"));
    //let's handle the case if the user entered a number larger than the max
    if (ledMatrixType > ledMatrixTypeMax) {
        ledMatrixType = ledMatrixTypeDefault;
    } 
   }

  } catch (ParseException e) {
   log.log(Level.SEVERE, "Failed to parse command line properties", e);
   help();
  } catch (NumberFormatException e) {
   log.log(Level.SEVERE, "Failed to parse command line properties.  Number expected",e);        
   help();
  }
 }
 public int getPort()
 {
    return portOption;
 }
 
 public int getLEDMatrixType()
 {
    return ledMatrixType;
 }
 private void help() {
  // This prints out some help
  HelpFormatter formater = new HelpFormatter();

  formater.printHelp("Main", options);
 }
}
