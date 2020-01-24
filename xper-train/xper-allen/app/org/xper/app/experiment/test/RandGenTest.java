package org.xper.app.experiment.test;

import org.springframework.config.java.context.JavaConfigApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xper.Dependency;
import org.xper.allen.AllenDbUtil;
import org.xper.util.FileUtil;
import org.xper.time.DefaultTimeUtil;
import org.xper.time.TimeUtil;



public class RandGenTest {
	
	public static void main(String[] args) {
		TestGeneration gen = new TestGeneration();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/V1Microstim?useSSL=false");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");
		
		AllenDbUtil dbutil = new AllenDbUtil();
		dbutil.setDataSource(dataSource);
		gen.setDbUtil(dbutil);
		
		DefaultTimeUtil globalTimeUtil = new DefaultTimeUtil();
		gen.setGlobalTimeUtil(globalTimeUtil);
		
		gen.generate();
	}
}
