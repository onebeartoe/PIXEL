/*
 */
package org.onebeartoe.pixel.enterpirse.edition;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.onebeartoe.pixel.enterpirse.edition.InitializationServlet.PIXEL_KEY;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
@WebServlet(value = "/status")
public class StatusServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        ServletContext servletContext = getServletContext();     
        Pixel pixel = (Pixel) servletContext.getAttribute(PIXEL_KEY);
        boolean initialized;
        if(pixel == null)
        {
            initialized = false;
        }
        else
        {
            initialized = true;
            
            String firmware = pixel.getFirmwareVersion();            
            request.setAttribute("firmware", firmware);
            
            String hardware = pixel.getHardwareVersion();
            request.setAttribute("hardware", hardware);
        }
        
        request.setAttribute("initialized", initialized);
        
        ServletContext c = getServletContext();
        RequestDispatcher rd = c.getRequestDispatcher("/status.jsp");
        rd.forward(request, response);
    }    
}
