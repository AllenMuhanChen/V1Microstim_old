package org.xper.sach.example;

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
import org.xper.sach.SachTrialExperiment;
import org.xper.sach.config.SachConfig;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(SachConfig.class)
public class SachAppConfig {
	@Autowired ClassicConfig classicConfig;
	@Autowired SachConfig sachConfig;
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
	public SachGaborScene taskScene() {
		SachGaborScene scene = new SachGaborScene();
		scene.setRenderer(experimentGLRenderer());
		scene.setFixation(classicConfig.experimentFixationPoint());
		scene.setMarker(classicConfig.screenMarker());
		scene.setBlankScreen(new BlankScreen());
		
		return scene;
	}
	
	@Bean(lazy=Lazy.FALSE)
	public Object wireCircularDependencies () {
		SachTrialExperiment e = sachConfig.experiment();
		SachGaborScene scene = taskScene();
		scene.setExperiment(e);
		return new Object();
	}
	
	@Bean
	public SachGaborSpecGenerator generator() {
		SachGaborSpecGenerator gen = new SachGaborSpecGenerator();
		return gen;
	}
	
	@Bean
	public RandomGeneration randomGen () {
		RandomGeneration gen = new RandomGeneration();
		gen.setDbUtil(baseConfig.dbUtil());
		gen.setGlobalTimeUtil(acqConfig.timeClient());
		gen.setTaskCount(10);
		gen.setGenerator(generator());
		return gen;
	}
}
