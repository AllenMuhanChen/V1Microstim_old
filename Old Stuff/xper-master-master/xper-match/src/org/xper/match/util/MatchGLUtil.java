package org.xper.match.util;

import org.lwjgl.opengl.GL11;
import org.xper.drawing.object.Circle;

public class MatchGLUtil {
	public static void drawTarget(Circle target, double x, double y, double s, float r, float g, float b) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glColor3f(r, g, b);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 0.0);
		target.setRadius(s);
		target.draw(null);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}
