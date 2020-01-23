package org.xper.sach.somlab;


import org.dom4j.Document;
import org.dom4j.Node;
import org.xper.sach.expt.SachExptSpec;

import com.thoughtworks.xstream.XStream;

public class SachEstimSpec {
	
	boolean eStimFlag = false;					// do estim during this trial?
	int objIdx = -1;							// during which object/stimulus will estim be performed? -1 for none
	int[] channel = new int[]{0};				// which microstimulator channels to use
	int[] baselineAmp = new int[]{0}; 			// per channel offset baseline (in microAmps)
	boolean cathodalLeading = true; 			// if true, "negative" pulse followed by "positive" pulse
	int[] cathodalAmp = new int[]{0};			// cathodal amplitude (in mircoAmps)
	int[] anodalAmp = new int[]{0};				// anodal amplitude (in mircoAmps)
	int[] cathodalWidth = new int[]{0};			// cathodal pulse width (in microSec)
	int[] anodalWidth;							// (anodal will be calculated via load balance) (in microSec)
	int[] interPhaseDur = new int[]{0};  		// time between cathodal and anodal pulses (in microSec)
	int[] phaseWidth; 							// width of phase = cathodalWidth + interPhaseDur + anodalWidth (in microSec)
	int[] numPulses;							// (number of pulses, calculated from the above settings)
	double[] pulseFreq = new double[]{0};		// frequency of pulses (in Hz)
	double startOffset = 0;						// start time relative to estim trigger (in mSec)
	double stopOffset = 100;					// stop time relative to estim trigger (in mSec)
	
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("SachEstimSpec", SachEstimSpec.class);
	}
	
	public static void main(String[] args) {  
		SachEstimSpec s = new SachEstimSpec(true, 0, new int[]{0,1}, new int[]{0,0}, true, new int[]{-1,-1}, new int[]{1,1}, new int[]{1,1}, new int[]{0,0}, new double[]{200,300}, 0, 100);
		System.out.println(s.toXml());
	}
	
	public SachEstimSpec() {};
	
	public SachEstimSpec(boolean eStimFlag, int objIdx, int[] channel, int[] baselineAmp, boolean cathodalLeading, int[] cathodalAmp, int[] anodalAmp, int[] cathodalWidth, int[] interPhaseDur, double[] pulseFreq, double startOffset, double stopOffset) {
		
		
		// check to make sure values match # of channels:
		int numChannels = channel.length;
		if (baselineAmp.length !=numChannels || 
			cathodalAmp.length != numChannels || 
			anodalAmp.length != numChannels || 
			cathodalWidth.length != numChannels || 
			pulseFreq.length != numChannels ||
			interPhaseDur.length != numChannels) 
		{	
			System.err.println("SachEstimSpec ERROR! -- number of channels doesn't match pulse specifications!");
		}
		
		// make sure amplitudes and widths are positive
		double[] negCat = new double[cathodalAmp.length];
		for (int k=0;k<cathodalAmp.length;k++)
		{	negCat[k]=-(double)cathodalAmp[k];}
		
		if (!checkValuesNonNegative(negCat) ||
			!checkValuesNonNegative(interPhaseDur) || 
			!checkValuesNonNegative(anodalAmp) || 
			!checkValuesNonNegative(cathodalWidth) || 
			!checkValuesNonNegative(pulseFreq) ) 
		{	
			System.err.println("SachEstimSpec ERROR! -- pulse specifications must be non-negative!");
		}
		
		this.eStimFlag = eStimFlag;
		this.objIdx = objIdx;
		this.channel = channel;
		this.baselineAmp = baselineAmp;
		this.cathodalAmp = cathodalAmp;
		this.anodalAmp = anodalAmp;
		this.cathodalWidth = cathodalWidth;
		this.pulseFreq = pulseFreq;
		this.interPhaseDur= interPhaseDur;
		this.startOffset = startOffset;
		this.stopOffset = stopOffset;
		
		// calculate anodalWidth
		anodalWidth = calculateAnodalWidth();
		
		// calculate phaseWidth
		phaseWidth = calculatePhaseWidth();
		
		// calculate number of pulses
		numPulses = calculateNumPulses();
		
		
	}
	
	private boolean checkValuesNonNegative(int[] arr) {
		for (int k=0; k<arr.length; k++) {
			if (arr[k] < 0)
				return false;
		}
		return true;
	}private boolean checkValuesNonNegative(double[] arr) {
		for (int k=0; k<arr.length; k++) {
			if (arr[k] < 0)
				return false;
		}
		return true;
	}
	
	private int[] calculateAnodalWidth() {
		int[] out = new int[channel.length];
		for (int k=0; k<channel.length; k++) {
			out[k] = (int)((double)cathodalWidth[k] * Math.abs((double)cathodalAmp[k] / (double)anodalAmp[k]));
		}
		return out;
	}
	
	private int[] calculatePhaseWidth(){
		int[] out= new int[channel.length];
		for (int k=0; k<channel.length; k++){
			out[k] = cathodalWidth[k] + interPhaseDur[k] + anodalWidth[k];
		}
		return out;
	}
	
	private int[] calculateNumPulses() {
		int[] out = new int[channel.length];
		double[] duration = new double[channel.length];
		for (int k=0;k<channel.length;k++)
		{ 
			duration[k]=(stopOffset - startOffset - phaseWidth[k]/1000)/1000;
			out[k]=(int) (Math.floor(duration[k]*pulseFreq[k])+1);
		}
		
		return out;
	}	
	
	public String toXml() {
		return SachEstimSpec.toXml(this);
	}
	
	public static String toXml(SachEstimSpec spec) {
//		System.out.printf("**" + s.toXML(spec) + "**");
		return s.toXML(spec);
	}
	
	public static SachEstimSpec fromXml(Document xml) {
		Node n = xml.selectSingleNode("/SachEstimExptSpec/estimSpec");
		String xmlString = n.asXML();
			
		xmlString = "<SachEstimSpec>" + xmlString.substring(11, xmlString.length()-10) + "SachEstimSpec>";	
				
//		System.out.print("***" + xmlString + "***");
		
		SachEstimSpec g= fromXml(xmlString);
		return g;
	}
	
	public static SachEstimSpec fromXml(String xml) {
		SachEstimSpec g = (SachEstimSpec)s.fromXML(xml);
		return g;
	}
	
	public boolean isEstimTrial(){
		return eStimFlag;
	}

}
