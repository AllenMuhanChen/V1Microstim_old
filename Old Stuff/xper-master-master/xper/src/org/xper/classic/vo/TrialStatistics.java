package org.xper.classic.vo;

import com.thoughtworks.xstream.XStream;

public class TrialStatistics {
	int completeTrials = 0;
	int failedTrials = 0;
	int brokenTrials = 0;
	
	public void reset() {
		completeTrials = 0;
		failedTrials = 0;
		brokenTrials = 0;
	}
	
	static XStream xstream = new XStream();

	static {
		xstream.alias("TrialStatistics", TrialStatistics.class);
	}
	
	public static TrialStatistics fromXml (String xml) {
		return (TrialStatistics)xstream.fromXML(xml);
	}
	
	public static String toXml (TrialStatistics msg) {
		return xstream.toXML(msg);
	}

	public int getCompleteTrials() {
		return completeTrials;
	}

	public void setCompleteTrials(int completeTrials) {
		this.completeTrials = completeTrials;
	}

	public int getFailedTrials() {
		return failedTrials;
	}

	public void setFailedTrials(int failedTrials) {
		this.failedTrials = failedTrials;
	}

	public int getBrokenTrials() {
		return brokenTrials;
	}

	public void setBrokenTrials(int brokenTrials) {
		this.brokenTrials = brokenTrials;
	}
}
