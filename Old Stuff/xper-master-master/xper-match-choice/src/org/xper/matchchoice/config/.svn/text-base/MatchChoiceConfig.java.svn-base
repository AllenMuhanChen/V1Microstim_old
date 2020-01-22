package org.xper.matchchoice.config;

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
import org.xper.classic.TrialDrawingController;
import org.xper.classic.TrialEventListener;
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.drawing.CircleFactory;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.DrawableFactory;
import org.xper.drawing.SquareFactory;
import org.xper.drawing.object.BlankScreen;
import org.xper.drawing.object.Circle;
import org.xper.drawing.object.Square;
import org.xper.eye.RobustEyeTargetSelector;
import org.xper.eye.listener.EyeSamplerEventListener;
import org.xper.eye.strategy.AnyEyeInStategy;
import org.xper.eye.strategy.EyeInStrategy;
import org.xper.eye.vo.EyeDeviceReading;
import org.xper.eye.vo.EyeWindow;
import org.xper.juice.mock.NullDynamicJuice;
import org.xper.matchchoice.DefaultMatchChoiceTrialExperimentDrawingController;
import org.xper.matchchoice.MatchChoiceExperimentConsoleRenderer;
import org.xper.matchchoice.MatchChoiceExperimentJuiceController;
import org.xper.matchchoice.MatchChoiceExperimentMessageHandler;
import org.xper.matchchoice.MatchChoiceTrialEventLogger;
import org.xper.matchchoice.MatchChoiceTrialExperiment;
import org.xper.matchchoice.MatchChoiceTrialExperimentMessageDispatcher;
import org.xper.matchchoice.vo.MatchChoiceTrialExperimentState;

/**
 * 
 * Project for Takehiro
 * 
 * object 1
 * object 2
 * select 2 targets
 * 
 * classical juice
 * 
 * @author john
 *
 */

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(ClassicConfig.class)
public class MatchChoiceConfig {
	@Autowired BaseConfig baseConfig;
	@Autowired ClassicConfig classicConfig;
	@Autowired AcqConfig acqConfig;
	
	@Bean
	public MatchChoiceTrialExperiment experiment () {
		MatchChoiceTrialExperiment xper = new MatchChoiceTrialExperiment();
		xper.setStateObject(experimentState());
		return xper;
	}

	@Bean
	public MatchChoiceTrialExperimentState experimentState () {
		MatchChoiceTrialExperimentState state = new MatchChoiceTrialExperimentState ();
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
		state.setTargetSelectionStartDelay(xperTargetSelectionEyeMonitorStartDelay());
		state.setChoiceTargetEyeWindowSize(xperChoiceTargetEyeWindowSize());
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
	public MatchChoiceExperimentMessageHandler messageHandler() {
		MatchChoiceExperimentMessageHandler messageHandler = new MatchChoiceExperimentMessageHandler();
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
	public MatchChoiceExperimentConsoleRenderer consoleRenderer () {
		MatchChoiceExperimentConsoleRenderer renderer = new MatchChoiceExperimentConsoleRenderer();
		renderer.setMessageHandler(messageHandler());
		renderer.setFixation(classicConfig.consoleFixationPoint());
		renderer.setRenderer(classicConfig.consoleGLRenderer());
		renderer.setBlankScreen(new BlankScreen());
		renderer.setCircle(new Circle());
		renderer.setSquare(new Square());
		//Orenderer.setTargetColor(targetColor());
		renderer.setDrawableFactory(targetDrawableFactory());
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
		MatchChoiceExperimentJuiceController controller = new MatchChoiceExperimentJuiceController();
		if (acqConfig.acqDriverName.equalsIgnoreCase(acqConfig.DAQ_NONE)) {
			controller.setJuice(new NullDynamicJuice());
		} else {
			controller.setJuice(classicConfig.xperDynamicJuice());
		}
		return controller;
	}
	
	@Bean
	public MatchChoiceTrialEventLogger trialEventLogger() {
		MatchChoiceTrialEventLogger logger = new MatchChoiceTrialEventLogger();
		return logger;
	}
	
	@Bean
	public TrialDrawingController drawingController() {
		DefaultMatchChoiceTrialExperimentDrawingController controller;
		controller = new DefaultMatchChoiceTrialExperimentDrawingController();
		controller.setWindow(classicConfig.monkeyWindow());
		controller.setTaskScene(classicConfig.taskScene());
		controller.setFixationOnWithStimuli(classicConfig.xperFixationOnWithStimuli());
		return controller;
	}
	
	/*@Bean(scope = DefaultScopes.PROTOTYPE)
	public RGBColor[] targetColor () {
		RGBColor[] colors = new RGBColor[2];
		colors[0] = new RGBColor(0.0f, 0.0f, 1.0f);
		colors[1] = new RGBColor(1.0f, 1.0f, 0.0f);
		return colors;
	}*/
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public DrawableFactory[] targetDrawableFactory () {
		DrawableFactory[] factories = new DrawableFactory[2];
		factories[0] = new CircleFactory();
		factories[1] = new SquareFactory();
		return factories;
	}
	
	@Bean
	public MatchChoiceTrialExperimentMessageDispatcher messageDispatcher() {
		MatchChoiceTrialExperimentMessageDispatcher dispatcher = new MatchChoiceTrialExperimentMessageDispatcher();
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
	public RobustEyeTargetSelector eyeTargetSelector() {
		RobustEyeTargetSelector selector = new RobustEyeTargetSelector();
		selector.setEyeInstrategy(targetSelectorEyeInStrategy());
		selector.setLocalTimeUtil(baseConfig.localTimeUtil());
		selector.setTargetInTimeThreshold(xperTargetSelectionEyeInTimeThreshold());
		selector.setTargetOutTimeThreshold(xperTargetSelectionEyeOutTimeThreshold());
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
	public Double xperChoiceTargetEyeWindowSize() {
		return Double.parseDouble(baseConfig.systemVariableContainer().get("xper_choice_target_eye_window_size", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperTimeAllowedForInitialTargetSelection() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_time_allowed_for_initial_target_selection", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperRequiredTargetSelectionHoldTime() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_required_target_selection_hold_time", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperTargetSelectionEyeInTimeThreshold() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_target_selection_eye_in_time_threshold", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperTargetSelectionEyeOutTimeThreshold() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_target_selection_eye_out_time_threshold", 0));
	}
	
	@Bean(scope = DefaultScopes.PROTOTYPE)
	public Long xperTargetSelectionEyeMonitorStartDelay() {
		return Long.parseLong(baseConfig.systemVariableContainer().get("xper_target_selection_eye_monitor_start_delay", 0));
	}
	
}
