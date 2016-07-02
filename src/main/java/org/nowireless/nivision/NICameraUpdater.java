package org.nowireless.nivision;

import org.nowireless.configstore.ConfigStore;
import org.nowireless.imaqdx.IMAQdx;
import org.nowireless.imaqdx.IMAQdx.IMAQdxCameraInformationArray;
import org.nowireless.nivision.config.NICameraUpdaterConfig;
import org.nowireless.nivision.config.NICameraUpdaterConfigStore;
import org.nowireless.vision.api.imagedriver.annotation.IDriver;
import org.nowireless.vision.runtime.imagedriver.ImageDriverAbstract;

import com.ni.vision.NIVision;
import com.ni.vision.VisionException;
import com.ni.vision.NIVision.IMAQdxCameraControlMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

@IDriver
public class NICameraUpdater extends ImageDriverAbstract {

	private static String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
	private static String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
	private static String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
	private static String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
	private static String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
	private static String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
	private static String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";

	private Image frame;
	private int session;
	
	public NICameraUpdater() {
		super(5000, "NI Cam");
	}

	@Override
	public void init() {
//		NICameraUpdaterConfigStore store = new NICameraUpdaterConfigStore();
//		store.init();
//		NICameraUpdaterConfig config = store.get(ConfigStore.INSTANCE);
//		if(config == null) {
//			config = store.create(ConfigStore.INSTANCE);
//		}
		System.loadLibrary("IMAQdxJava");
		
		int count = IMAQdx.IMAQdxEnumerateCameras(null, true);
		if(count == 0) {
			log().fatal("No cameras");
			System.exit(-1);
		}
		IMAQdxCameraInformationArray array = new IMAQdxCameraInformationArray(count);
		IMAQdx.IMAQdxEnumerateCameras(array, true);
		String intName = array.get(0).interfaceName();
		
		
		try {
			session = NIVision.IMAQdxOpenCamera(intName, IMAQdxCameraControlMode.CameraControlModeController);
		}catch(VisionException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		//Set the resolution of the camera to 640x480
		NIVision.IMAQdxSetAttributeString(session, ATTR_VIDEO_MODE, "640x480 YUY2 30.00fps");
		
		//Set the exposure of the camera
		NIVision.IMAQdxSetAttributeString(session, ATTR_EX_MODE, "Manual");
        NIVision.IMAQdxSetAttributeF64(session, ATTR_EX_VALUE, 0.0010);

        //Set the brightness
        NIVision.IMAQdxSetAttributeString(session, ATTR_BR_MODE, "Manual");
        NIVision.IMAQdxSetAttributeI64(session, ATTR_BR_VALUE, 50);

        //Set the camera saturation
        NIVision.IMAQdxSetAttributeString(session, "CameraAttributes::Saturation::Mode", "Manual");
        NIVision.IMAQdxSetAttributeI64(session, "CameraAttributes::Saturation::Value", 200);

        //Set the white balance
        NIVision.IMAQdxSetAttributeString(session, ATTR_WB_MODE, "Manual");
        NIVision.IMAQdxSetAttributeI64(session, ATTR_WB_VALUE, 4000);
        
        //Start Acquisition
		NIVision.IMAQdxConfigureGrab(session);
		NIVision.IMAQdxStartAcquisition(session);

		frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);

	}

	@Override
	public void deinit() {
		NIVision.IMAQdxCloseCamera(session);
	}

	@Override
	protected long getSleepTime() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	protected void overdue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runTask() {
		NIVision.IMAQdxGrab(session, frame, 1);
		this.getUpdateable().updateSource(frame);
	}
}
