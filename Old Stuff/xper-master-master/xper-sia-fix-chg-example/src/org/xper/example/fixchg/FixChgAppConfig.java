package org.xper.example.fixchg;

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
import org.xper.drawing.TaskScene;
import org.xper.drawing.object.BlankScreen;
import org.xper.fixchg.FixChgConfig;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(FixChgConfig.class)
public class FixChgAppConfig {
	@Autowired ClassicConfig classicConfig;
	@Autowired FixChgConfig fixChgConfig;
	@Autowired BaseConfig baseConfig;
	@Autowired AcqConfig acqConfig;

	@Bean
	public TaskScene taskScene() {
		FixChgGaborScene scene = new FixChgGaborScene();
		scene.setStereoRenderer(fixChgConfig.experimentGLRenderer());
		scene.setMonoRenderer(fixChgConfig.experimentGLMonoRenderer());
		scene.setMarker(fixChgConfig.screenMarker());
		scene.setBlankScreen(new BlankScreen());
		scene.setFixationColor(classicConfig.xperFixationPointColor());
		scene.setFixationSize(classicConfig.xperFixationPointSize());
		return scene;
	}
	
	@Bean
	public FixChgGaborSpecGenerator generator() {
		FixChgGaborSpecGenerator gen = new FixChgGaborSpecGenerator();
		return gen;
	}
	
	@Bean
	public RandomGeneration randomGen () {
		RandomGeneration gen = new RandomGeneration();
		gen.setDbUtil(baseConfig.dbUtil());
		gen.setGlobalTimeUtil(acqConfig.timeClient());
		gen.setTaskCount(2);
		gen.setGenerator(generator());
		return gen;
	}
}
