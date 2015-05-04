
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
        int totalRead = 0;
        String contentDisposition = null;
        String contentType = null;
        
        try
        {
            InputStream requestBody = exchange.getRequestBody();
            
            InputStreamReader streamReader = new InputStreamReader(requestBody);            
            BufferedReader reader = new BufferedReader(streamReader);
            
            String barrier = reader.readLine();
            contentDisposition = reader.readLine();
            contentType = reader.readLine();
            
            // blank line
            reader.readLine();
            
            // now the HTTP POST multiform data,
            // plus the last barrier,
            // are left in the input stream
            
            File outfile = new File("c:\\home\\", "upload.jpg");
            OutputStream outputStream = new FileOutputStream(outfile);
            Writer writer = new OutputStreamWriter(outputStream);
            BufferedWriter outputWriter = new BufferedWriter(writer);
            
            char [] fileData = new char[1024];
            int readCount = reader.read(fileData);
            

            while(readCount > 0)
            {
                if( String.valueOf(fileData).contains(barrier))
                {
                    // the 'end' barrier has been reached
                    break;
                }
                
                outputWriter.write(fileData, 0, readCount);
                totalRead += readCount;
                
                readCount = reader.read(fileData);
            }
            
            reader.close();
            
            outputWriter.flush();
            outputWriter.close();
                        
//            IOUtils.copy
//            totalRead = IOUtils.copy(requestBody, outputStream);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(UploadHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(UploadHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        return contentDisposition + "\n" + 
                contentType + "\n" + 
                "upload recived: " + totalRead;
    }

}
