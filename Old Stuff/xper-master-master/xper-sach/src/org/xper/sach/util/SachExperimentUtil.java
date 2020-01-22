package org.xper.sach.util;

import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.drawing.object.Circle;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.sach.vo.SachTrialContext;

public class SachExperimentUtil {
	public static boolean isTargetOn (SachTrialContext context) {
		if (context.getSlideIndex() == context.getCountObjects() - 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void drawTargetEyeWindow(AbstractRenderer renderer, Coordinates2D pos, double size, RGBColor targetColor) {
		Circle eyeWin = new Circle();
		eyeWin.setSolid(false);
		
		double x = renderer.deg2mm(pos.getX());
		double y = renderer.deg2mm(pos.getY());
		double s = renderer.deg2mm(size);
		
		SachGLUtil.drawCircle(eyeWin, x, y, s, targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue());
	}
}
