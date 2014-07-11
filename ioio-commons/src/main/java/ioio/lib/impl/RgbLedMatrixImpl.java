/*
 * Copyright 2012 Ytai Ben-Tsvi. All rights reserved.
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
package ioio.lib.impl;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.IOException;

class RgbLedMatrixImpl extends AbstractResource implements RgbLedMatrix {
	final Matrix kind_;
	byte[] frame_;

	public RgbLedMatrixImpl(IOIOImpl ioio, Matrix kind)
			throws ConnectionLostException {
		super(ioio);
		kind_ = kind;
		frame_ = new byte[getFrameSize(kind)];
	}

	synchronized public void beginFrame() throws ConnectionLostException {
		try {
			//ioio_.protocol_.rgbLedMatrixWriteFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	synchronized public void writeFile(float fps) throws ConnectionLostException {
		try {
			ioio_.protocol_.rgbLedMatrixWriteFile(fps, getShifterLen(kind_));
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	@Override
	synchronized public void interactive() throws ConnectionLostException {
		try {
			ioio_.protocol_.rgbLedMatrixEnable(getShifterLen(kind_));
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	@Override
	synchronized public void playFile() throws ConnectionLostException {
		try {
			ioio_.protocol_.rgbLedMatrixEnable(0);
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	@Override
	synchronized public void frame(short[] rgb565)
			throws ConnectionLostException {
		if (rgb565.length != kind_.width * kind_.height) {
			throw new IllegalArgumentException("Frame length must be "
					+ kind_.width * kind_.height);
		}
		checkState();
		switch (kind_) {
		case ADAFRUIT_32x16:
			convertAdafruit32x16(rgb565, frame_);
			break;

		case SEEEDSTUDIO_32x16:
			convertSeeedStudio32x16(rgb565, frame_);
			break;

		case SEEEDSTUDIO_32x32:  //this is the one that is used
			convertSeeedStudio32x32(rgb565, frame_);
			break;

		case SEEEDSTUDIO_32x32_NEW:  //this was the seeed one that didn't work, it's not used
			convertSeeedStudio32x32New(rgb565, frame_);
			break;
		
		case SEEEDSTUDIO_64x32:
			convertSeeedStudio64x32(rgb565, frame_);
			break;	
			
		case SEEEDSTUDIO_32x64:
			convertSeeedStudio32x64(rgb565, frame_);
			break;	
			
		case SEEEDSTUDIO_2_MIRRORED:
			convertSeeedStudio32x64Mirrored(rgb565, frame_);
			break;	
			
		case SEEEDSTUDIO_4_MIRRORED:
			convertSeeedStudio32x128Mirrored(rgb565, frame_);
			break;	
			
		case SEEEDSTUDIO_64x64:
			convertSeeedStudio64x64(rgb565, frame_);
			break;	
		
		case SEEEDSTUDIO_128x32:
			convertSeeedStudio128x32(rgb565, frame_);
			break;	
			
		case SEEEDSTUDIO_32x128:
			convertSeeedStudio32x128(rgb565, frame_);
			break;	
		

		default:
			throw new IllegalStateException("This format is not supported");
		}

		try {
			ioio_.protocol_.rgbLedMatrixFrame(frame_);
		} catch (IOException e) {
			throw new ConnectionLostException(e);
		}
	}

	@Override
	synchronized public void close() {
		super.close();
		ioio_.closeRgbLedMatrix();
	}

	private static void convertAdafruit32x16(short[] rgb565, byte[] dest) {
		int outIndex = 0;
		for (int subframe = 0; subframe < 3; ++subframe) {
			int inIndex = 0;
			for (int row = 0; row < 8; ++row) {
				for (int col = 0; col < 32; ++col) {
					int pixel1 = ((int) rgb565[inIndex]) & 0xFFFF;
					int pixel2 = ((int) rgb565[inIndex + 256]) & 0xFFFF;

					int r1 = (pixel1 >> (11 + 2 + subframe)) & 1;
					int g1 = (pixel1 >> (5 + 3 + subframe)) & 1;
					int b1 = (pixel1 >> (0 + 2 + subframe)) & 1;

					int r2 = (pixel2 >> (11 + 2 + subframe)) & 1;
					int g2 = (pixel2 >> (5 + 3 + subframe)) & 1;
					int b2 = (pixel2 >> (0 + 2 + subframe)) & 1;

					dest[outIndex++] = (byte) (r1 << 5 | g1 << 4 | b1 << 3
							| r2 << 2 | g2 << 1 | b2 << 0);
					++inIndex;
				}
			}
		}
	}

	private static void convertSeeedStudio32x32New(short[] rgb565, byte[] dest) {
		int outIndex = 0;
		for (int subframe = 0; subframe < 3; ++subframe) {
			for (int row = 0; row < 256; row += 32) {
				for (int half = 0; half < 1024; half += 512) {
					for (int col = 0; col < 32; ++col) {
						final int inIndex = col + half + row;
						int pixel1 = ((int) rgb565[inIndex]) & 0xFFFF;
						int pixel2 = ((int) rgb565[inIndex + 256]) & 0xFFFF;

						int r1 = (pixel1 >> (11 + 2 + subframe)) & 1;
						int g1 = (pixel1 >> (5 + 3 + subframe)) & 1;
						int b1 = (pixel1 >> (0 + 2 + subframe)) & 1;

						int r2 = (pixel2 >> (11 + 2 + subframe)) & 1;
						int g2 = (pixel2 >> (5 + 3 + subframe)) & 1;
						int b2 = (pixel2 >> (0 + 2 + subframe)) & 1;

						dest[outIndex++] = (byte) (r1 << 5 | g1 << 4 | b1 << 3
								| r2 << 2 | g2 << 1 | b2 << 0);
					}
				}
			}
		}
	}

	// Input format:
	// =============
	// Each pixel is in an RGB565 format:
	// +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
	// | 15 | 14 | 13 | 12 | 11 | 10 |  9 |  8 |  7 |  6 |  5 |  4 |  3 |  2 |  1 |  0 |
	// +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
	// | r4 | r3 | r2 | r1 | r0 | g5 | g4 | g3 | g2 | g1 | g0 | b4 | b3 | b2 | b1 | b0 |
	// +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
	//
	// Pixels are ordered in a 1D array, row by row from top to bottom, each row left to right:
	// { p(0,0), p(1,0), .... (width-1, 0), p(0,1), ... p(width-1,height-1) }
	//
	// Output format:
	// ==============
	// Each frame comprises 3 sub-frames.
	// Each sub-frame comprises 8 rows (which do not each correspond to a single physical row, but
	// rather to one eighth of the total rows.
	// Each row comprises multiples of 32 dot-pairs, where each such pair defines the color value of
	// two pixels (physically on separate rows) on a single sub-frame.
	// a dot-pair is contained in a single byte, with the following format:
	// 
	// Sub-frame 0:
	// +----+----+----+----+----+----+----+----+
	// |  7 |  6 |  5 |  4 |  3 |  2 |  1 |  0 |
	// +----+----+----+----+----+----+----+----+
	// |  0 |  0 | R2 | G3 | B2 | r2 | g3 | b2 |
	// +----+----+----+----+----+----+----+----+
	// 
	// Sub-frame 1:
	// +----+----+----+----+----+----+----+----+
	// |  7 |  6 |  5 |  4 |  3 |  2 |  1 |  0 |
	// +----+----+----+----+----+----+----+----+
	// |  0 |  0 | R3 | R4 | B3 | r3 | g4 | b3 |
	// +----+----+----+----+----+----+----+----+
	//
	// Sub-frame 2:
	// +----+----+----+----+----+----+----+----+
	// |  7 |  6 |  5 |  4 |  3 |  2 |  1 |  0 |
	// +----+----+----+----+----+----+----+----+
	// |  0 |  0 | R4 | G5 | B4 | r4 | g5 | b4 |
	// +----+----+----+----+----+----+----+----+
	// 
	// Where R,G,B are the color values for the first (top) pixel and r,g,b are the color values for the
	// second pixel.
	//
	// The mapping between logical rows to physical rows is the following:
	// Each logical row (one of the eight) includes all the pixels on all the displays that are in
	// all the rows that have the same modulo-8 value.
	// For example, logical row 0 physical contains rows 0, 8, 16, 24 of all matrices.
	// row 1 contains physical rows 1, 9, 17, 25 of all matrices.
	// Within a row each pixel-pair contains the color value of two pixels that are 16 rows apart
	// and on the same column, so row 0 and row 16 go together, row 1 and 17, and so on, up to 7 and 23.
	// Then, physical row 8 is a continuation of row 0, etc.
	// Finally, several matrices daisy-chained together have their logical rows concatenated.
	//
	//  0 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
	//  1 bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
	//  ... 5 more ....
	//  7 hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
	//  8 iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
	//  9 jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
	//  ... 5 more ....
	// 15 pppppppppppppppppppppppppppppppp
	// 16 qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
	// 17 rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
	// ... 14 more ...
	//
	// The following physical rows will appear as the following logical rows:
	// { (a+q), (i+...), next matrix (a+q), (i+...) }
	// { (b+r), (j+...), next matrix (b+r), (j+...) }
	//
	// Finally, within each physical row, the pixels are sorted in a funny order, define by
	// SEEED_MAP.
	
	/**
	 * Find the output index of a pixel within a sub-frame, given its input coordinates.
	 * The y-value (row index) modulo 32 has to be [0..15]. The returned index also applies
	 * for (x, y+16) - sharing the same dot-pair.
	 * 
	 * @param x Column index.
	 * @param y Row index.
	 * @param width Width of the input image.
	 * @return The index within a sub-frame.
	 */
	private static int mapSeeedStudioIndex(int x, int y, int width, int height) {
		assert y % 32 < 16;
		int logicalRow = y % 8;
		boolean firstHalf = y % 16 < 8;
		int dotPairsPerLogicalRow = width * height / 16;
		int widthInMatrices = width / 32;
		int matrixX = x / 32;
		int matrixY = y / 32;
		int totalMatrices = width * height / 1024;
		int matrixNumber = totalMatrices - 1 - (matrixY * widthInMatrices + matrixX);
		int indexWithinMatrixRow = x % 32 + (firstHalf ? 0 : 32);
		int index = logicalRow * dotPairsPerLogicalRow
				+ matrixNumber * 64
				+ SEEED_MAP[indexWithinMatrixRow];
		return index;
	}
	
	private static void convertSeeedStudio(short[] rgb565, int width, byte[] dest) {
		final int height = rgb565.length / width;
		final int subframeSize = rgb565.length / 2;
		
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (y % 32 >= 16) continue;
				
				// This are the two indices of the pixel comprising a dot-pair in the input.
				int inputIndex0 = y * width + x;
				int inputIndex1 = (y + 16) * width + x;
				
				short color0 = rgb565[inputIndex0];
				// Take the top 3 bits of each {r,g,b}
				int r0 = (color0 >> 13) & 0x7;
				int g0 = (color0 >> 8) & 0x7;
				int b0 = (color0 >> 2) & 0x7;
										
				short color1 = rgb565[inputIndex1];
				// Take the top 3 bits of each {r,g,b}
				int r1 = (color1 >> 13) & 0x7;
				int g1 = (color1 >> 8) & 0x7;
				int b1 = (color1 >> 2) & 0x7;
				
				// Hack: Odd matrices have their G/B channels swapped.
				boolean oddMatrix = (((x / 32) + (y / 32) * (width / 32)) & 1) != 0;
				
				if (oddMatrix) {
					int t = g0;
					g0 = b0;
					b0 = t;
					t = g1;
					g1 = b1;
					b1 = t;
				}

				for (int subframe = 0; subframe < 3; ++subframe) {
					int dotPair =
							(r0 & 1) << 5
							| (g0 & 1) << 4
							| (b0 & 1) << 3
							| (r1 & 1) << 2
							| (g1 & 1) << 1
							| (b1 & 1) << 0;
					int indexWithinSubframe = mapSeeedStudioIndex(x, y, width, height);
					int indexWithinOutput = subframe * subframeSize + indexWithinSubframe;
					dest[indexWithinOutput] = (byte) dotPair;
					r0 >>= 1;
					g0 >>= 1;
					b0 >>= 1;
					r1 >>= 1;
					g1 >>= 1;
					b1 >>= 1;
				}
			}
		}
	}
	
	private static void convertSeeedStudio32x32(short[] rgb565, byte[] dest) {
		convertSeeedStudio(rgb565, 32, dest); //32 is the width
	}
	
	private static void convertSeeedStudio64x32(short[] rgb565, byte[] dest) {
		convertSeeedStudio(rgb565, 64, dest);
	}
	
	private static void convertSeeedStudio32x64(short[] rgb565, byte[] dest) {
		convertSeeedStudio(rgb565, 32, dest);
	}
	
	private static void convertSeeedStudio32x64Mirrored(short[] rgb565, byte[] dest) { //2 mirrored
		convertSeeedStudio(rgb565, 64, dest);
	}
	
	private static void convertSeeedStudio32x128Mirrored(short[] rgb565, byte[] dest) { //4 mirrored
		convertSeeedStudio(rgb565, 32, dest);
	}
	
	private static void convertSeeedStudio128x32(short[] rgb565, byte[] dest) {
		convertSeeedStudio(rgb565, 128, dest);
	}
	
	private static void convertSeeedStudio32x128(short[] rgb565, byte[] dest) {
		convertSeeedStudio(rgb565, 32, dest);
	}
	
	private static void convertSeeedStudio64x64(short[] rgb565, byte[] dest) {
		convertSeeedStudio(rgb565, 64, dest);
	}
	
	
	
	private static void convertSeeedStudio32x16(short[] rgb565, byte[] dest) {
		int outIndex = 0;
		for (int subframe = 0; subframe < 3; ++subframe) {
			int inIndex = 0;
			for (int row = 0; row < 8; ++row) {
				for (int col = 0; col < 32; ++col) {
					int pixel1 = ((int) rgb565[inIndex]) & 0xFFFF;
					int pixel2 = ((int) rgb565[inIndex + 256]) & 0xFFFF;

					int r1 = (pixel1 >> (11 + 2 + subframe)) & 1;
					int g1 = (pixel1 >> (5 + 3 + subframe)) & 1;
					int b1 = (pixel1 >> (0 + 2 + subframe)) & 1;

					int r2 = (pixel2 >> (11 + 2 + subframe)) & 1;
					int g2 = (pixel2 >> (5 + 3 + subframe)) & 1;
					int b2 = (pixel2 >> (0 + 2 + subframe)) & 1;

					dest[outIndex + SEEED_MAP[col]] = (byte) (r1 << 5 | g1 << 4 | b1 << 3);
					dest[outIndex + SEEED_MAP[col + 32]] = (byte) (r2 << 5
							| g2 << 4 | b2 << 3);
					++inIndex;
				}
				outIndex += 64;
			}
		}
	}

	private static final int[] SEEED_MAP = {
		 7,  6,  5,  4,  3,  2,  1,  0,
		23, 22, 21, 20, 19, 18, 17, 16,
		39, 38, 37, 36, 35, 34, 33, 32,
		55, 54, 53,	52, 51, 50, 49, 48,
		 8,  9, 10, 11, 12, 13, 14, 15,
		24, 25, 26, 27, 28, 29, 30, 31,
		40, 41, 42, 43, 44, 45, 46, 47,
		56, 57, 58, 59, 60,	61, 62, 63,
		};

	public static int getShifterLen(Matrix kind) {
		switch (kind) {
		case ADAFRUIT_32x16:
			return 1;

		case SEEEDSTUDIO_32x16:
		case SEEEDSTUDIO_32x32:
		case SEEEDSTUDIO_32x32_NEW:
			return 2;
			
		case SEEEDSTUDIO_64x32:
		case SEEEDSTUDIO_32x64:
		case SEEEDSTUDIO_2_MIRRORED:
			return 4; 
			
		case SEEEDSTUDIO_4_MIRRORED:
		case SEEEDSTUDIO_64x64:
		case SEEEDSTUDIO_128x32:
		case SEEEDSTUDIO_32x128:
			return 8; 	

		default:
			throw new IllegalStateException("Unsupported kind.");
		}
	}

	private static int getFrameSize(Matrix kind) {
		switch (kind) {
		case ADAFRUIT_32x16:
			return 768;

		case SEEEDSTUDIO_32x16:
		case SEEEDSTUDIO_32x32:
		case SEEEDSTUDIO_32x32_NEW:
			return 1536;
			
			
		case SEEEDSTUDIO_64x32:
		case SEEEDSTUDIO_32x64:
		case SEEEDSTUDIO_2_MIRRORED:
			return 3072; 
			
		case SEEEDSTUDIO_4_MIRRORED:
		case SEEEDSTUDIO_64x64:
		case SEEEDSTUDIO_128x32:
		case SEEEDSTUDIO_32x128:
			return 6144;		

		default:
			throw new IllegalStateException("Unsupported kind.");
		}
	}
}
