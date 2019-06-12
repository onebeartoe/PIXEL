
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.onebeartoe.network.TextHttpHandler;

/**
 * @author Roberto Marquez
 */
public class UploadOriginHttpHandler extends TextHttpHandler
{
    private UploadHttpHandler uploadHttpHandler;
    
    public UploadOriginHttpHandler(UploadHttpHandler uploadHttpHandler)
    {
        this.uploadHttpHandler = uploadHttpHandler;
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        String response = uploadHttpHandler.getLastUploadOrigin();
        
        return response;
    }

}
