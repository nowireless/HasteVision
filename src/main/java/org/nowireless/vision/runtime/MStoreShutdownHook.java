package org.nowireless.vision.runtime;

import org.nowireless.common.util.ShutDownHookAbstract;
import org.nowireless.mstore.MStore;

public class MStoreShutdownHook extends ShutDownHookAbstract {

	private static final MStoreShutdownHook i = new MStoreShutdownHook();
	public static MStoreShutdownHook get() { return i; }
	
	@Override
	public void run() {
		MStore.get().deinit();
		
	}

}
