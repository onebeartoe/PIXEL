
package org.onebeartoe.pixel;

import ioio.lib.api.RgbLedMatrix;

/**
 * @author Roberto Marquez
 */
public class PixelEnvironment
{

    public RgbLedMatrix.Matrix KIND;

    public int frame_length;

    public int currentResolution;

    public PixelEnvironment(int id)
    {

        switch (id)
        {
            case 0:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
                frame_length = 1024;
                currentResolution = 16;
                break;
            case 1:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
                frame_length = 1024;
                currentResolution = 16;
                break;
            case 2:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //an early version of the PIXEL LED panels, only used in a few early prototypes
                frame_length = 2048;
                currentResolution = 32;
                break;
            case 3:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //the current version of PIXEL 32x32
                frame_length = 2048;
                currentResolution = 32;
                break;
            case 4:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x32;
                frame_length = 8192;
                currentResolution = 64;
                break;
            case 5:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x64;
                frame_length = 8192;
                currentResolution = 64;
                break;
            case 6:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_2_MIRRORED;
                frame_length = 8192;
                currentResolution = 64;
                break;
            case 7:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_4_MIRRORED;
                frame_length = 8192;
                currentResolution = 128;
            case 8:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_128x32; //horizontal
                frame_length = 8192;
                currentResolution = 128;
                break;
            case 9:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x128; //vertical mount
                frame_length = 8192;
                currentResolution = 128;
                break;
            case 10:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_64x64;
                frame_length = 8192;
                currentResolution = 128;
                break;
            default:
                KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 as the default
                frame_length = 2048;
                currentResolution = 32;
        }
    }
    
}
