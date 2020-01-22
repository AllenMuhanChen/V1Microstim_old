package org.xper.matchchoice.vo;

import org.dom4j.Document;
import org.xper.Dependency;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.eye.EyeTargetSelector;

public class MatchChoiceTrialExperimentState extends SlideTrialExperimentState {
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
	
	/**
	 * in degree
	 */ 
	@Dependency
	double choiceTargetEyeWindowSize;
	
	Document currentSpecDoc;
	public static int SLIDE_PER_TRIAL = 2;
	
	public MatchChoiceTrialExperimentState () {
		slidePerTrial = SLIDE_PER_TRIAL;
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

	public double getChoiceTargetEyeWindowSize() {
		return choiceTargetEyeWindowSize;
	}

	public void setChoiceTargetEyeWindowSize(double choiceTargetEyeWindowSize) {
		this.choiceTargetEyeWindowSize = choiceTargetEyeWindowSize;
	}

	public long getTargetSelectionStartDelay() {
		return targetSelectionStartDelay;
	}

	public void setTargetSelectionStartDelay(long targetSelectionStartDelay) {
		this.targetSelectionStartDelay = targetSelectionStartDelay;
	}

	
}
