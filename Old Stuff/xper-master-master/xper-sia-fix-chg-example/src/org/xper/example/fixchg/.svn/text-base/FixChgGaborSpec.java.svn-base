package org.xper.example.fixchg;

import com.thoughtworks.xstream.XStream;

public class FixChgGaborSpec {
	double xCenter;
	double yCenter;
	double orientation;
	double frequency;
	double phase;
	double size;
	boolean animation;
	boolean stereo;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", FixChgGaborSpec.class);
		s.useAttributeFor("animation", boolean.class);
		s.useAttributeFor("stereo", boolean.class);
	}
	
	public String toXml () {
		return FixChgGaborSpec.toXml(this);
	}
	
	public static String toXml (FixChgGaborSpec spec) {
		return s.toXML(spec);
	}
	
	public static FixChgGaborSpec fromXml (String xml) {
		FixChgGaborSpec g = (FixChgGaborSpec)s.fromXML(xml);
		return g;
	}
	
	public FixChgGaborSpec() {}
	
	public FixChgGaborSpec(FixChgGaborSpec d) {
		xCenter = d.getXCenter();
		yCenter = d.getYCenter();
		orientation = d.getOrientation();
		frequency = d.getFrequency();
		phase = d.getPhase();
		size = d.getSize();
		animation = d.isAnimation();
		//fixationDisparity = d.getFixationDisparity();
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
	public double getOrientation() {
		return orientation;
	}
	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public double getPhase() {
		return phase;
	}
	public void setPhase(double phase) {
		this.phase = phase;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public boolean isStereo() {
		return stereo;
	}

	public void setStereo(boolean stereo) {
		this.stereo = stereo;
	}
}
