package org.xper.match.vo;

import org.dom4j.Document;
import org.xper.Dependency;
import org.xper.classic.vo.SlideTrialExperimentState;
import org.xper.eye.EyeTargetSelector;

public class MatchTrialExperimentState extends SlideTrialExperimentState {
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
	double targetSize;
	@Dependency
	double targetEyeWindowSize;
	@Dependency
	double targetDistanceFromOrigin;

	Document currentSpecDoc;
	
	public MatchTrialExperimentState () {
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

	public double getTargetSize() {
		return targetSize;
	}

	public void setTargetSize(double targetSize) {
		this.targetSize = targetSize;
	}

	public double getTargetEyeWindowSize() {
		return targetEyeWindowSize;
	}

	public void setTargetEyeWindowSize(double targetEyeWindowSize) {
		this.targetEyeWindowSize = targetEyeWindowSize;
	}

	public double getTargetDistanceFromOrigin() {
		return targetDistanceFromOrigin;
	}

	public void setTargetDistanceFromOrigin(double targetDistanceFromOrigin) {
		this.targetDistanceFromOrigin = targetDistanceFromOrigin;
	}

	public long getTargetSelectionStartDelay() {
		return targetSelectionStartDelay;
	}

	public void setTargetSelectionStartDelay(long targetSelectionStartDelay) {
		this.targetSelectionStartDelay = targetSelectionStartDelay;
	}

	
	
}
