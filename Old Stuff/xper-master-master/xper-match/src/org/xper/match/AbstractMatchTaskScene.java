package org.xper.match;

import org.lwjgl.opengl.GL11;
import org.xper.drawing.AbstractTaskScene;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.Drawable;
import org.xper.drawing.RGBColor;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.match.util.MatchExperimentUtil;
import org.xper.match.vo.MatchTrialContext;

public abstract class AbstractMatchTaskScene extends AbstractTaskScene implements MatchTaskScene {

//	@Dependency
//	RGBColor targetColor;
	
	public void drawTarget(Context context) {
		blankScreen.draw(null);
		renderer.draw(new Drawable() {
			public void draw(Context context) {
				if (useStencil) {
					// 0 will pass for stimulus region
					GL11.glStencilFunc(GL11.GL_EQUAL, 0, 1);
				}
				RGBColor targetColor = ((MatchTrialContext) context).getTargetColor();
				Coordinates2D pos = ((MatchTrialContext) context).getTargetPos();
				double size = ((MatchTrialContext) context).getTargetSize();
				AbstractRenderer renderer = context.getRenderer();
				MatchExperimentUtil.drawTarget(renderer, pos, size, targetColor);
				if (useStencil) {
					// 1 will pass for fixation and marker regions
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
				}
				fixation.draw(context);
				marker.draw(context);
			}
		}, context);
	}
	
//	public RGBColor getTargetColor() {
//		return targetColor;
//	}
//
//	public void setTargetColor(RGBColor targetColor) {
//		this.targetColor = targetColor;
//	}
}
