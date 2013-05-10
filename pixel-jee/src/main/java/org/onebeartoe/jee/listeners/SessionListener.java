
package org.onebeartoe.jee.listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rmarquez
 */
@WebListener
public class SessionListener implements HttpSessionListener
{
    
    private static final Logger log = LoggerFactory.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent arg0) 
    {
        log.info("Session created");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) 
    {
        log.info("Session destroyed");
    }
    
}

