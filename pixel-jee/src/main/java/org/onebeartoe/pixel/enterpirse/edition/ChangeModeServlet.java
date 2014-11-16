
package org.onebeartoe.pixel.enterpirse.edition;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/mode/"})
public class ChangeModeServlet extends HttpServlet
{
    private Logger logger;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        // remove the leading forward slash
        String pi = request.getPathInfo();
        String mode;
        if(pi == null)
        {
            mode = "/";
        }
        else
        {
            mode = pi.substring(1);
        }

        String forward;
        switch(mode)
        {
            case "/":
            {
                forward = "";
                        
                break;
            }
            default:
            {
                // scrolling text
                forward = "scrolling-text";
            }
        }
        
        forward = "/mode/" + forward + "/index.jsp";
//        forward = request.getContextPath() + "/mode/" + forward + "/index.jsp";
        
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher(forward);
//        response.sendRedirect(forward);
        
        rd.forward(request, response);
    }
    
    @Override
    public void init() throws ServletException 
    {
        super.init();
        
        logger = Logger.getLogger(getClass().getName());
    }
}
