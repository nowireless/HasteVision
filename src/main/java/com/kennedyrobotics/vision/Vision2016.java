package com.kennedyrobotics.vision;

import java.util.ArrayList;
import java.util.List;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.GetImageSizeResult;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.MeasurementType;
import com.ni.vision.NIVision.ParticleFilterCriteria2;
import com.ni.vision.NIVision.ParticleFilterOptions2;
import com.ni.vision.NIVision.Point;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ShapeMode;

/**
 * Vision Processing algorithm for the 2016 FRC season game
 * @author Ryan Sjostrand
 *
 */
public class Vision2016 {
	
	public static final double AREA_MINIMUM = 150;
//	public static final double VIEW_ANGLE = 60; //View angle fo camera, set to Axis m1011 by default, 64 for m1013, 51.7 for 206, 52 for HD3000 square, 60 for HD3000 640x480
	public static final double TARGET_WIDTH = 20;
	
	private Image binaryFrame;
	private Image debugFrame;
	private final Config config;
	private final ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	private final ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);//False, False, True, True
	private final Scores scores = new Scores();
	
	private ParticleReport target;

	public Image getDebugFrame() {
		return debugFrame;
	}
	
	private volatile boolean targetPresent = false;
	public boolean isTargetPresent() {
		return this.targetPresent;
	}
	
	private volatile double targetDistance = 0.0;
	public double getTargetDistance() {
		return targetDistance;
	}
	
	private volatile int numberOfParticles = 0;
	public int getNumberOfParticles() {
		return numberOfParticles;
	}
	
	private volatile double comX = 0;
	public double getTargetComX() {
		return comX;
	}
	
	private volatile double angleToTurn;
	public double getAngleToTurn() {
		return angleToTurn;
	}
	
	public Config getConfig() {
		return this.config;
	}
	
	public Vision2016() {
		this(new Config());
	}
	
	public Vision2016(Config config) {
		this.config = config;
	}
	
	/**
	 * Initializes the internal state
	 */
	public void init() {
		binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		debugFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		this.reset();
	}
	
	/**
	 * Deinitializes Vision2016
	 */
	public void deinit() {
		
	}
	
	/**
	 * Reset the internal state
	 */
	public void reset() {
//		targetPresent = false;
		target = null;
//		targetDistance = 0.0;
	}
	
	/**
	 * Processes the given image
	 * @param image
	 */
	public void process(Image image) {
		this.reset();
		NIVision.imaqColorThreshold(binaryFrame, image, 255, ColorMode.HSV, config.hueRange, config.saturationRange, config.valueRange);
		
		float areaMin = (float) AREA_MINIMUM;
		criteria[0] = new ParticleFilterCriteria2();
		criteria[0].parameter = MeasurementType.MT_AREA;
		criteria[0].lower = 0;
		criteria[0].upper = areaMin;
		criteria[0].exclude = 0; //False
		criteria[0].calibrated = 0; //False
		
		filterOptions.rejectMatches = 1;//True
		
		NIVision.imaqParticleFilter4(binaryFrame, binaryFrame, criteria, filterOptions, null);
		
		NIVision.imaqDuplicate(debugFrame, binaryFrame);
		
		NIVision.imaqConvexHull(binaryFrame, binaryFrame, 0);
		
		int numParticlesAfter = NIVision.imaqCountParticles(binaryFrame, 1);
		numberOfParticles = numParticlesAfter;
//		System.out.println("Particles " + numParticlesAfter);
		
		if(0 < numParticlesAfter) {
			List<ParticleReport> particles = new ArrayList<ParticleReport>();
			for(int particleIndex = 0; particleIndex < numParticlesAfter; particleIndex++) {
				ParticleReport par = new ParticleReport();
				par.index = particleIndex;
				par.percentAreaToImageArea = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
				par.area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
				par.boundingRectTop = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
				par.boundingRectLeft = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
				par.boundingRectBottom = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
				par.boundingRectRight = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
				par.comX = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
				par.comY = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
				particles.add(par);
			}
			
			this.score(particles);
		} else  {
			this.score(null);
		}	
	}
	
	/**
	 * Score the provided list of reports
	 * @param reports
	 */
	public void score(List<ParticleReport> reports) {
		if(reports == null) {
			targetPresent = false;
			targetDistance = 0.0;
			angleToTurn = 0.0;

		} else {
			
			//Sorts the particles from Biggest to smallest areas
			reports.sort(null);
			
			//Let's consider the largest body
			scores.aspect = this.aspectScore(reports.get(0));
			scores.area = this.areaScore(reports.get(0));
			
//			System.out.println(scores);
			
			//Determine whether this particle is the target
			targetPresent = this.isTarget(scores);
			
			if(targetPresent) {
				target = reports.get(0);
				targetDistance = this.computeDistance(binaryFrame, target);
				comX = target.comX;
//				angleToTurn = this.computeAngleToTurn(targetDistance, comX,640);
				System.out.println("Dist: " + targetDistance);
				System.out.println("Angle: " + angleToTurn);
			} else {
				target = null;
				targetDistance = 0.0;
				angleToTurn = 0.0;
			}
		}
 
	}
	
	/**
	 * Will Draw info onto the image to be displayed
	 * At the moment it will place a green rectangle on top of the target
	 * @param dest Image to draw on
	 */
	public void draw(Image dest) {
		if(target != null&& targetPresent) {
			
//			Rect rect1 = new Rect();
//			rect1.top = (int)target.boundingRectTop;
//			rect1.left = (int) target.boundingRectLeft; 
//			rect1.height = (int)(target.boundingRectBottom-target.boundingRectTop); 
//			rect1.width =(int)(target.boundingRectRight-target.boundingRectLeft);
//			rect1.write();
//			
			Rect left = new Rect();
			left.top = (int)target.boundingRectTop;
			left.left = (int) target.boundingRectLeft; 
			left.height = (int)(target.boundingRectBottom-target.boundingRectTop); 
			left.width = 20;
			left.write();
			
			Rect right = new Rect();
			right.top = (int)target.boundingRectTop;
			right.left = (int) target.boundingRectRight - 20; 
			right.height = (int)(target.boundingRectBottom-target.boundingRectTop); 
			right.width = 20;
			right.write();
			
			Rect top = new Rect();
			top.top = (int)target.boundingRectTop;
			top.left = (int) target.boundingRectLeft; 
			top.height = 20; 
			top.width =(int)(target.boundingRectRight-target.boundingRectLeft);
			top.write();
			
			Rect bottom = new Rect();
			bottom.top = (int) (target.boundingRectBottom-20);
			bottom.left = (int) target.boundingRectLeft; 
			bottom.height = 20; 
			bottom.width =(int)(target.boundingRectRight-target.boundingRectLeft);
			bottom.write();
			
			//Draw the bounding box on the target
			NIVision.imaqDrawShapeOnImage(dest, dest, left, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 50500);
			NIVision.imaqDrawShapeOnImage(dest, dest, right, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 50500);
			NIVision.imaqDrawShapeOnImage(dest, dest, top, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 50500);
			NIVision.imaqDrawShapeOnImage(dest, dest, bottom, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 50500);

			Rect vertical = new Rect(0, (int)comX-2, 480, 4);
			NIVision.imaqDrawShapeOnImage(dest, dest, vertical, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, 1000);
			vertical.free();
			
			left.free();
			right.free();
			top.free();
			bottom.free();
		}
		this.drawCrossHairs(dest);

	}
	
	private void drawCrossHairs(Image dest) {
		GetImageSizeResult ret = NIVision.imaqGetImageSize(dest);
		
		Point one = new Point();
		Point two = new Point();
		
		//Draw the Vertical line
		one.x = ret.width/2;
		one.y = 0;
		one.write();
		
		two.x = ret.width/2;
		two.y = ret.height;
		two.write();
		NIVision.imaqDrawLineOnImage(dest, dest, DrawMode.DRAW_VALUE, one, two, 70000);
		
		//Draw the horizontal line
//		one.x = 0;
//		one.y = ret.height/2;
//		one.write();
//		
//		two.x = ret.width;
//		two.y = ret.height/2;
//		two.write();
//		NIVision.imaqDrawLineOnImage(dest, dest, DrawMode.DRAW_VALUE, one, two, 70000);
		
		one.free();
		two.free();
	}
	
	/**
	 * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
	 * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
	 */
	private double ratioToScore(double ratio)
	{
		return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
	}

	/**
	 * Scores the area of the particle to the area of the bounding box
	 * @param report The report to score
	 * @return The score from 0-100
	 */
	private double areaScore(ParticleReport report) {
		double boundingArea = (report.boundingRectBottom - report.boundingRectTop) * (report.boundingRectRight - report.boundingRectLeft);
		return ratioToScore(report.area/boundingArea);
	}

	/**
	 * Method to score if the aspect ratio of the particle appears to match the retro-reflective target. Target is 7"x7" so aspect should be 1
	 * @param report The report to score
	 * @return The score from 0-100
	 */
	private double aspectScore(ParticleReport report) {
		return ratioToScore(((report.boundingRectRight-report.boundingRectLeft)/(report.boundingRectBottom-report.boundingRectTop))/(20.0/12.0));
	}

	/**
	 * Determines whether the given set of scores is a target
	 * @param scores
	 * @return
	 */
	private boolean isTarget(Scores scores) {
		boolean ret = true;
		ret &= scores.area >= config.areaScoreMin;
		ret &= scores.aspect >= config.aspectRatioScoreMin;
		return ret;
	}
	
	/**
	 * Computes the estimated distance to a target using the width of the particle in the image. For more information and graphics
	 * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
	 *
	 * @param image The image to use for measuring the particle estimated rectangle
	 * @param report The Particle Analysis Report for the particle
	 * @return The estimated distance to the target in feet.
	 */
	private double computeDistance (Image image, ParticleReport report) {
		double width = report.boundingRectRight-report.boundingRectLeft;
//		SmartDashboard.putNumber("target width", width);
		
		return 2694.4 * Math.pow(width , -1.192);
		
	}
	
//    public double computeAngleToTurn(double distance, double comX, int imageWidth) {
//    	double theta = -0.9709*distance + 61.587;
//    	double fovFt = 2.0 * (Math.tan(Math.toRadians(0.5 * theta)));
//    	double fovPixel = imageWidth;
//    	double ftPerPixel = fovFt / fovPixel;
//    	double delta = (imageWidth/2 - comX) * ftPerPixel;
//    	double angle = Math.toDegrees(Math.atan2(delta, distance));
//    	return angle;
//    	
////		double fov = 2*distance * Math.tan(Math.toRadians(1/2*Vision2016.VIEW_ANGLE));
////		double ftPerPix = fov/640;
////		double delta = 640/2 - comX;
////		delta *= ftPerPix;
////		return Math.toDegrees(Math.atan2(distance, delta));
//	}
	
}
