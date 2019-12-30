// Max 7219 Single Color LED Matrix, 7 Segment Display, and OLED Accessories for Pixelcade
// 
// MD_MAX72XX library can be found at https://github.com/MajicDesigns/MD_MAX72XX
//

#include "LedControl.h"
#include <MD_Parola.h>
#include <MD_MAX72xx.h>
#include <SPI.h>
//#include "SSD1306Ascii.h"
//#include "SSD1306AsciiAvrI2c.h"
#include <Wire.h>
#include "SSD1306Ascii.h"
#include "SSD1306AsciiWire.h"

//TO DO add firmware string that java can read, have strings for led matrix , oled displays, and any other accessories
// TO DO would also be nice to add high scores

#define HANDSHAKE_INIT "pixelcadeh"   //the pixelcade software on the PC or Pi will send this string to the Arduino to trigger the handshake
#define HANDSHAKE_RETURN 45           //once pixelcadeh is received, arduino will send back 45 45. Once the PC receives that, we know we are communicating correctly
#define FW_VERSION "MAX70001"         //PMAX is the platform or short for PMAX7219 in this case and 0001 is the version

// ***************** 7 Segment ****************
LedControl lc=LedControl(9,7,8,2); //pins for the 7 segment modules and 2 is the number of modules

// ****************** LED MATRIX ***************
#define HARDWARE_TYPE MD_MAX72XX::FC16_HW
#define MAX_DEVICES 4                 //for 4 MAX7219 LED modules, change this number if you have more, up to 8 are supported
#define CLK_PIN   13
#define DATA_PIN  11
#define CS_PIN    10
MD_Parola P = MD_Parola(HARDWARE_TYPE, CS_PIN, MAX_DEVICES);\
// LED MATRIX Scrolling parameters
uint8_t scrollSpeed = 25;    // default frame delay value
textEffect_t scrollEffect = PA_SCROLL_LEFT;
textPosition_t scrollAlign = PA_LEFT;
uint16_t scrollPause = 2000; // in milliseconds
unsigned long delaytime=250;

// Global message buffers shared by Serial and Scrolling functions
#define  BUF_SIZE  250     //had to increase the buffer size from the default of 75 becasue our string length was exceeding
char curMessage[BUF_SIZE] = { "" };
char newMessage[BUF_SIZE] = { "Pixelcade" };
bool newMessageAvailable = true;
// **********************************************

//*************** OLED display
#define RTN_CHECK 1
// 0X3C+SA0 - 0x3C or 0x3D
#define I2C_ADDRESS 0x3C
// Define proper RST_PIN if required.
#define RST_PIN -1
SSD1306AsciiWire oled;
// Ticker state. Maintains text pointer queue and current ticker state.
//TickerState state;
//uint32_t tickTime = 0;
//String readAdc(uint8_t first, uint8_t last);
//int n = 0;
//char* oledScrollBottom[] = {"        Pixelcade Sub Display Accessory: Waiting to Connect..."};
//**********************************

bool handShakeResponse = false;
int gameYearArray[4] = {0x67, 0x10, 8, 8};
String gameTitle="";
String gameYear="";
String gameManufacturer="";
String gameGenre="";
String gameRating="";

int firstSeparator=0;
int secondSeparator=0;
int thirdSeparator=0;
int fourthSeparator=0;
int fifthSeparator=0;

void readSerial(void)
{
  static char *cp = newMessage;

  while (Serial.available())
  {
    *cp = (char)Serial.read();
    if ((*cp == '\n') || (cp - newMessage >= BUF_SIZE-2))  {   // end of message character or full buffer
   
      *cp = '\0'; // end the string
      cp = newMessage;  //reset the pointer for the next incoming string
      
      //now let's test if we received the handshake string from pixelcade and response back if yes. But if not, we'll just continue
      if (String(cp).equals(HANDSHAKE_INIT)) {
       
        newMessageAvailable = false;
        handShakeResponse = true;
        Serial.println("went here");  //not sure why, but if this is removed, the handshake with pixelcade breaks so leave this here
        
      } else {

            firstSeparator = String(cp).indexOf('%');
            secondSeparator = String(cp).indexOf('%', firstSeparator+1);
            thirdSeparator = String(cp).indexOf('%', secondSeparator+1);
            fourthSeparator = String(cp).indexOf('%', thirdSeparator+1);
            fifthSeparator = String(cp).indexOf('%', fourthSeparator+1);
            
            gameTitle = String(cp).substring(0, firstSeparator); //as we don't have much memory in our buffer, need to shorten the game title if too long
           
            gameYear = String(cp).substring(firstSeparator+1, secondSeparator);
            gameManufacturer = String(cp).substring(secondSeparator+1, thirdSeparator);
            gameGenre = String(cp).substring(thirdSeparator+1, fourthSeparator);
            gameRating = String(cp).substring(fourthSeparator+1, fifthSeparator); 
           
            int YearStr_len = gameYear.length(); 
      
            Serial.print(gameTitle);
            Serial.print("\n");
            
            for (int i = 0; i < 4; i++) {
              gameYearArray[i] = gameYear.substring(i, i+1).toInt();
            }

            //newMessage is what actuallly gets sent to the LED matrix so we'll manipulate our desired string here based on the meta-data we have available, you can customize here as you like!
            String MatrixMessage = gameTitle + " from " + gameYear + " by " + gameManufacturer;
            MatrixMessage.toCharArray(newMessage, BUF_SIZE);
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
    lc.setDigit(0,5,gameYearArray[0],false);
    lc.setDigit(0,4,gameYearArray[1],false);
    lc.setDigit(0,3,gameYearArray[2],false);
    lc.setDigit(0,2,gameYearArray[3],false);
}

void display7SegmentInit() {
     lc.setChar(0,7,'p',false);
     lc.setDigit(0,6,1,false); //note i and x are not avaailble chars on 7 digit displays , available chars here https://en.wikipedia.org/wiki/Seven-segment_display
     lc.setChar(0,5,'e',false);
     lc.setChar(0,4,'L',false);
     lc.setChar(0,3,'c',false);
     lc.setChar(0,2,'a',false);
     lc.setChar(0,1,'d',false);
     lc.setChar(0,0,'e',false);
     delay(5000);
}

void MovingDigits7Segment() 
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


void writeOled () {
  oled.setFont(Adafruit5x7);
  oled.clear();
  oled.println(gameTitle);
  oled.println(gameManufacturer);
  oled.println();
  oled.set2X();
  oled.println(gameYear);
  oled.set1X();
  oled.println(gameGenre);
  oled.println(gameRating);

   // Use Adafruit5x7 font, field at row 2, set1X, columns 16 through 100.
  //oled.tickerInit(&state, Adafruit5x7, 7, false, 16, 100); //this worked but couldn't figure out how to change dynamically
}

void setup()
{
  
  /////******** for OLED screen ***************
   Wire.begin();
   Wire.setClock(400000L);
    #if RST_PIN >= 0
    oled.begin(&Adafruit128x64, I2C_ADDRESS, RST_PIN);
    #else // RST_PIN >= 0
    oled.begin(&Adafruit128x64, I2C_ADDRESS);
    #endif // RST_PIN >= 0

   // Use Adafruit5x7 font, field at row 2, set1X, columns 16 through 100.
  //oled.tickerInit(&state, Adafruit5x7, 7, false, 16, 100);
  
  /////*****************************************
  
  Serial.begin(57600);
  Serial.setTimeout(50);
  Serial.print(FW_VERSION);
  
  P.begin();
  P.displayText(curMessage, scrollAlign, scrollSpeed, scrollPause, scrollEffect, scrollEffect);

  // the zero refers to the MAX7219 number, it is zero for 1 chip
  lc.shutdown(0,false);// turn off power saving, enables display
  lc.setIntensity(0,8);// sets brightness (0~15 possible values)
  lc.clearDisplay(0);// clear screen
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
      MovingDigits7Segment(); 
      display7Segment();                //write game year to the 7 segment display
      writeOled ();                     //write to the OLED
      newMessageAvailable = false;
    }
    P.displayReset();
  }
  readSerial();
  
 /*
 //******* For OLED display ***********
  if (tickTime <= millis()) {
    tickTime = millis() + 30;

   // Should check for error. rtn < 0 indicates error.
    int8_t rtn = oled.tickerTick(&state);

    // See above for definition of RTN_CHECK.
    if (rtn <= RTN_CHECK) {
      // Should check for error. Return of false indicates error.
      oled.tickerText(&state, oledScrollBottom[(n++)%3]);
    }
  }
  //***********************************
  */

  
}
