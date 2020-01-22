package org.xper.match;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.xper.classic.TrialEventLogger;
import org.xper.match.vo.MatchTrialContext;

public class MatchTrialEventLogger extends TrialEventLogger implements
		MatchTrialEventListener {
	static Logger logger = Logger.getLogger(MatchTrialEventLogger.class);

	public void targetInitialSelection(long timestamp, MatchTrialContext context) {
		log("targetInitialSelection", timestamp);
	}

	public void targetOn(long timestamp, MatchTrialContext context) {
		log("targetOn", timestamp);
	}

	public void targetSelectionCorrect(long timestamp, MatchTrialContext context) {
		log("targetSelectionCorrect", timestamp);
	}
	
	public void targetSelectionWrong(long timestamp, MatchTrialContext context) {
		log("targetSelectionWrong", timestamp);
	}
	
	protected void log(String event, long timestamp, String data) {
		logger.info(event + ": " + new Timestamp(timestamp/1000).toString() + " - " + data);
	}

}
