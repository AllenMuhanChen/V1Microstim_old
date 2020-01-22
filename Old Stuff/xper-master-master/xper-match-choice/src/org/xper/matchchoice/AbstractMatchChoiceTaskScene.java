package org.xper.matchchoice;

import org.lwjgl.opengl.GL11;
import org.xper.Dependency;
import org.xper.drawing.AbstractTaskScene;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.Drawable;
import org.xper.drawing.DrawableFactory;
import org.xper.drawing.RGBColor;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.matchchoice.util.MatchChoiceExperimentUtil;
import org.xper.matchchoice.vo.MatchChoiceTrialContext;

public abstract class AbstractMatchChoiceTaskScene extends AbstractTaskScene implements MatchChoiceTaskScene {

//	@Dependency
//	RGBColor[] targetColor;
	
	@Dependency
	DrawableFactory[] drawableFactory;
	
	public void drawAllTargets(Context context) {
		blankScreen.draw(null);
		renderer.draw(new Drawable() {
			public void draw(Context context) {
				if (useStencil) {
					GL11.glStencilFunc(GL11.GL_EQUAL, 0, 1);
				}
				RGBColor[] targetColor = ((MatchChoiceTrialContext) context).getTargetColor();
				Coordinates2D[] pos = ((MatchChoiceTrialContext) context).getTargetPos();
				double[] size = ((MatchChoiceTrialContext) context).getTargetSize();
				for (int i = 0; i < pos.length; i++) {
					drawTarget(context, targetColor, pos, size, i);
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
				RGBColor[] targetColor = ((MatchChoiceTrialContext) context).getTargetColor();
				Coordinates2D[] pos = ((MatchChoiceTrialContext) context).getTargetPos();
				double[] size = ((MatchChoiceTrialContext) context).getTargetSize();
				drawTarget(context, targetColor, pos, size, sel);
				if (useStencil) {
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
				}
				marker.draw(context);
			}
		}, context);
	}
	
	private void drawTarget(Context context, RGBColor[] targetColor, Coordinates2D[] pos, double[] size, int i) {
		AbstractRenderer renderer = context.getRenderer();
		MatchChoiceExperimentUtil.drawChoiceTarget(renderer, pos, size, targetColor, drawableFactory, i);
	}
	
//	public RGBColor[] getTargetColor() {
//		return targetColor;
//	}
//
//	public void setTargetColor(RGBColor[] targetColor) {
//		this.targetColor = targetColor;
//	}

	public DrawableFactory[] getDrawableFactory() {
		return drawableFactory;
	}

	public void setDrawableFactory(DrawableFactory[] drawableFactory) {
		this.drawableFactory = drawableFactory;
	}
}
