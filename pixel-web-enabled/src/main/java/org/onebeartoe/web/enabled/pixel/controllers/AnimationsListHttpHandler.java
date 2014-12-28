/*
 */
package org.onebeartoe.web.enabled.pixel.controllers;

import java.util.List;

/**
 *
 * @author Roberto Marquez
 */
public class AnimationsListHttpHandler extends ListHttpHandler
{
    @Override
    protected List<String> getList()
    {
        return getApp().animationImageNames;
    }
}
