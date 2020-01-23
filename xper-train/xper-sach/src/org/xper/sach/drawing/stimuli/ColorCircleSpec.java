package org.xper.sach.drawing.stimuli;

import com.thoughtworks.xstream.XStream;

public class ColorCircleSpec {
	double xCenter;
	double yCenter;
	double radius;
	float lineWidth = 1f;
	boolean animation;
	float colorRed;
	float colorGreen;
	float colorBlue;
	boolean solid;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", ColorCircleSpec.class);
		s.useAttributeFor("animation", boolean.class);
	}
	
	public String toXml () {
		return ColorCircleSpec.toXml(this);
	}
	
	public static String toXml (ColorCircleSpec spec) {
		return s.toXML(spec);
	}
	
	public static ColorCircleSpec fromXml (String xml) {
		ColorCircleSpec g = (ColorCircleSpec)s.fromXML(xml);
		return g;
	}
	
	public ColorCircleSpec() {}
	
	public ColorCircleSpec(ColorCircleSpec d) {
		xCenter = d.getXCenter();
		yCenter = d.getYCenter();
		radius = d.getRadius();
		lineWidth = d.getLineWidth();
		animation = d.isAnimation();
		colorRed = d.getColorRed();
		colorGreen = d.getColorGreen();
		colorBlue = d.getColorBlue();
		solid = d.isSolid();
	}
	
	public double getXCenter() {
		return xCenter;
	}
	public void setXCenter(double center) {
		xCenter = center;
	}
	public double getYCenter() {
		return yCenter;
	}
	public void setYCenter(double center) {
		yCenter = center;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}
	
	public float getColorRed() {
		return colorRed;
	}

	public void setColorRed(float colorRed) {
		this.colorRed = colorRed;
	}

	public float getColorGreen() {
		return colorGreen;
	}

	public void setColorGreen(float colorGreen) {
		this.colorGreen = colorGreen;
	}

	public float getColorBlue() {
		return colorBlue;
	}

	public void setColorBlue(float colorBlue) {
		this.colorBlue = colorBlue;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}
}
