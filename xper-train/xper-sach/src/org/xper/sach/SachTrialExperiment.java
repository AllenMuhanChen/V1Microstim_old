package org.xper.sach;


import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.xper.Dependency;
import org.xper.classic.SlideRunner;
import org.xper.classic.TrialDrawingController;
import org.xper.classic.TrialEventListener;
import org.xper.classic.TrialRunner;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.classic.vo.TrialResult;
import org.xper.config.BaseConfig;
import org.xper.drawing.Coordinates2D;
import org.xper.exception.XmlDocInvalidFormatException;
import org.xper.experiment.Experiment;
import org.xper.experiment.ExperimentTask;
import org.xper.experiment.TaskDoneCache;
import org.xper.eye.EyeMonitor;
import org.xper.eye.EyeTargetSelector;
import org.xper.eye.EyeTargetSelectorConcurrentDriver;
import org.xper.eye.TargetSelectorResult;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachEventUtil;
import org.xper.sach.util.SachExperimentUtil;
import org.xper.sach.util.SachXmlUtil;
import org.xper.sach.vo.SachExperimentState;
import org.xper.sach.vo.SachTrialContext;
import org.xper.time.TimeUtil;
import org.xper.util.ThreadHelper;
import org.xper.util.ThreadUtil;
import org.xper.util.TrialExperimentUtil;
import org.xper.util.XmlUtil;

//import org.xper.sach.somlab.ElectricalStimulator;
import org.xper.sach.somlab.SachEstimSpec;


/**
 * Format of StimSpec:
 * 
 * 
 * <StimSpec> 
 * 	<object animation="false"> ... </object> 
 * 	<object animation="false"> ... </object> 
 *  ... (one to ten objects)
 * 	<object animation="false"> ... </object>
 *  <targetPosition>...</targetPosition>
 *  <targetEyeWinSize>...</targetEyeWinSize>
 *  <targetIndex>...</targetIndex>
 *  <reward>...</reward>
 * </StimSpec>
 * 
 * If attribute animation is false or missing, the object is treated as a static
 * slide.
 * 
 * @author wang
 * 
 */

public class SachTrialExperiment implements Experiment {
	static Logger logger = Logger.getLogger(SachTrialExperiment.class);

	@Dependency
	SachExperimentState stateObject;
	
	@Dependency
	EyeMonitor eyeMonitor;
	
	@Dependency
	int firstSlideISI;
	
	@Dependency
	int firstSlideLength;
	
	@Dependency
	int blankTargetScreenDisplayTime; // in milliseconds
	
	@Dependency
	int earlyTargetFixationAllowableTime; // in milliseconds
	
	ThreadHelper threadHelper = new ThreadHelper("SachExperiment", this);
	
	// JK added all this to write the modified reward in the stimSpec back to the database
	@Autowired BaseConfig baseConfig;	
	SachDbUtil sdb = new SachDbUtil();
	boolean rewardIsDynamic = true;

	// JK advanceOnCorrectOnly
	boolean advanceOnCorrectOnly = false;
	
	// JK 25 July 2016
	// EStim
	boolean useEstim = false;
	
	boolean debugFlag = false;
	
	// JK
	//if(useEstim){
	//	ElectricalStimulator estim;
	//}
	
	
	// for dev
	SachEstimSpec estimSpec;
	
	public boolean isRunning() {
		return threadHelper.isRunning();
	}

	public void start() {
		threadHelper.start();
		
//		if(useEstim){
//			 estim = new ElectricalStimulator();
//			 
//			 estimSpec = new SachEstimSpec(true,    // eStimFlag
//						0, 	// objIdx
//						new int[]{0},    	// channel []
//						new int[]{0},     // baselineAmp []
//						true, //cathodalLeading 
//						new int[]{-250}, 	// cathodalAmp [] 
//						new int[]{250}, 	// anodalAmp[]
//				        new int[]{200}, 	// cathodalWidth []
//				        new int[]{0},  	//interPhaseDuration
//				        new double[]{200},     // pulseFreq []
//				    	50,   	// startOffset
//				    	70);	    // stopOffset
//				
//					
//			// JK where should this happen?
//			estim.configureStim(estimSpec);
//		}		
	}

	
	public void run() {
		// JK
		if (debugFlag)
			System.out.print("repIfEyeBreak: " + stateObject.isRepeatTrialIfEyeBreak() + "\n");
		
		stateObject.setRepeatTrialIfEyeBreak(false);
//		System.out.print(stateObject.isRepeatTrialIfEyeBreak());
		
		// JK  Make sure the database connection is valid
		if(true){
			sdb.setDataSource(baseConfig.dataSource());		
		}
				
		TrialExperimentUtil.run(stateObject, threadHelper, new TrialRunner() {

			public TrialResult runTrial() {
				// JK  
				//   update rewardIsDynamic
				Float temp = Float.parseFloat (sdb.readSystemVar("xper_training_reward_is_dynamic").get("xper_training_reward_is_dynamic").getValue(0));

				
				
				if(temp > 0.5f) {
					rewardIsDynamic = true;
				} else {
					rewardIsDynamic =  false;		
				}			
				TrialResult ret = TrialResult.INITIAL_EYE_IN_FAIL;
				try {
					// get a task
					TrialExperimentUtil.getNextTask(stateObject);

					if (stateObject.getCurrentTask() == null) {
						try {
							Thread.sleep(SlideTrialExperimentState.NO_TASK_SLEEP_INTERVAL);
						} catch (InterruptedException e) {
						}
						return TrialResult.NO_MORE_TASKS;
					}
					
					// parse and save the doc object for later use.
					stateObject.setCurrentSpecDoc(XmlUtil.parseSpec(stateObject.getCurrentTask().getStimSpec()));

					//System.out.println("JK25 SachTrialExperiment : doc = " + stateObject.getCurrentSpecDoc().asXML());
//					if(useEstim){	
//						estim.configureStim(estimSpec);
//					}

					// initialized context
					SachTrialContext context = new SachTrialContext();
					context.setCurrentTask(stateObject.getCurrentTask());	// add current task to context!
					stateObject.setCurrentContext(context);
					
					final List<?> objectNodeList = stateObject.getCurrentSpecDoc().selectNodes("/StimSpec/object");
					final int countObject = objectNodeList.size();
					if (countObject == 0) {
						throw new XmlDocInvalidFormatException("No objects in match task specification.");
					}
					context.setCountObjects(countObject);
					if (logger.isDebugEnabled()) {
						logger.debug(stateObject.getCurrentTask().getTaskId() + " " + countObject);
					}

					// target info -shs
					Coordinates2D targetPosition = SachXmlUtil.getTargetPosition(stateObject.getCurrentSpecDoc());
					double targetEyeWinSize = SachXmlUtil.getTargetEyeWinSize(stateObject.getCurrentSpecDoc());
					long targetIndex = SachXmlUtil.getTargetIndex(stateObject.getCurrentSpecDoc());
					context.setTargetPos(targetPosition);
					context.setTargetEyeWindowSize(targetEyeWinSize);
					context.setTargetIndex(targetIndex);

					// reward info -shs
					long reward = SachXmlUtil.getReward(stateObject.getCurrentSpecDoc());
					context.setReward(reward);
					
					// first object animated?
					Node objectNode = (Node)objectNodeList.get(0);
					stateObject.setAnimation(XmlUtil.isAnimation(objectNode));								

//					System.out.println("    AWC2 ==> SachTrialExperiment 215, pre-TrialExperimentUtil.runTrial(...)");
					// run task
					ret = TrialExperimentUtil.runTrial(stateObject,
							threadHelper, new SlideRunner() {

						public TrialResult runSlide() {
							TrialDrawingController drawingController = stateObject.getDrawingController();
							ExperimentTask currentTask = stateObject.getCurrentTask();
							SachTrialContext currentContext = (SachTrialContext) stateObject.getCurrentContext();
							TaskDoneCache taskDoneCache = stateObject.getTaskDoneCache();
							TimeUtil globalTimeClient = stateObject.getGlobalTimeClient();
							TimeUtil timeUtil = stateObject.getLocalTimeUtil();
							EyeTargetSelector targetSelector = stateObject.getTargetSelector();
							List<? extends TrialEventListener> trialEventListeners = stateObject.getTrialEventListeners();
							TrialResult result = TrialResult.FIXATION_SUCCESS;
							boolean behCorrect = true;

							// JK set the reward for this trial based on past performance and update the stimspec in the database
							if(rewardIsDynamic){
								long newReward = currentContext.getReward() * stateObject.getCorrectTrialCount();
								currentContext.setReward(newReward);					
								SachXmlUtil.setReward(stateObject.getCurrentSpecDoc(), newReward);
								sdb.updateStimSpec(currentTask.getTaskId(), stateObject.getCurrentSpecDoc().asXML());
							}
							
							long startTime = timeUtil.currentTimeMicros();
							long timeElapsed;
							
							try {
								for (int i = 0; i < countObject; i++) {
						
									// show first slide, it's already draw in drawingController while waiting for monkey fixation
									result = TrialExperimentUtil.doSlide(i, stateObject);
										
									// JK this might not be the best place
//									if(useEstim ){									
//										estim.triggerStim();
//									}
									
									if (result != TrialResult.SLIDE_OK) {
										if (SachExperimentUtil.isTargetOn(currentContext) && currentContext.getTargetIndex() >= 0) {
											if (earlyTargetFixationAllowableTime < 0) {
												// ok to break fixation
											} else {
												long currentTime = timeUtil.currentTimeMicros();
												long earliestTime = currentContext.getCurrentSlideOnTime() + stateObject.getSlideLength() * 1000 - 
														earlyTargetFixationAllowableTime * 1000;
												if (currentTime >= earliestTime) {
													// ok to break fixation
												} else {
													SachEventUtil.fireTrialBREAKEvent(timeUtil.currentTimeMicros(), trialEventListeners, currentContext,i,false);
													// JK fail ... reset trialCorrectCounter
													if(rewardIsDynamic){														
														stateObject.countCorrectTrial(false);						
													}													
												
													return result;
												}
											}
										} else {
											SachEventUtil.fireTrialBREAKEvent(timeUtil.currentTimeMicros(), trialEventListeners, currentContext,i,false);
											// JK fail ... reset trialCorrectCounter
											if(rewardIsDynamic){		
												stateObject.countCorrectTrial(false);		
											}												
											return result;
										}
									}
									
									if (i < countObject - 1){
										System.out.println("\n________\nFixation Success.  targetIndex: " + currentContext.getTargetIndex());
									}
									
									boolean saccadeDone = false;
									// Only enter the next section if this is a target trial or 50/50 (target index = 0 or 2)
									// if a maintain trial (target index is -1), the respons is handled afterwards during inter trial interval
										if (SachExperimentUtil.isTargetOn(currentContext) && currentContext.getTargetIndex() >= 0) {
											long targetOnLocalTime = currentContext.getCurrentSlideOffTime();
											currentContext.setTargetOnTime(targetOnLocalTime);
											SachEventUtil.fireTargetOnEvent(targetOnLocalTime, trialEventListeners, currentContext);
				
											// for the target screen, test for saccade, otherwise wait
											ThreadUtil.sleep(stateObject.getTargetSelectionStartDelay());
	
											EyeTargetSelectorConcurrentDriver selectorDriver = new EyeTargetSelectorConcurrentDriver(targetSelector, timeUtil);
											
//											System.out.println("Presenting target. Init Target Selection delay: " + stateObject.getTargetSelectionStartDelay());
											selectorDriver.start(new Coordinates2D[] {currentContext.getTargetPos()},
													new double[] {currentContext.getTargetEyeWindowSize()}, 
													currentContext.getTargetOnTime() + stateObject.getTimeAllowedForInitialTargetSelection() * 1000
													+ stateObject.getTargetSelectionStartDelay() * 1000, 
													stateObject.getRequiredTargetSelectionHoldTime() * 1000);
	
											/*
												xper_blank_target_screen_display_time has to be smaller than xper_time_allowed_for_initial_target_selection.
												Otherwise the target screen won't be shown. 
											 */
											boolean targetShown = false;
											while (!selectorDriver.isDone()) {
												if (!targetShown) {
													if (timeUtil.currentTimeMicros() > targetOnLocalTime + blankTargetScreenDisplayTime * 1000) {
														((DefaultSachTrialDrawingController)drawingController).showTarget(currentTask, currentContext);
														targetShown = true;
													}
												}
											}
	
											selectorDriver.stop();
	
											// monkey fixate target. These information won't be available when the target selection is run in another thread.
											// the context object and the event listeners are not thread-safe.
											
											TargetSelectorResult selectorResult = selectorDriver.getResult();
										
											if (selectorResult.getSelectionStatusResult() != TrialResult.TARGET_SELECTION_DONE) {
												if (currentContext.getTargetIndex()==0){
													TrialExperimentUtil.breakTrial(stateObject);
													// shs -- print out elapsed target time here:
													long targetFailTime = timeUtil.currentTimeMicros();
													timeElapsed = timeUtil.currentTimeMicros() - startTime;
													behCorrect = false;
													
													if (debugFlag)
														System.out.println("Target Trial and no saccade: FIREFAIL. time elapsed: " + timeElapsed);
													
													SachEventUtil.fireTrialTARGETFAILEvent(targetFailTime, trialEventListeners, currentContext,selectorResult.getSelectionStatusResult(),targetOnLocalTime);
													SachExperimentUtil.waitTimeoutPenaltyDelay(stateObject, threadHelper);
													
													// JK fail ... reset trialCorrectCounter
													if(rewardIsDynamic){		
														stateObject.countCorrectTrial(false);															
													}
													
													// JK	
													if(advanceOnCorrectOnly){											
														TrialExperimentUtil.cleanupTask(stateObject);											
													}

													timeElapsed = timeUtil.currentTimeMicros() - startTime;
													if (debugFlag)
														System.out.println("Target Trial and no saccade: penalty given. time elapsed: " + timeElapsed + "\n___________");
													return selectorResult.getSelectionStatusResult();
												}
												else {
													if (debugFlag)
														System.out.println("50/50 Trial and no saccade. do isi");
												}
											}
											else {
												if (debugFlag)
													System.out.println("Saccade detected, skip isi");
												saccadeDone = true;
											}
	
											long targetSelectionSuccessLocalTime = timeUtil.currentTimeMicros();
											// shs -- print out elapsed target time here:
											currentContext.setTargetSelectionSuccessTime(targetSelectionSuccessLocalTime);
											SachEventUtil.fireTargetSelectionSuccessEvent(targetSelectionSuccessLocalTime, trialEventListeners, currentContext);
											SachEventUtil.fireTrialTARGETPASSEvent(targetSelectionSuccessLocalTime, trialEventListeners, currentContext, stateObject.getRequiredTargetSelectionHoldTime(), targetOnLocalTime);
											
											// JK pass ... trialCorrectCounter
											if(rewardIsDynamic){		
												stateObject.countCorrectTrial(true);
											}										
	
											// clear target
											((DefaultSachTrialDrawingController)drawingController).targetSelectionDone(currentTask, currentContext);
										}  // if there are targets (not maintain trials)
										else {
											if (debugFlag && (i == countObject - 1)) {
												System.out.println("Maint Trial, enter isi");
											}
										}
							

									boolean doISI = false;
									
									if(!SachExperimentUtil.isTargetOn(currentContext)) {	
										doISI = true; // need to check this here because the context is updated in the next step
									}
									else {
										doISI = !saccadeDone;
									}
									
									if (i < countObject - 1) {
										// prepare second object
										stateObject.setAnimation(XmlUtil.isAnimation((Node)objectNodeList.get(i+1)));
										currentContext.setSlideIndex(i + 1);
										// setTask is being called in prepareNextSlide, which is redundant since we are not getting new tasks.
										// It was designed for classic experiment designs, which can have multiple tasks per trial with one slide per task.
										// This experiment scheme is doing one task per trial with multiple slides defined inside one task.
										// We still need to draw new objects for next slide by calling prepareNextSlide.
										drawingController.prepareNextSlide(currentTask, currentContext);
									}
									
									if (doISI) {	// this can only be entered during a maintain Trial or 50/50 trial with no saccade 
										// do inter slide interval
										// JK
										int origISI = stateObject.getInterSlideInterval();		
										int maintainHoldTime = (int)(stateObject.getMaintainHoldTime()); 	// xper_maintain_hold_time
														
										// HACK: modify the stateObject ISI to non-target fixation duration
										if (i == countObject-1) {
											timeElapsed = timeUtil.currentTimeMicros() - startTime;
											if (debugFlag)
												System.out.println("executing maintain period. time elapsed: " + timeElapsed + ". changing ISI from " + origISI + " to maintainHoldTime " + maintainHoldTime);
											stateObject.setInterSlideInterval(maintainHoldTime);
										} 
																				
											
										result = TrialExperimentUtil.waitInterSlideInterval(stateObject,threadHelper);
		
										if (result != TrialResult.SLIDE_OK) {
											if (i == countObject - 1){
												timeElapsed = timeUtil.currentTimeMicros() - startTime;
												behCorrect = false;
												if (debugFlag)
													System.out.println("failed maintain. FIREFAIL. time elapsed: " + timeElapsed);
												SachEventUtil.fireTrialFAILEvent(timeUtil.currentTimeMicros(), trialEventListeners, currentContext);
												SachExperimentUtil.waitTimeoutPenaltyDelay(stateObject, threadHelper);
											}

											else {
													SachEventUtil.fireTrialBREAKEvent(timeUtil.currentTimeMicros(), trialEventListeners, currentContext,i,true);
											}
												
											// JK fail ... reset trialCorrectCounter
											if(rewardIsDynamic){
												stateObject.countCorrectTrial(false);
											}

											if(advanceOnCorrectOnly){											
												TrialExperimentUtil.cleanupTask(stateObject);
											}
													
											timeElapsed = timeUtil.currentTimeMicros() - startTime;
											if (debugFlag)
												System.out.println("failed maintain. return. time elapsed: " + timeElapsed + "\n____________");
											stateObject.setInterSlideInterval(origISI);
											return result;
										}
											
										stateObject.setInterSlideInterval(origISI);
									} // end of "if doISI"
		
								} // end 'for' loop

								if (SachExperimentUtil.isLastSlide(currentContext) && !SachExperimentUtil.isTargetTrial(currentContext)) {	// shs
									SachEventUtil.fireTrialPASSEvent(timeUtil.currentTimeMicros(), trialEventListeners, currentContext);
													
									// JK pass ... trialCorrectCounter
									if(rewardIsDynamic){
										stateObject.countCorrectTrial(true);
									}
									
									
								}

								// trial finished successfully
								// set task to null so that it won't get repeated.
								if (currentTask != null) {
										taskDoneCache.put(currentTask,globalTimeClient.currentTimeMicros(),false);
										currentTask = null;
										stateObject.setCurrentTask(currentTask);			// not sure about this ....  
										
								}
								timeElapsed = timeUtil.currentTimeMicros() - startTime;
								if (debugFlag)
									System.out.println("trial passed! return\ntime elapsed: " + timeElapsed + "\n_____________");
								return TrialResult.TRIAL_COMPLETE;
							} finally {
								try {
									boolean repeatTrial = stateObject.isRepeatTrialIfEyeBreak() && (!behCorrect || (result == TrialResult.EYE_BREAK));
									if (!repeatTrial) stateObject.setCurrentTask(null);
									
									// Do not repeat task unless (repeatTrialIfEyeBreak=true & EYE_BREAK)
//									System.out.print("isRepeat: " + stateObject.isRepeatTrialIfEyeBreak() + ", eyebreak: " + TrialResult.EYE_BREAK + "\n");
//									if (!stateObject.isRepeatTrialIfEyeBreak() || result != TrialResult.EYE_BREAK) {
//										System.out.print("Not repeating....  repIfEyeBreak: " + stateObject.isRepeatTrialIfEyeBreak() + ", result: " + result + "\n");
//										stateObject.setCurrentTask(null); // Do not repeat task\
//									}
//									else System.out.print("repeating....  repIfEyeBreak: " + stateObject.isRepeatTrialIfEyeBreak() + ", result: " + result + "\n");
//									
									TrialExperimentUtil.cleanupTask(stateObject);
								} catch (Exception e) {
									logger.warn(e.getMessage());
									e.printStackTrace();
								}
							}
						}
					});		// end 'run task'
					
					return ret;
					
				} finally {
					//System.out.println(ret);	// for debugging
					try {
						// repeat if INITIAL_EYE_IN_FAIL or EYE_IN_HOLD_FAIL, otherwise do not repeat
						if (ret != TrialResult.INITIAL_EYE_IN_FAIL && ret != TrialResult.EYE_IN_HOLD_FAIL && ret != TrialResult.EYE_BREAK) {
							stateObject.setCurrentTask(null); // Do not repeat task
						}
						TrialExperimentUtil.cleanupTrial(stateObject);
					} catch (Exception e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					}
				}


			}
		});
	}

	public void stop() {
		System.out.println("Stopping SachTrialExperiment ...");
		
//		// JK
//		if(useEstim){
//			estim.shutdown();
//		}
		
		if (isRunning()) {
			threadHelper.stop();
			threadHelper.join();
		}
	}

	public void setPause(boolean pause) {
		stateObject.setPause(pause);
	}

	public SachExperimentState getStateObject() {
		return stateObject;
	}

	public void setStateObject(SachExperimentState stateObject) {
		this.stateObject = stateObject;
	}

	public EyeMonitor getEyeMonitor() {
		return eyeMonitor;
	}

	public void setEyeMonitor(EyeMonitor eyeMonitor) {
		this.eyeMonitor = eyeMonitor;
	}

	public int getFirstSlideISI() {
		return firstSlideISI;
	}

	public void setFirstSlideISI(int firstSlideISI) {
		this.firstSlideISI = firstSlideISI;
	}

	public int getFirstSlideLength() {
		return firstSlideLength;
	}

	public void setFirstSlideLength(int firstSlideLength) {
		this.firstSlideLength = firstSlideLength;
	}

	public int getBlankTargetScreenDisplayTime() {
		return blankTargetScreenDisplayTime;
	}

	public void setBlankTargetScreenDisplayTime(int blankTargetScreenDisplayTime) {
		this.blankTargetScreenDisplayTime = blankTargetScreenDisplayTime;
	}

	public int getEarlyTargetFixationAllowableTime() {
		return earlyTargetFixationAllowableTime;
	}

	public void setEarlyTargetFixationAllowableTime(
			int earlyTargetFixationAllowableTime) {
		this.earlyTargetFixationAllowableTime = earlyTargetFixationAllowableTime;
	}
}
