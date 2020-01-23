package org.xper.sach.drawing.stimuli;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.xper.drawing.Context;
import org.xper.rfplot.RFPlotDrawable;

public class ColorCircle2Object implements RFPlotDrawable {
	
	ColorCircle2Spec spec;
	
	// init variables
	double xCenter;
	double yCenter;
	double radius;
	float r;
	float g;
	float b;
	double radius2;
	float r2;
	float g2;
	float b2;
	boolean solid;
	
	/*
	@Dependency
	boolean solid = false;
	@Dependency
	double radius;
	*/
	
	ColorCircleObject circle1 = new ColorCircleObject();
	ColorCircleObject circle2 = new ColorCircleObject();

	static final int STEPS = 200;
	ByteBuffer array = ByteBuffer.allocateDirect(
			STEPS * 3 * Float.SIZE / 8).order(
			ByteOrder.nativeOrder());

	ByteBuffer array2 = ByteBuffer.allocateDirect(
			STEPS * 3 * Float.SIZE / 8).order(
			ByteOrder.nativeOrder());
	
	/**
	 * @param context ignored.
	 */
	public void draw(Context context) {
			
		// initArray
		for (int i = 0; i < STEPS; i ++) {
			double angle = i * 2 * Math.PI / STEPS;
			//V3F
			array.putFloat((float)(radius*Math.cos(angle)));
			array.putFloat((float)(radius*Math.sin(angle)));
			array.putFloat(0.0f);
			array2.putFloat((float)(radius2*Math.cos(angle)));
			array2.putFloat((float)(radius2*Math.sin(angle)));
			array2.putFloat(0.0f);
		}
		array.flip();
		array2.flip();
		
		
		//draw/render
		
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glColor3f(r, g, b);
		GL11.glPushMatrix();
		GL11.glTranslated(xCenter, yCenter, 0);

		GL11.glInterleavedArrays(GL11.GL_V3F, 0, array);
		GL11.glInterleavedArrays(GL11.GL_V3F, 0, array2);
		if (solid) {
			GL11.glDrawArrays(GL11.GL_POLYGON, 0, STEPS);
		} else {
			GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, STEPS);
		}
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
	
	public void setSpec(String s) {
		spec = ColorCircle2Spec.fromXml(s);
		
		// pull variables
		xCenter = spec.getXCenter();
		yCenter = spec.getYCenter();
		radius = spec.getRadius();
		r = spec.getColorRed();
		g = spec.getColorGreen();
		b = spec.getColorBlue();
		radius2 = spec.getRadius2();
		r2 = spec.getColorRed2();
		g2 = spec.getColorGreen2();
		b2 = spec.getColorBlue2();
		solid = spec.isSolid();
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


