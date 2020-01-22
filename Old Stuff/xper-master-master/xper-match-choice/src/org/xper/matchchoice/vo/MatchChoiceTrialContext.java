package org.xper.matchchoice.vo;

import org.xper.classic.vo.TrialContext;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;

public class MatchChoiceTrialContext extends TrialContext {
	public static int NUM_TARGETS = 2;
	
	long targetOnTime;
	long targetInitialSelectionTime;
	long targetSelectionSuccessTime;
	
	int targetSelected;
	RGBColor targetColor[] = new RGBColor[NUM_TARGETS];
	Coordinates2D targetPos [] = new Coordinates2D[NUM_TARGETS];
	double[] targetSize = new double[NUM_TARGETS];
	double[] targetEyeWindowSize = new double[NUM_TARGETS];
	int reward;
	
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
	public int getReward() {
		return reward;
	}
	public void setReward(int reward) {
		this.reward = reward;
	}
	public RGBColor[] getTargetColor() {
		return targetColor;
	}
	public void setTargetColor(RGBColor[] targetColor) {
		this.targetColor = targetColor;
	}
}
