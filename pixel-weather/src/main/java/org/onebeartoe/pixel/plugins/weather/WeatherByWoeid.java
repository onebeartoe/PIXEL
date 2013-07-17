
package org.onebeartoe.pixel.plugins.weather;

import ioio.lib.api.RgbLedMatrix.Matrix;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.onebeartoe.pixel.plugins.swing.ScrollingTextPanel;

/**
 *
 */
public class WeatherByWoeid extends ScrollingTextPanel
{
    
    private WoeidLocation weather;
    
    private BufferedImage icon;
    
    private DefaultComboBoxModel<WoeidLocation> locationsModel;
            
    public WeatherByWoeid(Matrix m)
    {
	super(m);
	
        WoeidLocation location = new WoeidLocation();
        location.city = "Paris";
        location.locationId = "615702";
        locationsModel = new DefaultComboBoxModel();        
        locationsModel.addElement(location);
        JComboBox locationDropdown = new JComboBox(locationsModel);
        
        JRadioButton woeidRadio = new JRadioButton("WOEID");
        JRadioButton usZipRadio = new JRadioButton("US ZIP Code");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(woeidRadio);
        radioGroup.add(usZipRadio);
        JPanel radioPanel = new JPanel( new GridLayout(1,2, 5, 5) );
        radioPanel.add(woeidRadio);
        radioPanel.add(usZipRadio);                        
        final JTextArea locationIdField = new JTextArea();
	JButton addLocationButton = new JButton("Add");
	addLocationButton.addActionListener( new ActionListener() 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
                String s = locationIdField.getText();
                
		int id = Integer.parseInt(s);
                
                
		
	    }
	});	
	JPanel addLocationPanel = new JPanel( new BorderLayout() );
	addLocationPanel.add(locationIdField, BorderLayout.CENTER);
	addLocationPanel.add(addLocationButton, BorderLayout.EAST);
        
        JPanel newlocationPanel = new JPanel( new GridLayout(2,1, 10, 10) );        
        newlocationPanel.add(radioPanel);
        newlocationPanel.add(addLocationPanel);
        newlocationPanel.setBorder( BorderFactory.createTitledBorder("New Location") );
        
        JPanel locationPanel = new JPanel( new GridLayout(3,1, 10, 10) );
        locationPanel.add(locationDropdown);
        locationPanel.add(newlocationPanel);
        locationPanel.setBorder( BorderFactory.createTitledBorder("Current Location") );
	
	JEditorPane webView = new JEditorPane();
	webView.setContentType("text/html");
	String uri = "http://weather.yahooapis.com/forecastrss?" + location.toQueryString();
	int start = -1;
	int end = -1;
	try 
	{
            WeatherService weatherService = new WeatherService();
	    InputStream instream = weatherService.retrieve( uri );
	    
	    weather = weatherService.parse(instream);
            
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

        add(locationPanel, BorderLayout.NORTH);
	add(webviewScroller, BorderLayout.CENTER);
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
