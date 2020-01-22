package org.xper.match;

import org.xper.classic.TrialExperimentConsoleRenderer;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.match.util.MatchExperimentUtil;

public class MatchExperimentConsoleRenderer extends
		TrialExperimentConsoleRenderer {
//	@Dependency
//	RGBColor targetColor;
	
//	public RGBColor getTargetColor() {
//		return targetColor;
//	}
//
//	public void setTargetColor(RGBColor targetColor) {
//		this.targetColor = targetColor;
//	}
	
	@Override
	public void drawCanvas(Context context, String devId) {
		super.drawCanvas(context, devId);
		if (messageHandler instanceof MatchExperimentMessageHandler) {
			MatchExperimentMessageHandler r = (MatchExperimentMessageHandler) messageHandler;
			if (r.isTargetOn()) {
				RGBColor targetColor = r.getTargetColor();
				Coordinates2D pos = r.getTargetPosition();
				double size = r.getTargetSize();
				double eyeWinSize = r.getTargetEyeWindowSize();
				if (pos != null) {
					MatchExperimentUtil.drawTarget(renderer, pos, size, targetColor);
					MatchExperimentUtil.drawTargetEyeWindow(renderer, pos, eyeWinSize, targetColor);
				}
			}
		}
	}
}
