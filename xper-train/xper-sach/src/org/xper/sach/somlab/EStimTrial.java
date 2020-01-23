package org.xper.sach.somlab;


import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;


// plotting stuff for devel
import org.math.plot.*;

//     To Do: catch/ handle errors

public class EStimTrial {

	List<Point> params = new ArrayList();    

	int trialNum = -1;
	boolean isStim = true; 	// is this an actual estim trial
	
	// default constructor
	public EStimTrial() {
		params.clear();
		
	}

	public EStimTrial(String paramStr){
		StringTokenizer tok = new StringTokenizer(paramStr, " ");
		Integer ch;
		Integer id;
		Integer delay;
		Integer amp;
		Integer sw;
		
	
		if(tok.countTokens() == 0){
			// some kind of error
		}
		
		trialNum = Integer.parseInt(tok.nextToken());
		
		while(tok.hasMoreTokens()) {
			ch = Integer.valueOf(tok.nextToken());
			id =  Integer.valueOf(tok.nextToken());
			delay = Integer.valueOf(tok.nextToken());
			amp =  Integer.valueOf(tok.nextToken());
			sw =  Integer.valueOf(tok.nextToken());

			params.add(new Point(ch, id, delay, amp, sw));

			//System.out.printf("\nEStimTrial() : %3d : %3d, %5d, %9d, %9d, %2d", trialNum, ch, id, delay, amp, sw);

		}
	}
	
	
	public static void main(String[] args) {  
			
		EStimTrial trial = new EStimTrial();

		
//		SachEstimSpec s = new SachEstimSpec(true,   // eStimFlag
//										       0, 	// objIdx
//									new int[]{0},   // channel []
//								    new int[]{0},   // baselineAmp []
//								  new int[]{-50}, 	// cathodalAmp [] 
//								   new int[]{50}, 	// anodalAmp[]
//							      new int[]{200}, 	// cathodalWidth []
//							   new double[]{200},   // pulseFreq []
//								    new int[]{0},   // interPhaseDur []
//									    	 500,  	// startOffset
//									         520);  // stopOffset
		//System.out.println(s.toXml());
//		trial.generateFromEstimSpec(s);
		
	}
	
	

	
	public boolean generateFromEstimSpec(SachEstimSpec spec){
		final double StimCurrentUnit = 0.030;  // 30 nA as microamps (?!)
		final int MaxDelay = 65538;
		//double baseOffsetMs = 0;  // 100 ms offset from visual stim on
		double t = 0;
		double pulseDur = 0;
		Integer interPulseDur = 0;
		
		//int numPointsPerPulse = 0;
		Integer id;
		Integer initialDelay, delay1, delay2;
		Integer amp1, amp2;
		Integer sw = 0;
		
		isStim = spec.eStimFlag;
			
		for(int ch : spec.channel){
	
			// the first delay is the same for all channels, but initialize it here
			t = spec.startOffset;			
			initialDelay = (int)(t * 1000);
			pulseDur =  spec.cathodalWidth[ch] + spec.interPhaseDur[ch] +  spec.anodalWidth[ch];
			interPulseDur =(int)(((1 / spec.pulseFreq[ch]) * 1e6) - pulseDur);
			
			// reset the point id and switch state
			id = 0;
			sw = 0;
					
			// calculate the amplitudes and delays
			if(spec.cathodalLeading){						
				amp1 = twosComplement((int) (spec.cathodalAmp[ch]  / StimCurrentUnit));
				amp2 = twosComplement((int) (spec.anodalAmp[ch] / StimCurrentUnit));
				delay1 = spec.cathodalWidth[ch];
				delay2 = spec.anodalWidth[ch];
			} else {
				amp1 = twosComplement((int) (spec.anodalAmp[ch] / StimCurrentUnit));
				amp2 = twosComplement((int) (spec.cathodalAmp[ch] / StimCurrentUnit));
				delay1 = spec.anodalWidth[ch];
				delay2 = spec.cathodalWidth[ch];		
			}
			
			//System.out.println("EStimTrial::generateFromEstimSpec() numPulses == " + spec.numPulses[ch]);							
			
			for(int n = 0; n < spec.numPulses[ch]; n++){
					
				// add extra points to create a longer delay
				while(initialDelay > MaxDelay) {
					params.add(new Point(ch, id++, MaxDelay, 0, sw));
					initialDelay -= MaxDelay;
					
				}
				
				params.add(new Point(ch, id++, initialDelay, amp1, sw));
				
				// add extra point for separated pulses
				if(spec.interPhaseDur[ch] > 0) {
					params.add(new Point(ch, id++, delay1, 0, sw));
					params.add(new Point(ch, id++, spec.interPhaseDur[ch], amp2, sw));
				} else {
					params.add(new Point(ch, id++, delay1, amp2, sw));
				}
				
				// the last point, set the switch to 2
				if(n == spec.numPulses[ch] - 1) {
					sw = 2;
				}
				
				params.add(new Point(ch, id++, delay2, 0, sw));
						
				initialDelay = interPulseDur;
			}
		}
		
		plotPoints(false);
		
		return true;
		
	}
	
	
	public int getNumberOfPoints() {
		return params.size();
	}

	public Point getPoint(int whichPoint){
		return params.get(whichPoint);
	}


	public void plotPoints(boolean shouldActuallyShowPlot){
		
		// devel
		double[]  tSec = new double[params.size() + 1];
		double[] vals = new double[params.size() + 1];
		int ndx = 1;
		tSec[0] = 0;
		vals[0]	= 0;
		for(Point p : params) {
			//System.out.println( p.ch + " " + p.id + " " + p.delay  + " " + p.amp + " " + p.sw );
			tSec[ndx] =( p.delay  / 1e6) + tSec[ndx - 1];
			vals[ndx] = p.amp;
			//System.out.println( " t = " + tSec[ndx] + ", val = " + vals[ndx] );
			++ndx;
		}

		if(shouldActuallyShowPlot){
			Plot2DPanel plot = new Plot2DPanel();
			//plot.addBarPlot("scatter", vals);
			plot.addLinePlot("pulse train",  tSec, vals);

			JFrame frame = new JFrame("Plot");
			frame.setContentPane(plot);
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	public int twosComplement(int n){
		int numBits = 16;
			
		String val = Integer.toBinaryString(n);
			
		if(n < 0){
			
			int  x = (int)(Integer.parseInt(val.substring(val.length() - numBits, val.length()), 2) & 0xFFFF);
			
			//System.out.println("EStimTrial::twosComp() : " + n + "  :  " + x);
			return x;
		}else {
			//System.out.println("EStimTrial::twosComp() : " + n + "  :  " + n);
			return n;
		}
				
	}


}
