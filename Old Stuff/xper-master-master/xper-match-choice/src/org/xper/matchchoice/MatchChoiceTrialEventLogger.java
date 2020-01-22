package org.xper.matchchoice;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.classic.TrialEventLogger;

public class MatchChoiceTrialEventLogger extends TrialEventLogger implements
		MatchChoiceTrialEventListener {
	static Logger logger = Logger.getLogger(MatchChoiceTrialEventLogger.class);

	public void targetInitialSelection(long timestamp, int sel, MatchChoiceTrialContext context) {
		log("targetInitialSelection", timestamp, String.valueOf(sel));
	}

	public void targetOn(long timestamp, MatchChoiceTrialContext context) {
		log("targetOn", timestamp);
	}

	public void targetSelectionSuccess(long timestamp, int sel,
			MatchChoiceTrialContext context) {
		log("targetSelectionSuccess", timestamp, String.valueOf(sel));
	}
	
	protected void log(String event, long timestamp, String data) {
		logger.info(event + ": " + new Timestamp(timestamp/1000).toString() + " - " + data);
	}

}
