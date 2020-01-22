package org.xper.choice;

import java.util.List;

import org.apache.log4j.Logger;
import org.xper.Dependency;
import org.xper.choice.util.ChoiceEventUtil;
import org.xper.choice.util.ChoiceXmlUtil;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.choice.vo.ChoiceTrialExperimentState;
import org.xper.classic.SlideRunner;
import org.xper.classic.TrialEventListener;
import org.xper.classic.TrialRunner;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.classic.vo.TrialResult;
import org.xper.drawing.Coordinates2D;
import org.xper.experiment.Experiment;
import org.xper.experiment.ExperimentTask;
import org.xper.experiment.TaskDoneCache;
import org.xper.eye.EyeTargetSelector;
import org.xper.time.TimeUtil;
import org.xper.util.ThreadHelper;
import org.xper.util.TrialExperimentUtil;
import org.xper.util.XmlUtil;

/**
 * Format of StimSpec:
 * 
 * 
 * <StimSpec> <object1 animation="false"> ... </object1> <object2
 * animation="false"> ... </object2> <reward1>0x1</reward1> <reward2>0x2</reward2>
 * </StimSpec>
 * 
 * If attribute animation is false or missing, the object is treated as a static
 * slide.
 * 
 * @author wang
 * 
 */

public class ChoiceTrialExperiment implements Experiment {
	static Logger logger = Logger.getLogger(ChoiceTrialExperiment.class);

	@Dependency
	ChoiceTrialExperimentState stateObject;

	ThreadHelper threadHelper = new ThreadHelper("ChoiceTrialExperiment", this);

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
					stateObject.setCurrentContext(new ChoiceTrialContext());
					stateObject.setAnimation(XmlUtil.isAnimation(stateObject
							.getCurrentSpecDoc(), "/StimSpec/object1"));

					// run task
					return TrialExperimentUtil.runTrial(stateObject,
							threadHelper, new SlideRunner() {
								protected void prepareChoice(
										ChoiceTrialContext context) {
									double outDistance = stateObject
											.getChoiceTargetDistanceFromOrigin();
									double targetSize = stateObject
											.getChoiceTargetSize();
									double targetEyeWindowSize = stateObject.getChoiceTargetEyeWindowSize();

									double angle = Math
											.floor(Math.random() * 8.0)
											* Math.PI / 4.0;
									double x1 = outDistance * Math.cos(angle);
									double y1 = outDistance * Math.sin(angle);

									double x2 = outDistance
											* Math.cos(angle + Math.PI);
									double y2 = outDistance
											* Math.sin(angle + Math.PI);

									context.getTargetPos()[0] = new Coordinates2D(
											x1, y1);
									context.getTargetPos()[1] = new Coordinates2D(
											x2, y2);
									context.getTargetSize()[0] = targetSize;
									context.getTargetSize()[1] = targetSize;
									
									context.getTargetEyeWindowSize()[0] = targetEyeWindowSize;
									context.getTargetEyeWindowSize()[1] = targetEyeWindowSize;

									context.getReward()[0] = ChoiceXmlUtil
											.getChoiceReward(stateObject
													.getCurrentSpecDoc(),
													"/StimSpec/reward1");
									context.getReward()[1] = ChoiceXmlUtil
									.getChoiceReward(stateObject
											.getCurrentSpecDoc(),
											"/StimSpec/reward2");
								}

								public TrialResult runSlide() {
									ChoiceTrialDrawingController drawingController = (ChoiceTrialDrawingController) stateObject
											.getDrawingController();
									ExperimentTask currentTask = stateObject
											.getCurrentTask();
									ChoiceTrialContext currentContext = (ChoiceTrialContext) stateObject
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
										for (int i = 0; i < ChoiceTrialExperimentState.SLIDE_PER_TRIAL; i++) {
											// do first object
											TrialResult result = TrialExperimentUtil
													.doSlide(i, stateObject);
											if (result != TrialResult.SLIDE_OK) {
												return result;
											}

											if (i < ChoiceTrialExperimentState.SLIDE_PER_TRIAL - 1) {
												// prepare second object
												stateObject.setAnimation(
														XmlUtil.isAnimation(stateObject.getCurrentSpecDoc(), "/StimSpec/object2"));
												currentContext.setSlideIndex(i + 1);
												drawingController.prepareNextSlide(currentTask, currentContext);
											} else {
												// prepare choice targets
												prepareChoice(currentContext);
												drawingController.prepareTarget((ChoiceTrialContext) currentContext);
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
										ChoiceEventUtil.fireTargetOnEvent(targetOnLocalTime,trialEventListeners,currentContext);

										// monkey select target
										int sel = targetSelector.waitInitialSelection(
														currentContext.getTargetPos(),
														currentContext.getTargetEyeWindowSize(),
														targetOnLocalTime + stateObject.getTimeAllowedForInitialTargetSelection() * 1000);
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
										ChoiceEventUtil
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
										ChoiceEventUtil.fireTargetSelectionSuccessEvent(
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
		System.out.println("Stopping ChoiceTrialExperiment ...");
		if (isRunning()) {
			threadHelper.stop();
			threadHelper.join();
		}
	}

	public void setPause(boolean pause) {
		stateObject.setPause(pause);
	}

	public ChoiceTrialExperimentState getStateObject() {
		return stateObject;
	}

	public void setStateObject(ChoiceTrialExperimentState stateObject) {
		this.stateObject = stateObject;
	}
}
