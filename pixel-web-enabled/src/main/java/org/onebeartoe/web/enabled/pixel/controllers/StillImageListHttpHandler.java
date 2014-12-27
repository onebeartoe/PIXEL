/*
 */
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author Roberto Marquez
 */
public class StillImageListHttpHandler extends TextHttpHandler
{
    @Override
    protected String getHttpText(HttpExchange t)
    {
        StringBuilder response = new StringBuilder();
        
        for(String name : getApp().stillImageNames)
        {
            response.append(name);
            response.append("\n");
            response.append("-+-");
            response.append("\n");
        }

        return response.toString();
    }    
}
