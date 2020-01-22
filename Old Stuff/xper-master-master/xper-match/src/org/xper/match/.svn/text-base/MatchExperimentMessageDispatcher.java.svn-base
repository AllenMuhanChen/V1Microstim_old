package org.xper.match;

import org.xper.classic.TrialExperimentMessageDispatcher;
import org.xper.match.vo.MatchTargetMessage;
import org.xper.match.vo.MatchTrialContext;

public class MatchExperimentMessageDispatcher extends
		TrialExperimentMessageDispatcher implements MatchTrialEventListener {

	public void targetOn(long timestamp, MatchTrialContext context) {
		enqueue(timestamp, "TargetOn", MatchTargetMessage
				.toXml(new MatchTargetMessage(timestamp, context.getTargetColor(), context
						.getTargetPos(), context.getTargetSize(), context.getTargetEyeWindowSize())));
	}

	public void targetInitialSelection(long timestamp, MatchTrialContext context) {
		enqueue(timestamp, "TargetInitialSelection", "");
	}

	public void targetSelectionCorrect(long timestamp, MatchTrialContext context) {
		enqueue(timestamp, "TargetSelectionCorrect", "");
	}
	
	public void targetSelectionWrong(long timestamp, MatchTrialContext context) {
		enqueue(timestamp, "TargetSelectionWrong", "");
	}

}
