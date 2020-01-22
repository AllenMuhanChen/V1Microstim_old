package org.xper.matchchoice;

import java.util.List;

import org.apache.log4j.Logger;
import org.xper.Dependency;
import org.xper.classic.SlideRunner;
import org.xper.classic.TrialEventListener;
import org.xper.classic.TrialRunner;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.classic.vo.TrialResult;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.experiment.Experiment;
import org.xper.experiment.ExperimentTask;
import org.xper.experiment.TaskDoneCache;
import org.xper.eye.EyeTargetSelector;
import org.xper.matchchoice.util.MatchChoiceEventUtil;
import org.xper.matchchoice.util.MatchChoiceXmlUtil;
import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.matchchoice.vo.MatchChoiceTrialExperimentState;
import org.xper.time.TimeUtil;
import org.xper.util.ThreadHelper;
import org.xper.util.ThreadUtil;
import org.xper.util.TrialExperimentUtil;
import org.xper.util.XmlUtil;

/**
 * Format of StimSpec:
 * 
 * 
 * <StimSpec> <object1 animation="false"> ... </object1> <object2
 * animation="false"> ... </object2> <reward>1</reward>
 * <size1></size1><size2><size2>
 * <distance1></distance1><distance2><distance2>
 * <color1></color1><color2></color2>
 * </StimSpec>
 * 
 * <reward>index</reward> : if the monkey selected the indicated object, reward is delivered. 
 * 
 * If attribute animation is false or missing, the object is treated as a static
 * slide.
 * 
 * @author wang
 * 
 */

public class MatchChoiceTrialExperiment implements Experiment {
	static Logger logger = Logger.getLogger(MatchChoiceTrialExperiment.class);

	@Dependency
	MatchChoiceTrialExperimentState stateObject;

	ThreadHelper threadHelper = new ThreadHelper("MatchChoiceTrialExperiment", this);

	public boolean isRunning() {
		return threadHelper.isRunning();
	}

	public void start() {
		threadHelper.start();
	}

	public void run() {
		TrialExperimentUtil.run(stateObject, threadHelper, new TrialRunner() {

			public TrialResult runTrial() {
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

					stateObject.setCurrentSpecDoc(XmlUtil.parseSpec(stateObject
							.getCurrentTask().getStimSpec()));

					// initialized context
					stateObject.setCurrentContext(new MatchChoiceTrialContext());
					stateObject.setAnimation(XmlUtil.isAnimation(stateObject
							.getCurrentSpecDoc(), "/StimSpec/object1"));

					// run task
					return TrialExperimentUtil.runTrial(stateObject,
							threadHelper, new SlideRunner() {
								protected void prepareChoice(
										MatchChoiceTrialContext context) {
									double outDistance1 = MatchChoiceXmlUtil.getChoiceDistance(stateObject.getCurrentSpecDoc(), 
																	"StimSpec/distance1");
									double outDistance2 = MatchChoiceXmlUtil.getChoiceDistance(stateObject.getCurrentSpecDoc(), 
																	"StimSpec/distance2");
									
									double targetSize1 = MatchChoiceXmlUtil.getChoiceSize(stateObject.getCurrentSpecDoc(), 
																	"StimSpec/size1");
									double targetSize2 = MatchChoiceXmlUtil.getChoiceSize(stateObject.getCurrentSpecDoc(), 
																	"StimSpec/size2");
									RGBColor color1 = MatchChoiceXmlUtil.getChoiceColor(stateObject.getCurrentSpecDoc(), 
																	"StimSpec/color1");
									RGBColor color2 = MatchChoiceXmlUtil.getChoiceColor(stateObject.getCurrentSpecDoc(), 
																	"StimSpec/color2");
									double targetEyeWindowSize = stateObject.getChoiceTargetEyeWindowSize();

									double angle = Math
											.floor(Math.random() * 8.0)
											* Math.PI / 4.0;
									double x1 = outDistance1 * Math.cos(angle);
									double y1 = outDistance1 * Math.sin(angle);

									double x2 = outDistance2
											* Math.cos(angle + Math.PI);
									double y2 = outDistance2
											* Math.sin(angle + Math.PI);

									context.getTargetPos()[0] = new Coordinates2D(
											x1, y1);
									context.getTargetPos()[1] = new Coordinates2D(
											x2, y2);
									context.getTargetSize()[0] = targetSize1;
									context.getTargetSize()[1] = targetSize2;
									
									context.getTargetColor()[0] = color1;
									context.getTargetColor()[1] = color2;
									
									context.getTargetEyeWindowSize()[0] = targetEyeWindowSize;
									context.getTargetEyeWindowSize()[1] = targetEyeWindowSize;

									context.setReward(MatchChoiceXmlUtil.getChoiceReward(stateObject.getCurrentSpecDoc(),
													"/StimSpec/reward"));
								}

								public TrialResult runSlide() {
									MatchChoiceTrialDrawingController drawingController = (MatchChoiceTrialDrawingController) stateObject
											.getDrawingController();
									ExperimentTask currentTask = stateObject
											.getCurrentTask();
									MatchChoiceTrialContext currentContext = (MatchChoiceTrialContext) stateObject
											.getCurrentContext();
									TaskDoneCache taskDoneCache = stateObject
											.getTaskDoneCache();
									TimeUtil globalTimeClient = stateObject
											.getGlobalTimeClient();
									TimeUtil timeUtil = stateObject
											.getLocalTimeUtil();
									EyeTargetSelector targetSelector = stateObject
											.getTargetSelector();
									List<? extends TrialEventListener> trialEventListeners = stateObject
											.getTrialEventListeners();

									try {
										for (int i = 0; i < MatchChoiceTrialExperimentState.SLIDE_PER_TRIAL; i++) {
											// do first object
											TrialResult result = TrialExperimentUtil
													.doSlide(i, stateObject);
											if (result != TrialResult.SLIDE_OK) {
												return result;
											}

											if (i < MatchChoiceTrialExperimentState.SLIDE_PER_TRIAL - 1) {
												// prepare second object
												stateObject.setAnimation(
														XmlUtil.isAnimation(stateObject.getCurrentSpecDoc(), "/StimSpec/object2"));
												currentContext.setSlideIndex(i + 1);
												drawingController.prepareNextSlide(currentTask, currentContext);
											} else {
												// prepare choice targets
												prepareChoice(currentContext);
												drawingController.prepareTarget((MatchChoiceTrialContext) currentContext);
											}

											// inter slide interval
											result = TrialExperimentUtil.waitInterSlideInterval(stateObject, threadHelper);
											if (result != TrialResult.SLIDE_OK) {
												return result;
											}
										}
										// draw targets
										drawingController.targetOn(currentContext);
										long targetOnLocalTime = timeUtil.currentTimeMicros();
										currentContext.setTargetOnTime(targetOnLocalTime);
										MatchChoiceEventUtil.fireTargetOnEvent(targetOnLocalTime,trialEventListeners,currentContext);

										ThreadUtil.sleep(stateObject.getTargetSelectionStartDelay());
										// monkey select target
										int sel = targetSelector.waitInitialSelection(
														currentContext.getTargetPos(),
														currentContext.getTargetEyeWindowSize(),
														targetOnLocalTime + stateObject.getTimeAllowedForInitialTargetSelection() * 1000
														+ stateObject.getTargetSelectionStartDelay() * 1000);
										if (sel < 0) {
											TrialExperimentUtil.breakTrial(stateObject);
											return TrialResult.EYE_BREAK;
										}

										// monkey fixate target
										drawingController.targetSelected(
												currentContext, sel);
										long targetInitialSelectionLocalTime = timeUtil
												.currentTimeMicros();
										currentContext.setTargetInitialSelectionTime(targetInitialSelectionLocalTime);
										MatchChoiceEventUtil
												.fireTargetInitialSelectionEvent(
														targetInitialSelectionLocalTime,
														trialEventListeners,
														sel, currentContext);

										boolean success = targetSelector.waitEyeHold(sel,
														targetInitialSelectionLocalTime
																+ stateObject.getRequiredTargetSelectionHoldTime()
																* 1000);
										if (!success) {
											TrialExperimentUtil.breakTrial(stateObject);
											return TrialResult.EYE_BREAK;
										}

										// choice selection done
										currentContext.setTargetSelected(sel);
										long targetSelectionSuccessLocalTime = timeUtil
												.currentTimeMicros();
										currentContext.setTargetSelectionSuccessTime(targetSelectionSuccessLocalTime);
										MatchChoiceEventUtil.fireTargetSelectionSuccessEvent(
														targetSelectionSuccessLocalTime,
														trialEventListeners,
														sel, currentContext);

										// trial finished successfully
										if (currentTask != null) {
											taskDoneCache.put(currentTask,
															globalTimeClient.currentTimeMicros(),
															false);
											currentTask = null;
											stateObject.setCurrentTask(currentTask);
										}

										return TrialResult.TRIAL_COMPLETE;
									} finally {
										try {
											TrialExperimentUtil.cleanupTask(stateObject);
										} catch (Exception e) {
											logger.warn(e.getMessage());
											e.printStackTrace();
										}
									}
								}
							});

				} finally {
					try {
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
		System.out.println("Stopping MatchChoiceTrialExperiment ...");
		if (isRunning()) {
			threadHelper.stop();
			threadHelper.join();
		}
	}

	public void setPause(boolean pause) {
		stateObject.setPause(pause);
	}

	public MatchChoiceTrialExperimentState getStateObject() {
		return stateObject;
	}

	public void setStateObject(MatchChoiceTrialExperimentState stateObject) {
		this.stateObject = stateObject;
	}
}
