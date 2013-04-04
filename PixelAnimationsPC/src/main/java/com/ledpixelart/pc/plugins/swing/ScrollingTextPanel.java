
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.PixelApp;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author rmarquez
 */
public class ScrollingTextPanel extends SingleThreadedPixelPanel
{
    private JTextField textField;
    
    private JComboBox fontFamilyChooser;
    
    private HashMap<String, Font> fonts;
    
    private int x;
    
    public ScrollingTextPanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
        
        fonts = new HashMap();
        
        x = 0;
        
        textField = new JTextField("Hello world!");
        
        JPanel inputSubPanel = new JPanel( new BorderLayout() );
        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.setBorder( BorderFactory.createTitledBorder("Input File") );
        inputSubPanel.add(textField, BorderLayout.CENTER);        
        inputPanel.add(inputSubPanel, BorderLayout.NORTH);               
        
        String [] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        
        JPanel fontPanel = new JPanel( new BorderLayout() );
        fontFamilyChooser = new JComboBox(fontNames);
        fontPanel.add(fontFamilyChooser, BorderLayout.CENTER);
        
        JPanel configurationPanel = new JPanel( new GridLayout(4, 1));
        configurationPanel.add(inputSubPanel);
        configurationPanel.add(fontPanel);
        
        setLayout(new BorderLayout());
        add(configurationPanel, BorderLayout.NORTH);
    }

    @Override
    public ActionListener getActionListener() 
    {
        ActionListener listener = new TextScroller();
        
        return listener;
    }
    
    private class TextScroller implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {        
            int w = 64;
            int h = 64;
            BufferedImage img = new BufferedImage(            w, h, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2d = img.createGraphics();
            g2d.setPaint(Color.orange);
            
            String fontFamily = fontFamilyChooser.getSelectedItem().toString();
            
            Font font = fonts.get(fontFamily);
            if(font == null)
            {
                font = new Font(fontFamily, Font.PLAIN, 32);
                fonts.put(fontFamily, font);
            }            
            
            g2d.setFont(font);
            
            String message = textField.getText();
            
            FontMetrics fm = g2d.getFontMetrics();
            
            int y = fm.getHeight();            

            g2d.drawString(message, x, y);
            g2d.dispose();

            try 
            {              
                PixelApp.pixel.writeImagetoMatrix(img);
            } 
            catch (ConnectionLostException ex) 
            {
                Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
                        
            int messageWidth = fm.stringWidth(message);            
            int resetX = 0 - messageWidth;
            
            if(x == resetX)
            {
                x = w;
            }
            else
            {
                x--;
            }
        }        
    }
    
}
