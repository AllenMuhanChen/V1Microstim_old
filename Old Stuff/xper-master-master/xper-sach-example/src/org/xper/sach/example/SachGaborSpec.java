package org.xper.sach.example;

import java.util.ArrayList;
import java.util.List;

import org.xper.drawing.Coordinates2D;
import org.xper.rfplot.GaborSpec;

import com.thoughtworks.xstream.XStream;

public class SachGaborSpec {
	List<GaborSpec> objects = new ArrayList<GaborSpec>();
	// The units of targetPosition and targetEyeWinSize have to be in degree
	Coordinates2D targetPosition = new Coordinates2D();
	double targetEyeWinSize;
	long reward;
	long targetIndex;
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("StimSpec", SachGaborSpec.class);
		s.alias("object", GaborSpec.class);
		s.useAttributeFor("animation", boolean.class);
		s.alias("targetPosition", Coordinates2D.class);
		
		s.addImplicitCollection(SachGaborSpec.class, "objects", "object", GaborSpec.class);
	}
	
	public void addObjectSpec (GaborSpec spec) {
		objects.add(spec);
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
		return SachGaborSpec.toXml(this);
	}
	
	public static String toXml (SachGaborSpec spec) {
		return s.toXML(spec);
	}
	
	public static SachGaborSpec fromXml (String xml) {
		SachGaborSpec g = (SachGaborSpec)s.fromXML(xml);
		return g;
	}

	public Coordinates2D getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Coordinates2D targetPosition) {
		this.targetPosition = targetPosition;
	}

	public double getTargetEyeWinSize() {
		return targetEyeWinSize;
	}

	public void setTargetEyeWinSize(double targetEyeWinSize) {
		this.targetEyeWinSize = targetEyeWinSize;
	}

	public long getReward() {
		return reward;
	}

	public void setReward(long reward) {
		this.reward = reward;
	}

	public long getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(long targetIndex) {
		this.targetIndex = targetIndex;
	}
}
