package org.xper.choice;

import org.lwjgl.opengl.GL11;
import org.xper.Dependency;
import org.xper.choice.util.ChoiceExperimentUtil;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.drawing.AbstractTaskScene;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.Drawable;
import org.xper.drawing.RGBColor;
import org.xper.drawing.renderer.AbstractRenderer;

public abstract class AbstractChoiceTaskScene extends AbstractTaskScene implements ChoiceTaskScene {

	@Dependency
	RGBColor[] targetColor;
	
	public void drawAllTargets(Context context) {
		blankScreen.draw(null);
		renderer.draw(new Drawable() {
			public void draw(Context context) {
				if (useStencil) {
					GL11.glStencilFunc(GL11.GL_EQUAL, 0, 1);
				}
				Coordinates2D[] pos = ((ChoiceTrialContext) context).getTargetPos();
				double[] size = ((ChoiceTrialContext) context).getTargetSize();
				for (int i = 0; i < pos.length; i++) {
					drawTarget(context, pos, size, i);
				}
				if (useStencil) {
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
				}
				marker.draw(context);
			}
		}, context);
	}
	
	public void drawSelectedTarget(Context context, final int sel) {
		blankScreen.draw(null);
		renderer.draw(new Drawable() {
			public void draw(Context context) {
				if (useStencil) {
					GL11.glStencilFunc(GL11.GL_EQUAL, 0, 1);
				}
				Coordinates2D[] pos = ((ChoiceTrialContext) context).getTargetPos();
				double[] size = ((ChoiceTrialContext) context).getTargetSize();
				drawTarget(context, pos, size, sel);
				if (useStencil) {
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
				}
				marker.draw(context);
			}
		}, context);
	}
	
	private void drawTarget(Context context, Coordinates2D[] pos, double[] size, int i) {
		AbstractRenderer renderer = context.getRenderer();
		ChoiceExperimentUtil.drawChoiceTarget(renderer, pos, size, targetColor, i);
	}
	
	public RGBColor[] getTargetColor() {
		return targetColor;
	}

	public void setTargetColor(RGBColor[] targetColor) {
		this.targetColor = targetColor;
	}
}
