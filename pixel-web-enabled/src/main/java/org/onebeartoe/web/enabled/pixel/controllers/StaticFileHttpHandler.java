
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;

/**
 * @author Roberto Marquez
 */
public class StaticFileHttpHandler extends PixelHttpHandler
{
    @Override    
    public void handle(HttpExchange t) throws IOException
    {
        logger.log(Level.INFO, "static file handler request: " + t.getRequestURI());
        String root = app.getPixel().getPixelHome();
        URI uri = t.getRequestURI();
        String path = root + uri.getPath();
        File file = new File(path).getCanonicalFile();
        if (!file.getPath().startsWith(root))
        {
            logger.log(Level.INFO, "forbidden request: " + t.getRequestURI());
            
            // Suspected path traversal attack: reject with 403 error.
            String response = "403 (Forbidden)\n";
            t.sendResponseHeaders(403, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } 
        else if (!file.isFile())
        {
            logger.log(Level.INFO, "file not found request: " + t.getRequestURI());
            
            // Object does not exist or is not a file: reject with 404 error.
            String response = "404 (Not Found)\n";
            t.sendResponseHeaders(404, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } 
        else
        {
            logger.log(Level.INFO, "sending static file for request: " + t.getRequestURI());
            
            // Object exists and is a file: accept with response code 200.
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0)
            {
                os.write(buffer, 0, count);
            }
            fs.close();
            os.close();
        }
    }
}
