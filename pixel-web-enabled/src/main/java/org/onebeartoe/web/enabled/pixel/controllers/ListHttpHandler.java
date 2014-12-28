
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.util.List;

/**
 * @author Roberto Marquez
 */
public abstract class ListHttpHandler extends TextHttpHandler
{
    @Override
    protected String getHttpText(HttpExchange t)
    {
        StringBuilder response = new StringBuilder();
        
        List<String> list = getList();
        for(String name : list)
        {
            response.append(name);
            response.append("\n");
            response.append("-+-");
            response.append("\n");
        }

        return response.toString();
    }
    
    protected abstract List<String> getList();
}
