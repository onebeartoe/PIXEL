
package org.onebeartoe.pixel.plugins.weather;

import ioio.lib.api.RgbLedMatrix.Matrix;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.onebeartoe.pixel.plugins.swing.ScrollingTextPanel;

/**
 *
 */
public class WeatherByWoeid extends ScrollingTextPanel
{
    
    private Weather weather;
    
    BufferedImage icon;
            
    public WeatherByWoeid(Matrix m)
    {
	super(m);
	
//	setLayout( new BorderLayout() );
	
	JEditorPane webView = new JEditorPane();
	webView.setContentType("text/html");
	String uri = "http://weather.yahooapis.com/forecastrss?w=615702";
	int start = -1;
	int end = -1;
	try 
	{
            WeatherService weatherService = new WeatherService();
	    InputStream dataIn = weatherService.retrieve( uri );
	    
	    weather = weatherService.parse( dataIn );
            
            SwingUtilities.invokeLater( new Runnable() 
            {
                public void run() 
                {
                    try 
                    {
                        URL url = new URL(weather.imageUrl);
                        icon = ImageIO.read(url);
                    } 
                    catch(Exception ex) 
                    {
                        Logger.getLogger(WeatherByWoeid.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
	    
	    String description = weatherService.format( weather );
            
	    webView.setText(description);
	} 
	catch (Exception ex) 
	{
	    String message = "start: " + start + "  -   end: " + end;
	    Logger.getLogger(WeatherByWoeid.class.getName()).log(Level.SEVERE, message, ex);
	}
        JScrollPane webviewScroller = new JScrollPane(webView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

System.out.println("text panel removed");
        remove(textPanel);
        
        add(textPanel, BorderLayout.CENTER);
	add(webviewScroller, BorderLayout.SOUTH);
    }
    
    @Override
    protected void additionalBackgroundDrawing(Graphics2D g2d) throws Exception
    {
        if(icon == null)
        {
            System.err.println("The weather image is not available.");
        }
        else
        {
            g2d.drawImage(icon, 0, 0, this);
        }
    }
    
    @Override
    public ImageIcon getTabIcon()
    {
	System.out.println("\n\n\nusing a custom tab\n");
	String path = "tab-icon.png";
	URL url = getClass().getResource(path);
        ImageIcon imagesTabIcon = new ImageIcon(url);
	
	return imagesTabIcon;
    }

    @Override    
    public String getText()
    {
        String text = weather.toString().replaceAll("\n", "");
	return text;
    }
    
}
