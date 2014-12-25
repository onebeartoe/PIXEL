
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.onebeartoe.web.enabled.pixel.controllers.PixelHttpHandler;

/**
 * @author Roberto Marquez
 */
public abstract class TextHttpHandler extends PixelHttpHandler
{    
    protected abstract String getHttpText(HttpExchange exchange);

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {            
        String response = getHttpText(exchange);
        
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }        
}
