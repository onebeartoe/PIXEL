
package org.onebeartoe.net.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 *
 * @author Roberto Marquez
 */
public abstract class TextHttpHandler implements HttpHandler
{
    protected Logger logger;    
    
    public TextHttpHandler()
    {
        String name = getClass().getName();
        logger = Logger.getLogger(name);
    }
    
    protected abstract String getHttpText(HttpExchange exchange);

    public void handle(HttpExchange exchange) throws IOException
    {            
        String response = getHttpText(exchange);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }        
}
