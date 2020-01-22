package org.xper.fixchg;

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
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.drawing.object.LeftRightOverlapScreenMarker;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.drawing.renderer.PerspectiveIdenticalStereoRenderer;
import org.xper.drawing.renderer.PerspectiveStereoRenderer;
import org.xper.eye.IscanDevice;
import org.xper.eye.strategy.EyeInStrategy;
import org.xper.eye.strategy.MonoEyeInStrategy;
import org.xper.eye.zero.EyeZeroAdjustable;

/**
 * 

Project for Sia

1. Add a switch that allows us to base the eye tracking logic entirely on
one eye.  (We would still save position information for both eyes in the
data file.)

2. Add a switch that allows us to draw the fixation point.  At every time
the program would normally draw the fixation point, it would instead call
this experimenter-defined program.

Those two changes would allow us to change depth of fixation.  We would keep
the fixation point position constant for the tracking eye, and only change
the other eye position  to change vergence angle.

 * @author john
 *
 */

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(ClassicConfig.class)
public class FixChgConfig {
	@Autowired BaseConfig baseConfig;
	@Autowired ClassicConfig classicConfig;
	@Autowired AcqConfig acqConfig;
	
	@Bean
	public AbstractRenderer experimentGLRenderer () {
		PerspectiveStereoRenderer renderer = new PerspectiveStereoRenderer();
		renderer.setDistance(classicConfig.xperMonkeyScreenDistance());
		renderer.setDepth(classicConfig.xperMonkeyScreenDepth());
		renderer.setHeight(classicConfig.xperMonkeyScreenHeight());
		renderer.setWidth(classicConfig.xperMonkeyScreenWidth());
		renderer.setPupilDistance(classicConfig.xperMonkeyPupilDistance());
		renderer.setInverted(classicConfig.xperMonkeyScreenInverted());
		return renderer;
	}
	
	@Bean
	public AbstractRenderer experimentGLMonoRenderer () {
		PerspectiveIdenticalStereoRenderer renderer = new PerspectiveIdenticalStereoRenderer();
		renderer.setDistance(classicConfig.xperMonkeyScreenDistance());
		renderer.setDepth(classicConfig.xperMonkeyScreenDepth());
		renderer.setHeight(classicConfig.xperMonkeyScreenHeight());
		renderer.setWidth(classicConfig.xperMonkeyScreenWidth());
		renderer.setPupilDistance(classicConfig.xperMonkeyPupilDistance());
		renderer.setInverted(classicConfig.xperMonkeyScreenInverted());
		return renderer;
	}
	
	@Bean
	public IscanDevice leftIscan() {
		IscanDevice iscan = new IscanDevice();
		iscan.setEyeDeviceMessageListener(classicConfig.eyeDeviceMessageListeners());
		iscan.setEyeZeroMessageListener(classicConfig.eyeZeroMessageListeners());
		iscan.setId(classicConfig.xperLeftIscanId());
		iscan.setChannel(classicConfig.xperLeftIscanChannelSpec());
		iscan.setEyeZero(classicConfig.xperLeftIscanEyeZero());
		iscan.setEyeZeroAlgorithm(classicConfig.leftIscanMovingAverageEyeZeroAlgorithm());
		iscan.setEyeZeroUpdateEnabled(true);
		iscan.setMappingAlgorithm(classicConfig.leftIscanMappingAlgorithm());
		iscan.setLocalTimeUtil(baseConfig.localTimeUtil());
		return iscan;
	}
	
	@Bean
	public IscanDevice rightIscan() {
		IscanDevice iscan = new IscanDevice();
		iscan.setEyeDeviceMessageListener(classicConfig.eyeDeviceMessageListeners());
		iscan.setEyeZeroMessageListener(classicConfig.eyeZeroMessageListeners());
		iscan.setId(classicConfig.xperRightIscanId());
		iscan.setChannel(classicConfig.xperRightIscanChannelSpec());
		iscan.setEyeZero(classicConfig.xperRightIscanEyeZero());
		iscan.setEyeZeroAlgorithm(classicConfig.rightIscanMovingAverageEyeZeroAlgorithm());
		iscan.setEyeZeroUpdateEnabled(false);
		iscan.setMappingAlgorithm(classicConfig.rightIscanMappingAlgorithm());
		iscan.setLocalTimeUtil(baseConfig.localTimeUtil());
		return iscan;
	}
	
	@Bean
	public EyeInStrategy eyeInStrategy () {
		MonoEyeInStrategy strategy = new MonoEyeInStrategy();
		strategy.setEyeDeviceId(classicConfig.xperLeftIscanId());
		return strategy;
	}
	
	@Bean (scope = DefaultScopes.PROTOTYPE)
	public List<EyeZeroAdjustable> eyeZeroAdjustables () {
		List<EyeZeroAdjustable> adjustables = new LinkedList<EyeZeroAdjustable>();
		adjustables.add(leftIscan());
		return adjustables;
	}
	
	@Bean
	public LeftRightOverlapScreenMarker screenMarker() {
		LeftRightOverlapScreenMarker marker = new LeftRightOverlapScreenMarker();
		marker.setSize(classicConfig.xperScreenMarkerSize());
		marker.setViewportIndex(classicConfig.xperScreenMarkerViewportIndex());
		return marker;
	}
}
