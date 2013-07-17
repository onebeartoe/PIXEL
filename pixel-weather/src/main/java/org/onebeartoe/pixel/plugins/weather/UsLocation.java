
package org.onebeartoe.pixel.plugins.weather;

/**
 * @author rmarquez
 */
public class UsLocation extends WoeidLocation
{
    @Override
    public String toQueryString()
    {
        return "p=" + locationId;
    }
}
