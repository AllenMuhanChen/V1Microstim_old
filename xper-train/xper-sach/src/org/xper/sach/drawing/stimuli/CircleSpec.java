package org.xper.sach.drawing.stimuli;

import com.thoughtworks.xstream.XStream;

public class CircleSpec {
	double xCenter;
	double yCenter;
	float radius;
	float innerRadius;
	float colorRed;
	float colorGreen;
	float colorBlue;
	boolean animation;
	String category;
	String stimClass;
	String stimColor;
	

	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", CircleSpec.class);
		s.useAttributeFor("animation", boolean.class);
	}
	
	public String toXml () {
		return CircleSpec.toXml(this);
	}
	
	public static String toXml (CircleSpec spec) {
		return s.toXML(spec);
	}
	
	public static CircleSpec fromXml (String xml) {
		CircleSpec g = (CircleSpec)s.fromXML(xml);
		return g;
	}
	
	public CircleSpec() {}
	
	public CircleSpec(CircleSpec d) {
		xCenter = d.getXCenter();
		yCenter = d.getYCenter();
		radius = d.getRadius();
		innerRadius = d.getInnerRadius();
		colorRed = d.getColorRed();
		colorGreen = d.getColorGreen();
		colorBlue = d.getColorBlue();
		animation = d.isAnimation();
		category = d.getCategory();
		stimClass = d.getStimClass();
		stimColor = d.getStimColor();
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
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(float innerRadius) {
		this.innerRadius = innerRadius;
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
	
	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStimClass() {
		return stimClass;
	}

	public void setStimClass(String stimClass) {
		this.stimClass = stimClass;
	}
	
	public String getStimColor() {
		return stimColor;
	}

	public void setStimColor(String stimColor) {
		this.stimColor = stimColor;
	}
	
}
