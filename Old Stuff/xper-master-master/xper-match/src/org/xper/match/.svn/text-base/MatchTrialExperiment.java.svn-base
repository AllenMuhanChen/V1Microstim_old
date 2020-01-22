package org.xper.match;

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
import org.xper.drawing.RGBColor;
import org.xper.exception.XmlDocInvalidFormatException;
import org.xper.experiment.Experiment;
import org.xper.experiment.ExperimentTask;
import org.xper.experiment.TaskDoneCache;
import org.xper.eye.EyeMonitor;
import org.xper.eye.EyeTargetSelector;
import org.xper.match.util.MatchEventUtil;
import org.xper.match.util.MatchExperimentUtil;
import org.xper.match.util.MatchXmlUtil;
import org.xper.match.vo.MatchTrialContext;
import org.xper.match.vo.MatchTrialExperimentState;
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
 *  ... (one to five objects)
 *  <matchIndex></matchIndex>
 *  <targetColor>...</targetColor>
 *  <targetColor>...</targetColor>
 *  ... (one for each target)
 * </StimSpec>
 * 
 * If attribute animation is false or missing, the object is treated as a static
 * slide.
 * 
 * @author wang
 * 
 */

public class MatchTrialExperiment implements Experiment {
	static Logger logger = Logger.getLogger(MatchTrialExperiment.class);

	@Dependency
	MatchTrialExperimentState stateObject;
	
	@Dependency
	EyeMonitor eyeMonitor;

	ThreadHelper threadHelper = new ThreadHelper("MatchTrialExperiment", this);

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
					MatchTrialContext context = new MatchTrialContext();
					stateObject.setCurrentContext(context);
					
					Node matchIndexNode = stateObject.getCurrentSpecDoc().selectSingleNode("/StimSpec/matchIndex");
					if (matchIndexNode == null) {
						throw new XmlDocInvalidFormatException("No matching object index in match task specification.");
					}
					int matchIndex = Integer.parseInt(matchIndexNode.getText());
					context.setMatchIndex(matchIndex);
					
					final List<?> objectNodeList = stateObject.getCurrentSpecDoc().selectNodes("/StimSpec/object");
					final int countObject = objectNodeList.size();
					if (countObject == 0) {
						throw new XmlDocInvalidFormatException("No objects in match task specification.");
					}
					if (logger.isDebugEnabled()) {
						logger.debug(stateObject.getCurrentTask().getTaskId() + " " + countObject);
					}
					
					final List<RGBColor> targetColors = MatchXmlUtil.getTargetColor(stateObject.getCurrentSpecDoc());
					
					// first object animated?
					Node objectNode = (Node)objectNodeList.get(0);
					stateObject.setAnimation(XmlUtil.isAnimation(objectNode));								

					// run task
					ret = TrialExperimentUtil.runTrial(stateObject,
							threadHelper, new SlideRunner() {
						
								protected void prepareTarget(int i, MatchTrialContext context) {
									double outDistance = stateObject.getTargetDistanceFromOrigin();
									double targetSize = stateObject.getTargetSize();
									double targetEyeWindowSize = stateObject.getTargetEyeWindowSize();
		
									double angle = Math.floor(Math.random() * 8.0) * Math.PI / 4.0;
									double x = outDistance * Math.cos(angle);
									double y = outDistance * Math.sin(angle);
		
									context.setTargetColor(targetColors.get(i-1>=0?i-1:0));
									context.setTargetPos(new Coordinates2D(x, y));
									context.setTargetSize(targetSize);
									context.setTargetEyeWindowSize(targetEyeWindowSize);
								}

								public TrialResult runSlide() {
									TrialDrawingController drawingController = stateObject.getDrawingController();
									ExperimentTask currentTask = stateObject.getCurrentTask();
									MatchTrialContext currentContext = (MatchTrialContext) stateObject.getCurrentContext();
									TaskDoneCache taskDoneCache = stateObject.getTaskDoneCache();
									TimeUtil globalTimeClient = stateObject.getGlobalTimeClient();
									TimeUtil timeUtil = stateObject.getLocalTimeUtil();
									EyeTargetSelector targetSelector = stateObject.getTargetSelector();
									List<? extends TrialEventListener> trialEventListeners = stateObject.getTrialEventListeners();

									try {
										for (int i = 0; i < countObject; i++) {
											// do first object
											// target will be shown at the end of doSlide
											prepareTarget(i, currentContext);
											
											TrialResult result = TrialExperimentUtil.doSlide(i, stateObject);
											
											if (result != TrialResult.SLIDE_OK) {
												return result;
											}
											
											if (MatchExperimentUtil.isTargetOn(currentContext)) {
												long targetOnLocalTime = currentContext.getCurrentSlideOffTime();
												currentContext.setTargetOnTime(targetOnLocalTime);
												MatchEventUtil.fireTargetOnEvent(targetOnLocalTime, trialEventListeners, currentContext);
											}
												
											// for the matching stimulus test for saccade, otherwise wait
											if (i > 0) {
												ThreadUtil.sleep(stateObject.getTargetSelectionStartDelay());
												// monkey select target
												int sel = targetSelector.waitInitialSelection(
																new Coordinates2D[] {currentContext.getTargetPos(), eyeMonitor.getEyeWinCenter()},
																new double[] {currentContext.getTargetEyeWindowSize(), eyeMonitor.getEyeWindowAlgorithm().getCurrentEyeWindowSize()},
																currentContext.getTargetOnTime() + stateObject.getTimeAllowedForInitialTargetSelection() * 1000
																+ stateObject.getTargetSelectionStartDelay() * 1000);
												if (sel < 0) {
													TrialExperimentUtil.breakTrial(stateObject);
													return TrialResult.EYE_BREAK;
												}

												// monkey fixate target
												long targetInitialSelectionLocalTime = timeUtil.currentTimeMicros();
												currentContext.setTargetInitialSelectionTime(targetInitialSelectionLocalTime);
												MatchEventUtil.fireTargetInitialSelectionEvent( targetInitialSelectionLocalTime,
																trialEventListeners, currentContext);

												boolean success = targetSelector.waitEyeHold(sel,
																targetInitialSelectionLocalTime + stateObject.getRequiredTargetSelectionHoldTime() * 1000);
												if (!success) {
													TrialExperimentUtil.breakTrial(stateObject);
													return TrialResult.EYE_BREAK;
												}
												
												long targetSelectionSuccessLocalTime = timeUtil.currentTimeMicros();
												currentContext.setTargetSelectionSuccessTime(targetSelectionSuccessLocalTime);
												boolean targetSelectionCorrect;
												if (i == currentContext.getMatchIndex() && sel == 0 ||
													i != currentContext.getMatchIndex() && sel == 1) {
													// correct selection
													MatchEventUtil.fireTargetSelectionCorrectEvent(targetSelectionSuccessLocalTime,
																	trialEventListeners, currentContext);
													targetSelectionCorrect = true;
												} else {
													// incorrect selection
													MatchEventUtil.fireTargetSelectionWrongEvent(targetSelectionSuccessLocalTime,
															trialEventListeners, currentContext);
													targetSelectionCorrect = false;
												}
												
												// clear target
												((DefaultMatchTrialDrawingController)drawingController).targetSelectionDone(currentTask, currentContext);
												
												if (!targetSelectionCorrect) {
													// break the trial whenever there's TargetSelectionWrong
													TrialExperimentUtil.breakTrial(stateObject);
													return TrialResult.EYE_BREAK;
												}
											}
											
											if (i < countObject - 1) {
												// prepare second object
												stateObject.setAnimation(XmlUtil.isAnimation((Node)objectNodeList.get(i+1)));
												currentContext.setSlideIndex(i + 1);
												drawingController.prepareNextSlide(currentTask, currentContext);
											}
											
											// inter slide interval
											result = TrialExperimentUtil.waitInterSlideInterval(stateObject,threadHelper);
											if (result != TrialResult.SLIDE_OK) {
												return result;
											}
										}

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
						// Do not repeat task even if trial fails. 
						// This is achieved by setting current task to null.
						// Change 2/29/2012: repeat if INITIAL_EYE_IN_FAIL or EYE_IN_HOLD_FAIL, otherwise do not repeat
						if (ret != TrialResult.INITIAL_EYE_IN_FAIL && ret != TrialResult.EYE_IN_HOLD_FAIL) {
							stateObject.setCurrentTask(null);
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
		System.out.println("Stopping MatchTrialExperiment ...");
		if (isRunning()) {
			threadHelper.stop();
			threadHelper.join();
		}
	}

	public void setPause(boolean pause) {
		stateObject.setPause(pause);
	}

	public MatchTrialExperimentState getStateObject() {
		return stateObject;
	}

	public void setStateObject(MatchTrialExperimentState stateObject) {
		this.stateObject = stateObject;
	}

	public EyeMonitor getEyeMonitor() {
		return eyeMonitor;
	}

	public void setEyeMonitor(EyeMonitor eyeMonitor) {
		this.eyeMonitor = eyeMonitor;
	}
}
