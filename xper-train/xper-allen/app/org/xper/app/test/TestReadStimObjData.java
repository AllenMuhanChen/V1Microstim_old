package org.xper.app.test;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xper.allen.*;



public class TestReadStimObjData
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
		long StimObjId = 1;
		StimObjData so = new StimObjData();
		so = dbutil.readStimObjData(StimObjId);
		
		System.out.println(so.get_id());
		System.out.println(so.get_spec());
		System.out.println(so.get_data());
	}
}