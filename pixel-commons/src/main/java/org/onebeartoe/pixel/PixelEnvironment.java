
package org.onebeartoe.pixel;

import ioio.lib.api.RgbLedMatrix;

/**
 * @author Roberto Marquez
 */
public class PixelEnvironment
{
    public RgbLedMatrix.Matrix LED_MATRIX;

    public int frame_length;

    public int currentResolution;

    public PixelEnvironment(int id)
    {
        setById(id);
    }
    
    public void setById(int id)
    {
        switch (id)
        {
            case 0:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
                frame_length = 1024;
                currentResolution = 16;
                break;
            case 1:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
                frame_length = 1024;
                currentResolution = 16;
                break;
            case 2:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //an early version of the PIXEL LED panels, only used in a few early prototypes
                frame_length = 2048;
                currentResolution = 32;
                break;
            case 3:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //the current version of PIXEL 32x32
                frame_length = 2048;
                currentResolution = 32;
                break;
            case 4:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x32;
                frame_length = 8192;
                currentResolution = 64;
                break;
            case 5:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x64;
                frame_length = 8192;
                currentResolution = 64;
                break;
            case 6:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_2_MIRRORED;
                frame_length = 8192;
                currentResolution = 64;
                break;
            case 7:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_4_MIRRORED;
                frame_length = 8192;
                currentResolution = 128;
            case 8:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_128x32; //horizontal
                frame_length = 8192;
                currentResolution = 128;
                break;
            case 9:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x128; //vertical mount
                frame_length = 8192;
                currentResolution = 128;
                break;
            case 10:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x64;
                frame_length = 8192;
                currentResolution = 128;
                break;
             case 11:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32;
                frame_length = 2048;
                currentResolution = 32; 
                break;	 
            case 12:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_ColorSwap;
                frame_length = 2048;
                currentResolution = 32; 
                break;	 	 
            case 13:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32;
                frame_length = 4096;
                currentResolution = 64; 
                break;	
            case 14:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x64;
                frame_length = 8192;
                currentResolution = 128; 
                break;
            case 15:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_128x32;
                frame_length = 8192;
                currentResolution = 128; 
                break;	 	 	
            case 16:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x128;
                frame_length = 8192;
                currentResolution = 128; 
                break;	
            case 17:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x16;
                frame_length = 2048;
                currentResolution = 6416; 
                break;	 
            case 18:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32_MIRRORED;
                frame_length = 8192;
                currentResolution = 128999;   //had to add the 999 to indicate mirroring because it won't re-encode if for example 4 32x32 mirror is selected and then 64x32 mirror 
                break;	 
            case 19:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_256x16;
                frame_length = 8192;
                currentResolution = 25616; 
                break;	
            case 20:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_MIRRORED;
                frame_length = 4096;
                currentResolution = 64999; //had to make unique
                break;	
            case 21:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32_4X_MIRRORED;
                frame_length = 8192;
                currentResolution = 128; 
                break;	
            case 22:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_128x16;
                frame_length = 4096;
                currentResolution = 12816; 
                break;	
            case 23:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ALIEXPRESS_RANDOM1_32x32;
                frame_length = 2048;
                currentResolution = 32; 
                break;	
            case 24:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x32_ColorSwap;
                frame_length = 4096;
                currentResolution = 64; 
                break;	
            case 25:
            	KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_64x64_ColorSwap;
                frame_length = 8192;
                currentResolution = 128; 
                break;
            default:
                LED_MATRIX = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x32; //v2 as the default
                frame_length = 2048;
                currentResolution = 32;
        }        
    }
}
