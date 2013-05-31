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

	public Weather parse(InputStream inputStream) throws Exception {
		Weather weather = new Weather();
		
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
		
		return weather;
	}

	private SAXReader createXmlReader() {
		Map<String,String> uris = new HashMap<String,String>();
        uris.put( "y", "http://xml.weather.yahoo.com/ns/rss/1.0" );
        
        DocumentFactory factory = new DocumentFactory();
        factory.setXPathNamespaceURIs( uris );
        
		SAXReader xmlReader = new SAXReader();
		xmlReader.setDocumentFactory( factory );
		return xmlReader;
	}

	public String format(Weather weather)
	{
	    StringBuilder buf = new StringBuilder();
	    
	    buf.append("<pre>");
	    buf.append("-----------------------------------");
	    buf.append("\n");
	    buf.append("Current Weather Conditions for:");
	    buf.append(weather.city + ", " + weather.region + ", " + weather.condition);
	    buf.append("\n");
	    buf.append("Temperature: " + weather.temp);
	    buf.append("Condition: " + weather.condition);
	    buf.append("Humidity: " + weather.humidity);
	    buf.append("Wind Chill: " + weather.chill);
	    buf.append("-----------------------------------");
	    buf.append("</pre>");
	    
	    return buf.toString();
	}
	
	public InputStream retrieve(String zipcode) throws Exception {
		log.info( "Retrieving Weather Data" );
		
		String url = zipcode;
//		String url = "http://weather.yahooapis.com/forecastrss?p=" + zipcode;
		
		URLConnection conn = new URL(url).openConnection();
		return conn.getInputStream();
	}	
	
}
