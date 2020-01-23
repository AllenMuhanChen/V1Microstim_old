package org.xper.sach.drawing.stimuli;

import com.thoughtworks.xstream.XStream;

public class ColorCircle2Spec {
	double xCenter;
	double yCenter;
	double radius;
	double radius2;
	boolean animation;
	float colorRed;
	float colorGreen;
	float colorBlue;
	float colorRed2;
	float colorGreen2;
	float colorBlue2;
	boolean solid;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", ColorCircle2Spec.class);
		s.useAttributeFor("animation", boolean.class);
	}
	
	public String toXml () {
		return ColorCircle2Spec.toXml(this);
	}
	
	public static String toXml (ColorCircle2Spec spec) {
		return s.toXML(spec);
	}
	
	public static ColorCircle2Spec fromXml (String xml) {
		ColorCircle2Spec g = (ColorCircle2Spec)s.fromXML(xml);
		return g;
	}
	
	public ColorCircle2Spec() {}
	
	public ColorCircle2Spec(ColorCircle2Spec d) {
		xCenter = d.getXCenter();
		yCenter = d.getYCenter();
		radius = d.getRadius();
		radius2 = d.getRadius2();
		animation = d.isAnimation();
		colorRed = d.getColorRed();
		colorGreen = d.getColorGreen();
		colorBlue = d.getColorBlue();
		colorRed2 = d.getColorRed2();
		colorGreen2 = d.getColorGreen2();
		colorBlue2 = d.getColorBlue2();
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

	public double getRadius2() {
		return radius2;
	}

	public void setRadius2(double radius2) {
		this.radius2 = radius2;
	}

	public float getColorRed2() {
		return colorRed2;
	}

	public void setColorRed2(float colorRed2) {
		this.colorRed2 = colorRed2;
	}

	public float getColorGreen2() {
		return colorGreen2;
	}

	public void setColorGreen2(float colorGreen2) {
		this.colorGreen2 = colorGreen2;
	}

	public float getColorBlue2() {
		return colorBlue2;
	}

	public void setColorBlue2(float colorBlue2) {
		this.colorBlue2 = colorBlue2;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}
}
