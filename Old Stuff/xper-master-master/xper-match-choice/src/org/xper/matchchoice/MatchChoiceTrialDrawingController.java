package org.xper.matchchoice;

import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.classic.TrialDrawingController;

public interface MatchChoiceTrialDrawingController extends TrialDrawingController {
	public void targetOn(MatchChoiceTrialContext context);
	public void prepareTarget(MatchChoiceTrialContext context);
	public void targetSelected (MatchChoiceTrialContext context, int sel);
}
