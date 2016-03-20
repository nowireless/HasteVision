import org.nowireless.nivision.NiImageUtil;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.CompressionType;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.FlattenType;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.RawData;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ShapeMode;

public class Test {
	public static void main(String[] args) {
		Image image = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		NIVision.imaqSetImageSize(image, 50, 50);
		Rect rect = new Rect(0, 0, 50, 50);
		NIVision.imaqDrawShapeOnImage(image, image, rect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 255);
		
		NIVision.imaqCountParticles(image, 1);
		NIVision.imaqConvexHull(image, image, 0);
		
		RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_NONE, 0);
		NiImageUtil.dumpContents(data);
		
	}
}