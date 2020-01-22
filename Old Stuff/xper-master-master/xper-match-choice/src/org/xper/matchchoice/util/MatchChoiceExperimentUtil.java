package org.xper.matchchoice.util;

import org.xper.drawing.Coordinates2D;
import org.xper.drawing.Drawable;
import org.xper.drawing.DrawableFactory;
import org.xper.drawing.MatchChoiceGLUtil;
import org.xper.drawing.RGBColor;
import org.xper.drawing.object.Circle;
import org.xper.drawing.renderer.AbstractRenderer;

public class MatchChoiceExperimentUtil {
	public static void drawChoiceTarget(AbstractRenderer renderer, Coordinates2D pos[], double size[], RGBColor[] targetColor, DrawableFactory[] drawableFactory, int i) {
		double x = renderer.deg2mm(pos[i].getX());
		double y = renderer.deg2mm(pos[i].getY());
		double s = renderer.deg2mm(size[i]);
		
		Drawable target = drawableFactory[i].getDrawable(true, s);
		
		MatchChoiceGLUtil.drawTarget(target, x, y, targetColor[i].getRed(), targetColor[i].getGreen(), targetColor[i].getBlue());
	}
	
	public static void drawChoiceTargetEyeWindow(AbstractRenderer renderer, Coordinates2D pos[], double size[], RGBColor[] targetColor, int i) {
		Circle eyeWin = new Circle();
		eyeWin.setSolid(false);
		
		double x = renderer.deg2mm(pos[i].getX());
		double y = renderer.deg2mm(pos[i].getY());
		double s = renderer.deg2mm(size[i]);
		eyeWin.setRadius(s);
		
		MatchChoiceGLUtil.drawTarget(eyeWin, x, y, targetColor[i].getRed(), targetColor[i].getGreen(), targetColor[i].getBlue());
	}
}
