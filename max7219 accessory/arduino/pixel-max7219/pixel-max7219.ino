// Max 7219 Single Color LED Matrix, 7 Segment Display, and OLED Accessories for Pixelcade
// 
// MD_MAX72XX library can be found at https://github.com/MajicDesigns/MD_MAX72XX
//

#include "LedControl.h"
#include <MD_Parola.h>
#include <MD_MAX72xx.h>
#include <SPI.h>
#include "SSD1306Ascii.h"
#include "SSD1306AsciiAvrI2c.h"

//TO DO add firmware string that java can read, have strings for led matrix , oled displays, and any other accessories
// TO DO would also be nice to add high scores

// Define the number of devices we have in the chain and the hardware interface

#define HARDWARE_TYPE MD_MAX72XX::FC16_HW

#define MAX_DEVICES 4                 //for 4 MAX7219 LED modules, change this number if you have more, up to 8 are supported
#define CLK_PIN   13
#define DATA_PIN  11
#define CS_PIN    10
#define HANDSHAKE_INIT "pixelcadeh"   //the pixelcade software on the PC or Pi will send this string to the Arduino to trigger the handshake
#define HANDSHAKE_RETURN 45           //once pixelcadeh is received, arduino will send back 45 45. Once the PC receives that, we know we are communicating correctly
#define FW_VERSION "MAX70001"         //PMAX is the platform or short for PMAX7219 in this case and 0001 is the version

LedControl lc=LedControl(9,7,8,2); //pins for the 7 segment modules and 2 is the number of modules

// HARDWARE SPI
MD_Parola P = MD_Parola(HARDWARE_TYPE, CS_PIN, MAX_DEVICES);

// Scrolling parameters
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
bool handShakeResponse = false;
int gameYearArray[4] = {0x67, 0x10, 8, 8};

String gameTitle;
String gameYear;
String gameManufacturer;
String gameGenre;
String gameRating;
String MessageMax7219Matrix;
String pixelcadeIncomingMessageString;

int firstCommaIndex;
int secondCommaIndex;
int thirdCommaIndex;
int fourthCommaIndex;
int fifthCommaIndex;

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
        Serial.println("went here");
        
      } else {

            firstCommaIndex = String(cp).indexOf('%');
            secondCommaIndex = String(cp).indexOf('%', firstCommaIndex+1);
            thirdCommaIndex = String(cp).indexOf('%', secondCommaIndex+1);
            fourthCommaIndex = String(cp).indexOf('%', thirdCommaIndex+1);
            fifthCommaIndex = String(cp).indexOf('%', fourthCommaIndex+1);
      
            
            gameTitle = String(cp).substring(0, firstCommaIndex);
            gameYear = String(cp).substring(firstCommaIndex+1, secondCommaIndex);
            gameManufacturer = String(cp).substring(secondCommaIndex+1, thirdCommaIndex);
            gameGenre = String(cp).substring(thirdCommaIndex+1, fourthCommaIndex);
            gameRating = String(cp).substring(fourthCommaIndex+1, fifthCommaIndex);  
      
           
            int YearStr_len = gameYear.length(); 
      
            Serial.print(gameTitle);
            Serial.print("\n");
            
            for (int i = 0; i < 4; i++) {
              gameYearArray[i] = gameYear.substring(i, i+1).toInt();
            }

            //newMessage is what actuallly gets sent to the LED matrix so we'll manipulate our desired string here based on the meta-data we have available, you can customize here as you like!
            String MatrixMessage = gameTitle + " from " + gameYear + " by " + gameManufacturer;
            // gameTitle + " from " + gameYear + " by " + gameManufacturer;
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
      MovingDigits7Segment(); 
      display7Segment();                //write game year to the 7 segment display
      newMessageAvailable = false;
    }
    P.displayReset();
  }
  readSerial();
}
