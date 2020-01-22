package org.xper.acq.counter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import junit.framework.TestCase;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xper.XperConfig;
import org.xper.acq.vo.DigitalChannel;
import org.xper.db.vo.AcqDataEntry;
import org.xper.db.vo.SystemVariable;
import org.xper.db.vo.TaskDoneEntry;
import org.xper.time.DefaultTimeUtil;
import org.xper.time.TimeUtil;
import org.xper.util.DbUtil;

public class MarkEveryStepExperimentSpikeCounterTest extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
		
		List<String> libs = new ArrayList<String>();
		libs.add("xper");
		new XperConfig("", libs);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testData201406232 () {
		MarkEveryStepTaskSpikeDataEntry e = getCounterFromDataFile("acq_data_2014_06_23_2.txt", (short)2, (short)1);
		for (int i = 0; i < e.getTrialStageData().size(); i ++) {
			TrialStageData d = e.getTrialStageData(i);
			System.out.println(d.getStartSampleIndex() + "-" + d.getStopSampleIndex());
		}
		//assertEquals(19, e.getTrialStageData().size());
		//assertEquals(112790, e.getTrialStageData(18).getStartSampleIndex());
		//assertEquals(112818, e.getTrialStageData(18).getStopSampleIndex());
//		assertEquals(107789, e.getTrialStageData(17).getStartSampleIndex());
//		assertEquals(112818, e.getTrialStageData(17).getStopSampleIndex());
//		assertEquals(100286, e.getTrialStageData(16).getStartSampleIndex());
//		assertEquals(107800, e.getTrialStageData(16).getStopSampleIndex());
		//assertEquals(1086, e.getTrialStageData(0).getStartSampleIndex());
		//assertEquals(3598, e.getTrialStageData(0).getStopSampleIndex());
	}
	
	
	public void testData20140623 () {
		MarkEveryStepTaskSpikeDataEntry e = getCounterFromDataFile("acq_data_2014_06_23.txt", (short)2, (short)1);
		for (int i = 0; i < e.getTrialStageData().size(); i ++) {
			TrialStageData d = e.getTrialStageData(i);
			System.out.println(d.getStartSampleIndex() + "-" + d.getStopSampleIndex());
		}
		//assertEquals(19, e.getTrialStageData().size());
		//assertEquals(112790, e.getTrialStageData(18).getStartSampleIndex());
		//assertEquals(112818, e.getTrialStageData(18).getStopSampleIndex());
//		assertEquals(107789, e.getTrialStageData(17).getStartSampleIndex());
//		assertEquals(112818, e.getTrialStageData(17).getStopSampleIndex());
//		assertEquals(100286, e.getTrialStageData(16).getStartSampleIndex());
//		assertEquals(107800, e.getTrialStageData(16).getStopSampleIndex());
		//assertEquals(1086, e.getTrialStageData(0).getStartSampleIndex());
		//assertEquals(3598, e.getTrialStageData(0).getStopSampleIndex());
	}
	
	
	public void testData20140620 () {
		MarkEveryStepTaskSpikeDataEntry e = getCounterFromDataFile("acq_data_2014_06_18.txt", (short)2, (short)1);
		for (int i = 0; i < e.getTrialStageData().size(); i ++) {
			TrialStageData d = e.getTrialStageData(i);
			System.out.println(d.getStartSampleIndex() + "-" + d.getStopSampleIndex());
		}
		assertEquals(19, e.getTrialStageData().size());
		assertEquals(112790, e.getTrialStageData(18).getStartSampleIndex());
		assertEquals(112818, e.getTrialStageData(18).getStopSampleIndex());
		assertEquals(107789, e.getTrialStageData(17).getStartSampleIndex());
		assertEquals(112818, e.getTrialStageData(17).getStopSampleIndex());
		assertEquals(100286, e.getTrialStageData(16).getStartSampleIndex());
		assertEquals(107800, e.getTrialStageData(16).getStopSampleIndex());
		assertEquals(1086, e.getTrialStageData(0).getStartSampleIndex());
		assertEquals(3598, e.getTrialStageData(0).getStopSampleIndex());
	}
	
	private ArrayList<AcqDataEntry> readAcqData(String fileName, Map<Short, Short> map) {
		ArrayList<AcqDataEntry> data = new ArrayList<AcqDataEntry>();
		InputStream s = MarkEveryStepExperimentSpikeCounterTest.class.getResourceAsStream("/org/xper/acq/counter/" + fileName);
		BufferedReader r = new BufferedReader(new InputStreamReader(s));
		String line = null;
		try {
			while((line = r.readLine()) != null) {
				String[] fields = line.trim().split("\\s+");
				if (fields.length != 3) {
					throw new RuntimeException("Error: " + line);
				}
				AcqDataEntry entry = new AcqDataEntry();
				entry.setChannel(Short.parseShort(fields[0].trim()));
				entry.setSampleInd(Integer.parseInt(fields[1].trim()));
				entry.setValue(Double.parseDouble(fields[2].trim()));
				entry.setChannel(map.get(entry.getChannel()));
				data.add(entry);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return data;
	}
	
	private MarkEveryStepTaskSpikeDataEntry getCounterFromDataFile (String dataFileName, 
			final short evenChannel, final short oddChannel) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/wang");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		TimeUtil timeUtil = new DefaultTimeUtil();
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		final short evenMarkerChannel = Short.parseShort(vars.get("acq_even_marker_chan").getValue(0));
		final short oddMarkerChannel = Short.parseShort(vars.get("acq_odd_marker_chan").getValue(0));
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		long sessionStart = timeUtil.currentTimeMicros();
		long sessionStop = sessionStart + 100;
		
		dbUtil.writeBeginAcqSession(sessionStart);
		dbUtil.writeEndAcqSession(sessionStart, sessionStop);
		
		long task1 = sessionStart + 10;
		dbUtil.writeTaskDone(task1, task1, 0);
		
		ArrayList<AcqDataEntry> data = readAcqData(dataFileName, new HashMap<Short, Short>(){
			private static final long serialVersionUID = -1632643446801678509L;
			{
				put(evenChannel, evenMarkerChannel);
				put(oddChannel, oddMarkerChannel);
			}
		});
		
		dbUtil.writeAcqData(sessionStop - 1, data);
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(sessionStart, sessionStop);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);
		
		MarkEveryStepTaskSpikeDataEntry e = result.get(task1);
		
		return e;
	}

	public void testData20140618() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/wang");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		TimeUtil timeUtil = new DefaultTimeUtil();
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		short evenMarkerChannel = Short.parseShort(vars.get("acq_even_marker_chan").getValue(0));
		short oddMarkerChannel = Short.parseShort(vars.get("acq_odd_marker_chan").getValue(0));
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		long sessionStart = timeUtil.currentTimeMicros();
		long sessionStop = sessionStart + 100;
		
		dbUtil.writeBeginAcqSession(sessionStart);
		long task1 = sessionStart + 10;
		dbUtil.writeTaskDone(task1, task1, 0);
		
		//1086-3598, 3587-12367, 12339-19854, 19843-19854
		
		// even up
		ArrayList<AcqDataEntry> data = new ArrayList<AcqDataEntry>();
		AcqDataEntry ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 1086);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// odd up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 3587);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// even down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 3598);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// even up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 12339);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// odd down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 12367);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// odd up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 19843);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// even down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 19854);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		dbUtil.writeAcqData(sessionStop - 1, data);
		
		dbUtil.writeEndAcqSession(sessionStart, sessionStop);
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(sessionStart, sessionStop);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);
		
		MarkEveryStepTaskSpikeDataEntry e = result.get(task1);
		assertEquals(4, e.getTrialStageData().size());
		assertEquals(19843, e.getTrialStageData(3).startSampleIndex);
		assertEquals(19854, e.getTrialStageData(3).stopSampleIndex);
		assertEquals(12339, e.getTrialStageData(2).startSampleIndex);
		assertEquals(19854, e.getTrialStageData(2).stopSampleIndex);
		assertEquals(3587, e.getTrialStageData(1).startSampleIndex);
		assertEquals(12367, e.getTrialStageData(1).stopSampleIndex);
		assertEquals(1086, e.getTrialStageData(0).startSampleIndex);
		assertEquals(3598, e.getTrialStageData(0).stopSampleIndex);
	}
	
	public void testSingleUpEdgeAtTheEnd() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/wang");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		TimeUtil timeUtil = new DefaultTimeUtil();
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		short evenMarkerChannel = Short.parseShort(vars.get("acq_even_marker_chan").getValue(0));
		short oddMarkerChannel = Short.parseShort(vars.get("acq_odd_marker_chan").getValue(0));
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		long sessionStart = timeUtil.currentTimeMicros();
		long sessionStop = sessionStart + 100;
		
		dbUtil.writeBeginAcqSession(sessionStart);
		long task1 = sessionStart + 10;
		dbUtil.writeTaskDone(task1, task1, 0);
		
		// even up
		ArrayList<AcqDataEntry> data = new ArrayList<AcqDataEntry>();
		AcqDataEntry ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 0);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Up = 20;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Down = 22;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// even down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 100);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// odd up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 110);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		dbUtil.writeAcqData(sessionStop - 1, data);
		
		dbUtil.writeEndAcqSession(sessionStart, sessionStop);
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(sessionStart, sessionStop);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);

		//XStream stream = new XStream();
		//System.out.println(stream.toXML(result));
		
		MarkEveryStepTaskSpikeDataEntry e = result.get(task1);
		assertEquals((spike1Up + spike1Down) / 2, e.getTrialStageData(0).getSpikeData()[0]);
		assertEquals(2, e.getTrialStageData().size());
	}
	
	public void testNoDownEdgeAtTheEnd() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/wang");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		TimeUtil timeUtil = new DefaultTimeUtil();
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		short evenMarkerChannel = Short.parseShort(vars.get("acq_even_marker_chan").getValue(0));
		short oddMarkerChannel = Short.parseShort(vars.get("acq_odd_marker_chan").getValue(0));
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		long sessionStart = timeUtil.currentTimeMicros();
		long sessionStop = sessionStart + 100;
		
		dbUtil.writeBeginAcqSession(sessionStart);
		long task1 = sessionStart + 10;
		dbUtil.writeTaskDone(task1, task1, 0);
		
		// even up
		ArrayList<AcqDataEntry> data = new ArrayList<AcqDataEntry>();
		AcqDataEntry ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 0);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Up = 20;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Down = 22;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// even down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 100);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// odd up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 110);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// odd down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 120);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		int spike2Up = 122;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike2Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike2Down = 124;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike2Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 130);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		dbUtil.writeAcqData(sessionStop - 1, data);
		
		dbUtil.writeEndAcqSession(sessionStart, sessionStop);
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(sessionStart, sessionStop);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);

		//XStream stream = new XStream();
		//System.out.println(stream.toXML(result));
		
		MarkEveryStepTaskSpikeDataEntry e = result.get(task1);
		assertEquals((spike1Up + spike1Down) / 2, e.getTrialStageData(0).getSpikeData()[0]);
		
		assertEquals(0, e.getTrialStageData(1).getSpikeData().length);
		assertEquals(2, e.getTrialStageData().size());
	}
	
	public void testImcompleteSpikes() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/wang");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		TimeUtil timeUtil = new DefaultTimeUtil();
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		short evenMarkerChannel = Short.parseShort(vars.get("acq_even_marker_chan").getValue(0));
		short oddMarkerChannel = Short.parseShort(vars.get("acq_odd_marker_chan").getValue(0));
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		long sessionStart = timeUtil.currentTimeMicros();
		long sessionStop = sessionStart + 100;
		
		dbUtil.writeBeginAcqSession(sessionStart);
		long task1 = sessionStart + 10;
		dbUtil.writeTaskDone(task1, task1, 0);
		
		// even up
		ArrayList<AcqDataEntry> data = new ArrayList<AcqDataEntry>();
		AcqDataEntry ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 0);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Up = 20;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Down = 22;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// even down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 100);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// odd up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 110);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike3Down = 111;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike3Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		int spike2Up = 119;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike2Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// odd down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 120);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		dbUtil.writeAcqData(sessionStop - 1, data);
		
		dbUtil.writeEndAcqSession(sessionStart, sessionStop);
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(sessionStart, sessionStop);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);

		//XStream stream = new XStream();
		//System.out.println(stream.toXML(result));
		
		MarkEveryStepTaskSpikeDataEntry e = result.get(task1);
		assertEquals((spike1Up + spike1Down) / 2, e.getTrialStageData(0).getSpikeData()[0]);
		assertEquals(2, e.getTrialStageData().size());
		assertEquals(0.0, e.getSpikePerSec(1), 0.0001);
		assertEquals(0, e.getTrialStageData(1).getSpikeData().length);
	}
	
	public void testNoDownEdgeAtTheEndWithMoreNonMarkerSamples() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/wang");
		dataSource.setUsername("xper_rw");
		dataSource.setPassword("up2nite");

		DbUtil dbUtil = new DbUtil();
		dbUtil.setDataSource(dataSource);
		
		TimeUtil timeUtil = new DefaultTimeUtil();
		
		Map<String,SystemVariable> vars = dbUtil.readSystemVar("%");
		short evenMarkerChannel = Short.parseShort(vars.get("acq_even_marker_chan").getValue(0));
		short oddMarkerChannel = Short.parseShort(vars.get("acq_odd_marker_chan").getValue(0));
		short dataChannel = Short.parseShort(vars.get("acq_data_chan").getValue(0));
		
		long sessionStart = timeUtil.currentTimeMicros();
		long sessionStop = sessionStart + 100;
		
		dbUtil.writeBeginAcqSession(sessionStart);
		long task1 = sessionStart + 10;
		dbUtil.writeTaskDone(task1, task1, 0);
		
		// even up
		ArrayList<AcqDataEntry> data = new ArrayList<AcqDataEntry>();
		AcqDataEntry ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 0);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Up = 20;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike1Down = 22;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike1Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// even down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)evenMarkerChannel);
		ent.setSampleInd ( 100);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		// odd up
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 110);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		// odd down
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 120);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		int spike2Up = 122;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike2Up);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		int spike2Down = 124;
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( spike2Down);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		ent = new AcqDataEntry();
		ent.setChannel ( (short)oddMarkerChannel);
		ent.setSampleInd ( 130);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( 140);
		ent.setValue (DigitalChannel.UP);
		data.add(ent);
		
		ent = new AcqDataEntry();
		ent.setChannel ( (short)dataChannel);
		ent.setSampleInd ( 150);
		ent.setValue (DigitalChannel.DOWN);
		data.add(ent);
		
		dbUtil.writeAcqData(sessionStop - 1, data);
		
		dbUtil.writeEndAcqSession(sessionStart, sessionStop);
		
		List<TaskDoneEntry> tasks = dbUtil.readTaskDoneByTimestampRange(sessionStart, sessionStop);
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		spikeCounter.setDbUtil(dbUtil);

		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> result = spikeCounter.getTaskSpike(tasks, dataChannel);

		//XStream stream = new XStream();
		//System.out.println(stream.toXML(result));
		
		MarkEveryStepTaskSpikeDataEntry e = result.get(task1);
		assertEquals((spike1Up + spike1Down) / 2, e.getTrialStageData(0).getSpikeData()[0]);
		
		assertEquals(0, e.getTrialStageData(1).getSpikeData().length);
		assertEquals(2, e.getTrialStageData().size());
	}
}
