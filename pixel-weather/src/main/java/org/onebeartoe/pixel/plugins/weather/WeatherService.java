package org.onebeartoe.pixel.plugins.weather;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

//import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.io.SAXReader;

public class WeatherService {

	private static Logger log = Logger.getLogger(WeatherService.class.getName());

	public WoeidLocation parse(InputStream inputStream) throws Exception 
        {
		WoeidLocation weather = new WoeidLocation();
		
		log.info( "Creating XML Reader" );
		SAXReader xmlReader = createXmlReader();
		Document doc = xmlReader.read( inputStream );

		log.info( "Parsing XML Response" );
		weather.city = doc.valueOf("/rss/channel/y:location/@city") ;
		weather.region =  doc.valueOf("/rss/channel/y:location/@region");
		weather.country = doc.valueOf("/rss/channel/y:location/@country") ;
		weather.condition = doc.valueOf("/rss/channel/item/y:condition/@text") ;
		weather.temp = doc.valueOf("/rss/channel/item/y:condition/@temp") ;
		weather.chill = doc.valueOf("/rss/channel/y:wind/@chill") ;
		weather.humidity = doc.valueOf("/rss/channel/y:atmosphere/@humidity") ;		
		weather.htmlDescription = doc.valueOf("/rss/channel/item/description");
		
		String target = "<img src=\"";
		int i = weather.htmlDescription.indexOf(target);
		int start = i + target.length();
		int end = weather.htmlDescription.indexOf("\"", start);
		weather.imageUrl = weather.htmlDescription.substring(start, end);
		
		return weather;
	}

	private SAXReader createXmlReader() 
        {
            Map<String,String> uris = new HashMap<String,String>();
            uris.put( "y", "http://xml.weather.yahoo.com/ns/rss/1.0" );
        
            DocumentFactory factory = new DocumentFactory();
            factory.setXPathNamespaceURIs( uris );
        
            SAXReader xmlReader = new SAXReader();
            xmlReader.setDocumentFactory( factory );
            
            return xmlReader;
	}

	public String format(WoeidLocation weather)
	{
	    StringBuilder buf = new StringBuilder();
	    
	    buf.append("<pre>");
	    buf.append("-----------------------------------");
	    buf.append("\n");
	    buf.append( weather.toString() );
            
            String text = "HTML Description:\n" + weather.htmlDescription;            
	    buf.append(text);
            System.out.println(text);
            
	    buf.append("\n");
	    buf.append("-----------------------------------");
	    buf.append("</pre>");
	    
	    return buf.toString();
	}
	
	public InputStream retrieve(String url) throws Exception 
        {
            log.info( "Retrieving Weather Data" );

//          String url = "http://weather.yahooapis.com/forecastrss?p=" + zipcode;

            URLConnection conn = new URL(url).openConnection();

            return conn.getInputStream();
	}	
	
}
