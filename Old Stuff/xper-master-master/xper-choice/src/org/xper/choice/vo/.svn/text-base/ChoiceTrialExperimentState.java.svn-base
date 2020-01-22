package org.xper.choice.vo;

import org.dom4j.Document;
import org.xper.Dependency;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.eye.EyeTargetSelector;

public class ChoiceTrialExperimentState extends SlideTrialExperimentState {
	@Dependency
	EyeTargetSelector targetSelector;
	
	/**
	 * in ms
	 */
	@Dependency
	long timeAllowedForInitialTargetSelection;
	@Dependency
	long requiredTargetSelectionHoldTime;
	
	/**
	 * in degree
	 */ 
	@Dependency
	double choiceTargetSize;
	@Dependency
	double choiceTargetDistanceFromOrigin;
	@Dependency
	double choiceTargetEyeWindowSize;
	
	Document currentSpecDoc;
	public static int SLIDE_PER_TRIAL = 2;
	
	public ChoiceTrialExperimentState () {
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

	public double getChoiceTargetSize() {
		return choiceTargetSize;
	}

	public void setChoiceTargetSize(double choiceTargetSize) {
		this.choiceTargetSize = choiceTargetSize;
	}

	public double getChoiceTargetDistanceFromOrigin() {
		return choiceTargetDistanceFromOrigin;
	}

	public void setChoiceTargetDistanceFromOrigin(
			double choiceTargetDistanceFromOrigin) {
		this.choiceTargetDistanceFromOrigin = choiceTargetDistanceFromOrigin;
	}

	public double getChoiceTargetEyeWindowSize() {
		return choiceTargetEyeWindowSize;
	}

	public void setChoiceTargetEyeWindowSize(double choiceTargetEyeWindowSize) {
		this.choiceTargetEyeWindowSize = choiceTargetEyeWindowSize;
	}

	
}
