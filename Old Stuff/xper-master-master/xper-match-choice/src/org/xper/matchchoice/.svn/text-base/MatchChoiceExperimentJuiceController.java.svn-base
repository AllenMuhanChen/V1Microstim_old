package org.xper.matchchoice;

import org.xper.Dependency;
import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.classic.TrialEventListener;
import org.xper.classic.vo.TrialContext;
import org.xper.juice.DynamicJuice;

public class MatchChoiceExperimentJuiceController implements TrialEventListener {
	@Dependency
	DynamicJuice juice;

	public void eyeInBreak(long timestamp, TrialContext context) {
	}

	public void eyeInHoldFail(long timestamp, TrialContext context) {
	}

	public void fixationPointOn(long timestamp, TrialContext context) {
	}

	public void fixationSucceed(long timestamp, TrialContext context) {
	}

	public void initialEyeInFail(long timestamp, TrialContext context) {
	}

	public void initialEyeInSucceed(long timestamp, TrialContext context) {
	}

	public void trialComplete(long timestamp, TrialContext context) {
		MatchChoiceTrialContext matchChoiceContext = (MatchChoiceTrialContext)context;
		int sel = matchChoiceContext.getTargetSelected();
		long reward = matchChoiceContext.getReward();
		if (reward == sel) {
			juice.deliver();
		}
	}
	
	public void trialInit(long timestamp, TrialContext context) {
	}

	public void trialStart(long timestamp, TrialContext context) {
	}

	public void trialStop(long timestamp, TrialContext context) {
	}

	public DynamicJuice getJuice() {
		return juice;
	}

	public void setJuice(DynamicJuice juice) {
		this.juice = juice;
	}

}
