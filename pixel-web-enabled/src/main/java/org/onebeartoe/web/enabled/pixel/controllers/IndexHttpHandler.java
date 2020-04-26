package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.onebeartoe.network.TextHttpHandler;

/**
 *
 * @author Roberto Marquez
 */
public class IndexHttpHandler extends TextHttpHandler
{
    @Override
    protected String getHttpText(HttpExchange t)
    {
        InetAddress localhost = null; 
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(IndexHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String response = "Pixelcade Arcade Marquee and Pinball Display\n\nBrowse artwork from any computer on the same Wi-Fi network from this URL:\n\nhttp://" + localhost.getHostAddress().trim() + ":8080/files/index.html";

        return response;
    }
}

