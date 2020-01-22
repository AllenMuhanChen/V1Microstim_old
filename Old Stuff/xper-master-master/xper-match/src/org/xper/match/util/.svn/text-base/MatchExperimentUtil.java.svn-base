package org.xper.match.util;

import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.drawing.object.Circle;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.match.vo.MatchTrialContext;

public class MatchExperimentUtil {
	public static boolean isTargetOn (MatchTrialContext context) {
		if (context.getSlideIndex() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void drawTarget(AbstractRenderer renderer, Coordinates2D pos, double size, RGBColor targetColor) {
		Circle target = new Circle();
		target.setSolid(true);
		
		double x = renderer.deg2mm(pos.getX());
		double y = renderer.deg2mm(pos.getY());
		double s = renderer.deg2mm(size);
		
		MatchGLUtil.drawTarget(target, x, y, s, targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue());
	}
	
	public static void drawTargetEyeWindow(AbstractRenderer renderer, Coordinates2D pos, double size, RGBColor targetColor) {
		Circle eyeWin = new Circle();
		eyeWin.setSolid(false);
		
		double x = renderer.deg2mm(pos.getX());
		double y = renderer.deg2mm(pos.getY());
		double s = renderer.deg2mm(size);
		
		MatchGLUtil.drawTarget(eyeWin, x, y, s, targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue());
	}
}
