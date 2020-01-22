package org.xper.matchchoice;

import org.xper.matchchoice.vo.MatchChoiceSelectionMessage;
import org.xper.matchchoice.vo.MatchChoiceTargetMessage;
import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.classic.TrialExperimentMessageDispatcher;

public class MatchChoiceTrialExperimentMessageDispatcher extends
		TrialExperimentMessageDispatcher implements MatchChoiceTrialEventListener {

	public void targetOn(long timestamp, MatchChoiceTrialContext context) {
		enqueue(timestamp, "TargetOn", MatchChoiceTargetMessage
				.toXml(new MatchChoiceTargetMessage(timestamp, context.getTargetColor(), context
						.getTargetPos(), context.getTargetSize(), context.getTargetEyeWindowSize())));
	}

	public void targetInitialSelection(long timestamp, int sel,
			MatchChoiceTrialContext context) {
		enqueue(timestamp, "TargetInitialSelection", MatchChoiceSelectionMessage
				.toXml(new MatchChoiceSelectionMessage(sel)));
	}

	public void targetSelectionSuccess(long timestamp, int sel,
			MatchChoiceTrialContext context) {
		enqueue(timestamp, "TargetSelectionSuccess", MatchChoiceSelectionMessage
				.toXml(new MatchChoiceSelectionMessage(sel)));
	}

}
