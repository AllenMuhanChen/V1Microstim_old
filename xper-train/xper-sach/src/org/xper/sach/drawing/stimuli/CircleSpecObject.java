package org.xper.sach.drawing.stimuli;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Disk;
import org.xper.drawing.Context;
import org.xper.rfplot.RFPlotDrawable;
//import org.xper.sach.vo.SachTrialContext;
//import org.xper.time.DefaultTimeUtil;


public class CircleSpecObject implements RFPlotDrawable {
	CircleSpec spec;
	
	double xCenter;
	double yCenter;
	float radius;
	float innerRadius;
	float r;
	float g;
	float b;
	boolean animation; 
	String category;
	String stimClass;
	
	int slicesPerDegree = 5;
	int numSlices = 360 * slicesPerDegree;
	int loops = 1;
	
	Disk disk = new Disk();
	
	public void draw(Context context) {
		
		/*if ((stimClass == "target replay") && animation) {	// if its the target replay stim and it is set for animation, proceed with introducing time delay
			System.out.println("target replay!");
			SachTrialContext c = (SachTrialContext)context;
			
			DefaultTimeUtil timeBot = new DefaultTimeUtil();
			long curr_time = timeBot.currentTimeMicros();
			
			if (c.getAnimationFrameIndex() == 0) { // if first animation frame, set target scene start time
				c.setTargetSceneStartTime(curr_time);
			}
			
			long targetScene_start = c.getTargetSceneStartTime();
			System.out.println(curr_time-targetScene_start);
			//long targetScene_end = 10; 	// not sure how to get this time (find time allowed for target selection and add to start time?)
			long delay_time = 50 * 1000;	// delay onset by 200 msec, in microsec
			long delay_end = targetScene_start + delay_time;
			
			
			if (curr_time < delay_end && curr_time >= targetScene_start) { // && curr_time <= targetScene_end) {
				// if time < some_time and within time window for this taskScene/targetScene, then show blank (don't do anything?)
				System.out.println("delay_period ");
			} else if (curr_time >= delay_end && curr_time >= targetScene_start) { // && curr_time <= targetScene_end) {
				System.out.println("draw_period ");
				// if time >= some_time and within ..., then draw figure as usual
				GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
				GL11.glColor3f(r, g, b);
				GL11.glPushMatrix();
				GL11.glTranslated(xCenter, yCenter, 0);	
				disk.draw(innerRadius,radius,numSlices,loops);		// (this class uses the convention in which 0deg is along the +y axis and 90deg is along the +x axis)
				GL11.glPopMatrix();
				GL11.glPopAttrib();				
			} else {
				System.out.println("ERROR: time signals during target scene are out of sync!");
			}
			
			
		} else {*/
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glColor3f(r, g, b);
			GL11.glPushMatrix();
			GL11.glTranslated(xCenter, yCenter, 0);	
			disk.draw(innerRadius,radius,numSlices,loops);		// (this class uses the convention in which 0deg is along the +y axis and 90deg is along the +x axis)
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		//}
		
	}
	
	public void setSpec(String s) {
		spec = CircleSpec.fromXml(s);
		
		// pull variables
		xCenter = spec.getXCenter();
		yCenter = spec.getYCenter();
		radius = spec.getRadius();
		innerRadius = spec.getInnerRadius();
		r = spec.getColorRed();
		g = spec.getColorGreen();
		b = spec.getColorBlue();
		animation = spec.isAnimation();
		category = spec.getCategory();
		stimClass = spec.getStimClass();
	}
	
	public double getxCenter() {
		return xCenter;
	}

	public void setxCenter(double xCenter) {
		this.xCenter = xCenter;
	}

	public double getyCenter() {
		return yCenter;
	}

	public void setyCenter(double yCenter) {
		this.yCenter = yCenter;
	}
	
}


