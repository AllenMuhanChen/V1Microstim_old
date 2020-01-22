package org.xper.choice.util;

import org.xper.drawing.ChoiceGLUtil;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.drawing.object.Circle;
import org.xper.drawing.renderer.AbstractRenderer;

public class ChoiceExperimentUtil {
	public static void drawChoiceTarget(AbstractRenderer renderer, Coordinates2D pos[], double size[], RGBColor[] targetColor, int i) {
		Circle target = new Circle();
		target.setSolid(true);
		
		double x = renderer.deg2mm(pos[i].getX());
		double y = renderer.deg2mm(pos[i].getY());
		double s = renderer.deg2mm(size[i]);
		
		ChoiceGLUtil.drawTarget(target, x, y, s, targetColor[i].getRed(), targetColor[i].getGreen(), targetColor[i].getBlue());
	}
	
	public static void drawChoiceTargetEyeWindow(AbstractRenderer renderer, Coordinates2D pos[], double size[], RGBColor[] targetColor, int i) {
		Circle eyeWin = new Circle();
		eyeWin.setSolid(false);
		
		double x = renderer.deg2mm(pos[i].getX());
		double y = renderer.deg2mm(pos[i].getY());
		double s = renderer.deg2mm(size[i]);
		
		ChoiceGLUtil.drawTarget(eyeWin, x, y, s, targetColor[i].getRed(), targetColor[i].getGreen(), targetColor[i].getBlue());
	}
}
