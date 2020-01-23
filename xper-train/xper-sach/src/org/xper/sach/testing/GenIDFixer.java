package org.xper.sach.testing;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.xper.acq.counter.MarkEveryStepTaskSpikeDataEntry;
import org.xper.db.vo.GenerationTaskDoneList;
import org.xper.db.vo.TaskDoneEntry;
import org.xper.exception.InvalidAcqDataException;
import org.xper.exception.NoMoreAcqDataException;
import org.xper.sach.acq.counter.SachMarkEveryStepExptSpikeCounter;
import org.xper.sach.analysis.SachStimDataEntry;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.expt.SachExptSpec;
import org.xper.sach.expt.generate.SachRandomGeneration.TrialType;
import org.xper.sach.util.CreateDbDataSource;
import org.xper.sach.util.SachDbUtil;


public class GenIDFixer {


	SachDbUtil dbUtil;


	public static void main(String[] args) {

		GenIDFixer test = new GenIDFixer(setDbUtil());
		//test.FixGenIDsForGen(1);
		
		// fixed 469-478, but screwed up 470. need to re do spike data on that one

		// re-create data (accidentally screwed it up for gen 471):
//		test.FixGenIDsForGen(470);
//		test.addSpikeDataEntryIfMissing(470);
//		test.getSpikeResponses(470);

		//TODO: for gen 470, the parentId field still needs to be fixed, it was erased. 
		//      prob need to do this by eye (only 1 gen preceeds it)
		
		
	}


    public GenIDFixer(SachDbUtil dbUtil) {
    	this.dbUtil = dbUtil;
    }
	
	public void FixGenIDsForGen(long genId) {

		// get stimObjIds for genID
    	List<Long> stimObjIds = dbUtil.readStimObjIdsFromGenId(genId);
//    	List<SachStimDataEntry> stimData = dbUtil.readStimObjData(stimObjIds);
    	List<SachStimDataEntry> stimData = dbUtil.readListStimData(stimObjIds);

    	int counter = 0;
		for (SachStimDataEntry d : stimData) {
			if (d.getParentId() != -1) {
				
				System.out.println(d.getStimObjId() + " -- " + d.getLineage() + " to: " + (d.getLineage()-1));
				counter++;
				// set lineage and re-save data:
				d.setLineage(d.getLineage()-1);
				System.out.println(d.toXml());
				//dbUtil.updateStimObjData(d.getStimObjId(), d.toXml());
			}
		}
		System.out.println(counter);

	}
	
	public void addSpikeDataEntryIfMissing(long genId) { // only use this once!
    	List<Long> stimObjIds = dbUtil.readStimObjIdsFromGenId(genId);

    	int t = 0;
    	
    	for (long id : stimObjIds) {
//    		String d = dbUtil.readStimDataFromStimObjIdAsString(id);
    		String d = dbUtil.readString("StimObjData","data",dbUtil.sArray(id));

    		if (d.charAt(0) == 'o') {	// data bad/missing
    			System.out.print(id + " -- ");
    			System.out.println(d);
    			    			
    			SachStimDataEntry data = new SachStimDataEntry();
    			data.setStimObjId(id);
    			data.setTrialType(TrialType.GA.toString());
    			data.setBirthGen(2);
    			data.setLineage(t%2);
    			data.setParentId(0);
    			
    			t++;
    			
    			//System.out.println(data.toXml());
//    			dbUtil.updateStimObjData(id, data.toXml());
				dbUtil.updateLine("stimObjData","data",SachDbUtil.addQ(data.toXml()),dbUtil.sArray(id));
    			
    		}	
    	}
		
	}
	
	public void getSpikeResponses(long currentGen) {
		
//		long lastTrialToDo;
//		long lastTrialDone;
//
//		// first, wait for some time to make sure previous 'TaskToDo's are written to the db (the stimuli need to be presented anyway):
//		try
//		{	Thread.sleep(8000);	}
//		catch (Exception e) {System.out.println(e);}
//		
//		// Wait for spike data collection to be completed:	
//		int counter = 0;
//		System.out.print("Waiting for ACQ process.");
//		while (true)
//		{
//			lastTrialToDo = dbUtil.readTaskToDoMaxId();	// move this outside loop?
//			lastTrialDone = dbUtil.readTaskDoneCompleteMaxId();
//			if ( counter % 20 == 0)
//				System.out.print(".");
//			counter++;
//			if ( lastTrialToDo == lastTrialDone) { // Completed the tasks in this generation:
//				try
//				{	Thread.sleep(3000);	}
//				catch (Exception e) {System.out.println(e);}
//				System.out.println();
//				break;
//			}
//			try
//			{	Thread.sleep(300);	}
//			catch (Exception e) {System.out.println(e);}
//		}		

		// obtain spike data:
		long taskId;

		// use mine because it adds fake spike stuff!
		//MarkStimExperimentSpikeCounter spikeCounter = new MarkStimExperimentSpikeCounter();
		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
		spikeCounter.setDbUtil(dbUtil);

		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
			
			spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, 0);
			
			// for each trial done in a generation:
				// get blank FRs:
			List<Double> blankFRs = new ArrayList<Double>();
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
			{
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
				taskId = ent.getTaskId();
				
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial:
				long stimObjId;
				BsplineObjectSpec spec;
				int entIdx;				// MarkEveryStepTaskSpikeEntry gives the following epochs:
										//    [ fixation_pt_on, eye_in_succeed, stim, isi, ... (repeat x numStims), done_last_isi_to_task_end ]
										//    so to index the stimuli we skip the first 2 and do every other for as many stims as we present in a trial

				// first get blank stim FR data:
				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
					stimObjId = trialSpec.getStimObjId(n);
//					spec = BsplineObjectSpec.fromXml(dbUtil.readStimSpecFromStimObjId(stimObjId).getSpec());
					spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(stimObjId).getSpec());
					
					if (spec.isBlankStim()) {
						entIdx = 2*n+2;
						blankFRs.add(ent.getSpikePerSec(entIdx)); 
					}
				}
			}
			
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
			{
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
				taskId = ent.getTaskId();

				//System.out.println("Entering spike info for trial: " + taskId);
				
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial get FR data for all stims and save:
				long stimObjId;
				SachStimDataEntry data;
				int entIdx;

				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
					stimObjId = trialSpec.getStimObjId(n);
									
//					data = SachStimDataEntry.fromXml(dbUtil.readStimDataFromStimObjId(stimObjId).getSpec());
					data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
					
					// add acq info:					
					entIdx = 2*n+2;
					data.addTaskDoneId(taskId);
					data.setSampleFrequency(ent.getSampleFrequency());
					data.addSpikesPerSec(ent.getSpikePerSec(entIdx));
					data.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
					data.addTrialStageData(ent.getTrialStageData(entIdx));
					
					// resave data:
					if (data.getParentId() != -1) {
						System.out.println(stimObjId);
						System.out.println(data.toXml());
//						dbUtil.updateStimObjData(stimObjId, data.toXml());
						dbUtil.updateLine("stimObjData","data",SachDbUtil.addQ(data.toXml()),dbUtil.sArray(stimObjId));
					}
				}
			}	
		} catch(InvalidAcqDataException ee) {
			ee.printStackTrace();
		} catch(NoMoreAcqDataException ee) {
			ee.printStackTrace();
		}
	}


	public SachDbUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}
	
	public static SachDbUtil setDbUtil() {
    	CreateDbDataSource dataSourceMaker = new CreateDbDataSource();
    	return new SachDbUtil(dataSourceMaker.getDataSource());
	}



}
