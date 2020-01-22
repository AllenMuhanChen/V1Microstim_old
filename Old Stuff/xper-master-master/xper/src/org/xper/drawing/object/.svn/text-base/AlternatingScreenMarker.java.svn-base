package org.xper.drawing.object;

import org.lwjgl.opengl.GL11;
import org.xper.Dependency;
import org.xper.drawing.Context;
import org.xper.drawing.RGBColor;
import org.xper.drawing.ScreenMarker;
import org.xper.drawing.renderer.AbstractRenderer;

public class AlternatingScreenMarker implements ScreenMarker {
	@Dependency
	protected int viewportIndex = 0;
	@Dependency
	protected int size = 20;

	protected int i = 0;
	protected RGBColor whiteColor = new RGBColor(1, 1, 1);
	protected RGBColor blackColor = new RGBColor(0, 0, 0);

	public void next() {
		i++;
	}

	protected void drawMarker(Context context, RGBColor firstColor, RGBColor secondColor) {
		AbstractRenderer renderer = context.getRenderer();
		double lx = renderer.getXmin();
		double ly = renderer.getYmin();
		double rx = renderer.getXmax() - size;
		double ry = renderer.getYmin();
		RGBColor l = null;
		RGBColor r = null;
		if (i % 2 == 0) {
			l = firstColor;
			r = secondColor;
		} else {
			l = secondColor;
			r = firstColor;
		}
		double z = 1;
		
		GL11.glColor3f(l.getRed(), l.getGreen(), l.getBlue());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(lx, ly, z);
		GL11.glVertex3d(lx + size, ly, z);
		GL11.glVertex3d(lx + size, ly + size, z);
		GL11.glVertex3d(lx, ly + size, z);
		GL11.glEnd();

		GL11.glColor3f(r.getRed(), r.getGreen(), r.getBlue());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(rx, ry, z);
		GL11.glVertex3d(rx + size, ry, z);
		GL11.glVertex3d(rx + size, ry + size, z);
		GL11.glVertex3d(rx, ry + size, z);
		GL11.glEnd();
	}
	
	public void draw(Context context) {
		if (context.getViewportIndex() == viewportIndex) {
			drawMarker(context, whiteColor, blackColor);
		}
	}
	
	public void drawAllOff(Context context) {
		if (context.getViewportIndex() == viewportIndex) {
			drawMarker(context, blackColor, blackColor);
		}
	}

	public int getViewportIndex() {
		return viewportIndex;
	}

	public void setViewportIndex(int viewportIndex) {
		this.viewportIndex = viewportIndex;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
