package Team4450.Robot9;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

import Team4450.Lib.Util;

public class Vision 
{
	private Robot robot;
	
	// This variable and method make sure this class is a singleton.
	
	public static Vision vision = null;
	
	/**
	* Get reference to the single instance of this class shared by any caller of
	* this method.
	* @return Reference to single shared instance of this class.
	*/
	public static Vision getInstance(Robot robot) 
	{
		if (vision == null) vision = new Vision(robot);
		
		return vision;
	}
	
	// Private constructor prevents multiple instances from being created.
	
	private Vision(Robot robot) 
	{
		this.robot = robot;
		
		Util.consoleLog("Vision created!");
	}
	
	/**
	* Release any resources allocated and the singleton object.
	*/
	public void dispose()
	{
		vision =  null;
	}
	
	// This is the rest of the class.
	
	/**
	 * Trigger the image processing pipeline to run one time with image
	 * available internally. Only used if the pipeline does not run
	 * automatically.
	 */
	public void processImage()
	{
		
	}
	
	/**
	 * Trigger the image processing pipeline to run on time with the supplied
	 * image. Only used if the pipeline does not run automatically.
	 * @param image
	 */
	public void processImage(Mat image)
	{
		
	}
	
	/**
	 * Indicates if vision system has a target in the camera field of vision as of
	 * the last run of the image processing pipeline.
	 * @return True if target visible, false if not.
	 */
	public boolean targetVisible()
	{
		return false;
	}
	
	/**
	 * Returns an array of target rectangles derived from the contours returned
	 * by the last run of the image processing pipeline representing the targets
	 * in the field of vision of camera. Not valid if targetVisible is not true.
	 * @return An array of target rectangles.
	 */
	public ArrayList<Rect> getTargetRectangles()
	{
		return null;
	}
	
	/**
	 * Returns the contours returned by the last run of the image processing pipeline representing
	 * the targets in the field of vision of camera. Not valid if targetVisible is
	 * not true.
	 * @return An array of target contours.
	 */
	public ArrayList<MatOfPoint> getContours()
	{
		return null;
	}
	
	/**
	 * Returns the distance value to the target(s). The distance is typically the number of
	 * pixels separating two targets in the camera field of vision.
	 * @return The distance to the targets.
	 */
	public double getDistance()
	{
		return 0;
	}
}
