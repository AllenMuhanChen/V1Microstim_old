/*
package org.xper.allen.config;
import org.xper.app.experiment.test.TestGeneration;
import org.xper.example.classic.ClassicAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
public class AllenAppConfig extends ClassicAppConfig {
	@Autowired AllenConfig allenConfig;
	//AllenConfig allenConfig = new AllenConfig();
	@Bean
	public TestGeneration testGen() {
		TestGeneration gen = new TestGeneration();
		gen.setDbUtil(allenConfig.allenDbUtil());
		gen.setGlobalTimeUtil(acqConfig.timeClient());
		//gen.setTaskCount(100);
		gen.setGenerator(generator());
		return gen;
	}
}
*/
package org.xper.allen.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Import;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;
import org.xper.app.experiment.test.TestGeneration;
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.drawing.TaskScene;
import org.xper.drawing.object.BlankScreen;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.drawing.renderer.PerspectiveStereoRenderer;
import org.xper.example.classic.GaborScene;
import org.xper.example.classic.GaborSpecGenerator;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(ClassicConfig.class)
public class AllenAppConfig {
	@Autowired ClassicConfig classicConfig; //AC: changed to protected
	@Autowired BaseConfig baseConfig;
	@Autowired AllenConfig allenConfig; 

	@Autowired protected AcqConfig acqConfig;
	
	
	@Bean
	public AbstractRenderer experimentGLRenderer () {
		PerspectiveStereoRenderer renderer = new PerspectiveStereoRenderer();
		renderer.setDistance(classicConfig.xperMonkeyScreenDistance());
		renderer.setDepth(classicConfig.xperMonkeyScreenDepth());
		renderer.setHeight(classicConfig.xperMonkeyScreenHeight());
		renderer.setWidth(classicConfig.xperMonkeyScreenWidth());
		
		System.out.println("23108 screen width = " + classicConfig.xperMonkeyScreenWidth());
		
		renderer.setPupilDistance(classicConfig.xperMonkeyPupilDistance());
		renderer.setInverted(classicConfig.xperMonkeyScreenInverted());
		return renderer;
	}
	
	@Bean
	public TaskScene taskScene() {
		GaborScene scene = new GaborScene();
		scene.setRenderer(experimentGLRenderer());
		scene.setFixation(classicConfig.experimentFixationPoint());
		scene.setMarker(classicConfig.screenMarker());
		scene.setBlankScreen(new BlankScreen());
		return scene;
	}
	
	@Bean
	public GaborSpecGenerator generator() {
		GaborSpecGenerator gen = new GaborSpecGenerator();
		return gen;
	}
	
	@Bean
	public TestGeneration randomGen () {
		TestGeneration gen = new TestGeneration();
		gen.setDbUtil(allenConfig.allenDbUtil());
		gen.setGlobalTimeUtil(acqConfig.timeClient());
		gen.setTaskCount(100);
		gen.setGenerator(generator());
		return gen;
	}
	@Bean
	public TestGeneration testGen() {
		TestGeneration gen = new TestGeneration();
		gen.setDbUtil(allenConfig.allenDbUtil());
		gen.setGlobalTimeUtil(acqConfig.timeClient());
		//gen.setTaskCount(100);
		gen.setGenerator(generator());
		return gen;
	}

}