// this shoujdl be a class in onebeartoe Java libraries that does not extends PixelHttpHandler
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @deprecated use the version in the onebeartoe Java Libraries
 * 
 * @author Roberto Marquez
 */
@Deprecated
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

