package org.xper.match.util;

import java.util.List;

import org.xper.classic.TrialEventListener;
import org.xper.match.MatchTrialEventListener;
import org.xper.match.vo.MatchTrialContext;

public class MatchEventUtil {

	public static void fireTargetOnEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners,
			MatchTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchTrialEventListener) {
				((MatchTrialEventListener)listener).targetOn(timestamp, currentContext);
			}
		}
	}

	public static void fireTargetInitialSelectionEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, MatchTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchTrialEventListener) {
				((MatchTrialEventListener)listener).targetInitialSelection(timestamp, currentContext);
			}
		}
	}
	
	public static void fireTargetSelectionCorrectEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, MatchTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchTrialEventListener) {
				((MatchTrialEventListener)listener).targetSelectionCorrect(timestamp, currentContext);
			}
		}
	}
	
	public static void fireTargetSelectionWrongEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, MatchTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof MatchTrialEventListener) {
				((MatchTrialEventListener)listener).targetSelectionWrong(timestamp, currentContext);
			}
		}
	}
	
}
