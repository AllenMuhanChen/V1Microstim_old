package org.xper.choice.vo;

import org.xper.drawing.Coordinates2D;

import com.thoughtworks.xstream.XStream;

public class ChoiceTargetMessage {
	long timestamp;
	Coordinates2D targetPos [] = new Coordinates2D[ChoiceTrialContext.NUM_TARGETS];
	double[] targetSize = new double[ChoiceTrialContext.NUM_TARGETS];
	double[] targetEyeWindowSize = new double[ChoiceTrialContext.NUM_TARGETS];
	
	public ChoiceTargetMessage(long timestamp, Coordinates2D[] targetPos,
			double[] targetSize, double[] targetEyeWindowSize) {
		super();
		this.timestamp = timestamp;
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
		xstream.alias("ChoiceTargetMessage", ChoiceTargetMessage.class);
		xstream.alias("Coordinates2D", Coordinates2D.class);
	}
	
	public static ChoiceTargetMessage fromXml (String xml) {
		return (ChoiceTargetMessage)xstream.fromXML(xml);
	}
	
	public static String toXml (ChoiceTargetMessage msg) {
		return xstream.toXML(msg);
	}
	public double[] getTargetEyeWindowSize() {
		return targetEyeWindowSize;
	}
	public void setTargetEyeWindowSize(double[] targetEyeWindowSize) {
		this.targetEyeWindowSize = targetEyeWindowSize;
	}
	
}
