package org.xper.matchchoice.util;

import java.util.List;

import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.classic.TrialEventListener;
import org.xper.matchchoice.MatchChoiceTrialEventListener;

public class MatchChoiceEventUtil {

	public static void fireTargetOnEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners,
			MatchChoiceTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchChoiceTrialEventListener) {
				((MatchChoiceTrialEventListener)listener).targetOn(timestamp, currentContext);
			}
		}
	}

	public static void fireTargetInitialSelectionEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, int sel,
			MatchChoiceTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchChoiceTrialEventListener) {
				((MatchChoiceTrialEventListener)listener).targetInitialSelection(timestamp, sel, currentContext);
			}
		}
	}
	
	public static void fireTargetSelectionSuccessEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, int sel,
			MatchChoiceTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchChoiceTrialEventListener) {
				((MatchChoiceTrialEventListener)listener).targetSelectionSuccess(timestamp, sel, currentContext);
			}
		}
	}
	
}
