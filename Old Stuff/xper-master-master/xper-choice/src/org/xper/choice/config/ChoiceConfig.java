package org.xper.choice.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Import;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;
import org.springframework.config.java.util.DefaultScopes;
import org.xper.choice.ChoiceExperimentConsoleRenderer;
import org.xper.choice.ChoiceExperimentJuiceController;
import org.xper.choice.ChoiceExperimentMessageHandler;
import org.xper.choice.ChoiceTrialEventLogger;
import org.xper.choice.ChoiceTrialExperiment;
import org.xper.choice.ChoiceTrialExperimentMessageDispatcher;
import org.xper.choice.DefaultChoiceTrialExperimentDrawingController;
import org.xper.choice.vo.ChoiceTrialExperimentState;
import org.xper.classic.TrialDrawingController;
import org.xper.classic.TrialEventListener;
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.drawing.object.BlankScreen;
import org.xper.drawing.object.Circle;
import org.xper.drawing.object.Square;
import org.xper.exception.ExperimentSetupException;
import org.xper.eye.DefaultEyeTargetSelector;
import org.xper.eye.listener.EyeSamplerEventListener;
import org.xper.eye.strategy.AnyEyeInStategy;
import org.xper.eye.strategy.EyeInStrategy;
import org.xper.eye.vo.EyeDeviceReading;
import org.xper.eye.vo.EyeWindow;
import org.xper.juice.DigitalPortJuice;
import org.xper.juice.mock.NullDynamicJuice;

/**
 * 
 * 

Project for Dennis

1. Fixation spot appears
2. Once monkeys eyes enters the target area, there is a delay of 500 ms (T1) before the 
first visual object (O1) is shown, centred around the fixation spot. The object remains 
on for about 750 ms (T2) and then disappears, leaving the fixation spot still on. 
3. There is a delay of 750 ms (T3) after O1 disappears when it is just the fixation spot 
on screen, after which the second visual object is shown (O2), which also remains on for 
another 750 ms (T4).
4. After T4 is past O2 disappears, leaving just the fixation spot still remaining for an 
additional time of 750 ms (T5), after which the fixation spot disappears and two circular 
targets appear, one blue and the other yellow on diametrically opposite sides of the 
erstwhile position of the fixation spot. 
5. The two targets stay on for about 1000 ms during which the monkey is free to make a 
saccade to any one of the two targets. Once the monkey has made its choice by making a 
saccade to one of the two targets, the non-chosen target disappears leaving the monkey to 
remain inside the chosen target area for about 500 ms (T6). During this time the monkey 
cannot look away from the target. Following this the monkey is given the reward 
corresponding to the target it has picked. 
6. After an inter-trial interval (T7) during which the monkey is allowed to freely shift 
its gaze the fixation spot appears again and the trial is repeated in this manner.
 
During times T1 through T5, the fixation spot is on, and the monkey must maintain its 
gaze on the spot. If the monkey, at any time, allows its gaze to stray outside the fixation 
area, the the trial is aborted. During time T6 the monkey has to maintain fixation on the 
target it made its first saccade to - doing otherwise, would again cause the trial to be aborted. 
 
The two saccade target can appear in any of eight configurations around the central area - 
along the two cardinal axes and the two principal diagonals; the saccade target configuration 
is randomly chosen. The blue target represents the O1, while the yellow one represents O2. 
Both O1 and O2 will be chosen as per the operative genetic algorithm. One of the parameters 
in the parametric set that defines each object will specify the reward associated with that 
object. For every trial, once the O1 and O2 and chosen, according to the reward parameters, 
rewards will be assigned to the blue and the yellow targets at the beginning of the trial. 
Therefore, when the monkey picks one of the two targets, it will be rewarded appropriately. 

Port Juice is used instead of classical juice.

 * 
 * @author john
 *
 */

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(ClassicConfig.class)
public class ChoiceConfig {
	@Autowired BaseConfig baseConfig;
	@Autowired ClassicConfig classicConfig;
	@Autowired AcqConfig acqConfig;
	
	@Bean
	public ChoiceTrialExperiment experiment () {
		ChoiceTrialExperiment xper = new ChoiceTrialExperiment();
		xper.setStateObject(experimentState());
		return xper;
	}

	@Bean
	public ChoiceTrialExperimentState experimentState () {
		ChoiceTrialExperimentState state = new ChoiceTrialExperimentState ();
		state.setLocalTimeUtil(baseConfig.localTimeUtil());
		state.setTrialEventListeners(trialEventListeners());
		state.setSlideEventListeners(classicConfig.slideEventListeners());
		state.setEyeController(classicConfig.eyeController());
		state.setExperimentEventListeners(classicConfig.experimentEventListeners());
		state.setTaskDataSource(classicConfig.taskDataSource());
		state.setTaskDoneCache(classicConfig.taskDoneCache());
		state.setGlobalTimeClient(acqConfig.timeClient());
		state.setRequiredTargetSelectionHoldTime(xperRequiredTargetSelectionHoldTime());
		state.setTimeAllowedForInitialTargetSelection(xperTimeAllowedForInitialTargetSelection());
		state.setChoiceTargetDistanceFromOrigin(xperChoiceTargetDistanceFromOrigin());
		state.setChoiceTargetEyeWindowSize(xperChoiceTargetEyeWindowSize());
		state.setChoiceTargetSize(xperChoiceTargetSize());
		state.setTargetSelector(eyeTargetSelector());
		state.setDrawingController(drawingController());
		state.setInterTrialInterval(classicConfig.xperInterTrialInterval());
		state.setTimeBeforeFixationPointOn(classicConfig.xperTimeBeforeFixationPointOn());
		state.setTimeAllowedForInitialEyeIn(classicConfig.xperTimeAllowedForInitialEyeIn());
		state.setRequiredEyeInHoldTime(classicConfig.xperRequiredEyeInHoldTime());
		state.setSlidePerTrial(classicConfig.xperSlidePerTrial());
		state.setSlideLength(classicConfig.xperSlideLength());
		state.setInterSlideInterval(classicConfig.xperInterSlideInterval());
		state.setDoEmptyTask(classicConfig.xperDoEmptyTask());
		state.setSleepWhileWait(true);
		state.setPause(classicConfig.xperExperimentInitialPause());
		state.setDelayAfterTrialComplete(classicConfig.xperDelayAfterTrialComplete());
		return state;
	}
	
	@Bean
	public ChoiceExperimentMessageHandler messageHandler() {
		ChoiceExperimentMessageHandler messageHandler = new ChoiceExperimentMessageHandler();
		HashMap<String, EyeDeviceReading> eyeDeviceReading = new HashMap<String, EyeDeviceReading>();
		eyeDeviceReading.put(classicConfig.xperLeftIscanId(), classicConfig.zeroEyeDeviceReading());
		eyeDeviceReading.put(classicConfig.xperRightIscanId(), classicConfig.zeroEyeDeviceReading());
		messageHandler.setEyeDeviceReading(eyeDeviceReading);
		messageHandler.setEyeWindow(new EyeWindow(classicConfig.xperEyeWindowCenter(), classicConfig.xperEyeWindowAlgorithmInitialWindowSize()));
		HashMap<String, Coordinates2D> eyeZero = new HashMap<String, Coordinates2D>();
		eyeZero.put(classicConfig.xperLeftIscanId(), classicConfig.xperLeftIscanEyeZero());
		eyeZero.put(classicConfig.xperRightIscanId(), classicConfig.xperRightIscanEyeZero());
		messageHandler.setEyeZero(eyeZero);
		return messageHandler;
	}
	
	@Bean
	public ChoiceExperimentConsoleRenderer consoleRenderer () {
		ChoiceExperimentConsoleRenderer renderer = new ChoiceExperimentConsoleRenderer();
		renderer.setMessageHandler(messageHandler());
		renderer.setFixation(classicConfig.consoleFixationPoint());
		renderer.setRenderer(classicConfig.consoleGLRenderer());
		renderer.setBlankScreen(new BlankScreen());
		renderer.setCircle(new Circle());
		renderer.setSquare(new Square());
		renderer.setTargetColor(targetColor());
		return renderer;
	}
	
	@Bean (scope = DefaultScopes.PROTOTYPE)
	public List<TrialEventListener> trialEventListeners () {
		List<TrialEventListener> trialEventListener = new LinkedList<TrialEventListener>();
		trialEventListener.add(classicConfig.eyeMonitorController());
		trialEventListener.add(trialEventLogger());
		trialEventListener.add(classicConfig.experimentProfiler());
		trialEventListener.add(messageDispatcher());
		trialEventListener.add(juiceController());
		trialEventListener.add(classicConfig.dataAcqController());
		trialEventListener.add(classicConfig.jvmManager());
		return trialEventListener;
	}
	
	@Bean
	public TrialEventListener juiceController() {
		ChoiceExperimentJuiceController controller = new ChoiceExperimentJuiceController();
		if (acqConfig.acqDriverName.equalsIgnoreCase(acqConfig.DAQ_NONE)) {
			controller.setJuice(new NullDynamicJuice());
		} else {
			controller.setJuice(xperDynamicJuice());
		}
		return controller;
	}
	
	@Bean
	public DigitalPortJuice xperDynamicJuice() {
		DigitalPortJuice juice = new DigitalPortJuice();
		juice.setTriggerDelay(acqConfig.digitalPortJuiceTriggerDelay);
		if (acqConfig.acqDriverName.equalsIgnoreCase(acqConfig.DAQ_NI)) {
			juice.setDevice(classicConfig.niDigitalPortJuiceDevice());
		} else if (acqConfig.acqDriverName.equalsIgnoreCase(acqConfig.DAQ_COMEDI)) {
			juice.setDevice(classicConfig.comediDigitalPortJuiceDevice());
		} else {
			throw new ExperimentSetupException("Acq driver " + acqConfig.acqDriverName + " not supported.");
		}
		return juice;
	}
	
	@Bean
	public ChoiceTrialEventLogger trialEventLogger() {
		ChoiceTrialEventLogger logger = new ChoiceTrialEventLogger();
		return logger;
	}
	
	@Bean
	public TrialDrawingController drawingController() {
		DefaultChoiceTrialExperimentDrawingController controller;
		controller = new DefaultChoiceTrialExperimentDrawingController();
		controller.setWindow(classicConfig.monkeyWindow());
		controller.setTaskScene(classicConfig.taskScene());
		controller.setFixationOnWithStimuli(classicConfig.xperFixationOnWithStimuli());
		return controller;
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public RGBColor[] targetColor () {
		RGBColor[] colors = new RGBColor[2];
		colors[0] = new RGBColor(0.0f, 0.0f, 1.0f);
		colors[1] = new RGBColor(1.0f, 1.0f, 0.0f);
		return colors;
	}
	
	@Bean
	public ChoiceTrialExperimentMessageDispatcher messageDispatcher() {
		ChoiceTrialExperimentMessageDispatcher dispatcher = new ChoiceTrialExperimentMessageDispatcher();
		dispatcher.setHost(classicConfig.experimentHost);
		dispatcher.setDbUtil(baseConfig.dbUtil());
		return dispatcher;
	}
	
	@Bean (scope = DefaultScopes.PROTOTYPE)
	public List<EyeSamplerEventListener> eyeSamplerEventListeners () {
		List<EyeSamplerEventListener> sampleListeners = new LinkedList<EyeSamplerEventListener>();
		sampleListeners.add(eyeTargetSelector());
		sampleListeners.add(classicConfig.eyeMonitor());
		return sampleListeners;
	}
	
	@Bean
	public DefaultEyeTargetSelector eyeTargetSelector() {
		DefaultEyeTargetSelector selector = new DefaultEyeTargetSelector();
		selector.setEyeInstrategy(targetSelectorEyeInStrategy());
		selector.setLocalTimeUtil(baseConfig.localTimeUtil());
		return selector;
	}
	
	@Bean
	public EyeInStrategy targetSelectorEyeInStrategy() {
		AnyEyeInStategy strategy = new AnyEyeInStategy();
		List<String> devices = new LinkedList<String>();
		devices.add(classicConfig.xperLeftIscanId());
		devices.add(classicConfig.xperRightIscanId());
		strategy.setEyeDevices(devices);
		return strategy;
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Double xperChoiceTargetSize() {
		return Double.parseDouble(baseConfig.systemVariableContainer().get("xper_choice_target_size", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Double xperChoiceTargetEyeWindowSize() {
		return Double.parseDouble(baseConfig.systemVariableContainer().get("xper_choice_target_eye_window_size", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Double xperChoiceTargetDistanceFromOrigin() {
		return Double.parseDouble(baseConfig.systemVariableContainer().get("xper_choice_target_distance_from_origin", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperTimeAllowedForInitialTargetSelection() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_time_allowed_for_initial_target_selection", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperRequiredTargetSelectionHoldTime() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_required_target_selection_hold_time", 0));
	}
	
}
