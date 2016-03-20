package com.kennedyrobotics.vision.other;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;
import org.nowireless.common.gui.BufferedImageCache;
import org.nowireless.nivision.NiImageUtil;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.CompressionType;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.FlattenType;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.RGBValue;
import com.ni.vision.NIVision.RawData;
import com.ni.vision.NIVision.ShapeMode;

public class ImageTest {
	@Ignore
	@Test
	public void imageConversion() throws IOException {
		int w = 200;
		int h = 100;
		
		Image image = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		NIVision.imaqSetImageSize(image, w, h);
		
		NIVision.Rect rect = new NIVision.Rect(0, 0, h, w/2);
        NIVision.imaqDrawShapeOnImage(image, image, rect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 70000000);
		NIVision.Rect rect2 = new NIVision.Rect(0, w/2, h, w/2);
        NIVision.imaqDrawShapeOnImage(image, image, rect2, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, -70000000);

        long start = System.currentTimeMillis();
        RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_NONE,0);
        ByteBuffer buff = data.getBuffer();
        
        //System.out.println(buff.order());
        
        assertEquals(4, NiImageUtil.getImageType(buff));

        assertEquals(200, NiImageUtil.getWidth(buff));

        assertEquals(100, NiImageUtil.getHeight(buff));
           
        int dataSize = NiImageUtil.getDataSize(buff);
        assertEquals(83200, dataSize);
        
        int dataStartByte  = 172;
        //Test to see if there are zeros before the start
        assertEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE  - 12));
        assertEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE- 8));
        assertEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE  - 4));
        //Make sure that there is data at the data start byte.
        //Since we have drawn over this pixel it should not be the color black;
        assertNotEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE));

        int paddingSize = NiImageUtil.calculateImagePadding(dataSize, w, h, 4);
        assertEquals(32, paddingSize);

		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
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
    	
    	buff.position(dataStartByte);
    	System.out.println(buff.remaining());
    	for(int i = 0; i < h; i++) {
    		//Loop through the height//Rows of the image
//    		System.out.println("Height "+i);
	    	for(int j = 0; j < w; j++) {
//	    		System.out.println("Height "+i + " Width "+j+ " Pos: "+pos+" Remaining " + buff.remaining());
		    	//Read the Width/columns pixel in a row
	        	buff.get(pixel);
	        	targetPixels[pos++]=pixel[0];
	        	targetPixels[pos++]=pixel[1];
	        	targetPixels[pos++]=pixel[2];

	    	}
	    	for(int j = 0; j < paddingSize; j++) {
	    		//Throw away the padding bytes
	        	assertEquals(Integer.toString(j),0, buff.get());
	    	}
	    }
    	assertEquals(0, buff.remaining());
    	assertEquals(3*100*200, pos);

    	long delta = System.currentTimeMillis()-start;
    	System.out.println("Time "+ delta + " ms");
    	
//		RGBValue value = new RGBValue();
//		NIVision.imaqWriteFile(image, "ni.jpg", value);
//
//		File outFile = new File("buffered.jpg");
//		ImageIO.write(ret, "jpg", outFile);
	}
	
	@Test
	public void imageConversion2() throws IOException{
		int w = 1920;
		int h = 1080;
		
		Image image = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		NIVision.imaqSetImageSize(image, w, h);
		
		NIVision.Rect rect = new NIVision.Rect(0, 0, h, w/2);
        NIVision.imaqDrawShapeOnImage(image, image, rect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 70000000);
		NIVision.Rect rect2 = new NIVision.Rect(0, w/2, h, w/2);
        NIVision.imaqDrawShapeOnImage(image, image, rect2, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, -70000000);
        
        BufferedImageCache cache = new BufferedImageCache();
      
        long start = System.currentTimeMillis();
        BufferedImage buffImage = NiImageUtil.niImage2BufferedImage(image, cache);
        long delta = System.currentTimeMillis()-start;
    	System.out.println("Time "+ delta + " ms");

        start = System.currentTimeMillis();
        buffImage = NiImageUtil.niImage2BufferedImage(image, cache);
        delta = System.currentTimeMillis()-start;
    	System.out.println("Time "+ delta + " ms");

    	
    	start = System.currentTimeMillis();
        buffImage = NiImageUtil.niImage2BufferedImage(image, cache);
        delta = System.currentTimeMillis()-start;
    	System.out.println("Time "+ delta + " ms");

    	
        assertEquals(w, buffImage.getWidth());
        assertEquals(h, buffImage.getHeight());

//		RGBValue value = new RGBValue();
//		NIVision.imaqWriteFile(image, "ni2.jpg", value);
//
//		File outFile = new File("buffered2.jpg");
//		ImageIO.write(buffImage, "jpg", outFile);
	}
	
	@Test
	public void grayImageTest() throws IOException {
		int w = 50;
		int h = 50;
		
		Image image = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		NIVision.imaqSetImageSize(image, w, h);
		
		NIVision.Rect rect = new NIVision.Rect(0, 0, h, w);
        NIVision.imaqDrawShapeOnImage(image, image, rect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 255);

        long start = System.currentTimeMillis();
        RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_NONE,0);
        ByteBuffer buff = data.getBuffer();
                
        assertEquals(0, NiImageUtil.getImageType(buff));

        assertEquals(w, NiImageUtil.getWidth(buff));

        assertEquals(h, NiImageUtil.getHeight(buff));
           
        int dataSize = NiImageUtil.getDataSize(buff);
        assertEquals(3200, dataSize);
        
        int dataStartByte  = 172;
        //Test to see if there are zeros before the start
        assertEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE - 12));
        assertEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE - 8));
        assertEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE - 4));
        //Make sure that there is data at the data start byte.
        //Since we have drawn over this pixel it should not be the color black;
        assertNotEquals(0, NiImageUtil.readInt(buff, NiImageUtil.DATA_START_BYTE));

        int paddingSize = NiImageUtil.calculateImagePadding(dataSize, w, h, 1);
        assertEquals(14, paddingSize);

		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		final byte[] targetPixels = ((DataBufferByte) ret.getRaster().getDataBuffer()).getData();
        
        int pos = 0;
    	buff.position(dataStartByte);
    	System.out.println(buff.remaining());
    	for(int i = 0; i < h; i++) {
    		//Loop through the height/Rows of the image
	    	for(int j = 0; j < w; j++) {
		    	//Read the Width/columns pixel in a row
	        	byte val = buff.get();
	        	assertEquals(-1, val);
	        	targetPixels[pos++] = val;
	    	}
	    	for(int j = 0; j < paddingSize; j++) {
	    		//Throw away the padding bytes
	        	assertEquals(Integer.toString(j),0, buff.get());
	    	}
	    }
    	assertEquals(0, buff.remaining());
    	assertEquals(w*h, pos);

    	long delta = System.currentTimeMillis()-start;
    	System.out.println("Time "+ delta + " ms");
    	
		RGBValue value = new RGBValue();
		NIVision.imaqWriteFile(image, "ni.jpg", value);

		File outFile = new File("buffered.jpg");
		ImageIO.write(ret, "jpg", outFile);

	}

}
