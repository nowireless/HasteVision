import com.ni.vision.NIVision;
import com.ni.vision.NIVision.IMAQdxCameraControlMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class Test {
	private static String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
	private static String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
	private static String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
	private static String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
	private static String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
	private static String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
	private static String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";
	
	
	public static void main(String[] args) {
		int session = NIVision.IMAQdxOpenCamera("cam2", IMAQdxCameraControlMode.CameraControlModeController);
		
		//Set the resolution of the camera to 640x480
		NIVision.IMAQdxSetAttributeString(session, ATTR_VIDEO_MODE, "640x480 YUY2 30.00fps");

        System.out.println("Exposure Min: " + NIVision.IMAQdxGetAttributeMaximumF64(session, ATTR_EX_VALUE) 
			+ " Max: " + NIVision.IMAQdxGetAttributeMinimumF64(session, ATTR_EX_VALUE));

		//Set the exposure of the camera
//		NIVision.IMAQdxSetAttributeString(session, ATTR_EX_MODE, "Manual");
//        NIVision.IMAQdxSetAttributeF64(session, ATTR_EX_VALUE, 5);

        System.out.println("Brightness Min: " + NIVision.IMAQdxGetAttributeMaximumF64(session, ATTR_BR_VALUE) 
			+ " Max: " + NIVision.IMAQdxGetAttributeMinimumF64(session, ATTR_BR_VALUE));
        
        
        //Set the brightness
//        NIVision.IMAQdxSetAttributeString(session, ATTR_BR_MODE, "Manual");
//        NIVision.IMAQdxSetAttributeI64(session, ATTR_BR_VALUE, 75);

        System.out.println("Saturation Min: " + NIVision.IMAQdxGetAttributeMaximumF64(session, "CameraAttributes::Saturation::Value") 
		+ " Max: " + NIVision.IMAQdxGetAttributeMinimumF64(session,  "CameraAttributes::Saturation::Value"));
        
        //Set the camera saturation
//        NIVision.IMAQdxSetAttributeString(session, "CameraAttributes::Saturation::Mode", "Manual");
//        NIVision.IMAQdxSetAttributeI64(session, "CameraAttributes::Saturation::Value", 200);

        System.out.println("White Balance Min: " + NIVision.IMAQdxGetAttributeMaximumI64(session, ATTR_WB_VALUE) 
		+ " Max: " + NIVision.IMAQdxGetAttributeMinimumI64(session, ATTR_BR_VALUE));
        
        //Set the white balance
//        NIVision.IMAQdxSetAttributeString(session, ATTR_WB_MODE, "Manual");
//        NIVision.IMAQdxSetAttributeI64(session, ATTR_WB_VALUE, 6000);
        
        //Start Acquisition
	}
}
