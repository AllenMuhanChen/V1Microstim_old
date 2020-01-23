package org.xper.sach.expt; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xper.Dependency;
import org.xper.db.vo.SystemVariable;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.sach.analysis.SachStimDataEntry;
import org.xper.sach.drawing.screenobj.DiskObject;
import org.xper.sach.drawing.stimuli.BsplineObject;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.drawing.stimuli.OccluderManifoldSpec;
import org.xper.sach.expt.generate.SachRandomGeneration.TrialType;
import org.xper.sach.expt.generate.SachStimSpecGenerator;
import org.xper.sach.util.MyMathRepository;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachMathUtil;
import org.xper.time.TimeUtil;

public class SachExptSpecGenerator implements SachStimSpecGenerator {	// ugh, just remove SachStimSpecGenerator!

	@Dependency
	SachDbUtil dbUtil;
	@Dependency
	TimeUtil globalTimeUtil;
	@Dependency
	AbstractRenderer renderer;
	
	// variables
	static long rewardAmount = 100;
	static double targetEyeWindowSize = 4; // make smaller, 3
	double morphLineLimit = 0;	// from 0 to 1: 0 is no morphing, 1 is full morphing // TODO: finish this
	boolean doRandLengths = false;	// want this true only for training?
	
	static public enum StimType { BEH_MedialAxis, BEH_Curvature, BEH, GA, GAManual, BLANK, NA };						// stimulus types
	static public enum BehavioralClass { SAMPLE, MATCH, NON_MATCH, NA };										// behavioral class of stimuli	
//	static public enum BehavStimCats { SEVEN_h, Y_h, downL_downT, I_downT, SEVEN_t, Y_t, downL_J, I_J }; 	// behavioral stimulus categories

	TrialType trialType;	// set via SachRandomGeneration
	long taskId;		
	
	
	// JK variables for training enhancements i.e size, color
	int prevCategory = -1;
	float rgbaVec[] = {0.0f, 0.0f, 0.0f, 1.0f};
	double fader = 1.0;
	boolean  shouldUseColorCues = false; 
	boolean shouldUseSizeCues = false;
	float growthFactor = 1.0f;
	static int previousTrialType = 0;
	static int previousTest = -1;
	static int previousSample = -1;
	double morphLengthGain = 0.0;
	double morphWidthGain = 0.0;
	double morphBias = 0.0;
	double AlphaGainThreshold = 0.975;
	
	// JK 7 June 2016 
	// occluder manifold parameter container 
	// 
	// seriesId, stim1, stim2, #steps, node1, node2,  perpDist, paraDist1, paraDist2, innerRad, outerRad
	// 
	static Map<Integer, OccluderManifoldSpec> occluderSpecMap;
	
//	public void fillOccluderSpecMap(){
//		occluderSpecMap = new HashMap<Integer, OccluderManifoldSpec>();
//		
//		
//		final int NumSeries = 4;
//
//		// this table should be in the database
//		// series parameters:
//		// 
//		double params[][] = {
//				//   0       1     2      3       4      5       6           7          8        9          10  
//				//seriesId stim1 stim2 numSteps node1, node2,  perpDist, paraDist1, paraDist2, innerRad, outerRad
//				{ 1.0,  0.0, 1.0, 5.0,  3.0,  5.0,   -1.0,  0.0,   -1.0,  8.0,  12.0},
//				{ 2.0,  2.0, 3.0, 5.0,  0.0,  1.0,   -4.0,  0.0,   -1.0,  5.0,  9.0},
//				{ 3.0,  5.0, 6.0, 5.0,  0.0,  1.0,   -4.0,  0.0,   -1.0,  13.0,  17.0},
//				{ 4.0,  7.0, 8.0, 5.0,  0.0,  1.0,   -4.0,  0.0,   -1.0,  8.0,  12.0}
//
//		};
//		
//		for(int n = 0; n < NumSeries; n++)	{
//			OccluderManifoldSpec oms = new OccluderManifoldSpec();
//			oms.setValues(params[n]);
//			occluderSpecMap.put(oms.getSeriesId(), oms);
//		}
//		
//		// debug
////		System.out.println("fillOccluderSpecMap() \nseriesId   stim1   stim2   ");
////		for( int i = 0; i < NumSeries; i++ ){
////			OccluderManifoldSpec oms = new OccluderManifoldSpec();
////			oms = occluderSpecMap.get(Integer.valueOf(i + 1));
////			System.out.format("\n%5d %5d %5d ", oms.getSeriesId(), oms.getStim1(), oms.getStim2());			
////		}
//	}
//	
	
	
	// ---------------------------------
	// ---- Beh stimulus generation ----
	// ---------------------------------
		
	
	public SachExptSpec generateBehTrial_training(int[] nonMatchCats, int[] match_HalfMatch_Cats, double percentMatches, double percentHalfCommon) {
		// -- this method randomly chooses whether this is a match or non-match trial, then randomly picks 
		// stim categories appropriately
		doRandLengths = true;
		
		// WHICH TYPE OF STIM TO USE?	
		BehavioralClass behClass_test = null;
		int cat_test, cat_sample;
		int targetIndex;
		
		double percentHC_NonMatch = percentHalfCommon/(1 - percentMatches);
		boolean doHalfCommon = SachMathUtil.randBoolean(percentHC_NonMatch);
		
		List<int[]> halfCommonList = new ArrayList<int[]>();
		halfCommonList.add(new int[]{1,4});
		halfCommonList.add(new int[]{0,5});
		halfCommonList.add(new int[]{3,6});
		halfCommonList.add(new int[]{2,7});
		halfCommonList.add(new int[]{0,5});
		halfCommonList.add(new int[]{1,4});
		halfCommonList.add(new int[]{2,7});
		halfCommonList.add(new int[]{3,6});
		halfCommonList.add(new int[]{9,12});
		halfCommonList.add(new int[]{8,13});
		halfCommonList.add(new int[]{11,14});
		halfCommonList.add(new int[]{10,15});
		halfCommonList.add(new int[]{8,13});
		halfCommonList.add(new int[]{9,12});
		halfCommonList.add(new int[]{10,15});
		halfCommonList.add(new int[]{11,14});
		
//		int[] curHalfList = halfCommonList. 
//		int[] newHalf   = new int[]{1,4};
		
		
//		if(percentMatches != 0.500){
			// MATCH or NON-MATCH trial (0 = match, -1 = no match):
			targetIndex = SachMathUtil.randBoolean(percentMatches) ? 0 : -1;
//		} else {
//			if(previousTrialType == 0){
//				targetIndex = -1;				
//			
//			} else {
//				targetIndex = 0;
//			}
//			previousTrialType = targetIndex;
//			System.out.println("SachExptSpec.generateBehTrial_training() : alternating trial types : " + targetIndex);
//		}

		int matchInd = SachMathUtil.randRange(match_HalfMatch_Cats.length-1,0);
		cat_test = match_HalfMatch_Cats[matchInd];	// randomly pick test category from among match_HalfMatch_Cats
		
		// CHOOSE STIM CATEGORIES:		
		if (targetIndex >= 0) { 	//match trial 
			behClass_test = BehavioralClass.MATCH;
			
			System.out.println("matchInd: " + matchInd + ",  cat_test: " + cat_test);
			
			// JK 29 August 2016 prevent duplicates
			// AWC: taking out duplicate prevention: July2017
//			if(cat_test == previousTest && cat_test == previousSample){
//				int[] rematchCats = SachMathUtil.removeElement(match_HalfMatch_Cats, cat_test);
//				cat_test = rematchCats[SachMathUtil.randRange(rematchCats.length-1, 0)];	// randomly pick test category from among match_HalfMatch_Cats
//			}
			cat_sample = cat_test;												// sample category same as test (match)
			
		} else {					//non-match trial 
			behClass_test = BehavioralClass.NON_MATCH;
//			int nonMatchInd = SachMathUtil.randRange(nonMatchCats.length-1, 0);
//			cat_test = nonMatchCats[nonMatchInd];		// randomly pick test category from among nonMatchCats
			
			// JK 29 August 2016 prevent duplicates
			if(cat_test == previousTest){
				// remove current cat_test if there are only 2 options
				if(nonMatchCats.length == 2){
					int[] regenCatTest = SachMathUtil.removeElement(nonMatchCats, cat_test);
					cat_test = regenCatTest[SachMathUtil.randRange(regenCatTest.length-1, 0)];
				} else {
					// remove the previous sample
					nonMatchCats = SachMathUtil.removeElement(nonMatchCats, previousSample);
				}				
			}
			
			
			int[] curHalfList = halfCommonList.get(cat_test);
//			int[] curNonHalfList = SachMathUtil.removeElement(nonMatchCats,cat_test);
//			for (int rr = 0 ; rr<curHalfList.length;rr++){
//				curNonHalfList = SachMathUtil.removeElement(curNonHalfList, curHalfList[rr]);
//			}
			
			if (doHalfCommon){
				curHalfList = SachMathUtil.intersectElement(curHalfList,nonMatchCats);
				cat_sample = curHalfList[SachMathUtil.randRange(curHalfList.length-1,0)];
				
			} else{
				int[] curNonMatch = SachMathUtil.removeElement(nonMatchCats,cat_test);
				for (int rr = 0;rr<curHalfList.length;rr++){
					curNonMatch = SachMathUtil.removeElement(curNonMatch,curHalfList[rr]);
				}
				cat_sample = curNonMatch[SachMathUtil.randRange(curNonMatch.length-1,0)]; 
			}
			
//			int[] nonMatchCats = SachMathUtil.removeElement(nonMatchCats, cat_test);	// remove cat_test from nonMatchCats
//			cat_sample = nonMatchCats[SachMathUtil.randRange(nonMatchCats.length-1,0)];	// pick sample category from among (nonMatchCats-catA) (non-match!)
		}
		
		// final check
		if(previousTest == cat_test &&	previousSample == cat_sample){
			System.out.println("generateBehTrial_training() duplicate trial detected!!" );
		}
		
		previousTest = cat_test;
		previousSample = cat_sample;
		
		SachExptSpec g = genBehTrialFromCats(cat_test,cat_sample,targetIndex, behClass_test,TrialType.BEH_train);
		return g;
		
	}
	
	
	public SachExptSpec genBehTrialFromCats(int cat_test, int cat_sample, int targetIdx, BehavioralClass behClass, TrialType trialType) {
		
		StimType type = StimType.BEH_MedialAxis;	// only using medial-axis defined stimuli for now
		
		// JK 6 Oct 2016 adding morphs to training protocol
		// BsplineObjectSpec spec_test = createBehavStimFromCat(type,cat_test,behClass,trialType);
		BsplineObjectSpec spec_test = createMorphedBehavStimFromCat(type, cat_test, behClass, trialType);
		BsplineObjectSpec spec_sample = createBehavStimFromCat(type,cat_sample,BehavioralClass.SAMPLE,trialType);
		
//		System.out.println("BehTrial: test=" + cat_test + " sample=" + cat_sample);
		
		// test stimulus is shown first, then the sample (or reference) stimulus
		// [this allows that the neural response to the test stimulus be unaffected by any expectation caused
		//  by the sample stimulus]
		return createBehTrial(spec_test.getStimObjId(), spec_sample.getStimObjId(), targetIdx);
	}
	
	// JK 9 August 2016
	// generate a list of SachExptSpecs for a given series
	//
	public List<SachExptSpec> generateOccludedSeries(int seriesId, double percentMatches, double alphaGain, Coordinates2D currentTargetLocation,double currentWindowSize) {
		//		
		double MaxLocationVal = 998.0;
		
		StimType type = StimType.BEH_MedialAxis;	// only using medial-axis defined stimuli for now
		int cat_test = -1;
		int cat_sample = -1;
		int numLocs = -1;
		int targetIdx = -1;
		BehavioralClass behClass = BehavioralClass.NON_MATCH;
		
		OccluderManifoldSpec oms1 = new OccluderManifoldSpec();
		OccluderManifoldSpec oms2 = new OccluderManifoldSpec();
		OccluderManifoldSpec omsTest = new OccluderManifoldSpec();
		OccluderManifoldSpec omsSample = new OccluderManifoldSpec();	
		
		List<SachExptSpec> seriesSpecs = new ArrayList<SachExptSpec>();
		SachExptSpec spec = null;
		
		// this assumes a particular table structure ....
		oms1 = occluderSpecMap.get(Integer.valueOf(seriesId - 1) * 2);
		oms2 = occluderSpecMap.get(Integer.valueOf(seriesId - 1) * 2 + 1);
	
//		System.out.println("SachExptSpecGenerator::generateOccludedSeries() : seriesIds = " + oms1.getSeriesId() + ", " + oms2.getSeriesId());

		// verify seriesId?
		if(oms1.getSeriesId().intValue() != oms2.getSeriesId().intValue()){
			System.out.println("SachExptSpecGenerator::generateOccludedSeries() : seriesIds are different!! ");
		}
					
		// set sample & test
		// -shs- test stimulus is shown first, then the sample (or reference) stimulus
		// -shs- [this allows that the neural response to the test stimulus be unaffected by any expectation caused
		// -shs- by the sample stimulus]
		
		// 
		for(int orderNum = 1; orderNum <= 2; orderNum++){
					
			if(orderNum == 1){
				omsTest = oms1;
				omsSample = oms2;
			} else {
				omsTest = oms2;
				omsSample = oms1;
			}
						
			numLocs = omsTest.getNumLocations();
				
			// build trials for each location
			for(int n = 0; n < numLocs; n++){
				
				if(omsTest.getLocation(n) < MaxLocationVal){
				
					// MATCH or NON-MATCH trial (0 = match, -1 = no match):
					targetIdx = SachMathUtil.randBoolean(percentMatches) ? 0 : -1;
					
					cat_test = omsTest.getCategory();
					cat_sample = omsSample.getCategory();		
					
					if(targetIdx == 0){					
						behClass = BehavioralClass.MATCH;
						cat_sample = cat_test;	
					} else {
						behClass = BehavioralClass.NON_MATCH;
					}
				
					omsTest.setAlphaGain(alphaGain);
					
					//BsplineObjectSpec spec_test   = createBehavOccludedStimFromCat(seriesId, omsTest.getId(), n, cat_test, behClass, alphaGain);
					BsplineObjectSpec spec_test   = createBehavOccludedStimFromCat(omsTest, n, behClass);
					BsplineObjectSpec spec_sample = createBehavStimFromCat( type, cat_sample, BehavioralClass.SAMPLE, TrialType.BEH_occluded);	
					
					// the last location will be 50% ambiguity, indicated by targetIdx == 2 instead of 0, or -1
					// only when alphaGain > AlphaGainThreshold
					if(alphaGain >= AlphaGainThreshold && n == numLocs - 1){
						targetIdx = 2;
						System.out.println("SachExptSpecGenerator::generateOccludedSeries() : setting targetIdx = 2");
					}
					
	// JK DEBUG!!
	//				targetIdx = 2;	
					
					// JK 26 Oct 2016 - adding target location
					spec = createBehTrial(spec_test.getStimObjId(), spec_sample.getStimObjId(), targetIdx);
					spec.setTargetPosition(currentTargetLocation);
					spec.setTargetEyeWinSize(currentWindowSize);
					seriesSpecs.add(spec);
					
				}
				

//	System.out.println("SachExptSpecGenerator::generateOccludedSeries() : DEBUG!!  Hardcoded targetIdx = " + targetIdx + ", test cat = "  + cat_test + ", sample = " + cat_sample);
					
			}
		}
		
		// verify ...		
//		for(SachExptSpec temp : seriesSpecs){
//			System.out.println(temp.toXml());
//		}
		
		return seriesSpecs;
		
	}
	
	
	
	//private BsplineObjectSpec createBehavOccludedStimFromCat(int seriesId, int occluderId, int locationIndex, int category, BehavioralClass behavClass, double alphaGain) {
	private BsplineObjectSpec createBehavOccludedStimFromCat(OccluderManifoldSpec oms, int locationIndex, BehavioralClass behavClass) {
		boolean jitterPosition = false;
//		boolean doRandLengths = true;
		boolean doMorphs = false;
		double morphLim = morphLineLimit;	// *** implement this ***
		// TODO:when morph lines are spec'ed out in BsplineObjectSpec, need to keep track of morph line
		//		limits for each category this will need to intereact with whatever module uses
		//		behavioral data to update the morph line limits

		
		// STIMULUS SETUP
		BsplineObjectSpec s = new BsplineObjectSpec();
		SachStimDataEntry d = new SachStimDataEntry();
		
		double xCntr = 0;	// defaults
		double yCntr = 0;
								
		
		if (jitterPosition) {
			xCntr += Math.random() * 10 - 5;	// for randomly jittering the position of stimuli
			yCntr += Math.random() * 10 - 5;
		}
		
		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();

		// -- set spec values 		
		s.setStimObjId(stimObjId);
		s.setStimType(StimType.BEH_MedialAxis.toString());
		s.setBehavioralClass(behavClass.name());

		// JK 25 April 2016 
		s.setGrowthFactor(growthFactor);
		s.setFader(fader);
		s.setCategory(oms.getCategory(), shouldUseColorCues, shouldUseSizeCues);

		//s.setCategory(category);
		s.setXCenter(xCntr);
		s.setYCenter(yCntr);
		
		// JK 25 April 2016 added sizeCues so size is set in setCategory(int, bool, bool)
		// s.setSize(size);
		
		// extract the relevant OccluderManifold stuff needed by the occluderSpec
		s.getOccluderSpec().setSeriesId(oms.getSeriesId());
		s.getOccluderSpec().setLocationVal((float)oms.getLocation(locationIndex));
		s.getOccluderSpec().setOffsetVal((float)oms.getOffsetVal(locationIndex));
		s.getOccluderSpec().setAlphaGain((float)oms.getAlphaGain());
		
		s.setAnimation(false);
		s.setDoRandom(doRandLengths);	// randomly change the lengths (and/or widths) of behavioral simuli
		s.setDoMorph(doMorphs);			// randomly choose object parameters from morph line TODO: remove this and just control w morphLim?
		s.setMorphLengthWidthOnly(false);
		s.setMorphLim(morphLim);		// current limit set for morph line 		
		s.setMorphBias(morphBias);
		s.setMorphLengthGain(morphLengthGain);
		
		// -- need to re-create object spec after running it through the object (BsplineObject), (this saves the limbs info with the spec):
		BsplineObject obj = new BsplineObject();
//		obj.setSpec(s.toXml());

		obj.setOccluderManifoldParams(oms);

		// create the obj?
		obj.setSpec(s.toXml());

		// the bso now has real node locations so calculate the occluder spec
		//calculateOccluderSpec(obj, oms.getSeriesId(), oms.getId(), locationIndex);
		obj.createOccluder(oms, locationIndex);
		
		// retrieve the fully specified  BsplineObjectSpec
		BsplineObjectSpec ss = obj.getSpec();			
		
		
		// -- set data values
		d.setStimObjId(stimObjId);
		d.setTrialType(trialType.toString());
		
		// create SachStimDataEntry, then write to DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
				
		return s;
	}
	
	public BsplineObjectSpec createMorphedBehavStimFromCat(StimType type, int category, BehavioralClass behavClass, TrialType trialType) {
		return createMorphedBehavStimFromCat(type, category, behavClass, trialType,true);
	}
// JK 6 Oct 2016  adding length/width morphs to training protocol
// AWC July2017, making this public
	public BsplineObjectSpec createMorphedBehavStimFromCat(StimType type, int category, BehavioralClass behavClass, TrialType trialType,boolean writeToDatabase) {
		// given stim type, category, and behavioral class (sample, match), generate stim spec
	
		// STIMULUS SETUP
		BsplineObjectSpec s = new BsplineObjectSpec();
		
		double xCntr = 0;	// defaults
		double yCntr = 0;
		
		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();
		
		// -- set spec values 		
		s.setStimObjId(stimObjId);
		s.setStimType(type.toString());
		s.setBehavioralClass(behavClass.toString());

		// JK 25 April 2016 
		s.setGrowthFactor(growthFactor);
		s.setFader(fader);
		s.setCategory(category, shouldUseColorCues, shouldUseSizeCues);

		s.setCategory(category);
		s.setXCenter(xCntr);
		s.setYCenter(yCntr);
		
		// JK 25 April 2016 added sizeCues so size is set in setCategory(int, bool, bool)
		// s.setSize(size);
		
		s.setAnimation(false);
		s.setDoRandom(doRandLengths);	// randomly change the lengths (and/or widths) of behavioral simuli
		
		//prevent the default morphing behaviour by setting doMorph == false
		s.setDoMorph(false);			//
		s.setMorphLengthWidthOnly(true);
		
		//s.setMorphLim(morphLim);		// current limit set for morph line 		
		s.setMorphLengthGain(this.morphLengthGain);
		s.setMorphWidthGain(this.morphWidthGain);
		s.setMorphBias(this.morphBias);
		

		if (writeToDatabase){
			writeSpecToDatabase(s);
		}
				
		return s;	
	}
	public BsplineObjectSpec createMorphedBehavStimFromCat(StimType type, int category) {
		return createMorphedBehavStimFromCat(type, category,true);
	}
	public BsplineObjectSpec createMorphedBehavStimFromCat(StimType type, int category, boolean writeToDatabase) {
		// given stim type, category, and behavioral class (sample, match), generate stim spec
		
			// STIMULUS SETUP
			BsplineObjectSpec s = new BsplineObjectSpec();
			
			double xCntr = 0;	// defaults
			double yCntr = 0;
			
			// GENERATE STIM	
			long stimObjId = globalTimeUtil.currentTimeMicros();
			
			// -- set spec values 		
			s.setStimObjId(stimObjId);
			s.setStimType(type.toString());

			// JK 25 April 2016 
			s.setGrowthFactor(growthFactor);
			s.setFader(fader);
			s.setCategory(category, shouldUseColorCues, shouldUseSizeCues);

			s.setCategory(category);
			s.setXCenter(xCntr);
			s.setYCenter(yCntr);
			
			// JK 25 April 2016 added sizeCues so size is set in setCategory(int, bool, bool)
			// s.setSize(size);
			
			s.setAnimation(false);
			s.setDoRandom(doRandLengths);	// randomly change the lengths (and/or widths) of behavioral simuli
			
			//prevent the default morphing behaviour by setting doMorph == false
			s.setDoMorph(false);			//
			s.setMorphLengthWidthOnly(true);
			
			//s.setMorphLim(morphLim);		// current limit set for morph line 		
			s.setMorphLengthGain(this.morphLengthGain);
			s.setMorphWidthGain(this.morphWidthGain);
			s.setMorphBias(this.morphBias);
			

			if (writeToDatabase){
				writeSpecToDatabase(s);
			}
					
			return s;
	}
	
	public BsplineObjectSpec createBehavStimFromCat(StimType type, int category, BehavioralClass behavClass, TrialType trialType) {
		return createBehavStimFromCat(type,category,behavClass,trialType,true);
	}
	//AWC July2017: making this public
	public BsplineObjectSpec createBehavStimFromCat(StimType type, int category, BehavioralClass behavClass, TrialType trialType,boolean writeToDatabase) {
		// given stim type, category, and behavioral class (sample, match), generate stim spec
		boolean jitterPosition = false;
//		boolean doRandLengths = true;
		boolean doMorphs = false;
		double morphLim = morphLineLimit;	// *** implement this ***
		// TODO:when morph lines are spec'ed out in BsplineObjectSpec, need to keep track of morph line
		//		limits for each category this will need to intereact with whatever module uses
		//		behavioral data to update the morph line limits

		
		// STIMULUS SETUP
		BsplineObjectSpec s = new BsplineObjectSpec();
		SachStimDataEntry d = new SachStimDataEntry();
		
		double xCntr = 0;	// defaults
		double yCntr = 0;
		
		if (jitterPosition) {
			xCntr += Math.random() * 10 - 5;	// for randomly jittering the position of stimuli
			yCntr += Math.random() * 10 - 5;
		}
		
		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();
		
// JKSystem.out.println("SachExptGenerator:createBehavStimFromCat() : stimObjId == " + stimObjId);

		// -- set spec values 		
		s.setStimObjId(stimObjId);
		s.setStimType(type.toString());
		s.setBehavioralClass(behavClass.toString());

		// JK 25 April 2016 
		s.setGrowthFactor(growthFactor);
		s.setFader(fader);
		s.setCategory(category, shouldUseColorCues, shouldUseSizeCues);

		s.setCategory(category);
		s.setXCenter(xCntr);
		s.setYCenter(yCntr);
		
		// JK 25 April 2016 added sizeCues so size is set in setCategory(int, bool, bool)
		// s.setSize(size);
		
		s.setAnimation(false);
		s.setDoRandom(doRandLengths);	// randomly change the lengths (and/or widths) of behavioral simuli
		s.setDoMorph(doMorphs);			// randomly choose object parameters from morph line TODO: remove this and just control w morphLim?
		s.setMorphLim(morphLim);		// current limit set for morph line 		
		
		if (writeToDatabase){
			writeSpecToDatabase(s);
		}
				
		return s;
	}
	
	public void writeSpecToDatabase(BsplineObjectSpec s){
		
		long stimObjId = s.getStimObjId();
		
		// -- need to re-create object spec after running it through the object (BsplineObject), (this saves the limbs info with the spec):
		BsplineObject obj = new BsplineObject();
		obj.setSpec(s.toXml());					 // this is where the object is actually "built" in obj.createObj()
		BsplineObjectSpec ss = obj.getSpec();
		
		// -- set data values
		SachStimDataEntry d = new SachStimDataEntry();
		d.setStimObjId(stimObjId);
		d.setTrialType(trialType.toString());
		
		// create SachStimDataEntry, then write to DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
	}
	
	
	public long generateBehStimFromCat(int category) {
		
		BsplineObjectSpec s = createBehavStimFromCat(StimType.BEH_MedialAxis,category,BehavioralClass.NA,TrialType.BEH_quick);
		return s.getStimObjId();
		
	}
	
	public long generateBlankStim() {
		
		BsplineObjectSpec s = createBehavStimFromCat(StimType.BLANK,-1,BehavioralClass.NA,TrialType.BEH_quick);
		return s.getStimObjId();
		
	}
	
	// AWC July2017, made this public
	public SachExptSpec createBehTrial(long stimObjId_A, long stimObjId_B, int targetIndex) {
			
		// TRIAL INIT 
		SachExptSpec g = new SachExptSpec(); 	// spec for each trial
		
		// ADD STIMULI TO TRIAL
		g.addStimObjId(stimObjId_A);			
		g.addStimObjId(stimObjId_B);
		g.setTargetIndex(targetIndex);			// targetIndex indicates which object is the target, if no target then it is -1 and fixation at end of trial is rewarded
		
		// TRIAL SPECS
			// for setting the target position to the 'disk' saccadic response spot
		double pos_x = 0, pos_y = 0;								// default target position
		if (targetIndex != -1) {									// map target position to the response spot, if no target then just reward fixation
			pos_x = renderer.mm2deg(DiskObject.getTx());	// set target position to the position of the response disk (see SachExptScene) in deg
			pos_y = renderer.mm2deg(DiskObject.getTy());
//			pos_y = renderer.mm2deg(75);
		}
		
		g.setTrialType(trialType.toString());						// shows whether trial is Behavioral or GA
		g.setTargetPosition(new Coordinates2D(pos_x, pos_y));		// (in degrees)
		g.setTargetEyeWinSize(targetEyeWindowSize);					// use this to change accuracy tolerance (in degrees)
		//g.setReward((long)(Math.random() * 100 + 100));			// randomize reward size
		g.setReward(rewardAmount);									// fixed reward size
		
		return g;	
	}


	// --------------------------------
	// ---- GA stimulus generation ----
	// --------------------------------
	
	public long generateBlankStim(long gen, int lineage) {
		
		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();

		BsplineObjectSpec s = new BsplineObjectSpec();
		SachStimDataEntry d = new SachStimDataEntry();
		
		// -- set spec values 		
		s.setStimObjId(stimObjId);
		s.setStimType(StimType.BLANK.toString());

		// want to re-create object spec after running it through the object (BsplineObject), (this saves the limbs info with the spec):
		BsplineObject obj = new BsplineObject();
		obj.setSpec(s.toXml());
		BsplineObjectSpec ss = obj.getSpec();

		// -- set data values
		d.setStimObjId(stimObjId);
		
		if (gen<0) 	d.setTrialType(TrialType.GAManual.toString());
		else 		d.setTrialType(TrialType.GA.toString());
		
		d.setBirthGen(gen);
		d.setLineage(lineage);
		
		// create stimObjId and add it to this and SachStimDataEntry, then write them to the DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
		
		return stimObjId;
		
	}
	
	public long generateRandGAStim(long gen, int lineage) {
		
		// STIMULUS SETUP
		BsplineObjectSpec s = new BsplineObjectSpec();
		SachStimDataEntry d = new SachStimDataEntry();

		double xCntr = 0;											// defaults
		double yCntr = 0;										
		float size = 4;										

		xCntr += Math.random() * 10 - 5;							// for randomly jittering the position of stimuli
		yCntr += Math.random() * 10 - 5;

		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();

		// -- set spec values 		
		s.setStimObjId(stimObjId);
		s.setStimType(StimType.GA.toString());
		s.setXCenter(xCntr);
		s.setYCenter(yCntr);
		s.setSize(size);
		s.setAnimation(false);
		s.setDoMorph(false);			// randomly choose object parameters from morph line

		// want to re-create object spec after running it through the object (BsplineObject), (this saves the limbs info with the spec):
		BsplineObject obj = new BsplineObject();
		obj.setSpec(s.toXml());
		BsplineObjectSpec ss = obj.getSpec();

		// -- set data values
		d.setStimObjId(stimObjId);
		d.setTrialType(TrialType.GA.toString());
		d.setBirthGen(gen);
		d.setLineage(lineage);
		
		// create stimObjId and add it to this and SachStimDataEntry, then write them to the DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
		
		return stimObjId;
	}

	public long generateMorphStim(long gen, int lineage, long parentId) {
		
		// PARENT STIM
		BsplineObjectSpec s = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(parentId).getSpec());
				
		// turn on morphing
		s.setDoMorph(true);

		// GENERATE STIM	
		BsplineObject obj = new BsplineObject();
		obj.setSpec(s.toXml());
		
		// -- get spec and set values 		
		BsplineObjectSpec ss = obj.getSpec();
		long stimObjId = globalTimeUtil.currentTimeMicros();
		ss.setStimObjId(stimObjId);				// set new id
		ss.setDoMorph(false);					// turn off morphing (or it will be re-morphed when drawing)

		// -- set data values
		SachStimDataEntry d = new SachStimDataEntry();
		d.setStimObjId(stimObjId);
		d.setTrialType(TrialType.GA.toString());
		d.setBirthGen(gen);
		d.setLineage(lineage);
		d.setParentId(parentId);
		
		// create stimObjId and add it to this and SachStimDataEntry, then write them to the DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
		
		return stimObjId;
	}
	
	public SachExptSpec generateGATrial(List<Long> stimObjIds, String trialType) {
		
		// TRIAL SETUP 
		SachExptSpec g = new SachExptSpec(); 	// spec for each trial
		int numObjects = stimObjIds.size();		// number of objects in this trial
		
		// ADD STIMULI TO TRIAL
		for (int n=0;n<numObjects;n++) {
			g.addStimObjId(stimObjIds.get(n));
		}
				
		// TRIAL SPECS
		g.setTrialType(trialType);						// shows whether trial is Behavioral or GA
		g.setTargetPosition(new Coordinates2D(0, 0));	// 'target position' is same as fixation when no target (in degrees) -- (maybe load these values directly in case fixation changes?)
		g.setTargetEyeWinSize(targetEyeWindowSize);			// use this to change accuracy tolerance (in degrees)
		g.setReward(rewardAmount);						// fixed reward size
		g.setTargetIndex(-1);							// targetIndex indicates which object is the target, if no target then it is -1 and fixation at end of trial is rewarded
				
		return g;
	}

	public long writeStimObjIdFromSpec(BsplineObjectSpec spec,String type) {
		StimType st;
		TrialType tt;
		if (type=="BEH_morph"){
			st = StimType.BEH_MedialAxis;
			tt = TrialType.BEH_morph;
		}
		else { //(type=="GAManual"){
			st = StimType.GAManual;
			tt = TrialType.GAManual;
		}
		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();

		SachStimDataEntry d = new SachStimDataEntry();
		
		// -- set spec values 		
		spec.setStimObjId(stimObjId);
		spec.setStimType(st.toString());

		// want to re-create object spec after running it through the object (BsplineObject), (this saves the limbs info with the spec):
		BsplineObject obj = new BsplineObject();
		obj.setCantFail(true);
		obj.setSpec(spec.toXml());
		BsplineObjectSpec ss = obj.getSpec();

		// -- set data values
		d.setStimObjId(stimObjId);
		
		d.setTrialType(type);
		d.setTrialType(tt.toString());
		
		d.setBirthGen(-1);
		d.setLineage(-1);
		
		// create stimObjId and add it to this and SachStimDataEntry, then write them to the DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
		
		return stimObjId;
		
	}
	public long generateGAManualObjId(BsplineObjectSpec spec) {
		//NOW OBSOLETE USE writeStimObjIdFromSpec ABOVE...
		// GENERATE STIM	
		long stimObjId = globalTimeUtil.currentTimeMicros();

		SachStimDataEntry d = new SachStimDataEntry();
		
		// -- set spec values 		
		spec.setStimObjId(stimObjId);
		spec.setStimType(StimType.GAManual.toString());

		// want to re-create object spec after running it through the object (BsplineObject), (this saves the limbs info with the spec):
		BsplineObject obj = new BsplineObject();
		obj.setCantFail(true);
		obj.setSpec(spec.toXml());
		BsplineObjectSpec ss = obj.getSpec();

		// -- set data values
		d.setStimObjId(stimObjId);
		
		d.setTrialType(TrialType.GAManual.toString());
		
		d.setBirthGen(-1);
		d.setLineage(-1);
		
		// create stimObjId and add it to this and SachStimDataEntry, then write them to the DB
		writeStimObjData(stimObjId, ss.toXml(), d.toXml());
		
		return stimObjId;
		
	}
	
	public void writeStimObjData(long stimObjId, String spec, String data){
		dbUtil.writeLine("StimObjData",new String[] {"id", "spec", "data"}, new Object[]{stimObjId, SachDbUtil.addQ(spec), SachDbUtil.addQ(data)});
	}
	
	// ------------------------
	// ---- output methods ----
	// ------------------------
	
	public String generateBehTrialSpec(long stimObjId_A, long stimObjId_B, int targetIndex) {
		return this.createBehTrial(stimObjId_A,stimObjId_B,targetIndex).toXml();
	}
	
	public String generateGATrialSpec(List<Long> stimObjIds) {
		return this.generateGATrial(stimObjIds,TrialType.GA.toString()).toXml();
	}

	public String generateGAManualTrialSpec(List<Long> stimObjIds) {
		return this.generateGATrial(stimObjIds,TrialType.GAManual.toString()).toXml();
	}
	
	public String generateBEHQuickTrialSpec(List<Long> stimObjIds) {
		return this.generateGATrial(stimObjIds,TrialType.BEH_quick.toString()).toXml();
	}
	
	// -----------------------------
	// ---- setters and getters ----
	// -----------------------------

	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long id) {
		taskId = id;
	}

	public double getMorphLineLimit() {
		return morphLineLimit;
	}
	public void setMorphLineLimit(double morphLineLimit) {
		this.morphLineLimit = morphLineLimit;
	}

	public SachDbUtil getDbUtil() {
		return dbUtil;
	}
	public void setDbUtil(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public TimeUtil getGlobalTimeUtil() {
		return globalTimeUtil;
	}
	public void setGlobalTimeUtil(TimeUtil globalTimeUtil) {
		this.globalTimeUtil = globalTimeUtil;
	}

	public AbstractRenderer getRenderer() {
		return renderer;
	}
	public void setRenderer(AbstractRenderer renderer) {
		this.renderer = renderer;
	}

	public TrialType getTrialType() {
		return trialType;
	}
	public void setTrialType(TrialType trialType) {
		this.trialType = trialType;
	}

	// JK 20 April 2016
	//  refresh system variables from the database
	public void updateTrainingVariables(){
		Map<String, SystemVariable> colorMap =dbUtil.readSystemVar("%training_%");
		Float temp = Float.parseFloat (colorMap.get("xper_training_use_color_cues").getValue(0));
		
		if(temp > 0.5f) {
			shouldUseColorCues = true;
		} else {
			 shouldUseColorCues =  false;
			 this.rgbaVec = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		}
		
		if(shouldUseColorCues){		
			System.out.println(" using color ");
			Float r = Float.parseFloat( colorMap.get("xper_training_red_color").getValue(0));
			Float g = Float.parseFloat( colorMap.get("xper_training_green_color").getValue(0));
			Float b = Float.parseFloat( colorMap.get("xper_training_blue_color").getValue(0));
			Float a = Float.parseFloat( colorMap.get("xper_training_alpha_color").getValue(0));
			
			fader = Double.parseDouble(colorMap.get("xper_training_color_fader").getValue(0));

			this.rgbaVec = new float[] {r.floatValue(), g.floatValue(), b.floatValue(), a.floatValue()};
		}
		
		temp = Float.parseFloat(colorMap.get("xper_training_use_size_cues").getValue(0));
		
		shouldUseSizeCues =   Boolean.parseBoolean(colorMap.get("xper_training_use_size_cues").getValue(0));
		if(temp > 0.5f) {
			shouldUseSizeCues = true;
		} else {
			shouldUseSizeCues =  false;
			 this.growthFactor = 1.0f; 
		}
		
		if(shouldUseSizeCues){		
			growthFactor = Float.parseFloat( colorMap.get("xper_training_growth_factor").getValue(0));
			//System.out.println(" using size cue, growth factor == " + growthFactor);
		}
		
		// JK 6 Oct 2016
		// retrieve morphParams from dbase
		Map<String, SystemVariable> morphMap = dbUtil.readSystemVar("xper_morph%");
		morphLengthGain = Double.parseDouble(morphMap.get("xper_morph_length_gain").getValue(0));
		morphWidthGain = Double.parseDouble(morphMap.get("xper_morph_width_gain").getValue(0));
		morphBias = Double.parseDouble(morphMap.get("xper_morph_bias").getValue(0));
		System.out.println("ExptSpecGen generateBehTrial_training() : morph stuff : " + morphLengthGain + ", " + morphWidthGain + ", " + morphBias);
		
		
	}


	// JK 8 August 2016
	public void fillOccluderSpecMap() {
		
		occluderSpecMap = dbUtil.getOccludedSeriesSpecMap();
		Map<String, SystemVariable> morphMap = dbUtil.readSystemVar("%morph%");
		morphLengthGain = Double.parseDouble(morphMap.get("xper_morph_length_gain").getValue(0));
		morphBias = Double.parseDouble(morphMap.get("xper_morph_bias").getValue(0));
		
		//System.out.println("fillOccluderMap() : morphBias = " + morphBias + ", morphGain = " + morphGain);
		
		
		 
	}
	
}
