
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import static javafx.scene.paint.Color.color;
import org.onebeartoe.network.TextHttpHandler;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.CliPixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class UpdateHttpHandler extends TextHttpHandler
{
    protected WebEnabledPixel application;
    
    public UpdateHttpHandler(WebEnabledPixel application)
    {
        this.application = application;
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        LogMe logMe = LogMe.getInstance();
        
        String UpdateOutput = "Update Failed";
        String returnMessage = "";
        Color color = null;
        
        if (WebEnabledPixel.isUnix()) {  
            
            System.out.println("Received update command, now checking for updates...");
            logMe.aLogger.info("Received update command, now checking for updates...");
            
            ProcessBuilder processBuilder = new ProcessBuilder();

            // -- Linux --

            // Run a shell command
            processBuilder.command("bash", "-c", "git stash && git pull > gitpullresult");

             // Run a shell script
            //processBuilder.command("path/to/hello.sh");

            // -- Windows --

            // Run a command
            //processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");

            // Run a bat file
            //processBuilder.command("C:\\Users\\mkyong\\hello.bat");

            try {
                    
                    Pixel.setFontFamily("Tall Films Fine");
                    application.getPixel().scrollText("Now Checking for Updates...", 10, 10,color.CYAN ,WebEnabledPixel.pixelConnected,1);
                
                    Process process = processBuilder.start();

                    StringBuilder output = new StringBuilder();

                    BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()));
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                            output.append(line + "\n");
                    }
                    
                    //String value = output.substring(output.indexOf("\n") + 1).trim(); 

                    int exitVal = process.waitFor();
                    if (exitVal == 0) {
                            
                            File updateFileResult = new File("gitpullresult");
                            if (updateFileResult.exists() && !updateFileResult.isDirectory()) {
                                    BufferedReader br = new BufferedReader(new FileReader("gitpullresult"));
                                    UpdateOutput = br.readLine();
                                    System.out.println(UpdateOutput);
                            }
                          
                            returnMessage = "Update command sent with result:\n";
                            returnMessage = returnMessage + UpdateOutput;
                            System.out.println(UpdateOutput);
                            application.getPixel().scrollText("Update Complete: " + UpdateOutput,0,10,color.GREEN,WebEnabledPixel.pixelConnected,1); //only the scroll the second line of the output
                            //app.getPixel().scrollText(text_, loop, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
                            updateFileResult.delete(); //let's clean up and delete the file
                           
                    } else {
                            //abnormal...
                            returnMessage = "Update command failed:\n";
                            returnMessage = returnMessage + output;
                            System.out.println(returnMessage);
                            application.getPixel().scrollText("Update Failed: " + output, 0, 10,color.RED ,WebEnabledPixel.pixelConnected,1);
                            //app.getPixel().scrollText(text_, loop, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
                    }

                    } catch (IOException e) {
                            e.printStackTrace();
                    } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
           
        } 
        else {
            returnMessage = "Sorry, update command only available on Raspberry Pi and Linux";
            System.out.println(returnMessage);
            logMe.aLogger.info(returnMessage);
        }
        
        return returnMessage;
    }

}


