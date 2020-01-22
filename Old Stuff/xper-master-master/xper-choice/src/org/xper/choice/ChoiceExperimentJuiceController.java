package org.xper.choice;

import org.xper.Dependency;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.classic.TrialEventListener;
import org.xper.classic.vo.TrialContext;
import org.xper.juice.DynamicJuice;

public class ChoiceExperimentJuiceController implements TrialEventListener {
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
		ChoiceTrialContext choiceContext = (ChoiceTrialContext)context;
		long reward = choiceContext.getReward()[choiceContext.getTargetSelected()];
		juice.setReward(reward);
		juice.deliver();
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
