package org.xper.match;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.xper.classic.TrialExperimentMessageHandler;
import org.xper.db.vo.BehMsgEntry;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.match.vo.MatchTargetMessage;

public class MatchExperimentMessageHandler extends
		TrialExperimentMessageHandler {
	
	AtomicBoolean targetOn = new AtomicBoolean(false);
	AtomicBoolean initialSelection = new AtomicBoolean(false);
	AtomicReference<Coordinates2D> targetPosition = new AtomicReference<Coordinates2D>();
	AtomicReference<RGBColor> targetColor = new AtomicReference<RGBColor>();
	AtomicReference<Double> targetSize = new AtomicReference<Double> ();
	AtomicReference<Double> targetEyeWindowSize = new AtomicReference<Double> ();
	
	public boolean handleMessage(BehMsgEntry msg) {
		if(super.handleMessage(msg)) {
			if ("EyeInBreak".equals(msg.getType())) {
				targetOn.set(false);
				initialSelection.set(false);
			}
			return true;
		}
		if ("TargetOn".equals(msg.getType())) {
			targetOn.set(true);
			initialSelection.set(false);
			
			MatchTargetMessage m = MatchTargetMessage.fromXml(msg.getMsg());
			targetColor.set(m.getTargetColor());
			targetPosition.set(m.getTargetPos());
			targetSize.set(m.getTargetSize());
			targetEyeWindowSize.set(m.getTargetEyeWindowSize());
			return true;
		} else if ("TargetSelectionCorrect".equals(msg.getType()) || "TargetSelectionWrong".equals(msg.getType())) {
			targetOn.set(false);
			initialSelection.set(false);
			return true;
		} else if ("TargetInitialSelection".equals(msg.getType())) {
			initialSelection.set(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean isTargetOn() {
		return targetOn.get();
	}

	public boolean isInitialSelection() {
		return initialSelection.get();
	}
	
	public RGBColor getTargetColor() {
		return targetColor.get();
	}

	public Coordinates2D getTargetPosition() {
		return targetPosition.get();
	}

	public double getTargetSize() {
		Double size = targetSize.get();
		return size == null ? 0.0 : size;
	}

	public double getTargetEyeWindowSize() {
		Double size = targetEyeWindowSize.get();
		return size == null ? 0.0 : size;
	}
}
