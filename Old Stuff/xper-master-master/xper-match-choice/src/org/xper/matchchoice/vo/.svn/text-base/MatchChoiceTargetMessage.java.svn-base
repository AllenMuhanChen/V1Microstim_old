package org.xper.matchchoice.vo;

import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;

import com.thoughtworks.xstream.XStream;

public class MatchChoiceTargetMessage {
	long timestamp;
	RGBColor targetColor [] = new RGBColor[MatchChoiceTrialContext.NUM_TARGETS]; 
	Coordinates2D targetPos [] = new Coordinates2D[MatchChoiceTrialContext.NUM_TARGETS];
	double[] targetSize = new double[MatchChoiceTrialContext.NUM_TARGETS];
	double[] targetEyeWindowSize = new double[MatchChoiceTrialContext.NUM_TARGETS];
	
	public MatchChoiceTargetMessage(long timestamp, RGBColor[] targetColor, Coordinates2D[] targetPos,
			double[] targetSize, double[] targetEyeWindowSize) {
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
	public Coordinates2D[] getTargetPos() {
		return targetPos;
	}
	public void setTargetPos(Coordinates2D[] targetPos) {
		this.targetPos = targetPos;
	}
	public double[] getTargetSize() {
		return targetSize;
	}
	public void setTargetSize(double[] targetSize) {
		this.targetSize = targetSize;
	}
	
	static XStream xstream = new XStream();

	static {
		xstream.alias("ChoiceTargetMessage", MatchChoiceTargetMessage.class);
		xstream.alias("Coordinates2D", Coordinates2D.class);
		xstream.alias("RGBColor", RGBColor.class);
	}
	
	public static MatchChoiceTargetMessage fromXml (String xml) {
		return (MatchChoiceTargetMessage)xstream.fromXML(xml);
	}
	
	public static String toXml (MatchChoiceTargetMessage msg) {
		return xstream.toXML(msg);
	}
	public double[] getTargetEyeWindowSize() {
		return targetEyeWindowSize;
	}
	public void setTargetEyeWindowSize(double[] targetEyeWindowSize) {
		this.targetEyeWindowSize = targetEyeWindowSize;
	}
	public RGBColor[] getTargetColor() {
		return targetColor;
	}
	public void setTargetColor(RGBColor[] targetColor) {
		this.targetColor = targetColor;
	}
	
}
