package org.xper.app.test;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xper.Dependency;
import org.xper.allen.*;
import org.xper.app.experiment.test.RandomGeneration;
import org.xper.app.experiment.test.RandomGenerationAllen;
import org.xper.util.DbUtil;



public class TestBlockLogic
{

	//static AllenDbUtil dbutil;
	public static void main(String[] args)
	{
		//Setting up DataSource
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/V1Microstim?useSSL=false");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");
		
		AllenDbUtil dbutil = new AllenDbUtil();
		dbutil.setDataSource(dataSource);
		
		RandomGenerationAllen randomGeneration = new RandomGenerationAllen();
		randomGeneration.setTaskCount(100);
		
		long blockId = 1;
		BlockSpec blockspec = dbutil.readBlockSpec(blockId);
		randomGeneration.testBlockClass(blockspec);
		
	

	}
}