package org.xper.choice;

import org.xper.choice.vo.ChoiceSelectionMessage;
import org.xper.choice.vo.ChoiceTargetMessage;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.classic.TrialExperimentMessageDispatcher;

public class ChoiceTrialExperimentMessageDispatcher extends
		TrialExperimentMessageDispatcher implements ChoiceTrialEventListener {

	public void targetOn(long timestamp, ChoiceTrialContext context) {
		enqueue(timestamp, "TargetOn", ChoiceTargetMessage
				.toXml(new ChoiceTargetMessage(timestamp, context
						.getTargetPos(), context.getTargetSize(), context.getTargetEyeWindowSize())));
	}

	public void targetInitialSelection(long timestamp, int sel,
			ChoiceTrialContext context) {
		enqueue(timestamp, "TargetInitialSelection", ChoiceSelectionMessage
				.toXml(new ChoiceSelectionMessage(sel)));
	}

	public void targetSelectionSuccess(long timestamp, int sel,
			ChoiceTrialContext context) {
		enqueue(timestamp, "TargetSelectionSuccess", ChoiceSelectionMessage
				.toXml(new ChoiceSelectionMessage(sel)));
	}

}
