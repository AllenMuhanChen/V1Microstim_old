package org.xper.sach.somlab;

import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import org.xper.sach.somlab.EStimTrial;
import org.xper.sach.somlab.ElectricalStimulator;
import org.xper.sach.somlab.SachEstimSpec;


public class SerialTest {	 
	
    public static void main(String[] args) throws InterruptedException, IOException {
        	
    	
    	ElectricalStimulator estim = new ElectricalStimulator();
    	
    	Thread.sleep(50);
    	int sleepTimeMs = 100;
    	int numTrials = 0;
    	
    	long startTime = System.currentTimeMillis();
    	long stopTime = startTime;
    	
    	
//    	if(simulate(estim)){
//    		
//    		estim.shutdown();
//    	  	return;
//    	}
    	
    	
    	// make sure the stimulator is powered up!!
//    	if(estim.isOff()){
//    		System.out.println("Stimulator is off ... ");
//    		estim.shutdown();
//    		
//    		return;    	
//    	}
    	
    	
//	EStimTrial trial = new EStimTrial();
		
//		trial.twosComplement(-1652);
//		trial.twosComplement(1652);
		
		
		SachEstimSpec s = new SachEstimSpec(true,    // eStimFlag
											0, 	// objIdx
											new int[]{0},    	// channel []
											new int[]{0},     // baselineAmp []
											true, //cathodalLeading 
											new int[]{-250}, 	// cathodalAmp [] 
											new int[]{250}, 	// anodalAmp[]
									        new int[]{200}, 	// cathodalWidth []
									        new int[]{0},  	//interPhaseDuration
									        new double[]{200},     // pulseFreq []
									    	50,   	// startOffset
									    	70);	    // stopOffset
		//System.out.println(s.toXml());
		
		
//		System.in.read();
		
		for(int k = 0 ; k<100 ; k++){
			estim.configureStim(s);
			Thread.sleep(200);
			estim.triggerStim();
			System.out.println(k);
			Thread.sleep(1000);
		}
		//System.in.read();		
		estim.shutdown();
		
		
		
    	
    	
//    	
//
//    	String testFile = "/home/justin/git/somlab/SerialTest/bin/StimCmd_test2.txt";
// 
//    	if(!estim.loadFile(testFile)){
//    		estim.shutdown();
//    		System.out.println("Error loading file ... Program exiting\n\n");
//    		return;
//    	}
//    	
//    	numTrials = estim.getNumberOfTrials();
//    	
//    	System.out.printf("\n File loaded %d trials.  Press Enter to trigger each trial\n", numTrials);
//    	
//    	Thread.sleep(1000);
//    	
//    	for(int n = 0; n < numTrials; n++){
//    		estim.configureStim();
//    		System.out.printf("\nStimulator configured for trial %d, waiting to trigger ...", n + 1);
//    		System.in.read();
//    		//Thread.sleep(100);
//    		estim.triggerStim();
//    		//Thread.sleep(1000);
//    		System.in.read();
//    		
//    		
//    	}
//    	
//    	Thread.sleep(sleepTimeMs * 5);
//    	
//    	estim.queryBattery();
//    	Thread.sleep(sleepTimeMs * 5);
//    	
//    	
//    	estim.shutdown();
//    	
//    	stopTime = System.currentTimeMillis();
//    	System.out.printf("\n\n%5.5f sec", ((double)stopTime - (double)startTime) / 1000.0);
    	
//    	try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	
    	
    	return;
//    	
//    	
//    	
//    	
//    	
//    	
//    	estim.put32(0,  0,  100,  10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  1,  100,  -10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  2,  100,  10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  3,  100,  -10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  4,  100,  10, 0);
//    	Thread.sleep(sleepTimeMs);
//    	estim.put32(0,  0,  100,  0, 0);
//    	
//    	try {
//			Thread.sleep(sleepTimeMs);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	
//    	estim.triggerStim();
//    	
//    	try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	estim.shutdown();
    	
    }
    


public static boolean simulate(ElectricalStimulator estim){
	
	System.out.println("Simulating full experiment");
		
//	SachEstimSpec s = new SachEstimSpec(true, 0, new int[]{0,1}, new int[]{0,1}, new int[]{1,1}, new int[]{1,1}, new int[]{1,1}, new double[]{200,300}, new int[]{0, 0}, 0, 100);
	SachEstimSpec s = new SachEstimSpec(true, 0, new int[]{0,1}, new int[]{0,0}, true, new int[]{1,1}, new int[]{1,1}, new int[]{1,1}, new int[]{0,0},new double[]{200,300}, 0, 100);

	estim.configureStim(s);
	return true;
}

 
}


