package org.nowireless.vision.runtime.imagedriver;

import org.nowireless.common.engine.impl.WatchedEngineAbstact;
import org.nowireless.vision.api.FrameUpdatable;
import org.nowireless.vision.api.imagedriver.ImageDriver;

public abstract class ImageDriverAbstract extends WatchedEngineAbstact implements ImageDriver {

	private FrameUpdatable updateable;
	
	public ImageDriverAbstract(long timeout, String threadName) {
		super(timeout, threadName);
	}
	
	@Override
	public void setUpdatable(FrameUpdatable updatable) {
		this.updateable = updatable;
	}
	
	@Override
	public FrameUpdatable getUpdateable() {
		return updateable;
	}

}
