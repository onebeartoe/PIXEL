
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roberto Marquez
 */
public class UploadHttpHandler extends TextHttpHandler
{

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        int count = -99;
        try
        {
            Headers requestHeaders = exchange.getRequestHeaders();
            HttpContext httpContext = exchange.getHttpContext();
            
            InputStream requestBody = exchange.getRequestBody();
            InputStreamReader streamReader = new InputStreamReader(requestBody);
            BufferedReader reader = new BufferedReader(streamReader);
            
            String barrier = reader.readLine();
            String contentDisposition = reader.readLine();
            String contentType = reader.readLine();
            
            // blank line
            reader.readLine();
            
            // now the HTTP POST multiform data,
            // plus the last barrier,
            // are left in the input stream
            
            File outfile = new File("c:\\home\\", "upload.jpg");
            OutputStream outputStream = new FileOutputStream(outfile);
            Writer writer = new OutputStreamWriter(outputStream);
            BufferedWriter outputWriter = new BufferedWriter(writer);
            
            String fileData = reader.readLine();
            
// figure otu a way to do thiw equals()!!!!!            
            while( fileData != null)
            {
                if(fileData.contains(barrier))
                {
                    break;
                }
                outputWriter.write(fileData + System.lineSeparator() );
                
                fileData = reader.readLine();
            }
            
            reader.close();
            
            outputWriter.flush();
            outputWriter.close();
                        
//            count = IOUtils.copy(requestBody, outputStream);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(UploadHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(UploadHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        return "upload recived: " + count;
    }

}
