package org.xper.matchchoice;

import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.classic.MarkEveryStepTrialDrawingController;
import org.xper.exception.ExperimentSetupException;

public class DefaultMatchChoiceTrialExperimentDrawingController extends
		MarkEveryStepTrialDrawingController implements
		MatchChoiceTrialDrawingController {

	public void prepareTarget(MatchChoiceTrialContext context) {
		taskScene.nextMarker();
		if (taskScene instanceof MatchChoiceTaskScene) {
			((MatchChoiceTaskScene) taskScene).drawAllTargets(context);
		} else {
			throw new ExperimentSetupException("Task scene must implement MatchChoiceTaskScene interface for match choice experiments.");
		}
	}

	public void targetOn(MatchChoiceTrialContext context) {
		window.swapBuffers();
	}

	public void targetSelected(MatchChoiceTrialContext context, int sel) {
		taskScene.nextMarker();
		if (taskScene instanceof MatchChoiceTaskScene) {
			((MatchChoiceTaskScene) taskScene).drawSelectedTarget(context, sel);
		} else {
			throw new ExperimentSetupException("Task scene must implement MatchChoiceTaskScene interface for match choice experiments.");
		}
		window.swapBuffers();
	}

}
