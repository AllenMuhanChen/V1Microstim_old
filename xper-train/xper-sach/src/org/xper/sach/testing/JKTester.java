package org.xper.sach.testing;
import org.xper.sach.drawing.stimuli.Node;

import java.beans.PropertyVetoException;
import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.xper.acq.counter.MarkEveryStepExperimentSpikeCounter;
import org.xper.acq.counter.MarkEveryStepTaskSpikeDataEntry;
import org.xper.acq.counter.TrialStageData;
import org.xper.config.AcqConfig;
import org.xper.config.BaseConfig;
import org.xper.config.ClassicConfig;
import org.xper.exception.DbException;
import org.xper.exception.InvalidAcqDataException;
import org.xper.exception.NoMoreAcqDataException;
import org.xper.sach.acq.counter.SachMarkEveryStepExptSpikeCounter;
import org.xper.sach.analysis.BehCatPrefPlot_2;
import org.xper.sach.analysis.GAGenAnalysisPlot_2;
import org.xper.sach.analysis.SachStimDataEntry;
import org.xper.sach.analysis.SimpleHistogram;
import org.xper.sach.drawing.StimTestWindow;
import org.xper.sach.drawing.stimuli.BsplineObject;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.drawing.stimuli.LimbSpec;
import org.xper.sach.expt.SachExptSpec;
import org.xper.sach.expt.SachExptSpecGenerator;
import org.xper.sach.expt.SachExptSpecGenerator.BehavioralClass;
import org.xper.sach.expt.SachExptSpecGenerator.StimType;
import org.xper.sach.expt.generate.SachRandomGeneration;
import org.xper.sach.expt.generate.SachRandomGeneration.TrialType;
import org.xper.sach.renderer.SachPerspectiveStereoRenderer;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachIOUtil;
import org.xper.sach.util.SachMathUtil;
import org.xper.time.DefaultTimeUtil;
import org.xper.time.SocketTimeClient;
import org.xper.time.TimeUtil;

import com.mchange.v2.c3p0.ComboPooledDataSource;



import org.jfree.ui.RefineryUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.config.java.annotation.Import;
import org.springframework.config.java.annotation.Lazy;
import org.springframework.config.java.annotation.valuesource.SystemPropertiesValueSource;
import org.springframework.config.java.context.JavaConfigApplicationContext;
import org.springframework.config.java.plugin.context.AnnotationDrivenConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.FileSystemUtils;
import org.xper.config.AcqConfig;
import org.xper.sach.config.SachTrainingConfig;
import org.xper.db.vo.StimSpecEntry;
import org.xper.db.vo.SystemVariable;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.time.SocketTimeServer;
import org.xper.util.FileUtil;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;


@Configuration(defaultLazy=Lazy.TRUE)
@SystemPropertiesValueSource
@AnnotationDrivenConfig
@Import(BaseConfig.class)

public class JKTester {

	@Autowired 
	static AcqConfig acqConfig = new AcqConfig();
//	static BaseConfig baseConfig;
//	
	static SachRandomGeneration srg = new SachRandomGeneration();

//	SimpleHistogram shtg = new SimpleHistogram();
	static SachDbUtil dbUtil = new SachDbUtil();
	static private boolean useFakeSpikes = false;
	static int dataChannel = 0;
	static int minNumStages = 0;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		
		ComboPooledDataSource source = new ComboPooledDataSource();
		try {
			source.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			throw new DbException(e);
		}
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/test1");
		source.setJdbcUrl("jdbc:mysql://172.30.6.48/shaggy_ecpc48_2016_07");
//		source.setJdbcUrl("jdbc:mysql://172.30.4.9/jk_test");
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/sach_ecpc48_2014_08_12_recording");
//		source.setJdbcUrl("jdbc:mysql://172.30.4.9/sach_ecpc48_2014_08_12_recording_20161122");
		source.setUser("xper_rw");
		source.setPassword("up2nite");
		dbUtil.setDataSource(source);

		
		List<Long> selectStimId = new ArrayList<Long>();
		List<Long> behStimIds = dbUtil.readStimObjIdsFromGenId(dbUtil.readLong("ExpLog","globalGenId",new String[] {"type,'BEH_exptRepeat'","odl"}));
		
		for (int cc=0;cc<8;cc++){
			for (int mm=0;mm<3;mm++)
				selectStimId.add(behStimIds.get(cc*5+mm));
		}
		
		showStim(false,selectStimId);
		
		
//		try {
//			System.out.println(source.getConnection().getMetaData().getURL());
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		System.out.print(dbUtil.readTableColumnNames("TaskDone"));
		
//		StimSpecEntry ent;
//		ent = dbUtil.readSingleStimSpec(1503511229876583L);
//		System.out.println("done1");
//		ent = dbUtil.readSingleStimSpec(1503511229689901L);
//		System.out.println("done2");
//		ent = dbUtil.readSingleStimSpec(1503511229890114L);
//		System.out.println("done3");
//		ent = dbUtil.readSingleStimSpec(1503511229938525L);
//		System.out.println("done4");
//		ent = dbUtil.readSingleStimSpec(1503511229738792L);
//		System.out.println("done5");
//		ent = dbUtil.readSingleStimSpec(1503511229863918L);
//		System.out.println("done6");
//		ent = dbUtil.readSingleStimSpec(1503511229689901L);
//		System.out.println("done7");
//		ent = dbUtil.readSingleStimSpec(1503511229718396L);
//		System.out.println("done8");
//		
//		dbUtil.resetSource();
//		
//		StimSpecEntry ent2;
//		ent2 = dbUtil.readSingleStimSpec(1503511229876583L);
//		System.out.println("done1");
//		ent2 = dbUtil.readSingleStimSpec(1503511229689901L);
//		System.out.println("done2");
//		ent2 = dbUtil.readSingleStimSpec(1503511229890114L);
//		System.out.println("done3");
//		ent2 = dbUtil.readSingleStimSpec(1503511229938525L);
//		System.out.println("done4");
//		ent2 = dbUtil.readSingleStimSpec(1503511229738792L);
//		System.out.println("done5");
//		ent2 = dbUtil.readSingleStimSpec(1503511229863918L);
//		System.out.println("done6");
//		ent2 = dbUtil.readSingleStimSpec(1503511229689901L);
//		System.out.println("done7");
//		ent2 = dbUtil.readSingleStimSpec(1503511229718396L);
//		System.out.println("done8");
		// ****************meta info from DB**********************
//		String urlAll = source.getJdbcUrl();
//		String[] urlSep = urlAll.split("/");
//		String url = urlSep[urlSep.length-1];
//		System.out.println(url);
		
		
		// ******************list to array*********************
//		List<String> stockList = new ArrayList<String>();
//		stockList.add("stock1");
//		stockList.add("stock2");
//
//		String[] stockArr = new String[stockList.size()];
//		stockArr = stockList.toArray(stockArr);
//		System.out.print(stockArr);
		
//		List<String> hi = new ArrayList<String>();
//		hi.add("hi");
//		hi.add("you");
//		String[] hia = dbUtil.toArray(hi);
//		System.out.print(hia);
//		Long[] hia = dbUtil.toArray(hi);
//		System.out.print(hia);

		// *************************new sql Query/Write functions*************
//		long activeCellNum = dbUtil.readLong("ExpRecTargets","isFinished=0;cellNum;-1",new String[]{"order,cellNum,desc" , "limit"});
//		System.out.println(activeCellNum);
		
//		String hi = new String(dbUtil.readDouble("ExpRecTargets","coordsAS_dist",new String[]{"limit"}).toString());
//		System.out.println(hi);
		
//		List<String> hi = dbUtil.readTrialOutcomeNearestTimeStamp(1467913968170902L);
//		System.out.println(hi);
		
//		dbUtil.updateLine("SessionLog","drivenDistance",new Object[]{16.55},new String[] {"dateTime,20170816192458"});
//		dbUtil.writeLine("ExpRecTargets",new Object[] {20170899999L, 1.0,2.0,3.0,4.0,5.0,6.0, "'hello'", 0});
//		
//		List<Long> bla = dbUtil.readRowsLong("GenId_to_StimObjId","stimObjId",dbUtil.sArray(5091L));
//		List<Long> bla = dbUtil.readRowsLong("TaskDone d, TaskToDo t","d.tstamp",new String[] {"d.task_id,t.task_id" , "t.gen_id,5100"});
//		System.out.println(bla);
		
		//combine beh and GA plotting
//		plotGenData();
//		String hi = "heythereyou";
//		for (String retval: hi.split("-")) {
//	         System.out.println(retval);
//	      }
		
		//*********** simple input testing***************
//		String newVal = SachIOUtil.promptString("Enter new value:");
//		long value = Long.parseLong(newVal);
//		System.out.println(value);
		
		//***********ONE TIME ONLY!!  post-writing BEH_quick & BEH_exptRepeat into GenId_to_StimObjId***********//
		
		// cells I did NOT process yet (all were not useful data):  
		// 20170804000
		// 20170808001
		// 20170816001
		// 20170816002
		// 20170816003
		// 20170816004
		
		
		// for genId 5091, BEH_exptRepeat, cell 20170816000
//		long genId = 5091;
//		String type = "BEH_exptRepeat";
//		long start = 1502911870373719L;
//		long end = 1502913977559508L;
		
		// for genId 5084, BEH_quick, cell 20170816000
//		long genId = 5084;
//		String type = "BEH_quick";
//		long start = 1502909569839551L;
//		long end = 1502910285415345L;
		
		// for genId 5039, BEH_exptRepeat, cell 20170810001 
//		long genId = 5039;
//		String type = "BEH_exptRepeat";
//		long start = 1502406729424997L;
//		long end = 1502408281706100L;
		
		// for genId 5033, BEH_quick #2, cell 20170810001
//		long genId = 5033;
//		String type = "BEH_quick";
//		long start = 1502404818504506L;
//		long end = 1502405164549222L;
		
		// for genId 5032, BEH_quick #1, cell 20170810001
//		long genId = 5032;
//		String type = "BEH_quick";
//		long start = 1502403877111564L;
//		long end = 1502404983119139L;
		
		// for genId 5029, BEH_exptRepeat, cell 20170810000
//		long genId = 5029;
//		String type = "BEH_exptRepeat";
//		long start = 1502401674257913L;
//		long end = 1502403634317162L;
		
		// for genId 5022, BEH_quick, cell 20170810000
//		long genId = 5022;
//		String type = "BEH_quick";
//		long start = 1502398742564765L;
//		long end = 1502399570390201L;
		
		// for genId 5006, BEH_exptRepeat, cell 20170808004
//		long genId = 5006;
//		String type = "BEH_exptRepeat";
//		long start = 1502231771835396L;
//		long end = 1502301127903594L;
		
		// for genId 5002, BEH_quick, cell 20170808004.  NOTE: this was the final behquick genId to feature 24 stims instead of 32
//		long genId = 5002;
//		String type = "BEH_quick";
//		long start = 1502230153267423L;
//		long end = 1502230998749103L;
		
		// for genId 4998, BEH_quick #2, cell 20170808003
//		long genId = 4998;
//		String type = "BEH_quick";
//		long start = 1502228502839880L;
//		long end = 1502230802882049L;
		
		// for genId 4997, BEH_quick #1, cell 20170808003
//		long genId = 4997;
//		String type = "BEH_quick";
//		long start = 1502218008879435L;
//		long end = 1502228660336808L;
		
		// for genId 4981, BEH_exptRepeat, cell 20170808002
//		long genId = 4981;
//		String type = "BEH_exptRepeat";
//		long start = 1502219373299378L;
//		long end = 1502221205040807L;
		
		// for genId 4976, BEH_quick, cell 20170808002
//		long genId = 4976;
//		String type = "BEH_quick";
//		long start = 1502217786773844L;
//		long end = 1502218156338251L;
		
		// for genId 4963, BEH_quick, cell 20170808000
//		long genId = 4963;
//		String type = "BEH_quick";
//		long start = 1502212426455512L;
//		long end = 1502212924510369L;
		
		// for genId 4949, BEH_exptRepeat, cell 20170804001
//		long genId = 4949;
//		String type = "BEH_exptRepeat";
//		long start = 1501888206948749L;
//		long end = 1501889743247751L;
		
		// for genId 4942, BEH_quick #2, cell 20170804001
//		long genId = 4942;
//		String type = "BEH_quick";
//		long start = 1501885147199148L;
//		long end = 1501885517761053L;
		
		// for genId 4941, BEH_quick #1, cell 20170804001
//		long genId = 4941;
//		String type = "BEH_quick";
//		long start = 1501884574995078L;
//		long end = 1501885350535612L;
		
		
//		List<Long> stimObjIds = dbUtil.readSubsetStimObjIds(start,end,type);
//		dbUtil.writeStimObjIdsForEachGenId(genId,stimObjIds);
		
//		long genId = ;
//		String type = "";
//		long start = L;
//		long end = L;
		
		
		//**********************testing new BehCatPrefPlot_2************
//		long genId = 5084L; //beh_quick for cell 20170816000
//		long genId = 5091L; //exptRepeat for cell 20170816000
//		new BehCatPrefPlot_2(genId,dbUtil,"Run");
//		plotBeh.run();
//		plotBeh.pack();
//		RefineryUtilities.centerFrameOnScreen(plotBeh);
//		plotBeh.setVisible(true);

		// **************************GA plotting*************************
//		List<Long> genIds = dbUtil.readGAGenSetFromExpLog(5090);
//		System.out.println(genIds);
//		System.out.println(genIds.get(genIds.size()-1));
		
//		GAGenAnalysisPlot_2 plotGA = new GAGenAnalysisPlot_2(dbUtil);
//		plotGA.run(5090);
//		plotGA.pack();
//		RefineryUtilities.centerFrameOnScreen(plotGA);
//		plotGA.setVisible(true);
//		
		// **************get active cellNum from ExpRecTargets
		
//		System.out.print(dbUtil.readActiveCellNum());
//		System.out.print(dbUtil.readActiveCellDistance());
		
		
		
		// **********************load png into byte[]*******************
		
//		String pathname = System.getProperty("user.dir") + "/images/manualTemp/temp0.png";
//		System.out.print(pathname + "\n");
//		File imageFile = new File(pathname);
//		System.out.print(imageFile.exists() + "\n");
//		BufferedImage img = ImageIO.read(imageFile);
//		int imgWidth = img.getWidth();
//		int imgHeight = img.getHeight();
//		byte[] src = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
//		System.out.print("done");
		
		/////////////////
//		Long stimObjId = 1502469554614708L;
//		String pngFileName = findFullPngFileName(stimObjId);
//		System.out.print(pngFileName);
		
		/////////////////
//		dbUtil.readThumbnail(stimObjId);
		/////////////////
		
//		dbUtil.loadTexture();
		
		
		//********************updateSpecParam infinite loop...********************
//		Long curStim = 1501016167661502L;
//		BsplineObjectSpec s = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(curStim).getSpec());
//		s.setDoMorph(true);
//		BsplineObject obj = new BsplineObject();
//		obj.setSpec(s.toXml());
//		
//		System.out.print("done");
		
		//*******************test directory maker********************************
//		String pngPath = System.getProperty("user.dir") + "/images/GA/test/test2";
//		myMkDir(pngPath);
		
		//**********************************************
//		List<Long> allStimObjIds = new ArrayList<Long>();
	
//		List<Long> GAgenIds 	= dbUtil.readGAGenIdFromExpLog();
//		System.out.print(GAgenIds);
//		Long prevGenId = GAgenIds.get(0) + 1;
//		for (int gg=0;gg<GAgenIds.size();gg++){
//			System.out.print("\n" + GAgenIds.get(gg) + " "); 
//			System.out.print((GAgenIds.get(gg)-prevGenId)==0);
//			if (GAgenIds.get(gg) - prevGenId == 0){
//				break;
//			}
//			allStimObjIds.addAll(dbUtil.readNonBlankStimObjIdsFromGenId(GAgenIds.get(gg)));
//			prevGenId = GAgenIds.get(gg);
//		}
		
//		System.out.print(allStimObjIds.size());
//		List<Long> stimObjIds = new ArrayList<Long>();
//		stimObjIds.add(1500573780192900L);
////		stimObjIds.add(1500574369175062L);
//		
//		showStim(false,stimObjIds);
		
		
		//*********************** MANUAL GA PROTOTYPE*******************
//		int indices = 3;
//		int[] indices   = new int[]{3,4};
//		
//		List<Long> stimId = recoverStimId(584,indices);
//		
//		showStim(true,stimId);
//		
//		ManualEditGA(stimId);

		//*******************************************************************
//		July2017
//		repeatExptTesting();
		//*******************************************************************
		//July2017
//		long currentGen = 4949;
//		getSpikeResponses(currentGen,true);
		
//		***June 2017
//		BufferedImage bi = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
//		Graphics g = bi.getGraphics();
//		g.drawString("www.tutorialspoint.com",20,20);
//		
//		editGA();
//		*******
		// ********************* May 2017***********************************
//		int currentGen = 4610;
//		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
//		spikeCounter.setDbUtil(dbUtil);
//		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
//		spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, 0);
//		List<Double> blankFRs = new ArrayList<Double>();
//		long taskId;
//		long stimObjId;
//		BsplineObjectSpec spec;
//		int entIdx;	
//		
//		for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
//		{
//			MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();
//			taskId = ent.getTaskId();
//			System.out.println("taskId: " + taskId);
//			SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
////			for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
//			int n = 0;
//			
//				stimObjId = trialSpec.getStimObjId(n);
//				spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(stimObjId).getSpec());
//				System.out.println("stimId: " + stimObjId);
//				System.out.println("spike: " + ent.getSpikePerSec());
////				if (spec.isBlankStim()) {
////					entIdx = 2*n+2;
////					blankFRs.add(ent.getSpikePerSec(entIdx)); 
////				}
////			}
//			
//		}
		// *************************************************************************************
		
		// ***************** test modifying a data field (from StimObjData table) and updating it in the database (May/29/2017 AWC)**************
//		Double dummy = 1495828364832207.0;
//		long stimObjId = dummy.longValue();
//		dummy = 1495828364842761.0;
//		long taskId = dummy.longValue();
//		
//		List<Double> blankFRs = new ArrayList<Double>();
//		blankFRs.add(1.0);
//		blankFRs.add(2.0);
//		
//		SachStimDataEntry data;
//		data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
//		data.addTaskDoneId(taskId);
//		data.setSampleFrequency(1.0);
//		data.addSpikesPerSec(2.0);
//		data.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
////		data.addTrialStageData(ent.getTrialStageData(entIdx));
//		dbUtil.updateStimObjData(stimObjId, data.toXml());
		//*************************************************************************************************************************************
		
		//getSpikeResponses(454);
//		int currentGen = 466;
		// 415 is empty
		// 416 has data
		// 208 has data
		// 3760 has data
		
//		int MaxGenId = 417;
//		
//		for(int i = 416; i < MaxGenId; i++){
//			updateStimObjData(i);
//		}

// JK 31 Jan 2017 : this chunk updates stimObjData		
//		showTrialStageHistogram(currentGen, dataChannel);
//		minNumStages = SachIOUtil.promptInt("enter minimum number of stages [0 to abort]");
//		
//		if(minNumStages > 0){
//			updateStimObjData(currentGen);
//			System.out.println("finished");
//		} else {
//			System.out.println("aborted ...");
//		}
	}
	
	
	public void plotGenData() {
		List<String> behTrials =new ArrayList<String>(); 
		long genId = SachIOUtil.promptInt("genId ");
		
		
	}


	public void loadTexture() {
		
		File imageFile = new File(findFullPngFileName(1L));
		BufferedImage img = null;
		try {
			img = ImageIO.read(imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		imgWidth = img.getWidth();
//		imgHeight = img.getHeight();
		byte[] src = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();

		bgr2rgb(src);

		ByteBuffer pixels = (ByteBuffer)BufferUtils.createByteBuffer(src.length).put(src, 0x00000000, src.length).flip();

//		return pixels; 
	}
	
	void bgr2rgb(byte[] target) {
		byte tmp;

		for(int i=0x00000000; i<target.length; i+=0x00000003) {
			tmp = target[i];
			target[i] = target[i+0x00000002];
			target[i+0x00000002] = tmp;
		}
	}


	
	private static String findFullPngFileName(Long stimObjId) {
		return findFullPngFileName(stimObjId,System.getProperty("user.dir") + "/images");
	}
	private static String findFullPngFileName(Long stimObjId,String dir) {
		String fileName = "stim_" + stimObjId;
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	File curFile = listOfFiles[i];
	    	String curName = curFile.getName();
	    	String curFullName = dir + "/" + curName;
	    	if (curFile.isFile()) {
	    		if (curName.equals(fileName)) return curFullName;   
	    	}
	    	else if (curFile.isDirectory()) {
	    		String subOut = findFullPngFileName(stimObjId,curFullName);
	    		if (subOut!=null) return subOut;
	    	}
	    }
		return null;
	}

	public static void myMkDir(String pathInp){
		File curDir = new File(pathInp);
		File parentDir;
		
		while (!curDir.exists()){
			parentDir = curDir.getParentFile();
			if (parentDir.exists()) curDir.mkdir();
			else myMkDir(curDir.getParent());
		}
		
	}
	
	public static void repeatExptTesting(){
		//JKTester vars only
		double distance = 600;
		double deg = Math.atan(100 / distance) * 180.0 / Math.PI;
		Coordinates2D currentTargetLocation = new Coordinates2D(0,deg);
		double currentWindowSize = 4;
		int[] nonMatchCats   = new int[]{8,9,10,11,12,13,14,15};	
		SachExptSpecGenerator generator = new SachExptSpecGenerator();
		generator.setDbUtil(dbUtil);
		generator.setGlobalTimeUtil(acqConfig.timeClient());
		generator.setTrialType(TrialType.BEH_exptRepeat);


		//Initialize trial structure parameters:
		int numCats = nonMatchCats.length;
		int numMorphsPerCat = 3;  					//3 levels of morph for each category
		int numMorphsPlusCan = numMorphsPerCat+1;	//in addition to morph, 1 canonical version
		int numStim = numCats*numMorphsPlusCan; 	//total number of unique stimuli; default is 8*(3+1) = 32
		int numRepeats = 5; 						//repeats for each stimulus (same as GA or beh_quick)
		int numTrials = numCats*numMorphsPerCat*numRepeats; 	//total number of trials; default is 8*3*5 = 120

		//variables for stimuli spec generation:
		StimType type = StimType.BEH_MedialAxis;
		BehavioralClass behClassMatch 		= BehavioralClass.MATCH;
		BehavioralClass behClassNonMatch 	= BehavioralClass.NON_MATCH;
		BehavioralClass behClassSample 		= BehavioralClass.SAMPLE;

		//read in percMatch values (for this protocol, NO halfMatches)
		Map<String, SystemVariable> valMap = dbUtil.readSystemVar("%training_%");
		double percMatches = Double.parseDouble(valMap.get("xper_training_percent_match").getValue(0));
		double percNonMatch = 1.0-percMatches;
		generator.updateTrainingVariables();

		// Initialize idx vectors
		int[] stimIdxVec = new int[numStim]; 			//vector of length "numStim" designating the category # of each stimuli
		int[] trialTestVec = new int[numTrials]; 		//vector of length "numTrials" designating the stimuli Idx of each trial TEST period (morphs only)
		int[] trialSampleVec = new int[numTrials];      //vector of length "numTrials" designating the stimuli Idx of each trial SAMPLE period (canonical only)
		Integer[] trialIdxVec = new Integer[numTrials]; //vector of length "numTrials" simply designating trial number (to be shuffled later)
		BsplineObjectSpec[] testMatchSpecVec = new BsplineObjectSpec[numCats*numMorphsPerCat]; 		//Initialize morphed matched stimuli vector 
		BsplineObjectSpec[] testNonMatchSpecVec = new BsplineObjectSpec[numCats*numMorphsPerCat];   //Initialize morphed nonMatch stimuli vector
		BsplineObjectSpec[] sampleSpecVec = new BsplineObjectSpec[numCats]; 						//Initialize canonical (sample) stimuli vector
		List<SachExptSpec> behTrials =new ArrayList<SachExptSpec>(); 	//for later...Initialize behTrial list

		//populate vectors, create stimuli
		int morphCnt = 0;
		int trialCnt = 0;
		for (int cc=0;cc<numCats;cc++){
			for (int mm=0;mm<numMorphsPlusCan;mm++){
				//Insert category into stimIdxVec
				stimIdxVec[morphCnt]=cc;
				morphCnt++;

				if(mm<numMorphsPerCat){ 	//for morphed stimuli...
					//insert "numRepeats" copies of the appropriate stimuliIdx into trialTestVec and trialSampleVec. Populate trialIdxVec
					for (int tt=0;tt<numRepeats;tt++){
						trialTestVec[trialCnt]=cc*4+mm;
						trialSampleVec[trialCnt]=cc*4+numMorphsPerCat;
						trialIdxVec[trialCnt]=trialCnt;
						trialCnt++;
					}

					//create morphed stimuli (match and nonMatch)
					testMatchSpecVec[cc*3+mm] 		= generator.createMorphedBehavStimFromCat(type, cc, behClassMatch, 		generator.getTrialType(),false);
					testNonMatchSpecVec[cc*3+mm] 	= generator.createMorphedBehavStimFromCat(type, cc, behClassNonMatch, 	generator.getTrialType(),false);
				}
				else { 	//for canonical stimuli...
					//create canonical stimuli
					sampleSpecVec[cc] = generator.createBehavStimFromCat(type,cc,behClassSample,generator.getTrialType());
				}
			}
		}

		//number of trial pairs to swap the sample period (to create nonMatch trials)
		int numNonMatchPairs = (int) Math.round((double)numTrials/2*percNonMatch);

		//shuffle trialIdx
		List<Integer> shuffleTrialIdx = Arrays.asList(trialIdxVec);
		Collections.shuffle(shuffleTrialIdx);


		for (int tt=0;tt<numNonMatchPairs;tt++){  //to swap each pair...
			//the first trial of the pair is taken from the "front" of the shuffled list
			int frontTrialIdx = shuffleTrialIdx.get(tt);
			//isolate the stimIdx and the category # of this trial
			int frontSampleVal = trialSampleVec[frontTrialIdx];
			int frontSampleCat = (int) Math.floor((double)frontSampleVal/4);

			//search from the "back" of the shuffled list until...
			boolean done = false;
			int idx = numTrials-1;
			int backSampleVal = 0;
			while(!done){
				int backTrialIdx = shuffleTrialIdx.get(idx);
				int backTestVal = trialTestVec[backTrialIdx];
				backSampleVal = trialSampleVec[backTrialIdx];
				int backTestCat = (int) Math.floor((double)backTestVal/4);
				int backSampleCat = (int) Math.floor((double)backSampleVal/4);

				//a match trial that does not have the same category as the first trial is found
				done = (backTestCat==backSampleCat) && (frontSampleCat !=backSampleCat);
				if (!done){
					idx--;
				}
			}

			//swap the sample stimIdx of between the trial pair
			trialSampleVec[frontTrialIdx] = backSampleVal;
			trialSampleVec[shuffleTrialIdx.get(idx)] = frontSampleVal;
		}

		//sanity check
		int sumDiff=0;
		for (int tt=0;tt<numTrials;tt++){
			if ((int) Math.floor((double)trialTestVec[tt]/4)!=(int) Math.floor((double)trialSampleVec[tt]/4)){
				sumDiff++;
			}
		}
		System.out.print(sumDiff);

		// Make TRIALS:

		//shuffle trialIdx again
		Collections.shuffle(shuffleTrialIdx);


		for (int tt=0;tt<numTrials;tt++){
			int trialIdx = shuffleTrialIdx.get(tt);
			//determine whether trial isMatch
			boolean isMatch = ((int) Math.floor((double)trialTestVec[trialIdx]/4)==(int) Math.floor((double)trialSampleVec[trialIdx]/4));

			SachExptSpec spec;
			if (isMatch){
				//write the generated MATCH test stimuli to the database, then create the match trialSpec
				generator.writeSpecToDatabase(testMatchSpecVec[trialIdx]);
				//						long stimObjId = testMatchSpecVec[trialIdx].getStimObjId();
				spec = generator.createBehTrial(testMatchSpecVec[trialIdx].getStimObjId(), sampleSpecVec[trialIdx].getStimObjId(), 0);
			}
			else {
				//write the generated NONMATCH test stimuli to the database, then create the nonMatch trialSpec
				generator.writeSpecToDatabase(testNonMatchSpecVec[trialIdx]);
				spec = generator.createBehTrial(testNonMatchSpecVec[trialIdx].getStimObjId(), sampleSpecVec[trialIdx].getStimObjId(), -1);
			}
			spec.setTargetPosition(currentTargetLocation);
			spec.setTargetEyeWinSize(currentWindowSize);
			behTrials.add(spec);
		}
				
	}
	
	
	public static void getSpikeResponses(long currentGen){
		getSpikeResponses(currentGen,false);
	}
	
	public static void getSpikeResponses(long currentGen,boolean isBeh) {
		
		long lastTrialToDo;
		long lastTrialDone;

		// Wait for spike data collection to be completed:	
		System.out.print("Waiting for ACQ process.");
		lastTrialToDo = dbUtil.readTaskToDoMaxId();	// move this outside loop?
		lastTrialDone = dbUtil.readTaskDoneCompleteMaxId(isBeh);
		if ( lastTrialToDo != lastTrialDone) { // Completed the tasks in this generation:
			System.out.print("TrialDone~=TrialToDo");
			return;
		}

		// obtain spike data:
		long taskId;

		// use mine because it adds fake spike stuff!
		//MarkStimExperimentSpikeCounter spikeCounter = new MarkStimExperimentSpikeCounter();
		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
		spikeCounter.setDbUtil(dbUtil);

		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
			if (useFakeSpikes) {
				// this populates fake spike rates for trials 
				spikeEntry = spikeCounter.getFakeTaskSpikeByGeneration(currentGen);
			} else {
				spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, 0);
			}
			// for each trial done in a generation:
				// get blank FRs:
			List<Double> blankFRs = new ArrayList<Double>();
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
			{
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
				taskId = ent.getTaskId();
				
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial:
				long stimObjId;
				BsplineObjectSpec spec;
				int entIdx;				// MarkEveryStepTaskSpikeEntry gives the following epochs:
										//    [ fixation_pt_on, eye_in_succeed, stim, isi, ... (repeat x numStims), done_last_isi_to_task_end ]
										//    so to index the stimuli we skip the first 2 and do every other for as many stims as we present in a trial

				// first get blank stim FR data:
				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
					stimObjId = trialSpec.getStimObjId(n);
					spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(stimObjId).getSpec());
					
					if (spec.isBlankStim()) {
						entIdx = 2*n+2;
						blankFRs.add(ent.getSpikePerSec(entIdx)); 
					}
				}
			}
			
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet())
			{
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
				taskId = ent.getTaskId();

				System.out.println("Entering spike info for trial: " + taskId);
				
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial get FR data for all stims and save:
				long stimObjId;
				SachStimDataEntry data;
				int entIdx;

				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
					stimObjId = trialSpec.getStimObjId(n);
					data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
					
					// add acq info:					
					entIdx = 2*n+2;
					data.addTaskDoneId(taskId);
					data.setSampleFrequency(ent.getSampleFrequency());
					data.addSpikesPerSec(ent.getSpikePerSec(entIdx));
					data.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
					data.addTrialStageData(ent.getTrialStageData(entIdx));
					
					// resave data:
//					dbUtil.updateStimObjData(stimObjId, data.toXml());
					dbUtil.updateLine("StimObjData","data",data.toXml(),new String[]{"id,"+stimObjId});
				}
			}	
		} catch(InvalidAcqDataException ee) {
			ee.printStackTrace();
		} catch(NoMoreAcqDataException ee) {
			ee.printStackTrace();
		}
	}
	
	static public void ManualEditGA(List<Long> stimIds){
		List<BsplineObjectSpec> specs 		=  dbUtil.readListStimSpecs(stimIds);
		List<BsplineObjectSpec> finalSpecs 	=  new ArrayList<BsplineObjectSpec>();
		StimTestWindow testWindow = initTestWindow(700,600,true,1.0);
		testWindow.create();
		boolean doneAll = false;
		while (!doneAll){
			int s = SachIOUtil.promptInt("\nWhich spec? [0 through " + (specs.size()-1) + "], -1 to terminate, -2 to clear all morphs: ");
			if (s==-1){
				doneAll = true;
			}
			else if (s==-2){
				finalSpecs.clear();
			}
			else if (s>=0 && s<specs.size()){
				BsplineObjectSpec curOrigSpec = specs.get(s);
				List<BsplineObjectSpec> curSpecList = new ArrayList<BsplineObjectSpec>();
				List<BsplineObjectSpec> curMorphList = new ArrayList<BsplineObjectSpec>();
				curSpecList.add(curOrigSpec);
				
				int n;
				boolean doneStim = false;
				while(!doneStim){
					boolean isMorphAction = true;
					char c = SachIOUtil.prompt("Current object list length = " + curSpecList.size() + 
							"\nChoose action:" +
							"\n  (c) canonicalize" +  	//probably the first action: make all widths the same
							"\n  (a) add limb (single object only)" + 		//
						 	"\n  (r) remove limb (single object only)" + 	// 
							"\n  (o) rotate" + 			// 
						 	"\n  (s) change size" +		//
						 	"\n  (v) view current-stim morphs" +
						 	"\n  (w) view all morphs" +
							"\n  (d) done with stim, COMMIT CHANGES" +	// done with morphs to this stim, return to main menu
						 	"\n  (e) done with stim, CANCEL CHANGES" + // cancel all changes (do not put curMorph into finalSpecs)
							"\n" );
					
					switch (c) {
						case 'c':
							for (int ss=0;ss<curSpecList.size();ss++){
								BsplineObject newObj = new BsplineObject();
								newObj.setSpec(curSpecList.get(ss).toXml());
								newObj.createObjFromSpec();
								newObj.canonicalize();
								newObj.updateSpecParams();
								newObj.createObjFromSpec();
								curMorphList.add(newObj.getSpec());
							}
							break;
						case 'a':
							if (curSpecList.size()!=1){
								break;
							}
							BsplineObject curObj = new BsplineObject();
							curObj.setSpec(curSpecList.get(0).toXml());
							curObj.createObjFromSpec();
							n = SachIOUtil.promptInt("\nselect Node: ");
							int limbID = n>0 ? n-1 : 0;
							
							double[] L = new double[1];
							double 	l = SachIOUtil.promptDouble("\nset length (-2 for same, -1 for random, 0 for range):");
							if (l==-2){
								l = curObj.getLimbs(limbID).getLength();
								L[0] = l;
							}
							else if(l==-1){
								l = SachMathUtil.randRange(6,2);
								L[0] = l;
							}
							else if(l==0){
								L = promptList();
							}
							else {
								L[0] = l;
							}
							double[] W = new double[1];
							double w = SachIOUtil.promptDouble("\nset width (-2 for same, -1 for random, 0 for range): ");
							if (w==-2){
								w = curObj.getNodes(n).getWidth();
								W[0] = w;
							}
							else if(w==-1){
								w = SachMathUtil.randRange(3.5,.4);
								W[0] = w; 
							}
							else if(w==0){
								W = promptList();
							}
							else {
								W[0] = w;
							}
							double[] A = new double[1];
							double a = SachIOUtil.promptDouble("\nset ori (-1 for range): ");
							if(a==-1){
								A = promptList();
							}
							else{
								A[0] = a;
							}
//							double [] curv = new double[4];
//							curv[0] = .063;curv[1]=.948;curv[2]=.273;curv[3]=1.576;
							for (int ll=0;ll<L.length;ll++){
								for (int ww=0;ww<W.length;ww++){
									for(int aa=0;aa<A.length;aa++){
										BsplineObject newObj = new BsplineObject();
										newObj.setSpec(curSpecList.get(0).toXml());
										newObj.createObjFromSpec();
										newObj.addManualLimbHandler(n,L[ll],W[ww],A[aa]);
//										newObj.addRandLimbWithSplit(1,false);
										newObj.updateSpecParams();
										newObj.createObjFromSpec();
										curMorphList.add(newObj.getSpec());
										
//										LimbSpec limb = newObj.getLimbs(4);
										
//										BsplineObject newObj2 = new BsplineObject();
//										newObj2.setSpec(curOrigSpec.toXml());
//										newObj2.createObjFromSpec();
//										newObj2.addManualLimbHandler(n,L[ll],W[ww],A[aa]);
//										newObj2.updateSpecParams();
//										newObj2.createObjFromSpec();
//										finalSpecs.add(newObj2.getSpec());
										
//										if (newObj2.addLimb(n,limb.getLength(),limb.getWidth(),limb.getOri(),limb.getCurv(),limb.isSmoother())){
//											newObj2.getLimbs().add(new LimbSpec(n,limb.getLength(),limb.getWidth(),limb.getOri(),limb.getCurv(),limb.isSmoother()));
////										if (newObj2.addLimb(n,L[ll],W[ww],A[aa],curv,true)){
//											newObj2.updateSpecParams();
//											if (newObj2.createObjFromSpec()){
//												finalSpecs.add(newObj2.getSpec());
//											}
//										}
									}
								}
							}
							break;
						case 'r':
							if (curSpecList.size()!=1){
								break;
							}
							BsplineObject newObj = new BsplineObject();
							newObj.setSpec(curSpecList.get(0).toXml());
							newObj.createObjFromSpec();
							int localNumLimbs = newObj.getLimbs().size();
//							int n;
							n = -1;
							boolean done = false;
							while(!done){
								n = SachIOUtil.promptInt("\nselect Node: ");
								done = true;
								for (int ll=n;ll<localNumLimbs;ll++){
									if (n==newObj.getLimbs(ll).getNodeId()){
										System.out.print("\nnot an end node!");
										done = false;
										break;
									}
								}
							}
//							if(n!=0){
//								n--;
//							}
							newObj.removeEndLimb(n);
							newObj.updateSpecParams();
							newObj.createObjFromSpec();
							curMorphList.add(newObj.getSpec());
							break;
						case 'o':
							double[] Ori = new double[1];
							double ori = SachIOUtil.promptDouble("\n change Ori (-1 for range): ");
							if (ori==-1){
								Ori = promptList();
							}
							else Ori[0] = ori;
							
							for (int ss=0;ss<curSpecList.size();ss++){
								for (int oo=0;oo<Ori.length;oo++){
									BsplineObject newObj1 = new BsplineObject();
									newObj1.setSpec(curSpecList.get(ss).toXml());
									newObj1.createObjFromSpec();
									newObj1.setGlobalOri(newObj1.getGlobalOri()+Ori[oo]);
									newObj1.updateSpecParams();
									newObj1.createObjFromSpec();
									curMorphList.add(newObj1.getSpec());
								}
							}
							break;
						case 's':
							double[] Size = new double[1];
							double size = SachIOUtil.promptDouble("\n Size (-1 for range): ");
							if (size==-1){
								Size = promptList();
							}
							else Size[0] = size;
							
							for (int ss=0;ss<curSpecList.size();ss++){
								for (int si=0;si<Size.length;si++){
									BsplineObject newObj1 = new BsplineObject();
									newObj1.setSpec(curSpecList.get(ss).toXml());
									newObj1.createObjFromSpec();
									newObj1.setSize(Size[si]);
									newObj1.updateSpecParams();
									newObj1.createObjFromSpec();
									curMorphList.add(newObj1.getSpec());
								}
							}
							break;
						case 'v':
							showStim(curSpecList,false,testWindow);
							isMorphAction = false;
							break;
						case 'w':
							showStim(finalSpecs,false,testWindow);
							isMorphAction = false;
							break;
						case 'd':
							for (int ll=0;ll<curSpecList.size();ll++){
								finalSpecs.add(curSpecList.get(ll));
							}
							curSpecList.clear();
							doneStim = true;
							isMorphAction = false;
							break;
						case 'e':
							curSpecList.clear();
							doneStim = true;
							isMorphAction = false;
							break;
					}
					if (isMorphAction){
						curSpecList.clear();
						for (int ll=0;ll<curMorphList.size();ll++){
							curSpecList.add(curMorphList.get(ll));
						}
					}
					curMorphList.clear();
				}
			}
			
		}
		showStim(finalSpecs,false,testWindow);
	}
	
	static public double[] promptList(){
		double b1 = SachIOUtil.promptDouble("lowerBound: ");
		double b2 = SachIOUtil.promptDouble("upperBound: ");
		double s  = SachIOUtil.promptDouble("step size:  ");
		double[] out = new double[(int) ((b2-b1)/s + 1)];
		int cnt = 0;
		for (double oo=b1;oo<=b2;oo=oo+s){
			out[cnt] = oo;
			cnt++;
		}
		return out;
	}
	
	static public void editGA_old() {
		
		// get all stimObjIds		
		List<Long> objIds =  recoverStimId(584,3);
		System.out.println(objIds.size());
		// just use NumToEdit
		int NumToEdit = 1;
		int count = 0;
		if(objIds.size() < 5){
			NumToEdit = objIds.size();
		}
	
		// get the specs for these object ids
		List<BsplineObjectSpec> specs =  dbUtil.readListStimSpecs(objIds);
		
		// avoid stimType == BLANK so can't use subList 
		List<BsplineObjectSpec> specsToEdit = new ArrayList<BsplineObjectSpec>();  // objIds.subList(0, NumToEdit - 1);
			
		for(BsplineObjectSpec spec : specs){
			
			if(!spec.isBlankStim()){
				System.out.println(spec.getStimObjId());
				specsToEdit.add(spec);
				count++;
				if(count >= NumToEdit){
					break;
				}
			} else {
				System.out.println("skipped BLANK : " + spec.getStimObjId());
			}
		}
		

		if(specsToEdit.isEmpty()){
			System.out.println("nothing found ...");
			return;
		}
		System.out.println(specsToEdit.size() + " objects");
		
//		// create and edit the BsplineObject ...	
//		for(BsplineObjectSpec s : specsToEdit){
//			BsplineObject bso = new BsplineObject();
//			bso.setSpec(s.toXml());
//						
//			
//					
//		}
		
		
		
		
		
		// display the new stimuli		
		int windowWidth = 700;
		int windowHeight = 600;
		double magLevel = 6.0;  // used inside the TestWindow ...
		
		StimTestWindow testWindow = new StimTestWindow(windowHeight, windowWidth , magLevel);
		testWindow.setSpeedInSecs(1.0);
		testWindow.setGridOn(true);				
				
		// do this early to initialize the pngMaker
	//	testWindow.shouldSavePngFile(false);
	//	testWindow.setPngPath(pngPath);
		for(BsplineObjectSpec s : specsToEdit){
			System.out.println(s.toXml());
			BsplineObject bso = new BsplineObject();
			bso.setSpec(s.toXml());
			testWindow.setStimObjs(bso);		
			
			BsplineObject bsoM = new BsplineObject();
			bsoM.setSpec(s.toXml());
			
			bsoM.createMorphObj(0);
//			bsoM.getLimbs().get(2).setWidth(1);
//			bsoM.getLimbs().get(1).setWidth2(1);

			
			bsoM.updateSpecParams();
//			s.setAllLimbSpecs(bsoM.getLimbs());
//			
//			s.setSize(bsoM.getSize());
//			s.setGlobalOri(bsoM.getGlobalOri());
//			
//			bsoM.setSpec(s.toXml());
			
			boolean bla = bsoM.createObjFromSpec();
			System.out.println(bla);
			
//			bso.updateSpecLimbs();
			testWindow.setStimObjs(bsoM);			
//			System.out.println(bsoM.getSpec().toXml());
			
			
			
		}
		
		testWindow.testDraw();
						
	}

	static public void test(){
		List<int[]> foo = new ArrayList<int[]>();
		
		foo.add(new int[]{0, 2});
		foo.add(new int[]{34, 65});
		
		
		System.out.println("foo[0] = " + foo.get(0)[0] + ", " + foo.get(0)[1]);
		System.out.println("foo[1] = " + foo.get(1)[0] + ", " + foo.get(1)[1]);
	}
	
	
	
	static public void testDatabase(){
		double	percMatches = -0.1;
		double percentHalfCommon = -0.1;
		
		Map<String, SystemVariable> valMap = dbUtil.readSystemVar("%training_%");
		try{
			percMatches =Double.parseDouble(valMap.get("xper_training_percent_match").getValue(0));
			percentHalfCommon =Double.parseDouble(valMap.get("xper_training_percent_match_half_common").getValue(0));		
			Float temp = Float.parseFloat(valMap.get("xper_training_use_size_cues").getValue(0));
			
		} catch (NullPointerException npe) {
			System.out.println("xper_training_* was not found in the database! " );
		}
		System.out.println("PercMatches = " + percMatches + ", percentHalfCommon = " + percentHalfCommon );
	}
	
	static public void updateStimObjData(int currentGen){

		boolean writeAppendToTextFile = false; //  true;  //  
		
		long taskId;
		Integer numberOfStages = 0;
		PrintWriter existingDataFile = null;
		PrintWriter newDataFile = null;
		
		// 	JK need a way to update periodically			
		Map <Long, SachStimDataEntry> newDataMap = new HashMap<Long, SachStimDataEntry>();
		
		try {
			existingDataFile = new PrintWriter("/home/justin/scratch/existingDataFile_" + currentGen + ".txt");
			newDataFile = new PrintWriter("/home/justin/scratch/newDataFile_" + currentGen + ".txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
		spikeCounter.setDbUtil(dbUtil);

		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
			
			spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, dataChannel);
			
			System.out.println("spikeEntry has " + spikeEntry.size() + " items ");
//			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
//				System.out.println(entry.getValue().getTaskId());
//			}
			
			// for each trial done in a generation:
			// get blank FRs:
			List<Double> blankFRs = new ArrayList<Double>();
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();			
				
				taskId = ent.getTaskId();
				numberOfStages = ent.getTrialStageData().size();
				
				// only process tasks that have enough stages
				if(numberOfStages >= minNumStages){
					
								
					// get TrialSpec:
					SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
					// if only interested in BEH_quick
	//				if(trialSpec.getTrialType() != "BEH_quick"){
	//					System.out.println("generation " + currentGen + " not BEH_quick : taskId = " + taskId);
	//					return;
	//				}
									
					// for each stimObj in the trial:
					long stimObjId;
					BsplineObjectSpec spec;
					int entIdx;				// MarkEveryStepTaskSpikeEntry gives the following epochs:
											//    [ fixation_pt_on, eye_in_succeed, stim, isi, ... (repeat x numStims), done_last_isi_to_task_end ]
											//    so to index the stimuli we skip the first 2 and do every other for as many stims as we present in a trial
	
					// first get blank stim FR data:
					for (int n = 0; n < trialSpec.getStimObjIdCount(); n++) {
						stimObjId = trialSpec.getStimObjId(n);
						spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(stimObjId).getSpec());
						
						if (spec.isBlankStim()) {
							entIdx = 2*n+2;
							blankFRs.add(ent.getSpikePerSec(entIdx)); 
						}
					}				
				}
			}
	
			
			for (SortedMap.Entry <Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
				taskId = ent.getTaskId();
				numberOfStages = ent.getTrialStageData().size();
				
				// only process tasks that have enough stages
				if(numberOfStages >= minNumStages){
	//				System.out.print(taskId  + "  :  ");					
					// get TrialSpec:
					SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
					
					// for each stimObj in the trial get FR data for all stims and save:
					long stimObjId = 0;
					SachStimDataEntry existingData = new SachStimDataEntry();
					SachStimDataEntry newDataEntry = new SachStimDataEntry();
										
					int entIdx;
					int numStages;
	
					for (int n = 0; n < trialSpec.getStimObjIdCount(); n++) {
						stimObjId = trialSpec.getStimObjId(n);
	//					System.out.print(stimObjId  + ", ");						
						// read the existing stimObjData 
						existingData = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());		
	
						
	/////////////////// 					
						if(existingData.getSampleFrequency() == 0.0 && existingData.getAvgFR() == 0.0 ){
	//						System.out.println("Skipping taskId =  " + taskId);
	//						System.out.println(currentGen + " : " + n + 1 + " : " + taskId + " : " +  stimObjId + " : SampleFreq == " + existingData.getSampleFrequency() + ", avgFR == " + existingData.getAvgFR());
	//						break;
						}
						
						// is it in the map?
						if(newDataMap.containsKey(stimObjId)){
							newDataEntry = newDataMap.get(stimObjId);
	//						System.out.println(n + " getting " + stimObjId);
						} else {
							newDataEntry =  new SachStimDataEntry();
							// copy these values after creation
							newDataEntry.setTrialType(existingData.getTrialType());
							newDataEntry.setStimObjId(existingData.getStimObjId());
	//						System.out.println(n + " adding " + stimObjId);
						}				
	
						numStages =  ent.getTrialStageData().size() ;
						
						// add acq info:					
						entIdx = 2 * n + 2;
						
						if(entIdx < numStages){				
																
							newDataEntry.addTaskDoneId(taskId);
							newDataEntry.setSampleFrequency(ent.getSampleFrequency());
							newDataEntry.addSpikesPerSec(ent.getSpikePerSec(entIdx));
							newDataEntry.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
							newDataEntry.addTrialStageData(ent.getTrialStageData(entIdx));
							
							newDataMap.put(stimObjId, newDataEntry);
						} else{
							newDataMap.put(stimObjId, newDataEntry);
							System.out.println("task " + ent.getTaskId() + " : trialStageData has " + numStages + " items. Incomplete?!");	
						}
						
							
					}
	//				System.out.println("");
					
					
	//				if(writeAppendToTextFile){
	//					existingDataFile.append(existingData.toXml());
	//					newDataFile.append(newDataMap.get(stimObjId).toXml());
	//					existingDataFile.close();
	//					newDataFile.close();
	//				}
					
						//  moved to verification loop ...
						//   resave data:
						//   dbUtil.updateStimObjData(stimObjId, newDataEntry.toXml());
				} else {
					System.out.println("Skipping " + taskId );
				}
			}
			
		} catch(InvalidAcqDataException ee) {
			ee.printStackTrace();
		} catch(NoMoreAcqDataException ee) {
			ee.printStackTrace();
		}
	
		

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// do it again, to verify, and maybe, write to database
				
		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
			
			spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, dataChannel);
//			System.out.println("spikeEntry has " + spikeEntry.size() + " items ");
//			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
//				System.out.println(entry.getValue().getTaskId());
//			}
		int count1 = 1;
		for (SortedMap.Entry <Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
			MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();				
			taskId = ent.getTaskId();
			numberOfStages = ent.getTrialStageData().size();
			// only process tasks that have enough stages
			if(numberOfStages >= minNumStages){				
				
	//			System.out.println("Entering spike info for taskId: " + taskId);
				System.out.println(count1 + " : taskId : " + taskId  + "  ....  ");		
				count1++;
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial get FR data for all stims and save:
				long stimObjId = 0;
				SachStimDataEntry existingData = new SachStimDataEntry();
				SachStimDataEntry newDataEntry = new SachStimDataEntry();
				StimSpecEntry     existingSpec = new StimSpecEntry();
				
				// for each object, compare avgFR, taskDoneIds
				for (int n = 0; n < trialSpec.getStimObjIdCount(); n++) {
					stimObjId = trialSpec.getStimObjId(n);
					System.out.print("\n\t" + stimObjId  + " : ");						
					// read the existing stimObjData 
					existingData = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());					
					newDataEntry = newDataMap.get(stimObjId);
					existingSpec = dbUtil.readSingleStimSpec(stimObjId);
					
					System.out.print("\n\t old : " + existingData.getAvgFR() + " : " + existingData.getTaskDoneIds());
					System.out.print("\n\t new : " + newDataEntry.getAvgFR() + " : " + newDataEntry.getTaskDoneIds()); 
					
					//   resave data:
					//   dbUtil.updateStimObjData(stimObjId, newDataEntry.toXml());
					if(writeAppendToTextFile){
						//System.out.println("(*)");
						existingDataFile.append(existingSpec.getSpec() + "\n" + existingData.toXml() + "\n");
						newDataFile.append(existingSpec.getSpec() + "\n" + newDataMap.get(stimObjId).toXml() + "\n");
					}
					
				}
				System.out.println("");
			}
		}
				
	} catch(InvalidAcqDataException ee) {
		ee.printStackTrace();
	} catch(NoMoreAcqDataException ee) {
		ee.printStackTrace();
	}

		existingDataFile.close();
		newDataFile.close();
		
	}

//	static public List<Long> showStim(){
//		return showStim(false,1414690537305343L);
//	}
	static public List<Long> recoverStimId(int genId, int index){
		int[] indices = new int[1];
		indices[0] = index;
		return recoverStimId(genId,indices);
	}
	static public List<Long> recoverStimId(int genId, int[] indices){
		List<Long> stimObjIdsAll = new ArrayList<Long>();
		List<Long> stimObjIds = new ArrayList<Long>();
		stimObjIdsAll = dbUtil.readStimObjIdsFromGenId(genId);
		for(int ii=0;ii<indices.length;ii++){
			stimObjIds.add(stimObjIdsAll.get(indices[ii]));
		}
		return stimObjIds;
	}
//	static public ng> showStim(boolean saveFlag,long stimObjIds){
//		List<Long> stimObjIdsList = new ArrayList<Long>();
//		stimObjIdsList.add(stimObjIds);
//		return showStim(saveFlag,stimObjIdsList);
//	}
	static public List<Long> showStim(boolean saveFlag,int genId, int index){
		List<Long> stimObjIds = recoverStimId(genId,index);
		showStim(saveFlag,stimObjIds);
		return stimObjIds;
	}
	static public List<Long> showStim(boolean saveFlag,int genId, int[] indices){
		List<Long> stimObjIds = recoverStimId(genId,indices);
		showStim(saveFlag,stimObjIds);
		return stimObjIds;
	}
	static public void showStim(boolean saveFlag,List<Long> stimObjIds){
		
		List<BsplineObjectSpec> specs = new ArrayList<BsplineObjectSpec>();
		specs =  dbUtil.readListStimSpecs(stimObjIds);
		showStim(specs,saveFlag);
//		cell 28, stim 105 (globalOri -25)
//		stimObjIds.add(1414690537305343L);

//		cell 28, stim 1 (no rotation)
//		stimObjIds.add(1414690174619172L);
		
	//	stimObjIds.add(1462910263489055L);
	//	stimObjIds.add(1462910263500730L);
	//	stimObjIds.add(1462910263510480L);
	//	stimObjIds.add(1462910263520145L);
	//	stimObjIds.add(1462910263530299L);
		
//		stimObjIds = dbUtil.readStimObjIdsFromGenId(genId);
	}
	
	static public void showStim(List<BsplineObjectSpec> specs,boolean saveFlag){
		StimTestWindow testWindow = initTestWindow(700,600,true,1.0);
		testWindow.create();
		showStim(specs,saveFlag,testWindow);
		testWindow.close();
	}
	static public void showStim(List<BsplineObjectSpec> specs,boolean saveFlag,StimTestWindow testWindow){
		testWindow.clearStimObjs();
		
		if(specs.isEmpty()){
			System.out.println("nothing found ...");
		}
		System.out.println(specs.size() + " objects");
		
						
				
		if (saveFlag){
			// do this early to initialize the pngMaker
			String pngPath = System.getProperty("user.dir") + "/images/manualTemp/";
			File folder = new File(pngPath);
			File[] files = folder.listFiles();
			for (int ff = 0;ff<files.length;ff++){
				files[ff].delete();
			}
			
//			final File[] files = pngPath.listFiles();
//			files.delete();
			String pngFileName = pngPath + "temp";
			System.out.print(pngPath);
			System.out.print('\n');
			System.out.print(pngFileName);
			testWindow.setSavePng_pngMaker(true);
			testWindow.setPngPath(pngPath);
			testWindow.setPngFilename(pngFileName);
		}
		
		for(BsplineObjectSpec s : specs){
			BsplineObject bso = new BsplineObject();
			bso.setSpec(s.toXml());
			testWindow.setStimObjs(bso);			
		}
		
		testWindow.drawOnly();
	}


	static public StimTestWindow initTestWindow(int windowWidth,int windowHeight,boolean gridOn,double windowDurationSecs){
		double magLevel = 6.0;  // used inside the TestWindow ...
		StimTestWindow testWindow = new StimTestWindow(windowHeight, windowWidth , magLevel);
		testWindow.setGridOn(gridOn);
		testWindow.setSpeedInSecs(windowDurationSecs);
		
		return testWindow;
	}
	

	public void Test1(){
		MarkEveryStepExperimentSpikeCounter spikeCounter = new MarkEveryStepExperimentSpikeCounter();
		
//		JavaConfigApplicationContext context = new JavaConfigApplicationContext(FileUtil.loadConfigClass("experiment.beh.config_class"));
//
//		SachRandomGeneration gen = context.getBean(SachRandomGeneration.class);
//		gen.generateBeh();
		long genId = 454;
		int dataChan = 0;
		
		
		ComboPooledDataSource source = new ComboPooledDataSource();
		try {
			source.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			throw new DbException(e);
		}
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/shaggy_ecpc48_2016_07");
//		source.setJdbcUrl("jdbc:mysql://172.30.4.9/jk_test");
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/sach_ecpc48_2014_08_12_recording");
		source.setJdbcUrl("jdbc:mysql://172.30.4.9/sach_ecpc48_2014_08_12_recording_20161122");
		source.setUser("xper_rw");
		source.setPassword("up2nite");
		dbUtil.setDataSource(source);
		
		// dbUtil.getRandomSeed(genId);
		
		spikeCounter.setDbUtil(dbUtil);
//		
		SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikes =	spikeCounter.getTaskSpikeByGeneration(genId, dataChan);
		Set<Long> keys = spikes.keySet();
		List<Double> sps;
		List<TrialStageData> tsd;
		MarkEveryStepTaskSpikeDataEntry entry;
		int [] spikeData;
		
		System.out.println("generation " + genId + " has " + spikes.size() + " entries"	);
		//
		for(Long n : keys){
			entry = spikes.get(n);
			tsd = entry.getTrialStageData();
			
			for(int m = 0; m < tsd.size(); m++){
				spikeData = tsd.get(m).getSpikeData();
				
				
				System.out.format("\ntaskId = %10d : has %4d stages ", n, tsd.size()); //spikeData.length  );
			}
			
			sps = spikes.get(n).getSpikePerSec();
			
		}
////			spikeCounter.getTaskSpikeByIdRange(startTime, endTime, dataChan,0,0);
		
		System.out.format("\n running \n");
if(false){					
		////   JK  22 Nov   
		// copied from GenIDFixer public void getSpikeResponses(long currentGen) 
		// obtain spike data:
		long taskId;

		// use mine because it adds fake spike stuff!
		//MarkStimExperimentSpikeCounter spikeCounter = new MarkStimExperimentSpikeCounter();
//		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
//		spikeCounter.setDbUtil(dbUtil);
		
		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
//			spikeEntry = spikeCounter.getFakeTaskSpikeByGeneration(genId);
			spikeEntry = spikeCounter.getTaskSpikeByGeneration(genId, dataChan);
			if(spikeEntry == null){
				return;
			}
//			spikeEntry = spikeCounter.getTaskSpikeByGeneration(genId, dataChan);
			System.out.format("found %d spikeEntries ", spikeEntry.size());
			// for each trial done in a generation:
				// get blank FRs:
			List<Double> blankFRs = new ArrayList<Double>();
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry2 : spikeEntry.entrySet())
			{
				MarkEveryStepTaskSpikeDataEntry ent = entry2.getValue();				
				taskId = ent.getTaskId();
				
				// get TrialSpec:
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial:
				long stimObjId;
				BsplineObjectSpec spec;
				int entIdx;				// MarkEveryStepTaskSpikeEntry gives the following epochs:
										//    [ fixation_pt_on, eye_in_succeed, stim, isi, ... (repeat x numStims), done_last_isi_to_task_end ]
										//    so to index the stimuli we skip the first 2 and do every other for as many stims as we present in a trial

				// first get blank stim FR data:
				for (int n=0;n<trialSpec.getStimObjIdCount();n++) {
					stimObjId = trialSpec.getStimObjId(n);
					spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(stimObjId).getSpec());
					
					if (spec.isBlankStim()) {
						entIdx = 2*n+2;
						blankFRs.add(ent.getSpikePerSec(entIdx)); 
					}
				}
			}
			
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry2 : spikeEntry.entrySet())
			{
				MarkEveryStepTaskSpikeDataEntry ent = entry2.getValue();				
				taskId = ent.getTaskId();

				System.out.println("Entering spike info for trial: " + taskId);
				List<TrialStageData> trialStageData = ent.getTrialStageData();
				List<Double> spikePerSec = ent.getSpikePerSec();
				System.out.println("this entry has " + trialStageData.size() + " stages, and " + spikePerSec.size() + " spikesPerSec entries"	);
				
				// get TrialSpec:
				
				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
				
				// for each stimObj in the trial get FR data for all stims and save:
				long stimObjId;
				SachStimDataEntry data;
				int entIdx;

				for (int n = 0; n < trialSpec.getStimObjIdCount(); n++) {
					stimObjId = trialSpec.getStimObjId(n);
									
					data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
					if(n >= 2){
						System.out.println("lookout ...");
					}
					// add acq info:					
					entIdx = n; // JK        2 * n + 2;
					data.addTaskDoneId(taskId);
					data.setSampleFrequency(ent.getSampleFrequency());
					data.addSpikesPerSec(ent.getSpikePerSec(entIdx));
					data.setBkgdSpikesPerSec(blankFRs);					// add blank FR data
					data.addTrialStageData(ent.getTrialStageData(entIdx));
	System.out.println(stimObjId + " : " + ent.getSpikePerSec(entIdx));
	System.out.println(data.toXml());				
					// resave data:
					if (data.getParentId() != -1) {
						System.out.println(stimObjId);
						System.out.println(data.toXml());
//		CAREFUL!!				dbUtil.updateStimObjData(stimObjId, data.toXml());
					}
				}
			}	
		} catch(InvalidAcqDataException ee) {
			ee.printStackTrace();
		} catch(NoMoreAcqDataException ee) {
			ee.printStackTrace();
		}
		
		
		
		
}
		
		
		
		
		
//		TimeUtil timeUtil = new DefaultTimeUtil();
//		SachExptSpecGenerator generator = new SachExptSpecGenerator();
//
//		JavaConfigApplicationContext context = new JavaConfigApplicationContext(
//				FileUtil.loadConfigClass("acq.config_class", AcqConfig.class));
//				
//		SachDbUtil dbUtil = new SachDbUtil();
//		ComboPooledDataSource source = new ComboPooledDataSource();
//		try {
//			source.setDriverClass("com.mysql.jdbc.Driver");
//		} catch (PropertyVetoException e) {
//			throw new DbException(e);
//		}
//		source.setJdbcUrl("jdbc:mysql://172.30.4.9/jk_test");
//		source.setUser("xper_rw");
//		source.setPassword("up2nite");
//		dbUtil.setDataSource(source);
//		
//		System.out.println("timeutil  " + timeUtil.currentTimeMicros());
//		
//		srg.setDbUtil(dbUtil);
//		srg.setGlobalTimeUtil(timeUtil);
//		
//		generator.setGlobalTimeUtil(timeUtil);
//		generator.setDbUtil(dbUtil);
//				
//		
//		srg.setGenerator(generator);
//		
//		srg.generateBeh();
		

	}
	
	
	public static void showTrialStageHistogram(int currentGen, int dataChannel){
		SachMarkEveryStepExptSpikeCounter spikeCounter = new SachMarkEveryStepExptSpikeCounter(); 
		spikeCounter.setDbUtil(dbUtil);

		long taskId;
		int numberOfStages;
		
		// data structure to store taskId and size of trialStageData
		Map <Long, Integer> trialStageCounts = new HashMap<Long, Integer>();
		// data structure to store trialStageData histogram 
		Map <Integer, Integer> trialStageHistogram = new HashMap<Integer, Integer>();
		
		try{
			// get spike data for all trials:
			SortedMap<Long, MarkEveryStepTaskSpikeDataEntry> spikeEntry;
			
			spikeEntry = spikeCounter.getTaskSpikeByGeneration(currentGen, dataChannel);
			System.out.println("spikeEntry has " + spikeEntry.size() + " items ");
//			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
//				System.out.println(entry.getValue().getTaskId());
//			}
			
			// for each trial done in a generation:
			// get blank FRs:
//			List<Double> blankFRs = new ArrayList<Double>();
			for (SortedMap.Entry<Long, MarkEveryStepTaskSpikeDataEntry> entry : spikeEntry.entrySet()){
				MarkEveryStepTaskSpikeDataEntry ent = entry.getValue();			
				
				taskId = ent.getTaskId();
				numberOfStages = ent.getTrialStageData().size();
				trialStageCounts.put(taskId, numberOfStages);
				
				// count trialStages for each taskId
				if(trialStageHistogram.containsKey(numberOfStages)){
//				//	
					trialStageHistogram.put(numberOfStages,  trialStageHistogram.get(numberOfStages) + 1);
//					
//				//	
				} else {
					trialStageHistogram.put(numberOfStages, 1);
				}
				
				
					
				// get TrialSpec:
//				SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
			
				// only interested in BEH_quick
//				if(trialSpec.getTrialType() != "BEH_quick"){
//					System.out.println("generation " + currentGen + " not BEH_quick : taskId = " + taskId);
//					return;
//				}
								

			}
//			
//			System.out.println("trial id           # trialStages ");
//			// show trialStage for each task
//			for(Long id : trialStageCounts.keySet()){
//				System.out.println(id + " : " + trialStageCounts.get(id));
//			}
//			System.out.println("");
			
			System.out.println("# trialStages      # task counts");
			// show trialStage counts
			for(Integer num : trialStageHistogram.keySet()){
				System.out.println(num + " : " + trialStageHistogram.get(num));
			}
			System.out.println("");
		} catch(InvalidAcqDataException ee) {
			ee.printStackTrace();
		}
//		} catch(NoMoreAcqDataException ee) {
//			ee.printStackTrace();
//		}
	
	}
}
