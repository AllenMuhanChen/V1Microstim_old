package org.xper.example.choice;

import org.xper.rfplot.GaborSpec;

import com.thoughtworks.xstream.XStream;

public class ChoiceGaborSpec {
	GaborSpec object1 = new GaborSpec();
	GaborSpec object2 = new GaborSpec();
	long reward1;
	long reward2;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", ChoiceGaborSpec.class);
		s.useAttributeFor("animation", boolean.class);
	}
	
	public String toXml () {
		return ChoiceGaborSpec.toXml(this);
	}
	
	public static String toXml (ChoiceGaborSpec spec) {
		return s.toXML(spec);
	}
	
	public static ChoiceGaborSpec fromXml (String xml) {
		ChoiceGaborSpec g = (ChoiceGaborSpec)s.fromXML(xml);
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

	public long getReward1() {
		return reward1;
	}

	public void setReward1(long reward1) {
		this.reward1 = reward1;
	}

	public long getReward2() {
		return reward2;
	}

	public void setReward2(long reward2) {
		this.reward2 = reward2;
	}
}
