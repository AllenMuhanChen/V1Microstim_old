package org.xper.choice;

import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.classic.MarkEveryStepTrialDrawingController;
import org.xper.exception.ExperimentSetupException;

public class DefaultChoiceTrialExperimentDrawingController extends
		MarkEveryStepTrialDrawingController implements
		ChoiceTrialDrawingController {

	public void prepareTarget(ChoiceTrialContext context) {
		taskScene.nextMarker();
		if (taskScene instanceof ChoiceTaskScene) {
			((ChoiceTaskScene) taskScene).drawAllTargets(context);
		} else {
			throw new ExperimentSetupException("Task scene must implement ChoiceTaskScene interface for choice experiments.");
		}
	}

	public void targetOn(ChoiceTrialContext context) {
		window.swapBuffers();
	}

	public void targetSelected(ChoiceTrialContext context, int sel) {
		taskScene.nextMarker();
		if (taskScene instanceof ChoiceTaskScene) {
			((ChoiceTaskScene) taskScene).drawSelectedTarget(context, sel);
		} else {
			throw new ExperimentSetupException("Task scene must implement ChoiceTaskScene interface for choice experiments.");
		}
		window.swapBuffers();
	}

}
