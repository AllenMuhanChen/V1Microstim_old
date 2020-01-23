package org.xper.app.experiment.test;
//original imports
import org.xper.Dependency;  
import org.xper.example.classic.EStimSpecGenerator;
import org.xper.exception.VariableNotFoundException;
import org.xper.experiment.StimSpecGenerator;
import org.xper.time.TimeUtil;
//AC imports
import org.xper.util.DbUtil;
import java.util.Arrays;
import org.xper.allen.AllenDbUtil;
import org.xper.allen.BlockSpec;
import org.xper.allen.EStimSpec;

public class RandomGeneration {
	@Dependency
	DbUtil dbUtil;

	@Dependency
	TimeUtil globalTimeUtil;
	@Dependency
	StimSpecGenerator generator;

	@Dependency
	int taskCount;

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
		//AC: Block Logic
		long blockId = 1;
		BlockSpec block = ((AllenDbUtil) dbUtil).readBlockSpec(blockId);
		//blockRef: c-catch trial, v-vstim only, e-estim only, b-both
		char blockref[] = new char[taskCount];
		Arrays.fill(blockref, 0, block.get_num_catches()-1, 'c');
		Arrays.fill(blockref, block.get_num_catches(), block.get_num_catches()+block.get_num_stims_only()-1, 'v');
		Arrays.fill(blockref, block.get_num_catches()+block.get_num_stims_only(), block.get_num_catches()+block.get_num_stims_only()+block.get_num_estims_only()-1, 'e');		
		Arrays.fill(blockref, block.get_num_catches()+block.get_num_stims_only()+block.get_num_estims_only(), block.get_num_catches()+block.get_num_stims_only()+block.get_num_estims_only()+block.get_num_both()-1, 'b');	
	
		if (block.get_shuffle() == "yes") {
		//Shuffle Code Here	
		}
		//
		for (int i = 0; i < taskCount; i++) {
			if (i % 10 == 0) {
				System.out.print(".");
			}
			String spec = generator.generateStimSpec();
			//AC
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
																			//added estimId
			dbUtil.writeStimSpec(taskId, spec);								
			dbUtil.writeTaskToDo(taskId, estimId, -1, genId);		//Added estimId to db
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
}
