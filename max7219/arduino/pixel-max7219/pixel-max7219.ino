// Use the Parola library to scroll text on the display
//
// Demonstrates the use of the scrolling function to display text received
// from the serial interface
//
// User can enter text on the serial monitor and this will display as a
// scrolling message on the display.
// Speed for the display is controlled by a pot on SPEED_IN analog in.
// Scrolling direction is controlled by a switch on DIRECTION_SET digital in.
// Invert ON/OFF is set by a switch on INVERT_SET digital in.
//
// UISwitch library can be found at https://github.com/MajicDesigns/MD_UISwitch
// MD_MAX72XX library can be found at https://github.com/MajicDesigns/MD_MAX72XX
//

#include "LedControl.h"
#include <MD_Parola.h>
#include <MD_MAX72xx.h>
#include <SPI.h>

//TO DO add firmware string that java can read, have strings for led matrix , oled displays, and any other accessories
// TO DO would also be nice to add high scores

// Define the number of devices we have in the chain and the hardware interface
// NOTE: These pin numbers will probably not work with your hardware and may
// need to be adapted
//#define HARDWARE_TYPE MD_MAX72XX::PAROLA_HW
#define HARDWARE_TYPE MD_MAX72XX::FC16_HW
//#define HARDWARE_TYPE MD_MAX72XX::GENERIC_HW

#define MAX_DEVICES 4                 //for 4 MAX7219 LED modules, change this number if you have more, up to 8 are supported
#define CLK_PIN   13
#define DATA_PIN  11
#define CS_PIN    10
#define HANDSHAKE_INIT "pixelcadeh"   //the pixelcade software on the PC or Pi will send this string to the Arduino to trigger the handshake
#define HANDSHAKE_RETURN 45           //once pixelcadeh is received, arduino will send back 45 45. Once the PC receives that, we know we are communicating correctly
#define FW_VERSION "PMAX0001"         //PMAX is the platform or short for PMAX7219 in this case and 0001 is the version

LedControl lc=LedControl(7,5,6,2); //pins for the 7 segment modules and 2 is the number of modules

// HARDWARE SPI
MD_Parola P = MD_Parola(HARDWARE_TYPE, CS_PIN, MAX_DEVICES);

// Scrolling parameters
uint8_t scrollSpeed = 25;    // default frame delay value
textEffect_t scrollEffect = PA_SCROLL_LEFT;
textPosition_t scrollAlign = PA_LEFT;
uint16_t scrollPause = 2000; // in milliseconds
unsigned long delaytime=250;

// Global message buffers shared by Serial and Scrolling functions
#define	BUF_SIZE	75
char curMessage[BUF_SIZE] = { "" };
char newMessage[BUF_SIZE] = { "Connecting..." };
bool newMessageAvailable = true;
bool handShakeResponse = false;
int gameYear[4] = {0x67, 0x10, 8, 8};

void readSerial(void)
{
  static char *cp = newMessage;

  while (Serial.available())
  {
    *cp = (char)Serial.read();
    if ((*cp == '\n') || (cp - newMessage >= BUF_SIZE-2)) // end of message character or full buffer
    {
      *cp = '\0'; // end the string
      // restart the index for next filling spree and flag we have a message waiting
      
      cp = newMessage;

      String pixelcadeIncomingMessageString;
      pixelcadeIncomingMessageString = String(pixelcadeIncomingMessageString + cp);
      int firstCommaIndex = pixelcadeIncomingMessageString.indexOf(',');
      int secondCommaIndex = pixelcadeIncomingMessageString.indexOf(',', firstCommaIndex+1);
      String gameTitleForMatrix = pixelcadeIncomingMessageString.substring(0, firstCommaIndex);
      String gameYearFor7Segment = pixelcadeIncomingMessageString.substring(firstCommaIndex+1, secondCommaIndex);
      int TitleStr_len = gameTitleForMatrix.length() + 1; 
      int YearStr_len = gameYearFor7Segment.length(); 
      // Copy string to char array 
      gameTitleForMatrix.toCharArray(cp, TitleStr_len);
      cp = newMessage;
      //do the same for the year for the 7segment
      //gameYearFor7Segment.toCharArray(gameYear,YearStr_len);
     
      for (int i = 0; i < 4; i++) {
        gameYear[i] = gameYearFor7Segment.substring(i, i+1).toInt();
      }
      
      //Serial.println(gameYear[0]);
      //Serial.println(gameYear[1]);
      //Serial.println(gameYear[2]);
      //Serial.println(gameYear[3]);
      
      Serial.print(cp);
      Serial.print("\n");
    

      if (gameTitleForMatrix.equals(HANDSHAKE_INIT)) {
        newMessageAvailable = false;
        handShakeResponse = true;
      } else {
        newMessageAvailable = true;
        handShakeResponse = false;
      }
    }
    
    else  // move char pointer to next position
      cp++;
  }
}

void display7Segment()
{
    lc.setDigit(0,5,gameYear[0],false);
    lc.setDigit(0,4,gameYear[1],false);
    lc.setDigit(0,3,gameYear[2],false);
    lc.setDigit(0,2,gameYear[3],false);
}

void display7SegmentInit() {
     lc.setChar(0,7,'p',false);
     lc.setDigit(0,6,1,false); //i and x are not avaailble chars o 7 digits , available chars here https://en.wikipedia.org/wiki/Seven-segment_display
     lc.setChar(0,5,'e',false);
     lc.setChar(0,4,'L',false);
     lc.setChar(0,3,'c',false);
     lc.setChar(0,2,'a',false);
     lc.setChar(0,1,'d',false);
     lc.setChar(0,0,'e',false);
     delay(5000);
}

void moving_digits() 
{
  
  lc.clearDisplay(0);
  for (int a=0; a<8; a++)
  {
    lc.setDigit(0,a,8,false);
    delay(100);
  }
  delay(100);
  lc.clearDisplay(0);
}

void setup()
{
  
  Serial.begin(57600);
  Serial.setTimeout(50);
  Serial.print(FW_VERSION);
  
  P.begin();
  P.displayText(curMessage, scrollAlign, scrollSpeed, scrollPause, scrollEffect, scrollEffect);

  // the zero refers to the MAX7219 number, it is zero for 1 chip
  lc.shutdown(0,false);// turn off power saving, enables display
  lc.setIntensity(0,8);// sets brightness (0~15 possible values)
  lc.clearDisplay(0);// clear screen
  //moving_digits() ;
  display7SegmentInit();
}

void loop()
{
  if (P.displayAnimate())
  {
    
    if (handShakeResponse) {
       Serial.write(HANDSHAKE_RETURN);
       Serial.write(HANDSHAKE_RETURN);
       handShakeResponse = false;
    }
    
    if (newMessageAvailable)
    {
      strcpy(curMessage, newMessage);
      moving_digits(); 
      display7Segment();                //write game year to the 7 segment display
      newMessageAvailable = false;
    }
    P.displayReset();
  }
  readSerial();
}
