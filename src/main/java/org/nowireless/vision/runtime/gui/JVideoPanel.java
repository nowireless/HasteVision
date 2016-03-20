package org.nowireless.vision.runtime.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


import org.nowireless.common.Initializable;
import org.nowireless.common.gui.BufferedImageCache;
import org.nowireless.nivision.NiImageUtil;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.GetImageSizeResult;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

/**
 * A extension of a {@link JPanel} to make it easy to display a {@link Mat} image in Swing.
 * @author nowireless
 *
 */
public class JVideoPanel extends JPanel implements Initializable{

	private static final long serialVersionUID = 3570076862706867432L;
	
	//private transient final Size size;
	private final int width;
	private final int height;
	private transient final Object imageLock = new Object();
	private transient final BufferedImageCache cache = new BufferedImageCache();
	private transient BufferedImage jImage = null;
	
	public JVideoPanel() {
		this(640, 480);
	}
	
	public JVideoPanel(int width, int height) {
		//size = new Size(width, height);
		this.setPreferredSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void init() {
		Image black = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		NIVision.imaqSetImageSize(black, width, height);
		this.updateImage(black);
		black.free();
	}

	@Override
	public void deinit() {
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		synchronized (imageLock) {
			g.drawImage(jImage, 0, 0, null);
		}
	}
	
	/**
	 * Updates the the internal {@link BufferedImage} to the given {@link Mat}.
	 * Once set it calls {@link #repaint()}
	 * @param image
	 */
	public void updateImage(Image image) {
		if(image == null) {
			return;
		}
		
		GetImageSizeResult size = NIVision.imaqGetImageSize(image);
		
		if(size.height * size.width == 0) {
			return;
		}
		
		synchronized (imageLock) {
			if(jImage != null) {
				jImage.flush();
			}
			
			jImage = NiImageUtil.niImage2BufferedImage(image, cache);
		}
		
		repaint();
	}

}
