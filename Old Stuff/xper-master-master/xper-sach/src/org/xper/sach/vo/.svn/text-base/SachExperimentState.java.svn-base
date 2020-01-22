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

	Document currentSpecDoc;
	
	public SachExperimentState () {
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
}
