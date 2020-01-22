package org.xper.choice.vo;

import org.xper.classic.vo.TrialContext;
import org.xper.drawing.Coordinates2D;

public class ChoiceTrialContext extends TrialContext {
	public static int NUM_TARGETS = 2;
	
	long targetOnTime;
	long targetInitialSelectionTime;
	long targetSelectionSuccessTime;
	
	int targetSelected;
	Coordinates2D targetPos [] = new Coordinates2D[NUM_TARGETS];
	double[] targetSize = new double[NUM_TARGETS];
	double[] targetEyeWindowSize = new double[NUM_TARGETS];
	long reward[] = new long[NUM_TARGETS];
	
	public long getTargetOnTime() {
		return targetOnTime;
	}
	public void setTargetOnTime(long targetOnTime) {
		this.targetOnTime = targetOnTime;
	}
	public long getTargetInitialSelectionTime() {
		return targetInitialSelectionTime;
	}
	public void setTargetInitialSelectionTime(long targetInitialSelectionTime) {
		this.targetInitialSelectionTime = targetInitialSelectionTime;
	}
	public int getTargetSelected() {
		return targetSelected;
	}
	public void setTargetSelected(int targetSelected) {
		this.targetSelected = targetSelected;
	}
	public Coordinates2D[] getTargetPos() {
		return targetPos;
	}
	public void setTargetPos(Coordinates2D[] targetPos) {
		this.targetPos = targetPos;
	}
	public double[] getTargetSize() {
		return targetSize;
	}
	public void setTargetSize(double[] targetSize) {
		this.targetSize = targetSize;
	}
	public long[] getReward() {
		return reward;
	}
	public void setReward(long[] reward) {
		this.reward = reward;
	}
	public long getTargetSelectionSuccessTime() {
		return targetSelectionSuccessTime;
	}
	public void setTargetSelectionSuccessTime(long targetSelectionSuccessTime) {
		this.targetSelectionSuccessTime = targetSelectionSuccessTime;
	}
	public double[] getTargetEyeWindowSize() {
		return targetEyeWindowSize;
	}
	public void setTargetEyeWindowSize(double[] targetEyeWindowSize) {
		this.targetEyeWindowSize = targetEyeWindowSize;
	}
}
