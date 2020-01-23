package org.xper.sach.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import org.xper.acq.counter.TrialStageData;
import org.xper.sach.util.SachMathUtil;
//import org.xper.sach.drawing.stimuli.BsplineObjectSpec;

import com.thoughtworks.xstream.XStream;

public class SachStimDataEntry {
	/**
	 * GA and firing rate information for a single stimulus object, for Beh objects we ignore parentage stuff
	 */

	// pre-run info:
	String trialType;									// "BEH" or "GA"
	int lineage = -1;									// for GA stim, the lineage in which it arose (prob 0 or 1)
	long birthGen = -1;									// for GA stim, the generation in which it first arose
	long parentId = -1;									// for GA stim, the parent stimObjId from which it was derived (-1 if no parent)
	long stimObjId;										// index into StimObjData db table
//	List<Long> stimSpecIds = new ArrayList<Long>();		// array of trials (indexed by StimSpec id) in which stim obj can be found
//	List<Long> taskToDoIds = new ArrayList<Long>();		// array of tasks (indexed by TaskToDo id) in which stim obj can be found
	
	// post-run info:
	double sampleFrequency;

	List<Long> taskDoneIds = new ArrayList<Long>();							// array of tasks (indexed by TaskDone id) in which stim obj was presented
	List<TrialStageData> trialStageData = new ArrayList<TrialStageData>();	// array of spike data for each stimulus presentation
	List<Double> spikesPerSec = new ArrayList<Double>();					// firing rate for each stimulus presentation
	List<Double> bkgdSpikesPerSec = new ArrayList<Double>();				// firing rate for any blank stimuli shown in the same generation

	double avgFR;															
	double stdFR;
	double avgBkgdFR = 0;	// this is calculated from any blank stimuli run in the same generation
	double stdBkgdFR;
	
//	List<BsplineObjectSpec> stimObjSpecs = new ArrayList<BsplineObjectSpec>();	// stimulus details for each stimulus presentation (useful for Beh stimuli when morphing or randomizing limb lengths)

	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("Data", SachStimDataEntry.class);
//		s.addImplicitCollection(SachStimDataEntry.class, "objects", "object", SachStimDataEntry.class);
		
//		s.alias("limb", LimbSpec.class);
//		s.addImplicitCollection(BsplineObjectSpec.class, "limbs", "limb", LimbSpec.class);
	}

	public String toXml() {
		return SachStimDataEntry.toXml(this);
	}
	
	public static String toXml(SachStimDataEntry spec) {
		return s.toXML(spec);
	}
	
	public static SachStimDataEntry fromXml(String xml) {
		SachStimDataEntry g = (SachStimDataEntry)s.fromXML(xml);
		return g;
	}

	// setters & getters:
	
	public long getStimObjId() {
		return stimObjId;
	}
	public void setStimObjId(long stimObjId) {
		this.stimObjId = stimObjId;
	}
	public String getTrialType() {
		return trialType;
	}
	public void setTrialType(String type) {
		this.trialType = type;
	}
	public int getLineage() {
		return lineage;
	}
	public void setLineage(int lineage) {
		this.lineage = lineage;
	}
	public long getBirthGen() {
		return birthGen;
	}
	public void setBirthGen(long birthGen) {
		this.birthGen = birthGen;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
//	public List<Long> getStimSpecIds() {
//		return stimSpecIds;
//	}
//	public long getStimSpecId(int i) {
//		return stimSpecIds.get(i);
//	}
//	public void setStimSpecIds(List<Long> stimSpecIds) {
//		this.stimSpecIds = stimSpecIds;
//	}
//	public void addStimSpecId(long id) {
//		stimSpecIds.add(id);
//	}
//	
//	public List<Long> getTaskToDoIds() {
//		return taskToDoIds;
//	}
//	public long getTaskToDoId(int i) {
//		return taskToDoIds.get(i);
//	}
//	public void setTaskToDoIds(List<Long> taskToDoIds) {
//		this.taskToDoIds = taskToDoIds;
//	}
//	public void addTaskToDoId(long id) {
//		taskToDoIds.add(id);
//	}
	
	// data:
	public double getSampleFrequency() {
		return sampleFrequency;
	}
	public void setSampleFrequency(double sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
	}
	
	public List<Long> getTaskDoneIds() {
		return taskDoneIds;
	}
	public long getTaskDoneId(int i) {
		return taskDoneIds.get(i);
	}
	public void setTaskDoneIds(List<Long> taskDoneIds) {
		this.taskDoneIds = taskDoneIds;
	}
	public void addTaskDoneId(long d) {
		taskDoneIds.add(d);
	}

	public List<TrialStageData> getTrialStageData() {
		return trialStageData;
	}
	public TrialStageData getTrialStageData(int i) {
		return trialStageData.get(i);
	}
	public void setTrialStageData(List<TrialStageData> data) {
		this.trialStageData = data;
	}
	public void addTrialStageData(TrialStageData d) {
		trialStageData.add(d);
	}
	
	public List<Double> getSpikesPerSec() {
		return spikesPerSec;
	}
	public double getSpikesPerSec(int i) {
		return spikesPerSec.get(i);
	}
	public void setSpikesPerSec(List<Double> spikesPerSec) {
		this.spikesPerSec = spikesPerSec;
		
		setAvgFR(SachMathUtil.mean(spikesPerSec));
		setStdFR(SachMathUtil.std(spikesPerSec));
	}
	public void addSpikesPerSec(double r) {
		spikesPerSec.add(r);
		
		// also set the avg and std FRs:
		DescriptiveStatistics stats = DescriptiveStatistics.newInstance();

		for (int n=0;n<getNumPresentations();n++) {
			stats.addValue(spikesPerSec.get(n));
		}
		
		setAvgFR(stats.getMean());
		setStdFR(stats.getStandardDeviation());
	}

	public int getNumPresentations() {
		return taskDoneIds.size();
	}
	
	public double getAvgFR() {
		return avgFR;
	}
	public double getAvgFRminusBkgd() {
		return avgFR-avgBkgdFR;
	}
	private void setAvgFR(double avgFR) {
		this.avgFR = avgFR;
	}
	public double getStdFR() {
		return stdFR;
	}
	private void setStdFR(double stdFR) {
		this.stdFR = stdFR;
	}
	
	public List<Double> getBkgdSpikesPerSec() {
		return bkgdSpikesPerSec;
	}
	public double getBkgdSpikesPerSec(int i) {
		return bkgdSpikesPerSec.get(i);
	}
	public void setBkgdSpikesPerSec(List<Double> bkgdSpikesPerSec) {
		this.bkgdSpikesPerSec = bkgdSpikesPerSec;
		
		setBkgdAvgFR(SachMathUtil.mean(bkgdSpikesPerSec));
		setBkgdStdFR(SachMathUtil.std(bkgdSpikesPerSec));
	}
	public void addBkgdSpikesPerSec(double r) {
		bkgdSpikesPerSec.add(r);
		
		setBkgdAvgFR(SachMathUtil.mean(bkgdSpikesPerSec));
		setBkgdStdFR(SachMathUtil.std(bkgdSpikesPerSec));
	}
	
	public double getBkgdAvgFR() {
		return avgBkgdFR;
	}
	private void setBkgdAvgFR(double avgBkgdFR) {
		this.avgBkgdFR = avgBkgdFR;
	}
	public double getBkgdStdFR() {
		return stdBkgdFR;
	}
	private void setBkgdStdFR(double stdBkgdFR) {
		this.stdBkgdFR = stdBkgdFR;
	}
}
