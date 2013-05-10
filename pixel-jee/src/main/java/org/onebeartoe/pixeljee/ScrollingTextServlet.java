
package org.onebeartoe.pixeljee;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rmarquez
 */
@WebServlet(value = "/report", loadOnStartup=1)
public class ScrollingTextServlet extends HttpServlet
{
    
    @Override
    public void init()
    {
	
    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("text/plain");
        response.getWriter().write(new Date().toString());
    }
}
