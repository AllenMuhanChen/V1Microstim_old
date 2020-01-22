package org.xper.choice.util;

import java.util.List;

import org.xper.choice.ChoiceTrialEventListener;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.classic.TrialEventListener;

public class ChoiceEventUtil {

	public static void fireTargetOnEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners,
			ChoiceTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof ChoiceTrialEventListener) {
				((ChoiceTrialEventListener)listener).targetOn(timestamp, currentContext);
			}
		}
	}

	public static void fireTargetInitialSelectionEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, int sel,
			ChoiceTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof ChoiceTrialEventListener) {
				((ChoiceTrialEventListener)listener).targetInitialSelection(timestamp, sel, currentContext);
			}
		}
	}
	
	public static void fireTargetSelectionSuccessEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, int sel,
			ChoiceTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof ChoiceTrialEventListener) {
				((ChoiceTrialEventListener)listener).targetSelectionSuccess(timestamp, sel, currentContext);
			}
		}
	}
	
}
