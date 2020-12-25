
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eh.core.model.FileInfo;
import org.eh.core.util.FileUploadContentAnalysis;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * The classes from this Github repository are used:
 * https://github.com/NotBadPad/easy-httpserver
 * @author Roberto Marquez
 */
public class UploadHttpHandler implements HttpHandler//extends TextHttpHandler
{
    protected WebEnabledPixel application;
    
    private List<String> uploadOrigins;
    
    public UploadHttpHandler(WebEnabledPixel application)
    {
        this.application = application;
        
        uploadOrigins = new ArrayList();
    }

    protected String handleUpload(HttpExchange httpExchange)
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

            String uploadKey = "upload";
            FileInfo fileInfo = (FileInfo) map.get(uploadKey);
            
            if(fileInfo == null)
            {
                issues.append("No file was posted with request, for param named: " + uploadKey);
            }
            else
            {
                String uploadTypeKey = "upload-type";
                String s = (String) map.get(uploadTypeKey);
                
                String path = null;
                        
                UploadType type = UploadType.valueOf(s);
                
                uploadOrigins.add(type.name());
                
                switch(type)
                {
                    case ANIMATED_GIF:
                    {
                        path = application.getPixel().getAnimationsPath();
                        
                        break;
                    }
                    case STILL_IMAGE:
                    {
                        path = application.getPixel().getImagesPath();
                        
                        break;
                    }
                    default:
                    {
                        throw new Exception("upload type is needed");
                    }
                }

                String outpath = path + fileInfo.getFilename();
                
                FileOutputStream fos = new FileOutputStream(outpath);
                fos.write(fileInfo.getBytes());
                fos.close();
            }
        }
        catch (Exception ex)
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

    public String getLastUploadOrigin()
    {
        String lastUploadOrigin;
        
        if(uploadOrigins.size() < 1)
        {
            lastUploadOrigin = "ALL-CONSUMED";
        }
        else
        {
            lastUploadOrigin = uploadOrigins.remove(0);
        }
            
        return lastUploadOrigin;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        handleUpload(exchange);

//TODO: Pull the HTML document from a static file in the distribution JAR        
        String response = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
"\n" +
"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
"\n" +
"    <head>\n" +
"<META HTTP-EQUIV=REFRESH CONTENT=\"1; URL=files/index.html\">" +
"	            \n" +
"        <title>Upload Redirect</title>\n" +
"    </head>\n" +
"\n" +
"    <body>\n" +
"        redirecting...\n" +
"    </body>\n" +
"</html>";
        
        exchange.sendResponseHeaders(302, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    enum UploadType
    {
        ANIMATED_GIF,
        STILL_IMAGE
    }
}
