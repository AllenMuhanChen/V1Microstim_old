package org.xper.example.matchchoice;

import org.xper.drawing.RGBColor;
import org.xper.rfplot.GaborSpec;

import com.thoughtworks.xstream.XStream;

public class MatchChoiceGaborSpec {
	GaborSpec object1 = new GaborSpec();
	GaborSpec object2 = new GaborSpec();
	int reward;
	double size1;
	double size2;
	double distance1;
	double distance2;
	RGBColor color1;
	RGBColor color2;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", MatchChoiceGaborSpec.class);
		s.useAttributeFor("animation", boolean.class);
	}
	
	public String toXml () {
		return MatchChoiceGaborSpec.toXml(this);
	}
	
	public static String toXml (MatchChoiceGaborSpec spec) {
		return s.toXML(spec);
	}
	
	public static MatchChoiceGaborSpec fromXml (String xml) {
		MatchChoiceGaborSpec g = (MatchChoiceGaborSpec)s.fromXML(xml);
		return g;
	}

	public GaborSpec getObject1() {
		return object1;
	}

	public void setObject1(GaborSpec object1) {
		this.object1 = object1;
	}

	public GaborSpec getObject2() {
		return object2;
	}

	public void setObject2(GaborSpec object2) {
		this.object2 = object2;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public double getSize1() {
		return size1;
	}

	public void setSize1(double size1) {
		this.size1 = size1;
	}

	public double getSize2() {
		return size2;
	}

	public void setSize2(double size2) {
		this.size2 = size2;
	}

	public double getDistance1() {
		return distance1;
	}

	public void setDistance1(double distance1) {
		this.distance1 = distance1;
	}

	public double getDistance2() {
		return distance2;
	}

	public void setDistance2(double distance2) {
		this.distance2 = distance2;
	}

	public RGBColor getColor1() {
		return color1;
	}

	public void setColor1(RGBColor color1) {
		this.color1 = color1;
	}

	public RGBColor getColor2() {
		return color2;
	}

	public void setColor2(RGBColor color2) {
		this.color2 = color2;
	}
}
