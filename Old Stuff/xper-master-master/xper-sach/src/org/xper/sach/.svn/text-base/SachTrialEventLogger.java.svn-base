package org.xper.sach;


import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.xper.classic.TrialEventLogger;
import org.xper.sach.vo.SachTrialContext;

public class SachTrialEventLogger extends TrialEventLogger implements
		SachTrialEventListener {
	static Logger logger = Logger.getLogger(SachTrialEventLogger.class);

//	public void targetInitialSelection(long timestamp, SachTrialContext context) {
//		log("targetInitialSelection", timestamp);
//	}

	public void targetOn(long timestamp, SachTrialContext context) {
		log("targetOn", timestamp);
	}

	public void targetSelectionSuccess(long timestamp, SachTrialContext context) {
		log("targetSelectionSuccess", timestamp);
	}
	
	protected void log(String event, long timestamp, String data) {
		logger.info(event + ": " + new Timestamp(timestamp/1000).toString() + " - " + data);
	}

}
