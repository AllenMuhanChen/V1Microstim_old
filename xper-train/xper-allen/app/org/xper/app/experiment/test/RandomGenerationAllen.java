package org.xper.app.experiment.test;
//original imports
import org.xper.Dependency;    
import org.xper.exception.VariableNotFoundException;
import org.xper.experiment.StimSpecGenerator;
import org.xper.time.TimeUtil;
//AC imports
import org.xper.util.DbUtil;
import java.util.Arrays;
import org.xper.allen.*;

public class RandomGenerationAllen {
	@Dependency
	DbUtil dbUtil;

	@Dependency
	TimeUtil globalTimeUtil;
	@Dependency
	StimSpecGenerator generator;

	@Dependency
	protected int taskCount;

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	
	public void generate() {
		System.out.print("Generating ");
		long genId = 1;
		try {
			genId = dbUtil.readReadyGenerationInfo().getGenId() + 1;
		} catch (VariableNotFoundException e) {
			dbUtil.writeReadyGenerationInfo(genId, 0);
		}
		/*
		//AC: Block Logic
		long blockId = 1;
		BlockSpec blockspec = ((AllenDbUtil) dbUtil).readBlockSpec(blockId);
		Block block = new Block();
		block.generateTrialList(taskCount); 
		*/
		
		//
		for (int i = 0; i < taskCount; i++) {
			if (i % 10 == 0) {
				System.out.print(".");
			}
			String spec = generator.generateStimSpec();
			//AC
			/*
			long stimId;
			int estimId =1;//STILL NEED TO ADD LOGIC FOR STIM PARAMETERS
			long taskId = globalTimeUtil.currentTimeMicros();
			if (blockref[i]=='c'){
				stimId=0;
				estimId=0;
			}else if(blockref[i]=='v') {
				stimId=taskId;
				estimId=0;
			}else if(blockref[i]=='e') {
				stimId=0;
				estimId=1;
			}else {
				stimId=taskId;
				estimId=1;
			}
				*/															//added estimId
			long taskId = globalTimeUtil.currentTimeMicros();
			long stimId = taskId; 
			dbUtil.writeStimSpec(taskId, spec);								
			dbUtil.writeTaskToDo(taskId, stimId, -1, genId);		//Added estimId to db
			EStimSpec e = EStimSpecGenerator.generate();					//Generate EStimSpec
			((AllenDbUtil) dbUtil).writeEStimSpec(e);										//Write EStimspec class to db
			//
		}
		dbUtil.updateReadyGenerationInfo(genId, taskCount);
		System.out.println("done.");
	}

	public DbUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(DbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	
	public TimeUtil getGlobalTimeUtil() {
		return globalTimeUtil;
	}

	public void setGlobalTimeUtil(TimeUtil globalTimeUtil) {
		this.globalTimeUtil = globalTimeUtil;
	}

	public StimSpecGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(StimSpecGenerator generator) {
		this.generator = generator;
	}
	public void testBlockClass(BlockSpec blockspec) {
		Block block = new Block();
		System.out.print(block.generateTrialList(blockspec));
	}
}
