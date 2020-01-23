package org.xper.sach.renderer;

import org.lwjgl.opengl.GL11;
import org.xper.drawing.renderer.PerspectiveIdenticalStereoRenderer;
import org.xper.drawing.RGBColor;

public class SachPerspectiveStereoRenderer extends PerspectiveIdenticalStereoRenderer {
	
	public RGBColor backgroundColor = new RGBColor(0f,0f,0f);	// initialize to black background color
	float r,g,b;
	
	public void init() {
		super.init();
		r = backgroundColor.getRed();
		g = backgroundColor.getGreen();
		b = backgroundColor.getBlue();
		GL11.glClearColor(r, g, b, 1f);
	}
	
	public RGBColor getRgbColor() {
		return backgroundColor;
	}
	public void setRgbColor(RGBColor rgbColor) {
		this.backgroundColor = rgbColor;
	}
}
