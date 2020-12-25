// Simple I2C test for ebay 128x64 oled.
// Use smaller faster AvrI2c class in place of Wire.
// Edit AVRI2C_FASTMODE in SSD1306Ascii.h to change the default I2C frequency.
//
#include "SSD1306Ascii.h"
#include "SSD1306AsciiAvrI2c.h"

// 0X3C+SA0 - 0x3C or 0x3D
#define I2C_ADDRESS 0x3C
#define HANDSHAKE_INIT "pixelcadeh"   //the pixelcade software on the PC or Pi will send this string to the Arduino to trigger the handshake
#define HANDSHAKE_RETURN 45           //once pixelcadeh is received, arduino will send back 45 45. Once the PC receives that, we know we are communicating correctly
#define FW_VERSION "OLE10001"         //PMAX is the platform or short for PMAX7219 in this case and 0001 is the version
#define  BUF_SIZE  75
// Define proper RST_PIN if required.
#define RST_PIN -1



char curMessage[BUF_SIZE] = { "" };
char newMessage[BUF_SIZE] = { "Pixelcade" };
bool newMessageAvailable = true;
bool handShakeResponse = false;
int gameYear[4] = {0x67, 0x10, 8, 8};
SSD1306AsciiAvrI2c oled;

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
      
      for (int i = 0; i < 4; i++) {
        gameYear[i] = gameYearFor7Segment.substring(i, i+1).toInt();
      }
      
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





//------------------------------------------------------------------------------
void setup() {

Serial.begin(57600);
Serial.setTimeout(50);
Serial.print(FW_VERSION);

  

#if RST_PIN >= 0
  oled.begin(&Adafruit128x64, I2C_ADDRESS, RST_PIN);
#else // RST_PIN >= 0
  oled.begin(&Adafruit128x64, I2C_ADDRESS);
#endif // RST_PIN >= 0
  // Call oled.setI2cClock(frequency) to change from the default frequency.

  oled.setFont(Adafruit5x7);

  uint32_t m = micros();
  oled.clear();
  oled.println("Pixelcade 2.3.6");
  oled.println("LED Marquee");
  oled.println();
  oled.set2X();
  oled.println("Pacman");
  oled.set1X();
  oled.print("by Midway\n");
  oled.print("1984");
}
//------------------------------------------------------------------------------
void loop()
{
  
    
    if (handShakeResponse) {
       Serial.write(HANDSHAKE_RETURN);
       Serial.write(HANDSHAKE_RETURN);
       handShakeResponse = false;
    }
    
    if (newMessageAvailable)
    {
      strcpy(curMessage, newMessage);
      oled.print(newMessage);
      //moving_digits(); 
      //display7Segment();                //write game year to the 7 segment display
      newMessageAvailable = false;
    }
   
  
  readSerial();
}
