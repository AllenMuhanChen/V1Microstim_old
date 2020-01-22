package org.xper.choice;

import org.xper.Dependency;
import org.xper.choice.util.ChoiceExperimentUtil;
import org.xper.classic.TrialExperimentConsoleRenderer;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;

public class ChoiceExperimentConsoleRenderer extends
		TrialExperimentConsoleRenderer {
	@Dependency
	RGBColor[] targetColor;
	
	public RGBColor[] getTargetColor() {
		return targetColor;
	}

	public void setTargetColor(RGBColor[] targetColor) {
		this.targetColor = targetColor;
	}
	
	@Override
	public void drawCanvas(Context context, String devId) {
		super.drawCanvas(context, devId);
		if (messageHandler instanceof ChoiceExperimentMessageHandler) {
			ChoiceExperimentMessageHandler r = (ChoiceExperimentMessageHandler) messageHandler;
			if (r.isInChoice()) {
				int sel = r.getSelection();
				Coordinates2D pos[] = r.getTargetPosition();
				double size[] = r.getTargetSize();
				double eyeWinSize[] = r.getTargetEyeWindowSize();
				if (pos != null && size != null && eyeWinSize != null) {
					ChoiceExperimentUtil.drawChoiceTarget(renderer, pos, size, targetColor, sel);
					ChoiceExperimentUtil.drawChoiceTargetEyeWindow(renderer, pos, eyeWinSize, targetColor, sel);
					if (!r.isInitialSelection()) {
						for (int i = 0; i < pos.length; i ++) {
							if (i != sel) {
								ChoiceExperimentUtil.drawChoiceTarget(renderer, pos, size, targetColor, i);
								ChoiceExperimentUtil.drawChoiceTargetEyeWindow(renderer, pos, eyeWinSize, targetColor, i);
							}
						}
					}
				}
			}
		}
	}
}
