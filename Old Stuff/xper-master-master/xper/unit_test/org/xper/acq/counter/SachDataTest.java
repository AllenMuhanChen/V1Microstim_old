package org.xper.acq.counter;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xper.ManualTest;
import org.xper.db.vo.SystemVariable;
import org.xper.db.vo.TaskDoneEntry;
import org.xper.util.DbUtil;

import junit.framework.TestCase;

@ManualTest
public class SachDataTest extends TestCase {
	public void test () {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/sach");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(1407870694079527L, 1407870698995110L);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);
		
		System.out.println(result.size());
	}
}
