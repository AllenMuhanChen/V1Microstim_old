package org.xper.sach;


import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.xper.Dependency;
import org.xper.classic.SlideRunner;
import org.xper.classic.TrialDrawingController;
import org.xper.classic.TrialEventListener;
import org.xper.classic.TrialRunner;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.classic.vo.TrialResult;
import org.xper.drawing.Coordinates2D;
import org.xper.exception.XmlDocInvalidFormatException;
import org.xper.experiment.Experiment;
import org.xper.experiment.ExperimentTask;
import org.xper.experiment.TaskDoneCache;
import org.xper.eye.EyeMonitor;
import org.xper.eye.EyeTargetSelector;
import org.xper.eye.EyeTargetSelectorConcurrentDriver;
import org.xper.eye.TargetSelectorResult;
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
	int blankTargetScreenDisplayTime; // in milli seconds
	
	@Dependency
	int earlyTargetFixationAllowableTime; // in milli seconds

	public int getEarlyTargetFixationAllowableTime() {
		return earlyTargetFixationAllowableTime;
	}

	public void setEarlyTargetFixationAllowableTime(
			int earlyTargetFixationAllowableTime) {
		this.earlyTargetFixationAllowableTime = earlyTargetFixationAllowableTime;
	}

	ThreadHelper threadHelper = new ThreadHelper("SachExperiment", this);

	public boolean isRunning() {
		return threadHelper.isRunning();
	}

	public void start() {
		threadHelper.start();
	}

	public void run() {
		TrialExperimentUtil.run(stateObject, threadHelper, new TrialRunner() {

			public TrialResult runTrial() {
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

					// initialized context
					SachTrialContext context = new SachTrialContext();
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
					
					Coordinates2D targetPosition = SachXmlUtil.getTargetPosition(stateObject.getCurrentSpecDoc());
					double targetEyeWinSize = SachXmlUtil.getTargetEyeWinSize(stateObject.getCurrentSpecDoc());
					long targetIndex = SachXmlUtil.getTargetIndex(stateObject.getCurrentSpecDoc());
					context.setTargetPos(targetPosition);
					context.setTargetEyeWindowSize(targetEyeWinSize);
					context.setTargetIndex(targetIndex);
					
					long reward = SachXmlUtil.getReward(stateObject.getCurrentSpecDoc());
					context.setReward(reward);
					
					// first object animated?
					Node objectNode = (Node)objectNodeList.get(0);
					stateObject.setAnimation(XmlUtil.isAnimation(objectNode));								

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

									try {
										int interSlideInterval = stateObject.getInterSlideInterval();
										int slideLength = stateObject.getSlideLength();
										for (int i = 0; i < countObject; i++) {
											if (i == 0) {
												stateObject.setInterSlideInterval(firstSlideISI);
												stateObject.setSlideLength(firstSlideLength);
											} else {
												stateObject.setInterSlideInterval(interSlideInterval);
												stateObject.setSlideLength(slideLength);
											}
											// show first slide, it's already draw in drawingController while waiting for money fixation
											TrialResult result = TrialExperimentUtil.doSlide(i, stateObject);
											
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
															return result;
														}
													}
												} else {
													return result;
												}
											}
											
											// if target index is -1, there are no targets, so monkey should maintain fixation during inter trial interval
											if (SachExperimentUtil.isTargetOn(currentContext) && currentContext.getTargetIndex() >= 0) {
												long targetOnLocalTime = currentContext.getCurrentSlideOffTime();
												currentContext.setTargetOnTime(targetOnLocalTime);
												SachEventUtil.fireTargetOnEvent(targetOnLocalTime, trialEventListeners, currentContext);
												
												// for the target screen, test for saccade, otherwise wait
												ThreadUtil.sleep(stateObject.getTargetSelectionStartDelay());
												
												EyeTargetSelectorConcurrentDriver selectorDriver = new EyeTargetSelectorConcurrentDriver(targetSelector, timeUtil);
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
												/*long targetInitialSelectionLocalTime = timeUtil.currentTimeMicros();
												currentContext.setTargetInitialSelectionTime(targetInitialSelectionLocalTime);
												SachEventUtil.fireTargetInitialSelectionEvent( targetInitialSelectionLocalTime,
																trialEventListeners, currentContext);*/

												TargetSelectorResult selectorResult = selectorDriver.getResult();
												if (selectorResult.getSelectionStatusResult() != TrialResult.TARGET_SELECTION_DONE) {
													TrialExperimentUtil.breakTrial(stateObject);
													return selectorResult.getSelectionStatusResult();
												}
												
												long targetSelectionSuccessLocalTime = timeUtil.currentTimeMicros();
												currentContext.setTargetSelectionSuccessTime(targetSelectionSuccessLocalTime);
												SachEventUtil.fireTargetSelectionSuccessEvent(targetSelectionSuccessLocalTime, trialEventListeners, currentContext);
												
												// clear target
												((DefaultSachTrialDrawingController)drawingController).targetSelectionDone(currentTask, currentContext);
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
											
											// inter slide interval
											result = TrialExperimentUtil.waitInterSlideInterval(stateObject,threadHelper);
											if (result != TrialResult.SLIDE_OK) {
												return result;
											}
										}
										
										stateObject.setInterSlideInterval(interSlideInterval);
										stateObject.setSlideLength(slideLength);

										// trial finished successfully
										// set task to null so that it won't get repeated.
										if (currentTask != null) {
											taskDoneCache.put(currentTask,globalTimeClient.currentTimeMicros(),false);
											currentTask = null;
											stateObject.setCurrentTask(currentTask);
										}

										return TrialResult.TRIAL_COMPLETE;
									} finally {
										try {
											// Do not repeat task.
											stateObject.setCurrentTask(null);
											TrialExperimentUtil.cleanupTask(stateObject);
										} catch (Exception e) {
											logger.warn(e.getMessage());
											e.printStackTrace();
										}
									}
								}
							});
					return ret;
				} finally {
					try {
						// repeat if INITIAL_EYE_IN_FAIL or EYE_IN_HOLD_FAIL, otherwise do not repeat
						if (ret != TrialResult.INITIAL_EYE_IN_FAIL && ret != TrialResult.EYE_IN_HOLD_FAIL) {
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
}
