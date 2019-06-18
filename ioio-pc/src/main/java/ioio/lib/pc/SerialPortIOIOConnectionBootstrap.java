/*
 * Copyright 2011 Ytai Ben-Tsvi. All rights reserved.
 *  
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ARSHAN POURSOHI OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied.
 */

package ioio.lib.pc;

import ioio.lib.api.IOIOConnection;
import ioio.lib.spi.IOIOConnectionBootstrap;
import ioio.lib.spi.IOIOConnectionFactory;
import ioio.lib.spi.Log;
import java.io.File;
import java.io.IOException;
import org.ini4j.*;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;






public class SerialPortIOIOConnectionBootstrap implements
		IOIOConnectionBootstrap {
	private static final String TAG = "SerialPortIOIOConnectionBootstrap";
	private static String pixelPrefNode = "/com/ledpixelart/pc";
	private static Preferences prefs;

	@Override
	public void getFactories(Collection<IOIOConnectionFactory> result) {
		Collection<String> ports = getExplicitPorts();
		if (ports == null) {
			Log.w(TAG, "ioio.SerialPorts not defined.\n"
					+ "Will attempt to enumerate all possible ports (slow) "
					+ "and connect to PIXEL over each one.\n"
					+ "To fix, add the -Dioio.SerialPorts=xyz argument to "
					+ "the java command line, where xyz is a colon-separated "
					+ "list of port identifiers, e.g. COM1:COM2. for Windows or /dev/tty.usbmodem1411 on Mac OSX");
			ports = getAllOpenablePorts();
		}
		for (final String port : ports) {
		    
			Log.d(TAG, "Adding serial port " + port);
			result.add(new IOIOConnectionFactory() {
				@Override
				public String getType() {
					//System.out.println("Found Final Port: " + port);
					return SerialPortIOIOConnection.class.getCanonicalName();
				}

				@Override
				public Object getExtra() {
					return port;
				}

				@Override
				public IOIOConnection createConnection() {
					return new SerialPortIOIOConnection(port);
				}
			});
		}
	}

	static Collection<String> getAllOpenablePorts() {
		List<String> result = new LinkedList<String>();
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> identifiers = CommPortIdentifier
				.getPortIdentifiers();
		
		while (identifiers.hasMoreElements()) {
			final CommPortIdentifier identifier = identifiers.nextElement();
			if (identifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (checkIdentifier(identifier)) {
					Log.d(TAG, "Adding serial port " + identifier.getName());
					result.add(identifier.getName());
			    	
				} else {
					Log.w(TAG, "Serial port " + identifier.getName()
							+ " cannot be opened. Not adding.");
				}
			}
		}
		
		
		return result;
	}

	static Collection<String> getExplicitPorts() {
            List<String> result = new LinkedList<String>();
            
            //let's look in settings.ini for the port 
            Ini ini = null;
             try {
                ini = new Ini(new File("settings.ini"));
             } catch (IOException ex) {
                Logger.getLogger(SerialPortIOIOConnectionBootstrap.class.getName()).log(Level.SEVERE, null, ex);
             }
             System.out.println("Port was found in settings.ini: " + ini.get("PIXELCADE SETTINGS", "port"));
             String port_ = null;
             //only go here if settings.ini exists
             port_ = ini.get("PIXELCADE SETTINGS", "port");
            
             if (port_ != null) {
                 result.add(port_);
	         return result;
                 
             } else {
            
                String property = System.getProperty("ioio.SerialPorts"); //if the user specified the port, we should honor that but what if they did that and it's in prefs too, which one to choose?
                    //if the user forced it, then we should take that one

                    //TO DO should we have a flag to re-run pixel detection?

                    //if (org.onebeartoe.web.enabled.pixel.WebEnabledPixel.port_ != null) {
                    //	result.add(org.onebeartoe.web.enabled.pixel.WebEnabledPixel.port_);
                    //	return result;

                    //} else {

                    if (property == null) { //the user didn't specify a command line so let's check prefs
                            //and then should we also save it in prefs too? YES but let's not put it here and rather save it during IOIO setup because only then we know it was valid
                        //the user didn't force it in the command line so now let's check the preferences and see if there
                            prefs = Preferences.userRoot().node(pixelPrefNode); //let's get the port from preferences
                            property = prefs.get("prefSavedPort", "");

                            if (property.equals("")) {  //but if prefs is also null then we need to get out
                                    property = null;
                                    return null;
                            }


                    }

                    //List<String> result = new LinkedList<String>();
                    String[] portNames = property.split(":");
                    for (String portName : portNames) {
                            result.add(portName);
                    }
                    return result;
             }
	}

	static boolean checkIdentifier(CommPortIdentifier id) {
		if (id.isCurrentlyOwned()) {
			return false;
		}
		// The only way to find out is apparently to try to open the port...
		try {
			CommPort port = id.open(
					SerialPortIOIOConnectionBootstrap.class.getName(), 1000);
			port.close();
		} catch (PortInUseException e) {
			return false;
		}
		return true;
	}
}
