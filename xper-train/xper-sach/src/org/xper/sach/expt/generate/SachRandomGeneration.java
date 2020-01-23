package org.xper.sach.expt.generate;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jfree.ui.RefineryUtilities;
import org.xper.Dependency;
import org.xper.acq.counter.MarkEveryStepTaskSpikeDataEntry;
import org.xper.db.vo.StimSpecEntry;
import org.xper.db.vo.SystemVariable;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.exception.InvalidAcqDataException;
import org.xper.exception.NoMoreAcqDataException;
import org.xper.exception.VariableNotFoundException;
import org.xper.sach.acq.counter.SachMarkEveryStepExptSpikeCounter;
import org.xper.sach.analysis.BehCatPrefPlot_2;
import org.xper.sach.analysis.GAGenAnalysisPlot_2;
import org.xper.sach.analysis.PNGmaker;
import org.xper.sach.analysis.SachStimDataEntry;
import org.xper.sach.drawing.StimTestWindow;
import org.xper.sach.drawing.stimuli.BsplineObject;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.drawing.stimuli.LimbSpec;
import org.xper.sach.expt.SachExptSpec;
import org.xper.sach.expt.SachExptSpecGenerator;
import org.xper.sach.expt.SachExptSpecGenerator.BehavioralClass;
import org.xper.sach.expt.SachExptSpecGenerator.StimType;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachIOUtil;
import org.xper.sach.util.SachMapUtil;
import org.xper.sach.util.SachMathUtil;
import org.xper.sach.vo.SachExpLogMessage;
import org.xper.time.TimeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SachRandomGeneration {
	@Dependency
	SachDbUtil dbUtil;
	@Dependency
	TimeUtil globalTimeUtil;
	@Dependency
	AbstractRenderer renderer;			
	@Dependency
	SachExptSpecGenerator generator;	
	@Dependency
	int taskCount;
	
	static public enum Monkey {Gizmo, Shaggy};	//
	Monkey activeMonkey = Monkey.Gizmo;
//	Monkey activeMonkey = Monkey.Shaggy;
	
	
	GAGenAnalysisPlot_2 plotGA;
	
	boolean debugFlag = false;
	
	
	// ---- global variables:
	boolean useFakeSpikes = false;						// (for debugging)
	boolean realExp = false;							// flag for later analysis
	boolean saveThumbnails;								// do you want to save stim thumbnails?
	
	PNGmaker pngMaker;									// set of methods for creating and saving thumbnails
	
	long genId = -1;									// tracks global genId
	long thisGenId = 1;									// track generations locally, maybe also write the generation numbers to the output file?
	long firstGenId = -1;
	
	// ---- global variables for Beh:
	static public enum TrialType { BEH_search, BEH_quick, BEH_morph, BEH_train, BEH_exptRepeat,GA, GAManual, BEH_occluded};	// trial types
	TrialType trialType;
	
	
	// ---- global variables for GA:
	static int GA_maxNumGens = 40;									// maximum # of generations to run
	static int GA_numNonBlankStimsPerGen = 40; 				// # non-blank simuli per generation per lineage
	static int GA_numStimsPerGen = GA_numNonBlankStimsPerGen + 1;	// # random shapes (or offspring) + 1 blank, per lineage
	static int GA_numRepsPerStim = 5;//5;							// # repetitions of each stimulus
	static int GA_numStimsPerTrial = 8; 							// # stimuli per trial
	static int GA_numLineages = 2;									// # separate GA lineages
//	static int GA_numTrials = (int)Math.ceil((double)GA_numStimsPerGen*GA_numLineages*GA_numRepsPerStim/GA_numStimsPerTrial);
	
	static double GA_fracNewRandObjs = 0.3;
	static double GA_fracRespThreshMediumRespStims = 0.5; 
	static double GA_fracRespThreshLowRespStims = 0.05;

	static double[] GA_percDivs = {0.3,0.5,0.7,0.9,1.0}; 			// percentile divisions: [0-.3), [.3-.5), [.5-.7), [.7-.9), [.9-1.0] 
	static double[] GA_fracPerPercDiv = {0.1,0.15,0.2,0.2,0.35};	// probility per percentile division
	
//	int numGensBeforeEliteSelect = 2; 					// If this is 2 then we must have already done 2 generations before we can pick our elite shape (to be presented in the 3rd generation and onwards).
//	int maxNumOfMutationsToApply = 2; 					// This is the maximum number of possible mutations (e.g. size, azimuth, anchorX...) we can apply to a given parent shape.
//	int minNumOfMutationsToApply = 1;
//	boolean adaptNumOfMutationsWithResp = true; 		// If false a uniform distribution is used for number of mutations. If true then smaller number of mutations are more likely for higher response shapes.
//	int genIndxToStartAdapt = numGensBeforeEliteSelect; 	// It makes sense for this to be the same as numGensBeforeEliteSelct. If adaptNumOfMutationsWithResp is true then genIndxToStartAdapt determines after how many generations (e.g if genIndxToStartAdapt = 3 then after completion of 3 gens we begin, that is when we want to create the fourth generation), do we start deviating away from a uniform  probability selection of the number of mutations towards one that starts to favor lower number of mutations for higher response shapes.
//	double adaptationProportionality = 2.0; 			// Stimuli located at the highest rank will receive a mutation number that is adaptationProportionality more likely to be minNumOfMutationsToApply than maxNumOfMutationsToApply.
//	int numberTriesPerGAMutation = 50; 					// Number of attempts before trying a new set of mutations.
	
	
	// Each task ID (taskId) is associated with a unique trial ID (stimSpecId), which is composed of multiple stimulus object IDs (stimObjIds) that reference object specs in the stimObjData db table
	Map<Long, Long> TaskId2TrialId = new TreeMap<Long, Long>();
	Map<Long, Long[]> TrialId2StimObjIds = new TreeMap<Long, Long[]>();
	Map<Long, BsplineObjectSpec> StimObjId2ObjSpec = new TreeMap<Long, BsplineObjectSpec>();
	
	// track stimulus objects created:
	List<Long> allStimObjIds = new ArrayList<Long>();		// all non-blank stim objs created
	List<Long> allBlankStimObjIds = new ArrayList<Long>();	// all blank stim objs created

	// ------------------------------------
	// ---- Behavioral task generation ----
	// ------------------------------------
	
	public void generateBeh() {	// use this for behavioral trials
		System.out.print("Generating Behavioral trials... ");	
			
		long seed = 0L ;//
		// *** want a few different ways of generating behavioral stims:
		// 1. quickly characterize responses across canonical behavioral stim categories (to find
		//    "preferred" category)
		// 2. advance morph lines for sets of 4 stims (0,1,4,5) or (2,3,6,7)
		// 3. training regime (full training, with or without morph line advancement?)
				
		// ---- ask which type of behavioral run this is:
//	
		long prevGenId = -1;
		String prevGenInp;
		char c = SachIOUtil.prompt("Which Behavioral task is this?" + 
				"\n  (q) formal presentation of trained-untrained, and normal-upsideDown stimuli" +
			 	"\n  (s) screening/search presentation of trained stimuli" +
				"\n  (b) present morphed beh stimuli (rsd and usd)" +
				"\n  (t) training run" + 					// then ask which morph line level (0 to 1) ? for later?
			 	"\n  (e) experimental run; repeated morphs" +
				"\n  (m) Manual GA" +
				"\n  (o) occluded trials" + 					// then ask which morph line level (0 to 1) ? for later?
				"\n  (g) start GA" +
				"\n  (c) load/continue GA" +
//				"\n  (r) regenerate using a specific random seed " +
				"\n  (p) simply stop previous experiment" + 
				"\n  (r) get spike responses of a previous genId" +
				"\n" );
				
		boolean startGAFlag = false;
		switch (c) {
		case 'q': // quick characterization for finding "preferred" category
			trialType = TrialType.BEH_quick;
			realExp = true;
			break;
		case 's': // search for tuned cells
			trialType = TrialType.BEH_search;
			realExp = false;
			break;
		case 'b': // morphed BEH shapes
			trialType = TrialType.BEH_morph;
			realExp = true;
			break;
		case 't': // training run
			trialType = TrialType.BEH_train;
			realExp = false;
			break;		
		case 'e': // blocked experimental run
			trialType = TrialType.BEH_exptRepeat;
			realExp = true;
			break;
		case 'm': // post-manual GA
			trialType = TrialType.GAManual;
			realExp = true;
//			prevGenInp = SachIOUtil.promptString("previous genId (leave empty for most recent GA genId) ");
//			if (!prevGenInp.isEmpty()) 
//				prevGenId = Long.parseLong(prevGenInp);
			break;
		case 'g': // start GA
			trialType = TrialType.GA;
			realExp = true;
			startGAFlag = true;
			break;
		case 'c': // load-continue GA
			trialType = TrialType.GA;
			realExp = true;
//			prevGenInp = SachIOUtil.promptString("previous genId (leave empty for most recent GA genId) ");
//			if (!prevGenInp.isEmpty()) prevGenId = Long.parseLong(prevGenInp);
			break;
		case 'o': // occluded trials
			trialType = TrialType.BEH_occluded;
			realExp = false;
			break;
//		case 'r': // recreate a set of stimuli
//			break;
		case 'p': //simply stop previous expt;
			stopPrevExp();
			return;
		case 'r': //run getSpikeResponse for a prev genId
			stopPrevExp();
			prevGenInp = SachIOUtil.promptString("previous genId (leave empty for most recent) ");
			if (!prevGenInp.isEmpty()) prevGenId = Long.parseLong(prevGenInp);
			else prevGenId = dbUtil.readLong("ExpLog","globalGenId",new String[]{"status,'STOP',odl"});
			
			BehCatPrefPlot_2 plot = new BehCatPrefPlot_2(prevGenId,dbUtil);
			plot.run_pack_vis();
			return;
		default: 
			System.out.println("WARNING: '" + c + "' is not a valid entry. Exiting.");
			return;
		}
		
		if (c=='q') saveThumbnails = false;  //hardcoded: default should be false. should only need to be turned "true" once. 
		else 		saveThumbnails = realExp;

		getGenId();		// get genId from db
		
		// steps to recreate/reuse a previous generation:
		//   1 - get task list based on genId from TaskToDo
		//   2 - use taskId to get stimspec
//		if(c == 'r'){
//			prevGenId = SachIOUtil.promptInt("Enter genID to recreate");
//			int startTrial = -1;
//			int stopTrial = -1;		
//			recreateGeneration(prevGenId, startTrial, stopTrial);
//			return;
//		}
		
		seed = globalTimeUtil.currentTimeMicros();   // 123456789;  // 
		SachMathUtil.setSeed(seed);
		
		// AWC August 2017************************************
//		ComboPooledDataSource source = new ComboPooledDataSource();
//		try {
//			source.setDriverClass("com.mysql.jdbc.Driver");
//		} catch (PropertyVetoException e) {
//			throw new DbException(e);
//		}
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/shaggy_ecpc48_2016_07");
//		source.setUser("xper_rw");
//		source.setPassword("up2nite");
//		dbUtil.setDataSource2(source);
		//*****************************
		
		
		dbUtil.writeSystemVar("xper_random_seed", 0, Long.toString(seed),  globalTimeUtil.currentTimeMicros());
//		System.out.println("Generating Behavioral trials... (random seed stored) " + seed);	
		
		// set trialType in generator:
		//generator.setTrialType(trialType);
		
		if (c!='m')
			stopPrevExp();
		
		// write start time to ExpLog 
//		writeExptStart();

		boolean externalStop = false;
		int stopType = 1; //-1 externalStop, 0 normal stop/no continuation, 1 continue to another expt (beh_quick to GA)
		while (stopType==1){
			generator.setTrialType(trialType);
			stopType = 0;
			// -- run specified behavioral trial type:
			switch (trialType) {
			case BEH_quick:	// quick characterization for finding "preferred" category
				
				//		 * should quick characterization occur as the GA (fixation), or in beh context?
				// just cycle through enough of the stimulus categories to identify a preference
				// issue: show ala GA stims (fixation only) or as behavioral runs (would need more stims because of "random" pairings)?
				// if behav: could potentially just have 1st stim be the appropriate one and randomize 2nd stims?
				System.out.println("\n--- WARNING: make sure that you are using GAConsole and GAExperiment ---\n");	
					//TODO: fix this somehow so you can just use one console/expt?
				
	//			externalStop = generateBehTrials_quick(true);
				stopType = generateBehTrials_quick();
				break;
				
			case BEH_search:
				
				System.out.println("\n--- WARNING: make sure that you are using GAConsole and GAExperiment ---\n");		
				
				// TODO: if adding more search methods, ask which type and run here:
	//			externalStop = generateBehTrials_quick(false);
				stopType = generateBehTrials_quick();
				break;
				
			case BEH_morph:
				System.out.println("hi");
				System.out.println("\n--- WARNING: make sure that you are using GAConsole and GAExperiment ---\n");		
				stopType = generateBehTrials_quickMorph();
				break;
				
			case BEH_train:	// training run
				stopType = generateBehTrials_train();
				break;
			case BEH_exptRepeat: //blocked experimental run
				externalStop = generateBehTrials_repeated();
				break;
			case GAManual :
				externalStop = postManualGARun(prevGenId);
				break;
			case GA :
				if (startGAFlag)
					externalStop = generateGA();
				else
					externalStop = loadContinueGARun();
				break;
			case BEH_occluded:	// occluded protocol
				
				generateOccludedTrials();
				break;	
			}		
			
			if (externalStop) 
				stopType=-1;
			
			// ---- end Beh run		
			if (stopType==-1)	
				writeExptStop();
//			else if (stopType==1) //Continuation
//				if (trialType==TrialType.GA)
//					generateGA();
//				else if (trialType==TrialType.BEH_exptRepeat)
//					generateBehTrials_repeated();
		}
		

	}
	
	// ---------------------------------------------
	// ---- BehavioralStimuli flash presentation----
	// ---------------------------------------------
	
	int generateBehTrials_quickMorph(){
		// GENERATE BEH_QUICK TRIALS WITH MORPHED STIMULI

		//generator.setTrialType(trialType);
		BehCatPrefPlot_2 plot = new BehCatPrefPlot_2(genId,dbUtil);
		
		int[] dispCats = new int[]{	0 , 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,
                					16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
		int stopType = 0;
		int screenType = -1;
		
		int numMorphs = 5;
		
		List<Long> stimObjIds = new ArrayList<Long>();	// track stimObjIds for all stimuli created
		// create category stimuli:
		
		generator.updateTrainingVariables();
		for (int cat : dispCats) {
			for (int nm=0;nm<numMorphs;nm++){
				BsplineObjectSpec newSpec = generator.createMorphedBehavStimFromCat(StimType.BEH_MedialAxis, cat);
				stimObjIds.add(newSpec.getStimObjId());
			}
		}
		
		if (!dbUtil.readString("ExpLog","status").equals("START"))
			writeExptStart();
		
		if (saveThumbnails) {
			List<Long> stimObjIdsSave = new ArrayList<Long>();
			stimObjIdsSave.addAll(stimObjIds);

			String cellFolder = null;
			long cellNum = readActiveCellNum();
			if (cellNum==-1) cellFolder = "noCell";
			else cellFolder = "cell" + cellNum;
			
			String savePath = System.getProperty("user.dir") + "/images/" + cellFolder + "/Behavioral/genId" + genId + "/"; 
			myMkDir(savePath);
			pngMaker.MakeFromIds(stimObjIdsSave,savePath);
		}
		
		stimObjIds.add(generator.generateBlankStim());
		
		if (realExp)	dbUtil.writeStimObjIdsForEachGenId(genId, stimObjIds);
		
		List<Long> stimObjIds_all = new ArrayList<Long>();
		int numRepeats = 5;
		for (int n=0;n<numRepeats;n++) {
			stimObjIds_all.addAll(stimObjIds);
		}

		// shuffle stimuli:
		Collections.shuffle(stimObjIds_all, SachMathUtil.rand);
		
		long taskId;
		int stimCounter = 0;
		int numStims = stimObjIds_all.size();
		int numTrials = (int)Math.ceil((double)numStims/GA_numStimsPerTrial);


		for (int n=0;n<numTrials;n++) {
			taskId = globalTimeUtil.currentTimeMicros();

			// create trialspec using sublist and taskId
			int endIdx = stimCounter + GA_numStimsPerTrial;
			while (endIdx>numStims) endIdx--;	// this makes sure there's no index out of bounds exception

			String spec = generator.generateBEHQuickTrialSpec(stimObjIds_all.subList(stimCounter,endIdx));

			// save spec and tasktodo to db
			dbUtil.writeStimSpec(taskId, spec);
			dbUtil.writeTaskToDo(taskId, taskId, -1, genId);
	
			stimCounter = endIdx;
		}

		// write updated global genId and number of trials in this generation to db:
		dbUtil.updateReadyGenerationInfo(genId, numTrials);
		
		// get acq info, put spike resp in db
		boolean externalStop = getSpikeResponses(genId);
		
		if (externalStop) stopType = -1;
		
		// plot
		if (!externalStop){
			plot.run_pack_vis();
		}
		
		char cont = SachIOUtil.prompt("move to GA? (g or n) ");

		writeExptStop();
		if (cont=='g'){
			//stop beh_quick
			
			//change to GA
			trialType = TrialType.GA;
			stopType = 1;
		}
		return stopType;
	}
	int generateBehTrials_quick(){
		// GENERATE BEH_QUICK TRIALS (these will be GA style, not matching trials):
		
		//generator.setTrialType(trialType);
		BehCatPrefPlot_2 plot = new BehCatPrefPlot_2(genId,dbUtil);
		
		int[] dispCats;
		int numRepeats = -1;
		int stopType = 0; //-1 externalStop, 0 normal stop/no continuation, 1 continue to GA 
		int screenType = -1;
		
		boolean done = false;
		while (!done){
			generator.setTrialType(trialType);
			if (realExp){  //realExp=true ==> behQuick
				if (screenType==-1){
					String screenTypeInp = SachIOUtil.promptString("enter screening type...(1/empty) trained/untrained,  (2) upside-down,  (3) all letters ");
					if (screenTypeInp.equals("")) 
						screenType = 1;
					else 						  
						screenType = Integer.parseInt(screenTypeInp);
				}
				
				if (screenType==1){
					System.out.println("screenType: regular trained/untrained letters");
					dispCats   = new int[]{0 , 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15};
				}
				else if (screenType==2){
					System.out.println("screenType: UPSIDE-DOWN trained/untrained letters");
					dispCats   = new int[]{16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
				}
				else{
					System.out.println("screenType: ALL LETTERS (trained/untrained, regular/upsidedown)");
					dispCats   = new int[]{0 , 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,
						                   16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
				}
			
				if (!debugFlag)
					numRepeats = 15;
				else
					numRepeats = 2;
			}
			else {	//realExp=false ==> behSearch
				if(activeMonkey == Monkey.Gizmo) {
					dispCats = new int[]{0,1,2,3,4,5,6,7};		
				} else {  //Shaggy
					dispCats = new int[]{8,9,10,11,12,13,14,15};
				}
				if (numRepeats==-1){
					String numInp = SachIOUtil.promptString("How many repeats do you want to run? ('empty' for default 50) ");
					if (numInp.equals("")) 	
						if (!debugFlag)
							numRepeats = 50;
						else
							numRepeats = 1;
					else 					
						numRepeats = Integer.parseInt(numInp);
				}
			}
				
			if (!dbUtil.readString("ExpLog","status").equals("START"))
				writeExptStart();
			
			List<Long> stimObjIds = new ArrayList<Long>();	// track stimObjIds for all stimuli created
	
			// create category stimuli:
			for (int cat : dispCats) {
				stimObjIds.add(generator.generateBehStimFromCat(cat));
			}
	
			// create PNG thumbnails
			if (saveThumbnails) {
				// now make pngs from the stimObjIds:
				List<Long> cat = new ArrayList<Long>();
				for (Long cc=0L;cc<32L;cc++) cat.add(cc);
				
				pngMaker.MakeFromIds(cat,System.getProperty("user.dir") + "/images/Canonical/");
			}
			// create a blank stim:
			stimObjIds.add(generator.generateBlankStim());
			
			if (realExp)	dbUtil.writeStimObjIdsForEachGenId(genId, stimObjIds);
			
			// stim repetitions:
			List<Long> stimObjIds_all = new ArrayList<Long>();
			for (int n=0;n<numRepeats;n++) {
				stimObjIds_all.addAll(stimObjIds);
			}
	
			// shuffle stimuli:
			Collections.shuffle(stimObjIds_all, SachMathUtil.rand);
			
			// create trials using shuffled stimuli:
			long taskId;
			int stimCounter = 0;
			int numStims = stimObjIds_all.size();
			int numTrials = (int)Math.ceil((double)numStims/GA_numStimsPerTrial);
	
	
			for (int n=0;n<numTrials;n++) {
				taskId = globalTimeUtil.currentTimeMicros();
	
				// create trialspec using sublist and taskId
				int endIdx = stimCounter + GA_numStimsPerTrial;
				while (endIdx>numStims) endIdx--;	// this makes sure there's no index out of bounds exception
	
				String spec = generator.generateBEHQuickTrialSpec(stimObjIds_all.subList(stimCounter,endIdx));
	
				// save spec and tasktodo to db
				dbUtil.writeStimSpec(taskId, spec);
				dbUtil.writeTaskToDo(taskId, taskId, -1, genId);
		
				stimCounter = endIdx;
			}
	
			// write updated global genId and number of trials in this generation to db:
			dbUtil.updateReadyGenerationInfo(genId, numTrials);
		
			
			//POST-RUN cleanup/preparation for next run 
			
			if (!realExp) { 
				//*********************************************** Beh_Search (move onto Beh_Quick?)**********************************
				//option to repeat search or move to screening
				char repeat = SachIOUtil.prompt("(r) to repeat seach, (q) to start initial screening, or (t) to terminate");
				done = (repeat=='t');
				
				if (repeat=='q'){
					//stop beh_search
					writeExptStop();
					
					//change to beh_quick
					realExp = true;
					genId++;
					trialType = TrialType.BEH_quick;
					plot.setGenIds(genId);
				}
				
			}
			else {
				//*********************************************** Beh_Quick********************************************************
				// get acq info, put spike resp in db
				boolean externalStop = getSpikeResponses(genId);
				
				if (externalStop) stopType = -1;
				
				// plot
				if (!externalStop){
					plot.run_pack_vis();
				}
				
				char cont = 't';
				//option to continue screening
				if (screenType==1){ 	
					//						*************************** Finished cats 0-15 only (repeat? upside down? all?) ********************************
					cont = SachIOUtil.prompt("(u) continue onto upsideDown, (r) terminate & repeat q, (a) do all letters (new generation beh_quick), (t) terminate ");
					
					if (cont=='t')
						done = true;
					else if (cont=='r'){
						//stop this gen of beh_quick
						writeExptStop();
						
						//start a new one
						genId++;
						plot.setGenIds(genId);
					}
					else if (cont=='u' || cont=='a'){
						SachIOUtil.promptString("start a new cellRecording via matlab...then press enter here");
						if (readActiveCellNum()!=-1)
							dbUtil.updateLine("ExpLog","cellNum",readActiveCellNum(),new String[]{});

						if (cont=='u')
							screenType=2;
						else{
							writeExptStop();
							screenType=3;
							genId++;
							plot.setGenIds(genId);
						}
					}
					
				}
				else{
					// 					******************************** Finished cats upsideDown or all stims (move onto GA?) ********************************
					cont = SachIOUtil.prompt("move to GA or behMorphs? (g, b, or n) ");
					done = true;

					if (cont=='g'){
						//stop beh_quick
						writeExptStop();
						
						//change to GA
						trialType = TrialType.GA;
						stopType = 1;
					}
					else if (cont=='b'){
						//stop beh_quick
						writeExptStop();
						genId++;
						//change to BEH_morph
						saveThumbnails = realExp;
						trialType = TrialType.BEH_morph;
						stopType = 1;
					}
				}
			}
		}
		return stopType;
	}
	
	
	// ---------------------------------------------
	// ---- BehavioralTask: training---------------
	// ---------------------------------------------
	int generateBehTrials_train() {
	
		int stopType = 0;
		int[] nonMatchCats = null;
		int[] match_HalfMatch_Cats = null;
		Coordinates2D currentTargetLocation = null;
		
		if(activeMonkey == Monkey.Gizmo) {
			nonMatchCats   = new int[]{0,1,2,3,4,5,6,7};
//			match_HalfMatch_Cats = new int[]{2,3,6,7};		
			match_HalfMatch_Cats = new int[]{0,1,2,3,4,5,6,7};		
			currentTargetLocation = new Coordinates2D(renderer.mm2deg(0), renderer.mm2deg(75));
		} else if(activeMonkey == Monkey.Shaggy) {
			nonMatchCats = new int[]{8,9,10,11,12,13,14,15};
			match_HalfMatch_Cats = new int[]{8,9,10,11,12,13,14,15};
			//********************************************************
			
			
			currentTargetLocation = new Coordinates2D(renderer.mm2deg(0), renderer.mm2deg(100));
		}
		
		
		double percMatches = 1;							// % of trials that are matches
		double percentHalfCommon = 0;   						// probability of half common 
		int numTasks;
		int numRandTasks = 400;								// total number of trials to create, for use when drawing stimuli randomly
//		int minRepeats = 5;									// minimum amount of repeats for non-randomly generated cat-cat pairs
		
		int repsPerBlock = 1;
		
		//---------------------------
		

		// JK 10 May 2016 added random size
		// JK 20 April 2016  
		//  retrieve percent match param from database
		boolean shouldUseSizeCues = false;
		Float lowerBound = 4.0f;
		Float upperBound = 4.0f;
		
		
		boolean done = false;
		while (!done){
			Map<String, SystemVariable> valMap = dbUtil.readSystemVar("%training_%");
			try{
				percMatches = Double.parseDouble(valMap.get("xper_training_percent_match").getValue(0));
				percentHalfCommon = Double.parseDouble(valMap.get("xper_training_percent_match_half_common").getValue(0));		
				Float temp = Float.parseFloat(valMap.get("xper_training_use_size_cues").getValue(0));
				
				
				if(temp > 0.5f) {
					shouldUseSizeCues = true;
				} else {
					shouldUseSizeCues =  false;
					 
				}
				
				if(shouldUseSizeCues){					
					lowerBound = Float.parseFloat( valMap.get("xper_training_size_cue_lower_bound").getValue(0));
					System.out.println(" using size : lowerBound == " + lowerBound);
					upperBound = Float.parseFloat( valMap.get("xper_training_size_cue_upper_bound").getValue(0));
					System.out.println(" using size : upperBound == " + upperBound);				
				}
				
			} catch (NullPointerException npe) {
				System.out.println("xper_training_* was not found in the database! " );
			}
			
			// JK update the training variables in the generator
			generator.updateTrainingVariables();
			BsplineObjectSpec.setSizeGrowthParameters(3, lowerBound, upperBound);
					
			
			// -- GENERATE BEH TRIALS:
			List<SachExptSpec> behTrials =new ArrayList<SachExptSpec>();		
				
			String numTaskStr = SachIOUtil.promptString("How many UNIQUE trials do you want to run? (empty for default 100)");
			if (numTaskStr.isEmpty())
				numRandTasks = 100;
			else
				numRandTasks = Integer.parseInt(numTaskStr);

			if (!dbUtil.readString("ExpLog","status").equals("START"))
				writeExptStart();
			
			for (int n=0;n<numRandTasks;n++) {
				SachExptSpec spec = generator.generateBehTrial_training(nonMatchCats, match_HalfMatch_Cats, percMatches, percentHalfCommon);
				spec.setTargetPosition(currentTargetLocation);
				behTrials.add(spec);		
			}
			numTasks = numRandTasks;
				
				
			// JK negative repsPerBlock will create random block sizes (with a max size of repsPerBlock)
			if(repsPerBlock != 0){
				addBlocksOfBehTrials(behTrials, repsPerBlock);
			} else {
				// -- shuffle and add trials:
				shuffleAndAddBehTrials(behTrials);
			}
			
			
			System.out.println("xper_training_percent_match == " + percMatches);
	
	
			// write updated global genId and number of trials in this generation to db:
			dbUtil.updateReadyGenerationInfo(genId, numTasks);
			
			// ****************************************** 
			
			char repeat = SachIOUtil.prompt("(r) to repeat training, (e) to start beh_expt, or (t) to terminate");
			if (repeat=='t')
				done = true;
			else if (repeat=='e'){
				//stop beh_search
				writeExptStop();
				
				//change to beh_quick
				realExp = true;
				genId++;
				trialType = TrialType.BEH_exptRepeat;
				
				done = true;
				stopType = 1;
				
			}
			
		}
		return stopType;
	}
	
	// --------------------------------------------------------------------------
	// ---- BehavioralTask: realExperiment, repeated presentations---------------
	// --------------------------------------------------------------------------
	boolean generateBehTrials_repeated(){
		int[] allCats = null;
		Coordinates2D currentTargetLocation = null;
		double currentWindowSize = 4;
		
		if(activeMonkey == Monkey.Gizmo) {
//			allCats   = new int[]{0,1,2,3,4,5,6,7};		
			currentTargetLocation = new Coordinates2D(renderer.mm2deg(0), renderer.mm2deg(75));
			currentWindowSize = 6;
		} else if(activeMonkey == Monkey.Shaggy) {
//			allCats = new int[]{8,9,10,11,12,13,14,15};
			currentTargetLocation = new Coordinates2D(renderer.mm2deg(0), renderer.mm2deg(100));
		}
		allCats = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
		
		//Initialize trial structure parameters:
		int numCats = allCats.length;
		int numMorphsPerCat = 5;  					//3 levels of morph for each category
		int numMorphsPlusCan = numMorphsPerCat+1;	//in addition to morph, 1 canonical version
		int numStim = numCats*numMorphsPlusCan; 	//total number of unique stimuli; default is 8*(3+1) = 32
		int numRepeats = 15; 						//5 repeats for each stimulus (same as GA or beh_quick)
		int numTrials = numCats*numMorphsPerCat*numRepeats; 	//total number of trials; default is 8*3*5 = 120
		
		//variables for stimuli spec generation:
		StimType type = StimType.BEH_MedialAxis;
		BehavioralClass behClassMatch 		= BehavioralClass.MATCH;
		BehavioralClass behClassNonMatch 	= BehavioralClass.NON_MATCH;
		BehavioralClass behClassSample 		= BehavioralClass.SAMPLE;
		
		//read in percMatch values (for this protocol, NO halfMatches)
		Map<String, SystemVariable> valMap = dbUtil.readSystemVar("%training_%");
		double percMatches = Double.parseDouble(valMap.get("xper_training_percent_match").getValue(0));
		double percNonMatch = 1.0-percMatches;
		generator.updateTrainingVariables();
		
		// Initialize idx vectors
		int[] stimIdxVec = new int[numStim]; 			//vector of length "numStim" designating the category # of each stimuli
		int[] trialTestVec = new int[numTrials]; 		//vector of length "numTrials" designating the stimuli Idx of each trial TEST period (morphs only)
		int[] trialSampleVec = new int[numTrials];      //vector of length "numTrials" designating the stimuli Idx of each trial SAMPLE period (canonical only)
		Integer[] trialIdxVec = new Integer[numTrials]; //vector of length "numTrials" simply designating trial number (to be shuffled later)
		BsplineObjectSpec[] testMatchSpecVec = new BsplineObjectSpec[numStim]; 		//Initialize canonical matched stimuli vector 
		BsplineObjectSpec[] testNonMatchSpecVec = new BsplineObjectSpec[numStim];   //Initialize canonical nonMatch stimuli vector
		BsplineObjectSpec[] sampleSpecVec = new BsplineObjectSpec[numStim]; 		//Initialize morphed (sample) stimuli vector
		List<SachExptSpec> behTrials =new ArrayList<SachExptSpec>(); 	//for later...Initialize behTrial list
		List<Long> sampleObjIds = new ArrayList<Long>(); //for saving Thumbnails
		List<Long> allObjIds    = new ArrayList<Long>(); //for saving to db (GenId_to_StimObjId)
		
		List<Long> morphGenIds;
		long morphGenId = -1;
		List<BsplineObjectSpec> morphObjSpecs = new ArrayList<BsplineObjectSpec>();
		Integer[] morphObjCats  = new Integer[161];;
		long cellNum = readActiveCellNum();
		if (cellNum>0){
			morphGenIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_morph'" , "status,'STOP'" , "odl"});
//			if (morphGenIds.size()>0)
				morphGenId = morphGenIds.get(0);
			
			if (morphGenId>-1){			
				List<Long> morphObjIds = new ArrayList<Long>();
				morphObjIds.addAll(dbUtil.readStimObjIdsFromGenId(morphGenId));
				morphObjSpecs = dbUtil.readListStimSpecs(morphObjIds);
				morphObjCats = new Integer[morphObjSpecs.size()];
				int ii = 0;
				for (BsplineObjectSpec mSpec : morphObjSpecs){
					if (ii==161)
						System.out.println(ii);
					morphObjCats[ii] = mSpec.getCategory();
					System.out.println(ii);
					ii++;
				}
			}
		}
		
		//populate vectors, create stimuli
		int morphCnt = 0;
		int trialCnt = 0;
		int morphObjCounter;
		for (int cc=0;cc<numCats;cc++){
			morphObjCounter = 0;
			for (int mm=0;mm<numMorphsPlusCan;mm++){
				//Insert category into stimIdxVec
				stimIdxVec[morphCnt]=cc;
				morphCnt++;
				
				if(mm<numMorphsPerCat){ 	//for morphed stimuli...
					//insert "numRepeats" copies of the appropriate stimuliIdx into trialTestVec and trialSampleVec. Populate trialIdxVec
					for (int tt=0;tt<numRepeats;tt++){
						trialTestVec[trialCnt]=cc*numMorphsPlusCan+numMorphsPerCat;
						trialSampleVec[trialCnt]=cc*numMorphsPlusCan+mm;
						trialIdxVec[trialCnt]=trialCnt;
						trialCnt++;
					}

					//create morphed stimuli (match and nonMatch)
					while (morphObjCounter < morphObjSpecs.size()){
						if (morphObjCats[morphObjCounter]==cc){
							BsplineObjectSpec curSpec = morphObjSpecs.get(morphObjCounter);
							List<Long> newId = new ArrayList<Long>();
							newId.add(generator.writeStimObjIdFromSpec(curSpec,"BEH_exptRepeat"));
							List<BsplineObjectSpec> newSpec = dbUtil.readListStimSpecs(newId);
							sampleSpecVec[cc*numMorphsPlusCan+mm] = newSpec.get(0);   
							break;
						}
						else
							morphObjCounter++;
					}
					if (morphObjCounter >= morphObjSpecs.size())
						sampleSpecVec[cc*numMorphsPlusCan+mm] = generator.createMorphedBehavStimFromCat(type,allCats[cc],behClassSample,generator.getTrialType());
					else
						morphObjCounter++;
					
					sampleObjIds.add(sampleSpecVec[cc*numMorphsPlusCan+mm].getStimObjId());
				}
				else { 	//for canonical stimuli...
					//create canonical stimuli
					testMatchSpecVec[cc*numMorphsPlusCan+mm] 		= generator.createBehavStimFromCat(type, allCats[cc], behClassMatch, 		generator.getTrialType());
					testNonMatchSpecVec[cc*numMorphsPlusCan+mm] 	= generator.createBehavStimFromCat(type, allCats[cc], behClassNonMatch, 	generator.getTrialType());
					allObjIds.add(testMatchSpecVec[cc*numMorphsPlusCan+mm].getStimObjId());
					allObjIds.add(testNonMatchSpecVec[cc*numMorphsPlusCan+mm].getStimObjId());
				}
			}
		}
		allObjIds.addAll(sampleObjIds);
		

		if (saveThumbnails) {
			List<Long> stimObjIdsSave = new ArrayList<Long>();
			stimObjIdsSave.addAll(sampleObjIds);

			String cellFolder = null;
			cellNum = readActiveCellNum();
			if (cellNum==-1) cellFolder = "noCell";
			else cellFolder = "cell" + cellNum;
			
			String savePath = System.getProperty("user.dir") + "/images/" + cellFolder + "/Behavioral/genId" + genId + "/"; 
			myMkDir(savePath);
			pngMaker.MakeFromIds(stimObjIdsSave,savePath);
			
			
			dbUtil.writeStimObjIdsForEachGenId(genId, allObjIds);
		}
		if (!dbUtil.readString("ExpLog","status").equals("START"))
			writeExptStart();
		
		
		//number of trial pairs to swap the sample period (to create nonMatch trials)
		int numNonMatchPairs = (int) Math.round((double)numTrials/2*percNonMatch);
		
		//shuffle trialIdx
		List<Integer> shuffleTrialIdx = Arrays.asList(trialIdxVec);
		Collections.shuffle(shuffleTrialIdx);
		
		
		for (int tt=0;tt<numNonMatchPairs;tt++){  //to swap each pair...
			//the first trial of the pair is taken from the "front" of the shuffled list
			int frontTrialIdx = shuffleTrialIdx.get(tt);
			//isolate the stimIdx and the category # of this trial
			int frontSampleVal = trialSampleVec[frontTrialIdx];
			int frontSampleCat = (int) Math.floor((double)frontSampleVal/numMorphsPlusCan);
			
			//search from the "back" of the shuffled list until...
			boolean done = false;
			int idx = numTrials-1;
			int backSampleVal = 0;
			while(!done){
				int backTrialIdx = shuffleTrialIdx.get(idx);
				int backTestVal = trialTestVec[backTrialIdx];
				backSampleVal = trialSampleVec[backTrialIdx];
				int backTestCat = (int) Math.floor((double)backTestVal/numMorphsPlusCan);
				int backSampleCat = (int) Math.floor((double)backSampleVal/numMorphsPlusCan);
				
				//a match trial that does not have the same category as the first trial is found
				done = (backTestCat==backSampleCat) && (frontSampleCat !=backSampleCat);
				if (!done){
					idx--;
				}
			}
			
			//swap the sample stimIdx of between the trial pair
			trialSampleVec[frontTrialIdx] = backSampleVal;
			trialSampleVec[shuffleTrialIdx.get(idx)] = frontSampleVal;
		}
		
		//sanity check
//		int sumDiff=0;
//		for (int tt=0;tt<numTrials;tt++){
//			if ((int) Math.floor((double)trialTestVec[tt]/numMorphsPlusCan)!=(int) Math.floor((double)trialSampleVec[tt]/numMorphsPlusCan)){
//				sumDiff++;
//			}
//		}
//		System.out.print(sumDiff);
		
		// Make TRIALS:
		
		//shuffle trialIdx again
		Collections.shuffle(shuffleTrialIdx);
		
		
		for (int tt=0;tt<numTrials;tt++){
			int trialIdx = shuffleTrialIdx.get(tt);
			int testSpecIdx = trialTestVec[trialIdx];
			int sampleSpecIdx = trialSampleVec[trialIdx];
			//determine whether trial isMatch
			boolean isMatch = ((int) Math.floor((double)trialTestVec[trialIdx]/numMorphsPlusCan)==(int) Math.floor((double)trialSampleVec[trialIdx]/numMorphsPlusCan));
			
			SachExptSpec spec;
			if (isMatch){
				//write the generated MATCH test stimuli to the database, then create the match trialSpec
				spec = generator.createBehTrial(sampleSpecVec[sampleSpecIdx].getStimObjId(), testMatchSpecVec[testSpecIdx].getStimObjId(), 0);
			}
			else {
				//write the generated NONMATCH test stimuli to the database, then create the nonMatch trialSpec
				spec = generator.createBehTrial(sampleSpecVec[sampleSpecIdx].getStimObjId(), testNonMatchSpecVec[testSpecIdx].getStimObjId(), -1);
			}
			spec.setTargetPosition(currentTargetLocation);
			spec.setTargetEyeWinSize(currentWindowSize);
			behTrials.add(spec);
		}
		
		long taskId;
		for (int bt=0;bt<behTrials.size();bt++){
			taskId = globalTimeUtil.currentTimeMicros();
			// save spec and tasktodo to db
			dbUtil.writeStimSpec(taskId, behTrials.get(bt).toXml());
			dbUtil.writeTaskToDo(taskId, taskId, -1, genId);
		}

		dbUtil.updateReadyGenerationInfo(genId, behTrials.size());
		
		boolean externalStop = waitLastTaskDone(true);
//		boolean externalStop;
//		if (realExp){
//			externalStop = getSpikeResponses(genId,true);
//			if (!externalStop){
//				new BehCatPrefPlot_2(genId,dbUtil,"run");
//			}
//		}
//		else externalStop = waitLastTaskDone(true);
		return externalStop;
	}
	
	
	// JK used for blocks of repeated trials, negative repsPerBlock indicates random block sizes with a 
	// max size of -repsPerBlock
	
	// ----------------------------
	// ---- GA task generation ----
	// ----------------------------
	
	public boolean generateGA() {	// use this for GA trials
				
		// ---- initalize some stuff:
		System.out.println("Generating GA run... ");
		trialType = TrialType.GA;
		generator.setTrialType(trialType);	// set trialType in generator
		plotGA = new GAGenAnalysisPlot_2(dbUtil);

				
		if (!realExp)
			realExp = isRealExpt();
		
		saveThumbnails = realExp;
		getGenId();

		stopPrevExp();
		
		// ---- create first generation
		boolean externalStop = createFirstGen();
		if (externalStop) return externalStop;
		
		// ---- create subsequent generations
		char c;
		while((thisGenId<GA_maxNumGens+1) && realExp) {
			// ask if we want to continue:
			c = SachIOUtil.prompt("To continue to generation " + (thisGenId+1) + " press (y) if not press (n) ; (m) for GAManual");
			if (c == 'n') break;
			else if(c == 'm') {
				System.out.println("\n...ending GA");
				writeExptStop();
				
				getGenId();
				trialType = TrialType.GAManual;
				generator.setTrialType(trialType);	// set trialType in generator
				thisGenId++;
				
				externalStop = manualGARun();
				if (!externalStop){
					writeExptGenDone();
					showGAanalysis(genId);
				}
				break;
			}
			externalStop = createNextGen();
			if (externalStop) return externalStop;
		}
		
		// ---- end GA		
		System.out.println("\n...ending GA");
		writeExptStop();
		
		return externalStop;
	}
	
	boolean createFirstGen() {
		firstGenId = genId;		

		if (!dbUtil.readString("ExpLog","status").equals("START"))
			writeExptStart();
		
		// -- create stimuli
		List<Long> blankStimObjIds = new ArrayList<Long>();
		List<Long> stimObjIds = new ArrayList<Long>();	// track stimObjIds for all stimuli created
		
		// make blank stims: (create one blank stimulus for each lineage, if just to have a better baseline measure)
		for (int n=0;n<GA_numLineages;n++) {
			blankStimObjIds.add(generator.generateBlankStim(thisGenId, n));	
		}
		
		int numNonBlank;
		int numStimsPerGen;
		if (!debugFlag){
			numNonBlank = GA_numNonBlankStimsPerGen;
			numStimsPerGen = GA_numStimsPerGen;
		}
		else {
			numNonBlank = 5;
			numStimsPerGen = numNonBlank+1;
		}
		int GA_numTrials = (int)Math.ceil((double)numStimsPerGen*GA_numLineages*GA_numRepsPerStim/GA_numStimsPerTrial);
				
		// make random stims:		
		for (int n=0;n<GA_numLineages;n++) {
			for (int k=0;k<numNonBlank;k++) {
				stimObjIds.add(generator.generateRandGAStim(thisGenId, n));
			}
		}

		// create PNG thumbnails (not for blanks)
		if (saveThumbnails) {

			String cellFolder = null;
			long cellNum = readActiveCellNum();
			if (cellNum==-1) cellFolder = "noCell";
			else cellFolder = "cell" + cellNum;
			
			String savePath = System.getProperty("user.dir") + "/images/" + cellFolder + "/GA/GenStart" + firstGenId + "/genId" + genId + "/"; 
			myMkDir(savePath);
			List<Long> stimObjIdsSave = new ArrayList<Long>();
			stimObjIdsSave.addAll(stimObjIds);
			pngMaker.MakeFromIds(stimObjIdsSave,savePath);
		}
		
		// add blank stim objects to global list:
		allBlankStimObjIds.addAll(blankStimObjIds);
		// add non-blank stim objects to global list:
		allStimObjIds.addAll(stimObjIds);
		
		// now add blanks
		stimObjIds.addAll(blankStimObjIds);
		
		// create trial structure, populate stimspec, write task-to-do
		createGATrialsFromStimObjs(stimObjIds);
		
		// write updated global genId and number of trials in this generation to db:
		dbUtil.updateReadyGenerationInfo(genId, GA_numTrials);
		
		boolean externalStop;
		// get acq info and put into db:
		if (realExp)	externalStop = getSpikeResponses(genId);
		else 			externalStop = waitLastTaskDone();

		if (!externalStop) {
			writeExptGenDone();
			if (realExp)	showGAanalysis(genId);
		}
		
		return externalStop;
	}
	
	void createGATrialsFromStimObjs(List<Long> stimObjIds) {
		// -- create trial structure, populate stimspec, write task-to-do

		// first, log stimobjids for each genid:
		if (realExp)	dbUtil.writeStimObjIdsForEachGenId(genId, stimObjIds);
		
		// stim repetitions:
		List<Long> allStimObjIdsInGen = new ArrayList<Long>();
		for (int n=0;n<GA_numRepsPerStim;n++) {
			allStimObjIdsInGen.addAll(stimObjIds);
		}

		// shuffle stimuli:
		Collections.shuffle(allStimObjIdsInGen);

		// create trials using shuffled stimuli:
		long taskId;
		int stimCounter = 0;

		int numStimsPerGen;
		if (!debugFlag)
			numStimsPerGen = GA_numStimsPerGen;
		else
			numStimsPerGen = 6;
		
		int GA_numTrials = (int)Math.ceil((double)numStimsPerGen*GA_numLineages*GA_numRepsPerStim/GA_numStimsPerTrial);
		
		for (int n=0;n<GA_numTrials;n++) {
			taskId = globalTimeUtil.currentTimeMicros();

			// create trialspec using sublist and taskId
			int endIdx = stimCounter + GA_numStimsPerTrial;
			while (endIdx>allStimObjIdsInGen.size()) endIdx--;	// this makes sure there's no out index of bounds exception

			String spec = generator.generateGATrialSpec(allStimObjIdsInGen.subList(stimCounter,endIdx));

			// save spec and tasktodo to db
			dbUtil.writeStimSpec(taskId, spec);
			dbUtil.writeTaskToDo(taskId, taskId, -1, genId);

			stimCounter = endIdx;
		}
	}
	
	boolean createNextGen() {
		
		// update local generation counters:
		thisGenId++;	
		System.out.print("Starting generation: " + thisGenId);
		getGenId();
		
		List<Long> blankStimObjIds = new ArrayList<Long>();		
		List<Long> stimObjIds = new ArrayList<Long>();

		// make blank stims:		
		for (int n=0;n<GA_numLineages;n++) {
			blankStimObjIds.add(generator.generateBlankStim(thisGenId, n));	// create one blank stimulus for each lineage, if just to have a better baseline measure
		}
		
		int numNonBlank;
		int numStimsPerGen;
		if (!debugFlag){
			numNonBlank = GA_numNonBlankStimsPerGen;
			numStimsPerGen = GA_numStimsPerGen;
		}
		else{
			numNonBlank = 5;
			numStimsPerGen = numNonBlank+1;
		}
		int GA_numTrials = (int)Math.ceil((double)numStimsPerGen*GA_numLineages*GA_numRepsPerStim/GA_numStimsPerTrial);
		
		// make random stims:		
		int numRandObjs = (int)(GA_fracNewRandObjs*numNonBlank);
		for (int n=0;n<GA_numLineages;n++) {
			for (int k=0;k<numRandObjs;k++) {
				stimObjIds.add(generator.generateRandGAStim(thisGenId, n));
			}
		}
		
		// make offspring stims:
			// create stimulus/FR structure, sort by FR, randomly choose parents from FR quintiles
			// create morphed offspring from parents
		int numDecendantObjs = numNonBlank-numRandObjs;
		
		// for each non-blank stimulus shown previously, find lineage and avgFR, then add to appropriate list:
		Map<Long, Double> stimObjId2avgFR_lin1 = new HashMap<Long, Double>();
		Map<Long, Double> stimObjId2avgFR_lin2 = new HashMap<Long, Double>();
		
		SachStimDataEntry data;
		long stimObjId;
		for (int n=0;n<allStimObjIds.size();n++) {
			stimObjId = allStimObjIds.get(n);
			data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
			
			if (data.getLineage() == 0) {	// first lineage 
				stimObjId2avgFR_lin1.put(stimObjId, data.getAvgFR());
			} else {						// second lineage
				stimObjId2avgFR_lin2.put(stimObjId, data.getAvgFR());
			}
		}
		

		// choose stims top morph:
			// which fitness method? 	1 = using fixed probabilities by FR quintile
			// 							2 = using distance in firing rate space
		int fitnessMethod = 1;
		List<Long> stimsToMorph_lin1 = chooseStimsToMorph(stimObjId2avgFR_lin1,numDecendantObjs,fitnessMethod); 
		List<Long> stimsToMorph_lin2 = chooseStimsToMorph(stimObjId2avgFR_lin2,numDecendantObjs,fitnessMethod);
		
		System.out.println("lin1: " + stimsToMorph_lin1);
		System.out.println("lin2: " + stimsToMorph_lin2);
		
		// create morphed stimuli:
		for (int n=0;n<numDecendantObjs;n++) {
			stimObjIds.add(generator.generateMorphStim(thisGenId, 0,stimsToMorph_lin1.get(n)));
			stimObjIds.add(generator.generateMorphStim(thisGenId, 1,stimsToMorph_lin2.get(n)));
		}
	

		// create PNG thumbnails (not for blanks)
		if (saveThumbnails) {
			// only do for those stimObjsIds not already in allStimObjIds!
			List<Long> stimObjIds2save = new ArrayList<Long>();
			stimObjIds2save.addAll(stimObjIds);
			stimObjIds2save.removeAll(allStimObjIds);
			

			String cellFolder = null;
			long cellNum = readActiveCellNum();
			if (cellNum==-1) cellFolder = "noCell";
			else cellFolder = "cell" + cellNum;
			
			String savePath = System.getProperty("user.dir") + "/images/" + cellFolder + "/GA/GenStart" + firstGenId + "/genId" + genId + "/"; 
			myMkDir(savePath);
			pngMaker.MakeFromIds(stimObjIds2save,savePath);
		}
		
		// add stim objects to global list:
		allBlankStimObjIds.addAll(blankStimObjIds);		// blank stims
		allStimObjIds.addAll(stimObjIds);				// non-blank stims
		
		// add blanks
		stimObjIds.addAll(blankStimObjIds);	
		
		// create trial structure, populate stimspec, write task-to-do
		createGATrialsFromStimObjs(stimObjIds);

		// write updated global genId and number of trials in this generation to db:
		dbUtil.updateReadyGenerationInfo(genId, GA_numTrials);
		
		// get acq info and put into db:
		boolean externalStop = getSpikeResponses(genId);
		if (!externalStop) {
			writeExptGenDone();
			showGAanalysis(genId);
		}
		return externalStop;

	}
	
	// ------------------------------------------
	// ---- load and continue previous GA run----
	// ------------------------------------------
			
	public boolean loadContinueGARun() {
		long prevGenId;
//		long dummy = Long.parseLong("20180417000");
		List<Long> prevFirstGens = dbUtil.readRowsLong("ExpLog","firstGlobalGenId",new String[]{"type,'GA'" , "status,'START'" , "cellnum," + readActiveCellNum()});
		if (prevFirstGens.size() == 0)
			return generateGA();
		else if (prevFirstGens.size() > 1){
			String inp = SachIOUtil.promptString(prevFirstGens.size() + " GA sets found. enter which one (1-based, descending order) to continue. leave empty for the last one");
			if (inp.equals("")) 
				prevGenId = prevFirstGens.get(0);
			else 						  
				prevGenId = prevFirstGens.get(Integer.parseInt(inp)-1);
		}
		else
			prevGenId = prevFirstGens.get(0);
			
		List<Long> GAgenIds;
		
//		if (prevGenId==-1) 	GAgenIds = dbUtil.readGAGenSetFromExpLog();
		GAgenIds = dbUtil.readGAGenSetFromExpLog(prevGenId);
		
		
		thisGenId = GAgenIds.size();
		if (thisGenId==0)
			return generateGA();
		firstGenId = GAgenIds.get((int)thisGenId-1);
//		dbUtil.updateLine("ExpLog","firstGlobalGenId",new Object[] {firstGenId},dbUtil.sArray("odl"));

		long cellNum = readActiveCellNum();
		long prevCell = dbUtil.readLong("ExpLog","cellNum",new String[]{"globalGenId," + firstGenId , "status,'START'"});
		if (cellNum!=prevCell)
			return generateGA();
		
		plotGA = new GAGenAnalysisPlot_2(GAgenIds.get(0),dbUtil); //inclusion of GAgenIds will also "run" the plot
    	
    	List<Long> stimObjIds = new ArrayList<Long>();
    	for (long curG : GAgenIds) stimObjIds.addAll(dbUtil.readStimObjIdsFromGenId(curG));
    	allStimObjIds.addAll(stimObjIds);
    	
		boolean externalStop = createNextGen();
		if (externalStop) return externalStop;
		char c;
		while((thisGenId<GA_maxNumGens+1) && realExp) {
			// ask if we want to continue:
			c = SachIOUtil.prompt("To continue to generation " + (thisGenId+1) + " press (y) if not press (n) ; (m) for GAManual");
			if (c == 'n') break;
			else if(c == 'm') {
				System.out.println("\n...ending GA");
				writeExptStop();
				
				getGenId();
				trialType = TrialType.GAManual;
				generator.setTrialType(trialType);	// set trialType in generator
				thisGenId++;
				externalStop = manualGARun();
				if (!externalStop){
					writeExptGenDone();
					showGAanalysis(genId);
				}
				break;
			}
			externalStop =createNextGen();
			if (externalStop) break;
		}
		return externalStop;
		// ---- end GA		
//		System.out.println("\n...ending GA");
//		writeExptStop();
		
		
	}
	
	// ----------------------------
	// ---- Manual GA task generation ----
	// ----------------------------
		
	public boolean postManualGARun(long prevGenId) {

		plotGA = new GAGenAnalysisPlot_2(dbUtil);
		
//		List<Long> GAgenIds;
//		if (prevGenId==-1) 	GAgenIds = dbUtil.readGAGenSetFromExpLog();
//		GAgenIds = dbUtil.readGAGenSetFromExpLog(prevGenId);
//		
//		for (long genId : GAgenIds){
//			
//			allStimObjIds.addAll(dbUtil.readNonBlankStimObjIdsFromGenId(genId));
//		}
		
		boolean externalStop = manualGARun();
		if (!externalStop){
			writeExptGenDone();
			if (realExp)	showGAanalysis(genId);
		}
		return externalStop;
	}
	public boolean manualGARun() {	// manual GA
				
		// ---- initalize some stuff:
		System.out.println("Manual GA run... ");
		plotGA = new GAGenAnalysisPlot_2(dbUtil);
		
		List<BsplineObjectSpec> finalSpecs = EditStim();

		stopPrevExp();
//		if (!dbUtil.readString("ExpLog","status").equals("START"))
		writeExptStart();
		
		createGAManualTrialsFromSpecs(finalSpecs);
		
		// ---- end GA		
		System.out.println("\n...ending Manual GA");
		
		if (realExp)	return getSpikeResponses(genId);
		else return waitLastTaskDone();
		
	}
	
	void createGAManualTrialsFromSpecs(List<BsplineObjectSpec> finalSpecs) {
		// -- create trial structure, populate stimspec, write task-to-do
		List<Long> stimObjIds = new ArrayList<Long>();
		for (int ss=0;ss<finalSpecs.size();ss++){ 
			stimObjIds.add(generator.writeStimObjIdFromSpec(finalSpecs.get(ss),"GAManual"));
		}
		
		if (saveThumbnails) {
			// only do for those stimObjsIds not already in allStimObjIds!
			List<Long> stimObjIds2save = new ArrayList<Long>();
			stimObjIds2save.addAll(stimObjIds);
//			stimObjIds2save.removeAll(allStimObjIds);
			
			String cellFolder = null;
			long cellNum = readActiveCellNum();
			if (cellNum==-1) cellFolder = "noCell";
			else cellFolder = "cell" + cellNum;
			
			String savePath = System.getProperty("user.dir") + "/images/" + cellFolder + "/GAManual/GenStart" + firstGenId + "/genId" + genId + "/"; 
			myMkDir(savePath);
			pngMaker.MakeFromIds(stimObjIds2save,savePath);
		}
		
		// first, log stimobjids for each genid:
		dbUtil.writeStimObjIdsForEachGenId(genId, stimObjIds);
		
		// stim repetitions:
		List<Long> allStimObjIdsInGen = new ArrayList<Long>();
		for (int n=0;n<GA_numRepsPerStim;n++) {
			allStimObjIdsInGen.addAll(stimObjIds);
		}

		allStimObjIdsInGen.add(generator.generateBlankStim(-1,-1));
		
		int numTrials = (int)Math.ceil((double)allStimObjIdsInGen.size()/8);
//		System.out.print("\n" + allStimObjIdsInGen.size());
//		System.out.print("\n" + numTrials);
		dbUtil.writeStimObjIdForEachGenId(genId,allStimObjIdsInGen.get(allStimObjIdsInGen.size()-1));

		// shuffle stimuli:
		Collections.shuffle(allStimObjIdsInGen);

		// create trials using shuffled stimuli:
		long taskId;
		int stimCounter = 0;

		for (int n=0;n<numTrials;n++) {
			taskId = globalTimeUtil.currentTimeMicros();

			// create trialspec using sublist and taskId
			int endIdx = stimCounter + GA_numStimsPerTrial;
			while (endIdx>allStimObjIdsInGen.size()) endIdx--;	// this makes sure there's no out index of bounds exception

			String spec = generator.generateGAManualTrialSpec(allStimObjIdsInGen.subList(stimCounter,endIdx));

			// save spec and tasktodo to db
			dbUtil.writeStimSpec(taskId, spec);
			dbUtil.writeTaskToDo(taskId, taskId, -1, genId);

			stimCounter = endIdx;
		}
		
		dbUtil.updateReadyGenerationInfo(genId, numTrials);
	}
	
	public List<BsplineObjectSpec> EditStim(){
		long cellNum;
		if (!debugFlag)
			cellNum = dbUtil.readLong("ExpRecTargets","isFinished=0;cellNum;-1",new String[]{}); //"order,cellNum,desc" , "limit"});
		else{
			String cellStr = SachIOUtil.promptString("debug: enter cellNum (empty for last) ");
//			String cellStr = "20171016000";
			if (!cellStr.isEmpty())
				cellNum = Long.parseLong(cellStr);
			else
				cellNum = dbUtil.readLong("ExpRecTargets","cellNum",new String[]{});
		}
		
		System.out.println(cellNum);
		
		if (cellNum==-1)
			return null;
		else
			return EditStim(cellNum);
	}
	
	public List<BsplineObjectSpec> EditStim(long cellNum){
		
		BsplineObjectSpec 	curSpec  = new BsplineObjectSpec();
		double[]			curIden  = new double[8];
							//IDENTITY: type&num ; singleLimbMod num ; sweepLimbMod num ; rotation ; size ; isCanonical
		
		List<ArrayList<BsplineObjectSpec>> 	specLists = new ArrayList<ArrayList<BsplineObjectSpec>>();
		List<ArrayList<double[]>> 			iden = 		new ArrayList<ArrayList<double[]>>();
		for (int ss=1;ss<=6;ss++){
			specLists.add(new ArrayList<BsplineObjectSpec>());
			iden.add(new ArrayList<double[]>());
		}
		
		List<BsplineObjectSpec> origList;
		List<BsplineObjectSpec> singleMorphSpecs0;
		List<BsplineObjectSpec> singleMorphSpecs1;
		List<BsplineObjectSpec> singleMorphSpecs2;
		List<BsplineObjectSpec> singleMorphSpecs3;
		List<BsplineObjectSpec> finalSpecs;
		
//		List<BsplineObjectSpec> origList = new ArrayList<BsplineObjectSpec>();
//		List<BsplineObjectSpec> singleMorphSpecs0 = new ArrayList<BsplineObjectSpec>();
//		List<BsplineObjectSpec> singleMorphSpecs1 = new ArrayList<BsplineObjectSpec>();
//		List<BsplineObjectSpec> singleMorphSpecs2 = new ArrayList<BsplineObjectSpec>();
//		List<BsplineObjectSpec> singleMorphSpecs3 = new ArrayList<BsplineObjectSpec>();
//		List<BsplineObjectSpec> finalSpecs 	=  new ArrayList<BsplineObjectSpec>();
		
		char action;
//		char prevMorphType = '\0';
//		int morphInd = 0;

		StimTestWindow testWindow = initTestWindow(700,600,true,.5);
		testWindow.create();

		boolean doneAll = false;
		while (!doneAll){
			origList 			= specLists.get(0);
			singleMorphSpecs0 	= specLists.get(1);
			singleMorphSpecs1 	= specLists.get(2);
			singleMorphSpecs2 	= specLists.get(3);
			singleMorphSpecs3 	= specLists.get(4);
			finalSpecs 			= specLists.get(5);
			
			
			String actionStr = 	"\nwhich Action?" + 
							"\n --------- general actions (pertaining to origList, singleMorph 0-3, and finalSpecs)------" +
							"\n (i) display list idens" +
							"\n (v) view a list" + 
							"\n (c) clear a list" +
							"\n (x) remove a single element from a list" + 
							"\n (o) commit a list (unmorphed) to the next list (not applicable to finalSpecs)";
			
			if (origList.isEmpty())
				actionStr = actionStr + 
							"\n --------- populating origList-----------------" +
							"\n (g) pick GA shapes to morph" + 
							"\n (b) add Beh Canonical shapes to morph" +
							"\n (e) add morphed shapes from Beh_expt for further morphing";
			
			else
				actionStr = actionStr + 
							"\n ------------process origList (size: " + origList.size() + ")----------------" + 
						    "\n (s) morph single letter";
							
			
			if (!(singleMorphSpecs0.isEmpty() && singleMorphSpecs1.isEmpty() && singleMorphSpecs2.isEmpty() && singleMorphSpecs3.isEmpty()))
				actionStr = actionStr +	
							"\n-------------process singleMorphSpecs (sizes: [" +
							 													singleMorphSpecs0.size() + ", " +
							 													singleMorphSpecs1.size() + ", " +
							 													singleMorphSpecs2.size() + ", " +
							 													singleMorphSpecs3.size() + "])--------------------" +
							"\n  (f) perform final morph sweep functions";
			
			actionStr = actionStr + 
							"\n ------------ final functions-------------------" +
							"\n (t) CANCEL/TERMINATE";
			
			if (!finalSpecs.isEmpty())
				actionStr = actionStr +
							"\n  (d) FINISHED/CONTINUE!\n" ;
							
			
			action = SachIOUtil.prompt(actionStr);
			
			// **************************************** GENERAL VIEW/CLEAR/DELETE FUNCTIONS *******************
			if (action=='i' || action=='v' || action=='c' || action=='x' || action=='o'){
				int sumPop = 0;
				char[] allList = new char[]{'o', '0', '1', '2', '3', 'f'};
				
				int counter = -1;
				int listInd = -1;
				for (List<BsplineObjectSpec> ss : specLists){
					counter++;
					sumPop = sumPop + (ss.isEmpty() ? 0 : 1);
					if (!ss.isEmpty())
						listInd = counter;
				}
				if (sumPop>1){
					boolean inpDone = false;
					while (!inpDone){
						inpDone = true;
						char whichList = SachIOUtil.prompt("which list: (o, 0, 1, 2, 3, f) for (sizes: " + 
																										origList.size() + ", " +
																										singleMorphSpecs0.size() + ", " +
																										singleMorphSpecs1.size() + ", " +
																										singleMorphSpecs2.size() + ", " +
																										singleMorphSpecs3.size() + ", " +
																										finalSpecs.size() 		 + ") ");
						int al = 0;
						while (al<allList.length){
							if (whichList==allList[al]){
								listInd = al;
								break;
							}
							al++;
						}
						if (al==allList.length){
							System.out.println("do not recognize input. try again.");
							inpDone = false;
						}
					}
				}
				
				if (action=='i'){
					for (double[] ii : iden.get(listInd))
						System.out.println(mkIdenStr(ii));
				}
				else if (action=='v')
					showStim(specLists.get(listInd),false,testWindow);
				else if (action=='c'){
					specLists.get(listInd).removeAll(specLists.get(listInd));
					iden.get(listInd).removeAll(iden.get(listInd));
				}
				else if (action=='x'){
					boolean removeDone = false;
					int whichRemove = -1;
					while (!removeDone){
						whichRemove = SachIOUtil.promptInt("which element to remove? (0 to " + (specLists.get(listInd).size()-1) + ", -1 to cancel)");
						if (whichRemove<0 || whichRemove>=specLists.get(listInd).size()){
							if (whichRemove==-1)
								break;
							else
								System.out.println("do not recognize element index to remove. try again.");
						}
						else
							removeDone = true;
					}
					if (removeDone){
						specLists.get(listInd).remove(whichRemove);
						iden.get(listInd).remove(whichRemove);
					}
				}
				else if (action=='o'){
					if (listInd==5)
						System.out.println("you can't commit the finalSpecs to anything else silly...");
					else if (listInd==0){
						int[] keepInd;
						
						int group = SachIOUtil.promptInt("\n Which singleMorph group? (sizes: " + 	singleMorphSpecs0.size() + ", " +
																									singleMorphSpecs1.size() + ", " +
																									singleMorphSpecs2.size() + ", " +
																									singleMorphSpecs3.size() + ") ");
						
						mergeStimIdenList(specLists.get(group+1),iden.get(group+1),specLists.get(0),iden.get(0));
//						switch (group) {
//						case 0 :
//							mergeStimIdenList(specLists.get(1))
//							break;
//						case 1 :
//							keepInd = idenUnique(iden.get(2),origList);
//							specLists.get(2).addAll(applyIndex(origList,keepInd));
//							iden.get(2).addAll(applyIndex(iden.get(0),keepInd));
//							break;
//						case 2 :
//							keepInd = idenUnique(iden.get(3),origList);
//							specLists.get(3).addAll(applyIndex(origList,keepInd));
//							iden.get(3).addAll(applyIndex(iden.get(0),keepInd));
//							break;
//						case 3 :
//							keepInd = idenUnique(iden.get(4),origList);
//							specLists.get(4).addAll(applyIndex(origList,keepInd));
//							iden.get(4).addAll(applyIndex(iden.get(0),keepInd));
//							break;
//						}
					}
					else
						mergeStimIdenList(specLists.get(5),iden.get(5),specLists.get(listInd),iden.get(listInd));
					
				}
			}	
							
			// **************************************** POPULATE ORIGLIST ********************************
			if (action=='g' && origList.size()==0){
				List<Long> allFirstGens = dbUtil.readRowsLong("ExpLog","firstGlobalGenId",new String[]{"type,'GA'" , "status,'GEN_DONE'", "cellNum," + cellNum});
				List<Long> uniqueFirstGens = new ArrayList<Long>();
				uniqueFirstGens.add(allFirstGens.get(0));
				for (long af : allFirstGens){
					if (af!=uniqueFirstGens.get(uniqueFirstGens.size()-1))
						uniqueFirstGens.add(af);
				}
				long firstGen;
				if (uniqueFirstGens.size()>1){
					String gaSet = SachIOUtil.promptString(uniqueFirstGens.size() + " GA sets detected, pick which one (0-based), leave empty for last one, -1 for all: ");
					if (gaSet.isEmpty())
						firstGen = uniqueFirstGens.get(uniqueFirstGens.size()-1);
					else{
						int gaSetInt = Integer.parseInt(gaSet);
						if (gaSetInt>=0)
							firstGen = uniqueFirstGens.get(gaSetInt);
						else
							firstGen = -1;
					}
					
				}
				else
					firstGen = uniqueFirstGens.get(0);
				
				List<Long> genIds;
				if (firstGen == -1)
					genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'GA'" , "status,'GEN_DONE'", "cellNum," + cellNum});
				else
					genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'GA'" , "status,'GEN_DONE'", "cellNum," + cellNum, "firstGlobalGenId," + firstGen});
					
				List<Long> gaIdsL = new ArrayList<Long>();
				for (long gg : genIds)
					gaIdsL.addAll(dbUtil.readRowsLong("GenId_to_StimObjId","stimObjId",dbUtil.sArray("gen_id," + gg)));
				
				long[] gaIds = dbUtil.toArray(gaIdsL);
				double[] gaFrs = new double[gaIds.length];
				int[] gaLin = new int[gaIds.length];
				for (int gg=0;gg<gaIds.length;gg++){
					gaFrs[gg] = dbUtil.readDouble("StimObjData","extract;data;Data;avgFR",	dbUtil.sArray("id," + gaIds[gg]));
					gaLin[gg] = dbUtil.readInteger("StimObjData","extract;data;Data;lineage",dbUtil.sArray("id," + gaIds[gg]));
				}
				
				int[] sortInd = dbUtil.sortIndex(gaFrs,false);
				long[] gaIdsSorted = dbUtil.applyIndex(gaIds,sortInd);
				int[] gaLinSorted = dbUtil.applyIndex(gaLin,sortInd);
				
				List<Long> addInd = new ArrayList<Long>();
				List<Integer> addIndIden = new ArrayList<Integer>();
				boolean actionDone = false;
				while (!actionDone){
					String inp = SachIOUtil.promptString(	"pick indiv GA stim as 'r,(rank#)' or..." + 
							"\nchoose range as '(range),(lineage),(subType),(num)'" +
								"\nwhere range={'t' 'm' 'b'}, lineage={0 1}, subType={'f' 'r'}" +
								"\n leave empty to terminate");
					
					if (inp.isEmpty())
						break;
					
					String[] gaAdd = inp.split(",");
					
					if (gaAdd.length==1) 
						actionDone = true;
					else if (gaAdd.length==2) {
						//pick indiv GA
						addInd.add(gaIdsSorted[Integer.parseInt(gaAdd[1])]);
//						double[] initIden = {Double.parseDouble(gaAdd[1]) , 0, 0, -1.0, -1.0, -1.0};
						addIndIden.add(Integer.parseInt(gaAdd[1]));
//						addIndIden.add("G" + gaAdd[1] + ";");
					}
					else {
						//pick range
						int lowBound;
						int highBound;
						if (gaAdd[0].equals("t")){
							lowBound = 0;
							highBound = gaIds.length/3;
						}
						else if (gaAdd[0].equals("m")){
							lowBound = gaIds.length/3;
							highBound = gaIds.length/3 *2;
						}
						else if (gaAdd[0].equals("b")){
							lowBound = gaIds.length/3 *2;
							highBound = gaIds.length;
						}
						else {
							System.out.println("do not recognize range...\n--------------------------------------");
							continue;
						}
						int curLin = Integer.parseInt(gaAdd[1]);
						
						
						List<Integer> gaPool = new ArrayList<Integer>();
						for (int pp=lowBound;pp<highBound;pp++)
							if (gaLinSorted[pp]==curLin)
								gaPool.add(pp);

						if (gaAdd[2].equals("r"))
							Collections.shuffle(gaPool);
//						if (gaAdd[2].equals("f")){
						
//						List<Long> addList = new ArrayList<Long>();
						for (int oo=0;oo<Integer.parseInt(gaAdd[3]);oo++){
							addInd.add(gaIdsSorted[gaPool.get(oo)]);
//							addIndIden.add(new double[]{(double)gaPool.get(oo) , -1, -1, -1, -1, -1});
							addIndIden.add(gaPool.get(oo));
//							addIndIden.add("G" + gaPool.get(oo) + ";");
						}
					}
				}
				specLists.get(0).addAll(dbUtil.readListStimSpecs(addInd));
				for (int ss = 0;ss<addInd.size();ss++){
					curSpec = specLists.get(0).get(ss);
					//note changing the GA indexing to 1-based here so it is positive and does not clash with Beh indexing (non-positive) , will change back to 0-based when converting to String
					double[] newIden = {(double)addIndIden.get(ss)+1 , 0 , 0 , curSpec.getGlobalOri() , curSpec.getSize() , (double)(curSpec.isCanonical() ? 1 : 0) , 0 , 0}; 
					iden.get(0).add(newIden);
				}
				
				curSpec = specLists.get(0).get(0);
				curIden = iden.get(0).get(0);
//				origList.addAll(dbUtil.readListStimSpecs(addInd));
//				curSpec = origList.get(0);
			}
			else if (action=='b' && origList.size()==0){
				boolean groupDone = false;
				List<Integer> inpCats = new ArrayList<Integer>();
				while (!groupDone){
					String group = SachIOUtil.promptString("enter group: (0,1,2,3) for cats 0-7, 8-15, 16-23, 24-31 respectively. empty to terminate ");
					if (group.isEmpty())
						groupDone = true;
					else {
						int groupInt = Integer.parseInt(group);
						for (int gg = groupInt*8;gg<(groupInt+1)*8;gg++)
							inpCats.add(gg);	
					}
				}
				
				if (!inpCats.isEmpty()){
					for (int cc=0;cc<32;cc++)
						if (inpCats.contains(cc)){
							specLists.get(0).add(generator.createBehavStimFromCat(StimType.BEH_MedialAxis, cc, BehavioralClass.NA, TrialType.GAManual,false));
							iden.get(0).add(new double[]{(double)-cc , 0 , 0 , 0 , 4 , 1 , 0 , 0});
//							iden.get(0).add("B" + cc + ";can;");
						}
					
					curSpec = specLists.get(0).get(0);
					curIden = iden.get(0).get(0);
				}
			}
			else if (action=='e' && origList.size()==0){
				List<Long> exptIds;
				try
				{ exptIds = dbUtil.readStimObjIdsFromGenId(dbUtil.readLong("ExpLog","globalGenId",new String[]{"type,'Beh_exptRepeat'" , "status,'START'" , "cellNum," + cellNum}));}
				catch (Exception e) 
				{System.out.println("no beh_exptRepeats found..."); exptIds = new ArrayList<Long>();}
				
				if (!exptIds.isEmpty()){
					for (int ee=exptIds.size()-1;ee>=0;ee--)
						if (!dbUtil.readString("StimObjData","extract;spec;StimSpec;behavioralClass",dbUtil.sArray("id," + exptIds.get(ee))).equals("SAMPLE"))
							exptIds.remove(ee);
					
					
					int[] behCats;
					if(activeMonkey == Monkey.Gizmo) {
						behCats = new int[]{0,1,2,3,4,5,6,7};		
					} else {  //Shaggy
						behCats = new int[]{8,9,10,11,12,13,14,15};
					}
					
					
					specLists.get(0).addAll(dbUtil.readListStimSpecs(exptIds));
					
					List<double[]> exptIden = new ArrayList<double[]>();
					int cnt = 0;
					for (double cc=0;cc<8;cc++){
						for (double mo=0;mo<(exptIds.size()/8);mo++){
							curSpec = specLists.get(0).get(cnt);
							iden.get(0).add(new double[]{-(cc+mo/10) , 0 , 0 , 0 , curSpec.getSize() , (double)(curSpec.isCanonical() ? 1 : 0) , 0 , 0}); 
							cnt++;
						}
					}
					
					curSpec = specLists.get(0).get(0);
					curIden = iden.get(0).get(0);
				}
			}
			
			// **************************************** PROCESS ORIGLIST ********************************
			else if (action=='s' && origList.size()!=0){
				String stimStr = SachIOUtil.promptString("\n choose stim (empty for repeat, otherwise: 0 to " + (origList.size()-1) + ")" );
				if (stimStr.length()!=0){
					int stimNum = Integer.parseInt(stimStr);
					curSpec = origList.get(stimNum);
					curIden = iden.get(0).get(stimNum);
				}
				
				BsplineObject morphObj = new BsplineObject();
				morphObj.setCantFail(true);
				morphObj.setSpec(curSpec.toXml());
				morphObj.createObjFromSpec();
				
				List<BsplineObjectSpec> singleMorphSpecs = new ArrayList<BsplineObjectSpec>();
				List<double[]> 			singleMorphIden  = new ArrayList<double[]>();
				
				int nodeNum;
				String nodeStr;
				
				boolean doneSingle = false;
				boolean singleFlag = true;
				double curOri;
				BsplineObjectSpec 	curMorphSpec = curSpec;
				double[]			curMorphIden = Arrays.copyOf(curIden,8);
				while (!doneSingle){
					
					if (singleFlag)
						showStim(curMorphSpec,false,testWindow);
					
					String prompt = "\nChoose action:";
					if (singleFlag)
						prompt = prompt + 	"\n  (c) canonicalize" +  	//probably the first action: make all widths the same
											"\n  (a) add limb (may sweep, but one-time only)" + 		//
										 	"\n  (l) remove limb" + 	// 
											"\n  (r) rotate (single angle only)" + 			// 
										 	"\n  (s) change size (single size only)" +
											"\n  (m) mirror shape" + 
										 	"\n  (i) limb Info";
					
					prompt = prompt + 		"\n  (d) COMMIT CHANGES" + 
										 	"\n  (e) RETURN TO MAIN MENU (any un-committed changes will be lost)" + 
											"\n";
					
					
				
					char singleModType = SachIOUtil.prompt(prompt);
				
					switch (singleModType){
					case 'i':
						if (!singleFlag){
							System.out.println("WARNING: singleFlag is false");
							break;
						}
						List<LimbSpec> infoLimbs = curMorphSpec.getAllLimbSpecs();
						String lenStr = "Lengths:                   ";
						String widStr = "Widths:  " + morphObj.getNodes(0).getWidth() + ", ";
						String oriStr = "Oris:                      ";
						for (int ll=0 ; ll<infoLimbs.size() ; ll++){
							lenStr = lenStr + (infoLimbs.get(ll).getLength());
							widStr = widStr + (morphObj.getNodes(ll+1).getWidth());
							oriStr = oriStr + (infoLimbs.get(ll).getOri());
							if (ll!=infoLimbs.size()-1){
								lenStr = lenStr + ", ";
								widStr = widStr + ", ";
								oriStr = oriStr + ", ";
							}
						}
						System.out.println(lenStr);
						System.out.println(widStr);
						System.out.println(oriStr);
						System.out.println("GlobalOri: " + morphObj.getGlobalOri());
						System.out.println("Size: " + morphObj.getSize());
						System.out.println(mkIdenStr(curMorphIden));
						break;
						
					case 'c':
						if (!singleFlag){
							System.out.println("WARNING: singleFlag is false");
							break;
						}
						
//						if (curMorphIden[5] == 1){
//							System.out.println("WARNING: current stimuli is already canonical. exiting 'canonicalize'");
//							break;
//						}
						
						morphObj.canonicalize();
						morphObj.updateSpecParams();
						morphObj.createObjFromSpec();
						
						curMorphIden[5] = 1;
						
						break;
					
					case 'a':  //add limb
						if (!singleFlag){
							System.out.println("WARNING: singleFlag is false");
							break;
						}
						
						nodeStr = SachIOUtil.promptString("\nselect Node: (empty to cancel)");
						if (nodeStr.isEmpty())
							break;
						
						try {nodeNum = Integer.parseInt(nodeStr);}
						catch (Exception e) {
							nodeNum = -1;
						}
						if (nodeNum<0 || nodeNum>=morphObj.getNodes().length){
							System.out.println("do not recognize inputted nodeNum");
							break;
						}
						
						int limbID = nodeNum>0 ? nodeNum-1 : 0;

						curOri  = morphObj.getGlobalOri();
						double[] L = new double[1];
						double l;
						try {
						l = SachIOUtil.promptDouble("\nset length (-2 for same, -1 for random, 0 for range):");
						}
						catch (Exception e) {
							System.out.println("length must be numeric");
							break;
						}
						
						if (l==-2){
							l = morphObj.getLimbs(limbID).getLength();
							L[0] = l;
						}
						else if(l==-1){
							l = SachMathUtil.randRange(6,2);
							L[0] = l;
						}
						else if(l==0){
							L = promptList();
						}
						else if(l<0){
							System.out.println("length must be positive");
						}
						else {
							L[0] = l;
						}
						
						
						double[] W = new double[1];
						double w;
						try {
							w = SachIOUtil.promptDouble("\nset width (-2 for same, -1 for random, 0 for range): ");
						}
						catch (Exception e) {
							System.out.println("width must be numeric");
							break;
						}
						if (w==-2){
							w = morphObj.getNodes(nodeNum).getWidth();
							W[0] = w;
						}
						else if(w==-1){
							w = SachMathUtil.randRange(3.5,.4);
							W[0] = w; 
						}
						else if(w==0){
							W = promptList();
						}
						else if(w<0){
							System.out.println("width must be positive");
						}
						else {
							W[0] = w;
						}
						
						
						double[] A = new double[1];
						double a;
						try{  
							a = SachIOUtil.promptDouble("\nset ori (-1 for range): ");
						}
						catch (Exception e) {
							System.out.println("Ori must be numeric");
							break;
						}
						if(a==-1){
							A = promptList();
						}
						else{
							A[0] = a;
						}
						
						
						singleFlag = (L.length==1 && W.length==1 && A.length==1);
						double addLimbCnt = 1;
						double curNumLimbMod = limbModCnt(singleMorphIden);
						for (int ll=0;ll<L.length;ll++){
							for (int ww=0;ww<W.length;ww++){
								for(int aa=0;aa<A.length;aa++){
									
									morphObj = new BsplineObject();
									morphObj.setCantFail(true);
									morphObj.setSpec(curMorphSpec.toXml());
									morphObj.createObjFromSpec();
									morphObj.addManualLimbHandler(nodeNum,L[ll],W[ww],A[aa]-curOri,true);
									morphObj.updateSpecParams();
									morphObj.createObjFromSpec();
									
									curMorphIden[1] = curNumLimbMod + addLimbCnt;
									addLimbCnt++;
									
									if (!singleFlag){
										singleMorphSpecs.add(morphObj.getSpec());
										singleMorphIden.add(curMorphIden);
									}
								}
							}
						}
						
						break;
					case 'l':  //remove limb
						if (!singleFlag){
							System.out.println("WARNING: singleFlag is false");
							break;
						}
						
						int localNumLimbs = morphObj.getLimbs().size();
						
						if (localNumLimbs==1){
							System.out.println("only one limb! cannot remove it");
							break;
						}

						nodeNum = -1;
						boolean done = false;
						while(!done){
							nodeStr = SachIOUtil.promptString("\nselect Node: (empty to cancel)");
							if (nodeStr.isEmpty()){
								nodeNum = -1;
								break;
							}
							try {nodeNum = Integer.parseInt(nodeStr);}
							catch (Exception e) {
								nodeNum = -1;
								break;
							}
							
							done = true;
							for (int ll=nodeNum;ll<localNumLimbs;ll++){
								if (nodeNum==morphObj.getLimbs(ll).getNodeId()){
									System.out.print("\nnot an end node!");
									done = false;
									break;
								}
							}
						}
						
						if (nodeNum<0 || nodeNum>=morphObj.getNodes().length){
							System.out.println("do not recognize inputted nodeNum");
							break;
						}
						
						morphObj.removeEndLimb(nodeNum);
						morphObj.updateSpecParams();
						morphObj.createObjFromSpec();
						
						curMorphIden[1] = limbModCnt(singleMorphIden) + 1;
						
						break;
					case 'r':
						String oriInp = SachIOUtil.promptString("\n change Ori (single value only, empty to cancel): ");
						if (oriInp.isEmpty())
							break;
						
						double ori;
						try{
							ori = Double.parseDouble(oriInp);
						}
						catch (Exception e) {
							System.out.println("Ori must be numeric");
							break;
						}
						
						
						morphObj.setGlobalOri( (360 - ( (360 - (morphObj.getGlobalOri()+ori)) % 360) ) % 360);
						morphObj.updateSpecParams();
						morphObj.createObjFromSpec();
						
						curMorphIden[3] = morphObj.getGlobalOri();
						break;
					case 's':  //size
						if (!singleFlag)
							break;
						String sizeStr = SachIOUtil.promptString("\nCurrent size: " + morphObj.getSize() + ". Enter new size (empty to cancel): ");
						double size;
						if (sizeStr.isEmpty())
							break;
						else{
							try{
								size = Double.parseDouble(sizeStr);
							}
							catch (Exception e) {
								System.out.println("size must be numeric");
								break;
							}
						}
						if (size<=0){
							System.out.println("size must be positive");
							break;
						}
						
						morphObj.setSize(size);
						morphObj.updateSpecParams();
						morphObj.createObjFromSpec();
						
						curMorphIden[4] = morphObj.getSize();
						break;
					case 'm': //mirror
						
						boolean mirrorDebugFlag = false;
						
						String mirrorType = SachIOUtil.promptString("\n flip about 'x' or 'y' axis? (empty to cancel, all non-x inp defaults to y)");
						if (mirrorType.isEmpty())
							break;
//							mirrorType = "y";
						
						boolean flipY = !mirrorType.equals("x");
						
						double curSize = morphObj.getSize();
						curOri  = morphObj.getGlobalOri();
						double newOri;
						
						morphObj = new BsplineObject();
						morphObj.setCantFail(true);
						morphObj.setSize(curSize);
						List<LimbSpec> limbs = curMorphSpec.getAllLimbSpecs();
						int numLimbs = limbs.size();
						for (int n=0;n<numLimbs;n++) {
							LimbSpec curLimb = limbs.get(n);
							if (flipY)
								newOri = 90*2 - curLimb.getOri();
//								newOri = (90-curOri)*2-curLimb.getOri();
							else
								newOri = 180*2 - curLimb.getOri();
//								newOri = (180-curOri)*2-curLimb.getOri();

							
							int curvLen = curLimb.getCurv().length;
							double[] newCurv = new double[curvLen];
							double dummy;
							
							if (!curLimb.isSmoother()) {
								for (int nc = 0 ; nc<2 ; nc++){
									dummy = curLimb.getCurv()[nc*2];
									newCurv[nc*2] = curLimb.getCurv()[nc*2+1];
									newCurv[nc*2+1] = dummy;
								}
							}
							else{
								newCurv[0] = curLimb.getCurv()[0];
								newCurv[1] = 2-curLimb.getCurv()[1];
								newCurv[2] = curLimb.getCurv()[2];
								newCurv[3] = curLimb.getCurv()[3];
							}
							
							if (n==0) {
								if (!curLimb.isSmoother2()) {
									for (int nc = 2 ; nc<4 ; nc++){
										dummy = curLimb.getCurv()[nc*2];
										newCurv[nc*2] = curLimb.getCurv()[nc*2+1];
										newCurv[nc*2+1] = dummy;
									}
								}
								else{
									newCurv[4] = curLimb.getCurv()[4];
									newCurv[5] = 2-curLimb.getCurv()[5];
									newCurv[6] = curLimb.getCurv()[6];
									newCurv[7] = curLimb.getCurv()[7];
								}
								morphObj.firstLimb(curLimb.getLength(),curLimb.getWidth(),curLimb.getWidth2(),newOri,curLimb.getXy(),newCurv,curLimb.isSmoother(),curLimb.isSmoother2());
								morphObj.setLimbs(new LimbSpec(-1,curLimb.getLength(),curLimb.getWidth(),curLimb.getWidth2(),newOri,newCurv,curLimb.isSmoother(),curLimb.isSmoother2()));
							} else {
								morphObj.addLimb(curLimb.getNodeId(),curLimb.getLength(),curLimb.getWidth(),newOri,newCurv,curLimb.isSmoother());
								morphObj.getLimbs().add(new LimbSpec(curLimb.getNodeId(),curLimb.getLength(),curLimb.getWidth(),newOri,newCurv,curLimb.isSmoother()));
							}
							
							if (mirrorDebugFlag){
//								morphObj.setGlobalOri(curOri);
								morphObj.createObj();
								morphObj.updateSpecParams();
								showStim(morphObj.getSpec(),false,testWindow);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println(Arrays.toString(newCurv));
							}
						}
						if (flipY){
//							newOri = 90*2  - curOri;
							curMorphIden[7] = 1 - curMorphIden[7];
						}
						else {
//							newOri = 180*2 - curOri;
							curMorphIden[6] = 1 - curMorphIden[6];
						}
						
						morphObj.setGlobalOri( (360 - ( (360 - (-curOri) ) % 360) ) % 360 );
						morphObj.createObj();
						morphObj.updateSpecParams();
						
						break;
					case 'd':
						if (singleFlag){
							BsplineObjectSpec addSpec = BsplineObjectSpec.fromXml(morphObj.getSpec().toXml());
							singleMorphSpecs.add(addSpec);
							singleMorphIden.add(curMorphIden);
						}
						break;
					case 'e':
						doneSingle = true;
						break;
					}
					
					if (singleFlag && !doneSingle){
						curMorphSpec = morphObj.getSpec();
						curMorphIden = Arrays.copyOf(curMorphIden,8);
					}
				}
				
				showStim(singleMorphSpecs,false,testWindow);
				
				if (!singleMorphSpecs.isEmpty()){
					boolean commitDone = false;
					while (!commitDone){
						int group = SachIOUtil.promptInt("\n Which group to commit to? -1 to terminate, 0-3 otherwise (current sizes: " + 
								singleMorphSpecs0.size() + ", " +
								singleMorphSpecs1.size() + ", " +
								singleMorphSpecs2.size() + ", " +
								singleMorphSpecs3.size() + ")");
						
						if (group<-1 || group>3){
							System.out.println("do not recognize inputted group");
							continue;
						}
						commitDone = true;
						if (group!=-1)
							mergeStimIdenList(specLists.get(group+1),iden.get(group+1),singleMorphSpecs,singleMorphIden);
					}
				}
			}
			else if (action=='f'){
				boolean[] sweep = new boolean[]{false, false, false, false};
				boolean doneSweep = false;
				while (!doneSweep){
					String prompt = "\nChoose action:";
					for (int ss=0;ss<4;ss++)
						if (!sweep[ss])
							prompt = prompt + "\n (" + ss + ") sweep singleMorphSpecs" + ss + ", size " + specLists.get(ss+1).size();
					
					prompt = prompt + "\n (d) done with all morphs!";
					prompt = prompt + "\n     (finalSpecs size: " + finalSpecs.size();
					
					action = SachIOUtil.prompt(prompt);
					
					if (action=='d')
						doneSweep = true;
					else{
						int singleNum = Character.getNumericValue(action);
						if (singleNum<0 || singleNum>3){
							System.out.println("do not recognize inputted group");
							break;
						}
						List<BsplineObjectSpec> curSweptSpecs = specLists.get(singleNum+1);
						List<double[]> 			curSweptIden  = iden.get(singleNum+1);
						
						boolean doneCurSweep = false;
						while (!doneCurSweep){
							List<BsplineObjectSpec> nextSweptSpecs = new ArrayList<BsplineObjectSpec>();
							List<double[]> 			nextSweptIden  = new ArrayList<double[]>();
							
							prompt = 	"\nChoose action:" +
										"\n (c) duplicate and canonicalize" + 
										"\n (r) rotate" + 
										"\n (l) remove each outer limb individually" + 
										"\n (s) size" + 
										"\n (m) duplicate and mirror" +
										"\n (d) COMMIT CHANGES" + 
										"\n (e) CANCEL CHANGES" +
										"\n";
							char actionSweep = SachIOUtil.prompt(prompt);
							switch (actionSweep){
							case 'c':
								mergeStimIdenList(nextSweptSpecs,nextSweptIden,curSweptSpecs,curSweptIden);
								
								for (int ss=0;ss<curSweptSpecs.size();ss++){
									BsplineObject morphObj = new BsplineObject();
									morphObj.setCantFail(true);
									morphObj.setSpec(curSweptSpecs.get(ss).toXml());
									morphObj.createObjFromSpec();
									morphObj.canonicalize();
//									morphObj.addManualLimbHandler(nodeNum,L[ll],W[ww],A[aa]);
									morphObj.updateSpecParams();
									morphObj.createObjFromSpec();
									
									double[] newIden = Arrays.copyOf(curSweptIden.get(ss),8);
									newIden[5] = 1;
									mergeStimIdenList(nextSweptSpecs,nextSweptIden,morphObj.getSpec(),newIden);
								}
								break;
							case 'r':
								double[] Ori;
								String preSelect = SachIOUtil.promptString("ori range: (empty) for custom, (1) for -180:45:135, (2) for -90:22.5:90, (3) -45:11.25:45");
								if (preSelect.isEmpty())
									Ori = promptList();
								else if (preSelect.equals("1"))
									Ori = new double[]{-180, -135, -90, -45, 0, 45, 90, 135};
								else if (preSelect.equals("2"))
									Ori = new double[]{-90, -67.5, -45, -22.5, 0, 22.5, 45, 67.5, 90};
								else if (preSelect.equals("3"))
									Ori = new double[]{-45, -33.75, -22.5, -11.25, 0, 11.25, 22.5, 33.75, 45};
								else{
									System.out.println("do not recognize ori input");
									break;
								}
								
								for (int ss=0;ss<curSweptSpecs.size();ss++){
									for (int oo=0;oo<Ori.length;oo++){
										BsplineObject morphObj = new BsplineObject();
										morphObj.setCantFail(true);
										morphObj.setSpec(curSweptSpecs.get(ss).toXml());
										morphObj.createObjFromSpec();
										morphObj.setGlobalOri( (360 - ( (360-(morphObj.getGlobalOri()+Ori[oo]) ) % 360) ) % 360);
										morphObj.updateSpecParams();
										morphObj.createObjFromSpec();
										
										double[] newIden = Arrays.copyOf(curSweptIden.get(ss),8);
										newIden[3] = morphObj.getGlobalOri();
										mergeStimIdenList(nextSweptSpecs,nextSweptIden,morphObj.getSpec(),newIden);
									}
								}
								break;
							case 'l':
								for (int ss=0;ss<curSweptSpecs.size();ss++){
									BsplineObject singleObj = new BsplineObject();
									singleObj.setCantFail(true);
									singleObj.setSpec(curSweptSpecs.get(ss).toXml());
									
									int localNumLimbs = singleObj.getLimbs().size();
									if (localNumLimbs==1)
										continue;
									
									double cnt = 1;
									for (int nodeNum = 0;nodeNum<=localNumLimbs;nodeNum++){
										boolean skipFlag = false;
										for (int ll=nodeNum;ll<localNumLimbs;ll++){
											if (nodeNum==singleObj.getLimbs(ll).getNodeId()){
												skipFlag = true;
												break;
											}
										}
										if (!skipFlag){
											BsplineObject morphObj = new BsplineObject();
											morphObj.setCantFail(true);
											morphObj.setSpec(curSweptSpecs.get(ss).toXml());
											morphObj.removeEndLimb(nodeNum);
											morphObj.updateSpecParams();
											morphObj.createObjFromSpec();
											
											double[] newIden = Arrays.copyOf(curSweptIden.get(ss),8);
											newIden[2] = cnt;
											mergeStimIdenList(nextSweptSpecs,nextSweptIden,morphObj.getSpec(),newIden);
											cnt++;
										}
									}
								}
								break;
							case 's':
								double[] size = promptList();
								for (int ss=0;ss<curSweptSpecs.size();ss++){
									for (double si : size){
										if (si>0){
											BsplineObject morphObj = new BsplineObject();
											morphObj.setCantFail(true);
											morphObj.setSpec(curSweptSpecs.get(ss).toXml());
											morphObj.createObjFromSpec();
											morphObj.setSize(si);
											morphObj.updateSpecParams();
											morphObj.createObjFromSpec();
											nextSweptSpecs.add(morphObj.getSpec());
										
											double[] newIden = Arrays.copyOf(curSweptIden.get(ss),8);
											newIden[4] = morphObj.getSize();
											mergeStimIdenList(nextSweptSpecs,nextSweptIden,morphObj.getSpec(),newIden);
										}
									}
								}
								break;
							case 'm':
								BsplineObject morphObj;

								List<Integer> mirrorType = new ArrayList<Integer>();
								boolean inpDone = false;
								while (!inpDone){
									String newType = SachIOUtil.promptString("\n enter 1, 2, 3, for y, x, xy respectively. (empty to terminate)");
									if (newType.isEmpty())
										inpDone = true;
									else
										mirrorType.add(Integer.parseInt(newType));
								}
								
								mergeStimIdenList(nextSweptSpecs,nextSweptIden,curSweptSpecs,curSweptIden);
								for (int ss=0;ss<curSweptSpecs.size();ss++){
									double curSize = curSweptSpecs.get(ss).getSize();
									double curOri  = curSweptSpecs.get(ss).getGlobalOri();
									
									for (int mt : mirrorType){
										double[] newIden = Arrays.copyOf(curSweptIden.get(ss),8);
										
										morphObj = new BsplineObject();
										morphObj.setCantFail(true);
										morphObj.setSize(curSize);
										List<LimbSpec> limbs = curSweptSpecs.get(ss).getAllLimbSpecs();
										int numLimbs = limbs.size();
										for (int n=0;n<numLimbs;n++) {
											LimbSpec curLimb = limbs.get(n);
											double newOri;
											newOri = curLimb.getOri();
											if (mt==1 || mt==3) //flip about y-axis
												newOri = 90*2-curLimb.getOri();
											else
												newOri = curLimb.getOri();
											
											if (mt==2 || mt==3){ //flip about x-axis
												newOri = 180*2-newOri;
											}
											
											int curvLen = curLimb.getCurv().length;
											double[] newCurv = new double[curvLen];
											double dummy;
											
											if (!curLimb.isSmoother()) {
												for (int nc = 0 ; nc<2 ; nc++){
													dummy = curLimb.getCurv()[nc*2];
													newCurv[nc*2] = curLimb.getCurv()[nc*2+1];
													newCurv[nc*2+1] = dummy;
												}
											}
											else{
												newCurv[0] = curLimb.getCurv()[0];
												newCurv[1] = 2-curLimb.getCurv()[1];
												newCurv[2] = curLimb.getCurv()[2];
												newCurv[3] = curLimb.getCurv()[3];
											}
											if (n==0) {
												if (!curLimb.isSmoother2()) {
													for (int nc = 2 ; nc<4 ; nc++){
														dummy = curLimb.getCurv()[nc*2];
														newCurv[nc*2] = curLimb.getCurv()[nc*2+1];
														newCurv[nc*2+1] = dummy;
													}
												}
												else{
													newCurv[4] = curLimb.getCurv()[4];
													newCurv[5] = 2-curLimb.getCurv()[5];
													newCurv[6] = curLimb.getCurv()[6];
													newCurv[7] = curLimb.getCurv()[7];
												}
												morphObj.firstLimb(curLimb.getLength(),curLimb.getWidth(),curLimb.getWidth2(),newOri,curLimb.getXy(),newCurv,curLimb.isSmoother(),curLimb.isSmoother2());
												morphObj.setLimbs(new LimbSpec(-1,curLimb.getLength(),curLimb.getWidth(),curLimb.getWidth2(),newOri,newCurv,curLimb.isSmoother(),curLimb.isSmoother2()));
											} else {
												morphObj.addLimb(curLimb.getNodeId(),curLimb.getLength(),curLimb.getWidth(),newOri,newCurv,curLimb.isSmoother());
												morphObj.getLimbs().add(new LimbSpec(curLimb.getNodeId(),curLimb.getLength(),curLimb.getWidth(),newOri,newCurv,curLimb.isSmoother()));
											}
										}
										morphObj.setGlobalOri( (360 - ( (360 - (-curOri) ) % 360) ) % 360);
										morphObj.createObj();
										morphObj.updateSpecParams();
										
										if (mt==1 || mt==3)
											newIden[7] = 1 - newIden[7];
										if (mt==2 || mt==3)
											newIden[6] = 1 - newIden[6];
										
										mergeStimIdenList(nextSweptSpecs,nextSweptIden,morphObj.getSpec(),newIden);
									}
								}
								break;
							case 'd':
								mergeStimIdenList(specLists.get(5),iden.get(5),curSweptSpecs,curSweptIden);
								finalSpecs = specLists.get(5);
								doneCurSweep = true;
								break;
							case 'e':
								doneCurSweep = true;
								break;
							}
							curSweptSpecs = new ArrayList<BsplineObjectSpec>();
							for (BsplineObjectSpec nss : nextSweptSpecs)
								curSweptSpecs.add(BsplineObjectSpec.fromXml(nss.toXml()));
							//curSweptSpecs = nextSweptSpecs;
							curSweptIden  = nextSweptIden;
						}
					}
				}
			
			}
			else if (action=='t'){
				testWindow.close();
				return new ArrayList<BsplineObjectSpec>();
			}
			else if (action=='d'){
				
				List<Integer> failIdx = new ArrayList<Integer>();
				
				int idx = 0;
				for (BsplineObjectSpec checkSpec : specLists.get(5)){
					BsplineObject checkObj = new BsplineObject();
					checkObj.setSpec(checkSpec.toXml());
					if (!checkObj.createObjFromSpec()){
						failIdx.add(idx);
					}
					idx++;
				}
				
				if (!failIdx.isEmpty()){
					System.out.println("\n " + failIdx.size() + "FAILURES DETECTED!");
					
					boolean failFlag = false;
					while (!failFlag){
						String failAction = SachIOUtil.promptString("\n (v) to view failed shit, (c) to flush failed shit");
						if (failAction.equals("v")){
							for (int ff : failIdx)
								showStim(specLists.get(5).get(ff),false,testWindow);
						} else {
							failFlag = true;
							Collections.reverse(failIdx);
							for (int ff: failIdx){
								specLists.get(5).remove(ff);
								iden.get(5).remove(ff);
							}
						}	
					}
				} 
				else{
					doneAll = true;
					
					List<String> idenStr = mkIdenStr(iden.get(5));
					for (int ss=0;ss<specLists.get(5).size();ss++)
						specLists.get(5).get(ss).setBehavioralClass(idenStr.get(ss));
					
					
				}
			}
		}
		testWindow.close();
		return specLists.get(5);
	}
	
	public String mkIdenStr(double[] iden){
//		double globalOri = 360 - ( (360-(iden[3] % 360)) % 360);
		
		String newStr;
		if (iden[0]>0) //GA
			newStr = "G" + (int)(iden[0]-1);  //change from 1-based to 0-based;
		else if (Math.ceil(iden[0])+iden[0]<0) //beh_exptRepeat
			newStr = "E" + ((int) -iden[0]);
		else 
			newStr = "B" + ((int) -iden[0]);
		
		newStr = newStr + 	"singleLimbMod" 	+ 	(int)iden[1] 	+
							"sweepLimbMod"  	+	(int)iden[2] 	+
							"rot" 				+	(int)iden[3]	+
							"size" 				+ 	(double)Math.round(iden[4]*100)/100 	+
							"canonical" 		+  	(int)iden[5] 	+
							"mirrorX" 			+  	(int)iden[6] 	+
							"mirrorY" 			+ 	(int)iden[7];
		return newStr;
	}
	public List<String> mkIdenStr(List<double[]> iden){
		List<String> idenStr = new ArrayList<String>();
		for (double[] i : iden){
			idenStr.add(mkIdenStr(i));
		}
		return idenStr;
	}
	
	public boolean idenEquals(double[] A, double[] B){
		if (A.length!=B.length)
			return false;
		boolean out = true;
		for (int ii=0;ii<A.length;ii++)
			out = out && (A[ii]==B[ii]);
		
		return out;
	}
	public void mergeStimIdenList(List<BsplineObjectSpec> S, List<double[]> I, List<BsplineObjectSpec> s,List<double[]> i){
		for (int ii=0;ii<i.size();ii++){
			mergeStimIdenList(S,I,s.get(ii),i.get(ii));
		}
	}
	public void mergeStimIdenList(List<BsplineObjectSpec> S, List<double[]> I, BsplineObjectSpec s, double[] i){
		if (I.isEmpty()){
			S.add(BsplineObjectSpec.fromXml(s.toXml()));
			I.add(i);
			return;
		}

		boolean copyFlag = true;
		double maxLimbCnt = limbModCnt(I);
		double[] iCopy = Arrays.copyOf(i,8);
		for (double[] II : I){
			if (idenEquals(iCopy,II)){
				if (iCopy[1]!=0)
					iCopy[1] = maxLimbCnt+iCopy[1];
				else
					copyFlag = false;
				
				break;
			}
		}
		if (copyFlag){
			S.add(BsplineObjectSpec.fromXml(s.toXml()));
			I.add(i);
		}
	}
		
	public double limbModCnt(List<double[]> inp){
		double out = 0;
		for (int ii=0;ii<inp.size();ii++)
			out = Math.max(out,inp.get(ii)[1]);
		return out;
	}
	
	
	// ----------------------------
	// ---- Occluded Stimuli ----
	// ----------------------------
	void generateOccludedTrials() {
		
		int[] seriesToRun = null;   // max == 16
		Coordinates2D currentTargetLocation = null;
		double currentWindowSize = 4;
		
		if(activeMonkey == Monkey.Gizmo) {
			seriesToRun =  new int[]  { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};	
//			seriesToRun =  new int[]  { 8, 15, 16};
			
			currentTargetLocation = new Coordinates2D(renderer.mm2deg(0), renderer.mm2deg(95));
			currentWindowSize = 6;
		} else if(activeMonkey == Monkey.Shaggy) {
			seriesToRun = new int[]  {17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
			currentTargetLocation = new Coordinates2D(renderer.mm2deg(0), renderer.mm2deg(100));
		}
		
		int seriesRepeats = 1;
			
		// these should come from outside 
		double percentMatches = 0.80; // 
		double alphaGain = 0.84;
			
		percentMatches = SachIOUtil.promptDouble(" enter percentMatches" );
		alphaGain = SachIOUtil.promptDouble(" enter alphaGain" );
		seriesRepeats = SachIOUtil.promptInt(" enter number of repeats" );

		trialType = TrialType.BEH_occluded;
		
		// tell the generator to fill the series parameter map
		generator.fillOccluderSpecMap();
		
		
		// -- GENERATE BEH TRIALS:
		List<SachExptSpec> behTrials =new ArrayList<SachExptSpec>();		
				
		// -- create trials:
		for (int n = 0; n < seriesToRun.length; n++) {
//			SachExptSpec spec = generator.generateOccludedSeries(seriesToRun[n], percentMatches, alphaGain);
//			spec.setTargetPosition(currentTargetLocation);
			behTrials.addAll(generator.generateOccludedSeries(seriesToRun[n], percentMatches, alphaGain, currentTargetLocation,currentWindowSize));

		}


		if(seriesRepeats != 0){
			addBlocksOfBehTrials(behTrials, seriesRepeats);
		} else {
			// -- shuffle and add trials:
			shuffleAndAddBehTrials(behTrials);
		}

		// write updated global genId and number of trials in this generation to db:
		dbUtil.updateReadyGenerationInfo(genId, behTrials.size());	
		
	}
	
	// ----------------------------
	// ---- Generation Recreation----
	// ----------------------------
			
	void recreateGeneration(long prevGenId, int startTrial, int stopTrial){
		// 
		Map<Long, StimSpecEntry> specMap = new HashMap<Long, StimSpecEntry>();
		long taskId;
		long stimId; 
		long newStimId;
		int numObjs = -1;
		int typeCounter = 0;
		
//		long seed = dbUtil.getRandomSeed(prevGenId);
		long seed = dbUtil.readLong("SystemVar","val",new String[]{"name","'xper_random_seed'" , "tstamp,>,(SELECT MAX(TaskToDo.task_id) FROM TaskToDo WHERE TaskToDo.gen_id = ?)" , "limit"});
		
		SachMathUtil.setSeed(seed);
		
		dbUtil.writeSystemVar("xper_random_seed", 0, Long.toString(seed),  globalTimeUtil.currentTimeMicros());
		System.out.println("recreating generation " + prevGenId + " ... reusing random seed : " + seed);	
		
		// retrieve a map of specs, each spec contains stimobjData object ids 
		specMap = dbUtil.readStimSpecByGeneration(prevGenId);
		
		// need to sort the ids first
		List<Long> keys = new ArrayList<Long>(specMap.keySet());
		
		Collections.sort(keys);
				
		// iterate through the map, extracting stimObj ids and stimObj specs
		for(Long key : keys){			
			
			SachExptSpec spec = SachExptSpec.fromXml(specMap.get(key).getSpec());
			this.trialType = TrialType.valueOf(spec.getTrialType());
			// keep track of trial type
			if(spec.getTrialType().compareTo(trialType.name()) == -1){
				if(++typeCounter > 1){
					System.out.println("recreateGeneration() : found different trial types ! ");
				}
			}			
//			System.out.println("key " + key + ", TrialType = " + trialType.name());
		}
		
		
		// write start time to ExpLog 
		writeExptStart();
		
		// make a log entry that this is a recreation
		writeExptLogMsg("recreating " + prevGenId);
		
		//for(StimSpecEntry s : specMap.values()	){
		for(Long key : keys){
			taskId = globalTimeUtil.currentTimeMicros();
			SachExptSpec spec = SachExptSpec.fromXml(specMap.get(key).getSpec());
//			SachExptSpec spec = SachExptSpec.fromXml(s.getSpec());
//			numObjs = spec.getStimObjIdCount();
			System.out.println("\n=============================");
			System.out.println("SachExptSpec : old id = " + key + " new id =" + taskId);
//			

			// for each object, retrieve the stimObjData			
//			for(int i = 0; i < numObjs; i++){
//				// get the current stimSpec object id
//				stimId = spec.getStimObjId(i);
//				
//				// generate a fresh one
//				newStimId = globalTimeUtil.currentTimeMicros();
//				
//				//change the stimSpec object id in the exptSpec
//				spec.setStimObjId(i, newStimId);
//				
//				System.out.format("\t was %d now %d\n", stimId, newStimId);
//				
//				// this retrieves the id and the spec
//				StimSpecEntry stimSpecEntry = dbUtil.readSingleStimSpec(stimId);
//				// this is the data field
//				SachStimDataEntry data = dbUtil.readSingleStimData_v2(stimId);
//				
//				// need to use this to set the new stim obj id 
//				BsplineObjectSpec bos = BsplineObjectSpec.fromXml(stimSpecEntry.getSpec());
//				bos.setStimObjId(newStimId);
//				
//				// update the id in the data field
//				data.setStimObjId(newStimId);
//				
////				System.out.println(bos.toXml());
////				System.out.println(data.toXml());
//				
//				// write it back to the stimObjData table
//				dbUtil.writeStimObjData(newStimId, bos.toXml(), data.toXml());
//			}
//			
			// write the new stim spec and task to do
			dbUtil.writeStimSpec(taskId, spec.toXml());
			// use a new genId 
			dbUtil.writeTaskToDo(taskId, taskId, 0, genId);
			
			System.out.println("\n.......................... ");
//			System.out.println("SachExptSpec : " + spec.toXml());
	
			
		}
		
		// write updated global genId and number of trials in this generation to db:
		dbUtil.updateReadyGenerationInfo(genId, specMap.size());				
		
		System.out.println("\n...done REGENERATING  trials for " + activeMonkey.name());
		writeExptStop();
		
		return;
	}
	
	// --------------------------
	// ---- Helper functions ----
	
	public long readActiveCellNum(){
		return dbUtil.readLong("ExpRecTargets","isFinished=0;cellNum;-1",new String[]{"order,cellNum,desc" , "limit"});
	}
	public long readActiveCellDistance(){
		return (long) (dbUtil.readDouble("ExpRecTargets","isFinished=0;coordsAS_dist;-1",new String[]{"order,cellNum,desc" , "limit"})*1000);
	}
	
	public static void myMkDir(String pathInp){
		File curDir = new File(pathInp);
		File parentDir;
		
		while (!curDir.exists()){
			parentDir = curDir.getParentFile();
			if (parentDir.exists()) curDir.mkdir();
			else myMkDir(curDir.getParent());
		}
	}
	
	// --------------------------
	void addBlocksOfBehTrials(List<SachExptSpec> specs, int repsPerBlock) {
		// write trials to db:
		long taskId;
		boolean blockSizeIsRandom = false;
		int maxBlockSize = 0;
		int minBlockSize = 1;
		int trialCount = 0;
		List<SachExptSpec> originalSpecList = new ArrayList<SachExptSpec>();
		System.out.println("original list " + specs.size());
		if(repsPerBlock < 0){
			blockSizeIsRandom = true;
			maxBlockSize = repsPerBlock * -1;
			System.out.println("SachRandomGeneration : random block size, max = " + maxBlockSize);
		}
		
		
		if(trialType == TrialType.BEH_occluded) {
			if(repsPerBlock > 0){
				originalSpecList.addAll(specs);
				for(int i = 0; i < repsPerBlock - 1; i++){
					specs.addAll(originalSpecList);
					System.out.println("added list new size = " + specs.size());
				}
			}
			System.out.println("Shuffling list of " + specs.size() + " occluded specs");
			Collections.shuffle(specs, SachMathUtil.rand);
			
			// we've already built the reps using addAll(originalSpecList) so set repsPerBlock = 1
			// for the next bit
			repsPerBlock = 1;
			
		} else {
			System.out.println("Block#   Rep#   cat 1   cat 2");
		}
		
		for (int n = 0; n < specs.size(); n++) {
			if(blockSizeIsRandom){
				repsPerBlock = SachMathUtil.randRange(maxBlockSize, minBlockSize);
			}
			for(int repNum = 0; repNum < repsPerBlock; repNum++){
				taskId = globalTimeUtil.currentTimeMicros();
				// save spec and tasktodo to db
				dbUtil.writeStimSpec(taskId, specs.get(n).toXml());
				dbUtil.writeTaskToDo(taskId, taskId, repNum, genId);
				
				if(trialType == TrialType.BEH_occluded) {
					
				} else {
//					System.out.println(n + 1 + "          " + (repNum + 1) + "           " 
//							+ dbUtil.getCategory(specs.get(n).getStimObjId(0)) + "        "
//							+ dbUtil.getCategory(specs.get(n).getStimObjId(1)));
				}
				
				trialCount++;
			}
		}
		System.out.println(trialCount + " total trials");
		
	}
	void shuffleAndAddBehTrials(List<SachExptSpec> specs) {
		// -- shuffles trial specs and writes task-to-do

		// shuffle specs:
		Collections.shuffle(specs);

		// write trials to db:
		long taskId;

		for (int n=0;n<specs.size();n++) {
			taskId = globalTimeUtil.currentTimeMicros();
			
			// save spec and tasktodo to db
			dbUtil.writeStimSpec(taskId, specs.get(n).toXml());
			dbUtil.writeTaskToDo(taskId, taskId, -1, genId);
		}
	}
	
	
	public List<Long> recoverStimId(int genId, int index){
		int[] indices = new int[1];
		indices[0] = index;
		return recoverStimId(genId,indices);
	}
	public List<Long> recoverStimId(int genId, int[] indices){
		List<Long> stimObjIdsAll = new ArrayList<Long>();
		List<Long> stimObjIds = new ArrayList<Long>();
		stimObjIdsAll = dbUtil.readStimObjIdsFromGenId(genId);
		for(int ii=0;ii<indices.length;ii++){
			stimObjIds.add(stimObjIdsAll.get(indices[ii]));
		}
		return stimObjIds;
	}
//	public List<Long> showStim(boolean saveFlag,int genId, int index){
//		List<Long> stimObjIds = recoverStimId(genId,index);
//		showStim(saveFlag,stimObjIds);
//		return stimObjIds;
//	}
//	public List<Long> showStim(boolean saveFlag,int genId, int[] indices){
//		List<Long> stimObjIds = recoverStimId(genId,indices);
//		showStim(saveFlag,stimObjIds);
//		return stimObjIds;
//	}
	public void showStim(boolean saveFlag,List<Long> stimObjIds){
		
		List<BsplineObjectSpec> specs = new ArrayList<BsplineObjectSpec>();
		specs =  dbUtil.readListStimSpecs(stimObjIds);
		showStim(specs,saveFlag);
//			cell 28, stim 105 (globalOri -25)
//			stimObjIds.add(1414690537305343L);

//			cell 28, stim 1 (no rotation)
//			stimObjIds.add(1414690174619172L);
		
	//	stimObjIds.add(1462910263489055L);
	//	stimObjIds.add(1462910263500730L);
	//	stimObjIds.add(1462910263510480L);
	//	stimObjIds.add(1462910263520145L);
	//	stimObjIds.add(1462910263530299L);
		
//			stimObjIds = dbUtil.readStimObjIdsFromGenId(genId);
	}
	
	static public void showStim(BsplineObjectSpec spec,boolean saveFlag,StimTestWindow testWindow){
		List<BsplineObjectSpec> specs = new ArrayList<BsplineObjectSpec>();
		specs.add(spec);
		showStim(specs,saveFlag,testWindow);
	}
	
	public void showStim(List<BsplineObjectSpec> specs,boolean saveFlag){
		StimTestWindow testWindow = initTestWindow(700,600,true,1.0);
		testWindow.create();
		showStim(specs,saveFlag,testWindow);
		testWindow.close();
	}
	public static void showStim(List<BsplineObjectSpec> specs,boolean saveFlag,StimTestWindow testWindow){
		testWindow.clearStimObjs();
		
		if(specs.isEmpty()){
			System.out.println("nothing found ...");
		}
		System.out.println(specs.size() + " objects");
		
						
				
		if (saveFlag){
			String pngPath = System.getProperty("user.dir") + "/images/manualTemp/";
			String pngFileName = "temp";
			testWindow.setSavePng_pngMaker(true);
			testWindow.setPngPath(pngPath);
			testWindow.setPngFilename(pngFileName);
		}
		
		for(BsplineObjectSpec s : specs){
			BsplineObject bso = new BsplineObject();
			bso.drawNodes = true;
			bso.drawNums  = true;
//			bso.drawCtrlPts = true;
			bso.setCantFail(true);
			bso.setSpec(s.toXml());
			testWindow.setStimObjs(bso);			
		}
		
		testWindow.drawOnly();
	}


	static public StimTestWindow initTestWindow(int windowWidth,int windowHeight,boolean gridOn,double windowDurationSecs){
		double magLevel = 6.0;  // used inside the TestWindow ...
		StimTestWindow testWindow = new StimTestWindow(windowHeight, windowWidth , magLevel);
		testWindow.setGridOn(gridOn);
		testWindow.setSpeedInSecs(windowDurationSecs);
		
		return testWindow;
	}
	
		
	static public double[] promptList(){
		double b1 = SachIOUtil.promptDouble("lowerBound: ");
		double b2 = SachIOUtil.promptDouble("upperBound: ");
		double s  = SachIOUtil.promptDouble("step size:  ");
		double[] out = new double[(int) ((b2-b1)/s + 1)];
		int cnt = 0;
		for (double oo=b1;oo<=b2;oo=oo+s){
			out[cnt] = oo;
			cnt++;
		}
		return out;
	}
	
	public boolean waitLastTaskDone(){
		return waitLastTaskDone(false);
	}
	public boolean waitLastTaskDone(boolean isBeh){
		long lastTrialToDo;
		long lastTrialDone;
		long lastExptGen;
		
		try
		{	Thread.sleep(3000);	}
		catch (Exception e) {System.out.println(e);}
		
//		int counter = 0;
		System.out.print("Waiting for last taskDone...");
		while (true)
		{
			lastTrialToDo = dbUtil.readTaskToDoMaxId();	// move this outside loop?
			//					lastTrialDone = dbUtil.readTaskDoneCompleteMaxId();
			lastTrialDone = dbUtil.readTaskDoneCompleteMaxId(isBeh);
			
//			lastStatus = dbUtil.readLastExpLogStatus(genId);
			lastExptGen = dbUtil.readLong("ExpLog","globalGenId",new String[]{});
//			SELECT status FROM ExpLog WHERE globalGenId=? order by tstamp desc limit 1";
			
			
//			if ( counter % 20 == 0)
//				System.out.print(".");
//			counter++;
			if ( (lastTrialToDo == lastTrialDone) || (lastExptGen>genId) ) { // Completed the tasks in this generation:
				try
				{	Thread.sleep(3000);	}
				catch (Exception e) {System.out.println(e);}
				System.out.println();
				break;
			}
			try
			{	Thread.sleep(300);	}
			catch (Exception e) {System.out.println(e);}
		}		
		return (lastExptGen>genId);
	}
	public boolean getSpikeResponses(long currentGen){
		return getSpikeResponses(currentGen,false);
	}
	
	public boolean getSpikeResponses(long currentGen,boolean isBeh) {
		// first, wait for some time to make sure previous 'TaskToDo's are written to the db (the stimuli need to be presented anyway):
		
		boolean externalStop = waitLastTaskDone(isBeh);
		System.out.println("stop detected, externalStop=" + externalStop);
		if (externalStop || debugFlag) 
			return externalStop;
		
		try
		{	Thread.sleep(3000);	}
		catch (Exception e) {System.out.println(e);}
		
		// obtain spike data:
		long taskId;

		// use mine because it adds fake spike stuff!
		//MarkStimExperimentSpikeCounter spikeCounter = new MarkStimExperimentSpikeCounter();
		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
		spikeCounter.setDbUtil(dbUtil);

		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
			if (useFakeSpikes) {
				// this populates fake spike rates for trials 
				spikeEntry = spikeCounter.getFakeTaskSpikeByGeneration(currentGen);
			} else {
				spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, 0);
			}
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

				System.out.println("Entering spike info for trial: " + taskId);
				
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial get FR data for all stims and save:
				long stimObjId;
				SachStimDataEntry data;
				int entIdx;

				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
					stimObjId = trialSpec.getStimObjId(n);
					data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
					
					// add acq info:					
					entIdx = 2*n+2;
					data.addTaskDoneId(taskId);
					data.setSampleFrequency(ent.getSampleFrequency());
					data.addSpikesPerSec(ent.getSpikePerSec(entIdx));
					data.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
					data.addTrialStageData(ent.getTrialStageData(entIdx));
					
					// resave data:
//					dbUtil.updateStimObjData(stimObjId, data.toXml());
					dbUtil.updateLine("StimObjData","data",data.toXml(),new String[]{"id,"+stimObjId});
				}
			}	
		} catch(InvalidAcqDataException ee) {
			ee.printStackTrace();
		} catch(NoMoreAcqDataException ee) {
			ee.printStackTrace();
		}
		return externalStop;
	}
	
//	public void getSpikeResponsesAfterEachTrial(long currentGen) {
//		
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
//
//		// obtain spike data:
//		long taskId;
//
//		// use mine because it adds fake spike stuff!
//		//MarkStimExperimentSpikeCounter spikeCounter = new MarkStimExperimentSpikeCounter();
//		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
//		spikeCounter.setDbUtil(dbUtil);
//
//		try{
//			// get spike data for all trials:
//			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
//			if (useFakeSpikes) {
//				// this populates fake spike rates for trials 
//				spikeEntry = spikeCounter.getFakeTaskSpikeByGeneration(currentGen);
//			} else {
//				spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, 0);
//			}
//			// for each trial done in a generation:
//				// get blank FRs:
//			List<Double> blankFRs = new ArrayList<Double>();
//			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
//			{
//				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
//				taskId = ent.getTaskId();
//				
//				// get TrialSpec:
//				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
//				
//				// for each stimObj in the trial:
//				long stimObjId;
//				BsplineObjectSpec spec;
//				int entIdx;				// MarkEveryStepTaskSpikeEntry gives the following epochs:
//										//    [ fixation_pt_on, eye_in_succeed, stim, isi, ... (repeat x numStims), done_last_isi_to_task_end ]
//										//    so to index the stimuli we skip the first 2 and do every other for as many stims as we present in a trial
//
//				// first get blank stim FR data:
//				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
//					stimObjId = trialSpec.getStimObjId(n);
//					spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(stimObjId).getSpec());
//					
//					if (spec.isBlankStim()) {
//						entIdx = 2*n+2;
//						blankFRs.add(ent.getSpikePerSec(entIdx)); 
//					}
//				}
//			}
//			
//			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
//			{
//				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
//				taskId = ent.getTaskId();
//
//				System.out.println("Entering spike info for trial: " + taskId);
//				
//				// get TrialSpec:
//				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
//				
//				// for each stimObj in the trial get FR data for all stims and save:
//				long stimObjId;
//				SachStimDataEntry data;
//				int entIdx;
//
//				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
//					stimObjId = trialSpec.getStimObjId(n);
//					data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
//					
//					// add acq info:					
//					entIdx = 2*n+2;
//					data.addTaskDoneId(taskId);
//					data.setSampleFrequency(ent.getSampleFrequency());
//					data.addSpikesPerSec(ent.getSpikePerSec(entIdx));
//					data.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
//					data.addTrialStageData(ent.getTrialStageData(entIdx));
//					
//					// resave data:
//					dbUtil.updateStimObjData(stimObjId, data.toXml());
//				}
//			}	
//		} catch(InvalidAcqDataException ee) {
//			ee.printStackTrace();
//		} catch(NoMoreAcqDataException ee) {
//			ee.printStackTrace();
//		}
//	}
	
	
	private void showGAanalysis(long genId) {
//		final GAGenAnalysisPlot plotGA = new GAGenAnalysisPlot(genId,dbUtil);
		plotGA.run(genId);
		plotGA.pack();
		RefineryUtilities.centerFrameOnScreen(plotGA);
		plotGA.setVisible(true);
	}


	// TODO: the following two functions are for getting spike data from trialIds, though now I just use the stimObjIds lcoally 
	//  want this to return a list of SachStimDataEntry, 
	//  return double[][] {{mean0,stdev0},{mean1,stdev1},...} from SachStimDataEntry list
	
//	private void getStimObjData(long currentGen) {
//		
//		// need StimObjIds from genId:
//		
//		// get StimObjData for each StimObjIds
//		
//	}
//	
//	private void getSpikesFromStimObjData(List<SachStimDataEntry> data) {
//	}
	
	public List<Long> chooseStimsToMorph(Map<Long,Double> stim2FRmap, int numStimsToChoose, int method) {
		// choose which stimuli should be morphed based on thier firing rates
		// --- methods: 1 = by fixed probabilities by quintile
		// 				2 = by distance in firing rate space
		
		List<Long> stimsToMorph = new ArrayList<Long>();					// output array of stimObjIds to morph
		List<Long> allStimIds = new ArrayList<Long>(stim2FRmap.keySet());	// array of all available stimObjIds
		int numStims = allStimIds.size();
		long stimId = -1;
		Map<Long, Double> stimFitness = new HashMap<Long, Double>();		// the fitness value for each stim
		
		switch (method) {
		case 1:	// by fixed probabilities by quintile

			// sort stims by FR:
			stim2FRmap =  SachMapUtil.sortByValue(stim2FRmap);			
			allStimIds = new ArrayList<Long>(stim2FRmap.keySet());	// redo after sort

			// divide stims into percentiles by FR:
				//	double[] percDivs = {0.3,0.5,0.7,0.9,1.0}; 		
				//	double[] fracPerPercDiv = {0.1,0.15,0.2,0.2,0.35};
			
			// find rank order stim divisions: (***must be at least 6 stims for this to work given current percDivs***)
			int numPercDivs = GA_percDivs.length;
			int[] stimsDivs = new int[numPercDivs];
			for (int n=0;n<numPercDivs;n++) {
				stimsDivs[n] = (int)Math.round(numStims*GA_percDivs[n]);
			}
			//System.out.println(stim2FRmap);
			//System.out.println(Arrays.toString(stimsDivs));

			// assign probability based on FR quintile:
			int prevStimDiv, thisStimDiv;
			for (int n=0;n<numStims;n++) {
				stimId = allStimIds.get(n);
				prevStimDiv = 0;
				for (int m=0;m<numPercDivs;m++) {
					thisStimDiv = stimsDivs[m];
					if (stimsDivs[m] > n) {
						stimFitness.put(stimId,GA_fracPerPercDiv[m]/(thisStimDiv-prevStimDiv));
						break;
					}
					prevStimDiv = thisStimDiv;
				}
			}
			//System.out.println(stimFitness);

			break;
			
		case 2:	// by distance in firing rate space
			
			// -- find distance in FR space for each stim:
			double FRdist;
			for (int i=0;i<numStims;i++) {
				FRdist = 0;
				for (int j=0;j<numStims;j++) {
					// abs(FR of stim i - FR of stim j)
					FRdist += Math.abs(stim2FRmap.get(allStimIds.get(i))-stim2FRmap.get(allStimIds.get(j)));
				}
				stimFitness.put(allStimIds.get(i), FRdist);
			}

			// -- convert to fitness metric (normalize):
			// find total distance:
			double totalDist = 0;
			for (int n=0;n<numStims;n++) {
				totalDist += stimFitness.get(allStimIds.get(n));
			}
			// normalize:
			for (int n=0;n<numStims;n++) {
				stimId = allStimIds.get(n);
				stimFitness.put(stimId,stimFitness.get(stimId)/totalDist);
			}

//			// check that they add to 1:
//			totalDist = 0;
//			for (int n=0;n<numStims;n++) {
//				totalDist += stimFitness.get(allStimIds.get(n));
//			}
//			System.out.println("tot= " + totalDist);
			
			break;
			
		default:
			
			break;
		} // end switch
		
		
		// -- use fitness to choose stims:
		double x;
		double tmp;
		for (int n=0;n<numStimsToChoose;n++) {
			x = Math.random();
			tmp = 0;
			for (int m=0;m<numStims;m++) {
				stimId = allStimIds.get(m);
				tmp += stimFitness.get(stimId);
				if (x <= tmp) break;
			}
			stimsToMorph.add(stimId);
		}
		
//		// check proportions of each stimuli chosen: (for debugging)
//		int[] counts = new int[numStims];		
//		for (int n=0;n<numStimsToChoose;n++) {
//			stimId = stimsToMorph.get(n);
//			for (int m=0;m<numStims;m++) {
//				if (allStimIds.get(m) == stimId) {
//					counts[m]++;
//					break;
//				}
//			}
////			int idx = (int)(stimId-10001);
////			counts[idx]++;
//		}
//		Map<Long,Double> newMap = new TreeMap<Long,Double>(stim2FRmap);
//		System.out.println(newMap);
//		System.out.println(newMap.values());
//		System.out.println(Arrays.toString(counts));
		
		return stimsToMorph;
	}
		
	public List<Long> chooseStimsToMorph_old(Map<Long,Double> stim2FRmap, int numStimsToChoose, int method) {
		// choose which stimuli should be morphed based on thier firing rates
		// --- methods: 1 = by fixed probabilities by quintile
		// 				2 = by distance in firing rate space
		
		
		List<Long> stimsToMorph = new ArrayList<Long>();					// output array of stimObjIds to morph
		List<Long> allStimIds = new ArrayList<Long>(stim2FRmap.keySet());	// array of all available stimObjIds
		int numStims = allStimIds.size();
		long stimId = -1;
		Map<Long, Double> stimFitness = new HashMap<Long, Double>();		// the fitness value for each stim
		
		switch (method) {
		case 1:	// by fixed probabilities by quintile
			
//			// sort stims by FR:
//			stim2FRmap = SachMapUtil.sortByValue(stim2FRmap);			
			
			// divide stims into percentiles by FR:
				//	double[] percentileDivs = {30,50,70,90,100}; 		
				// 	Groups: [0-30), [30-50), [50-70), [70-90), [90-100]. 
				//	double[] fracOfNewStimPerPercentileGroup = {0.1,0.15,0.2,0.2,0.35};
			
			// -- normalize FR:
			// find max FR:
			double maxFR = stimFitness.get(allStimIds.get(0));
			double tmp;
			for (int n=1;n<numStims;n++) {
				tmp = stimFitness.get(allStimIds.get(n));
				if (tmp > maxFR) {
					maxFR = tmp;
				}
			}
			// normalize:
			for (int n=0;n<numStims;n++) {
				stimId = allStimIds.get(n);
				stimFitness.put(stimId,stimFitness.get(stimId)/maxFR);
			}

			// -- assign probability based on FR quintile:
			double normFR;
			for (int n=0;n<numStims;n++) {
				stimId = allStimIds.get(n);
				normFR = stimFitness.get(stimId);
				
				for (int m=0;m<GA_percDivs.length;m++) {
					if (normFR < GA_percDivs[m]) {
						stimFitness.put(stimId,GA_fracPerPercDiv[m]);
					}
				}
			}
			
			break;
			
		case 2:	// by distance in firing rate space
			// -- find distance in FR space for each stim:
			double FRdist;
			for (int i=0;i<numStims;i++) {
				FRdist = 0;
				for (int j=0;j<numStims;j++) {
					// abs(FR of stim i - FR of stim j)
					FRdist += Math.abs(stim2FRmap.get(allStimIds.get(i))-stim2FRmap.get(allStimIds.get(j)));
				}
				stimFitness.put(allStimIds.get(i), FRdist);
			}

			// -- convert to fitness metric (normalize):
			// find total distance:
			double totalDist = 0;
			for (int n=0;n<numStims;n++) {
				totalDist += stimFitness.get(allStimIds.get(n));
			}
			// normalize:
			for (int n=0;n<numStims;n++) {
				stimId = allStimIds.get(n);
				stimFitness.put(stimId,stimFitness.get(stimId)/totalDist);
			}

//			// check that they add to 1:
//			totalDist = 0;
//			for (int n=0;n<numStims;n++) {
//				totalDist += stimFRdist.get(stimIds.get(n));
//			}
//			System.out.println("tot= " + totalDist);
			
			break;
		}
		
		
		// -- use fitness to choose stims:
		double x;
		double tmp;
		for (int n=0;n<numStimsToChoose;n++) {
			x = Math.random();
			tmp = 0;
			for (int m=0;m<numStims;m++) {
				stimId = allStimIds.get(m);
				tmp += stimFitness.get(stimId);
				if (x < tmp) break;
			}
			stimsToMorph.add(stimId);
		}
		
//		// check proportions of each stimuli chosen:
//		int[] counts = new int[4];		
//		for (int n=0;n<numStimsToChoose;n++) {
//			stimId = stimsToMorph.get(n);
//			int idx = (int)(stimId-10001);
//			counts[idx]++;
//		}
//		System.out.println(Arrays.toString(counts));
		
		return stimsToMorph;
	}
	
	
	private void getGenId() {
		// get genId from db:
		try {
			genId = dbUtil.readReadyGenerationInfo().getGenId() + 1;
//			System.out.println("(genId=" + genId + ") ");
		} catch (VariableNotFoundException e) {
			dbUtil.writeReadyGenerationInfo(genId, 0);
		}
	}
		
	private boolean isRealExpt() {
//		char c = SachIOUtil.prompt("If this is a real experiment press (y) if not press (n)");
//		if (c != 'y') return false;	
//		return true;
		return SachIOUtil.promptReturnBoolean("If this is a real experiment press (y) if not press (n)");
	}
	
	private void writeExptStart() {
		writeExptLogMsg("START");
	}
	
	private void writeExptStop() {
		writeExptLogMsg("STOP");
	}
	
	private void writeExptGenDone() {
		writeExptLogMsg("GEN_DONE");
	}
	
//	private void writeExptLogMsg(String memo) {
//		// write ExpLog message
//		long tstamp = globalTimeUtil.currentTimeMicros();
//		memo = trialType.toString() + memo;
//		if (!realExp) memo = "MOCK_" + memo;
//		dbUtil.writeExpLog(tstamp,memo);
//	}
	
//	private void writeExptLogMsg(String status) {
//		// write ExpLog message
//		long tstamp = globalTimeUtil.currentTimeMicros();
//		SachExpLogMessage msg = new SachExpLogMessage(status,trialType.toString(),thisGenId,genId,realExp,tstamp);
//		dbUtil.writeExpLog(tstamp,SachExpLogMessage.toXml(msg));
//		
//		//SachTrialOutcomeMessage.toXml(new SachTrialOutcomeMessage(timestamp,"PASS",taskId))
//	}
	
	private void writeExptLogMsg(String status) {
		// write ExpLog message
		long tstamp = globalTimeUtil.currentTimeMicros();
//		SachExpLogMessage msg = new SachExpLogMessage(status,trialType.toString(),thisGenId,genId,realExp,tstamp);
//		System.out.print(readActiveCellNum() + "\n");
//		System.out.print(readActiveCellDistance() + "\n");
//		SachExpLogMessage msg = new SachExpLogMessage(status,trialType.toString(),thisGenId,genId,-1,realExp,tstamp);
		SachExpLogMessage msg = new SachExpLogMessage(status,trialType.toString(),readActiveCellDistance(),
				                    thisGenId,genId,firstGenId,readActiveCellNum(),realExp,tstamp);
//		dbUtil.writeExpLog(tstamp,trialType.toString(),status,genId,thisGenId,realExp,SachExpLogMessage.toXml(msg));
// JK 1/7/2016 uncommented the .writeExpLog()		
		dbUtil.writeLine("ExpLog",msg.dbObjectList());
	}
	
	public void stopPrevExp(){
		// stop any previous Experiments that were prematurely stopped
		SachExpLogMessage msg = new SachExpLogMessage(dbUtil.toArray(dbUtil.readColsString("ExpLog",new String[]{})));
		if (!msg.getStatus().equals("STOP")) {
			long tstamp = globalTimeUtil.currentTimeMicros();
			msg.setStatus("STOP");
			msg.setTimestamp(tstamp);
			dbUtil.writeLine("ExpLog",msg.dbObjectList());
			
			genId = msg.getGlobalGenId() +1;  //this will apply if the previous expt was arrested BEFORE updateGenInfo occured
		}
		System.out.println("(genId=" + genId + ") ");
	}
	
	public boolean strFind(String line, String pattern){
		return line.matches(".*" + pattern + ".*");
//		Pattern p = Pattern.compile(pattern);
//		Matcher m = p.matcher(line);
//		return m.find();
	}
	public String strFind_PullNext(String line,String pattern){
		if (!strFind(line,pattern))
			return "";
		String[] split =  line.split(pattern);
		String[] split2 = split[1].split(";");
		return split2[0];
	}
	public Integer strFind_PullNextInt(String line,String pattern){
		String str = strFind_PullNext(line,pattern);
		if (str.isEmpty())
			return -1;
		else
			return Integer.parseInt(str);
	}
	
	
	// ---------------------------
	// ---- Getters & Setters ----
	// ---------------------------
	
	public SachDbUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
		pngMaker = new PNGmaker(dbUtil);
	}

	public TimeUtil getGlobalTimeUtil() {
		return globalTimeUtil;
	}

	public void setGlobalTimeUtil(TimeUtil globalTimeUtil) {
		this.globalTimeUtil = globalTimeUtil;
	}

	public SachExptSpecGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(SachExptSpecGenerator generator) {
		this.generator = generator;
	}
	
	public AbstractRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(AbstractRenderer renderer) {
		this.renderer = renderer;
	}
	
	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	
}
