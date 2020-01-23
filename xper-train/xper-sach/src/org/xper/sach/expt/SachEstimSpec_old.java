package org.xper.sach.expt;


import com.thoughtworks.xstream.XStream;

public class SachEstimSpec_old {
	
	boolean eStimFlag = false;					// do estim during this trial?
	int objIdx = -1;							// during which object/stimulus will estim be performed? -1 for none
	int[] channel = new int[]{0};				// which microstimulator channels to use
	double[] cathodalAmp = new double[]{0};		// cathodal amplitude (in mircoAmps)
	double[] anodalAmp = new double[]{0};		// anodal amplitude (in mircoAmps)
	double[] cathodalWidth = new double[]{0};	// cathodal pulse width (in msec)
	double[] anodalWidth;						// (anodal will be calculated via load balance) (in msec)
	double[] pulseFreq = new double[]{0};		// frequency of pulses (in Hz)
	int[] numPulses;							// (number of pulses, calculated from the above settings)
	double startOffset = 0;						// start time realtive to visual stimulus onset (in ms)
	double stopOffset = 100;					// stop time realtive to visual stimulus onset (in ms)
	
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("SachEstimSpec", SachEstimSpec_old.class);
	}
	
	public static void main(String[] args) {  
		SachEstimSpec_old s = new SachEstimSpec_old(true, 0, new int[]{0,1}, new double[]{1,1}, new double[]{1,1}, new double[]{1,1}, new double[]{200,300}, 0, 100);
		System.out.println(s.toXml());
	}
	
	public SachEstimSpec_old(boolean eStimFlag, int objIdx, int[] channel, double[] cathodalAmp, double[] anodalAmp, double[] cathodalWidth, double[] pulseFreq, double startOffset, double stopOffset) {
		
		// check to make sure values match # of channels:
		int numChannels = channel.length;
		if (cathodalAmp.length != numChannels || 
			anodalAmp.length != numChannels || 
			cathodalWidth.length != numChannels || 
			pulseFreq.length != numChannels ) 
		{	
			System.err.println("ERROR! -- number of channels doesn't match pulse specifications!");
		}
		
		// make sure amplitudes and widths are positive
		if (!checkValuesGreaterThanZero(cathodalAmp) || 
			!checkValuesGreaterThanZero(anodalAmp) || 
			!checkValuesGreaterThanZero(cathodalWidth) || 
			!checkValuesGreaterThanZero(pulseFreq) ) 
		{	
			System.err.println("ERROR! -- pulse specifications must be non-negative!");
		}
		
		this.eStimFlag = eStimFlag;
		this.objIdx = objIdx;
		this.channel = channel;
		this.cathodalAmp = cathodalAmp;
		this.anodalAmp = anodalAmp;
		this.cathodalWidth = cathodalWidth;
		this.pulseFreq = pulseFreq;
		this.startOffset = startOffset;
		this.stopOffset = stopOffset;
		
		// calculate anodalWidth
		anodalWidth = calculateAnodalWidth();
		
		// calculate number of pulses
		numPulses = calculateNumPulses();
		
		
		
	}
	
	private boolean checkValuesGreaterThanZero(double[] arr) {
		for (int k=0; k<arr.length; k++) {
			if (arr[k] < 0)
				return false;
		}
		return true;
	}
	
	private double[] calculateAnodalWidth() {
		double[] out = new double[channel.length];
		for (int k=0; k<channel.length; k++) {
			out[k] = cathodalWidth[k] * cathodalAmp[k] / anodalAmp[k];
		}
		return out;
	}
	
	private int[] calculateNumPulses() {
		int[] out = new int[channel.length];
		double[] duration = new double[channel.length];
		for (int k=0;k<channel.length;k++)
		{ 
			duration[k]=(stopOffset - startOffset - cathodalWidth[k] - anodalWidth[k])/1000;
			out[k]=(int) Math.floor(duration[k]*pulseFreq[k]);
		}
		
		return out;
	}	
	
	public String toXml() {
		return SachEstimSpec_old.toXml(this);
	}
	
	public static String toXml(SachEstimSpec_old spec) {
		return s.toXML(spec);
	}
	
	public static SachEstimSpec_old fromXml(String xml) {
		SachEstimSpec_old g = (SachEstimSpec_old)s.fromXML(xml);
		return g;
	}

}
