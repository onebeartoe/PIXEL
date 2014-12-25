
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
public class AnimationsHttpHandler extends TextHttpHandler
{
    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        boolean saveAnimation = false;
        
        String animationName = "arrows.png";
        String message = null;
        try
        {
            Pixel pixel = app.getPixel();
            pixel.writeAnimation(animationName, saveAnimation);
            message = "written to the Pixel";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            message = e.getMessage();
        }
        finally
        {
            return "animation " + animationName + ": " + message;
        }
    }
}
