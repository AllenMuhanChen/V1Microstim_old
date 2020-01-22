package org.xper.sach;


import org.xper.classic.TrialExperimentMessageDispatcher;
import org.xper.sach.vo.SachTargetMessage;
import org.xper.sach.vo.SachTrialContext;

public class SachExperimentMessageDispatcher extends
		TrialExperimentMessageDispatcher implements SachTrialEventListener {

	public void targetOn(long timestamp, SachTrialContext context) {
		enqueue(timestamp, "TargetOn", SachTargetMessage
				.toXml(new SachTargetMessage(timestamp, context.getTargetPos(), context.getTargetEyeWindowSize())));
	}

//	public void targetInitialSelection(long timestamp, SachTrialContext context) {
//		enqueue(timestamp, "TargetInitialSelection", "");
//	}

	public void targetSelectionSuccess(long timestamp, SachTrialContext context) {
		enqueue(timestamp, "TargetSelectionSuccess", "");
	}

}
