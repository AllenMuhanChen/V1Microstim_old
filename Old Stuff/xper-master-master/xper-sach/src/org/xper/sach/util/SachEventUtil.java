package org.xper.sach.util;

import java.util.List;

import org.xper.classic.TrialEventListener;
import org.xper.sach.SachTrialEventListener;
import org.xper.sach.vo.SachTrialContext;

public class SachEventUtil {

	public static void fireTargetOnEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners,
			SachTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof SachTrialEventListener) {
				((SachTrialEventListener)listener).targetOn(timestamp, currentContext);
			}
		}
	}

	/*public static void fireTargetInitialSelectionEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, SachTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof SachTrialEventListener) {
				((SachTrialEventListener)listener).targetInitialSelection(timestamp, currentContext);
			}
		}
	}*/
	
	public static void fireTargetSelectionSuccessEvent(long timestamp,
			List<? extends TrialEventListener> trialEventListeners, SachTrialContext currentContext) {
		for (TrialEventListener listener : trialEventListeners) {
			if (listener instanceof SachTrialEventListener) {
				((SachTrialEventListener)listener).targetSelectionSuccess(timestamp, currentContext);
			}
		}
	}
	
}
