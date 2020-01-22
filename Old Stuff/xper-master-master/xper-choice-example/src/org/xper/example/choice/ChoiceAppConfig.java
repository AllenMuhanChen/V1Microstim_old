package org.xper.example.choice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Import;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;
import org.xper.app.experiment.test.RandomGeneration;
import org.xper.choice.config.ChoiceConfig;
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.drawing.object.BlankScreen;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(ChoiceConfig.class)
public class ChoiceAppConfig {
	@Autowired ClassicConfig classicConfig;
	@Autowired BaseConfig baseConfig;
	@Autowired AcqConfig acqConfig;
	@Autowired ChoiceConfig choiceConfig;

	@Bean
	public ChoiceGaborScene taskScene() {
		ChoiceGaborScene scene = new ChoiceGaborScene();
		scene.setRenderer(classicConfig.experimentGLRenderer());
		scene.setFixation(classicConfig.experimentFixationPoint());
		scene.setMarker(classicConfig.screenMarker());
		scene.setBlankScreen(new BlankScreen());
		scene.setTargetColor(choiceConfig.targetColor());
		return scene;
	}
	
	@Bean
	public ChoiceGaborSpecGenerator generator() {
		ChoiceGaborSpecGenerator gen = new ChoiceGaborSpecGenerator();
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
