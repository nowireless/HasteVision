package org.nowireless.nivision;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import org.nowireless.common.gui.BufferedImageCache;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.CompressionType;
import com.ni.vision.NIVision.FlattenType;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.RawData;

public class NiImageUtil {
	
	public static int IMAGE_TYPE_START_BYTE = 36;
	public static int WIDTH_START_BYTE = 40;
	public static int HEIGHT_START_BYTE = 48;
	public static int DATA_SIZE_START_BYTE = 148;
	public static int DATA_START_BYTE = 172;
	
	public static int calculateImagePadding(int totalSize, int width, int height, int channels) {
		return (totalSize - channels * width * height) / height;
	}
	
	public static int readInt(ByteBuffer buff, int startIndex) {
		byte integer[] = new byte[4];
        integer[0] = buff.get(startIndex+3);
        integer[1] = buff.get(startIndex+2);
        integer[2] = buff.get(startIndex+1);
        integer[3] = buff.get(startIndex);
        
        return ByteBuffer.wrap(integer).getInt();    
	}
	
	public static void dumpContents(RawData data) {
		ByteBuffer buf = data.getBuffer();
		int n = buf.remaining();
		for (int i = 0; i < n; i++) {
			byte b = buf.get();
			System.out.println(b);
		}
	}
	
	public static int getImageType(ByteBuffer buff) {
		return NiImageUtil.readInt(buff, IMAGE_TYPE_START_BYTE);
	}
	
	public static int getWidth(ByteBuffer buff) {
		return NiImageUtil.readInt(buff, WIDTH_START_BYTE);
	}
	
	public static int getHeight(ByteBuffer buff) {
		return NiImageUtil.readInt(buff, HEIGHT_START_BYTE);
	}
	
	public static int getDataSize(ByteBuffer buff) {
		return NiImageUtil.readInt(buff, DATA_SIZE_START_BYTE);
	}
	
	/**
	 * Not the most efficient way, but it works
	 * @param image
	 * @param cache
	 * @return
	 */
	public static BufferedImage niImage2BufferedImage(Image image, BufferedImageCache cache) {		
		RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_NONE, 0);
		ByteBuffer buff = data.getBuffer();
		int type = getImageType(buff);
		
		switch (type) {
		case 0:
			return handleGrayImage(buff, data, cache);
		case 4:
			return handleRGBImage(buff, data, cache);
		default:
			data.free();
			throw new IllegalArgumentException("Invalid Image type");
		}
		
		
	}
	
	private static BufferedImage handleRGBImage(ByteBuffer buff, RawData data, BufferedImageCache cache) {
		int width = getWidth(buff);
		int height = getHeight(buff);
		int dataSize = getDataSize(buff);
		int paddingSize = calculateImagePadding(dataSize, width, height, 4);
		
		BufferedImage ret = cache.get(BufferedImage.TYPE_3BYTE_BGR, 3, height, width);
		final byte[] targetPixels = ((DataBufferByte) ret.getRaster().getDataBuffer()).getData();
		
		int pos = 0;
    	byte [] pixel = new byte[4];
    	//Ni Image Pixel values
    	// Element 0 - B
    	// Element 1 - G
    	// Element 2 - R
    	// Element 3 - A
    	
    	//Buffered Image Pixel Values
    	// Element 0 - B
    	// Element 1 - G
    	// Element 2 - R
    	
    	buff.position(DATA_START_BYTE);
    	//System.out.println(buff.remaining());
    	for(int i = 0; i < height; i++) {
    		//Loop through the height//Rows of the image
    		//System.out.println("Height "+i);
	    	for(int j = 0; j < width; j++) {
	    		//System.out.println("Height "+i + " Width "+j+ " Pos: "+pos+" Remaining " + buff.remaining());
		    	//Read the Width/columns pixel in a row
	        	buff.get(pixel);
	        	targetPixels[pos++]=pixel[0];
	        	targetPixels[pos++]=pixel[1];
	        	targetPixels[pos++]=pixel[2];

	    	}
	    	buff.position(buff.position()+paddingSize);
    		//Throw away the padding bytes
	    }
		data.free();
		return ret;
	}
	
	private static BufferedImage handleGrayImage(ByteBuffer buff, RawData data, BufferedImageCache cache) {
		int width = getWidth(buff);
		int height = getHeight(buff);
		int dataSize = getDataSize(buff);
		int paddingSize = calculateImagePadding(dataSize, width, height, 1);
		
		BufferedImage ret = cache.get(BufferedImage.TYPE_BYTE_GRAY, 1, height, width);
		final byte[] targetPixels = ((DataBufferByte) ret.getRaster().getDataBuffer()).getData();
		
		int pos = 0;
    	
    	buff.position(DATA_START_BYTE);
    	//System.out.println(buff.remaining());
    	for(int i = 0; i < height; i++) {
    		//Loop through the height//Rows of the image
    		//System.out.println("Height "+i);
	    	for(int j = 0; j < width; j++) {
	    		//System.out.println("Height "+i + " Width "+j+ " Pos: "+pos+" Remaining " + buff.remaining());
		    	//Read the Width/columns pixel in a row
	    		byte val = buff.get();
	    		//Welp here we are turning this into a Binary image now
	        	if(val > 0) {
	        		targetPixels[pos++]= Byte.MAX_VALUE;
	        	} else {
	        		targetPixels[pos++]= 0;
	        	}
	    	}
	    	buff.position(buff.position()+paddingSize);
    		//Throw away the padding bytes
	    }
		data.free();
		return ret;		
	}
	
}
