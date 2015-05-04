
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eh.core.model.FileInfo;
import org.eh.core.util.FileUploadContentAnalysis;

/**
 * The classes from this Github repository are used:
 * https://github.com/NotBadPad/easy-httpserver
 * @author Roberto Marquez
 */
public class UploadHttpHandler extends TextHttpHandler
{

    @Override
    protected String getHttpText(HttpExchange httpExchange)
    {
        System.out.println("request reviced");

        Headers headers = httpExchange.getRequestHeaders();
        // get ContentType
        String contentType = headers.get("Content-type").toString().replace("[", "")
                        .replace("]", "");

        // get content length
        int length = Integer.parseInt(headers.get("Content-length").toString().replace("[", "")
                        .replace("]", ""));

        StringBuilder issues = new StringBuilder();
        
        try
        {
            InputStream requestBody = httpExchange.getRequestBody();
            Map<String, Object> map = FileUploadContentAnalysis.parse(requestBody, contentType, length);
            
            String paramKey = "animation";
            FileInfo fileInfo = (FileInfo) map.get(paramKey);
            
            if(fileInfo == null)
            {
                issues.append("No file was posted with request, for param named: " + paramKey);
            }
            else
            {
                String outpath = "c:\\home\\" + "out.png";//fileInfo.getFilename();
                FileOutputStream fos = new FileOutputStream(outpath);
                fos.write(fileInfo.getBytes());
                fos.close();
            }
        }
        catch (IOException ex)
        {
            String message = ex.getMessage();
            issues.append(message);
            
            Logger.getLogger(UploadHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        String response = "made it back";   
        
        return contentType + "\n" + 
               response + "\n" + 
               issues.toString();
    }

}
