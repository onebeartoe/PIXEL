
package org.onebeartoe.pixel;

import ioio.lib.api.IOIO;

/**
 * @author Roberto Marquez
 */
public class IoioListener 
{
    private IOIO ioio;
    
    public void ioioReady(IOIO ioio)
    {
        this.ioio = ioio;
    }
}
