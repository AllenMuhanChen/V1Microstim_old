package org.xper.match.vo;

import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;

import com.thoughtworks.xstream.XStream;

public class MatchTargetMessage {
	long timestamp;
	Coordinates2D targetPos = new Coordinates2D();
	RGBColor targetColor = new RGBColor();
	double targetSize;
	double targetEyeWindowSize;
	
	public MatchTargetMessage(long timestamp, RGBColor targetColor, Coordinates2D targetPos,
			double targetSize, double targetEyeWindowSize) {
		super();
		this.timestamp = timestamp;
		this.targetColor = targetColor;
		this.targetPos = targetPos;
		this.targetSize = targetSize;
		this.targetEyeWindowSize = targetEyeWindowSize;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public Coordinates2D getTargetPos() {
		return targetPos;
	}
	public void setTargetPos(Coordinates2D targetPos) {
		this.targetPos = targetPos;
	}
	public double getTargetSize() {
		return targetSize;
	}
	public void setTargetSize(double targetSize) {
		this.targetSize = targetSize;
	}
	public double getTargetEyeWindowSize() {
		return targetEyeWindowSize;
	}
	public void setTargetEyeWindowSize(double targetEyeWindowSize) {
		this.targetEyeWindowSize = targetEyeWindowSize;
	}


	static XStream xstream = new XStream();

	static {
		xstream.alias("MatchTargetMessage", MatchTargetMessage.class);
		xstream.alias("Coordinates2D", Coordinates2D.class);
		xstream.alias("RGBColor", RGBColor.class);
	}
	
	public static MatchTargetMessage fromXml (String xml) {
		return (MatchTargetMessage)xstream.fromXML(xml);
	}
	
	public static String toXml (MatchTargetMessage msg) {
		return xstream.toXML(msg);
	}
	public RGBColor getTargetColor() {
		return targetColor;
	}
	public void setTargetColor(RGBColor targetColor) {
		this.targetColor = targetColor;
	}
	
	
}
