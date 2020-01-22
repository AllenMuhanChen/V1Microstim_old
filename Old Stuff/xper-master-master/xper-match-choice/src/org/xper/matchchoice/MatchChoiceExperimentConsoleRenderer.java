package org.xper.matchchoice;

import org.xper.Dependency;
import org.xper.classic.TrialExperimentConsoleRenderer;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.DrawableFactory;
import org.xper.drawing.RGBColor;
import org.xper.matchchoice.util.MatchChoiceExperimentUtil;

public class MatchChoiceExperimentConsoleRenderer extends
		TrialExperimentConsoleRenderer {
	//@Dependency
	//RGBColor[] targetColor;
	
	@Dependency
	DrawableFactory[] drawableFactory;
	
//	public RGBColor[] getTargetColor() {
//		return targetColor;
//	}
//
//	public void setTargetColor(RGBColor[] targetColor) {
//		this.targetColor = targetColor;
//	}
	
	@Override
	public void drawCanvas(Context context, String devId) {
		super.drawCanvas(context, devId);
		if (messageHandler instanceof MatchChoiceExperimentMessageHandler) {
			MatchChoiceExperimentMessageHandler r = (MatchChoiceExperimentMessageHandler) messageHandler;
			if (r.isInChoice()) {
				int sel = r.getSelection();
				RGBColor[] targetColor = r.getTargetColor();
				Coordinates2D pos[] = r.getTargetPosition();
				double size[] = r.getTargetSize();
				double eyeWinSize[] = r.getTargetEyeWindowSize();
				if (pos != null && size != null && eyeWinSize != null) {
					MatchChoiceExperimentUtil.drawChoiceTarget(renderer, pos, size, targetColor, drawableFactory, sel);
					MatchChoiceExperimentUtil.drawChoiceTargetEyeWindow(renderer, pos, eyeWinSize, targetColor, sel);
					if (!r.isInitialSelection()) {
						for (int i = 0; i < pos.length; i ++) {
							if (i != sel) {
								MatchChoiceExperimentUtil.drawChoiceTarget(renderer, pos, size, targetColor, drawableFactory, i);
								MatchChoiceExperimentUtil.drawChoiceTargetEyeWindow(renderer, pos, eyeWinSize, targetColor, i);
							}
						}
					}
				}
			}
		}
	}

	public DrawableFactory[] getDrawableFactory() {
		return drawableFactory;
	}

	public void setDrawableFactory(DrawableFactory[] drawableFactory) {
		this.drawableFactory = drawableFactory;
	}
}
