
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
public class AnimationsHttpHandler extends PixelHttpHandler
{
    private String userHome = System.getProperty("user.home");

    private String decodedDir = userHome + "/pixel/animations/decoded/";

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        boolean saveAnimation = false;
        
        try
        {
            Pixel pixel = app.getPixel();
            pixel.writeAnimation("arrows.png", saveAnimation);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return "Animate an image!";
        }
    }
}
