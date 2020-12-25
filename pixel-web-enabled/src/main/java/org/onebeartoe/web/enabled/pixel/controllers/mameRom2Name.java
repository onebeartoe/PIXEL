/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.onebeartoe.web.enabled.pixel.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
//import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.File;
import java.io.IOException;


public class mameRom2Name  {

    public static void getGAMEName(String romName) {

        try (JsonParser jParser = new JsonFactory()
				.createParser(new File("mame2.json"));) {

            // loop until token equal to "}"
            while (jParser.nextToken() != JsonToken.END_OBJECT) {

                String fieldname = jParser.getCurrentName();
				
                if ("rom".equals(fieldname)) {
                    // current token is "name",
                    // move to next, which is "name"'s value
                    jParser.nextToken();
                    System.out.println(jParser.getText());
                }

                if ("title".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println(jParser.getIntValue());
                }
                
                if ("year".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println(jParser.getIntValue());
                }
            }

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
        
      // return "al";
    }
    
   
   
}
    

