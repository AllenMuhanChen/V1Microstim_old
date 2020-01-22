package org.xper.example.match;

import java.util.ArrayList;
import java.util.List;

import org.xper.drawing.RGBColor;
import org.xper.rfplot.GaborSpec;

import com.thoughtworks.xstream.XStream;

public class MatchGaborSpec {
	List<GaborSpec> objects = new ArrayList<GaborSpec>();
	List<RGBColor> targetColors = new ArrayList<RGBColor>();
	
	/**
	 * Index of the object matching prior one, negative if no matching object.
	 * Zero based.
	 */
	int matchIndex;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", MatchGaborSpec.class);
		s.alias("object", GaborSpec.class);
		s.useAttributeFor("animation", boolean.class);
		s.alias("targetColor", RGBColor.class);
		
		s.addImplicitCollection(MatchGaborSpec.class, "objects", "object", GaborSpec.class);
		s.addImplicitCollection(MatchGaborSpec.class, "targetColors", "targetColor", RGBColor.class);
	}
	
	public void addObjectSpec (GaborSpec spec) {
		objects.add(spec);
	}
	
	public void addTargetColor(RGBColor color) {
		targetColors.add(color);
	}
	
	public GaborSpec getObjectSpec (int index) {
		if (index < 0 || index >= objects.size()) {
			return null;
		} else {
			return objects.get(index);
		}
	}
	
	public int getObjectSpecCount () {
		return objects.size();
	}
	
	public String toXml () {
		return MatchGaborSpec.toXml(this);
	}
	
	public static String toXml (MatchGaborSpec spec) {
		return s.toXML(spec);
	}
	
	public static MatchGaborSpec fromXml (String xml) {
		MatchGaborSpec g = (MatchGaborSpec)s.fromXML(xml);
		return g;
	}

	public int getMatchIndex() {
		return matchIndex;
	}

	public void setMatchIndex(int matchIndex) {
		this.matchIndex = matchIndex;
	}
}
