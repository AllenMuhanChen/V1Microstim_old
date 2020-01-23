package org.xper.sach.vo;

import org.dom4j.Document;
import org.xper.Dependency;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.eye.EyeTargetSelector;

/**
 * Target position and size describe the response window.
 * 
 * @author john
 *
 */
public class SachExperimentState extends SlideTrialExperimentState {
	@Dependency
	EyeTargetSelector targetSelector;
	
	/**
	 * in ms
	 */
	@Dependency
	long timeAllowedForInitialTargetSelection;
	@Dependency
	long requiredTargetSelectionHoldTime;
	@Dependency
	long targetSelectionStartDelay;
	@Dependency
	long timeoutPenaltyDelay = 0;	// -shs, default to zero
	@Dependency
	boolean repeatTrialIfEyeBreak = false;
	
	// JK 25 July
	@Dependency
	long maintainHoldTime;
	
	int correctTrialCount = 1;
	int MaxTrialCount = 3;

	Document currentSpecDoc;
	
	public SachExperimentState () {
	}

	public int getCorrectTrialCount() {
		return correctTrialCount;
	}
	
	// JK 21 April 2016
	// dynamic reward needs to keep track of trial performance
	// this  should / could be a lot smarter ...
	public void countCorrectTrial(boolean isCorrect){
		if(isCorrect){
			if(correctTrialCount < MaxTrialCount){
				correctTrialCount += 1;
			}
		}
		else {
			correctTrialCount = 1;
		}
		
		//
//		System.out.println("SachExperimentState:countCorrectTrial() : correctTrialCount == " + correctTrialCount);
		
	}
	
	public Document getCurrentSpecDoc() {
		return currentSpecDoc;
	}

	public void setCurrentSpecDoc(Document currentSpecDoc) {
		this.currentSpecDoc = currentSpecDoc;
	}

	public EyeTargetSelector getTargetSelector() {
		return targetSelector;
	}

	public void setTargetSelector(EyeTargetSelector targetSelector) {
		this.targetSelector = targetSelector;
	}

	public long getTimeAllowedForInitialTargetSelection() {
		return timeAllowedForInitialTargetSelection;
	}

	public void setTimeAllowedForInitialTargetSelection(
			long timeAllowedForInitialTargetSelection) {
		this.timeAllowedForInitialTargetSelection = timeAllowedForInitialTargetSelection;
	}

	public long getRequiredTargetSelectionHoldTime() {
		return requiredTargetSelectionHoldTime;
	}

	public void setRequiredTargetSelectionHoldTime(
			long requiredTargetSelectionHoldTime) {
		this.requiredTargetSelectionHoldTime = requiredTargetSelectionHoldTime;
	}

	public long getTargetSelectionStartDelay() {
		return targetSelectionStartDelay;
	}

	public void setTargetSelectionStartDelay(long targetSelectionStartDelay) {
		this.targetSelectionStartDelay = targetSelectionStartDelay;
	}

	public long getTimeoutPenaltyDelay() { // -shs
		return timeoutPenaltyDelay;
	}
	
	public void setTimeoutPenaltyDelay(Integer timeoutPenaltyDelay) {	// -shs
		this.timeoutPenaltyDelay = timeoutPenaltyDelay;
	}
	
	public boolean isRepeatTrialIfEyeBreak() {
		return repeatTrialIfEyeBreak;
	}

	public void setRepeatTrialIfEyeBreak(boolean repeatTrialIfEyeBreak) {
		this.repeatTrialIfEyeBreak = repeatTrialIfEyeBreak;
	}
	
	// JK 25 July
	public long getMaintainHoldTime() {
		return maintainHoldTime;
	}

	public void setMaintainHoldTime(long maintainHoldTime) {
		this.maintainHoldTime = maintainHoldTime;
	}

	
	
}
