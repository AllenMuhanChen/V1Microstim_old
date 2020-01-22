/**
 * 
 */
package org.xper.example.choice;

import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;

@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
public class ConfigOne {
	@Bean
	public Object oneBean () {
		return "ConfigOne";
	}
	
	@Bean
	public Object refBean () {
		return oneBean(); 
	}
}