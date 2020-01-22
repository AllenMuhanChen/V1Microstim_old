package org.xper.match;

import org.xper.classic.MarkEveryStepTrialDrawingController;
import org.xper.classic.vo.TrialContext;
import org.xper.experiment.ExperimentTask;
import org.xper.match.util.MatchExperimentUtil;
import org.xper.match.vo.MatchTrialContext;

public class DefaultMatchTrialDrawingController extends
		MarkEveryStepTrialDrawingController {
	
	public void slideFinish(ExperimentTask task, TrialContext context) {
		taskScene.nextMarker();
		
		if (taskScene instanceof MatchTaskScene && MatchExperimentUtil.isTargetOn((MatchTrialContext)context)) {
			((MatchTaskScene)taskScene).drawTarget(context);
		} else {
			taskScene.drawBlank(context, true, true);
		}
		
		window.swapBuffers();
	}
	
	public void targetSelectionDone(ExperimentTask task, TrialContext context) {
		taskScene.nextMarker();
		taskScene.drawBlank(context, true, true);
		window.swapBuffers();
	}
}
