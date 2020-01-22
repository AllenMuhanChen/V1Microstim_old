package org.xper.choice;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.xper.choice.vo.ChoiceSelectionMessage;
import org.xper.choice.vo.ChoiceTargetMessage;
import org.xper.classic.TrialExperimentMessageHandler;
import org.xper.db.vo.BehMsgEntry;
import org.xper.drawing.Coordinates2D;

public class ChoiceExperimentMessageHandler extends
		TrialExperimentMessageHandler {
	
	AtomicBoolean inChoice = new AtomicBoolean(false);
	AtomicBoolean initialSelection = new AtomicBoolean(false);
	AtomicInteger selection = new AtomicInteger(0);
	AtomicReference<Coordinates2D[]> targetPosition = new AtomicReference<Coordinates2D[]>();
	AtomicReference<double[]> targetSize = new AtomicReference<double[]> ();
	AtomicReference<double[]> targetEyeWindowSize = new AtomicReference<double[]> ();
	
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
			
			ChoiceTargetMessage m = ChoiceTargetMessage.fromXml(msg.getMsg());
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
			ChoiceSelectionMessage m = ChoiceSelectionMessage.fromXml(msg.getMsg());
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
}
