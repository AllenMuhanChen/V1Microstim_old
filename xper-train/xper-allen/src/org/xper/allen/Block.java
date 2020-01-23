package org.xper.allen;

import java.util.Arrays;

import org.xper.app.experiment.test.RandomGenerationAllen;

public class Block extends RandomGenerationAllen{
	char trialList[]; 
	
	public int getTaskCount(BlockSpec block){
		return block.get_num_catches()+block.get_num_estims_only()+block.get_num_stims_only()+block.get_num_both();
	}
	public char[] generateTrialList(BlockSpec block) {
		//trialList: c-catch trial, v-vstim only, e-estim only, b-both
		trialList = new char[getTaskCount(block)]; //Currently gets taskCount from blockSpec. 
		Arrays.fill(trialList, 0, block.get_num_catches()-1, 'c');
		Arrays.fill(trialList, block.get_num_catches(), block.get_num_catches()+block.get_num_stims_only()-1, 'v');
		Arrays.fill(trialList, block.get_num_catches()+block.get_num_stims_only(), block.get_num_catches()+block.get_num_stims_only()+block.get_num_estims_only()-1, 'e');		
		Arrays.fill(trialList, block.get_num_catches()+block.get_num_stims_only()+block.get_num_estims_only(), block.get_num_catches()+block.get_num_stims_only()+block.get_num_estims_only()+block.get_num_both()-1, 'b');	
	
		System.out.println(block.get_num_catches()-1);
		if (block.get_shuffle() == "yes") {
			//Shuffle Code Here	
			}
		return trialList;
	}
	public char[] get_trialList( ) {
		return trialList;
	}

	
	
	
}
