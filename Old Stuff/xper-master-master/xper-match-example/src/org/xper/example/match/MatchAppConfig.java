package org.xper.example.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Import;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;
import org.xper.app.experiment.test.RandomGeneration;
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.drawing.object.BlankScreen;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.drawing.renderer.PerspectiveStereoRenderer;
import org.xper.match.config.MatchConfig;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(MatchConfig.class)
public class MatchAppConfig {
	@Autowired ClassicConfig classicConfig;
	@Autowired MatchConfig matchConfig;
	@Autowired AcqConfig acqConfig;
	@Autowired BaseConfig baseConfig;
	
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
	public MatchGaborScene taskScene() {
		MatchGaborScene scene = new MatchGaborScene();
		scene.setRenderer(experimentGLRenderer());
		scene.setFixation(classicConfig.experimentFixationPoint());
		scene.setMarker(classicConfig.screenMarker());
		scene.setBlankScreen(new BlankScreen());
//		scene.setTargetColor(matchConfig.targetColor());
		return scene;
	}
	
	@Bean
	public MatchGaborSpecGenerator generator() {
		MatchGaborSpecGenerator gen = new MatchGaborSpecGenerator();
		return gen;
	}
	
	@Bean
	public RandomGeneration randomGen () {
		RandomGeneration gen = new RandomGeneration();
		gen.setDbUtil(baseConfig.dbUtil());
		gen.setGlobalTimeUtil(acqConfig.timeClient());
		gen.setTaskCount(100);
		gen.setGenerator(generator());
		return gen;
	}
}
