package org.xper.example.matchchoice;

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
import org.xper.matchchoice.config.MatchChoiceConfig;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(MatchChoiceConfig.class)
public class MatchChoiceAppConfig {
	@Autowired ClassicConfig classicConfig;
	@Autowired BaseConfig baseConfig;
	@Autowired AcqConfig acqConfig;
	@Autowired MatchChoiceConfig matchChoiceConfig;

	@Bean
	public MatchChoiceGaborScene taskScene() {
		MatchChoiceGaborScene scene = new MatchChoiceGaborScene();
		scene.setRenderer(classicConfig.experimentGLRenderer());
		scene.setFixation(classicConfig.experimentFixationPoint());
		scene.setMarker(classicConfig.screenMarker());
		scene.setBlankScreen(new BlankScreen());
		//scene.setTargetColor(matchChoiceConfig.targetColor());
		scene.setDrawableFactory(matchChoiceConfig.targetDrawableFactory());
		return scene;
	}
	
	@Bean
	public MatchChoiceGaborSpecGenerator generator() {
		MatchChoiceGaborSpecGenerator gen = new MatchChoiceGaborSpecGenerator();
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
