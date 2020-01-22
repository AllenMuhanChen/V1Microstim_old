package org.xper.choice;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.classic.TrialEventLogger;

public class ChoiceTrialEventLogger extends TrialEventLogger implements
		ChoiceTrialEventListener {
	static Logger logger = Logger.getLogger(ChoiceTrialEventLogger.class);

	public void targetInitialSelection(long timestamp, int sel, ChoiceTrialContext context) {
		log("targetInitialSelection", timestamp, String.valueOf(sel));
	}

	public void targetOn(long timestamp, ChoiceTrialContext context) {
		log("targetOn", timestamp);
	}

	public void targetSelectionSuccess(long timestamp, int sel,
			ChoiceTrialContext context) {
		log("targetSelectionSuccess", timestamp, String.valueOf(sel));
	}
	
	protected void log(String event, long timestamp, String data) {
		logger.info(event + ": " + new Timestamp(timestamp/1000).toString() + " - " + data);
	}

}
