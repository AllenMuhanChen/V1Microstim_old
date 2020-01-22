package org.xper.matchchoice;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.xper.classic.TrialExperimentMessageHandler;
import org.xper.db.vo.BehMsgEntry;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.matchchoice.vo.MatchChoiceSelectionMessage;
import org.xper.matchchoice.vo.MatchChoiceTargetMessage;

public class MatchChoiceExperimentMessageHandler extends
		TrialExperimentMessageHandler {
	
	AtomicBoolean inChoice = new AtomicBoolean(false);
	AtomicBoolean initialSelection = new AtomicBoolean(false);
	AtomicInteger selection = new AtomicInteger(0);
	AtomicReference<Coordinates2D[]> targetPosition = new AtomicReference<Coordinates2D[]>();
	AtomicReference<double[]> targetSize = new AtomicReference<double[]> ();
	AtomicReference<double[]> targetEyeWindowSize = new AtomicReference<double[]> ();
	AtomicReference<RGBColor[]> targetColor = new AtomicReference<RGBColor[]>();
	
	public boolean handleMessage(BehMsgEntry msg) {
		if(super.handleMessage(msg)) {
			if ("EyeInBreak".equals(msg.getType())) {
				inChoice.set(false);
				initialSelection.set(false);
			}
			return true;
		}
		if ("TargetOn".equals(msg.getType())) {
			inChoice.set(true);
			initialSelection.set(false);
			
			MatchChoiceTargetMessage m = MatchChoiceTargetMessage.fromXml(msg.getMsg());
			targetColor.set(m.getTargetColor());
			targetPosition.set(m.getTargetPos());
			targetSize.set(m.getTargetSize());
			targetEyeWindowSize.set(m.getTargetEyeWindowSize());
			return true;
		} else if ("TargetSelectionSuccess".equals(msg.getType())) {
			inChoice.set(false);
			initialSelection.set(false);
			return true;
		} else if ("TargetInitialSelection".equals(msg.getType())) {
			initialSelection.set(true);
			MatchChoiceSelectionMessage m = MatchChoiceSelectionMessage.fromXml(msg.getMsg());
			selection.set(m.getSelection());
			return true;
		} else {
			return false;
		}
	}

	public boolean isInChoice() {
		return inChoice.get();
	}

	public boolean isInitialSelection() {
		return initialSelection.get();
	}

	public int getSelection() {
		return selection.get();
	}

	public Coordinates2D[] getTargetPosition() {
		return targetPosition.get();
	}

	public double[] getTargetSize() {
		return targetSize.get();
	}

	public double[] getTargetEyeWindowSize() {
		return targetEyeWindowSize.get();
	}

	public RGBColor[] getTargetColor() {
		return targetColor.get();
	}
}
