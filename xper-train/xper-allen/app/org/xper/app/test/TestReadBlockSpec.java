package org.xper.app.test;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xper.allen.*;
import org.xper.allen.config.AllenDbUtil;
import org.xper.allen.specs.BlockSpec;



public class TestReadBlockSpec
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
		long blockId = 1;
		BlockSpec b = new BlockSpec();
		b = dbutil.readBlockSpec(blockId);
		
		System.out.println(b.get_id());
		System.out.println(b.get_num_stims_only());
		System.out.println(b.get_num_estims_only());
		System.out.println(b.get_num_catches());
		System.out.println(b.get_num_both());
		System.out.println(b.get_shuffle());
		
	}
}